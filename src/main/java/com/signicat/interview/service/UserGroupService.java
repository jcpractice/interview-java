package com.signicat.interview.service;

import com.signicat.interview.entity.UserGroup;
import com.signicat.interview.exception.OperationalException;
import com.signicat.interview.repository.UserGroupRepository;
import com.signicat.interview.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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

    /**
     *
     * @param userGroup - UserGroup Entity
     * @return - UserGroup Entity
     */
    @Transactional
    public UserGroup saveUserGroup(UserGroup userGroup) {
        log.trace("Entry saveUserGroup");
        Example<UserGroup> example = ServiceUtils.getUserGroupExample(userGroup);
        if(userGroupRepository.exists(example)){
            throw new OperationalException("UserGroup already exists", HttpStatus.CONFLICT);
        }
        UserGroup response =  userGroupRepository.save(userGroup);

        log.trace("Exit saveUserGroup");
        return response ;
    }

    /**
     *
     * @param id -  Group Id
     * @return - UserGroup
     */
    public UserGroup getUserGroupDetail(Long id) {
        log.trace("Entry getUserGroupDetail "+id);

       Optional<UserGroup> userGroup = userGroupRepository.findById(id);
       if(userGroup.isEmpty()){
           log.error("Unable to retrieve userGroup for id : "+id);
           throw new OperationalException("UserGroup not found.", HttpStatus.NOT_FOUND);
       }

        log.trace("Exit getUserGroupDetail");
        return userGroup.get();
    }

    /**
     *
     * @param name -  UserGroup Name
     * @return - UserGroup
     */
    public Optional<UserGroup> findUserGroupByName(String name){
        log.trace("Entry findUserGroupByName"+name);

        UserGroup userGroup = new UserGroup(name);
        Optional<UserGroup> response = userGroupRepository.findOne(ServiceUtils.getUserGroupExample(userGroup));

        log.trace("Exit findUserGroupByName");
        return response;
    }

    /**
     *
     * @return - UserGroup
     */
    public UserGroup fetchDefaultUserGroup() {
        log.trace("Entry fetchDefaultUserGroup");
        UserGroup defaultGroup = new UserGroup("Default Group");
        Optional<UserGroup> defaultUserGroupResponse = userGroupRepository.findOne(/*Example.of(defaultGroup, matcher)*/ServiceUtils.getUserGroupExample(defaultGroup));

        if(defaultUserGroupResponse.isPresent()){

            log.trace("Exit fetchDefaultUserGroup");
            return defaultUserGroupResponse.get();
        }else{
            log.error("No default user group found!");
            throw new OperationalException("No default user group found!", HttpStatus.NOT_FOUND);
        }

    }

    /**
     *
     * @param id - UserGroupId
     */
    @Transactional
    public String deleteUserGroup(Long id) {
        log.trace("Entry deleteUserGroup "+id);

        getUserGroupDetail(id);

        userGroupRepository.deleteById(id);

        log.trace("Exit deleteUserGroup "+id);
        return "Record deleted";
    }

    /**
     *
     * @param userGroupPatched - UserGroup Entity
     * @return - UserGroup Entity
     */
    public UserGroup updateUserGroup(UserGroup userGroupPatched) {
        log.trace("Entry updateUserGroup");

        UserGroup response =  userGroupRepository.save(userGroupPatched);

        log.trace("Exit updateUserGroup");
        return response;
    }



}
