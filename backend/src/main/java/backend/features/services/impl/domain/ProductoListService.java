package backend.features.services.impl.domain;

import backend.features.dtos.response.ProductoResponseDto;
import backend.features.mappers.ProductoMapper;
import backend.features.models.Producto;
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

    @Override
    public List<ProductoResponseDto> execute(String nombre, BigDecimal precio, Integer stock) {
        // Usamos la especificación con los parámetros recibidos
        List<Producto> productos = productoRepository.findAll(
                ProductoSpecifications.filtrarProductos(nombre, precio, stock)
        );

        // Convertimos a DTO usando tu mapper
        return ProductoMapper.toResponseDtoList(productos);
    }
}
