package backend.features.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_detalle")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PedidoDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotNull
    @PositiveOrZero
    private BigDecimal precioUnitario;

    @NotNull
    @PositiveOrZero
    private BigDecimal subtotal;
}
