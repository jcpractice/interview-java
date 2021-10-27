package com.signicat.interview.controller.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.signicat.interview.domain.LoginDTO;
import com.signicat.interview.domain.SubjectDTO;
import com.signicat.interview.exception.OperationalException;
import com.signicat.interview.response.JwtTokenResponse;
import com.signicat.interview.security.util.TokenFactory;
import com.signicat.interview.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
public class UserActionControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    UserService userService;
    @MockBean
    TokenFactory tokenFactory;
    @Autowired
    ObjectMapper mapper;



    /*@BeforeEach
    public void init() {
        UserGroups userGroups = new UserGroups(1L, "Default Group");
        UserDetailsExtended user = new UserDetailsExtended(1L, "testuser", "passw0rd", Set.of(userGroups), new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testuser", "passw0rd"));
        Mockito.when(userDetailsServiceExtended.loadUserByUsername(Mockito.anyString())).thenReturn(user);
        Mockito.when(passwordEncoder.matches(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
    }*/
    @Test
    public void createUser_withValidMockData_success() throws Exception {
        SubjectDTO subjectDto = new SubjectDTO("testuser","passw0rd","test@gmail.com");
        Mockito.when(userService.registerUser(Mockito.any(SubjectDTO.class))).thenReturn(subjectDto);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user")
                .accept(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(subjectDto))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andExpect(status().isCreated());
    }

    @Test
    public void createUser_withInvalidMockData_badRequest() throws Exception {
        SubjectDTO subjectDto = new SubjectDTO("","testpassword","test@gmail.com");
        Mockito.when(userService.registerUser(Mockito.any(SubjectDTO.class))).thenReturn(subjectDto);
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user")
                .accept(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(subjectDto))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    public void createUser_withMockDataForExistingUser_conflict() throws Exception {
        SubjectDTO subjectDto = new SubjectDTO("testUser","passw0rd","test@gmail.com");
        Mockito.when(userService.registerUser(Mockito.any(SubjectDTO.class))).thenThrow(new OperationalException("User Already exists", HttpStatus.CONFLICT));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user")
                .accept(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(subjectDto))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andExpect(status().isConflict());
    }

    @Test
    public void loginUser_withMockData_sendToken() throws Exception {

        LoginDTO login =  new LoginDTO("testuser","passw0rd");
        Mockito.when(userService.performLogin(login)).thenReturn(new JwtTokenResponse("lemon chusle"));

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/user/login").content(this.mapper.writeValueAsString(login))
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andReturn();
        System.out.println("mvcResult--"+mvcResult.getResponse().getContentAsString());
    }
    @Test
    public void anonymous_contentAccess_returnOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/content")).andExpect(status().isOk());

    }
}
