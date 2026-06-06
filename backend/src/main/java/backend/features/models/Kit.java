package backend.features.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "kits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Kit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKit;

    @NotBlank(message = "Debe ingresar un nombre valido")
    @Size(min = 3, max = 50)
    @Column(unique = true)
    private String nombre;

    @NotBlank(message = "Debe ingresar una descripcion valida")
    private String descripcion;

    @NotNull(message = "Debe ingresar un precio valido")
    @Positive(message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio no puede tener mas de 2 decimales")
    private BigDecimal precio;

    @PositiveOrZero
    private Integer stock;

    @Builder.Default
    private boolean activo = true;

    @OneToMany(mappedBy = "kit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KitProducto> productos;

    @Builder.Default
    private Double promedioPuntuacion = 0.0;

    @Builder.Default
    private Integer cantidadResenas = 0;
}
