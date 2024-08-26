package es.lamc.appnegytech.aSystem;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentSplashBinding;

public class Splash extends Fragment {

    private FragmentSplashBinding binding;
    private NavController navController;
    private FirebaseAuth mAuth;
    private Context context;
    private View view;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            verificarDatos(user.getUid());
        } else {
            new Handler().postDelayed(() -> navController.navigate(R.id.navigation_login), 2000);
        }
    }

    private void verificarDatos(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lista_de_usuarios").document(uid)
                .collection("formulario1")
                .document("respuestas")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        new Handler().postDelayed(() -> navController.navigate(R.id.navigation_inicio_form), 2000);
                    } else {
                        verificarEmailYNavegar();
                    }
                })
                .addOnFailureListener(e -> {
                    verificarEmailYNavegar();
                });
    }

    private void verificarEmailYNavegar() {
        String email = mAuth.getCurrentUser().getEmail();
        if (email != null && email.equals("alexander.montalvo@negytech.com")) {
            new Handler().postDelayed(() -> navController.navigate(R.id.navigation_inicio_admin), 2000);
        } else {
            new Handler().postDelayed(() -> navController.navigate(R.id.navigation_inicio_form), 2000);
        }
    }
}
