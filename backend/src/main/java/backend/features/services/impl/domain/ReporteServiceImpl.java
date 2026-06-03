package backend.features.services.impl.domain;

import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.repositories.PedidoDetalleRepository;
import backend.features.services.interfaces.domain.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements IReporteService {

    private final PedidoDetalleRepository pedidoDetalleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoMasVendidoResponseDto> getProductosMasVendidos(Integer mes, Integer anio) {
        return pedidoDetalleRepository.findProductosMasVendidos(mes, anio);
    }
}
