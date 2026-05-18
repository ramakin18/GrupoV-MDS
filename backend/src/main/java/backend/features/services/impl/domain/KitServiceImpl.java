package backend.features.services.impl.domain;

import backend.exceptions.DuplicateResourceException;
import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.KitCreateRequest;
import backend.features.dtos.response.KitResponseDto;
import backend.features.mappers.KitMapper;
import backend.features.models.Kit;
import backend.features.models.KitProducto;
import backend.features.models.Producto;
import backend.features.repositories.IKitRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.services.interfaces.domain.IKitService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class KitServiceImpl implements IKitService {

    private final IKitRepository kitRepository;
    private final IProductoRepository productoRepository;
    private final KitMapper kitMapper;

    @Override
    @Transactional
    public KitResponseDto create(KitCreateRequest request) {
        if (kitRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new DuplicateResourceException("Ya existe un kit con ese nombre.");
        }

        boolean activo = request.isActivo();
        List<KitProducto> kitProductos = buildKitProductos(request, activo);

        int totalProductos = kitProductos.stream().mapToInt(kp -> kp.getCantidad()).sum();
        if (kitProductos.size() == 1 && kitProductos.get(0).getCantidad() <= 1 && totalProductos <= 1) {
            throw new ValidationException(
                "El kit debe tener mas de un producto o un producto con cantidad mayor a 1.");
        }

        int stock = calcularStock(kitProductos);

        Kit kit = Kit.builder()
            .nombre(request.nombre())
            .descripcion(request.descripcion())
            .precio(request.precio())
            .stock(stock)
            .activo(activo)
            .productos(kitProductos)
            .build();

        kitProductos.forEach(kp -> kp.setKit(kit));

        Kit saved = kitRepository.save(kit);
        return kitMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitResponseDto> getAll() {
        return kitMapper.toResponseDtoList(kitRepository.findAllByOrderByNombreAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitResponseDto> getActivos() {
        return kitMapper.toResponseDtoList(kitRepository.findByActivoTrueOrderByNombreAsc());
    }

    @Override
    @Transactional(readOnly = true)
    public KitResponseDto getById(Long id) {
        Kit kit = kitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kit no encontrado con id: " + id));
        return kitMapper.toResponseDto(kit);
    }

    @Override
    @Transactional
    public KitResponseDto update(Long id, KitCreateRequest request) {
        Kit kit = kitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kit no encontrado con id: " + id));

        if (kitRepository.existsByNombreIgnoreCaseAndIdKitNot(request.nombre(), id)) {
            throw new DuplicateResourceException("El nombre ya está en uso por otro kit.");
        }

        boolean activo = request.isActivo();
        List<KitProducto> kitProductos = buildKitProductos(request, activo);

        int totalProductos = kitProductos.stream().mapToInt(kp -> kp.getCantidad()).sum();
        if (kitProductos.size() == 1 && kitProductos.get(0).getCantidad() <= 1 && totalProductos <= 1) {
            throw new ValidationException(
                "El kit debe tener mas de un producto o un producto con cantidad mayor a 1.");
        }

        int stock = calcularStock(kitProductos);

        kit.setNombre(request.nombre());
        kit.setDescripcion(request.descripcion());
        kit.setPrecio(request.precio());
        kit.setStock(stock);
        kit.setActivo(activo);

        kit.getProductos().clear();
        kitProductos.forEach(kp -> {
            kp.setKit(kit);
            kit.getProductos().add(kp);
        });

        Kit saved = kitRepository.save(kit);
        return kitMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Kit kit = kitRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Kit no encontrado con id: " + id));
        kit.setActivo(false);
        kitRepository.save(kit);
    }

    private List<KitProducto> buildKitProductos(KitCreateRequest request, boolean kitActivo) {
        List<KitCreateRequest.KitProductoItem> items = request.productos();
        if (items == null || items.isEmpty()) {
            throw new ValidationException("El kit debe tener al menos un producto.");
        }

        List<KitProducto> kitProductos = new ArrayList<>();
        for (KitCreateRequest.KitProductoItem item : items) {
            Producto producto = productoRepository.findById(item.idProducto())
                .orElseThrow(() -> new ValidationException(
                    "El producto con ID " + item.idProducto() + " no existe."));

            if (producto.isBorrado() && kitActivo) {
                throw new ValidationException(
                    "No se puede crear un kit activo con el producto '" + producto.getNombreProducto()
                    + "' porque esta inactivo.");
            }

            KitProducto kp = KitProducto.builder()
                .producto(producto)
                .cantidad(item.cantidad())
                .build();
            kitProductos.add(kp);
        }
        return kitProductos;
    }

    private int calcularStock(List<KitProducto> productos) {
        return productos.stream()
            .mapToInt(kp -> {
                Producto p = kp.getProducto();
                if (p.isBorrado() || p.getStockDisponible() == null || p.getStockDisponible() <= 0) {
                    return 0;
                }
                return p.getStockDisponible() / kp.getCantidad();
            })
            .min()
            .orElse(0);
    }
}
