package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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
    void getAllPostsExpectPass () throws Exception {
        Mockito.when(mockPostModelRepository.findAll()).thenReturn(postList);
        var result = postService.getAllPosts();
        for (int i=0; i<result.size(); i++) {
            Assertions.assertEquals(postList.get(i).getGroupId(), result.get(i).getGroupId());
            Assertions.assertEquals(postList.get(i).getUserId(), result.get(i).getUserId());
            Assertions.assertEquals(postList.get(i).getPostContent(), result.get(i).getPostContent());
        }
    }

    @Test
    void getAllPostsForGivenGroupId() throws Exception {
        int groupId = 1;
        var newPostList = postList.stream().filter(postModel -> {
                                            return postModel.getGroupId() == groupId;
                                        }).collect(Collectors.toList());
        Mockito.when(mockPostModelRepository.findPostModelByGroupId(groupId))
                .thenReturn(newPostList);

        var result = postService.getAllPostsForAGroup(groupId);
        Assertions.assertTrue(result.size() > 0);
        for (int i=0; i<result.size(); i++) {
            Assertions.assertEquals(newPostList.get(i).getId(), result.get(i).getId());
            Assertions.assertEquals(newPostList.get(i).getGroupId(), result.get(i).getGroupId());
            Assertions.assertEquals(newPostList.get(i).getPostContent(), result.get(i).getPostContent());
        };
    }

    @Test
    void getAPostWithPostIdThatDoesNotExistExpectFail () throws Exception {
        int postId = 100;
        Mockito.when(mockPostModelRepository.findById(postId)).thenThrow(new NoSuchElementException());
        var result = postService.getPostById(postId);
        Assertions.assertNull(result);

    }

    @Test
    void deleteAPostWithAValidPostIdExpectPass () throws Exception {
        Mockito.when(mockPostModelRepository.findById(newPost1.getId())).thenReturn(Optional.ofNullable(newPost1));
        var result = postService.deletePost(newPost1.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void deleteAPostThatDoesNotExistExpectFail () throws Exception {
        Assertions.assertFalse(postService.deletePost(1000));
    }

    @Test
    void createANewPostWithValidParamsExpectPass () throws Exception {
        Mockito.when(mockPostModelRepository.save(newPost)).thenReturn(newPost);
        BasePostContract postContract = new BasePostContract(newPost.getGroupId(), newPost.getPostContent());

        var result = postService.createPost(postContract, newPost.getUserId());
        Assertions.assertTrue(result);
    }

    @Test
    void createANewPostWithInvalidParamsExpectFail () throws Exception {
        BasePostContract postContract = new BasePostContract(newPost.getGroupId(), "");
        var result = postService.createPost(postContract, 1);
        Assertions.assertFalse(result);
    }

    @Test
    void updateAPostExpectPass () throws Exception {
        Mockito.when(mockPostModelRepository.findById(newPost.getId())).thenReturn(Optional.ofNullable(newPost));
        BasePostContract postUpdate = new BasePostContract(1, "This is An UPDATED post");
        newPost.setPostContent(postUpdate.postContent());
        Mockito.when(mockPostModelRepository.save(newPost)).thenReturn(newPost);
        Assertions.assertTrue(postService.updatePost(postUpdate, newPost.getId()));
    }
}
