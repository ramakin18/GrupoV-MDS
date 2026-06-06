package backend.features.services.impl.domain;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.exceptions.ResourceNotFoundException;
import backend.exceptions.ValidationException;
import backend.features.dtos.request.ResenaCreateRequestDto;
import backend.features.dtos.response.ResenaResponseDto;
import backend.features.mappers.ResenaMapper;
import backend.features.models.Cliente;
import backend.features.models.Kit;
import backend.features.models.Producto;
import backend.features.models.Resena;
import backend.features.models.SituacionPedido;
import backend.features.repositories.ClienteRepository;
import backend.features.repositories.IKitRepository;
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
    private final IKitRepository kitRepository;
    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final ResenaMapper resenaMapper;

    @Override
    @Transactional
    public ResenaResponseDto create(ResenaCreateRequestDto request) {
        if (request.getProductoId() == null && request.getKitId() == null) {
            throw new ValidationException("Debe especificar un producto o un kit.");
        }

        Cliente cliente = clienteRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Producto producto = null;
        Kit kit = null;

        if (request.getProductoId() != null) {
            producto = productoRepository.findById(request.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

            if (producto.isBorrado()) {
                throw new ValidationException("No se pueden dejar reseñas para productos inactivos.");
            }

            boolean comproYRecibio = pedidoDetalleRepository.hasClienteCompradoYEntregado(
                    cliente.getId(), producto.getIdProducto(), SituacionPedido.ENTREGADO);

            if (!comproYRecibio) {
                throw new ValidationException(
                        "Solo puedes reseñar productos de pedidos que ya te han sido ENTREGADOS.");
            }

            if (resenaRepository.existsByUsuario_IdAndProducto_IdProductoAndEliminadoFalse(
                    cliente.getId(), producto.getIdProducto())) {
                throw new ValidationException("Ya reseñaste este producto anteriormente.");
            }
        }

        if (request.getKitId() != null) {
            kit = kitRepository.findById(request.getKitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Kit no encontrado"));

            if (!kit.isActivo()) {
                throw new ValidationException("No se pueden dejar reseñas para kits inactivos.");
            }

            List<Long> productoIds = kit.getProductos().stream()
                    .map(kp -> kp.getProducto().getIdProducto())
                    .toList();

            if (!productoIds.isEmpty()) {
                boolean comproYRecibio = pedidoDetalleRepository.hasClienteCompradoTodosLosProductos(
                        cliente.getId(), productoIds, productoIds.size(), SituacionPedido.ENTREGADO);

                if (!comproYRecibio) {
                    throw new ValidationException(
                            "Solo puedes reseñar kits de pedidos que ya te han sido ENTREGADOS.");
                }
            }

            if (resenaRepository.existsByUsuario_IdAndKit_IdKitAndEliminadoFalse(
                    cliente.getId(), kit.getIdKit())) {
                throw new ValidationException("Ya reseñaste este kit anteriormente.");
            }
        }

        Resena resena = resenaMapper.toModel(request);
        resena.setUsuario(cliente);
        resena.setProducto(producto);
        resena.setKit(kit);
        resena.setFechaCreacion(LocalDateTime.now());
        resena.setEliminado(false);

        Resena saved = resenaRepository.save(resena);

        resenaRepository.flush();
        if (producto != null) {
            recalcularEstadisticasProducto(producto);
        }
        if (kit != null) {
            recalcularEstadisticasKit(kit);
        }

        return resenaMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public ResenaResponseDto update(Long id, ResenaCreateRequestDto request, Long usuarioId) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));

        if (resena.getEliminado() != null && resena.getEliminado()) {
            throw new ValidationException("No puedes editar una reseña eliminada.");
        }

        if (!resena.getUsuario().getId().equals(usuarioId)) {
            throw new ValidationException("No puedes editar una reseña que no es tuya.");
        }

        resena.setPuntuacion(request.getPuntuacion());
        resena.setDescripcion(request.getDescripcion());

        Resena saved = resenaRepository.save(resena);

        resenaRepository.flush();
        if (resena.getProducto() != null) {
            recalcularEstadisticasProducto(resena.getProducto());
        }
        if (resena.getKit() != null) {
            recalcularEstadisticasKit(resena.getKit());
        }

        return resenaMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponseDto> getAll(boolean adminView) {
        List<Resena> resenas;
        if (adminView) {
            resenas = resenaRepository.findAll();
        } else {
            resenas = resenaRepository.findByEliminadoFalse();
        }
        return resenas.stream()
                .sorted(Comparator.comparing(Resena::getFechaCreacion).reversed())
                .map(resenaMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ResenaResponseDto getById(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + id));
        return resenaMapper.toResponseDto(resena);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponseDto> getByProductoId(Long productoId) {
        return resenaRepository.findByProducto_IdProductoAndEliminadoFalse(productoId)
                .stream()
                .sorted(Comparator.comparing(Resena::getFechaCreacion).reversed())
                .map(resenaMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResenaResponseDto> getByKitId(Long kitId) {
        return resenaRepository.findByKit_IdKitAndEliminadoFalse(kitId)
                .stream()
                .sorted(Comparator.comparing(Resena::getFechaCreacion).reversed())
                .map(resenaMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByAdmin(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));

        resena.setEliminado(true);
        resenaRepository.save(resena);

        resenaRepository.flush();
        if (resena.getProducto() != null) {
            recalcularEstadisticasProducto(resena.getProducto());
        }
        if (resena.getKit() != null) {
            recalcularEstadisticasKit(resena.getKit());
        }
    }

    @Override
    @Transactional
    public void deleteByCliente(Long id, Long usuarioId) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada"));

        if (!resena.getUsuario().getId().equals(usuarioId)) {
            throw new ValidationException("No puedes eliminar una reseña que no es tuya.");
        }

        resena.setEliminado(true);
        resenaRepository.save(resena);

        resenaRepository.flush();
        if (resena.getProducto() != null) {
            recalcularEstadisticasProducto(resena.getProducto());
        }
        if (resena.getKit() != null) {
            recalcularEstadisticasKit(resena.getKit());
        }
    }

    @Override
    @Transactional
    public ResenaResponseDto restore(Long id) {
        Resena resena = resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reseña no encontrada con id: " + id));

        if (resena.getEliminado() == null || !resena.getEliminado()) {
            throw new ValidationException("La reseña no está eliminada.");
        }

        resena.setEliminado(false);
        Resena saved = resenaRepository.save(resena);

        resenaRepository.flush();
        if (resena.getProducto() != null) {
            recalcularEstadisticasProducto(resena.getProducto());
        }
        if (resena.getKit() != null) {
            recalcularEstadisticasKit(resena.getKit());
        }

        return resenaMapper.toResponseDto(saved);
    }

    private void recalcularEstadisticasProducto(Producto producto) {
        long count = resenaRepository.countByProducto_IdProductoAndEliminadoFalse(producto.getIdProducto());
        Double avg = resenaRepository.avgPuntuacionByProductoId(producto.getIdProducto());

        producto.setCantidadResenas((int) count);
        producto.setPromedioPuntuacion(Math.round(avg * 10.0) / 10.0);
        productoRepository.save(producto);
    }

    private void recalcularEstadisticasKit(Kit kit) {
        long count = resenaRepository.countByKit_IdKitAndEliminadoFalse(kit.getIdKit());
        Double avg = resenaRepository.avgPuntuacionByKitId(kit.getIdKit());

        kit.setCantidadResenas((int) count);
        kit.setPromedioPuntuacion(Math.round(avg * 10.0) / 10.0);
        kitRepository.save(kit);
    }
}
