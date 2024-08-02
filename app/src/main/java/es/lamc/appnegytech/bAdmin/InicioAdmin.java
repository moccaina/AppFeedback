package es.lamc.appnegytech.bAdmin;

import static es.lamc.appnegytech.cUser.InicioForm.NUM_COLLECTIONS;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import es.lamc.appnegytech.R;
import es.lamc.appnegytech.dData.FormResult;
import es.lamc.appnegytech.dData.FormResultAdapter;
import es.lamc.appnegytech.databinding.FragmentInicioAdminBinding;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.Calendar;

public class InicioAdmin extends Fragment {

    private FragmentInicioAdminBinding binding;
    private FirebaseAuth mAuth;
    private View view;
    private Context context;
    private NavController navController;
    private FormResultAdapter userAdapter;
    private List<FormResult> userList;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInicioAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();

        setupRecyclerView();


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

        loadUsers();
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new FormResultAdapter(getContext(), userList, new FormResultAdapter.OnButtonClickListener() {
            @Override
            public void onVerResultadosClick(FormResult formResult) {
                Bundle bundle = new Bundle();
                bundle.putString("userId", formResult.getUserId());
                bundle.putString("documentId", formResult.getDocumentId());
                bundle.putString("collectionName", formResult.getCollectionName());
                navController.navigate(R.id.navigation_fragment_ver_resultados, bundle);
            }

            @Override
            public void onCambiarEstadoClick(FormResult formResult) {
                Bundle bundle = new Bundle();
                bundle.putString("userId", formResult.getUserId());
                bundle.putString("documentId", formResult.getDocumentId());
                bundle.putString("collectionName", formResult.getCollectionName());
                navController.navigate(R.id.navigation_fragment_cambiar_estado, bundle);
            }

        });
        binding.recyclerViewFormResult.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewFormResult.setAdapter(userAdapter);
    }

    private void loadUsers() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("lista_de_usuarios")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            userList.clear();
                            for (DocumentSnapshot userDocument : querySnapshot.getDocuments()) {
                                String nomCom = userDocument.getString("nomCom");
                                String userId = userDocument.getId();

                                for (int i = 1; i <= NUM_COLLECTIONS; i++) {
                                    String collectionName = "formulario" + i;
                                    userDocument.getReference().collection(collectionName)
                                            .get()
                                            .addOnCompleteListener(formTask -> {
                                                if (formTask.isSuccessful()) {
                                                    QuerySnapshot formQuerySnapshot = formTask.getResult();
                                                    if (formQuerySnapshot != null && !formQuerySnapshot.isEmpty()) {
                                                        for (DocumentSnapshot formDocument : formQuerySnapshot.getDocuments()) {
                                                            String servicioContratado = formDocument.getString("servicioContratado");
                                                            String estado = formDocument.getString("estado");
                                                            String documentId = formDocument.getId();

                                                            if (nomCom != null && !nomCom.isEmpty() &&
                                                                    servicioContratado != null && !servicioContratado.isEmpty() &&
                                                                    estado != null && !estado.isEmpty()) {

                                                                FormResult formResult = new FormResult(nomCom, servicioContratado, estado, userId, documentId, collectionName);
                                                                userList.add(formResult);
                                                                Log.d("InicioAdmin", "Added: " + formResult.toString());
                                                            } else {
                                                                Log.d("InicioAdmin", "Datos incompletos en " + collectionName);
                                                            }
                                                        }
                                                        userAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Log.d("InicioAdmin", "No documents in " + collectionName);
                                                    }
                                                } else {
                                                    Log.e("InicioAdmin", "Error getting " + collectionName + ": ", formTask.getException());
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.d("InicioAdmin", "No documents in lista_de_usuarios");
                        }
                    } else {
                        Log.e("InicioAdmin", "Error getting lista_de_usuarios: ", task.getException());
                    }
                });
    }

}
