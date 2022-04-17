package spd.trello.validators;

import org.springframework.stereotype.Component;
import spd.trello.domain.Label;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.CardRepository;
import spd.trello.repository.LabelRepository;

@Component
public class LabelValidator extends AbstractValidator<Label> {
    private final CardRepository cardRepository;
    private final LabelRepository labelRepository;

    public LabelValidator(CardRepository cardRepository, LabelRepository labelRepository) {
        this.cardRepository = cardRepository;
        this.labelRepository = labelRepository;
    }

    @Override
    public void validateSaveEntity(Label entity) {
        if (!cardRepository.existsById(entity.getCardId())) {
            throw new BadRequestException("The cardId field must belong to a card.");
        }
    }

    @Override
    public void validateUpdateEntity(Label entity) {
        var oldLabel = labelRepository.findById(entity.getId());
        if (oldLabel.isEmpty()) {
            throw new BadRequestException("Cannot update non-existent card!");
        }
        if(!oldLabel.get().getCardId().equals(entity.getCardId())){
            throw new BadRequestException("Label cannot be transferred to another card.");
        }
    }
}
