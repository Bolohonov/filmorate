package dev.bolohonov.filmorate.storage;

import dev.bolohonov.filmorate.exceptions.UserNotFoundException;
import dev.bolohonov.filmorate.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_SELECT =
            "select id, name, login, email, birthday from user_filmorate";
    private static final String SQL_INSERT =
            "insert into user_filmorate (name, login, email, birthday) " +
            "values (?, ?, ?, ?)";
    private static final String SQL_DELETE =
            "delete from user_filmorate where id = ?";
    private static final String SQL_UPDATE =
            "update user_filmorate set name = ?, login = ?, email = ?, birthday = ? where id = ?";
    private static final String SQL_SELECT_FIND_USER =
            "select id, name, login, email, birthday " +
                    "from user_filmorate where id = ?";


    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getUsers() {
        return jdbcTemplate.query(SQL_SELECT, this::mapRowToUser);
    }

    @Override
    public User addUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(SQL_INSERT, new String[]{"id"});
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getLogin());
                stmt.setString(3, user.getEmail());
                stmt.setDate(4, Date.valueOf(user.getBirthday()));
                return stmt;
            }, keyHolder);
            user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        jdbcTemplate.update(SQL_DELETE, id);
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(SQL_UPDATE,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId());
        return user;
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        Optional<User> user;
        try {
            user = Optional.ofNullable(jdbcTemplate.queryForObject(SQL_SELECT_FIND_USER, this::mapRowToUser, id));
        } catch (Exception exp) {
            log.warn("Пользователь с id {} не найден", id);
            throw new UserNotFoundException(exp.getMessage());
        }
        return user;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
