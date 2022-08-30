package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModelRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GroupFeedServiceTest {

    @InjectMocks
    private PostService postService;
    @Mock
    private PostModelRepository mockPostModelRepository;

    private PostModel newPost;
    private PostModel newPost1;
    private PostModel newPost2;
    private PostModel newPost3;
    private List<PostModel> postList;

    @BeforeEach
    public void setup() {
        mockPostModelRepository.deleteAll();
        newPost = new PostModel(1, 1, "A test Post by User 1");
        newPost1 = new PostModel(1, 2, "A test Post by User 2");
        newPost2 = new PostModel(2, 1, "A test Post by User 1 Again");
        newPost3 = new PostModel(3, 5, "A test Post by User 5");

        postList = new ArrayList<PostModel>();
        postList.add(newPost);
        postList.add(newPost1);
        postList.add(newPost2);
        postList.add(newPost3);

        postList.forEach(model -> {
            mockPostModelRepository.save(model);
        });
    }

    @Test
    public void getAllPostsForGivenGroupId() throws Exception {

        Mockito.when(mockPostModelRepository.findPostModelByGroupId(1)).thenReturn(postList);

        var result = postService.getAllPostsForAGroup(1);
        Assertions.assertTrue(result.size() > 0);
        for (int i=0; i<result.size(); i++) {
            Assertions.assertEquals(postList.get(i).getId(), result.get(i).getId());
            Assertions.assertEquals(postList.get(i).getGroupId(), result.get(i).getGroupId());
            Assertions.assertEquals(postList.get(i).getPostContent(), result.get(i).getPostContent());
        };
    }

    @Test
    public void deleteAPostWithTheFollowingPostIdSuccessfull () throws Exception {
        int postId = (1);
//        Mockito.when(mockPostModelRepository.deleteById(postId)).then(postList.remove(1));
    }
}
