package backend.features.models;

import backend.exceptions.ValidationException;

public enum ProductoViewRole {
    ADMIN,
    USUARIO;

    public static ProductoViewRole from(String value) {
        if (value == null || value.isBlank()) {
            return USUARIO;
        }

        for (ProductoViewRole role : values()) {
            if (role.name().equalsIgnoreCase(value.trim())) {
                return role;
            }
        }

        throw new ValidationException("Rol de visualizacion invalido. Use ADMIN o USUARIO.");
    }

    public boolean canViewDeletedProducts() {
        return this == ADMIN;
    }
}
