package es.lamc.appnegytech.dData;

public class FormResult {
    private String nomCom;
    private String servicioContratado;
    private String userId;
    private String documentId;
    private String collectionName;

    public FormResult(String nomCom, String servicioContratado, String userId, String documentId, String collectionName) {
        this.nomCom = nomCom;
        this.servicioContratado = servicioContratado;
        this.userId = userId;
        this.documentId = documentId;
        this.collectionName = collectionName;
    }

    public String getNomCom() { return nomCom; }

    public String getServicioContratado() { return servicioContratado; }

    public String getUserId() { return userId; }

    public String getDocumentId() { return documentId; }

    public String getCollectionName() { return collectionName; }

    @Override
    public String toString() {
        return "FormResult{" +
                "nomCom='" + nomCom + '\'' +
                ", servicioContratado='" + servicioContratado + '\'' +
                ", userId='" + userId + '\'' +
                ", documentId='" + documentId + '\'' +
                ", collectionName='" + collectionName + '\'' +
                '}';
    }
}

