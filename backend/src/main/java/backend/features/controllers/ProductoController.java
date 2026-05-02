package backend.features.controllers;

import backend.configs.BaseResponse;
import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.services.interfaces.domain.IProductoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    private final IProductoService productoService;

    @PostMapping
    public ResponseEntity<BaseResponse<ProductoResponseDto>> create(
            @Valid @RequestBody ProductoCreateReqDto request
    ) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoService.create(request),
                        "Producto creado correctamente"
                )
        );
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<ProductoResponseDto>>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precio,
            @RequestParam(required = false) Integer stock) {

        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoService.getAll(nombre, precio, stock),
                        "Productos listados correctamente"
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductoResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoService.getById(id),
                        "Producto encontrado"
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductoResponseDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductoCreateReqDto request) {
        return ResponseEntity.ok(
                BaseResponse.ok(
                        productoService.update(id, request),
                        "Producto actualizado correctamente"
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok(BaseResponse.ok(null, "Producto eliminado correctamente"));
    }
}
