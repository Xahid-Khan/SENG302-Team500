package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;
import org.springframework.stereotype.Component;

@Component
public class SprintMapper {
    public SprintEntity toEntity(BaseSprintContract contract) {
        return new SprintEntity(
            contract.name(),
            contract.description(),
            contract.startDate(),
            contract.endDate(),
            contract.colour()
        );
    }

    public SprintContract toContract(SprintEntity entity, long orderNumber) {
        return new SprintContract(
            entity.getProject().getId(),
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getColour(),
            orderNumber
        );
    }
}
