package es.lamc.appnegytech.bAdmin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentCambiarEstadoBinding;

public class CambiarEstado extends Fragment {

    private FragmentCambiarEstadoBinding binding;
    private View view;
    private Context context;
    private NavController navController;
    private String documentId;
    private String userId;
    private String collectionName;
    private String estado;
    private ImageButton backButton;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCambiarEstadoBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        backButton = view.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Recupera los argumentos del fragmento
        if (getArguments() != null) {
            documentId = getArguments().getString("documentId");
            userId = getArguments().getString("userId");
            collectionName = getArguments().getString("collectionName");
            estado = getArguments().getString("estado");
        }

        loadCurrentState();

        // Configura el Spinner para seleccionar el nuevo estado
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context, R.array.estado_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEstado.setAdapter(adapter);

        binding.btnActualizarEstado.setOnClickListener(v -> {
            String selectedEstado = binding.spinnerEstado.getSelectedItem().toString();
            updateEstado(selectedEstado);
        });
    }

    private void loadCurrentState() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lista_de_usuarios")
                .document(userId)
                .collection(collectionName)
                .document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String estado = document.getString("estado");
                            binding.textEstado.setText(estado);
                        } else {
                            binding.textEstado.setText("No disponible");
                        }
                    } else {
                        binding.textEstado.setText("Error al cargar estado");
                    }
                });
    }


    private void updateEstado(String newEstado) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lista_de_usuarios")
                .document(userId)
                .collection(collectionName)  // Ajusta el nombre de la colección si es necesario
                .document(documentId)
                .update("estado", newEstado)
                .addOnSuccessListener(aVoid -> {
                    // Manejar éxito
                    navController.navigate(R.id.action_cambiar_estado_to_inicio_admin);  // Navegar al fragmento deseado
                })
                .addOnFailureListener(e -> {
                    // Manejar error
                });
    }

}
