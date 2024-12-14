package com.strutynskyi.api.dto.director;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.strutynskyi.api.dto.movie.MovieDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@ToString
public class DirectorDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String biography;
    private LocalDate birthDate;
    private List<Long> moviesId;


    public DirectorDTO(
            Long id,
            String firstName,
            String lastName,
            String biography,
            LocalDate birthDate,
            List<Long> moviesId
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
        this.birthDate = birthDate;
        this.moviesId = moviesId;
    }
}
