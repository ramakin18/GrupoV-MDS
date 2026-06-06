package backend.features.models;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "resenas") 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private Integer puntuacion;

    @Size (max = 500, message = "La descripción no debe tener mas de 500 caracteres")
    private String descripcion;

    @NotNull
    private LocalDateTime fechaCreacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Cliente usuario;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "kit_id")
    private Kit kit;

    @Builder.Default
    @jakarta.persistence.Column(columnDefinition = "boolean default false")
    private Boolean eliminado = false;

}
