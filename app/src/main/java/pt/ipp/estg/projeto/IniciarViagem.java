package pt.ipp.estg.projeto;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class IniciarViagem extends Fragment implements OnMapReadyCallback {

    private Context mContext;

    private static final int REQUEST_FINE_LOCATION = 100;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private SupportMapFragment mMapFragment;
    private GoogleMap mGoogleMap;

    private FirebaseAuth mAuth;

    private TextView mEmail;

    private String email;

    private TextView mMatricula;
    private TextView mVelocidade;
    private TextView mNomeVelocidade;
    private TextView mkmh;
    private TextView mNomeLocalizacao;
    private FrameLayout mMap;

    private Button mIniciarViagem;
    private Button mPararViagem;

    private String velocidade;
    private String matricula;
    private Boolean emViagem;
    private String morada;

    private Context contexto;

    private TextView mMoradaAtualizadaTextView;

    private PassarDados mpassarDadosListener;

    private List<Viagem> viagens;
    private List<Utilizador> utilizadores;

    FirebaseDatabase database;// = FirebaseDatabase.getInstance();

    DatabaseReference currentRegistration; //Matricula veiculo
    DatabaseReference registrationTrips; //Viagens com esta matricula

    DatabaseReference Utilizadores;
    DatabaseReference currentUser; //Utilizador
    DatabaseReference userTrips; //Viagens
    DatabaseReference currentTrip;// = database.getReference("Utilizadores").child("Viagens");

    DatabaseReference currentDate; //Data

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_iniciar_viagem, container, false);

        setHasOptionsMenu(true);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        currentUser = database.getReference(user.getEmail().replace(".", "-"));

        Utilizadores = database.getReference().getRoot().child("Utilizadores");
        userTrips = Utilizadores.child(user.getEmail().replace(".", "-")).child("Viagens");

        registrationTrips = database.getReference().getRoot().child("Veiculos");

        emViagem = false;

        email = user.getEmail();
        /*mEmail = contentView.findViewById(R.id.email_utilizador);
        mEmail.setText(email);*/

        mMatricula = contentView.findViewById(R.id.text_matricula);
        mMatricula.setText(matricula);

        mVelocidade = contentView.findViewById(R.id.velocidadeatualizada);

        mNomeVelocidade = contentView.findViewById(R.id.nomeVelocidade);

        mkmh = contentView.findViewById(R.id.kmh);

        mNomeLocalizacao = contentView.findViewById(R.id.nomelocalizacao);

        mMap = contentView.findViewById(R.id.map);

        mNomeVelocidade.setVisibility(View.GONE);
        mVelocidade.setVisibility(View.GONE);
        mkmh.setVisibility(View.GONE);
        mNomeLocalizacao.setVisibility(View.GONE);
        mMap.setVisibility(View.GONE);

        mMoradaAtualizadaTextView = contentView.findViewById(R.id.moradaatualizada);
        mMoradaAtualizadaTextView.setVisibility(View.GONE);

        mIniciarViagem = contentView.findViewById(R.id.obter_atualizações_periódicas);
        mPararViagem = contentView.findViewById(R.id.parar_atualiazacoes_periódicas);

        final List<Local> lista_locais = new ArrayList<Local>();
        viagens = new ArrayList<>();
        utilizadores = new ArrayList<>();

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        emViagem = pref.getBoolean("pref", emViagem);

        mpassarDadosListener.onSetViagem(emViagem);

        if (emViagem == false) {
            mPararViagem.setVisibility(View.GONE);
        } else {
            mPararViagem.setVisibility(View.VISIBLE);
            mIniciarViagem.setVisibility(View.GONE);
        }

        final Button atualizacoesperiodicasButton = contentView.findViewById(R.id.obter_atualizações_periódicas);
        atualizacoesperiodicasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLocationUpdates();
                currentTrip = userTrips.push(); //cria outra viagem
                currentTrip.child("start").setValue(System.currentTimeMillis());
                currentTrip.child("locais");
                //myRef.child("Viagem").child("data_inicio").setValue(getDateTime());
                mIniciarViagem.setVisibility(View.GONE);
                mPararViagem.setVisibility(View.VISIBLE);

                emViagem = true;
                SharedPreferences.Editor editor = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE).edit();
                editor.putBoolean("pref", emViagem).apply();

                mpassarDadosListener.onSetViagem(emViagem);
            }
        });

        final Button pararatualizacoesperiodicasButton = contentView.findViewById(R.id.parar_atualiazacoes_periódicas);
        pararatualizacoesperiodicasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopLocationUpdates();

                currentTrip.child("end").setValue(System.currentTimeMillis());
                currentTrip = null;

                //myRef.child("Viagem").child("data_fim").setValue(getDateTime());
                pararatualizacoesperiodicasButton.setVisibility(View.GONE);
                emViagem = false;
                mpassarDadosListener.onSetViagem(emViagem);

                SharedPreferences.Editor editor = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE).edit();
                editor.putBoolean("pref", emViagem).apply();

                Toast.makeText(getContext(), "Acabou em: " + morada, Toast.LENGTH_LONG).show();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);

        mLocationCallback = new LocationCallback() {
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location == null) {
                        Toast.makeText(getContext(), "Não possui localização", Toast.LENGTH_LONG).show();
                    } else {
                        String address = getAddress(location.getLatitude(), location.getLongitude());
                        mMoradaAtualizadaTextView.setText(String.format("%s", address));
                        Local local = gravarLocal(location.getLatitude(), location.getLongitude(), location.getAccuracy(), location.getSpeed(), location.getAltitude(), address, System.currentTimeMillis());
                        mpassarDadosListener.onSetLatLng(location.getLatitude(), location.getLongitude());
                        DecimalFormat df = new DecimalFormat("0");
                        velocidade = df.format(location.getSpeed() * 3.6);
                        mVelocidade.setText(velocidade);
                        mVelocidade.setVisibility(View.VISIBLE);
                        mNomeVelocidade.setVisibility(View.VISIBLE);
                        mkmh.setVisibility(View.VISIBLE);
                        mNomeLocalizacao.setVisibility(View.VISIBLE);
                        mMoradaAtualizadaTextView.setVisibility(View.VISIBLE);
                        mMap.setVisibility(View.VISIBLE);
                        if (emViagem == true) {
                            currentTrip.child("locais").push().setValue(local);
                            currentRegistration = registrationTrips.child(matricula);
                            currentDate = currentRegistration.child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                            currentRegistration.child("Última Localização").setValue(local);
                            currentRegistration.child("Última Localização").child("Condutor").setValue(user.getEmail());
                        } else {
                            mIniciarViagem.setVisibility(View.GONE);
                        }
                        //currentUser.child("lastLocation").setValue(local);
                        morada = address;
                        mpassarDadosListener.onSetMorada(address);

                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
                        mGoogleMap.animateCamera(cameraUpdate);
                        mGoogleMap.clear();

                        lista_locais.add(local);
                        pararatualizacoesperiodicasButton.setEnabled(true);

                        mpassarDadosListener.onSetViagem(emViagem);
                    }
                }
            }
        };

        this.contexto = this.getContext();

        return contentView;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setEmViagem(Boolean emViagem) {
        this.emViagem = emViagem;
    }

    private Local gravarLocal(double lat, double lng, double precisao, double vel, double alt, String morada, long data) {
        Local local = new Local(lat, lng, precisao, vel, alt, morada, data);
        return local;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mpassarDadosListener = (PassarDados) context;
        } catch (Exception e) {
            Log.e("onAttach", e.toString());
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem addEncomendaItem = menu.findItem(R.id.addEncomenda);
        MenuItem iniciarViagemItem = menu.findItem(R.id.iniciarViagem);
        MenuItem entregarEncomenda = menu.findItem(R.id.entregarEncomenda);
        addEncomendaItem.setVisible(false);
        iniciarViagemItem.setVisible(false);
        entregarEncomenda.setVisible(true);
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private String getAddress(double mLatitudeatualizadaTextView, double mLongitudeatualizadaTextView) {
        String morada = "";
        Geocoder geocoder = new Geocoder(contexto, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(mLatitudeatualizadaTextView, mLongitudeatualizadaTextView, 1);
            morada = addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return morada;
    }

    private void startLocationUpdates() {
        if (checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.setMyLocationEnabled(true);
    }
}
