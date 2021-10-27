package com.signicat.interview.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signicat.interview.InterviewJavaApplication;
import com.signicat.interview.domain.LoginDTO;
import com.signicat.interview.domain.SubjectDTO;
import com.signicat.interview.domain.UserGroupDTO;
import com.signicat.interview.entity.Subject;
import com.signicat.interview.repository.UserRepository;
import com.signicat.interview.response.JwtTokenResponse;
import com.signicat.interview.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = InterviewJavaApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserActionControllerUnitTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    private String jwtToken;
    @Autowired
    UserRepository userRepository;

    @BeforeAll
    void init() throws Exception {
        SubjectDTO subject = new SubjectDTO("dummytest","passw0rd","dummytest@test.com");
        mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(subject))).andReturn();

        LoginDTO login = new LoginDTO("dummytest","passw0rd");
        MvcResult mvcResult = mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(login))).andExpect(status().isOk()).andReturn();
        JwtTokenResponse jwtTokenResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), JwtTokenResponse.class);
        jwtToken = jwtTokenResponse.getToken();
    }



    @Test
    @Order(1)
    public void givenValidCredentials_whenLogin_thenReturnToken() throws Exception {
        LoginDTO login = new LoginDTO("dummytest","passw0rd");
        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(login))).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
    }


    @Test
    @Order(2)
    public void givenInvalidUserName_whenLogin_thenReturn401() throws Exception {
        LoginDTO login = new LoginDTO("dummytest123","passw0rd");
        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(login))).andExpect(status().isUnauthorized()).andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());
    }

    @Test
    @Order(3)
    public void givenInvalidPassword_whenLogin_thenReturn401() throws Exception {
        LoginDTO login = new LoginDTO("dummytest","passw0rd1");
        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(login))).andExpect(status().isUnauthorized()).andExpect(MockMvcResultMatchers.jsonPath("$.message").isNotEmpty());
    }

    @Test
    @Order(4)
    public void givenValidData_whenRegister_thenReturn201() throws Exception {
        UserGroupDTO userGroupNonExisting = new UserGroupDTO("Dummy group");
        SubjectDTO subject = new SubjectDTO("appTester","testpassw0rd","apptester@test.com");
        subject.setUserGroups(List.of(userGroupNonExisting));
        mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(subject))).andExpect(status().isCreated());
    }

    @Test
    @Order(5)
    public void givenExistingUserName_whenRegister_thenReturn409() throws Exception {
        SubjectDTO subject = new SubjectDTO("appTester","testpassw0rd","apptester@test.com");
        mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(subject))).andExpect(status().isConflict());
    }

    @Test
    @Order(6)
    public void givenExistingEmail_whenRegister_thenReturn409() throws Exception {
        SubjectDTO subject = new SubjectDTO("appTester","testpassw0rd","apptester@test.com");
        mvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).
                content(mapper.writeValueAsString(subject))).andExpect(status().isConflict());
    }

    @Test
    @Order(7)
    public void givenValidToken_whenGetSelfRecord_thenReturnCurrentUserName() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+jwtToken);
        mvc.perform(MockMvcRequestBuilders.get("/user/@self").headers(headers)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void givenTokenWithOutBearer_whenGetSelfRecord_thenReturn401() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", jwtToken);
        mvc.perform(MockMvcRequestBuilders.get("/user/@self").headers(headers)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }
    @AfterAll
    public void deleteTestData(){
        Optional<Subject> appTesterUserEntity = userRepository.findByUserName("appTester");
        Optional<Subject> dummyTesterUserEntity = userRepository.findByUserName("dummytest");
        appTesterUserEntity.ifPresent(subject -> userRepository.deleteById(subject.getId()));
        dummyTesterUserEntity.ifPresent(subject -> userRepository.deleteById(subject.getId()));
    }
}
