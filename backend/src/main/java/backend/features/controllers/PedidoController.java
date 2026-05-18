package backend.features.controllers;

import backend.features.dtos.request.PedidoCancelRequest;
import backend.features.dtos.request.PedidoCreateRequest;
import backend.features.dtos.request.PedidoSituacionUpdateRequestDto;
import backend.features.dtos.response.PedidoResponseDTO;
import backend.features.models.SituacionPedido;
import backend.features.services.interfaces.domain.IPedidoService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@AllArgsConstructor
public class PedidoController {

    private final IPedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> getAll(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(pedidoService.getAll(estado));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<PedidoResponseDTO>> getPendingDelivery() {
        return ResponseEntity.ok(pedidoService.getPendingDelivery());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getById(id));
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> create(@Valid @RequestBody PedidoCreateRequest request) {
        PedidoResponseDTO response = pedidoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/situacion")
    public ResponseEntity<PedidoResponseDTO> updateSituacion(
            @PathVariable Long id,
            @Valid @RequestBody PedidoSituacionUpdateRequestDto request) {
        return ResponseEntity.ok(pedidoService.updateSituacion(id, request.situacion()));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDTO> cancelar(
            @PathVariable Long id,
            @Valid @RequestBody PedidoCancelRequest request) {
        return ResponseEntity.ok(pedidoService.cancelar(id, request.motivo()));
    }
}
