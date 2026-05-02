package backend.features.services.impl.domain;

import backend.exceptions.ResourceNotFoundException;
import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.mappers.ProductoMapper;
import backend.features.models.Producto;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.specs.ProductoSpecifications;
import backend.features.services.interfaces.domain.IProductoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductoServiceImpl implements IProductoService {

    private final IProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public ProductoResponseDto create(ProductoCreateReqDto request) {
        Producto entity = productoMapper.toModel(request);
        Producto saved = productoRepository.save(entity);
        return productoMapper.toResponseDto(saved);
    }

    @Override
    public List<ProductoResponseDto> getAll(String nombre, BigDecimal precio, Integer stock) {
        List<Producto> productos = productoRepository.findAll(
                ProductoSpecifications.filtrarProductos(nombre, precio, stock)
        );
        return productoMapper.toResponseDtoList(productos);
    }

    @Override
    public ProductoResponseDto getById(Long id) {
        Producto producto = productoRepository.findByIdProductoAndBorradoFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return productoMapper.toResponseDto(producto);
    }

    @Override
    public ProductoResponseDto update(Long id, ProductoCreateReqDto request) {
        Producto producto = productoRepository.findByIdProductoAndBorradoFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        producto.setNombreProducto(request.nombreProducto());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStockDisponible(request.stockDisponible());

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
