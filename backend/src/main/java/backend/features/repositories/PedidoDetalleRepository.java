package backend.features.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.features.models.PedidoDetalle;
import backend.features.models.SituacionPedido;
import backend.features.dtos.response.ProductoMasVendidoResponseDto;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    List<PedidoDetalle> findByPedidoIdPedido(Long idPedido);

    @Query("SELECT new backend.features.dtos.response.ProductoMasVendidoResponseDto(pd.producto.nombreProducto, SUM(pd.cantidad)) " +
           "FROM PedidoDetalle pd " +
           "WHERE pd.pedido.situacion = 'ENTREGADO' " +
           "AND pd.producto IS NOT NULL " +
           "AND (pd.pedido.fecha >= :desde) " +
           "AND (pd.pedido.fecha < :hasta) " +
           "GROUP BY pd.producto.idProducto, pd.producto.nombreProducto " +
           "ORDER BY SUM(pd.cantidad) DESC")
    List<ProductoMasVendidoResponseDto> findProductosMasVendidos(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );

    @Query("SELECT COUNT(pd) > 0 FROM PedidoDetalle pd " +
           "WHERE pd.pedido.cliente.id = :clienteId " +
           "AND pd.producto.idProducto = :productoId " +
           "AND pd.pedido.situacion = :situacion")
    boolean hasClienteCompradoYEntregado(
            @Param("clienteId") Long clienteId, 
            @Param("productoId") Long productoId, 
            @Param("situacion") SituacionPedido situacion
    );

    @Query("SELECT COUNT(DISTINCT pd.producto.idProducto) = :totalProductos FROM PedidoDetalle pd " +
           "WHERE pd.pedido.cliente.id = :clienteId " +
           "AND pd.producto.idProducto IN :productoIds " +
           "AND pd.pedido.situacion = :situacion")
    boolean hasClienteCompradoTodosLosProductos(
            @Param("clienteId") Long clienteId, 
            @Param("productoIds") List<Long> productoIds,
            @Param("totalProductos") long totalProductos,
            @Param("situacion") SituacionPedido situacion
    );
}
