package es.lamc.appnegytech.dData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.lamc.appnegytech.R;

public class FormFinanzasAdapter extends RecyclerView.Adapter<FormFinanzasAdapter.FormViewHolder> {

    private List<DocumentSnapshot> formList;

    public FormFinanzasAdapter(List<DocumentSnapshot> formList) {
        this.formList = formList;
    }

    @NonNull
    @Override
    public FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_finanzas, parent, false);
        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormViewHolder holder, int position) {
        DocumentSnapshot document = formList.get(position);

        String servicio = document.getString("servicioContratado");
        String estadoServicio = document.getString("estadoServicio");
        Date fechaVencimiento = document.getDate("fechaVencimiento");
        Double montoServicio = document.getDouble("montoServicio");
        String textMonto = document.getString("textMonto");

        holder.servicioTextView.setText(servicio != null ? servicio : "N/A");
        holder.estadoServicioTextView.setText(estadoServicio);
        holder.fechaVencimientoTextView.setText(fechaVencimiento != null ? fechaVencimiento.toString() : "No date");
        holder.montoServicioTextView.setText(montoServicio != null ? "S/. " + montoServicio.toString() : "S/. 0.00");
        holder.textMontoTextView.setText(textMonto != null ? textMonto : "N/A");


        if ("Pendiente".equals(estadoServicio) || "Atrasado".equals(estadoServicio)) {
            holder.textMontoTextView.setText("Monto a Pagar:");
        } else if ("Pagado".equals(estadoServicio)) {
            holder.textMontoTextView.setText("Monto Pagado:");
        } else {
            holder.textMontoTextView.setText("N/A");
        }


        if (fechaVencimiento != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = sdf.format(fechaVencimiento);
            holder.fechaVencimientoTextView.setText(formattedDate);

            if (fechaVencimiento.before(new Date())) {
                estadoServicio = "Atrasado";
            }
        }

        if (estadoServicio != null) {
            holder.estadoServicioTextView.setText(estadoServicio);

            // Establecer el color del texto basado en el estado del servicio
            int colorText;
            switch (estadoServicio) {
                case "Pendiente":
                    colorText = holder.itemView.getResources().getColor(android.R.color.holo_orange_dark);
                    break;
                case "Pagado":
                    colorText = holder.itemView.getResources().getColor(android.R.color.holo_green_dark);
                    break;
                case "Atrasado":
                    colorText = holder.itemView.getResources().getColor(android.R.color.holo_red_dark);
                    break;
                default:
                    colorText = holder.itemView.getResources().getColor(android.R.color.black);
                    break;
            }
            holder.estadoServicioTextView.setTextColor(colorText);
        } else {
            holder.estadoServicioTextView.setTextColor(holder.itemView.getResources().getColor(android.R.color.black));
        }

        if (montoServicio != null) {
            holder.montoServicioTextView.setText(String.format(Locale.getDefault(), "S/. %.2f", montoServicio));
        }
    }


    @Override
    public int getItemCount() {
        return formList != null ? formList.size() : 0;
    }

    public void updateData(List<DocumentSnapshot> newData) {
        List<DocumentSnapshot> filteredData = new ArrayList<>();
        for (DocumentSnapshot document : newData) {
            String servicio = document.getString("servicioContratado");
            String estadoServicio = document.getString("estadoServicio");
            Date fechaVencimiento = document.getDate("fechaVencimiento");
            Double montoServicio = document.getDouble("montoServicio");
            String textMonto = document.getString("textMonto");

            if (servicio != null && !servicio.isEmpty() &&
                    estadoServicio != null && !estadoServicio.isEmpty() &&
                    fechaVencimiento != null && montoServicio != null &&
                    textMonto != null && !textMonto.isEmpty()) {
                filteredData.add(document);
            }
        }
        formList.clear();
        formList.addAll(filteredData);
        notifyDataSetChanged();
    }


    static class FormViewHolder extends RecyclerView.ViewHolder {
        TextView servicioTextView;
        TextView estadoServicioTextView;
        TextView fechaVencimientoTextView;
        TextView montoServicioTextView;
        TextView textMontoTextView;

        FormViewHolder(@NonNull View itemView) {
            super(itemView);
            servicioTextView = itemView.findViewById(R.id.servicio_textview);
            estadoServicioTextView = itemView.findViewById(R.id.text_estado_pago);
            fechaVencimientoTextView = itemView.findViewById(R.id.text_fecha_vencimiento);
            montoServicioTextView = itemView.findViewById(R.id.text_monto_pago);
            textMontoTextView = itemView.findViewById(R.id.text_monto_pagado);
        }
    }
}
