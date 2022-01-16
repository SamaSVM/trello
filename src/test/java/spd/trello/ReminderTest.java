package spd.trello;

import org.junit.jupiter.api.Test;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.ReminderRepository;
import spd.trello.services.ReminderService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static spd.trello.Helper.*;

public class ReminderTest extends BaseTest {
    public ReminderTest() {
        service = new ReminderService(new ReminderRepository(dataSource));
    }

    private final ReminderService service;

    @Test
    public void successCreate() {
        User user = getNewUser("successCreate@RT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Reminder testReminder = service.create(
                member,
                card.getId(),
                Date.valueOf(LocalDate.of(2222, 1, 1)),
                Date.valueOf(LocalDate.of(2222, 1, 1))
        );
        assertNotNull(testReminder);
        assertAll(
                () -> assertEquals("successCreate@RT", testReminder.getCreatedBy()),
                () -> assertNull(testReminder.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getCreatedDate()),
                () -> assertNull(testReminder.getUpdatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getStart()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 1, 1)), testReminder.getEnd()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 1, 1)), testReminder.getRemindOn()),
                () -> assertTrue(testReminder.getActive()),
                () -> assertEquals(card.getId(), testReminder.getCardId())
        );
    }

    @Test
    public void findAll() {
        User user = getNewUser("findAll@RT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Reminder testFirstReminder = service.create(
                member,
                card.getId(),
                Date.valueOf(LocalDate.of(2222, 1, 1)),
                Date.valueOf(LocalDate.of(2222, 1, 1))
        );
        Reminder testSecondReminder = service.create(
                member,
                card.getId(),
                Date.valueOf(LocalDate.of(2222, 2, 2)),
                Date.valueOf(LocalDate.of(2222, 2, 2))
        );
        assertNotNull(testFirstReminder);
        assertNotNull(testSecondReminder);
        List<Reminder> testComments = service.findAll();
        assertAll(
                () -> assertTrue(testComments.contains(testFirstReminder)),
                () -> assertTrue(testComments.contains(testSecondReminder))
        );
    }

    @Test
    public void createFailure() {
        User user = getNewUser("test26@mail");
        Member member = getNewMember(user);
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.create(member,
                        null,
                        Date.valueOf(LocalDate.of(2222, 1, 1)),
                        Date.valueOf(LocalDate.of(2222, 1, 1))),
                "expected to throw  IllegalStateException, but it didn't"
        );
        assertEquals("Reminder doesn't creates", ex.getMessage());
    }

    @Test
    public void findByIdFailure() {
        UUID uuid = UUID.randomUUID();
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.findById(uuid),
                "no exception"
        );
        assertEquals("Reminder with ID: " + uuid + " doesn't exists", ex.getMessage());
    }

    @Test
    public void delete() {
        User user = getNewUser("test27@mail");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Reminder testReminder = service.create(
                member,
                card.getId(),
                Date.valueOf(LocalDate.of(2222, 1, 1)),
                Date.valueOf(LocalDate.of(2222, 1, 1))
        );
        assertNotNull(testReminder);
        UUID id = testReminder.getId();
        assertAll(
                () -> assertTrue(service.delete(id)),
                () -> assertFalse(service.delete(id))
        );
    }

    @Test
    public void update() {
        User user = getNewUser("update@RT");
        Member member = getNewMember(user);
        Workspace workspace = getNewWorkspace(member);
        Board board = getNewBoard(member, workspace.getId());
        CardList cardList = getNewCardList(member, board.getId());
        Card card = getNewCard(member, cardList.getId());
        Reminder reminder = service.create(member,
                card.getId(),
                Date.valueOf(LocalDate.of(2222, 1, 1)),
                Date.valueOf(LocalDate.of(2222, 1, 1)));
        assertNotNull(reminder);
        reminder.setEnd(Date.valueOf(LocalDate.of(2222, 2, 2)));
        reminder.setRemindOn(Date.valueOf(LocalDate.of(2222, 3, 3)));
        reminder.setActive(false);
        Reminder testReminder = service.update(member, reminder);
        assertAll(
                () -> assertEquals("update@RT", testReminder.getCreatedBy()),
                () -> assertEquals("update@RT", testReminder.getUpdatedBy()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getCreatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getUpdatedDate()),
                () -> assertEquals(Date.valueOf(LocalDate.now()), testReminder.getStart()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 2, 2)), testReminder.getEnd()),
                () -> assertEquals(Date.valueOf(LocalDate.of(2222, 3, 3)), testReminder.getRemindOn()),
                () -> assertFalse(testReminder.getActive())
        );
    }

    @Test
    public void updateFailure() {
        Member member = new Member();
        member.setMemberRole(MemberRole.ADMIN);
        member.setCreatedBy("user@");
        Reminder testReminder = new Reminder();
        testReminder.setId(UUID.fromString("e3aa391f-2192-4f2a-bf6e-a235459e78e5"));
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> service.update(member, testReminder),
                "expected to throw Illegal state exception, but it didn't"
        );
        assertEquals("Reminder with ID: e3aa391f-2192-4f2a-bf6e-a235459e78e5 doesn't exists", ex.getMessage());
    }
}
