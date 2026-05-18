package backend.features.controllers;

import backend.features.dtos.request.ProductoCreateReqDto;
import backend.features.dtos.response.ProductoResponseDto;
import backend.features.services.interfaces.domain.IProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IProductoService productoService;

    private final ProductoResponseDto dto = new ProductoResponseDto(
        1L, "Test", "Desc", BigDecimal.valueOf(100), 10, 5, false, null
    );

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(productoService.getAll(any(), any(), any(), any(), any(), any()))
            .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/productos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombreProducto").value("Test"));
    }

    @Test
    void getById_shouldReturnProducto() throws Exception {
        when(productoService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/productos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreProducto").value("Test"));
    }

    @Test
    void create_shouldReturnProducto() throws Exception {
        when(productoService.create(any(), any())).thenReturn(dto);

        MockMultipartFile producto = new MockMultipartFile(
            "producto", "", "application/json",
            objectMapper.writeValueAsBytes(new ProductoCreateReqDto("Test", "Desc", BigDecimal.valueOf(100), 10, 5, null))
        );
        MockMultipartFile imagen = new MockMultipartFile("imagen", "test.jpg", "image/jpeg", "image-data".getBytes());

        mockMvc.perform(multipart("/api/productos")
                .file(producto)
                .file(imagen))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreProducto").value("Test"));
    }

    @Test
    void update_shouldReturnUpdatedProducto() throws Exception {
        when(productoService.update(any(), any())).thenReturn(dto);

        mockMvc.perform(put("/api/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    new ProductoCreateReqDto("Test", "Desc", BigDecimal.valueOf(100), 10, 5, null)
                )))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreProducto").value("Test"));
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
            .andExpect(status().isOk());
    }
}
