package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    @NotNull
    private int id;
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @NotNull
    private long duration;

    private Set<Integer> likes = new HashSet<>();

    public int getAmountFilmLikes() {
        return likes.size();
    }
}
