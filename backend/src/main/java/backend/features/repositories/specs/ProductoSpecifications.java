package backend.features.repositories.specs;

import backend.features.models.Producto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoSpecifications {

    public static Specification<Producto> filtrarProductos(String nombre, BigDecimal precioMin, Integer stockMin) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombreProducto")), "%" + nombre.toLowerCase() + "%"));
            }
            if (precioMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precioMin));
            }
            if (stockMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stockDisponible"), stockMin));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

