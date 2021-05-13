package pt.ipp.estg.projeto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import pt.ipp.estg.projeto.adapter.EncomendaAdapter;

public class EntregarEncomenda extends Fragment {

    private ArrayList<Encomenda> encomendas = new ArrayList<>();
    private RecyclerView rvEncomendas;
    private EncomendaAdapter adapter;

    private String matricula;

    private String morada;

    private Boolean emViagem;

    private double latitude;
    private double longitude;

    private FirebaseAuth mAuth;

    FirebaseDatabase database;

    DatabaseReference currentRegistration; //Matricula veiculo
    DatabaseReference registrationTrips; //Viagens com esta matricula
    DatabaseReference orders; //As minhas encomendas
    DatabaseReference currentDate; //Data

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View contentView = inflater.inflate(R.layout.fragment_entregar_encomenda, container, false);

        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();

        registrationTrips = database.getReference().getRoot().child("Veiculos");

        rvEncomendas = contentView.findViewById(R.id.mRecyclerView);

        rvEncomendas.setLayoutManager(new LinearLayoutManager(getContext()));

        currentRegistration = registrationTrips.child(matricula);
        currentDate = currentRegistration.child(new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date()));
        currentDate.child("Condutor").setValue(user.getEmail());
        orders = currentDate.child("Encomendas");
        orders.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) == true) {
                    encomendas.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if ((data.child("Entregue").getValue().toString()).equals("Não")) {
                            Encomenda encomenda = new Encomenda();
                            encomenda.setmCodigo(data.child("Código da Encomenda").getValue().toString());
                            encomenda.setmDescricao(data.child("Descrição").getValue().toString());
                            encomendas.add(encomenda);
                        }
                    }
                    adapter = new EncomendaAdapter(getContext(), encomendas);
                    adapter.setMatricula(matricula);
                    adapter.setEmViagem(emViagem);
                    adapter.setMorada(morada);
                    adapter.setLatitude(latitude);
                    adapter.setLongitude(longitude);
                    adapter.notifyDataSetChanged();
                    LinearLayoutManager llm = new LinearLayoutManager(getContext());
                    rvEncomendas.setLayoutManager(llm);
                    rvEncomendas.setAdapter(adapter);

                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                    rvEncomendas.addItemDecoration(itemDecoration);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return contentView;

    }

    public void setEmViagem(Boolean emViagem) {
        this.emViagem = emViagem;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem addEncomendaItem = menu.findItem(R.id.addEncomenda);
        MenuItem iniciarViagemItem = menu.findItem(R.id.iniciarViagem);
        addEncomendaItem.setVisible(false);
        iniciarViagemItem.setVisible(false);
    }
}
