package spd.trello.domain;

import lombok.Data;
import spd.trello.domain.perent.Resource;

@Data
public class BoardTemplate extends Resource {
    private String name;

    @Override
    public String toString() {
        return "BoardTemplate{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCratedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                '}';
    }
}
