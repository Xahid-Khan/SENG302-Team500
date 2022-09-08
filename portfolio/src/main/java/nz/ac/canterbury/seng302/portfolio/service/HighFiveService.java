package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.repository.CommentModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HighFiveService {
  @Autowired
  private CommentModelRepository commentRepository;

  public void processHighFive(int userId) {
    //TODO
  }

  public boolean addHighFive(int userId) {
    //TODO
    return false;
  }

  public boolean removeHighFive(int userId) {
    //TODO
    return false;
  }
}
