package es.lamc.appnegytech.dData;

public class Form {
    private String servicio;
    private String estado;

    public Form(String servicio, String estado) {
        this.servicio = servicio;
        this.estado = estado;
    }

    public String getServicio() {
        return servicio;
    }

    public String getEstado() {
        return estado;
    }
}
