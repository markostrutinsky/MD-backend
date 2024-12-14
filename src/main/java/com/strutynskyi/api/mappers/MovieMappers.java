package com.strutynskyi.api.mappers;

import com.strutynskyi.api.dto.movie.CreateMovieRequestDTO;
import com.strutynskyi.api.dto.movie.MovieDTO;
import com.strutynskyi.api.dto.movie.MovieResponseDTO;
import com.strutynskyi.api.dto.movie.UpdateMovieRequestDTO;
import com.strutynskyi.api.models.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMappers {
    public static MovieDTO toMovieDTO(Movie movieModel) {
        return new MovieDTO(
                movieModel.getId(),
                movieModel.getTitle(),
                movieModel.getGenre(),
                movieModel.getReleaseDate(),
                movieModel.getDuration(),
                movieModel.getDirector().getId()
        );
    }

    public static Movie toMovieFromCreateDTO(CreateMovieRequestDTO movieDTO) {
        return new Movie(
                movieDTO.getTitle(),
                movieDTO.getGenre(),
                movieDTO.getReleaseDate(),
                movieDTO.getDuration()
        );
    }

    public static Movie toMovieFromUpdateDTO(UpdateMovieRequestDTO movieDTO) {
        return new Movie(
                movieDTO.getTitle(),
                movieDTO.getGenre(),
                movieDTO.getReleaseDate(),
                movieDTO.getDuration()
        );
    }

    public static MovieResponseDTO toMovieResponseDTOFromMovie(Movie movieModel) {
        return new MovieResponseDTO(
                movieModel.getTitle(),
                movieModel.getDirector().getId()
        );
    }
}
