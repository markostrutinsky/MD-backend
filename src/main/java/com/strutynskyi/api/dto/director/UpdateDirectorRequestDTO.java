package com.strutynskyi.api.dto.director;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
public class UpdateDirectorRequestDTO {
    @NotBlank(message = "firstName: filled incorrectly. It cannot consist only of spaces or be left unfilled.")
    @Size(min = 2, max = 50, message = "firstName: size must be between 2 and 50")
    private String firstName;


    @NotBlank(message = "lastName: filled incorrectly. It cannot consist only of spaces or be left unfilled.")
    @Size(min = 2, max = 50, message = "lastName: size must be between 2 and 50")
    private String lastName;


    private String biography;


    @NotNull(message = "birthDate: cannot be left unfilled.")
    @Past(message = "birthDate: must be in the past.")
    private LocalDate birthDate;


    @JsonCreator
    public UpdateDirectorRequestDTO(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("biography") String biography,
            @JsonProperty("birthDate") LocalDate birthDate
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
        this.birthDate = birthDate;
    }
}
