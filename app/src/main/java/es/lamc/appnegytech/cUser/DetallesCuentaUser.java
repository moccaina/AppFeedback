package es.lamc.appnegytech.cUser;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.dData.FormFinanzasAdapter;
import es.lamc.appnegytech.databinding.FragmentDetallesCuentaUserBinding;

public class DetallesCuentaUser extends Fragment {

    FragmentDetallesCuentaUserBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    View view;
    Context context;
    NavController navController;
    private RecyclerView recyclerViewFinanzas;
    private FormFinanzasAdapter formFinanzasAdapter;

    public static final int NUM_COLLECTIONS = 100;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetallesCuentaUserBinding.inflate(inflater, container, false );
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerViewFinanzas = binding.recyclerViewFinanzas;
        recyclerViewFinanzas.setLayoutManager(new LinearLayoutManager(context));

        formFinanzasAdapter = new FormFinanzasAdapter(new ArrayList<>());
        recyclerViewFinanzas.setAdapter(formFinanzasAdapter);

        binding.btnAtras.setOnClickListener(v -> {
            navController.navigate(R.id.action_detalles_cuenta_to_finanzas_user);
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            loadFormResponses(uid);
        }
    }

    private void loadFormResponses(String uid) {
        List<DocumentSnapshot> allResponses = new ArrayList<>();
        for (int i = 1; i <= NUM_COLLECTIONS; i++) {
            String collectionName = "formulario" + i;
            CollectionReference collectionRef = db.collection("lista_de_usuarios").document(uid)
                    .collection(collectionName);
            collectionRef.get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String servicioContratado = document.getString("servicioContratado");
                            String estadoServicio = document.getString("estadoServicio");
                            Date fechaVencimiento = document.getDate("fechaVencimiento");
                            Double montoServicio = document.getDouble("montoServicio");

                            if (servicioContratado != null && !servicioContratado.isEmpty() &&
                                    estadoServicio != null && !estadoServicio.isEmpty() &&
                                    fechaVencimiento != null && montoServicio != null) {
                                allResponses.add(document);
                            }
                        }
                        formFinanzasAdapter.updateData(new ArrayList<>(allResponses));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
