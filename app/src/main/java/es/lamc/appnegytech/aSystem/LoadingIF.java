package es.lamc.appnegytech.aSystem;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.databinding.FragmentLoadingIFBinding;

public class LoadingIF extends Fragment {

    FragmentLoadingIFBinding binding;
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
        binding = FragmentLoadingIFBinding.inflate(inflater, container, false );
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated (@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        new Handler().postDelayed(() -> Navigation.findNavController( view ).navigate( R.id.navigation_formulario),2000);
    }
}