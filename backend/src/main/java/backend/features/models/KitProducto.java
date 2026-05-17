package backend.features.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "kit_producto")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class KitProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idKit;

    @NotBlank
    @Size(min = 4, max = 50)
    private String nombre;

    @NotBlank
    private String descripcion;

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    private BigDecimal precio;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoKit estado;

    private Integer stock;

    @OneToMany(mappedBy = "kitProducto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<KitProductoDetalle> productos;
}