package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    @Autowired
    private SprintMapper sprintMapper;

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
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getSprints().stream().map(sprint -> sprintMapper.toContract(sprint)).collect(Collectors.toList())
        );
    }

}
