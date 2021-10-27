package com.signicat.interview.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.signicat.interview.domain.UserGroupDTO;
import com.signicat.interview.entity.UserGroup;
import com.signicat.interview.service.UserGroupService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

@RestController
@Slf4j
@RequestMapping(path = "/usergroup")
public class UserGroupOperationController {
    @Autowired
    UserGroupService userGroupService;
    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    ModelMapper modelMapper;

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserGroupDTO> create(@Valid @RequestBody UserGroupDTO userGroupDTO){
            log.trace("Entry create");

            UserGroup userGroupRequest = modelMapper.map(userGroupDTO, UserGroup.class);
            UserGroup userGroupResponse = userGroupService.saveUserGroup(userGroupRequest);
             URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/{id}")
                .buildAndExpand(userGroupDTO.getId()).toUri();

            log.trace("Exit create");
        return ResponseEntity.created(location).body(modelMapper.map(userGroupResponse, UserGroupDTO.class));
    }

    @GetMapping(path="/{id}",produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserGroupDTO> getUserGroupDetail(@NotNull @PathVariable Long id){
        log.trace("Entry getUserGroupDetail");
        UserGroup userGroup = userGroupService.getUserGroupDetail(id);

        log.trace("Exit getUserGroupDetail");
        return ResponseEntity.ok(modelMapper.map(userGroup, UserGroupDTO.class));
    }

    @PatchMapping(value = "/{id}" , consumes = {"application/json-patch+json"}, produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserGroup> patchUserGroup(@NotNull @PathVariable Long id, @RequestBody JsonPatch patch){
        log.trace("Entry patchUserGroup");
        try {
            UserGroup userGroup =userGroupService.getUserGroupDetail(id);
            UserGroup userGroupPatched = applyPatchToUseGroup(patch, userGroup);
            userGroupService.updateUserGroup(userGroupPatched);

            log.trace("Exit patchUserGroup");
            return ResponseEntity.ok(userGroupPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            log.error("Exception Occurred : "+e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping(path = "{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteUserGroup(@NotNull@PathVariable Long id){
        log.trace("Entry deleteUserGroup");

        userGroupService.deleteUserGroup(id);

        log.trace("Exit deleteUserGroup");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    private UserGroup applyPatchToUseGroup(JsonPatch patch, UserGroup userGroup) throws JsonPatchException, JsonProcessingException {
        log.trace("Entry applyPatchToUseGroup");
        JsonNode patched = patch.apply(objectMapper.convertValue(userGroup, JsonNode.class));

        log.trace("Exit applyPatchToUseGroup");
        return objectMapper.treeToValue(patched, UserGroup.class);
    }


}
