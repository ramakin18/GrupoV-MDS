package backend.features.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "kit_producto_detalle")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class KitProductoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kit_id")
    private KitProducto kitProducto;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @NotNull
    @Positive
    private Integer cantidad;
}