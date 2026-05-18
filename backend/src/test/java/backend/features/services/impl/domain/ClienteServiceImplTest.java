package backend.features.services.impl.domain;

import backend.exceptions.DuplicateResourceException;
import backend.exceptions.InvalidCredentialsException;
import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.ClienteCreateRequestDto;
import backend.features.dtos.request.ClienteLoginRequestDto;
import backend.features.dtos.response.ClienteResponseDto;
import backend.features.mappers.ClienteMapper;
import backend.features.models.Cliente;
import backend.features.repositories.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock private ClienteRepository clienteRepository;
    @Mock private ClienteMapper clienteMapper;

    private ClienteServiceImpl service;
    private ClienteCreateRequestDto validRequest;
    private ClienteLoginRequestDto validLogin;
    private Cliente existingCliente;

    @BeforeEach
    void setUp() {
        service = new ClienteServiceImpl(clienteRepository, clienteMapper);

        validRequest = new ClienteCreateRequestDto();
        validRequest.setNombre("Juan");
        validRequest.setApellido("Perez");
        validRequest.setEmail("juan@test.com");
        validRequest.setContrasena("password123");
        validRequest.setRol("CLIENTE");

        validLogin = new ClienteLoginRequestDto();
        validLogin.setEmail("juan@test.com");
        validLogin.setContrasena("password123");

        existingCliente = Cliente.builder()
            .id(1L)
            .nombre("Juan")
            .apellido("Perez")
            .email("juan@test.com")
            .contrasena("password123")
            .rol("CLIENTE")
            .build();
    }

    /* ================== REGISTER ================== */

    @Test
    void register_conDatosValidos_shouldPersist() {
        when(clienteRepository.findByEmail("juan@test.com")).thenReturn(Optional.empty());
        when(clienteMapper.toEntity(validRequest)).thenReturn(existingCliente);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(existingCliente);
        when(clienteMapper.toResponseDto(any(Cliente.class))).thenReturn(
            ClienteResponseDto.builder().id(1L).nombre("Juan").apellido("Perez")
                .email("juan@test.com").rol("CLIENTE").build()
        );

        ClienteResponseDto result = service.register(validRequest);
        assertNotNull(result);
        assertEquals("juan@test.com", result.getEmail());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void register_conEmailRegistrado_shouldThrow() {
        when(clienteRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(existingCliente));

        assertThrows(DuplicateResourceException.class, () -> service.register(validRequest));
        verify(clienteRepository, never()).save(any());
    }

    /* ================== LOGIN ================== */

    @Test
    void login_conCredencialesValidas_shouldSucceed() {
        when(clienteRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(existingCliente));
        when(clienteMapper.toResponseDto(any(Cliente.class))).thenReturn(
            ClienteResponseDto.builder().id(1L).nombre("Juan").email("juan@test.com").build()
        );

        ClienteResponseDto result = service.login(validLogin);
        assertNotNull(result);
    }

    @Test
    void login_conEmailInexistente_shouldThrow() {
        when(clienteRepository.findByEmail("no@existe.com")).thenReturn(Optional.empty());
        ClienteLoginRequestDto bad = new ClienteLoginRequestDto();
        bad.setEmail("no@existe.com");
        bad.setContrasena("pass");

        assertThrows(InvalidCredentialsException.class, () -> service.login(bad));
    }

    @Test
    void login_conContrasenaIncorrecta_shouldThrow() {
        when(clienteRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(existingCliente));
        ClienteLoginRequestDto bad = new ClienteLoginRequestDto();
        bad.setEmail("juan@test.com");
        bad.setContrasena("wrongpass");

        assertThrows(InvalidCredentialsException.class, () -> service.login(bad));
    }

    /* ================== UPDATE ================== */

    @Test
    void update_conEmailCambiadoYDuplicado_shouldThrow() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(existingCliente));

        ClienteCreateRequestDto updateReq = new ClienteCreateRequestDto();
        updateReq.setNombre("Juan");
        updateReq.setApellido("Perez");
        updateReq.setEmail("nuevo@test.com");
        updateReq.setContrasena("password123");

        when(clienteRepository.findByEmail("nuevo@test.com")).thenReturn(Optional.of(
            Cliente.builder().id(2L).email("nuevo@test.com").build()
        ));

        assertThrows(DuplicateResourceException.class, () -> service.update(1L, updateReq));
    }

    /* ================== DELETE ================== */

    @Test
    void delete_conIdValido_shouldRemove() {
        when(clienteRepository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(clienteRepository).deleteById(1L);
    }

    @Test
    void delete_conIdInexistente_shouldThrow() {
        when(clienteRepository.existsById(99L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }
}
