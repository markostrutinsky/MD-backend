package com.strutynskyi.api.dto.movie;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@ToString
public class MovieResponseDTO {
    private String title;
    private DirectorDTO director;


    @JsonCreator
    public MovieResponseDTO(
            @JsonProperty("title") String title,
            @JsonProperty("directorId") Long directorId
    ) {
        this.title = title;
        this.director = new DirectorDTO(directorId);
    }


    @RequiredArgsConstructor
    private static class DirectorDTO {
        @JsonProperty("id")
        private final Long id;
    }
}
