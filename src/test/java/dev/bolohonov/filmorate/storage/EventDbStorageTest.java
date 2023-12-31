package dev.bolohonov.filmorate.storage;

import dev.bolohonov.filmorate.enums.EventType;
import dev.bolohonov.filmorate.enums.OperationType;
import dev.bolohonov.filmorate.model.Event;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventDbStorageTest {

    private final EventDbStorage eventDbStorage;

    @Test
    void getEventsForUserTest() {
        Collection<Event> events = eventDbStorage.getFeedForUser(1001);
        assertThat(events).hasSize(3);
    }

    @Test
    void addEventTest() {
        eventDbStorage.addEvent(1002, 1005, EventType.LIKE, OperationType.ADD);
        assertThat(eventDbStorage.getFeedForUser(1001)).hasSize(4);
    }

}
