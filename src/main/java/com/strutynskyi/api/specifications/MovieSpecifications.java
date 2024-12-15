package com.strutynskyi.api.specifications;

import com.strutynskyi.api.models.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

public class MovieSpecifications {
    public static Specification<Movie> filterBy(String field, String value, EntityManager entityManager) {
        return (root, query, criteriaBuilder) -> {
            switch (field) {
                case "genre":
                    return criteriaBuilder.like(root.get("genre"), "%" + value + "%");
                case "rating":
                    System.out.println(root.get("rating").getJavaType());
                    return criteriaBuilder.equal(root.get("rating"), value);
                case "released":
                    System.out.println(root.get("released").getJavaType());
                    Expression<Integer> yearExpression = criteriaBuilder.function(
                            "DATE_PART", Integer.class,
                            criteriaBuilder.literal("year"),
                            root.get("released")
                    );
                    return criteriaBuilder.equal(yearExpression, Integer.valueOf(value));
                default:
                    throw new IllegalArgumentException("Invalid filter field: " + field);
            }
        };
    }
}