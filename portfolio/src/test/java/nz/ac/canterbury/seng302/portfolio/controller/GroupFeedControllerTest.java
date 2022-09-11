//package nz.ac.canterbury.seng302.portfolio.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.sql.Timestamp;
//import java.util.Date;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//import nz.ac.canterbury.seng302.portfolio.AuthorisationParamsHelper;
//import nz.ac.canterbury.seng302.portfolio.authentication.PortfolioPrincipal;
//import nz.ac.canterbury.seng302.portfolio.mapping.CommentMapper;
//import nz.ac.canterbury.seng302.portfolio.mapping.PostMapper;
//import nz.ac.canterbury.seng302.portfolio.model.contract.CommentContract;
//import nz.ac.canterbury.seng302.portfolio.model.contract.PostContract;
//import nz.ac.canterbury.seng302.portfolio.model.entity.CommentEntity;
//import nz.ac.canterbury.seng302.portfolio.model.entity.PostEntity;
//import nz.ac.canterbury.seng302.portfolio.service.AuthStateService;
//import nz.ac.canterbury.seng302.portfolio.service.CommentService;
//import nz.ac.canterbury.seng302.portfolio.service.GroupsClientService;
//import nz.ac.canterbury.seng302.portfolio.service.PostService;
//import nz.ac.canterbury.seng302.portfolio.service.UserAccountService;
//import nz.ac.canterbury.seng302.shared.identityprovider.GroupDetailsResponse;
//import nz.ac.canterbury.seng302.shared.identityprovider.UserResponse;
//import nz.ac.canterbury.seng302.shared.identityprovider.UserRole;
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//@AutoConfigureWebTestClient
//class GroupFeedControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private GroupFeedController controller;
//
//    @MockBean
//    private PostService postService;
//
//    @MockBean
//    private CommentService commentService;
//
//    @MockBean
//    private UserAccountService userAccountService;
//
//    @MockBean
//    private GroupsClientService groupsClientService;
//
//    @MockBean
//    private AuthStateService authStateService;
//
//    @Autowired private PostMapper postMapper;
//
//    @Autowired private CommentMapper commentMapper;
//
//    private List<PostEntity> allPosts;
//    private List<CommentEntity> allComments;
//
//    @BeforeEach
//    void setupBeforeEach () {
//        Mockito.when(groupsClientService.getGroupById(any(int.class))).thenReturn(GroupDetailsResponse.newBuilder()
//                .setGroupId(1)
//                .setShortName("Mock Group")
//                .setLongName("This is a new test Group").build());
//
//        int validUserId = 3;
//        Mockito.when(userAccountService.getUserById(any(int.class))).thenReturn(
//                UserResponse.newBuilder()
//                        .setId(validUserId)
//                        .setUsername("testing")
//                        .build()
//        );
//        Mockito.when(authStateService.getId(any(PortfolioPrincipal.class))).thenReturn(3);
//        AuthorisationParamsHelper.setParams("role", UserRole.STUDENT);
//
//        Date date = new Date();
//        Timestamp currentTime = new Timestamp(date.getTime());
//        PostContract post1 = new PostContract("1", 1, validUserId, "This a new Post From Teachers", currentTime, null);
//        PostContract post2 = new PostContract("2", 2, validUserId, "This a Test Post From Other Group", currentTime, null);
//        PostContract post3 = new PostContract("3", 3, 4, "This a Test Post From Group 3", currentTime, null);
//        PostContract post4 = new PostContract("4", 1, 2, "This another post from teachers.", currentTime, null);
//
//        allPosts = Stream.of(post1, post2, post3, post4)
//            .map(postContract -> postMapper.toEntity(postContract))
//            .collect(Collectors.toList());
//
//        CommentContract comment1 = new CommentContract("1", 4, allPosts.get(0).getId(),
//            "This a reply to teachers' post", currentTime);
//        CommentContract comment2 = new CommentContract("2", validUserId, allPosts.get(1).getId(),
//            "This message to group 2 post", currentTime);
//        CommentContract comment3 = new CommentContract("3", 4, allPosts.get(0).getId(),
//            "This a reply to the post from teachers", currentTime);
//        CommentContract comment4 = new CommentContract("4", validUserId, allPosts.get(2).getId(),
//            "This a reply to group 3", currentTime);
//
//        allComments = Stream.of(comment1, comment2, comment3, comment4)
//            .map(commentContract -> commentMapper.toEntity(commentContract))
//            .collect(Collectors.toList());
//    }
//
//    /**
//     * This test makes sure that controller is loaded and running.
//     */
//    @Test
//    void contextLoads() {
//        Assertions.assertNotNull(controller);
//    }
//
//    /**
//     * This test makes checks the all the posts can be retrieved.
//     */
//    @Test
//    void getPostsByGroupIdExpectPassAndExpectPass () throws Exception {
//        var expectedPots = allPosts.stream().filter(postModel -> postModel.getGroupId() == allPosts.get(0).getGroupId()).collect(Collectors.toList());
//        Mockito.when(postService.getAllPostsForGroup(allPosts.get(0).getGroupId())).thenReturn(expectedPots);
//
//        Mockito.when(commentService.getCommentsForGivenPost(allPosts.get(0).getId()))
//                .thenReturn(allComments.stream().filter(commentModel -> Objects.equals(
//                    commentModel.getPost().getId(), allPosts.get(0).getId())).collect(Collectors.toList()));
//
//        var result = mockMvc.perform(get("/feed_content/1"))
//                .andExpect(status().isOk())
//                .andReturn();
//        var response = (new JSONObject(result.getResponse().getContentAsString()));
//
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(allPosts.get(0).getGroupId(), response.get("groupId"));
//
//        Assertions.assertNotNull(response.get("posts"));
//        var posts =(JSONArray) response.get("posts");
//        for (int i=0; i < posts.length(); i++) {
//            var post = (JSONObject) posts.get(i);
//            Assertions.assertEquals(expectedPots.get(i).getPostContent(), post.get("content"));
//            Assertions.assertEquals(expectedPots.get(i).getUserId(), post.get("userId"));
//        }
//    }
//
//    /**
//     * This test creates a new post will all the valid values and expects a success code.
//     */
//    @Test
//    void createNewPostWithValidFieldsAndExpectPass () throws Exception {
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(true);
//        Mockito.when(postService.createPost(any(PostContract.class))).thenReturn(true);
//
//        mockMvc.perform(post("/group_feed/new_post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {
//                                    "groupId" : "1",
//                                    "postContent" : "This a test dummy post"
//                                }
//                                """)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is2xxSuccessful())
//                .andReturn();
//    }
//
//    /**
//     * This test checks if the User how is not member of the group can post on that group feed page or not, and it fails
//     * as expected.
//     */
//    @Test
//    void createNewPostUserNotMemberOfGroupExpectFail () throws Exception {
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(false);
//        Mockito.when(postService.createPost(new PostContract("5", 1,1, "This a test dummy post", null, null))).thenReturn(true);
//        mockMvc.perform(post("/group_feed/new_post")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {
//                                    "groupId" : 1,
//                                    "userId" : 1,
//                                    "postContent" : "This a test dummy post"
//                                }
//                                """)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is4xxClientError());
//    }
//
//    /**
//     * This tests deletion of a post, the user who made the post is deleting the post, so it is expected to pass.
//     */
//    @Test
//    void deleteAPostWhereUserIsWhoMadeThePostExpectPass () throws Exception {
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(true);
//        Mockito.when(postService.getPostById(any(String.class))).thenReturn(allPosts.get(0));
//
//        mockMvc.perform(delete("/delete_feed/1"))
//                .andExpect(status().is2xxSuccessful())
//                .andExpect(status().isOk());
//    }
//
//    /**
//     * this tests deletion of a post made by a different user, and it fails as expected as a user cannot delete posts made
//     * by other users.
//     */
//    @Test
//    void deleteAPostWhereUserIsNotWhoMadeThePostExpectFail () throws Exception {
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(true);
//        Mockito.when(postService.getPostById(any(String.class))).thenReturn(allPosts.get(2));
//        mockMvc.perform(delete("/delete_feed/3")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is4xxClientError());
//    }
//
//    /**
//     * A user cannot delete a post, once he/she is no longer part of that group.
//     */
//    @Test
//    void deleteAPostWhereUserIsWhoMadeThePostButNotMemberAnyMoreExpectFail () throws Exception {
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(false);
//        Mockito.when(postService.getPostById(any(String.class))).thenReturn(allPosts.get(0));
//        mockMvc.perform(delete("/delete_feed/3")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().is4xxClientError());
//    }
//
//    /**
//     * A Teacher can delete any post made by any member in any group.
//     */
//    @Test
//    void deleteAPostWhereUserIsATeacherExpectPass () throws Exception {
//        AuthorisationParamsHelper.setParams("role", UserRole.TEACHER);
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(false);
//        Mockito.when(postService.getPostById(any(String.class))).thenReturn(allPosts.get(2));
//        mockMvc.perform(delete("/delete_feed/3")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    /**
//     * An Admin can delete any post made by any member in any group.
//     */
//    @Test
//    void deleteAPostWhereUserIsAAdminExpectPass () throws Exception {
//        AuthorisationParamsHelper.setParams("role", UserRole.COURSE_ADMINISTRATOR);
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(false);
//        Mockito.when(postService.getPostById(any(String.class))).thenReturn(allPosts.get(2));
//        mockMvc.perform(delete("/delete_feed/3")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    /**
//     * A user who made the post can update the content of the post any time.
//     */
//    @Test
//    void updateAPostWhereUserIsWhoMadeOriginalPostExpectPass () throws Exception {
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(true);
//        Mockito.when(postService.getPostById(any(String.class))).thenReturn(allPosts.get(0));
//        Mockito.when(postService.updatePost(any(PostContract.class), any(String.class))).thenReturn(true);
//
//        mockMvc.perform(put("/update_feed/" + allPosts.get(0).getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {
//                                    "groupId" : 1,
//                                    "postContent" : "This a test dummy post Changed From Teachers"
//                                }
//                                """)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is2xxSuccessful())
//                .andReturn();
//    }
//
//    /**
//     * A post cannot be updated by any user other than the user who made the post, Event Teachers/Admins cannot edit a post.
//     */
//    @Test
//    void updateAPostWhereUserIsNotWhoMadeOriginalPostExpectFail () throws Exception {
//        Mockito.when(groupsClientService.isMemberOfTheGroup(any(int.class), any(int.class))).thenReturn(true);
//        Mockito.when(postService.getPostById(any(String.class))).thenReturn(allPosts.get(3));
//        Mockito.when(postService.updatePost(any(PostContract.class), any(String.class))).thenReturn(true);
//
//        mockMvc.perform(put("/update_feed/" + allPosts.get(3).getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("""
//                                {
//                                    "groupId" : 1,
//                                    "postContent" : "This a test dummy post Changed From Teachers"
//                                }
//                                """)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is4xxClientError())
//                .andReturn();
//    }
//}
