package backend.features.services.interfaces.domain;

import backend.features.dtos.request.CuponAplicacionRequest;
import backend.features.dtos.request.CuponCreateRequest;
import backend.features.dtos.request.PedidoItemRequest;
import backend.features.dtos.response.CuponAplicacionResponseDto;
import backend.features.dtos.response.CuponResponseDto;
import backend.features.models.Cupon;
import backend.features.models.CuponCliente;

import java.util.List;

public interface ICuponService {
    List<CuponResponseDto> getAll();
    CuponResponseDto create(CuponCreateRequest request);
    CuponAplicacionResponseDto aplicar(CuponAplicacionRequest request);
    CuponCalculation validateForOrder(Long clienteId, String codigo, List<PedidoItemRequest> items);
    void markAsUsed(CuponCliente asignacion, backend.features.models.Pedido pedido);

    record CuponCalculation(
        Cupon cupon,
        CuponCliente asignacion,
        java.math.BigDecimal subtotal,
        java.math.BigDecimal descuento,
        java.math.BigDecimal totalConDescuento
    ) {}
}
