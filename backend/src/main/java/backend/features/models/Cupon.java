package backend.features.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cupones", uniqueConstraints = {
    @UniqueConstraint(name = "uk_cupon_codigo", columnNames = "codigo")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCupon;

    @Column(nullable = false, unique = true, length = 12)
    private String codigo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoDescuento tipoDescuento;

    @NotNull
    @Positive
    @Column(precision = 12, scale = 2)
    private BigDecimal valor;

    @NotNull
    private LocalDate fechaDesde;

    @NotNull
    private LocalDate fechaHasta;

    @NotNull
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "cupon", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CuponCliente> destinatarios = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "cupon_productos",
        joinColumns = @JoinColumn(name = "cupon_id"),
        inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    @Builder.Default
    private List<Producto> productos = new ArrayList<>();
}
