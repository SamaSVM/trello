package spd.trello.services;

import spd.trello.db.ConnectionPool;
import spd.trello.domain.*;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CardService extends AbstractService<Card> {
    public CardService(InterfaceRepository<Card> repository) {
        super(repository);
    }

    private final MemberCardService memberCardService =
            new MemberCardService(new MemberCardRepository(ConnectionPool.createDataSource()));

    private final CommentCardService commentCardService =
            new CommentCardService(new CommentCardRepository(ConnectionPool.createDataSource()));

    private final ReminderCardService reminderCardService =
            new ReminderCardService(new ReminderCardRepository(ConnectionPool.createDataSource()));

    private final ChecklistCardService checklistCardService =
            new ChecklistCardService(new ChecklistCardRepository(ConnectionPool.createDataSource()));

    public Card create(Member member, UUID cardListId, String name, String description) {
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setCreatedBy(member.getCreatedBy());
        card.setCreatedDate(Date.valueOf(LocalDate.now()));
        card.setName(name);
        card.setDescription(description);
        card.setCardListId(cardListId);
        repository.create(card);
        if (!memberCardService.create(member.getId(), card.getId())) {
            delete(card.getId());
        }
        return repository.findById(card.getId());
    }

    public Card update(Member member, Card entity) {
        checkMember(member, entity.getId());
        Card oldCard = repository.findById(entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldCard.getName());
        }
        if (entity.getDescription() == null && oldCard.getDescription() != null) {
            entity.setDescription(oldCard.getDescription());
        }
        if (entity.getArchived() == null) {
            entity.setArchived(oldCard.getArchived());
        }
        return repository.update(entity);
    }

    @Override
    public boolean delete(UUID id) {
        memberCardService.deleteAllMembersForCard(id);
        return repository.delete(id);
    }

    public boolean addMember(Member member, UUID newMemberId, UUID cardId) {
        checkMember(member, cardId);
        return memberCardService.create(newMemberId, cardId);
    }

    public boolean deleteMember(Member member, UUID memberId, UUID cardId) {
        checkMember(member, cardId);
        return memberCardService.delete(memberId, cardId);
    }

    public List<Member> getAllMembers(Member member, UUID cardId) {
        checkMember(member, cardId);
        return memberCardService.findMembersByCardId(cardId);
    }

    public List<Comment> getAllComments(Member member, UUID cardId) {
        checkMember(member, cardId);
        return commentCardService.getAllCommentsForCard(cardId);
    }

    public List<Reminder> getAllReminders(Member member, UUID cardId) {
        checkMember(member, cardId);
        return reminderCardService.getAllCommentsForCard(cardId);
    }

    public List<Checklist> getAllChecklists(Member member, UUID cardId) {
        checkMember(member, cardId);
        return checklistCardService.getAllChecklistsForCard(cardId);
    }

    private void checkMember(Member member, UUID cardId) {
        if (member.getMemberRole() == MemberRole.GUEST ||
                !memberCardService.findByIds(member.getId(), cardId)) {
            throw new IllegalStateException("This member cannot update card!");
        }
    }
}
