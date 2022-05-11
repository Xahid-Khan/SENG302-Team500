package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseEventContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.EventContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.EventEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for events from JSON to entity and vice versa.
 */
@Component
public class EventMapper {
    /**
     * This function converts the received JSON body to the event Entity.
     * @param contract a JSON data received via HTTP body.
     * @return      an event entity
     */
    public EventEntity toEntity(BaseEventContract contract){
        return new EventEntity(
                contract.name(),
                contract.description(),
                contract.startDate(),
                contract.endDate(),
                contract.type()
        );
    }
    /**
     * This method receives an event Entity and converts that entity into transferable JSON data type,
     * while doing so it also retrieves all and events related to that event.
     * @param entity a event entity that is retried from database
     * @return      returns a event and related events in JSON data type.
     */
    public EventContract toContract(EventEntity entity) {
            return new EventContract(
                    entity.getProject().getId(),
                    entity.getId(),
                    entity.getName(),
                    entity.getDescription(),
                    entity.getStartDate(),
                    entity.getEndDate(),
                    entity.getType()
            );
        }
}
