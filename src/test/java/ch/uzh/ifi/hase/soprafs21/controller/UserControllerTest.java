package ch.uzh.ifi.hase.soprafs21.controller;

import ch.uzh.ifi.hase.soprafs21.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs21.entity.User;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserAuthDTO;
import ch.uzh.ifi.hase.soprafs21.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs21.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.JsonPath;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // given
        User user = new User();
        user.setPassword("Firstname Lastname");
        user.setUsername("firstname@lastname");
        user.setStatus(UserStatus.OFFLINE);

        List<User> allUsers = Collections.singletonList(user);

        // this mocks the UserService -> we define above what the userService should return when getUsers() is called
        given(userService.getUsers()).willReturn(allUsers);

        // when
        MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].password", is(user.getPassword())))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())))
                .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("username");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("username");

        given(userService.createUser(Mockito.any())).willReturn(user);

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(redirectedUrl("http://localhost/users/1"));
    }

    @Test
    public void createUser_invalidInput_conflict() throws Exception {
        // no actual user is created, as the call to the userService will throw an error anyway.
        //Thus only the request body for the post request is created.

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("username");

        given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict());
    }


    @Test
    public void loginUser_success() throws Exception{
        //given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("username");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("password");
        userPostDTO.setUsername("username");

        //ensuring the call to login will return the above created user
        given(userService.loginUser(userPostDTO.getUsername(),userPostDTO.getPassword())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class));
    }

    @Test
    public void loginUser_fail() throws Exception{
        //given
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("username");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("falsePassword");
        userPostDTO.setUsername("username");

        //ensuring the call to login will throw an error
        given(userService.loginUser(userPostDTO.getUsername(),userPostDTO.getPassword())).willThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        MockHttpServletRequestBuilder putRequest = put("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void logoutUser_success() throws Exception{
        //the user created here is the expected user after the logout (so token is set to null)
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("username");
        user.setToken(null);
        user.setStatus(UserStatus.OFFLINE);

        //the values for the put request are set here
        UserAuthDTO userAuthDTO = new UserAuthDTO();
        userAuthDTO.setToken("1");
        userAuthDTO.setId(1L);

        //ensuring the logout call will return the logged out user created above
        given(userService.logoutUser(userAuthDTO.getId())).willReturn(user);

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userAuthDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is(nullValue())))
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class));

    }

    @Test
    public void logoutUser_failure() throws Exception{

        //only the values for the put request are set, no initial user creation is necessary
        UserAuthDTO userAuthDTO = new UserAuthDTO();
        userAuthDTO.setToken("1");
        userAuthDTO.setId(2L);

        //ensuring the logout call will throw an error
        given(userService.logoutUser(userAuthDTO.getId())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        MockHttpServletRequestBuilder putRequest = put("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userAuthDTO));

        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound());
    }
/*
    @Test
    public void getWinner() throws Exception{
        //create a user
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("username");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);
        user.setRestrictedMode(true);

        //create the expected winner list and add user to it
        List <User> winners = new ArrayList<>();
        winners.add(user);

        //ensuring the getWinner() call will return the list containing the user
        given(userService.getWinner()).willReturn(winners);

        MockHttpServletRequestBuilder getRequest = get("/winner")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].token", is(user.getToken())))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].restrictedMode", is(true)));

    }
    */

    @Test
    public void isRestricted() throws Exception{
        //create a user
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        user.setUsername("username");
        user.setToken("1");
        user.setStatus(UserStatus.ONLINE);

        //create a userAuthDTO for the request body
        UserAuthDTO userAuthDTO = new UserAuthDTO();
        userAuthDTO.setId(1L);
        userAuthDTO.setToken("1");

        given(userService.isRestricted(user)).willReturn(false);

        MockHttpServletRequestBuilder getRequest = get("/restricted")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userAuthDTO));

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

    }



    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}