package es.lamc.appnegytech.dData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import es.lamc.appnegytech.R;

public class FormResultAdapter extends RecyclerView.Adapter<FormResultAdapter.ViewHolder> {

    private static Context context;
    private List<FormResult> formResultList;
    private OnButtonClickListener onButtonClickListener;

    public interface OnButtonClickListener {
        void onVerResultadosClick(FormResult formResult);
        void onCambiarEstadoClick(FormResult formResult);
    }

    public FormResultAdapter(Context context, List<FormResult> formResults, OnButtonClickListener listener) {
        this.context = context;
        this.formResultList = formResults != null ? formResults : new ArrayList<>();
        this.onButtonClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_form_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FormResult formResult = formResultList.get(position);

        // Verifica si los datos están presentes
        if (formResult != null) {
            holder.nombreEmpresa.setText(formResult.getNomCom() != null ? formResult.getNomCom() : "N/A");
            holder.servicioContratado.setText(formResult.getServicioContratado() != null ? formResult.getServicioContratado() : "N/A");
            holder.estado.setText(formResult.getEstado() != null ? formResult.getEstado() : "N/A");

            holder.btnVerResultado.setOnClickListener(v -> {
                if (onButtonClickListener != null) {
                    onButtonClickListener.onVerResultadosClick(formResult);
                }
            });

            // Configura el botón "Cambiar Estado"
            holder.btnCambiarEstado.setOnClickListener(v -> {
                if (onButtonClickListener != null) {
                    onButtonClickListener.onCambiarEstadoClick(formResult);
                }
            });
        } else {
            // Manejo de caso en el que formResult sea nulo
            holder.nombreEmpresa.setText("N/A");
            holder.servicioContratado.setText("N/A");
            holder.estado.setText("N/A");
        }
    }

    @Override
    public int getItemCount() {
        return formResultList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreEmpresa, servicioContratado, estado;
        Button btnVerResultado;
        Button btnCambiarEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreEmpresa = itemView.findViewById(R.id.nombre_empresa);
            servicioContratado = itemView.findViewById(R.id.servicio_contratado);
            estado = itemView.findViewById(R.id.estado);
            btnVerResultado = itemView.findViewById(R.id.btn_ver_resultado);
            btnCambiarEstado = itemView.findViewById(R.id.btn_cambiar_estado);
        }

    }
}

