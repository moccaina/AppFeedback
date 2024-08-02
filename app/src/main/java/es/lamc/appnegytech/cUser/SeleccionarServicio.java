package es.lamc.appnegytech.cUser;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.dData.ViewModelFormData1;
import es.lamc.appnegytech.databinding.FragmentSeleccionarServicioBinding;

public class SeleccionarServicio extends Fragment {

    private FragmentSeleccionarServicioBinding binding;
    private Context context;
    private NavController navController;
    private FirebaseAuth mAuth;
    private Spinner servicioContratado;
    private Button btnIniciarForm;
    private ViewModelFormData1 viewModel;
    private FirebaseFirestore db;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSeleccionarServicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelFormData1.class);

        servicioContratado = binding.servicioContratado;
        btnIniciarForm = binding.btnIniciarForm;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.servicios_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicioContratado.setAdapter(adapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("lista_de_usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String servicio = documentSnapshot.getString("servicioContratado");

                            if (servicio != null) {
                                int spinnerPosition = adapter.getPosition(servicio);
                                servicioContratado.setSelection(spinnerPosition);
                                viewModel.setServicioContratado(servicio);
                            }
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error al recuperar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }

        btnIniciarForm.setOnClickListener(view1 -> {
            if (btnIniciarForm.isEnabled()) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // Referencia a la colección de configuración
                db.collection("lista_de_usuarios").document(uid)
                        .collection("configuracion").document("numeroFormulario")
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            int maxNum = 1;
                            if (documentSnapshot.exists()) {
                                maxNum = documentSnapshot.getLong("maxNum").intValue();
                            }

                            String newCollectionName = "formulario" + (maxNum + 1);

                            // Crear el mapa de datos del formulario
                            Map<String, Object> datosFormulario = new HashMap<>();
                            datosFormulario.put("servicioContratado", servicioContratado.getSelectedItem().toString());
                            datosFormulario.put("estado", "No Iniciado"); // Establecer el estado inicial

                            // Guardar el formulario en la nueva colección
                            int finalMaxNum = maxNum;
                            db.collection("lista_de_usuarios").document(uid)
                                    .collection(newCollectionName).document("datos")
                                    .set(datosFormulario, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        // Actualizar el número máximo del formulario
                                        Map<String, Object> updateData = new HashMap<>();
                                        updateData.put("maxNum", finalMaxNum + 1);
                                        db.collection("lista_de_usuarios").document(uid)
                                                .collection("configuracion").document("numeroFormulario")
                                                .set(updateData, SetOptions.merge())
                                                .addOnSuccessListener(aVoid1 -> {
                                                    viewModel.setNombreColeccionFormulario(newCollectionName); // Establecer el nombre de la colección en ViewModel
                                                    navController.navigate(R.id.navigation_new_form);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(context, "Error al actualizar número de formulario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error al obtener el número máximo del formulario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });


        validarCampos();
    }

    private void validarCampos() {
        String servicio = servicioContratado.getSelectedItem() != null ? servicioContratado.getSelectedItem().toString().trim() : "";

        btnIniciarForm.setEnabled(!servicio.isEmpty());
    }
}
