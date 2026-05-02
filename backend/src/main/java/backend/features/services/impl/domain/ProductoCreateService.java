package backend.features.services.impl.domain;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.mappers.ProductoMapper;
import backend.features.models.Producto;
import backend.features.repositories.IProductoRepository;
import backend.features.services.interfaces.domain.IProductoCreateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductoCreateService implements IProductoCreateService {

    private final IProductoRepository productoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public ProductoResponseDto execute(ProductoCreateReqDto request) {
        Producto entity = productoMapper.toModel(request);
        Producto saved = productoRepository.save(entity);
        return productoMapper.toResponseDto(saved);
    }
}
