package es.lamc.appnegytech.bAdmin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentLoadingBinding;
import es.lamc.appnegytech.databinding.FragmentVerResultadoBinding;


public class VerResultado extends Fragment {

    FragmentVerResultadoBinding binding;
    View view;
    Context context;
    NavController navController;
    private String documentId;
    private String userId;
    private String collectionName;
    FirebaseFirestore db;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVerResultadoBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            documentId = getArguments().getString("documentId");
            userId = getArguments().getString("userId");
            collectionName = getArguments().getString("collectionName");
        }

        loadRespuestas();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigateUp();
            }
        });
    }

    private void loadRespuestas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lista_de_usuarios")
                .document(userId)
                .collection(collectionName)
                .document("respuestas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String respuesta1 = document.getString("pregunta1");
                            String respuesta2 = document.getString("pregunta2");
                            binding.textViewRespuesta1.setText(respuesta1);
                            binding.textViewRespuesta2.setText(respuesta2);
                        } else {
                            Toast.makeText(context, "No se encontraron respuestas.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Error al obtener respuestas: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}