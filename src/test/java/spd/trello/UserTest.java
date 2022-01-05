package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.User;
import spd.trello.repository.UserRepository;
import spd.trello.services.UserService;


import java.time.ZoneId;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest extends BaseTest {

    private final UserService service;

    public UserTest() {
        service = new UserService(new UserRepository(dataSource));
    }

    @Test
    public void successCreate() {
        User testUser = service.create("createFirstName", "createLastName", "create@email");
        assertNotNull(testUser);
        assertAll(
                () -> assertEquals("createFirstName", testUser.getFirstName()),
                () -> assertEquals("createLastName", testUser.getLastName()),
                () -> assertEquals("create@email", testUser.getEmail()),
                () -> assertEquals(ZoneId.systemDefault().toString(), testUser.getTimeZone())
        );
        service.delete(testUser.getId());
    }

    @Test
    public void createFailure() {
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create("createFirstName", "createLastName", null),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("User doesn't creates", ex.getMessage());
    }

    @Test
    public void testFindById() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("User with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void testDelete() {
        User testUser = service.create("deleteFirstName", "deleteLastName", "delete@email");
        assertNotNull(testUser);
        UUID id = testUser.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void testUpdate() {
        User testUser = service.create("updateFirstName", "updateLastName", "update@email");
        assertNotNull(testUser);
        UUID id = testUser.getId();
        User updateUser = new User();

        updateUser.setId(testUser.getId());
        updateUser.setFirstName("newFirstName");
        service.update(updateUser);
        assertAll(
                () -> assertEquals("newFirstName", service.findById(id).getFirstName())
        );
        updateUser.setFirstName(null);

        updateUser.setLastName("newLastName");
        service.update(updateUser);
        assertAll(
                () -> assertEquals("newFirstName", service.findById(id).getFirstName()),
                () -> assertEquals("newLastName", service.findById(id).getLastName())
        );
        updateUser.setLastName(null);

        updateUser.setEmail("new@email");
        service.update(updateUser);
        assertAll(
                () -> assertEquals("newFirstName", service.findById(id).getFirstName()),
                () -> assertEquals("newLastName", service.findById(id).getLastName()),
                () -> assertEquals("new@email", service.findById(id).getEmail())
        );
        updateUser.setEmail(null);

        updateUser.setTimeZone("Europe/Paris");
        service.update(updateUser);
        assertAll(
                () -> assertEquals("newFirstName", service.findById(id).getFirstName()),
                () -> assertEquals("newLastName", service.findById(id).getLastName()),
                () -> assertEquals("new@email", service.findById(id).getEmail()),
                () -> assertEquals("Europe/Paris", service.findById(id).getTimeZone())
        );
        service.delete(testUser.getId());
    }

    @Test
    public void updateFailure() {
        User testUser = new User();
        testUser.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(testUser),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("User with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
