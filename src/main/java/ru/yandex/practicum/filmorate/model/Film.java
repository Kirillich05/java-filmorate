package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Film {

    @PositiveOrZero
    int id;
    @NotNull
    String name;
    @NotNull
    String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    LocalDate releaseDate;
    @NotNull
    long duration;
    @NotNull
    Mpa mpa;
    Set<Integer> likes = new HashSet<>();
    LinkedHashSet<Genre> genres;

    public int getAmountFilmLikes() {
        return likes.size();
    }
}
