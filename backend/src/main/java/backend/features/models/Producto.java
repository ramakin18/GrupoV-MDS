package backend.features.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    @NotBlank(message = "Debe ingresar un nombre valido")
    @Size(min = 4, max = 50)
    private String nombreProducto;

    @NotBlank(message = "Debe ingresar una descripcion valida")
    private String descripcion;

    @NotNull(message = "Debe ingresar un precio valido")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "Debe ingresar un stock valido")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a 0")
    private Integer stockDisponible;

    @Builder.Default
    private boolean borrado = false;

    public void markAsDeleted() {
        this.borrado = true;
    }

    public boolean isActive() {
        return !borrado;
    }
}
