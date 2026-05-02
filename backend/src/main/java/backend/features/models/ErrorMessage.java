package backend.features.models;

public class ErrorMessage {
    private String mensaje;

    // Este es el constructor que necesita el Handler
    public ErrorMessage(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
