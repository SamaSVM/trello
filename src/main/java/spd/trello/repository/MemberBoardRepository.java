package spd.trello.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import spd.trello.domain.Member;
import spd.trello.services.MemberService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MemberBoardRepository {
    public MemberBoardRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    @Autowired
    private MemberService memberService;

    private final String CREATE_STMT = "INSERT INTO member_board (member_id, board_id) VALUES (?, ?);";

    private final String DELETE_BY_BOARD_ID_STMT = "DELETE FROM member_board WHERE board_id=?;";

    private final String DELETE_STMT = "DELETE FROM member_board WHERE (member_id=? AND board_id=?);";

    private final String FIND_BY_IDS_STMT = "SELECT * FROM member_board WHERE (member_id=? AND board_id=?);";

    private final String FIND_BY_BOARD_ID_STMT = "SELECT * FROM member_board WHERE board_id=?;";

    public boolean findByIds(UUID memberId, UUID boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_IDS_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, boardId);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new IllegalStateException("Member-Board link impossible to find!");
        }
    }

    public List<Member> findMembersByBoardId(UUID boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_BOARD_ID_STMT)) {
            List<Member> result = new ArrayList<>();
            statement.setObject(1, boardId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(memberService.findById(UUID.fromString(resultSet.getString("member_id"))));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("MemberBoardRepository::findByWorkspaceId failed", e);
        }
        throw new IllegalStateException("Board with ID: " + boardId.toString() + " doesn't exists");
    }

    public boolean create(UUID memberId, UUID boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(CREATE_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, boardId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Member-Board link doesn't creates");
        }
    }

    public boolean deleteAllMembersForBoard(UUID boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_BOARD_ID_STMT)) {
            statement.setObject(1, boardId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("BoardWorkspaceRepository::delete failed", e);
        }
    }

    public boolean delete(UUID memberId, UUID boardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_STMT)) {
            statement.setObject(1, memberId);
            statement.setObject(2, boardId);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("BoardWorkspaceRepository::delete failed", e);
        }
    }
}
