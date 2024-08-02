package es.lamc.appnegytech.cUser;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import java.util.Calendar;

import es.lamc.appnegytech.dData.FormAdapter;
import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentInicioFormBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InicioForm extends Fragment {

    private FragmentInicioFormBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;
    private NavController navController;
    private RecyclerView recyclerViewForms;
    private FormAdapter formAdapter;

    public static final int NUM_COLLECTIONS = 100;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInicioFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        recyclerViewForms = binding.recyclerViewForms;
        recyclerViewForms.setLayoutManager(new LinearLayoutManager(context));

        formAdapter = new FormAdapter(new ArrayList<>());
        recyclerViewForms.setAdapter(formAdapter);

        binding.btnSalir.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setMessage("¿Está seguro que quiere salir?")
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        mAuth.signOut();
                        navController.navigate(R.id.navigation_loading_exit);
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                    })
                    .show();
        });

        binding.fabAddForm.setOnClickListener(v -> {
            navController.navigate(R.id.action_inicio_form_to_seleccionar_servicio);
        });

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String nombreUsuario = currentUser.getDisplayName();
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                binding.nombreUsuario.setText(nombreUsuario);
            }

            loadFormResponses(uid);
        }

        Calendar calendar = Calendar.getInstance();
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        String greeting;
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Buenos días";
        } else if (timeOfDay >= 12 && timeOfDay < 18) {
            greeting = "Buenas tardes";
        } else {
            greeting = "Buenas noches";
        }
        binding.textBienvenido.setText(greeting);
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
                            String servicio = document.getString("servicioContratado");
                            String estado = document.getString("estado");

                            if (servicio != null && !servicio.isEmpty() &&
                                    estado != null && !estado.isEmpty()) {
                                allResponses.add(document);
                            }
                        }
                        formAdapter.updateData(new ArrayList<>(allResponses));
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    });
        }
    }

}

