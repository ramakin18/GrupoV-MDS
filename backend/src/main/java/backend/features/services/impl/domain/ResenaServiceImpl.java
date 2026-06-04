package backend.features.services.impl.domain;

import backend.exceptions.ResourceNotFoundException;
import backend.features.dtos.request.ResenaCreateRequestDto;
import backend.features.dtos.response.ResenaResponseDto;
import backend.features.mappers.ResenaMapper;
import backend.features.models.Cliente;
import backend.features.models.Producto;
import backend.features.models.Resena;
import backend.features.repositories.ClienteRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.specs.IResenaRepository;
import backend.features.services.interfaces.domain.IResenaService;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ResenaServiceImpl implements IResenaService {

    private final IResenaRepository resenaRepository;
    private final ClienteRepository clienteRepository;
    private final IProductoRepository productoRepository;
    private final ResenaMapper resenaMapper;

    @Override
    public ResenaResponseDto create(ResenaCreateRequestDto request) {
        Cliente cliente = clienteRepository.findById(request.getUsuario().getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con id: " + request.getUsuario()));

        Producto producto = productoRepository.findByIdProductoAndBorradoFalse(request.getProducto().getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con id: " + request.getProducto()));

        Resena reseña = resenaMapper.toModel(request);
        reseña.setUsuario(cliente);
        reseña.setProducto(producto);
        reseña.setFechaCreacion(LocalDateTime.now());

        if (resenaRepository.existsByUsuario_IdAndProducto_IdProducto(cliente.getId(), producto.getIdProducto())) {
            throw new ValidationException("Ya reseñaste este producto.");
        }

        Resena saved = resenaRepository.save(reseña);
        return resenaMapper.toResponseDto(saved);
    }

    @Override
    public List<ResenaResponseDto> getAll() {
        return resenaRepository.findAll()
                .stream()
                .map(resenaMapper::toResponseDto)
                .toList();
    }

    @Override
    public ResenaResponseDto getById(Long id) {
        Resena reseña = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reseña no encontrada con id: " + id));

        return resenaMapper.toResponseDto(reseña);
    }

    @Override
    public List<ResenaResponseDto> getByProductoId(Long productoId) {
        return resenaRepository.findByProducto_IdProducto(productoId)
                .stream()
                .map(resenaMapper::toResponseDto)
                .toList();
    }



    @Override
    public void deleteByAdmin(Long id) {

    }

    @Override
    public void deleteByCliente(Long id, Long usuarioId) {

    }
}