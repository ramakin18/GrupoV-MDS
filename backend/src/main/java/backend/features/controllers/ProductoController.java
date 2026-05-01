package backend.features.controllers;

import backend.configs.BaseResponse;
import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.services.interfaces.domain.IProductoCreateService;
import backend.features.services.interfaces.domain.IProductoListService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@AllArgsConstructor
public class ProductoController {

    private final IProductoCreateService productoCreateService;
    private final IProductoListService productoListService;

    @PostMapping
    public ResponseEntity<BaseResponse<ProductoResponseDto>> createProducto(
            @Valid @RequestBody ProductoCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoCreateService.execute(request),
                        "Clase creada correctamente"
                )
        );

    }
    @GetMapping
    public ResponseEntity<BaseResponse<List<ProductoResponseDto>>> listProducto(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precio,
            @RequestParam(required = false) Integer stock) {

        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoListService.execute(nombre, precio, stock),
                        "Productos listados correctamente"
                )
        );
    }
}
