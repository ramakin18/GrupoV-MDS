package backend.features.models;

public class LoginRequest {
    private String email;
    private String contraseña;

    // Constructor vacío (necesario para Spring)
    public LoginRequest() {
    }

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
}