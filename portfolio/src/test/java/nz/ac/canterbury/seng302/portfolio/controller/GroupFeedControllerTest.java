package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.entity.CommentModel;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.service.CommentService;
import nz.ac.canterbury.seng302.portfolio.service.PostService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.webservices.client.WebServiceClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@WebServiceClientTest
class GroupFeedControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupFeedController controller;

    @Mock
    private PostService postService = Mockito.mock(PostService.class);

    @Mock
    private CommentService commentService = Mockito.mock(CommentService.class);

    private PostModel post1;
    private PostModel post2;
    private PostModel post3;
    private PostModel post4;

    private CommentModel comment1;
    private CommentModel comment2;
    private CommentModel comment3;
    private CommentModel comment4;

    private List<PostModel> allPosts;
    private List<CommentModel> allComments;

    @BeforeEach
    void setupBeforeEach () {
        post1 = new PostModel(1, 3, "This a new Post From Teachers");
        post2 = new PostModel(2, 3, "This a Test Post From Other Group");
        post3 = new PostModel(3, 4, "This a Test Post From Group 3");
        post4 = new PostModel(1, 2, "This another post from teachers.");

        allPosts = new ArrayList<>();
        allPosts.add(post1);
        allPosts.add(post2);
        allPosts.add(post3);
        allPosts.add(post4);

        comment1 = new CommentModel(post1.getId(), 4, "This a reply to teachers' post");
        comment2 = new CommentModel(post2.getId(), 4, "This message to group 2 post");
        comment3 = new CommentModel(post1.getId(), 4, "This a reply to the post from teachers");
        comment4 = new CommentModel(post3.getId(), 4, "This a reply to group 3");

        allComments = new ArrayList<>();
        allComments.add(comment1);
        allComments.add(comment2);
        allComments.add(comment3);
        allComments.add(comment4);
    }

    @Test
    void contextLoads() throws Exception {
        Assertions.assertThat(controller).isNotNull();
    }

//    @Test
//    void getPostsByGroupIdExpectPass () throws Exception {
//        Mockito.when(postService.getAllPostsForAGroup(post1.getGroupId()))
//                .thenReturn(allPosts.stream().filter(postModel -> {return postModel.getGroupId() == post1.getGroupId();}).collect(Collectors.toList()));
//
//
//        Mockito.when(commentService.getCommentsForGivenPost(post1.getId()))
//                .thenReturn(allComments.stream().filter(commentModel -> {return commentModel.getPostId() == post1.getId();}).collect(Collectors.toList()));
//        var result = mockMvc.perform(get("/feed_content/1"))
//                .andExpect(status().isOk());
//    }

//    @Test
//    void createNewPostWithValidFields() throws Exception {
//        post = new BasePostContract(
//                1,
//                "This a test dummy post"
//        );
//
//        PostModel model = new PostModel(1, 1, "This a test dummy post.");
//
//        Mockito.when(postService.createPost(post, 1)).thenReturn(true);
//
//        var result = mockMvc.perform(post("/group_feed/new_post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(String.valueOf(post))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn();
//
//        System.err.println(result);
//    }


}
