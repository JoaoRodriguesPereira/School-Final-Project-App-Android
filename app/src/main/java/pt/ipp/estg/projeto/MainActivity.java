package pt.ipp.estg.projeto;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pt.ipp.estg.projeto.login.Login;

public class MainActivity extends AppCompatActivity implements PassarDados {

    private RegistoEncomenda fragmentRegisto;
    private FirebaseAuth mAuth;
    private String mMatricula;
    private String mMorada;
    private Boolean mEmViagem;
    private double mLatitude;
    private double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Toast.makeText(getApplicationContext(), "Bem-vindo de volta " + user.getEmail(), Toast.LENGTH_SHORT).show();

            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            SharedPreferences.Editor editor = getSharedPreferences("pref", Context.MODE_PRIVATE).edit();
            editor.putString("pref", mMatricula).apply();

            if (findViewById(R.id.fragment_container) != null) {
                if (savedInstanceState != null)
                    return;

                PaginaInicial fragmentPaginaInicial = new PaginaInicial();

                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragmentPaginaInicial).commit();
            }

        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addEncomenda:
                // User chose the "Settings" item, show the app settings UI...
                irparaRegistoEncomenda();
                return true;

            case R.id.iniciarViagem:
                if (mMatricula != null) {
                    irparaIniciarViagem();
                } else {
                    Toast.makeText(getApplicationContext(), "Tem que adicionar uma encomenda!", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.entregarEncomenda:
                irparaEntregarEncomenda();
                return true;

            case R.id.logout:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                signOut();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void signOut() {
        mAuth.signOut();
        AuthUI.getInstance().signOut(this);
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        mMatricula = pref.getString("pref", mMatricula);

        if (currentUser == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    private void irparaRegistoEncomenda() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.addToBackStack(null);

        this.fragmentRegisto = new RegistoEncomenda();
        fragmentRegisto.setMatricula(mMatricula);
        fragmentRegisto.show(ft, "dialog");
    }

    private void irparaEntregarEncomenda() {
        EntregarEncomenda entregarEncomenda = new EntregarEncomenda();
        entregarEncomenda.setMatricula(mMatricula);
        entregarEncomenda.setEmViagem(mEmViagem);
        entregarEncomenda.setMorada(mMorada);
        entregarEncomenda.setLatitude(mLatitude);
        entregarEncomenda.setLongitude(mLongitude);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.fragment_container, entregarEncomenda);
        ft.commit();
    }

    private void irparaIniciarViagem() {
        IniciarViagem iniciar = new IniciarViagem();
        iniciar.setMatricula(mMatricula);
        iniciar.setEmViagem(mEmViagem);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.fragment_container, iniciar);
        ft.commit();
    }

    @Override
    public void onSetViagem(Boolean emViagem) {
        mEmViagem = emViagem;
    }

    @Override
    public void onSetMorada(String morada) {
        mMorada = morada;
    }

    @Override
    public void onSetMatricula(String matricula) {
        mMatricula = matricula;
    }

    @Override
    public void onSetLatLng(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }
}
