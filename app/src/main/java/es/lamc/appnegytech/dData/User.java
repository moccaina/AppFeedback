package es.lamc.appnegytech.dData;

public class User {
    private String nomcom;
    private String razsoc;
    private String email;

    // Constructor vacío necesario para Firestore
    public User() {
    }

    public User(String nomcom, String razsoc, String email) {
        this.nomcom = nomcom;
        this.razsoc = razsoc;
        this.email = email;
    }

    public String getNomCom() { return nomcom; }
    public void setNomCom(String nomcom) { this.nomcom = nomcom; }
    public String getRazSoc() { return razsoc; }
    public void setRazSoc(String razsoc) { this.razsoc = razsoc; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

