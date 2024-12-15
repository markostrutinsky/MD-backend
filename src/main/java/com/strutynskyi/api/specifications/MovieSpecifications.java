package com.strutynskyi.api.specifications;

import com.strutynskyi.api.models.Movie;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class MovieSpecifications {
    public static Specification<Movie> filterBy(String field, String value) {
        return (root, query, criteriaBuilder) -> {
            switch (field) {
                case "genre":
                    return criteriaBuilder.equal(root.get("genre"), value);
                case "rating":
                    return criteriaBuilder.equal(root.get("rating"), Double.valueOf(value));
                case "releaseDate":
                    return criteriaBuilder.equal(root.get("releaseDate"), LocalDate.parse(value));
                default:
                    throw new IllegalArgumentException("Invalid filter field: " + field);
            }
        };
    }
}
