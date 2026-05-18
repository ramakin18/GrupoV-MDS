package backend.features.controllers;

import backend.features.dtos.request.KitCreateRequest;
import backend.features.dtos.response.KitResponseDto;
import backend.features.services.interfaces.domain.IKitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KitController.class)
class KitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IKitService kitService;

    private final KitResponseDto dto = new KitResponseDto(
        1L, "Kit Test", "Desc", BigDecimal.valueOf(500), 3, true, List.of()
    );

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(kitService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/kits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Kit Test"));
    }

    @Test
    void getAll_activos_shouldReturnActiveOnly() throws Exception {
        when(kitService.getActivos()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/kits?activos=true"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value("Kit Test"));
    }

    @Test
    void getById_shouldReturnKit() throws Exception {
        when(kitService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/kits/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Kit Test"));
    }

    @Test
    void create_shouldReturn201() throws Exception {
        when(kitService.create(any())).thenReturn(dto);

        mockMvc.perform(post("/api/kits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new KitCreateRequest("Kit Test", "Desc", BigDecimal.valueOf(500), true,
                        List.of(new KitCreateRequest.KitProductoItem(1L, 2)))
                )))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value("Kit Test"));
    }

    @Test
    void update_shouldReturnUpdated() throws Exception {
        when(kitService.update(any(), any())).thenReturn(dto);

        mockMvc.perform(put("/api/kits/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new KitCreateRequest("Kit Test", "Desc", BigDecimal.valueOf(600), true,
                        List.of(new KitCreateRequest.KitProductoItem(1L, 2)))
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Kit Test"));
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/kits/1"))
            .andExpect(status().isOk());
    }
}
