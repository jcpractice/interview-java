package com.signicat.interview.controller;

import com.signicat.interview.domain.LoginDTO;
import com.signicat.interview.domain.SubjectDTO;
import com.signicat.interview.response.JwtTokenResponse;
import com.signicat.interview.response.UserCreationResponse;
import com.signicat.interview.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserActionController {

    @Autowired
    private UserService userService;

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserCreationResponse> createUser(@Valid  @RequestBody SubjectDTO request){
        log.trace("Entry createUser");

        SubjectDTO subject = userService.registerUser(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/details/{id}")
                .buildAndExpand(subject.getId()).toUri();

        log.trace("Exit createUser");
        return ResponseEntity.created(location).body(new UserCreationResponse(subject.getId(), "user  created successfully"));
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping(path = "/login", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<JwtTokenResponse> login(@Valid @RequestBody LoginDTO loginRequest){
        log.trace("Entry login");

        JwtTokenResponse response  = userService.performLogin(loginRequest);

        log.trace("Exit login");
        return ResponseEntity.ok(response);

    }

    @GetMapping(path="/content", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> getContent(){
        return ResponseEntity.ok("Hey this is a generic content and anyone can access it..");
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping(path="/@self",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<String> getCurrentUser(){
        log.trace("Entry getCurrentUser");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        log.trace("Exit getCurrentUser");
        return ResponseEntity.ok(auth.getName());
    }


}
