package backend.features.services.impl.domain;

import backend.exceptions.ValidationException;
import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.dtos.response.StockMinimoReporteResponseDto;
import backend.features.models.Kit;
import backend.features.models.KitProducto;
import backend.features.models.Producto;
import backend.features.repositories.IKitRepository;
import backend.features.repositories.IProductoRepository;
import backend.features.repositories.PedidoDetalleRepository;
import backend.features.services.interfaces.domain.IReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements IReporteService {

    private static final int PORCENTAJE_CERCANIA_STOCK_MINIMO = 20;

    private final PedidoDetalleRepository pedidoDetalleRepository;
    private final IProductoRepository productoRepository;
    private final IKitRepository kitRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoMasVendidoResponseDto> getProductosMasVendidos(Integer mes, Integer anio, Integer dia) {
        RangoFechas rango = calcularRangoFechas(mes, anio, dia);
        return pedidoDetalleRepository.findProductosMasVendidos(rango.desde(), rango.hasta());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockMinimoReporteResponseDto> getProductosCercaStockMinimo() {
        Stream<StockMinimoReporteResponseDto> productos = productoRepository.findByBorradoFalse().stream()
            .filter(producto -> estaDentroDeCota(producto.getStockDisponible(), producto.getStockMinimo()))
            .map(this::toProductoStockMinimoDto);

        Stream<StockMinimoReporteResponseDto> kits = kitRepository.findByActivoTrueOrderByNombreAsc().stream()
            .map(this::toKitStockMinimoDto)
            .filter(dto -> estaDentroDeCota(dto.stockActual(), dto.stockMinimo()));

        return Stream.concat(productos, kits)
            .sorted(Comparator
                .comparingInt((StockMinimoReporteResponseDto dto) -> dto.stockActual() - dto.stockMinimo())
                .thenComparing(StockMinimoReporteResponseDto::nombre))
            .toList();
    }

    private boolean estaDentroDeCota(Integer stockActual, Integer stockMinimo) {
        int actual = normalizarStock(stockActual);
        int minimo = normalizarStock(stockMinimo);
        int cota = minimo + (int) Math.ceil(minimo * PORCENTAJE_CERCANIA_STOCK_MINIMO / 100.0);
        return actual <= cota;
    }

    private StockMinimoReporteResponseDto toProductoStockMinimoDto(Producto producto) {
        return new StockMinimoReporteResponseDto(
            "PROD-" + producto.getIdProducto(),
            producto.getNombreProducto(),
            normalizarStock(producto.getStockDisponible()),
            normalizarStock(producto.getStockMinimo())
        );
    }

    private StockMinimoReporteResponseDto toKitStockMinimoDto(Kit kit) {
        return new StockMinimoReporteResponseDto(
            "KIT-" + kit.getIdKit(),
            kit.getNombre(),
            normalizarStock(kit.getStock()),
            calcularStockMinimoKit(kit)
        );
    }

    private int calcularStockMinimoKit(Kit kit) {
        if (kit.getProductos() == null || kit.getProductos().isEmpty()) {
            return 0;
        }

        return kit.getProductos().stream()
            .mapToInt(this::calcularStockMinimoComponenteEnKits)
            .max()
            .orElse(0);
    }

    private int calcularStockMinimoComponenteEnKits(KitProducto kitProducto) {
        Producto producto = kitProducto.getProducto();
        int stockMinimoProducto = producto != null ? normalizarStock(producto.getStockMinimo()) : 0;
        int cantidad = kitProducto.getCantidad() != null && kitProducto.getCantidad() > 0
            ? kitProducto.getCantidad()
            : 1;
        return (int) Math.ceil(stockMinimoProducto / (double) cantidad);
    }

    private int normalizarStock(Integer stock) {
        return stock != null ? stock : 0;
    }

    private RangoFechas calcularRangoFechas(Integer mes, Integer anio, Integer dia) {
        if (mes == null && anio == null && dia == null) {
            return new RangoFechas(null, null);
        }

        validarMes(mes);
        validarAnio(anio);

        if (dia != null) {
            if (mes == null || anio == null) {
                throw new ValidationException("Para filtrar por dia debe indicar mes y anio.");
            }
            validarDia(anio, mes, dia);
            LocalDate desde = LocalDate.of(anio, mes, dia);
            return new RangoFechas(desde.atStartOfDay(), desde.plusDays(1).atStartOfDay());
        }

        if (mes != null && anio != null) {
            YearMonth periodo = YearMonth.of(anio, mes);
            return new RangoFechas(periodo.atDay(1).atStartOfDay(), periodo.plusMonths(1).atDay(1).atStartOfDay());
        }

        if (anio != null) {
            LocalDate desde = LocalDate.of(anio, 1, 1);
            return new RangoFechas(desde.atStartOfDay(), desde.plusYears(1).atStartOfDay());
        }

        return new RangoFechas(null, null);
    }

    private void validarMes(Integer mes) {
        if (mes != null && (mes < 1 || mes > 12)) {
            throw new ValidationException("El mes debe estar entre 1 y 12.");
        }
    }

    private void validarAnio(Integer anio) {
        if (anio != null && anio < 1) {
            throw new ValidationException("El anio debe ser mayor a 0.");
        }
    }

    private void validarDia(Integer anio, Integer mes, Integer dia) {
        try {
            LocalDate.of(anio, mes, dia);
        } catch (DateTimeException ex) {
            throw new ValidationException("El dia no es valido para el mes y anio indicados.");
        }
    }

    private record RangoFechas(LocalDateTime desde, LocalDateTime hasta) {}
}
