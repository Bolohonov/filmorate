package dev.bolohonov.filmorate.controllers;

import dev.bolohonov.filmorate.model.Genre;
import dev.bolohonov.filmorate.service.FilmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("Get all genres");
        return filmService.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public Optional<Genre> findGenre(@PathVariable("genreId") Integer genreId) {
        log.info("Get genre with ID {}", genreId);
        return filmService.getGenreById(genreId);
    }
}
