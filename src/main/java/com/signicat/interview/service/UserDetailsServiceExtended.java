package com.signicat.interview.service;

import com.signicat.interview.entity.Subject;
import com.signicat.interview.exception.OperationalException;
import com.signicat.interview.repository.UserRepository;
import com.signicat.interview.security.core.userdetails.UserDetailsExtended;
import com.signicat.interview.security.core.userdetails.UserGroups;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDetailsServiceExtended implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetailsExtended loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<Subject> returnedUserOptional = userRepository.findByUserName(userName);

        log.info("inside loadUserByUsername ::");
        if(returnedUserOptional.isPresent()) {
            Subject returnedUser = returnedUserOptional.get();
            Set<UserGroups> userGroups = returnedUser.getGroups().stream().map(userGroup -> {
                return new UserGroups(userGroup.getId(), userGroup.getName());
            }).collect(Collectors.toUnmodifiableSet());
            return new UserDetailsExtended(returnedUser.getId(), returnedUser.getUserName(), returnedUser.getPassword(),userGroups, new ArrayList<>());
        }else {
            log.error("User not present-------");
            /*
                Internal server error is set to avoid user enumeration threat
             */
            throw new OperationalException("Something went wrong.Please reach out to Support.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
