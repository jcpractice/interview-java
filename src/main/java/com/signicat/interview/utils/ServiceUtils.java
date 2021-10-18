package com.signicat.interview.utils;

import com.signicat.interview.entity.UserGroup;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Component
public class ServiceUtils {

    /**
     *
     * @param userGroup
     * @return
     */
    public static Example<UserGroup> getUserGroupExample(UserGroup userGroup){
        return Example.of(userGroup, getExampleMatcher("name"));
    }

    /**
     *
     * @return
     * @param propertyName
     */
    public static ExampleMatcher getExampleMatcher(String propertyName) {
        return ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher(propertyName, ignoreCase());
    }

}
