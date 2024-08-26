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

        holder.servicioContratadoTextView.setText(servicio != null ? servicio : "N/A");

    }

    @Override
    public int getItemCount() {
        return formList != null ? formList.size() : 0;
    }

    public void updateData(List<DocumentSnapshot> newData) {
        List<DocumentSnapshot> filteredData = new ArrayList<>();
        for (DocumentSnapshot document : newData) {
            String servicio = document.getString("servicioContratado");

            if (servicio != null && !servicio.isEmpty()) {
                filteredData.add(document);
            }
        }
        formList.clear();
        formList.addAll(filteredData);
        notifyDataSetChanged();
    }


    static class FormViewHolder extends RecyclerView.ViewHolder {
        TextView servicioContratadoTextView;

        FormViewHolder(@NonNull View itemView) {
            super(itemView);
            servicioContratadoTextView = itemView.findViewById(R.id.servicio_textview);
        }
    }
}
