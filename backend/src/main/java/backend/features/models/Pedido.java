package backend.features.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @NotNull
    private LocalDateTime fecha;

    private LocalDateTime fechaActualizacion;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SituacionPedido situacion;

    @NotNull
    @PositiveOrZero
    private BigDecimal total;

    @PositiveOrZero
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @PositiveOrZero
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "cupon_id")
    private Cupon cupon;

    @NotNull
    @Builder.Default
    private String formaPago = "EFECTIVO";

    private String motivoCancelacion;

    private String paisEnvio;
    private String provinciaEnvio;
    private String localidadEnvio;
    private String calleEnvio;
    private String numeroEnvio;
    private String pisoEnvio;
    private String departamentoEnvio;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoDetalle> detalles;
}
