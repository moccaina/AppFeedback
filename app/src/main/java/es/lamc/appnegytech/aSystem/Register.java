package es.lamc.appnegytech.aSystem;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentLoadingBinding;
import es.lamc.appnegytech.databinding.FragmentRegisterBinding;


public class Register extends Fragment {

    FragmentRegisterBinding binding;
    FragmentLoadingBinding loadingBinding;
    View view;
    Context context;
    NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FirebaseUser originalUser;
    private String originalUserPassword;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        originalUser = mAuth.getCurrentUser();
        originalUserPassword = "Admin1";

        binding.btnRegister.setOnClickListener(view22 -> registerUser());

        binding.btnTogglePassword.setOnClickListener(view23 -> {
            togglePasswordVisibility();
        });
    }

    private void registerUser() {

        showLoading(true);

        String nomcom = binding.nomcom.getText().toString().trim();
        String razsoc = binding.razsoc.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || nomcom.isEmpty() || razsoc.isEmpty()) {
            Toast.makeText(context, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            User newUser = new User(nomcom, razsoc, email);
                            db.collection("lista_de_usuarios")
                                    .document(user.getUid())
                                    .set(newUser)
                                    .addOnSuccessListener(aVoid -> {
                                        user.updateProfile(new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(nomcom)
                                                        .build())
                                                .addOnCompleteListener(updateTask -> {
                                                    if (updateTask.isSuccessful()) {
                                                        clearFields();
                                                        reauthenticateOriginalUser();
                                                        Toast.makeText(context, "Registro de usuario exitoso", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Error al guardar los datos del usuario", Toast.LENGTH_SHORT).show();
                                        showLoading(false);
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Error al registrarse.", Toast.LENGTH_SHORT).show();
                        showLoading(false);
                    }
                });
    }

    private void reauthenticateOriginalUser() {
        if (originalUser != null && originalUserPassword != null) {
            mAuth.signOut();
            mAuth.signInWithEmailAndPassword(originalUser.getEmail(), originalUserPassword)
                    .addOnCompleteListener(task -> {
                        showLoading(false);
                    });
        } else {
            showLoading(false);
        }
    }

    private void clearFields() {
        binding.nomcom.setText("");
        binding.razsoc.setText("");
        binding.email.setText("");
        binding.password.setText("");
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            if (loadingBinding == null) {
                loadingBinding = FragmentLoadingBinding.inflate(getLayoutInflater());
                ((ViewGroup) binding.getRoot()).addView(loadingBinding.getRoot());
            }
            loadingBinding.getRoot().setVisibility(View.VISIBLE);
        } else {
            if (loadingBinding != null) {
                loadingBinding.getRoot().setVisibility(View.GONE);
            }
        }
    }

    public static class User {
        private String nomcom;
        private String razsoc;
        private String email;

        public User(String nomcom, String razsoc, String email) {
            this.nomcom = nomcom;
            this.razsoc = razsoc;
            this.email = email;
        }

        public String getNomCom() { return nomcom; }
        public void setNomCom(String nomcom) { this.nomcom = nomcom; }
        public String getRazSoc() { return razsoc; }
        public void setRazSoc(String razsoc) { this.razsoc = razsoc; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    private void togglePasswordVisibility() {
        int inputType = binding.password.getInputType();

        if (inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            binding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.btnTogglePassword.setImageResource(R.drawable.ic_visibility_on);
        } else {
            binding.password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            binding.btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
        }

        binding.password.setSelection(binding.password.getText().length());
    }
}