package com.strutynskyi.api.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class Director implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(columnDefinition = "TEXT")
    private String biography;
    private LocalDate birthDate;

    private String imageName;
    private String imageType;
    @Lob
    private byte[] imageData;

    private Integer orderIndex;
    @OneToMany(mappedBy = "director", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Movie> movies = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void updateOrderIndex() {
        if (this.orderIndex == null && this.id != null) {
            this.orderIndex = Math.toIntExact(this.id);
        }
    }

    public Director(String firstName, String lastName, String biography, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.biography = biography;
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return "Director{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", biography='" + biography + '\'' +
                ", birthDate=" + birthDate +
                ", imageName='" + imageName + '\'' +
                ", movies=" + movies +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Director director = (Director) o;
        return Objects.equals(firstName.toLowerCase(), director.firstName.toLowerCase())
                && Objects.equals(lastName.toLowerCase(), director.lastName.toLowerCase())
                && Objects.equals(birthDate, director.birthDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName.toLowerCase(), lastName.toLowerCase(), birthDate);
    }
}
