package nz.ac.canterbury.seng302.portfolio.mapping;

import java.util.ArrayList;

import nz.ac.canterbury.seng302.portfolio.model.contract.BaseProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.ProjectContract;
import nz.ac.canterbury.seng302.portfolio.model.contract.SprintContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    @Autowired
    private SprintMapper sprintMapper;

    public ProjectEntity toEntity(BaseProjectContract contract) {
        return new ProjectEntity(
                contract.name(),
                contract.description(),
                contract.startDate(),
                contract.endDate()
        );
    }

    public ProjectContract toContract(ProjectEntity entity) {
        var sprintEntities = entity.getSprints();
        var sprintContracts = new ArrayList<SprintContract>();

        for (int i=0; i < sprintEntities.size(); i++) {
            sprintContracts.add(sprintMapper.toContract(
                sprintEntities.get(i),
                i+1
            ));
        }

        return new ProjectContract(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getStartDate(),
                entity.getEndDate(),
                sprintContracts
        );
    }

}
