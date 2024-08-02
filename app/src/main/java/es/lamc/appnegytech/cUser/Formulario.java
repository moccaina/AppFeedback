package es.lamc.appnegytech.cUser;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import es.lamc.appnegytech.dData.ViewModelFormData1;
import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentFormularioBinding;


public class Formulario extends Fragment {

    private FragmentFormularioBinding binding;
    private View view;
    private Context context;
    private NavController navController;
    private ViewModelFormData1 viewModel;

    private FirebaseAuth mAuth;
    private RadioGroup Pregunta1;
    private EditText Pregunta2;
    private Button btnEnviarFormulario;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFormularioBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(requireActivity()).get(ViewModelFormData1.class);
        mAuth = FirebaseAuth.getInstance();

        binding.btnAtras.setOnClickListener(view31 -> {
            navController.navigate(R.id.navigation_loading);
        });

        Pregunta1 = binding.Pregunta1;
        Pregunta2 = binding.Pregunta2;
        btnEnviarFormulario = binding.btnEnviarFormulario;

        Pregunta1.setOnCheckedChangeListener((group, checkedId) -> checkAllQuestionsAnswered());
        Pregunta2.addTextChangedListener(new TextWatcher() {
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
        boolean allAnswered = Pregunta1.getCheckedRadioButtonId() != -1 && !Pregunta2.getText().toString().isEmpty();

        btnEnviarFormulario.setEnabled(allAnswered);
    }

    private void enviarFormulario() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            String collectionName = viewModel.getNombreColeccionFormulario().getValue();

            if (collectionName != null) {
                Map<String, Object> respuestas = new HashMap<>();
                respuestas.put("pregunta1", ((RadioButton) getView().findViewById(Pregunta1.getCheckedRadioButtonId())).getText().toString());
                respuestas.put("pregunta2", Pregunta2.getText().toString());

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
            } else {
                Toast.makeText(getContext(), "Error: No se pudo determinar la colección del formulario.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
