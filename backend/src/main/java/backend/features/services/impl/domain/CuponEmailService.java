package backend.features.services.impl.domain;

import backend.features.models.Cliente;
import backend.features.models.Cupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CuponEmailService {

    public void sendCuponEmail(Cliente cliente, Cupon cupon) {
        String detalleDescuento = cupon.getTipoDescuento().name().equals("PORCENTAJE")
            ? cupon.getValor() + "%"
            : "$" + cupon.getValor();

        log.info(
            "Mail de cupon enviado a {} con codigo {}, vigencia {} - {}, descuento {}",
            cliente.getEmail(),
            cupon.getCodigo(),
            cupon.getFechaDesde(),
            cupon.getFechaHasta(),
            detalleDescuento
        );
    }
}
