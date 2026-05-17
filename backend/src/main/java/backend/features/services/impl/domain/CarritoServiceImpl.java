package backend.features.services.impl.domain;

import backend.exceptions.ValidationException;
import backend.features.dtos.request.CarritoItemRequestDto;
import backend.features.dtos.request.CarritoValidateRequestDto;
import backend.features.dtos.response.CarritoItemResponseDto;
import backend.features.dtos.response.CarritoResponseDto;
import backend.features.models.Producto;
import backend.features.repositories.IProductoRepository;
import backend.features.services.interfaces.domain.ICarritoService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CarritoServiceImpl implements ICarritoService {

    private final IProductoRepository productoRepository;

    @Override
    public CarritoResponseDto validar(CarritoValidateRequestDto request) {
        List<CarritoItemResponseDto> responseItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CarritoItemRequestDto item : request.getItems()) {
            Producto producto = productoRepository.findByIdProductoAndBorradoFalse(item.getIdProducto())
                    .orElseThrow(() -> new ValidationException(
                            "El producto con ID " + item.getIdProducto() + " no existe o fue desactivado"
                    ));

            if (item.getCantidad() > producto.getStockDisponible()) {
                throw new ValidationException(
                        "No hay stock suficiente para el producto " + producto.getNombreProducto()
                );
            }

            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            responseItems.add(new CarritoItemResponseDto(
                    producto.getIdProducto(),
                    producto.getNombreProducto(),
                    producto.getPrecio(),
                    item.getCantidad(),
                    subtotal
            ));
        }

        return new CarritoResponseDto(responseItems, total);
    }
}
