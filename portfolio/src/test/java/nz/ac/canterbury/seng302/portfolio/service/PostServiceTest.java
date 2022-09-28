package nz.ac.canterbury.seng302.portfolio.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
import nz.ac.canterbury.seng302.portfolio.repository.PostRepository;
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
  private PostRepository postRepository;

  private final int groupId = 1;

  private final PostEntity post1 = new PostEntity(1, 1, "Post 1");
  private final PostEntity post2 = new PostEntity(2, 1, "Post 2");
  private final PostEntity post3 = new PostEntity(3, 1, "Post 3");
  private final PostEntity post4 = new PostEntity(4, 1, "Post 4");

  /**
   * Tests getting paginated posts for a group
   */
  @Test
  void getValidGroupFeedExpectPass(){
    int offset = 0;
    int limit = 2;

    Page<PostEntity> postModelPage = Mockito.mock(Page.class);

    Pageable buildRequest = PageRequest.of(offset,limit);

    Mockito.when(postRepository.getPaginatedPostsByGroupId(groupId,buildRequest)).thenReturn(postModelPage);

    Page<PostEntity> result = postService.getPaginatedPostsForGroup(groupId,offset,limit);

    assertEquals(postModelPage,result);
  }
}
