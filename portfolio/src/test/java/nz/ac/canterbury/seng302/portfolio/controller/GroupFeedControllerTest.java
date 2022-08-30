package nz.ac.canterbury.seng302.portfolio.controller;

import nz.ac.canterbury.seng302.portfolio.model.contract.basecontract.BasePostContract;
import nz.ac.canterbury.seng302.portfolio.model.entity.PostModel;
import nz.ac.canterbury.seng302.portfolio.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWebTestClient
class GroupFeedControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupFeedController groupFeedController;

    @Autowired
    private PostService postService = Mockito.mock(PostService.class);

    private BasePostContract post;


    @Test
    void createNewPostWithValidFields() throws Exception {
        post = new BasePostContract(
                1,
                "This a test dummy post"
        );

        PostModel model = new PostModel(1, 1, "This a test dummy post.");


        Mockito.when(postService.createPost(post, 1)).thenReturn(model);
        var result = this.mockMvc.perform(post("/group_feed/new_post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(post))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        System.err.println(result);
    }


}
