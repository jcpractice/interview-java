package com.signicat.interview.service;

import com.nimbusds.jose.JOSEException;
import com.signicat.interview.domain.LoginDTO;
import com.signicat.interview.domain.SubjectDTO;
import com.signicat.interview.domain.UserGroupDTO;
import com.signicat.interview.entity.Subject;
import com.signicat.interview.entity.UserGroup;
import com.signicat.interview.exception.CustomTokenException;
import com.signicat.interview.repository.UserRepository;
import com.signicat.interview.response.JwtTokenResponse;
import com.signicat.interview.security.core.userdetails.UserDetailsExtended;
import com.signicat.interview.security.util.TokenFactory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.signicat.interview.utils.ServiceUtils.getExampleMatcher;

@Service
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserGroupService userGroupService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserDetailsServiceExtended userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    TokenFactory tokenFactory;

    @Transactional
    public Subject registerUser(SubjectDTO subjectDTO){
        /*
          Assumption is that the user will only input the user groups that are already exist in the system.
         */
        //TODO check if user already exists
          Subject subject = convertToEntity(subjectDTO);
        return userRepository.save(subject);

    }
    private Subject convertToEntity(SubjectDTO subjectDto) {
        Subject subject = modelMapper.map(subjectDto, Subject.class);
        subject.setPassword(passwordEncoder.encode(subjectDto.getPassword()));
        Set<UserGroup> userGroups = Optional.ofNullable(subjectDto.getUserGroups()).orElseGet(()-> List.of(new UserGroupDTO("Default Group"))).
                stream().map(userGroup-> fetchUserGroups(userGroup.getName())).collect(Collectors.toSet());
        subject.setGroups(userGroups);

        return subject;
    }

    private UserGroup fetchUserGroups(String name) {
        Optional<UserGroup> userGroup = userGroupService.findUserGroupByName(name);
        if(!userGroup.isPresent()){
            return userGroupService.fetchDefaultUserGroup();
        }

        return userGroup.get();
    }




    public JwtTokenResponse performLogin(LoginDTO loginRequest) {
        UserDetailsExtended userDetail = userDetailsService.loadUserByUsername(loginRequest.getUserName());
        String token = null;
        if(null==userDetail){
            //TODO throw an error stating "there is a problem in login .Please reach out to admin
            throw new RuntimeException("User does not exist");
        }else{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
            try {
                token = tokenFactory.generateToken(userDetail);

            } catch (JOSEException e) {
                throw new CustomTokenException("Unable to generate token",e);
            }
        }
        return new JwtTokenResponse(token);
    }

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }
}
