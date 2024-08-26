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
                String servicioSeleccionado = servicioContratado.getSelectedItem().toString();
                viewModel.setServicioContratado(servicioSeleccionado);

                switch (servicioSeleccionado) {
                    case "Creación de Contenido":
                        navController.navigate(R.id.navigation_form_cre_con);
                        break;
                    case "Diseño Web":
                        navController.navigate(R.id.navigation_form_dis_web);
                        break;
                    case "Branding":
                        navController.navigate(R.id.navigation_form_bra);
                        break;
                    case "Brochure digital":
                        navController.navigate(R.id.navigation_form_bro_dig);
                        break;
                    case "Soluciones Integrales de Diseño Gráfico":
                        navController.navigate(R.id.navigation_form_sol_int_dis_gra);
                        break;
                    case "Social Media Ads":
                        navController.navigate(R.id.navigation_form_soc_med_ads);
                        break;
                    default:
                        Toast.makeText(context, "Opción no válida seleccionada", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        validarCampos();
    }

    private void validarCampos() {
        String servicio = servicioContratado.getSelectedItem() != null ? servicioContratado.getSelectedItem().toString().trim() : "";

        btnIniciarForm.setEnabled(!servicio.isEmpty());
    }
}
