package com.strutynskyi.api.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.strutynskyi.api.config.FilteringConfig;
import com.strutynskyi.api.config.PaginationConfig;
import com.strutynskyi.api.dto.director.UpdateDirectorRequestDTO;
import com.strutynskyi.api.dto.movie.CreateMovieRequestDTO;
import com.strutynskyi.api.dto.movie.MovieDTO;
import com.strutynskyi.api.dto.movie.MovieResponseDTO;
import com.strutynskyi.api.dto.movie.UpdateMovieRequestDTO;
import com.strutynskyi.api.mappers.MovieMappers;
import com.strutynskyi.api.models.Movie;
import com.strutynskyi.api.services.interfaces.MovieService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataInput;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;
    private final ObjectMapper objectMapper;
    private final PaginationConfig paginationConfig;
    private final FilteringConfig filterConfig;
    private static final Logger logger = LogManager.getLogger("project");

    @GetMapping
    public ResponseEntity<Page<MovieResponseDTO>> getAll(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "filterField", required = false) String filterField,
            @RequestParam(value = "filterValue", required = false) String filterValue
    ) {
        if (pageNo == null)
            pageNo = paginationConfig.getDefaultPageNumber();

        if (pageSize == null)
            pageSize = paginationConfig.getDefaultPageSize();

        if (pageSize > paginationConfig.getMaxPageSize())
            pageSize = paginationConfig.getMaxPageSize();

        if (filterConfig.isEnabled() && filterField != null) {
            List<String> allowedFields = filterConfig.getAllowedFields();
            if (!allowedFields.contains(filterField)) {
                throw new IllegalArgumentException("Filtering by " + filterField + " is not allowed.");
            }
        }

        logger.info("MovieController:getAll() Received request to fetch all movies");
        Page<Movie> movies = movieService.findAll(pageNo, pageSize, filterField, filterValue);

        logger.info("MovieController:getAll() Fetched {} movies", movies.getContent().size());
        return ResponseEntity.ok(movies.map(MovieMappers::toMovieResponseDTOFromMovie));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getById(@PathVariable Long id) {
        logger.info("MovieController:getById() Received request to fetch movie by id: {}", id);
        Movie existingMovie = movieService.findById(id);
        MovieDTO movieDTO = MovieMappers.toMovieDTO(existingMovie);
        logger.info("MovieController:getById() Successfully fetched movie by id: {}", id);
        return ResponseEntity.ok(movieDTO);
    }

    @GetMapping("/{movieId}/image")
    public ResponseEntity<?> getImage(@PathVariable Long movieId) {
        logger.info("MovieController:getImage() Received request to fetch movie image by id: {}", movieId);
        Movie movie = movieService.findById(movieId);

        if (movie == null || movie.getImageData() == null)
            return new ResponseEntity<>("Image not found", HttpStatus.NOT_FOUND);

        byte[] imageFile = movie.getImageData();
        logger.info("MovieController:getImage() Successfully fetched movie image by id: {}", movieId);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(movie.getImageType()))
                .body(imageFile);
    }

    @GetMapping("/by-director")
    public ResponseEntity<List<MovieResponseDTO>> getByDirector(@RequestParam String firstName, @RequestParam String lastName) {
        logger.info("MovieController:getByDirector() Fetching movies by director: {} {}", firstName, lastName);
        List<Movie> movies = movieService.findByDirector(firstName, lastName);
        List<MovieResponseDTO> moviesResponseDTO = movies
                .stream()
                .map(MovieMappers::toMovieResponseDTOFromMovie)
                .toList();
        logger.info("MovieController:getByDirector() Successfully fetched {} movies", movies.size());
        return new ResponseEntity<>(moviesResponseDTO, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<String> createMovie(@RequestPart("createDTO") String createDTOstr, @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        CreateMovieRequestDTO createDTO = objectMapper.readValue(createDTOstr, CreateMovieRequestDTO.class);
        logger.info("MovieController:createMovie() Received request to create movie");
        Movie saved = movieService.save(createDTO, imageFile);
        logger.info("MovieController:createMovie() Created movie with id: {}", saved.getId());
        return new ResponseEntity<>("New movie was created with id: " + saved.getId(), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id, @RequestPart("updateDTO") String updateDTOstr, @RequestPart(value = "imageFile", required = false) MultipartFile multipartFile) throws IOException {
        UpdateMovieRequestDTO updateDTO = objectMapper.readValue(updateDTOstr, UpdateMovieRequestDTO.class);
        logger.info("MovieController:updateMovie() Received request to update movie by id: {}", id);
        Movie updatedMovie = movieService.update(id, updateDTO, multipartFile);
        MovieDTO movieDTO = MovieMappers.toMovieDTO(updatedMovie);
        logger.info("MovieController:updateMovie() Successfully updated movie: {}", movieDTO);
        return new ResponseEntity<>(movieDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMovie(@PathVariable Long id) {
        logger.info("MovieController:deleteMovie() Received request to delete movie by id: {}", id);
        Movie deletedMovie = movieService.delete(id);
        logger.info("MovieController:deleteMovie() Successfully deleted movie by id: {}", id);
        return new ResponseEntity<>("Movie was deleted by id: " + deletedMovie.getId(), HttpStatus.OK);
    }
}