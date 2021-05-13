package pt.ipp.estg.projeto;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegistoEncomenda extends DialogFragment {

    private Context mContext;

    private EditText mMatricula;
    private TextView mMatriculaComValor;
    private String matricula;

    private EditText mDescricao;
    private String descricao;

    private TextView mCodigo;
    private long codigo;
    private String cod;

    private String data;

    private PassarDados mpassarDadosListener;

    private FirebaseAuth mAuth;

    FirebaseDatabase database;

    DatabaseReference currentRegistration; //Matricula veiculo
    DatabaseReference registrationTrips; //Viagens com esta matricula
    DatabaseReference orders; //As minhas encomendas
    DatabaseReference currentOrder; //a minha encomenda
    DatabaseReference currentDate; //data

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_registo_encomenda, container, false);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        registrationTrips = database.getReference().getRoot().child("Veiculos");

        mMatricula = contentView.findViewById(R.id.matricula);
        mMatriculaComValor = contentView.findViewById(R.id.matriculaComValor);

        if (matricula == null) {
            mMatriculaComValor.setVisibility(View.GONE);
        } else {
            mMatricula.setVisibility(View.GONE);
            mMatriculaComValor.setText(matricula);
        }

        mCodigo = contentView.findViewById(R.id.codigo_encomenda_textView);
        long time = System.currentTimeMillis();
        codigo = time;
        mCodigo.setText("" + codigo);
        mDescricao = contentView.findViewById(R.id.descricao);

        final Button iniciarViagem = contentView.findViewById(R.id.iniciarViagem);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (matricula == null) {
                    matricula = mMatricula.getText().toString();
                } else {
                    matricula = mMatriculaComValor.getText().toString();
                }

                descricao = mDescricao.getText().toString();

                if ((matricula.matches("[0-9]{2}+-[A-Z]{2}+-[0-9]{2}+") ||
                        matricula.matches("[0-9]{2}+-[0-9]{2}+-[A-Z]{2}+") ||
                        matricula.matches("[A-Z]{2}+-[0-9]{2}+-[0-9]{2}+")) && (validateCodigo()) && (validateDescricao())) {
                    mpassarDadosListener.onSetMatricula(matricula);
                    currentRegistration = registrationTrips.child(matricula);
                    data = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    currentDate = currentRegistration.child(data);
                    currentDate.child("Condutor").setValue(user.getEmail());
                    orders = currentDate.child("Encomendas");
                    orders.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean mExiste = false;
                            if ((dataSnapshot.exists()) == true) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    cod = data.child("Código da Encomenda").getValue().toString();
                                    if (cod.equals(codigo)) {
                                        mExiste = true;
                                        break;
                                    }
                                }
                            }
                            if (mExiste == true) {
                                Toast.makeText(getContext(), "Insira outro código", Toast.LENGTH_SHORT).show();
                            } else {
                                currentOrder = orders.push();
                                currentOrder.child("Código da Encomenda").setValue(codigo);
                                currentOrder.child("Descrição").setValue(descricao);
                                currentOrder.child("Entregue").setValue("Não");
                                Toast.makeText(getContext(), "Dados gravados com sucesso", Toast.LENGTH_SHORT).show();
                                dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Os dados estão incorretos", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        };

        iniciarViagem.setOnClickListener(listener);

        return contentView;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
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

    private boolean validateCodigo() {
        boolean valid = true;
        String codigo = mCodigo.getText().toString();
        if (TextUtils.isEmpty(codigo)) {
            mCodigo.setError("Required.");
            valid = false;
        } else {
            mCodigo.setError(null);
        }
        return valid;
    }

    private boolean validateDescricao() {
        boolean valid = true;
        String descricao = mDescricao.getText().toString();
        if (TextUtils.isEmpty(descricao)) {
            mDescricao.setError("Required.");
            valid = false;
        } else {
            mDescricao.setError(null);
        }
        return valid;
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        mAuth.signOut();
        AuthUI.getInstance().signOut(this);
    }*/
}
