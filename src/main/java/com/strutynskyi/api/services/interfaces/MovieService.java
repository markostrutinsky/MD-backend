package com.strutynskyi.api.services.interfaces;

import com.strutynskyi.api.dto.movie.CreateMovieRequestDTO;
import com.strutynskyi.api.dto.movie.UpdateMovieRequestDTO;
import com.strutynskyi.api.models.Movie;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {
    Page<Movie> findAll(Integer pageNo, Integer pageSize, String filterField, String filterValue);
    Movie findById(Long id);
    List<Movie> findByDirector(String firstName, String lastName);
    Movie save(CreateMovieRequestDTO movie, MultipartFile imageFile) throws IOException;
    Movie update(Long id , UpdateMovieRequestDTO movie, MultipartFile imageFile ) throws IOException;
    Movie delete(Long id);
}
