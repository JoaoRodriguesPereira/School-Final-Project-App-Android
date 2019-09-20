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
    private String email;

    private EditText mMatricula;
    private String matricula;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_utilizador);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        email = user.getEmail();
        mEmail = findViewById(R.id.email_utilizador);
        mEmail.setText(email);


        mMatricula = findViewById(R.id.matricula);
        matricula = mMatricula.getText().toString();

        Button iniciarViagem = findViewById(R.id.iniciarViagem);

        iniciarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (matricula.matches("[0-9]{2}-[a-zA-z]{2}-[0-9]{2}")){
                    IniciarViagem(view);
                } else {

                    mMatricula.setError("Required.");
                }
            }
        });
    }

    public void IniciarViagem(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("matricula", mMatricula.getText().toString());
        startActivity(intent);
    }
}
