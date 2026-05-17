package backend.features.models;

import backend.exceptions.ValidationException;

public enum ProductoEstadoFiltro {
    TODOS,
    ACTIVO,
    INACTIVO;

    public static ProductoEstadoFiltro from(String value) {
        if (value == null || value.isBlank()) {
            return TODOS;
        }

        for (ProductoEstadoFiltro estado : values()) {
            if (estado.name().equalsIgnoreCase(value.trim())) {
                return estado;
            }
        }

        throw new ValidationException("Estado de producto invalido. Use TODOS, ACTIVO o INACTIVO.");
    }
}
