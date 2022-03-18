package nz.ac.canterbury.seng302.portfolio.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class ViewAccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ViewAccountController accountController;


    @Test
    public void getAllUsers() throws Exception{
        System.out.println("Test Starting:");
        var result = this.mockMvc.perform(get("/api/v1/account"))
                .andExpect(status().isOk())
                .andReturn();
        System.out.println(result);
    }

    @Test
    public void getUserById() throws Exception {
        var result = this.mockMvc.perform(get("/api/v1/account/12"))
                                .andExpect(status().isOk())
                                .andReturn();

        System.out.println(result);
    }

}
