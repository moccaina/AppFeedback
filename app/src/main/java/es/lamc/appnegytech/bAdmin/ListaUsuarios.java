package es.lamc.appnegytech.bAdmin;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.lamc.appnegytech.dData.User;
import es.lamc.appnegytech.dData.UserAdapter;
import es.lamc.appnegytech.databinding.FragmentListaUsuariosBinding;

public class ListaUsuarios extends Fragment {

    FragmentListaUsuariosBinding binding;
    NavController navController;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Context context;
    View view;
    UserAdapter userAdapter;
    List<User> userList;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListaUsuariosBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        if (context == null) {
            Toast.makeText(getActivity(), "Error al obtener el contexto.", Toast.LENGTH_SHORT).show();
            return;
        }
        navController = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);

        binding.recyclerViewUsers.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerViewUsers.setAdapter(userAdapter);

        loadUsers();
    }

    private void loadUsers() {
        db.collection("lista_de_usuarios")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Error al cargar la lista de usuarios.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al obtener datos de Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
