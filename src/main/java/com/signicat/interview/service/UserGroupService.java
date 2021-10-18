package com.signicat.interview.service;

import com.signicat.interview.entity.UserGroup;
import com.signicat.interview.exception.OperationalException;
import com.signicat.interview.repository.UserGroupRepository;
import com.signicat.interview.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
public class UserGroupService {

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    UserGroupRepository userGroupRepository;
    @Transactional
    public UserGroup saveUserGroup(UserGroup userGroup) {
        Example<UserGroup> example = ServiceUtils.getUserGroupExample(userGroup);
        if(userGroupRepository.exists(example)){
            throw new OperationalException("UserGroup already exists", HttpStatus.CONFLICT);
        }
        UserGroup response =  userGroupRepository.save(userGroup);
        return response ;
    }

    public UserGroup getUserGroupDetail(Long id) {
       Optional<UserGroup> userGroup = userGroupRepository.findById(id);
       if(userGroup.isEmpty()){
           throw new OperationalException("UserGroup not found.", HttpStatus.NOT_FOUND);
       }
        return userGroup.get();
    }

    public Optional<UserGroup> findUserGroupByName(String name){
        UserGroup userGroup = new UserGroup(name);
        Optional<UserGroup> response = userGroupRepository.findOne(ServiceUtils.getUserGroupExample(userGroup));

        return response;
    }

    public UserGroup fetchDefaultUserGroup() {
        UserGroup defaultGroup = new UserGroup("Default Group");
        Optional<UserGroup> defaultUserGroupResponse = userGroupRepository.findOne(/*Example.of(defaultGroup, matcher)*/ServiceUtils.getUserGroupExample(defaultGroup));

        if(defaultUserGroupResponse.isPresent()){
            return defaultUserGroupResponse.get();
        }else{
            throw new OperationalException("No default user group found!", HttpStatus.NOT_FOUND);
        }

    }

    public void deleteUserGroup(Long id) {
        userGroupRepository.deleteById(id);
    }

    public UserGroup updateUserGroup(UserGroup userGroupPatched) {
        UserGroup response =  userGroupRepository.save(userGroupPatched);
        return response;
    }

}
