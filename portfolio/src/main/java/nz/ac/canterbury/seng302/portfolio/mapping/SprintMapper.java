package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseSprintContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.EventContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.SprintEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Mapper for sprints from JSON to entity and vice versa.
 */
@Component
public class SprintMapper {

    @Autowired
    private EventMapper eventMapper;

    /**
     * This function converts the received JSON body to the sprint Entity.
     * @param contract a JSON data received via HTTP body.
     * @return      a sprint entity
     */
    public SprintEntity toEntity(BaseSprintContract contract) {
        return new SprintEntity(
            contract.name(),
            contract.description(),
            contract.startDate(),
            contract.endDate()
        );
    }
    /**
     * This method receives a sprint Entity and converts that entity into transferable JSON data type,
     * while doing so it also retrieves all and events related to that sprint.
     * @param entity a sprint entity that is retried from database
     * @return      returns a sprint and related sprints in JSON data type.
     */
    public SprintContract toContract(SprintEntity entity, long orderNumber) {
        var eventEntities = entity.getEvents();
        var eventContracts = new ArrayList<EventContract>();

        for (int i=0; i < eventEntities.size(); i++) {
            eventContracts.add(eventMapper.toContract(
                    eventEntities.get(i)
            ));
        }
        return new SprintContract(
            entity.getProject().getId(),
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStartDate(),
            entity.getEndDate(),
            eventContracts,
            orderNumber
        );
    }
}
