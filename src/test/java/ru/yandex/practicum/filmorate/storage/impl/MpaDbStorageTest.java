package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    void getAllMpa() {
        assertNotNull(mpaDbStorage.getAllMpa());
        assertEquals(5, mpaDbStorage.getAllMpa().size());
    }

    @Test
    void getMpa() {
        Mpa mpa = Mpa.builder()
                .name("NC-17")
                .build();

        String nameMpa = mpa.getName();
        assertEquals(nameMpa, mpaDbStorage.getMpa(5).get().getName());
    }
}