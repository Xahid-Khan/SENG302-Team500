package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {
    public ProjectEntity toEntity(ProjectContract contract) {
        return new ProjectEntity(
                contract.name(),
                contract.description(),
                contract.startDate(),
                contract.endDate()
        );
    }

    public ProjectContract toContract(ProjectEntity entity) {
        return new ProjectContract(
                entity.getName(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate()
        );
    }

}
