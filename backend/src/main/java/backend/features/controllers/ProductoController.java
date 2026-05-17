package backend.features.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.models.ProductoViewRole;
import backend.features.services.interfaces.domain.IProductoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@AllArgsConstructor
public class ProductoController {

    private final IProductoService productoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDto> create(
            @RequestPart("producto") @Valid ProductoCreateReqDto request,
            @RequestPart(value = "imagen", required = true) MultipartFile imagen
    ) {
        return ResponseEntity.ok(productoService.create(request, imagen));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> getAll(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precio,
            @RequestParam(required = false) Integer stock,
            @RequestParam(required = false) String rol) {

        ProductoViewRole viewRole = ProductoViewRole.from(rol);
        return ResponseEntity.ok(productoService.getAll(nombre, precio, stock, viewRole));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductoCreateReqDto request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok().build();
    }
}