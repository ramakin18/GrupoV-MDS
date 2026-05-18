package backend.features.controllers;

import backend.features.dtos.DomicilioEnvioDto;
import backend.features.dtos.request.PedidoCancelRequest;
import backend.features.dtos.request.PedidoCreateRequest;
import backend.features.dtos.request.PedidoItemRequest;
import backend.features.dtos.request.PedidoSituacionUpdateRequestDto;
import backend.features.dtos.response.PedidoResponseDTO;
import backend.features.models.SituacionPedido;
import backend.features.services.interfaces.domain.IPedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PedidoController.class)
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IPedidoService pedidoService;

    private final PedidoResponseDTO dto = new PedidoResponseDTO(
        1L, 1L, "Juan", "Perez", "j@t.com",
        LocalDateTime.of(2024, 1, 1, 10, 0), null,
        SituacionPedido.RESERVADO, null, "EFECTIVO",
        BigDecimal.valueOf(500),
        new DomicilioEnvioDto("Argentina", "BA", "CABA", "Calle", "123", null, null),
        List.of()
    );

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(pedidoService.getAll(any())).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pedidos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombreCliente").value("Juan"))
            .andExpect(jsonPath("$[0].situacion").value("RESERVADO"));
    }

    @Test
    void getPendingDelivery_shouldReturnList() throws Exception {
        when(pedidoService.getPendingDelivery()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/pedidos/pendientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].situacion").value("RESERVADO"));
    }

    @Test
    void getById_shouldReturnPedido() throws Exception {
        when(pedidoService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/pedidos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreCliente").value("Juan"));
    }

    @Test
    void create_shouldReturn201() throws Exception {
        when(pedidoService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/pedidos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PedidoCreateRequest(1L, List.of(new PedidoItemRequest(1L, 2)), null)
                )))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.idPedido").value(1));
    }

    @Test
    void updateSituacion_shouldReturnUpdated() throws Exception {
        when(pedidoService.updateSituacion(any(), any())).thenReturn(dto);

        mockMvc.perform(put("/api/pedidos/1/situacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PedidoSituacionUpdateRequestDto(SituacionPedido.ENTREGADO)
                )))
            .andExpect(status().isOk());
    }

    @Test
    void cancelar_shouldReturnCancelled() throws Exception {
        when(pedidoService.cancelar(any(), any())).thenReturn(dto);

        mockMvc.perform(put("/api/pedidos/1/cancelar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new PedidoCancelRequest("Cliente cancelo")
                )))
            .andExpect(status().isOk());
    }
}
