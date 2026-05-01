package backend.features.services.impl.domain;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.mappers.ProductoMapper;
import backend.features.repositories.IProductoRepository;
import backend.features.services.interfaces.domain.IProductoCreateService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductoCreateService implements IProductoCreateService {

    private final IProductoRepository productoRepository;

    @Override
    public ProductoResponseDto execute(ProductoCreateReqDto request){
        return ProductoMapper.toResponseDto(productoRepository.save(ProductoMapper.toModel(request)));
    }

}
