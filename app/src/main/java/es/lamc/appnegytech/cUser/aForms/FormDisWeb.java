package es.lamc.appnegytech.cUser.aForms;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.dData.ViewModelFormData1;
import es.lamc.appnegytech.databinding.FragmentFormDisWebBinding;

public class FormDisWeb extends Fragment {

    private FragmentFormDisWebBinding binding;
    private View view;
    private Context context;
    private NavController navController;
    private ViewModelFormData1 viewModel;

    private FirebaseAuth mAuth;
    private RadioGroup Pregunta1;
    private RadioGroup Pregunta2;
    private RadioGroup Pregunta3;
    private RadioGroup Pregunta4;
    private EditText Pregunta5;
    private Button btnEnviarFormulario;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFormDisWebBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelFormData1.class);
        mAuth = FirebaseAuth.getInstance();

        Pregunta1 = binding.Pregunta1;
        Pregunta2 = binding.Pregunta2;
        Pregunta3 = binding.Pregunta3;
        Pregunta4 = binding.Pregunta4;
        Pregunta5 = binding.Pregunta5;

        btnEnviarFormulario = binding.btnEnviarFormulario;

        Pregunta1.setOnCheckedChangeListener((group, checkedId) -> checkAllQuestionsAnswered());
        Pregunta2.setOnCheckedChangeListener((group, checkedId) -> checkAllQuestionsAnswered());
        Pregunta3.setOnCheckedChangeListener((group, checkedId) -> checkAllQuestionsAnswered());
        Pregunta4.setOnCheckedChangeListener((group, checkedId) -> checkAllQuestionsAnswered());
        Pregunta5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkAllQuestionsAnswered();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnEnviarFormulario.setOnClickListener(v -> enviarFormulario());
        checkAllQuestionsAnswered();
    }

    private void checkAllQuestionsAnswered() {
        boolean allAnswered = Pregunta1.getCheckedRadioButtonId() != -1 &&
                Pregunta2.getCheckedRadioButtonId() != -1 &&
                Pregunta3.getCheckedRadioButtonId() != -1 &&
                Pregunta4.getCheckedRadioButtonId() != -1;

        btnEnviarFormulario.setEnabled(allAnswered);
    }

    private void enviarFormulario() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String servicioSeleccionado = viewModel.getServicioContratado().getValue(); // Obtener servicio seleccionado
            if (servicioSeleccionado != null) {
                db.collection("lista_de_usuarios").document(uid)
                        .collection("configuracion").document("numeroFormulario")
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            int maxNum;
                            if (documentSnapshot.exists()) {
                                maxNum = documentSnapshot.getLong("maxNum").intValue();
                            } else {
                                maxNum = 0;
                            }

                            String newCollectionName = "formulario" + (maxNum + 1);

                            Map<String, Object> datosFormulario = new HashMap<>();
                            datosFormulario.put("servicioContratado", servicioSeleccionado);
                            datosFormulario.put("estadoServicio", "Pendiente"); // String
                            datosFormulario.put("fechaVencimiento", new Date());
                            datosFormulario.put("montoServicio", 0.0);
                            datosFormulario.put("textMonto", "Monto a Pagar");

                            // Guardar el formulario en la nueva colección
                            db.collection("lista_de_usuarios").document(uid)
                                    .collection(newCollectionName).document("datos")
                                    .set(datosFormulario, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        // Actualizar el número máximo del formulario
                                        Map<String, Object> updateData = new HashMap<>();
                                        updateData.put("maxNum", maxNum + 1);
                                        db.collection("lista_de_usuarios").document(uid)
                                                .collection("configuracion").document("numeroFormulario")
                                                .set(updateData, SetOptions.merge())
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // Guardar respuestas
                                                    guardarRespuestas(newCollectionName);
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
            } else {
                Toast.makeText(context, "Error: No se pudo obtener el servicio seleccionado.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void guardarRespuestas(String collectionName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            Map<String, Object> respuestas = new HashMap<>();
            respuestas.put("pregunta1", ((RadioButton) getView().findViewById(Pregunta1.getCheckedRadioButtonId())).getText().toString());
            respuestas.put("pregunta2", ((RadioButton) getView().findViewById(Pregunta2.getCheckedRadioButtonId())).getText().toString());
            respuestas.put("pregunta3", ((RadioButton) getView().findViewById(Pregunta3.getCheckedRadioButtonId())).getText().toString());
            respuestas.put("pregunta4", ((RadioButton) getView().findViewById(Pregunta4.getCheckedRadioButtonId())).getText().toString());
            respuestas.put("pregunta5", Pregunta5.getText().toString());

            db.collection("lista_de_usuarios").document(uid)
                    .collection(collectionName).document("respuestas")
                    .set(respuestas)
                    .addOnSuccessListener(aVoid -> {
                        navController.navigate(R.id.navigation_loading_f_i_f);
                        Toast.makeText(getContext(), "Formulario enviado exitosamente", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al enviar formulario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
}