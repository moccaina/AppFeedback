package es.lamc.appnegytech.dData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelFormData1 extends ViewModel {
    private final MutableLiveData<String> servicioContratado = new MutableLiveData<>();
    private final MutableLiveData<String> nombreColeccionFormulario = new MutableLiveData<>();

    public LiveData<String> getServicioContratado() {
        return servicioContratado;
    }

    public void setServicioContratado(String servicio) {
        servicioContratado.setValue(servicio);
    }

    public LiveData<String> getNombreColeccionFormulario() {
        return nombreColeccionFormulario;
    }

    public void setNombreColeccionFormulario(String nombreColeccion) {
        nombreColeccionFormulario.setValue(nombreColeccion);
    }
}
