package nz.ac.canterbury.seng302.portfolio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import nz.ac.canterbury.seng302.portfolio.model.entity.GroupRepositoryRepository;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.repository.PostModelRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Tests for the PostService class
 */
@SpringBootTest
public class PostServiceTest {

  @InjectMocks
  private PostService postService;

  @Mock
  private PostModelRepository postModelRepository;

  private final int groupId = 1;

  private final PostModel post1 = new PostModel(1, 1, "Post 1");
  private final PostModel post2 = new PostModel(2, 1, "Post 2");
  private final PostModel post3 = new PostModel(3, 1, "Post 3");
  private final PostModel post4 = new PostModel(4, 1, "Post 4");

  /**
   * Tests getting paginated posts for a group
   */
  @Test
  void getValidGroupFeedExpectPass(){
    int offset = 0;
    int limit = 2;

    Page<PostModel> postModelPage = Mockito.mock(Page.class);

    Pageable buildRequest = PageRequest.of(offset,limit);

    Mockito.when(postModelRepository.getPaginatedPostsByGroupId(groupId,buildRequest)).thenReturn(postModelPage);

    Page<PostModel> result = postService.getPaginatedPostsForGroup(groupId,offset,limit);

    assertEquals(postModelPage,result);
  }
}
