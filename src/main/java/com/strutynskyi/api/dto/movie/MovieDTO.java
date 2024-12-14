package com.strutynskyi.api.dto.movie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.LocalDate;

@Data
@ToString
public class MovieDTO {
    private Long id;
    private String title;
    private String genre;
    private LocalDate releaseDate;
    private Duration duration;
    private DirectorDTO director;

    public MovieDTO(
            Long id,
            String title,
            String genre,
            LocalDate releaseDate,
            Duration duration,
            Long directorId
    ) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.director = new DirectorDTO(directorId);
    }

    @RequiredArgsConstructor
    private static class DirectorDTO{
        @JsonProperty("id")
        private final Long id;
    }
}
