package backend.features.controllers;

import backend.features.dtos.request.CarritoItemRequestDto;
import backend.features.dtos.request.CarritoValidateRequestDto;
import backend.features.dtos.response.CarritoResponseDto;
import backend.features.services.interfaces.domain.ICarritoService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarritoController.class)
class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICarritoService carritoService;

    @Test
    void validar_shouldReturnResponse() throws Exception {
        CarritoResponseDto response = new CarritoResponseDto(List.of(), BigDecimal.valueOf(500));
        when(carritoService.validar(any())).thenReturn(response);

        mockMvc.perform(post("/api/carrito/validar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new CarritoValidateRequestDto(List.of(new CarritoItemRequestDto(1L, 2)))
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(500));
    }

    @Test
    void validar_conItemsInvalidos_shouldReturnTotalCero() throws Exception {
        CarritoResponseDto response = new CarritoResponseDto(List.of(), BigDecimal.ZERO);
        when(carritoService.validar(any())).thenReturn(response);

        mockMvc.perform(post("/api/carrito/validar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new CarritoValidateRequestDto(List.of(new CarritoItemRequestDto(1L, 2)))
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.total").value(0));
    }
}
