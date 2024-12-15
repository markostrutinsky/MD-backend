package com.strutynskyi.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = "director")
public class Movie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String genre;
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double rating;
    private LocalDate released;
    private Duration duration;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", nullable = false)
    private Director director;

    public Movie(String title, String genre, LocalDate released, Duration duration) {
        this.title = title;
        this.genre = genre;
        this.released = released;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(title.toLowerCase(), movie.title.toLowerCase())
                && Objects.equals(genre.toLowerCase(), movie.genre.toLowerCase())
                && Objects.equals(released, movie.released)
                && Objects.equals(duration, movie.duration)
                && Objects.equals(getDirector().getId(), movie.getDirector().getId());
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", releaseDate=" + released +
                ", duration=" + duration +
                ", imageName='" + imageName + '\'' +
                ", director=" + director +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, genre, released, duration, getDirector().getId());
    }
}
