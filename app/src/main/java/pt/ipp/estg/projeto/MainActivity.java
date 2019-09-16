package pt.ipp.estg.projeto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.ipp.estg.projeto.login.Login;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_FINE_LOCATION = 100;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private FirebaseAuth mAuth;

    private TextView mEmail;

    private String email;

    private TextView mMatricula;

    private String matricula;

    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private TextView mPrecisaoTextView;
    private TextView mVelocidadeTextView;
    private TextView mAltitudeTextView;

    private TextView mLatitudeatualizadaTextView;
    private TextView mLongitudeatualizadaTextView;
    private TextView mPrecisaoatualizadaTextView;
    private TextView mHoraatualizadaTextView;
    private TextView mVelocidadeatualizadaTextView;
    private TextView mAltitudeatualizadaTextView;

    private TextView mMoradaAtualizadaTextView;

    private String dataInicio;
    private String dataFim;

    private List<Viagem> viagens;
    private List<Utilizador> utilizadores;

    FirebaseDatabase database;// = FirebaseDatabase.getInstance();

    DatabaseReference currentRegistration; //Matricula veiculo
    DatabaseReference registrationTrips; //Viagens com esta matricula

    DatabaseReference currentUser; //Utilizador
    DatabaseReference userTrips; //Viagens
    DatabaseReference currentTrip;// = database.getReference("Utilizadores").child("Viagens");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matricula = getIntent().getStringExtra("matricula");

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Toast.makeText(getApplicationContext(), "Bem-vindo de volta " + user.getEmail(), Toast.LENGTH_SHORT).show();
            database = FirebaseDatabase.getInstance();
            currentUser = database.getReference(user.getEmail().replace(".","-"));
        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }

        userTrips = currentUser.child("Viagens");

        email = user.getEmail();
        mEmail = findViewById(R.id.email_utilizador);
        mEmail.setText(email);

        mMatricula = findViewById(R.id.text_matricula);
        mMatricula.setText(matricula);

        mLatitudeTextView = findViewById(R.id.latitude);
        mLongitudeTextView = findViewById(R.id.longitude);
        mPrecisaoTextView = findViewById(R.id.precisao);
        mVelocidadeTextView = findViewById(R.id.velocidade);
        mAltitudeTextView = findViewById(R.id.altitude);

        mLatitudeatualizadaTextView = findViewById(R.id.latitudeatualizada);
        mLongitudeatualizadaTextView = findViewById(R.id.longitudeatualizada);
        mPrecisaoatualizadaTextView = findViewById(R.id.precisaoatualizada);
        mVelocidadeatualizadaTextView = findViewById(R.id.velocidadeatualizada);
        mAltitudeatualizadaTextView = findViewById(R.id.altitudeatualizada);
        mHoraatualizadaTextView = findViewById(R.id.horaatualizada);

        Button signOut = findViewById(R.id.signOutButton);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        mMoradaAtualizadaTextView = findViewById(R.id.moradaatualizada);

        final List<Local> lista_locais = new ArrayList<Local>();
        viagens = new ArrayList<>();
        utilizadores = new ArrayList<>();

        Button lastLocationButton = findViewById(R.id.obter_ultima_localizacao);
        lastLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLastLocation();
            }
        });

        Button atualizacoesperiodicasButton = findViewById(R.id.obter_atualizações_periódicas);
        atualizacoesperiodicasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLocationUpdates();

                currentTrip = userTrips.push();
                currentTrip.child("start").setValue(System.currentTimeMillis());
                currentTrip.child("locais");
                //myRef.child("Viagem").child("data_inicio").setValue(getDateTime());
                dataInicio = getDateTime();
            }
        });

        final Button pararatualizacoesperiodicasButton = findViewById(R.id.parar_atualiazacoes_periódicas);
        pararatualizacoesperiodicasButton.setEnabled(false);
        pararatualizacoesperiodicasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();

                currentTrip.child("end").setValue(System.currentTimeMillis());
                currentTrip = null;

                //myRef.child("Viagem").child("data_fim").setValue(getDateTime());
                dataFim = getDateTime();
                pararatualizacoesperiodicasButton.setEnabled(false);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);

        mLocationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location == null) {
                        Toast.makeText(MainActivity.this, "Não possui localização", Toast.LENGTH_LONG).show();
                    } else {
                        mLatitudeatualizadaTextView.setText(String.format("Latitude: %s", location.getLatitude()));
                        mLongitudeatualizadaTextView.setText(String.format("Longitude: %s", location.getLongitude()));
                        mPrecisaoatualizadaTextView.setText(String.format("Precisão: %s", location.getAccuracy()));
                        mVelocidadeatualizadaTextView.setText(String.format("Velocidade: %s", location.getSpeed()));
                        mAltitudeatualizadaTextView.setText(String.format("Altitude: %s", location.getAltitude()));
                        mHoraatualizadaTextView.setText(String.format("Data/Hora: %s", getDateTime()));

                        String address = getAddress(location.getLatitude(), location.getLongitude());
                        mMoradaAtualizadaTextView.setText(String.format("%s", address));
                        Local local = gravarLocal(location.getLatitude(), location.getLongitude(), location.getAccuracy(), location.getSpeed(), location.getAltitude(), address);

                        currentTrip.child("locais").push().setValue(local);
                        currentUser.child("lastLocation").setValue(local);

                        lista_locais.add(local);
                        pararatualizacoesperiodicasButton.setEnabled(true);

                        //Viagem viagem = gravarViagem(dataInicio, dataFim, lista_locais);
                        //viagens.add(viagem);

                        //Utilizador utilizador = gravarUtilizador(email, viagens);
                        //utilizadores.add(utilizador);

                        //currentUser.setValue(viagens);

                    }
                }
            }
        };
    }

    private Utilizador gravarUtilizador(String email, List<Viagem> viagens) {
        Utilizador utilizador = new Utilizador(email, viagens);
        return utilizador;
    }

    private Viagem gravarViagem(String data_inicio, String data_fim, List<Local> locais) {
        Viagem viagem = new Viagem(data_inicio, data_fim, locais);
        return viagem;
    }

    private Local gravarLocal(double lat, double lng, double precisao, double vel, double alt, String morada) {
        Local local = new Local(lat, lng, precisao, vel, alt, morada);
        return local;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getAddress(double mLatitudeatualizadaTextView, double mLongitudeatualizadaTextView) {
        String morada = "";
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(mLatitudeatualizadaTextView, mLongitudeatualizadaTextView, 1);
            morada = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return morada;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void getLastLocation() {
        //Verificar permissões do utilizador
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            public void onSuccess(Location location) {
                if (location != null) {
                    mLatitudeTextView.setText(String.format("Latitude: %s", location.getLatitude()));
                    mLongitudeTextView.setText(String.format("Longitude: %s", location.getLongitude()));
                    mPrecisaoTextView.setText(String.format("Precisão: %s", location.getAccuracy()));
                    mVelocidadeTextView.setText(String.format("Velocidade: %s", location.getSpeed()));
                    mAltitudeTextView.setText(String.format("Altitude: %s", location.getAltitude()));
                } else {
                    Toast.makeText(MainActivity.this, "Não possui ultima localização", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Nao foi possivel obter localização", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this);
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
