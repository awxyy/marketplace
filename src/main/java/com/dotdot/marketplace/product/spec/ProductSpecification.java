package com.dotdot.marketplace.product.spec;

import com.dotdot.marketplace.product.dto.ProductFilterRequest;
import com.dotdot.marketplace.product.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> withFilters(ProductFilterRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getName() != null && !request.getName().isBlank())
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + request.getName().toLowerCase() + "%"));

            if (request.getMinPrice() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));

            if (request.getMaxPrice() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
