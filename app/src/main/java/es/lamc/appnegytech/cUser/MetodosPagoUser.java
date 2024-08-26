package es.lamc.appnegytech.cUser;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentMetodosPagoUserBinding;

public class MetodosPagoUser extends Fragment {

    FragmentMetodosPagoUserBinding binding;
    View view;
    Context context;
    NavController navController;

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMetodosPagoUserBinding.inflate(inflater, container, false );
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        binding.btnAtras.setOnClickListener(v -> {
            navController.navigate(R.id.action_metodos_pago_to_finanzas_user);
        });

    }
}