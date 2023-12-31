package dev.bolohonov.filmorate.service;

import dev.bolohonov.filmorate.enums.EventType;
import dev.bolohonov.filmorate.enums.OperationType;
import dev.bolohonov.filmorate.exceptions.UserNotFoundException;
import dev.bolohonov.filmorate.exceptions.ValidationException;
import dev.bolohonov.filmorate.model.Film;
import dev.bolohonov.filmorate.model.User;
import dev.bolohonov.filmorate.storage.EventStorage;
import dev.bolohonov.filmorate.storage.FriendsStorage;
import dev.bolohonov.filmorate.storage.LikesStorage;
import dev.bolohonov.filmorate.storage.UserStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final EventStorage eventStorage;
    private final LikesStorage likesStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendsStorage friendsStorage,
                       EventStorage eventStorage, LikesStorage likesStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.eventStorage = eventStorage;
        this.likesStorage = likesStorage;
  
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        if (validateUser(user) && validateEmailNotDuplicated(user)) {
            userStorage.addUser(user);
            log.info("User has been added");
        }
        return user;
    }

    public Optional<User> getUserById(Integer userId) {
        return userStorage.findUserById(userId);
    }

    public Optional<User> updateUser(User user) {
        this.getUserById(user.getId());
        if (validateUser(user)) {
            userStorage.updateUser(user);
            log.info("User has been updated");
        }
        return Optional.of(user);
    }

    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
        log.info("User with ID {} has been deleted", userId);
    }

    public User addToFriends(User user, Integer friendId) {
        if (!userStorage.findUserById(friendId).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (friendsStorage.addToFriends(user.getId(), friendId)) {
            eventStorage.addEvent(user.getId(), friendId, EventType.FRIEND, OperationType.ADD);
            log.info("User with ID {} and ID {} is friends now", friendId, user.getId());
        } else {
            log.info("User with ID {} and ID {} is NOT friends yet", friendId, user.getId());
        }
        return user;
    }

    public User removeFriend(User user, Integer friendId) {
        friendsStorage.removeFriend(user.getId(), friendId);
        eventStorage.addEvent(user.getId(), friendId, EventType.FRIEND, OperationType.REMOVE);
        log.info("User with ID {} and ID {} is NOT friends now", friendId, user.getId());
        return user;
    }

    public Collection<User> getUserFriends(Integer userId) {
        log.info("User with ID {} get friends", userId);
        return friendsStorage.getUserFriends(userId);
    }

    public Collection<User> getMatchingFriends(Integer id, Integer otherId) {
        log.info("User with ID {} get matching friends with user {}", id, otherId);
        return friendsStorage.getMatchingFriends(id, otherId);
    }

    private static boolean validateUser(User user) {
        if (user.getLogin().contains(" ")) {
            log.warn("Whitespaces in login");
            throw new ValidationException("Логин не может содержать пробелы.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Name is blank");
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("BirthDate is incorrect");
            throw new ValidationException("Указана неверная дата рождения.");
        }
        return true;
    }

    private boolean validateEmailNotDuplicated(User user) {
        for (User u : userStorage.getUsers()) {
            if (u.getEmail().equals(user.getEmail())) {
                log.warn("Duplicated email");
                throw new ValidationException(String.format("Пользователь с электронной почтой %s" +
                        " уже зарегистрирован.", user.getEmail()));
            }
        }
        return true;
    }

    public Collection<Film> getRecommendations(Integer userId) {
        if (!userStorage.findUserById(userId).isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return likesStorage.getRecommendations(userId);
    }
}
