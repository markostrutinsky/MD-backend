package com.strutynskyi.api.mappers;

import com.strutynskyi.api.dto.director.CreateDirectorRequestDTO;
import com.strutynskyi.api.dto.director.DirectorDTO;
import com.strutynskyi.api.dto.director.UpdateDirectorRequestDTO;
import com.strutynskyi.api.dto.movie.MovieDTO;
import com.strutynskyi.api.models.Director;
import org.springframework.stereotype.Component;

@Component
public class DirectorMappers {
    public static DirectorDTO toDirectorDTO(Director directorModel) {
        return new DirectorDTO(
                directorModel.getId(),
                directorModel.getFirstName(),
                directorModel.getLastName(),
                directorModel.getBiography(),
                directorModel.getBirthDate(),
                directorModel.getMovies()
                        .stream()
                        .map(MovieMappers::toMovieDTO)
                        .map(MovieDTO::getId)
                        .toList()
        );
    }

    public static Director toDirectorFromCreateDTO(CreateDirectorRequestDTO directorDTO) {
        return new Director(
                directorDTO.getFirstName(),
                directorDTO.getLastName(),
                directorDTO.getBiography(),
                directorDTO.getBirthDate()
        );
    }

    public static Director toDirectorFromUpdateDTO(UpdateDirectorRequestDTO directorDTO) {
        return new Director(
                directorDTO.getFirstName(),
                directorDTO.getLastName(),
                directorDTO.getBiography(),
                directorDTO.getBirthDate()
        );
    }
}
