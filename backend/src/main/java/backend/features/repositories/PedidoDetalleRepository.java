package backend.features.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.models.PedidoDetalle;
import backend.features.models.SituacionPedido;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    List<PedidoDetalle> findByPedidoIdPedido(Long idPedido);

    @Query("SELECT new backend.features.dtos.response.ProductoMasVendidoResponseDto(pd.producto.nombreProducto, SUM(pd.cantidad)) " +
           "FROM PedidoDetalle pd " +
           "WHERE pd.pedido.situacion = 'ENTREGADO' " +
           "AND (:mes IS NULL OR MONTH(pd.pedido.fecha) = :mes) " +
           "AND (:anio IS NULL OR YEAR(pd.pedido.fecha) = :anio) " +
           "GROUP BY pd.producto.nombreProducto " +
           "ORDER BY SUM(pd.cantidad) DESC")
    List<ProductoMasVendidoResponseDto> findProductosMasVendidos(@Param("mes") Integer mes, @Param("anio") Integer anio);

    // Consulta para verificar que el usuario compró y recibió el producto
    @Query("SELECT COUNT(pd) > 0 FROM PedidoDetalle pd " +
           "WHERE pd.pedido.cliente.id = :clienteId " +
           "AND pd.producto.idProducto = :productoId " +
           "AND pd.pedido.situacion = :situacion")
    boolean hasClienteCompradoYEntregado(
            @Param("clienteId") Long clienteId, 
            @Param("productoId") Long productoId, 
            @Param("situacion") SituacionPedido situacion
    );
}
