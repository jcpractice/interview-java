package com.signicat.interview.service;

import com.nimbusds.jose.JOSEException;
import com.signicat.interview.domain.LoginDTO;
import com.signicat.interview.domain.SubjectDTO;
import com.signicat.interview.domain.UserGroupDTO;
import com.signicat.interview.entity.Subject;
import com.signicat.interview.entity.UserGroup;
import com.signicat.interview.exception.CustomTokenException;
import com.signicat.interview.exception.OperationalException;
import com.signicat.interview.repository.UserRepository;
import com.signicat.interview.response.JwtTokenResponse;
import com.signicat.interview.security.core.userdetails.UserDetailsExtended;
import com.signicat.interview.security.util.TokenFactory;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     *
     * @param subjectDTO - Subject DTO
     * @return - SubjectDTO
     */
    @Transactional
    public SubjectDTO registerUser(SubjectDTO subjectDTO){
        log.trace("Entry registerUser");
        /*
          Assumption is that the user will only input the user groups that are already exist in the system.
         */

        Optional<Subject> existingUser = userRepository.findByUserNameOrEmail(subjectDTO.getUserName(), subjectDTO.getEmail());
        if(existingUser.isPresent()){
            log.error("User Already Exists.");
            throw new OperationalException("User already exists", HttpStatus.CONFLICT);
        }

        Subject subject = convertToEntity(subjectDTO);
        Subject subjectResponse = userRepository.save(subject);

        SubjectDTO subjectDTOResponse  = convertToDto(subjectResponse);

        log.trace("Exit registerUser");
        return subjectDTOResponse;

    }

    /**
     *
     * @param subjectDto - Subject DTO
     * @return - Subject
     */
    private Subject convertToEntity(SubjectDTO subjectDto) {
        log.trace("Entry convertToEntity");

        Subject subject = modelMapper.map(subjectDto, Subject.class);
        subject.setPassword(passwordEncoder.encode(subjectDto.getPassword()));

        Set<UserGroup> userGroups = Optional.ofNullable(subjectDto.getUserGroups()).orElseGet(()-> List.of(new UserGroupDTO("Default Group"))).
                stream().map(userGroup-> fetchUserGroups(userGroup.getName())).collect(Collectors.toSet());
        subject.setGroups(userGroups);

        log.trace("Exit convertToEntity");
        return subject;
    }

    /**
     *
     * @param subject - Subject Entity
     * @return - Subject
     */
    private SubjectDTO convertToDto(Subject subject){
        return modelMapper.map(subject, SubjectDTO.class);
    }

    /**
     *
     * @param name - User Group Name
     * @return - Subject
     */
    private UserGroup fetchUserGroups(String name) {
        log.trace("Entry fetchUserGroups "+name);

        Optional<UserGroup> userGroup = userGroupService.findUserGroupByName(name);
        if(userGroup.isEmpty()){
            log.trace("User group not found.Looking for default group.");
            log.trace("Exit fetchUserGroups");

            return userGroupService.fetchDefaultUserGroup();
        }

        log.trace("Exit fetchUserGroups");
        return userGroup.get();
    }


    /**
     *
     * @param loginRequest - Login DTO
     * @return JwtTokenResponse
     */
    public JwtTokenResponse performLogin(LoginDTO loginRequest) {
        log.trace("Entry performLogin");

        UserDetailsExtended userDetail = userDetailsService.loadUserByUsername(loginRequest.getUserName());
        String token;
        if(null==userDetail){
            log.error("User Not exist");
            throw new OperationalException("Invalid UserName or Password", HttpStatus.UNAUTHORIZED);
        }else{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUserName(), loginRequest.getPassword()));
            try {
                token = tokenFactory.generateToken(userDetail);
            } catch (JOSEException e) {
                log.error("Exception Occurred : "+e.getLocalizedMessage());
                throw new CustomTokenException("Unable to generate token",e);
            }
        }
        log.trace("Exit performLogin");
        return new JwtTokenResponse(token);
    }


}
