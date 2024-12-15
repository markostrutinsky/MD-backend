package com.strutynskyi.api.services.impl;

import com.strutynskyi.api.config.FilteringConfig;
import com.strutynskyi.api.config.PaginationConfig;
import com.strutynskyi.api.dto.movie.*;
import com.strutynskyi.api.exceptions.MovieAlreadyExistsException;
import com.strutynskyi.api.exceptions.NoMoviesByDirectorFoundException;
import com.strutynskyi.api.exceptions.NoSuchMovieException;
import com.strutynskyi.api.mappers.MovieMappers;
import com.strutynskyi.api.models.Director;
import com.strutynskyi.api.models.Movie;
import com.strutynskyi.api.repositories.MovieRepository;
import com.strutynskyi.api.services.interfaces.DirectorService;
import com.strutynskyi.api.services.interfaces.MovieService;
import com.strutynskyi.api.specifications.MovieSpecifications;
import com.strutynskyi.api.validators.RequestDTOValidator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;
    private final DirectorService directorService;
    private final PaginationConfig paginationConfig;
    private final FilteringConfig filterConfig;
    private final RequestDTOValidator<CreateMovieRequestDTO> createMovieRequestDTOValidator;
    private final RequestDTOValidator<UpdateMovieRequestDTO> updateMovieRequestDTOValidator;
    private static final Logger logger = LogManager.getLogger("project");

    @Override
    public Page<Movie> findAll(Integer pageNo, Integer pageSize, Map<String, String> filteringFields) {
        logger.info("MovieServiceImpl:findAll() Retrieving all movies from repository");
        if (pageNo == null)
            pageNo = paginationConfig.getDefaultPageNumber();

        if (pageSize == null)
            pageSize = paginationConfig.getDefaultPageSize();

        if (pageSize > paginationConfig.getMaxPageSize())
            pageSize = paginationConfig.getMaxPageSize();

        List<String> allowedFields = filterConfig.getAllowedFields();
        Map<String, String> filtersParam = new HashMap<>(filteringFields);
        if (!filtersParam.isEmpty()) {
            for (String filterKey : filtersParam.keySet()) {
                if (!allowedFields.contains(filterKey.toLowerCase())) {
                    filteringFields.remove(filterKey);
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Movie> movies;

        Specification<Movie> specification = Specification.where(null);
        for (Map.Entry<String, String> filter : filteringFields.entrySet()) {
            specification = specification.and(MovieSpecifications.filterBy(filter.getKey(), filter.getValue()));
        }
        movies = movieRepository.findAll(specification, pageable);

        logger.info("MovieServiceImpl:findAll() Retrieved {} movies", movies.getContent().size());
        return movies;
    }

    @Override
    public Movie findById(Long id) {
        Movie foundMovie = movieRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("MovieServiceImpl:findById() Movie not found by id: {}", id);
                    throw new NoSuchMovieException();
                });
        logger.info("MovieServiceImpl:findById() Movie with id {} found in repository", id);
        return foundMovie;
    }

    @Override
    public List<Movie> findByDirector(String firstName, String lastName) {
        logger.info("MovieServiceImpl:findByDirector() Retrieving movies from repository by name {} {}", firstName, lastName);

        boolean directorExists = directorService.existsByFirstNameAndLastName(firstName, lastName);
        if (!directorExists) {
            logger.error("MovieServiceImpl:findByDirector() Director not found by name {} {}", firstName, lastName);
            throw new NoMoviesByDirectorFoundException(firstName, lastName);
        }
        List<Movie> movies = movieRepository.findByDirector(firstName, lastName).orElse(List.of());
        logger.info("MovieServiceImpl:findByDirector() Retrieved {} movies from repository by name {} {}", movies.size(),firstName, lastName);
        return movies;
    }

    @Override
    @CacheEvict(value = "directors", key = "#movieDTO.directorId")
    public Movie save(CreateMovieRequestDTO movieDTO, MultipartFile imageFile) throws IOException {
        createMovieRequestDTOValidator.validate(movieDTO);
        Director existingDirector = directorService.findById(movieDTO.getDirectorId());
        Movie validMovieModel = MovieMappers.toMovieFromCreateDTO(movieDTO);
        validMovieModel.setDirector(existingDirector);

        Optional<Movie> existingMovie = movieRepository.findAll()
                .stream()
                .filter(m -> m.equals(validMovieModel))
                .findFirst();

        if (existingMovie.isPresent()) {
            logger.error("MovieServiceImpl:save() Movie already exists with id: {}", existingMovie.get().getId());
            throw new MovieAlreadyExistsException();
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            validMovieModel.setImageName(imageFile.getOriginalFilename());
            validMovieModel.setImageType(imageFile.getContentType());
            validMovieModel.setImageData(imageFile.getBytes());
        }

        Movie savedMovie = movieRepository.save(validMovieModel);
        logger.info("MovieServiceImpl:save() Saved new movie with id: {}", savedMovie.getId());
        return savedMovie;
    }

    @Override
    @CacheEvict(value = "directors", key = "#result.director.id")
    public Movie update(Long id, UpdateMovieRequestDTO movieDTO, MultipartFile imageFile) throws IOException {
        updateMovieRequestDTOValidator.validate(movieDTO);
        Movie validMovieModel = MovieMappers.toMovieFromUpdateDTO(movieDTO);
        Movie movieToUpdate = movieRepository.findById(id)
                .orElseThrow(
                        () -> {
                            logger.error("MovieServiceImpl:update() Movie not found with id: {}", id);
                            throw new NoSuchMovieException();
                        });

        movieToUpdate.setTitle(validMovieModel.getTitle());
        movieToUpdate.setGenre(validMovieModel.getGenre());
        movieToUpdate.setReleaseDate(validMovieModel.getReleaseDate());
        movieToUpdate.setDuration(validMovieModel.getDuration());

        if (imageFile != null && !imageFile.isEmpty()) {
            movieToUpdate.setImageName(imageFile.getOriginalFilename());
            movieToUpdate.setImageType(imageFile.getContentType());
            movieToUpdate.setImageData(imageFile.getBytes());
        }

        Movie savedMovie = movieRepository.save(movieToUpdate);
        logger.info("MovieServiceImpl:update() Updated movie with id: {}", savedMovie.getId());

        return savedMovie;
    }

    @Override
    @CacheEvict(value = "directors", key = "#result.director.id")
    public Movie delete(Long id) {
        Movie movieToDelete = movieRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("MovieServiceImpl:delete() Movie not found with id: {}", id);
                    throw new NoSuchMovieException();
                });

        movieRepository.deleteById(id);
        logger.info("MovieServiceImpl:delete() Deleted movie by id: {}", movieToDelete.getId());

        return movieToDelete;
    }
}
