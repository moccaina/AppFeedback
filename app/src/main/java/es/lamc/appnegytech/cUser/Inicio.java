package es.lamc.appnegytech.cUser;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.dData.ViewModelFormData1;
import es.lamc.appnegytech.databinding.FragmentInicioBinding;

public class Inicio extends Fragment {

    private FragmentInicioBinding binding;
    private View view;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInicioBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelFormData1.class);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String nombreUsuario = currentUser.getDisplayName();
            if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
                binding.nombreUsuario.setText(nombreUsuario);
            }
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

        servicioContratado = binding.servicioContratado;
        btnIniciarForm = binding.btnIniciarForm;

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.servicios_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        servicioContratado.setAdapter(adapter);

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

        String selectedServicio = viewModel.getServicioContratado().getValue();
        if (selectedServicio != null) {
            int spinnerPosition = adapter.getPosition(selectedServicio);
            servicioContratado.setSelection(spinnerPosition);
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setServicioContratado(servicioContratado.getSelectedItem() != null ? servicioContratado.getSelectedItem().toString().trim() : "");
                validarCampos();
            }
        };

        btnIniciarForm.setOnClickListener(view1 -> {
            if (btnIniciarForm.isEnabled()) {
                String uid = currentUser.getUid();

                db.collection("lista_de_usuarios").document(uid)
                        .collection("formulario")
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            int maxNum = 0;
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String collectionName = document.getId();
                                if (collectionName.startsWith("formulario")) {
                                    try {
                                        int num = Integer.parseInt(collectionName.substring(10));
                                        if (num > maxNum) {
                                            maxNum = num;
                                        }
                                    } catch (NumberFormatException e) {
                                        // No hacer nada si no se puede parsear el número
                                    }
                                }
                            }
                            String newCollectionName = "formulario" + (maxNum + 1);

                            Map<String, Object> datosFormulario = new HashMap<>();
                            datosFormulario.put("servicioContratado", servicioContratado.getSelectedItem().toString());
                            datosFormulario.put("estado", "No Iniciado"); // Establecer el estado inicial

                            db.collection("lista_de_usuarios").document(uid)
                                    .collection(newCollectionName).document("datos")
                                    .set(datosFormulario, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        viewModel.setNombreColeccionFormulario(newCollectionName); // Establecer el nombre de la colección en ViewModel
                                        navController.navigate(R.id.navigation_loading_i_f);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al guardar datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error al obtener formularios existentes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
