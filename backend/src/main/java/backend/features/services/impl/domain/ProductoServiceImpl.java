package backend.features.services.impl.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import backend.exceptions.DuplicateResourceException;
import backend.exceptions.ValidationException;
import backend.exceptions.ResourceNotFoundException;
import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.mappers.ProductoMapper;
import backend.features.models.Producto;
import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.ProductoViewRole;
import backend.features.repositories.IKitRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.specs.ProductoSpecifications;
import backend.features.services.interfaces.domain.IProductoService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements IProductoService {

    private final IProductoRepository productoRepository;
    private final ProductoMapper productoMapper;
    private final CloudinaryServiceImpl cloudinaryService;
    private final IKitRepository kitRepository;

    @Override
    public ProductoResponseDto create(ProductoCreateReqDto request, MultipartFile imagen) {
        if (productoRepository.existsByNombreProductoIgnoreCase(request.nombreProducto())) {
            throw new DuplicateResourceException("Ya existe un producto con ese nombre.");
        }

        Producto entity = productoMapper.toModel(request);

        try {
            if (imagen != null && !imagen.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(imagen);
                entity.setImagenUrl(imageUrl);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al subir la imagen a Cloudinary");
        }

        Producto saved = productoRepository.save(entity);
        return productoMapper.toResponseDto(saved);
    }

    @Override
    public List<ProductoResponseDto> getAll(
            String nombre,
            BigDecimal precio,
            Integer stockMin,
            Integer stockMax,
            ProductoViewRole role,
            ProductoEstadoFiltro estado) {
        validateStockRange(stockMin, stockMax);
        ProductoEstadoFiltro effectiveEstado = role.canViewDeletedProducts() ? estado : ProductoEstadoFiltro.ACTIVO;

        List<Producto> productos = productoRepository.findAll(
                ProductoSpecifications.filtrarProductos(nombre, precio, stockMin, stockMax, effectiveEstado)
        );
        return productoMapper.toResponseDtoList(productos);
    }

    private void validateStockRange(Integer stockMin, Integer stockMax) {
        if (stockMin != null && stockMin < 0) {
            throw new ValidationException("El stock minimo no puede ser negativo.");
        }
        if (stockMax != null && stockMax < 0) {
            throw new ValidationException("El stock maximo no puede ser negativo.");
        }
        if (stockMin != null && stockMax != null && stockMin > stockMax) {
            throw new ValidationException("El stock minimo no puede ser mayor al stock maximo.");
        }
    }

    @Override
    public ProductoResponseDto getById(Long id) {
        Producto producto = productoRepository.findByIdProductoAndBorradoFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return productoMapper.toResponseDto(producto);
    }

    @Override
    public ProductoResponseDto update(Long id, ProductoCreateReqDto request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        if (productoRepository.existsByNombreProductoIgnoreCaseAndIdProductoNot(request.nombreProducto(), id)) {
            throw new DuplicateResourceException("El nombre ya está en uso por otro producto.");
        }

        producto.setNombreProducto(request.nombreProducto());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStockDisponible(request.stockDisponible());
        producto.setStockMinimo(request.stockMinimo());
        
        if (request.borrado() != null) {
            if (request.borrado() && kitRepository.existsByProductos_Producto_IdProductoAndActivoTrue(id)) {
                throw new ValidationException(
                    "No se puede desactivar el producto porque pertenece a un kit activo.");
            }
            producto.setBorrado(request.borrado());
        }

        Producto updated = productoRepository.save(producto);
        return productoMapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {
        Producto producto = productoRepository.findByIdProductoAndBorradoFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        producto.markAsDeleted();
        productoRepository.save(producto);
    }
}