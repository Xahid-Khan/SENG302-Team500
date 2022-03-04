package nz.ac.canterbury.seng302.portfolio.mapping;

import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    @Autowired
    private SprintMapper sprintMapper;

    public ProjectContract toEntity(nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract contract) {
        return new ProjectContract(
                contract.name(),
                contract.description(),
                contract.startDate(),
                contract.endDate()
        );
    }

    public nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract toContract(ProjectContract entity) {
        return new nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getSprints().stream().map(sprint -> sprintMapper.toContract(sprint)).collect(Collectors.toList())
        );
    }

}
