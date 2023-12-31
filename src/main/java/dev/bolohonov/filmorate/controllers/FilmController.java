package dev.bolohonov.filmorate.controllers;

import dev.bolohonov.filmorate.model.Film;
import dev.bolohonov.filmorate.service.FilmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Get all films");
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public Optional<Film> findFilm(@PathVariable("filmId") Integer filmId) {
        log.info("Get film with ID {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilm(@PathVariable("filmId") Integer filmId) {
        filmService.deleteFilm(filmId);
        log.info("Delete film with ID {}", filmId);
    }

    @PostMapping
    public Film post(@Valid @RequestBody Film film) {
        log.info("Add new film");
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) {
        log.info("Update film");
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Optional<Film> addLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Add like to film with ID {}", id);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Optional<Film> removeLike(@PathVariable("id") Integer id, @PathVariable("userId") Integer userId) {
        log.info("Remove like from film with ID {}", id);
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getFilmsByLikes(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count,
            @RequestParam(value = "genreId", defaultValue = "0", required = false) Integer genreId,
            @RequestParam(value = "year", defaultValue = "0", required = false) Integer year) {
        if (count <= 0 || genreId < 0 || year < 0) {
            throw new IllegalArgumentException();
        }
        log.info("Get {} most popular films", count);
        return filmService.getFilmsByLikes(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(OK)
    public List<Film> getFilmsByDirectorSortedByLikeOrYear(@PathVariable Integer directorId,
                                                           @RequestParam String sortBy) {
        try {
            return filmService.getFilmsByDirectorSortedByLikeOrYear(directorId, sortBy);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST);
        }
    }


    @GetMapping("/search")
    public Collection<Film> search(@RequestParam(value = "query") String query,
                                   @RequestParam(value = "by", defaultValue = "title") String by) {
        log.info("Search films");
        return filmService.search(query, by);
    }

    @GetMapping("/common") //common?userId={userId}&friendId={friendId}
    @ResponseStatus(OK)
    public List<Film> getCommonFilms (@RequestParam Integer userId,
                                      @RequestParam Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);

    }
}
