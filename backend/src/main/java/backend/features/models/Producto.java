package backend.features.models;

import java.math.BigDecimal;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Digits(integer = 10, fraction = 2, message = "El precio no puede tener mas de 2 decimales") 
    private BigDecimal precio;

    @NotNull(message = "Debe ingresar un stock valido")
    @PositiveOrZero(message = "El stock debe ser mayor o igual a 0")
    private Integer stockDisponible;

    @NotNull(message = "Debe ingresar un stock mínimo valido")
    @PositiveOrZero(message = "El stock mínimo debe ser mayor o igual a 0")
    @ColumnDefault("0")
    @Builder.Default
    private Integer stockMinimo = 0;

    private String imagenUrl;

    @Builder.Default
    private boolean borrado = false;

    // A partir de aqui añadimos campos de reseña
    @ColumnDefault("0.0")
    @Builder.Default
    private Double promedioPuntuacion = 0.0;

    @ColumnDefault("0")
    @Builder.Default
    private Integer cantidadResenas = 0;

    public void markAsDeleted() {
        this.borrado = true;
    }

    public void setBorrado(boolean estado) {
        this.borrado = estado;
    }

    public boolean isActive() {
        return !borrado;
    }
}