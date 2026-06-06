package backend.features.services.impl.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.ResenaCreateRequestDto;
import backend.features.dtos.response.ResenaResponseDto;
import backend.features.mappers.ResenaMapper;
import backend.features.models.Cliente;
import backend.features.models.Producto;
import backend.features.models.Resena;
import backend.features.models.SituacionPedido;
import backend.features.repositories.ClienteRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.PedidoDetalleRepository;
import backend.features.repositories.specs.IResenaRepository;
import backend.features.services.interfaces.domain.IResenaService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ResenaServiceImpl implements IResenaService {

    private final IResenaRepository resenaRepository;
    private final ClienteRepository clienteRepository;
    private final IProductoRepository productoRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final ResenaMapper resenaMapper;

    @Override
    @Transactional
    public ResenaResponseDto create(ResenaCreateRequestDto request) {
        // Prevención de NullPointerExceptions
        if (request.getUsuario() == null || request.getProducto() == null) {
            throw new ValidationException("El usuario y el producto son obligatorios.");
        }

        Cliente cliente = clienteRepository.findById(request.getUsuario().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Producto producto = productoRepository.findById(request.getProducto().getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        if (producto.isBorrado()) {
            throw new ValidationException("No se pueden dejar reseñas para productos inactivos.");
        }

        boolean comproYRecibio = pedidoDetalleRepository.hasClienteCompradoYEntregado(
                cliente.getId(), producto.getIdProducto(), SituacionPedido.ENTREGADO);
        
        if (!comproYRecibio) {
            throw new ValidationException("Solo puedes reseñar productos de pedidos que ya te han sido ENTREGADOS.");
        }

        if (resenaRepository.existsByUsuario_IdAndProducto_IdProducto(cliente.getId(), producto.getIdProducto())) {
            throw new ValidationException("Ya reseñaste este producto anteriormente.");
        }

        Resena reseña = resenaMapper.toModel(request);
        reseña.setUsuario(cliente);
        reseña.setProducto(producto);
        reseña.setFechaCreacion(LocalDateTime.now());

        Resena saved = resenaRepository.save(reseña);
        
        resenaRepository.flush(); 
        recalcularEstadisticasProducto(producto);
        
        return resenaMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ResenaResponseDto update(Long id, ResenaCreateRequestDto request, Long usuarioId) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));

        if (!resena.getUsuario().getId().equals(usuarioId)) {
            throw new ValidationException("No puedes editar una reseña que no es tuya.");
        }

        resena.setPuntuacion(request.getPuntuacion());
        resena.setDescripcion(request.getDescripcion());
        
        Resena saved = resenaRepository.save(resena);
        
        resenaRepository.flush();
        recalcularEstadisticasProducto(resena.getProducto());
        
        return resenaMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponseDto> getAll() {
        return resenaRepository.findAll().stream().map(resenaMapper::toResponseDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResenaResponseDto getById(Long id) {
        Resena reseña = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + id));
        return resenaMapper.toResponseDto(reseña);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponseDto> getByProductoId(Long productoId) {
        return resenaRepository.findByProducto_IdProducto(productoId)
                .stream().map(resenaMapper::toResponseDto).toList();
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));
        Producto producto = resena.getProducto();
        
        resenaRepository.delete(resena);
        
        resenaRepository.flush();
        recalcularEstadisticasProducto(producto);
    }

    @Override
    @Transactional
    public void deleteByCliente(Long id, Long usuarioId) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));
                
        if (!resena.getUsuario().getId().equals(usuarioId)) {
            throw new ValidationException("No puedes eliminar una reseña que no es tuya.");
        }
        Producto producto = resena.getProducto();
        
        resenaRepository.delete(resena);
        
        // Forzamos el borrado real en BD antes de consultar
        resenaRepository.flush();
        recalcularEstadisticasProducto(producto);
    }

    // metodo de ayuda, para recalclar el producto
    private void recalcularEstadisticasProducto(Producto producto) {
        List<Resena> resenas = resenaRepository.findByProducto_IdProducto(producto.getIdProducto());
        if (resenas.isEmpty()) {
            producto.setCantidadResenas(0);
            producto.setPromedioPuntuacion(0.0);
        } else {
            producto.setCantidadResenas(resenas.size());
            double prom = resenas.stream().mapToInt(Resena::getPuntuacion).average().orElse(0.0);
            producto.setPromedioPuntuacion(Math.round(prom * 10.0) / 10.0);
        }
        productoRepository.save(producto);
    }
}