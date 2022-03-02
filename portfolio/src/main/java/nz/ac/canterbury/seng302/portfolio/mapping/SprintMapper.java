package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;

public class SprintMapper {
    public SprintEntity toEntity(SprintContract contract, long orderNumber) {
        return new SprintEntity(
            orderNumber,
            contract.name(),
            contract.description(),
            contract.startDate(),
            contract.endDate()
        );
    }

    public SprintContract toContract(SprintEntity entity) {
        return new SprintContract(
            entity.getProject().getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getOrderNumber()
        );
    }
}
