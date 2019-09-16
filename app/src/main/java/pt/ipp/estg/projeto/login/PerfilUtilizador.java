package pt.ipp.estg.projeto.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pt.ipp.estg.projeto.MainActivity;
import pt.ipp.estg.projeto.R;

public class PerfilUtilizador extends AppCompatActivity {

    private TextView mEmail;

    private EditText mMatrícula;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_utilizador);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.email_utilizador);
        mMatrícula = findViewById(R.id.matricula);


        Button iniciarViagem = findViewById(R.id.iniciarViagem);
        iniciarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IniciarViagem(view);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mEmail.setText(getString(R.string.emailpassword_status_fmt, currentUser.getEmail(), currentUser.isEmailVerified()));
    }

    public void IniciarViagem(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("matricula", mMatrícula.getText().toString());
        startActivity(intent);
    }
}
