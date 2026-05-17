package backend.features.services.impl.domain;

import backend.exceptions.DuplicateResourceException;
import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.KitProductoCreateReqDto;
import backend.features.dtos.response.KitProductoDetalleResponseDto;
import backend.features.dtos.response.KitProductoResponseDto;
import backend.features.models.EstadoKit;
import backend.features.models.KitProducto;
import backend.features.models.KitProductoDetalle;
import backend.features.models.Producto;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.KitProductoRepository;
import backend.features.services.interfaces.domain.IKitProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class KitProductoServiceImpl implements IKitProductoService {

    private final KitProductoRepository kitProductoRepository;
    private final IProductoRepository productoRepository;

    @Override
    public KitProductoResponseDto create(KitProductoCreateReqDto request) {
        if (kitProductoRepository.existsByNombre(request.nombre())) {
            throw new DuplicateResourceException("Ya existe un kit con ese nombre");
        }

        boolean tieneMasDeUnProducto = request.productos().size() > 1;

        boolean tieneUnProductoConCantidadMayorAUno = request.productos().stream()
                .anyMatch(detalle -> detalle.cantidad() > 1);

        if (!tieneMasDeUnProducto && !tieneUnProductoConCantidadMayorAUno) {
            throw new ValidationException("El kit debe tener mas de un producto o un producto con cantidad mayor a 1");
        }

        List<KitProductoDetalle> detalles = request.productos().stream()
                .map(detalleRequest -> {
                    Producto producto = productoRepository.findById(detalleRequest.idProducto())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Producto no encontrado con id: " + detalleRequest.idProducto()
                            ));

                    if (request.estado() == EstadoKit.ACTIVO && !producto.isActive()) {
                        throw new ValidationException("No se puede crear un kit activo con productos desactivados");
                    }

                    return KitProductoDetalle.builder()
                            .producto(producto)
                            .cantidad(detalleRequest.cantidad())
                            .build();
                })
                .toList();

        int stockKit = detalles.stream()
                .mapToInt(detalle -> detalle.getProducto().getStockDisponible() / detalle.getCantidad())
                .min()
                .orElse(0);

        KitProducto kit = KitProducto.builder()
                .nombre(request.nombre())
                .descripcion(request.descripcion())
                .precio(request.precio())
                .estado(request.estado())
                .stock(stockKit)
                .productos(detalles)
                .build();

        detalles.forEach(detalle -> detalle.setKitProducto(kit));

        KitProducto kitGuardado = kitProductoRepository.save(kit);

        return new KitProductoResponseDto(
                kitGuardado.getIdKit(),
                kitGuardado.getNombre(),
                kitGuardado.getDescripcion(),
                kitGuardado.getPrecio(),
                kitGuardado.getStock(),
                kitGuardado.getEstado(),
                kitGuardado.getProductos().stream()
                        .map(detalle -> new KitProductoDetalleResponseDto(
                                detalle.getProducto().getIdProducto(),
                                detalle.getProducto().getNombreProducto(),
                                detalle.getCantidad()
                        ))
                        .toList()
        );
    }
}