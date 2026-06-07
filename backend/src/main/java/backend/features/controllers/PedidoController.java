package backend.features.controllers;

import backend.features.dtos.request.PedidoCancelRequest;
import backend.features.dtos.request.PedidoCreateRequest;
import backend.features.dtos.request.PedidoSituacionUpdateRequestDto;
import backend.features.dtos.response.PedidoResponseDTO;
import backend.features.services.interfaces.domain.IPedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@AllArgsConstructor
@Tag(name = "Pedidos", description = "Operaciones CRUD para pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;

    @GetMapping
    @Operation(summary = "Listar pedidos", description = "Obtiene todos los pedidos, opcionalmente filtrados por estado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    })
    public ResponseEntity<List<PedidoResponseDTO>> getAll(
            @Parameter(description = "Filtrar por estado (RESERVADO, PENDIENTE, LISTO, RETIRADO, ENTREGADO, CANCELADO)")
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(pedidoService.getAll(estado));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener pedidos por cliente",
        description = "Obtiene todos los pedidos de un cliente específico, ordenados por fecha descendente")
    public ResponseEntity<List<PedidoResponseDTO>> getByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(pedidoService.getByClienteId(clienteId));
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Pedidos pendientes de entrega", description = "Obtiene los pedidos pendientes de entrega")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos pendientes",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class)))
    })
    public ResponseEntity<List<PedidoResponseDTO>> getPendingDelivery() {
        return ResponseEntity.ok(pedidoService.getPendingDelivery());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido por ID", description = "Obtiene un pedido específico por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<PedidoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Crear pedido", description = "Crea un nuevo pedido con los items especificados")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o stock insuficiente")
    })
    public ResponseEntity<PedidoResponseDTO> create(@Valid @RequestBody PedidoCreateRequest request) {
        PedidoResponseDTO response = pedidoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/situacion")
    @Operation(summary = "Actualizar situación del pedido", description = "Actualiza el estado de un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Situación actualizada",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "400", description = "Transición de estado inválida")
    })
    public ResponseEntity<PedidoResponseDTO> updateSituacion(
            @PathVariable Long id,
            @Valid @RequestBody PedidoSituacionUpdateRequestDto request) {
        return ResponseEntity.ok(pedidoService.updateSituacion(id, request.situacion()));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar pedido", description = "Cancela un pedido con un motivo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido cancelado",
            content = @Content(schema = @Schema(implementation = PedidoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "400", description = "El pedido no puede ser cancelado")
    })
    public ResponseEntity<PedidoResponseDTO> cancelar(
            @PathVariable Long id,
            @Valid @RequestBody PedidoCancelRequest request) {
        return ResponseEntity.ok(pedidoService.cancelar(id, request.motivo()));
    }
}
