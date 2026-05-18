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
import backend.features.models.ProductoEstadoFiltro;
import backend.features.models.ProductoViewRole;
import backend.features.services.interfaces.domain.IProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@AllArgsConstructor
@Tag(name = "Productos", description = "Operaciones CRUD para productos")
public class ProductoController {

    private final IProductoService productoService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear un producto", description = "Crea un nuevo producto con imagen")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto creado exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o imagen faltante")
    })
    public ResponseEntity<ProductoResponseDto> create(
            @Parameter(description = "Datos del producto en JSON") @RequestPart("producto") @Valid ProductoCreateReqDto request,
            @Parameter(description = "Archivo de imagen del producto") @RequestPart(value = "imagen", required = true) MultipartFile imagen
    ) {
        return ResponseEntity.ok(productoService.create(request, imagen));
    }

    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene todos los productos con filtros opcionales")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos",
            content = @Content(schema = @Schema(implementation = ProductoResponseDto.class)))
    })
    public ResponseEntity<List<ProductoResponseDto>> getAll(
            @Parameter(description = "Filtrar por nombre") @RequestParam(required = false) String nombre,
            @Parameter(description = "Filtrar por precio exacto") @RequestParam(required = false) BigDecimal precio,
            @Parameter(description = "Filtrar por stock exacto") @RequestParam(required = false) Integer stock,
            @Parameter(description = "Stock mínimo") @RequestParam(required = false) Integer stockMin,
            @Parameter(description = "Stock máximo") @RequestParam(required = false) Integer stockMax,
            @Parameter(description = "Filtrar por estado (TODOS, ACTIVO, INACTIVO)") @RequestParam(required = false) String estado,
            @Parameter(description = "Rol de visualización (ADMIN, USUARIO)") @RequestParam(required = false) String rol) {

        ProductoViewRole viewRole = ProductoViewRole.from(rol);
        ProductoEstadoFiltro estadoFiltro = ProductoEstadoFiltro.from(estado);
        Integer stockMinimo = stockMin != null ? stockMin : stock;
        return ResponseEntity.ok(productoService.getAll(nombre, precio, stockMinimo, stockMax, viewRole, estadoFiltro));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado",
            content = @Content(schema = @Schema(implementation = ProductoResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = ProductoResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ProductoResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductoCreateReqDto request) {
        return ResponseEntity.ok(productoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Realiza un borrado lógico del producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok().build();
    }
}