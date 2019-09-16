package pt.ipp.estg.projeto.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import pt.ipp.estg.projeto.R;

public class Login extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mEmail;
    private EditText mPassword;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Views
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        //Buttons
        findViewById(R.id.btnlogin).setOnClickListener(this);
        findViewById(R.id.btnSignin).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

        @Override
        protected void onStart() {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Sign In failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

        private void signIn(String email, String password) {
            Log.d(TAG, "signIn" + email);
            if (!validateForm()) {
                return;
            }

            showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(Login.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
                hideProgressDialog();
            }
        });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {

            Intent intent = new Intent(this, PerfilUtilizador.class);
            startActivity(intent);
            finish();
//            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.btnlogin).setVisibility(View.GONE);
//            findViewById(R.id.email).setVisibility(View.GONE);
//            findViewById(R.id.password).setVisibility(View.GONE);
            //findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);

            //findViewById(R.id.verifyEmailButton).setEnabled(!user.isEmailVerified());
        } else {

            findViewById(R.id.btnlogin).setVisibility(View.VISIBLE);
            findViewById(R.id.email).setVisibility(View.VISIBLE);
            findViewById(R.id.password).setVisibility(View.VISIBLE);
            //findViewById(R.id.signedInButtons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btnSignin) {
            createAccount(mEmail.getText().toString(), mPassword.getText().toString());
        } else
            if (i == R.id.btnlogin) {
            signIn(mEmail.getText().toString(), mPassword.getText().toString());
        } //else if (i == R.id.signOutButton) {
//            signOut();
//        } else if (i == R.id.verifyEmailButton) {
//            sendEmailVerification();
//        }
    }
}