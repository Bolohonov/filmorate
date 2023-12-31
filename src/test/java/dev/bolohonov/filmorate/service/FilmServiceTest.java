package dev.bolohonov.filmorate.service;

import dev.bolohonov.filmorate.model.Director;
import dev.bolohonov.filmorate.model.Film;
import dev.bolohonov.filmorate.model.Mpa;
import dev.bolohonov.filmorate.model.User;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.time.Duration.ofSeconds;
import static java.time.LocalDate.of;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmServiceTest {

    private final FilmService filmService;
    private final UserService userService;
    private final DirectorService directorService;

    @Test
    void getFilmsByDirectorSortedByLike() {
        Director director = directorService.createDirector(Director.builder().name("Dir1").build());
        List<Film> filmList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            filmList.add(filmService.addFilm(Film.builder()
                    .name("Film" + i)
                    .description("Descr" + i)
                    .releaseDate(of(2021, 12, 2))
                    .duration(ofSeconds(1500))
                    .mpa(new Mpa(1, "G"))
                    .director(director)
                    .build()));
        }

        User user1 = User.builder().birthday(of(2000, 1, 1)).name("user1").login("login1")
                .email("email1@ya.ru").build();
        User user2 = User.builder().birthday(of(2000, 1, 1)).name("user2").login("login2")
                .email("email2@ya.ru").build();
        User user3 = User.builder().birthday(of(2000, 1, 1)).name("user3").login("login3")
                .email("email3@ya.ru").build();

        user1 = userService.addUser(user1);
        user2 = userService.addUser(user2);
        user3 = userService.addUser(user3);

        filmService.addLike(filmList.get(2).getId(), 1);
        filmService.addLike(filmList.get(1).getId(), 2);
        filmService.addLike(filmList.get(2).getId(), 3);
        filmService.addLike(filmList.get(2).getId(), 2);
        filmService.addLike(filmList.get(0).getId(), 2);
        filmService.addLike(filmList.get(0).getId(), 1);

        var sortedList = filmService.getFilmsByDirectorSortedByLikeOrYear(director.getId(), "likes");

        assertThat(sortedList.get(0).getId()).isEqualTo(filmList.get(2).getId());
        assertThat(sortedList.get(1).getId()).isEqualTo(filmList.get(0).getId());
        assertThat(sortedList.get(2).getId()).isEqualTo(filmList.get(1).getId());
    }

    @Test
    void getFilmsByDirectorSortedByYear() {
        Director director = directorService.createDirector(Director.builder().name("Dir1").build());
        List<Film> filmList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            filmList.add(filmService.addFilm(Film.builder()
                    .name("Film" + i)
                    .description("Descr" + i)
                    .releaseDate(of(1950 + i, 12, 2))
                    .duration(ofSeconds(1500))
                    .mpa(new Mpa(1, "G"))
                    .director(director)
                    .build()));
        }

        var sortedList = filmService.getFilmsByDirectorSortedByLikeOrYear(director.getId(), "year");
        sortedList.forEach(System.out::println);
        assertThat(sortedList.get(0).getId()).isEqualTo(filmList.get(filmList.size() - 1).getId());
        assertThat(sortedList.get(1).getId()).isEqualTo(filmList.get(filmList.size() - 2).getId());
        assertThat(sortedList.get(2).getId()).isEqualTo(filmList.get(filmList.size() - 3).getId());
    }

    @Test
    void test0getCommonFilmsIf2CommonFilmsExistingShouldReturn2FilmsFromList() {
        List<Film> filmList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            filmList.add(filmService.addFilm(Film.builder()
                    .name("Film" + i)
                    .description("Descr" + i)
                    .releaseDate(of(1950 + i, 12, 2))
                    .duration(ofSeconds(1500))
                    .mpa(new Mpa(1, "G"))
                    .build()));
        }
        User user1 = User.builder().birthday(LocalDate.of(2000, 1, 1)).name("user1").login("login1").email("email1@ya.ru").build();
        User user2 = User.builder().birthday(LocalDate.of(2000, 1, 1)).name("user2").login("login2").email("email2@ya.ru").build();
        User user3 = User.builder().birthday(LocalDate.of(2000, 1, 1)).name("user3").login("login3").email("email3@ya.ru").build();


        user1 = userService.addUser(user1);
        user2 = userService.addUser(user2);
        user3 = userService.addUser(user3);

        filmService.addLike(filmList.get(0).getId(), user1.getId());
        filmService.addLike(filmList.get(0).getId(), user2.getId());
        filmService.addLike(filmList.get(0).getId(), user3.getId());
        filmService.addLike(filmList.get(1).getId(), user1.getId());
        filmService.addLike(filmList.get(1).getId(), user2.getId());
        filmService.addLike(filmList.get(2).getId(), user1.getId());
        filmService.addLike(filmList.get(3).getId(), user2.getId());

        assertThat(filmService.getCommonFilms(user1.getId(), user2.getId())).hasSize(2);
    }

    @Test
    void test1getCommonFilmsIfCommonFilmsNotExistingShouldReturn0FilmsFromList() {
        List<Film> filmList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            filmList.add(filmService.addFilm(Film.builder()
                    .id(i)
                    .name("Film" + i)
                    .description("Descr" + i)
                    .releaseDate(of(2021, 12, 2))
                    .duration(ofSeconds(1500))
                    .mpa(new Mpa(1, "G"))
                    .build()));
        }
        User user1 = User.builder().birthday(LocalDate.of(2000, 1, 1)).name("user1").login("login1").email("email1@ya.ru").build();
        User user2 = User.builder().birthday(LocalDate.of(2000, 1, 1)).name("user2").login("login2").email("email2@ya.ru").build();
        User user3 = User.builder().birthday(LocalDate.of(2000, 1, 1)).name("user3").login("login3").email("email3@ya.ru").build();

        user1 = userService.addUser(user1);
        user2 = userService.addUser(user2);
        user3 = userService.addUser(user3);

        filmService.addLike(filmList.get(0).getId(), user1.getId());
        filmService.addLike(filmList.get(0).getId(), user2.getId());
        filmService.addLike(filmList.get(1).getId(), user1.getId());
        filmService.addLike(filmList.get(1).getId(), user2.getId());

        assertThat(filmService.getCommonFilms(user2.getId(), user3.getId())).hasSize(0);
    }
}