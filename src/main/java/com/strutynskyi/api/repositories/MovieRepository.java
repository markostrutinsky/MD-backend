package com.strutynskyi.api.repositories;

import com.strutynskyi.api.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {


    @Query("SELECT m FROM Movie m JOIN m.director d WHERE d.firstName = :firstName AND d.lastName = :lastName")
    Optional<List<Movie>> findByDirector(String firstName, String lastName);


}
