package backend.features.controllers;

import backend.features.dtos.DomicilioEnvioDto;
import backend.features.dtos.request.ClienteCreateRequestDto;
import backend.features.dtos.request.ClienteLoginRequestDto;
import backend.features.dtos.request.DomicilioUpdateRequest;
import backend.features.dtos.response.ClienteResponseDto;
import backend.features.services.interfaces.domain.IClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IClienteService clienteService;

    private final ClienteResponseDto dto = new ClienteResponseDto(
        1L, "Juan", "Perez", "juan@test.com",
        new DomicilioEnvioDto("Argentina", "BA", "CABA", "Calle", "123", "2", "A"),
        "ADMIN"
    );

    @Test
    void registrar_shouldReturnCliente() throws Exception {
        when(clienteService.register(any())).thenReturn(dto);

        ClienteCreateRequestDto request = ClienteCreateRequestDto.builder()
            .nombre("Juan").apellido("Perez").email("juan@test.com")
            .contrasena("12345678")
            .domicilio(new DomicilioEnvioDto("Argentina", "BA", "CABA", "Calle", "123", null, null))
            .rol("CLIENTE").build();

        mockMvc.perform(post("/api/clientes/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void login_shouldReturnCliente() throws Exception {
        when(clienteService.login(any())).thenReturn(dto);

        mockMvc.perform(post("/api/clientes/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ClienteLoginRequestDto("juan@test.com", "12345678"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        when(clienteService.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/clientes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].email").value("juan@test.com"));
    }

    @Test
    void getById_shouldReturnCliente() throws Exception {
        when(clienteService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/clientes/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    void update_shouldReturnCliente() throws Exception {
        when(clienteService.update(any(), any())).thenReturn(dto);

        ClienteCreateRequestDto request = ClienteCreateRequestDto.builder()
            .nombre("Juan").apellido("Perez").email("juan@test.com")
            .contrasena("12345678")
            .domicilio(new DomicilioEnvioDto("Argentina", "BA", "CABA", "Calle", "123", null, null))
            .rol("CLIENTE").build();

        mockMvc.perform(put("/api/clientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    void delete_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/api/clientes/1"))
            .andExpect(status().isOk());
    }

    @Test
    void registrar_conEmailDuplicado_shouldReturn400() throws Exception {
        when(clienteService.register(any()))
            .thenThrow(new backend.exceptions.DuplicateResourceException("El email ya se encuentra registrado"));

        ClienteCreateRequestDto request = ClienteCreateRequestDto.builder()
            .nombre("Juan").apellido("Perez").email("dup@test.com")
            .contrasena("12345678")
            .domicilio(new DomicilioEnvioDto("Argentina", "BA", "CABA", "Calle", "123", null, null))
            .rol("CLIENTE").build();

        mockMvc.perform(post("/api/clientes/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("El email ya se encuentra registrado"));
    }
}
