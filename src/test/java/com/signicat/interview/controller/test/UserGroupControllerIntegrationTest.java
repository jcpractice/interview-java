package com.signicat.interview.controller.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signicat.interview.domain.UserGroupDTO;
import com.signicat.interview.entity.UserGroup;
import com.signicat.interview.exception.OperationalException;
import com.signicat.interview.service.UserGroupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static java.util.Optional.empty;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class UserGroupControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserGroupService userGroupService;

    @Test
    public void unAuthorizedUser_createUserGroup_ShouldReturn401() throws Exception {
        UserGroup userGroup = new UserGroup(1L,"Root");
        Mockito.when(userGroupService.saveUserGroup(Mockito.any(UserGroup.class))).thenReturn(userGroup);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/usergroup")
                .accept(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(userGroup))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void authorizedUser_createUserGroup_ShouldReturnUserGroup() throws Exception {
        UserGroup userGroup = new UserGroup(1L,"Root");
        Mockito.when(userGroupService.saveUserGroup(Mockito.any(UserGroup.class))).thenReturn(userGroup);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/usergroup")
                .accept(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(userGroup))
                .contentType(MediaType.APPLICATION_JSON);
         mockMvc.perform(requestBuilder).andExpect(status().isCreated()).
                 andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Root"));

    }

    @Test
    @WithMockUser
    public void authorizedUser_createUserGroupWithInvalidData_ShouldReturn400() throws Exception {
        UserGroup userGroup = new UserGroup(1L,"");
        Mockito.when(userGroupService.saveUserGroup(Mockito.any(UserGroup.class))).thenReturn(userGroup);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/usergroup")
                .accept(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(userGroup))
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andExpect(status().isBadRequest());

    }

    @Test
    public void unAuthorizedUser_getUserGroupWithValidId_ShouldReturn401() throws Exception {
        UserGroup userGroup = new UserGroup(1L,"Root");
        Mockito.when(userGroupService.getUserGroupDetail(1L)).thenReturn(userGroup);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/usergroup/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser
    public void authorizedUser_getUserGroupWithValidId_ShouldReturnUserGroupDetails() throws Exception {
        UserGroup userGroup = new UserGroup(1L,"Root");
        Mockito.when(userGroupService.getUserGroupDetail(1L)).thenReturn(userGroup);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/usergroup/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Root"));

    }


    @Test
    @WithMockUser
    public void authorizedUser_getUserGroupWithInvalidId_ShouldReturn404() throws Exception {
        UserGroup userGroup = new UserGroup(1L,"Root");
        Mockito.when(userGroupService.getUserGroupDetail(Mockito.any(Long.class))).thenThrow(new OperationalException("UserGroup not found.", HttpStatus.NOT_FOUND));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/usergroup/{id}", 2L)
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser
    public void authorizedUser_patchUserGroupWithValidId_ShouldReturnPatchedField() throws Exception {
        UserGroup userGroup = new UserGroup(2L,"Root");
        Mockito.when(userGroupService.getUserGroupDetail(Mockito.any(Long.class))).thenReturn(userGroup);
        String usergroupPatch = "[{\"op\":\"replace\",\"path\": \"/name\",\"value\":\"AH\"}]";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/usergroup/{id}", 2L).contentType("application/json-patch+json").content(usergroupPatch);
        mockMvc.perform(requestBuilder).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.name").value("AH"));

    }

    @Test
    public void unAuthorizedUser_patchUserGroupWithValidId_ShouldReturn401() throws Exception {
        UserGroup userGroup = new UserGroup(2L,"Root");
        Mockito.when(userGroupService.getUserGroupDetail(Mockito.any(Long.class))).thenReturn(userGroup);
        String usergroupPatch = "[{\"op\":\"replace\",\"path\": \"/name\",\"value\":\"AH\"}]";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/usergroup/{id}", 2L).contentType("application/json-patch+json").content(usergroupPatch);
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser
    public void authorizedUser_patchUserGroupWithInvalidId_ShouldReturnPatchedField() throws Exception {
        Mockito.when(userGroupService.getUserGroupDetail(Mockito.any(Long.class))).thenThrow(new OperationalException("UserGroup Not found!", HttpStatus.NOT_FOUND));
        String usergroupPatch = "[{\"op\":\"replace\",\"path\": \"/name\",\"value\":\"AH\"}]";
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/usergroup/{id}", 4L).contentType("application/json-patch+json").content(usergroupPatch);
        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser
    public void authorizedUser_deleteUserGroupWithValidId_ShouldReturn201() throws Exception {
        Mockito.when(userGroupService.deleteUserGroup(Mockito.any(Long.class))).thenReturn("Record Deleted");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/usergroup/{id}", 4L).contentType("application/json-patch+json");
        mockMvc.perform(requestBuilder).andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser
    public void authorizedUser_deleteUserGroupWithInvalidId_ShouldReturn404() throws Exception {
        Mockito.when(userGroupService.deleteUserGroup(Mockito.any(Long.class))).thenThrow(new OperationalException("UserGroup not found.", HttpStatus.NOT_FOUND));
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/usergroup/{id}", 4L).contentType("application/json-patch+json");
        mockMvc.perform(requestBuilder).andExpect(status().isNotFound());

    }


    @Test
    public void unAuthorizedUser_deleteUserGroupWithValidId_ShouldReturn401() throws Exception {
        Mockito.when(userGroupService.deleteUserGroup(Mockito.any(Long.class))).thenReturn("Record Deleted");
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/usergroup/{id}", 4L).contentType("application/json-patch+json");
        mockMvc.perform(requestBuilder).andExpect(status().isUnauthorized());

    }
}
