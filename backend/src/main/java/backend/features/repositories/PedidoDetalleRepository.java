package backend.features.repositories;

import backend.features.dtos.response.ProductoMasVendidoResponseDto;
import backend.features.models.PedidoDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Long> {
    List<PedidoDetalle> findByPedidoIdPedido(Long idPedido);

    @Query("SELECT new backend.features.dtos.response.ProductoMasVendidoResponseDto(pd.producto.nombreProducto, SUM(pd.cantidad)) " +
           "FROM PedidoDetalle pd " +
           "WHERE pd.pedido.situacion = 'ENTREGADO' " +
           "AND pd.producto IS NOT NULL " +
           "AND (pd.pedido.fecha >= COALESCE(:desde, pd.pedido.fecha)) " +
           "AND (pd.pedido.fecha < COALESCE(:hasta, pd.pedido.fecha)) " +
           "GROUP BY pd.producto.idProducto, pd.producto.nombreProducto " +
           "ORDER BY SUM(pd.cantidad) DESC")
    List<ProductoMasVendidoResponseDto> findProductosMasVendidos(
        @Param("desde") LocalDateTime desde,
        @Param("hasta") LocalDateTime hasta
    );
}
