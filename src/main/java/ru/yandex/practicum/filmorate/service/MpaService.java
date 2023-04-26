package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ModelNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaService {

    MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpa(int id) {
        log.info("Mpa with id {} received", id);
        return mpaStorage.getAllMpa().stream()
                .filter(x -> x.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
            log.warn("Mpa with id {} is not found", id);
                    return new ModelNotFoundException("Mpa with id {} is not found" + id);
        });
    }

    public List<Mpa> getAllMpa() {
        log.info("All mpa received");
        return mpaStorage.getAllMpa();
    }
}
