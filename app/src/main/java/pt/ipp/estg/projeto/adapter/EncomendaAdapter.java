package pt.ipp.estg.projeto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pt.ipp.estg.projeto.Encomenda;
import pt.ipp.estg.projeto.R;

public class EncomendaAdapter extends RecyclerView.Adapter<EncomendaAdapter.EncomendaViewHolder> {

    private Context mContext;
    private List<Encomenda> mListaEncomendas;

    private String matricula;

    private String morada;

    private Boolean emViagem;

    private double latitude;
    private double longitude;

    private FirebaseAuth mAuth;

    private String key;

    FirebaseDatabase database;

    DatabaseReference currentRegistration; //Matricula veiculo
    DatabaseReference registrationTrips; //Viagens com esta matricula
    DatabaseReference orders; //As minhas encomendas
    DatabaseReference currentDate; //Data

    public EncomendaAdapter() {
    }

    public EncomendaAdapter(Context mContext, List<Encomenda> mListaEncomendas) {
        this.mContext = mContext;
        this.mListaEncomendas = mListaEncomendas;
    }

    public EncomendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View encomendaView = inflater.inflate(R.layout.item_encomenda, parent, false);

        return new EncomendaViewHolder(encomendaView);
    }

    public void onBindViewHolder(final EncomendaViewHolder viewHolder, final int position) {
        final Encomenda encomenda = mListaEncomendas.get(position);
        TextView codigoTextView = viewHolder.codigoTextView;
        codigoTextView.setText(encomenda.getmCodigo());

        TextView descricaoTextView = viewHolder.descricaoTextView;
        descricaoTextView.setText(encomenda.getmDescricao());

        final Button button = viewHolder.entregarButton;
        button.setText("Entregar");

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        registrationTrips = database.getReference().getRoot().child("Veiculos");

        final String codigo;
        codigo = encomenda.getmCodigo();
        currentRegistration = registrationTrips.child(matricula);
        currentDate = currentRegistration.child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        currentDate.child("Condutor").setValue(user.getEmail());
        orders = currentDate.child("Encomendas");
        orders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cod;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    cod = data.child("Código da Encomenda").getValue().toString();
                    if (cod.equals(codigo)) {
                        key = data.getKey();
                        if ((data.child("Entregue").getValue().toString()).equals("Não")) {
                            button.setEnabled(true);
                        } else {
                            button.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.entregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String codigo1 = encomenda.getmCodigo();
                currentRegistration = registrationTrips.child(matricula);
                currentDate = currentRegistration.child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
                currentDate.child("Condutor").setValue(user.getEmail());
                orders = currentDate.child("Encomendas");
                orders.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String cod;
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            cod = data.child("Código da Encomenda").getValue().toString();
                            if (cod.equals(codigo1)) {
                                key = data.getKey();
                                if (emViagem == true) {
                                    orders.child(key).child("Entregue").setValue("Sim");
                                    orders.child(key).child("Local de Entrega").setValue(morada);
                                    orders.child(key).child("Latitude").setValue(latitude);
                                    orders.child(key).child("Longitude").setValue(longitude);
                                    mListaEncomendas.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                } else {
                                    Toast.makeText(mContext, "Tem que estar em viagem", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListaEncomendas.size();
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setEmViagem(Boolean emViagem) {
        this.emViagem = emViagem;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public class EncomendaViewHolder extends RecyclerView.ViewHolder {

        public TextView codigoTextView;
        public TextView descricaoTextView;
        public Button entregarButton;

        public EncomendaViewHolder(View itemView) {
            super(itemView);

            codigoTextView = itemView.findViewById(R.id.codigo_encomenda);
            descricaoTextView = itemView.findViewById(R.id.descricao_encomenda);
            entregarButton = itemView.findViewById(R.id.button_entregar);
        }
    }
}
