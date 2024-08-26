package es.lamc.appnegytech.bAdmin;

import android.app.DatePickerDialog;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.lamc.appnegytech.R;
import es.lamc.appnegytech.dData.CustomSpinnerAdapter;
import es.lamc.appnegytech.databinding.FragmentFinanzasAdminBinding;

public class FinanzasAdmin extends Fragment {

    private FragmentFinanzasAdminBinding binding;
    private View view;
    private Context context;
    private NavController navController;
    private String documentId;
    private String userId;
    private String collectionName;
    private ImageButton backButton;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFinanzasAdminBinding.inflate(inflater, container, false);
        return view = binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        navController = Navigation.findNavController(view);

        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> getActivity().onBackPressed());
        binding.fechaVencimiento.setOnClickListener(v -> showDatePickerDialog(v));

        if (getArguments() != null) {
            documentId = getArguments().getString("documentId");
            userId = getArguments().getString("userId");
            collectionName = getArguments().getString("collectionName");
        }

        setupSpinner();
        loadCurrentState();

        binding.btnActualizarEstado.setOnClickListener(v ->  updateEstado());

    }

    private void setupSpinner() {
        Spinner spinnerEstadoPago = binding.spinnerEstadoPago;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.estado_pago_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstadoPago.setAdapter(adapter);
    }

    private void setupSpinnerOptions(String estadoServicio) {
        Spinner spinnerEstadoPago = binding.spinnerEstadoPago;
        List<String> options = new ArrayList<>();
        List<Boolean> isEnabledList = new ArrayList<>();

        if ("Pagado".equals(estadoServicio)) {
            options.add("Pagado");
            isEnabledList.add(true);
        } else if ("Atrasado".equals(estadoServicio)) {
            options.add("Pagado");
            isEnabledList.add(true);
        } else {
            options.add("Pendiente");
            options.add("Pagado");
            isEnabledList.add(true);
            isEnabledList.add(true);
        }

        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getContext(), options, isEnabledList);
        spinnerEstadoPago.setAdapter(adapter);

        // Configurar el Spinner para permitir o deshabilitar la selección
        if ("Pagado".equals(estadoServicio)) {
            spinnerEstadoPago.setEnabled(false);
        } else {
            spinnerEstadoPago.setEnabled(true);
        }
    }


    private void loadCurrentState() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("lista_de_usuarios")
                .document(userId)
                .collection(collectionName)
                .document("datos")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String estadoServicio = document.getString("estadoServicio");
                            Date fechaVencimiento = document.getDate("fechaVencimiento");
                            Double montoServicio = document.getDouble("montoServicio");

                            binding.textServicio.setText(estadoServicio);
                            binding.fechaVencimiento.setText(dateFormat.format(fechaVencimiento)); // Convertir Date a String
                            binding.montoPagar.setText(String.valueOf(montoServicio));

                            setupSpinnerOptions(estadoServicio);
                        } else {
                            binding.textServicio.setText("No disponible");
                        }
                    } else {
                        binding.textServicio.setText("Error al cargar estado");
                    }
                });
    }

    private void updateEstado() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String nuevaFechaVencimientoStr = binding.fechaVencimiento.getText().toString();
        Double nuevoMontoServicio = Double.parseDouble(binding.montoPagar.getText().toString());
        String nuevoEstadoServicio = binding.spinnerEstadoPago.getSelectedItem().toString();

        try {
            Date nuevaFechaVencimiento = dateFormat.parse(nuevaFechaVencimientoStr);

            db.collection("lista_de_usuarios")
                    .document(userId)
                    .collection(collectionName)
                    .document("datos")
                    .update("fechaVencimiento", nuevaFechaVencimiento,
                            "montoServicio", nuevoMontoServicio,
                            "estadoServicio", nuevoEstadoServicio)
                    .addOnSuccessListener(aVoid -> {
                        navController.navigate(R.id.action_finanzas_admin_to_inicio_admin);
                    })
                    .addOnFailureListener(e -> {
                        // Manejar error
                    });
        } catch (ParseException e) {
            e.printStackTrace();
            // Manejar error en el formato de fecha
        }
    }

    public void showDatePickerDialog(View v) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            binding.fechaVencimiento.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }
}