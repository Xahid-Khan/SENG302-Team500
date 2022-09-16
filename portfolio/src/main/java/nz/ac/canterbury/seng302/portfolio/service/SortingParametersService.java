package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.GetPaginatedUsersOrderingElement;
import nz.ac.canterbury.seng302.portfolio.model.entity.SortingParameterEntity;
import nz.ac.canterbury.seng302.portfolio.repository.SortingParametersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SortingParametersService {

    @Autowired
    private SortingParametersRepository sortingRepository;

    public void saveSortingParams(int userId, String sortingParam, boolean reverseOrder) {
        SortingParameterEntity sortingData = new SortingParameterEntity(userId, sortingParam, reverseOrder);
        if (checkExistance(userId)) {
            System.out.println("Trying to Delete");
            sortingRepository.deleteById(userId);
        }
        System.out.println("Trying to save" + " " +  userId+ " " + sortingParam+ " " + reverseOrder);
        sortingRepository.save(sortingData);
    }

    public SortingParameterEntity getSortingParams(int userId) {
        try {
            SortingParameterEntity data = sortingRepository.findById(userId).get();
            return data;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    public boolean checkExistance(int userId) {
        return sortingRepository.existsById(userId);
    }

}
