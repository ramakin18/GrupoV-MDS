package backend.features.services.impl.domain;

import backend.exceptions.ValidationException;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.mappers.ProductoMapper;
import backend.features.models.Producto;
import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.ProductoViewRole;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.specs.ProductoSpecifications;
import backend.features.services.interfaces.domain.IProductoListService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductoListService implements IProductoListService {

    private final IProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<ProductoResponseDto> execute(
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
}
