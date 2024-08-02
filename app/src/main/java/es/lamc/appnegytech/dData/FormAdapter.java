package es.lamc.appnegytech.dData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import es.lamc.appnegytech.R;

public class FormAdapter extends RecyclerView.Adapter<FormAdapter.FormViewHolder> {

    private List<DocumentSnapshot> formList;

    public FormAdapter(List<DocumentSnapshot> formList) {
        this.formList = formList;
    }

    @NonNull
    @Override
    public FormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form, parent, false);
        return new FormViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FormViewHolder holder, int position) {
        DocumentSnapshot document = formList.get(position);

        String servicio = document.getString("servicioContratado");
        String estado = document.getString("estado");

        holder.servicioTextView.setText(servicio != null ? servicio : "N/A");
        holder.estadoTextView.setText(estado != null ? estado : "N/A");

        int color;
        if (estado != null) {
            switch (estado) {
                case "No Iniciado":
                    color = holder.itemView.getResources().getColor(android.R.color.holo_red_dark);
                    break;
                case "En Proceso":
                    color = holder.itemView.getResources().getColor(android.R.color.holo_orange_dark);
                    break;
                case "Finalizado":
                    color = holder.itemView.getResources().getColor(android.R.color.holo_green_dark);
                    break;
                default:
                    color = holder.itemView.getResources().getColor(android.R.color.darker_gray);
                    break;
            }
        } else {
            color = holder.itemView.getResources().getColor(android.R.color.darker_gray);
        }
        holder.estadoContainer.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return formList != null ? formList.size() : 0;
    }

    public void updateData(List<DocumentSnapshot> newData) {
        List<DocumentSnapshot> filteredData = new ArrayList<>();
        for (DocumentSnapshot document : newData) {
            String servicio = document.getString("servicioContratado");
            String estado = document.getString("estado");

            if (servicio != null && !servicio.isEmpty() &&
                    estado != null && !estado.isEmpty()) {
                filteredData.add(document);
            }
        }
        formList.clear();
        formList.addAll(filteredData);
        notifyDataSetChanged();
    }


    static class FormViewHolder extends RecyclerView.ViewHolder {
        TextView servicioTextView;
        TextView estadoTextView;
        View estadoContainer;

        FormViewHolder(@NonNull View itemView) {
            super(itemView);
            servicioTextView = itemView.findViewById(R.id.servicio_textview);
            estadoTextView = itemView.findViewById(R.id.estado_textview);
            estadoContainer = itemView.findViewById(R.id.estado_container);
        }
    }
}
