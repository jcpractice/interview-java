package com.signicat.interview.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signicat.interview.InterviewJavaApplication;
import com.signicat.interview.domain.LoginDTO;
import com.signicat.interview.domain.SubjectDTO;
import com.signicat.interview.domain.UserGroupDTO;
import com.signicat.interview.entity.Subject;
import com.signicat.interview.entity.UserGroup;
import com.signicat.interview.repository.UserRepository;
import com.signicat.interview.response.JwtTokenResponse;
import com.signicat.interview.service.UserGroupService;
import com.signicat.interview.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = InterviewJavaApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserGroupControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    UserService userService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    UserGroupService userGroupService;
    @Autowired
    UserRepository userRepository;
    private String jwtToken;

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
    public void unAuthorizedUser_createUserGroup_ShouldReturn401() throws Exception {
        UserGroup userGroup = new UserGroup(1L,"Test Group");

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/usergroup")
                .accept(MediaType.APPLICATION_JSON_VALUE).content(this.mapper.writeValueAsString(userGroup))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        mvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }
    @Test
    @Order(2)
    public void authorizedUser_createUserGroupWithInvalidData_ShouldReturn400() throws Exception {
        UserGroup userGroup = new UserGroup("");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+jwtToken);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/usergroup").headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE).content(this.mapper.writeValueAsString(userGroup))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        mvc.perform(requestBuilder).andExpect(status().isBadRequest());

    }
    @Test
    @Order(3)
    public void authorizedUser_createUserGroupAndReadUserGroup_ShouldReturnUserGroup() throws Exception {
        UserGroup userGroup = new UserGroup("Test Group");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+jwtToken);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/usergroup").headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE).content(this.mapper.writeValueAsString(userGroup))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MvcResult mvcResult = mvc.perform(requestBuilder).andExpect(status().isCreated()).andReturn();
        UserGroupDTO userGroupDTO = mapper.readValue(mvcResult.getResponse().getContentAsString(), UserGroupDTO.class);
        Assertions.assertEquals(userGroupDTO.getName(), "Test Group");
        Assertions.assertNotNull(userGroupDTO.getId());
        Long id = userGroupDTO.getId();

        mvc.perform(get("/usergroup/{id}", id).headers(headers).
                accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk());


    }

    @Test
    @Order(4)
    public void authorizedUser_updateUserGroup_ShouldReturnUserGroup() throws Exception {

        Optional<UserGroup> userGroupOptional = userGroupService.findUserGroupByName("Test Group");
        if(userGroupOptional.isPresent()){
            UserGroup userGroup = userGroupOptional.get();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer "+jwtToken);
            String userGroupPatch = "[{\"op\":\"replace\",\"path\": \"/name\",\"value\":\"Test Group New\"}]";

            RequestBuilder requestBuilder = MockMvcRequestBuilders
                    .patch("/usergroup/{id}", userGroup.getId()).headers(headers).contentType("application/json-patch+json").content(userGroupPatch);
            mvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Group New"));
        }else{
           fail("user group not present");
        }

    }

    @Test
    @Order(5)
    public void authorizedUser_getUserGroupWithInvalidId_ShouldReturn404() throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+jwtToken);

        mvc.perform(get("/usergroup/{id}",-999L).headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound());

    }
    @Test
    @Order(6)
    public void authorizedUser_createUserGroupWithExistingName_ShouldReturn401() throws Exception {
        UserGroup userGroup = new UserGroup("Test Group New");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+jwtToken);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/usergroup").headers(headers)
                .accept(MediaType.APPLICATION_JSON_VALUE).content(this.mapper.writeValueAsString(userGroup))
                .contentType(MediaType.APPLICATION_JSON_VALUE);
         mvc.perform(requestBuilder).andExpect(status().isConflict());
    }


   @AfterAll
    void deleteTestData() throws Exception {
        Optional<UserGroup> userGroupOptional = userGroupService.findUserGroupByName("Test Group New");
        if(userGroupOptional.isPresent()){

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer "+jwtToken);
            UserGroup userGroup = userGroupOptional.get();
            mvc.perform(delete("/usergroup/{id}", userGroup.getId()).headers(headers).content(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNoContent());
        }
       Optional<Subject> dummyTesterUserEntity = userRepository.findByUserName("dummytest");

       dummyTesterUserEntity.ifPresent(subject -> userRepository.deleteById(subject.getId()));
   }


}
