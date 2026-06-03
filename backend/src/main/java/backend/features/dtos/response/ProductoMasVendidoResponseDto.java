package backend.features.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductoMasVendidoResponseDto {
    private String nombreProducto;
    private Long cantidadVendida;
}
