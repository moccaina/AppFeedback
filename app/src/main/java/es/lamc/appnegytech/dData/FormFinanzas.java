package es.lamc.appnegytech.dData;

public class FormFinanzas {
    private String servicio;
    private String estadoServicio;
    private String fechaVencimiento;
    private String montoServicio;
    private String textMonto;

    public FormFinanzas(String servicio, String estadoServicio, String fechaVencimiento, String montoServicio, String textMonto) {
        this.servicio = servicio;
        this.estadoServicio = estadoServicio;
        this.fechaVencimiento = fechaVencimiento;
        this.montoServicio = montoServicio;
        this.textMonto = textMonto;
    }

    public String getServicio() {
        return servicio;
    }

    public String getEstadoServicio() { return estadoServicio; }

    public String getFechaVencimiento() { return fechaVencimiento; }

    public String getMontoServicio() { return montoServicio; }

    public String getTextMonto() { return textMonto; }
}
