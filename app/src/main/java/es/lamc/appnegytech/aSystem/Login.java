package es.lamc.appnegytech.aSystem;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentLoadingBinding;
import es.lamc.appnegytech.databinding.FragmentLoginBinding;


public class Login extends Fragment {
    FragmentLoginBinding binding;
    FragmentLoadingBinding loadingBinding;
    View view;
    Context context;
    NavController navController;
    private FirebaseAuth mAuth;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(view12 -> loginUser());

        binding.btnTogglePassword.setOnClickListener(view13 -> {
            togglePasswordVisibility();
        });
    }

    private void loginUser() {

        showLoading(true);

        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Por favor, ingrese su correo y contraseña", Toast.LENGTH_SHORT).show();
            showLoading(false);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {

                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            if (user.getEmail().equals("amontalvo259@gmail.com")) {
                                navController.navigate(R.id.navigation_inicio_admin);
                                showLoading(false);
                            } else {
                                verificarDatos(user.getUid());
                            }
                        }

                    } else {
                        Toast.makeText(context, "Usuario no registrado.", Toast.LENGTH_SHORT).show();
                        showLoading(false);
                    }
                });
    }

    private void verificarDatos(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lista_de_usuarios").document(uid)
                .collection("formulario1").document("respuestas").get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        navController.navigate(R.id.navigation_inicio_form);
                        showLoading(false);
                    } else {
                        if (mAuth.getCurrentUser().getEmail().equals("amontalvo259@gmail.com")) {
                            new Handler().postDelayed(() -> navController.navigate(R.id.navigation_inicio_admin), 2000);
                        } else {
                            new Handler().postDelayed(() -> navController.navigate(R.id.navigation_inicio), 2000);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (mAuth.getCurrentUser().getEmail().equals("amontalvo259@gmail.com")) {
                        new Handler().postDelayed(() -> navController.navigate(R.id.navigation_inicio_admin), 2000);
                    } else {
                        new Handler().postDelayed(() -> navController.navigate(R.id.navigation_inicio), 2000);
                    }
                });
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