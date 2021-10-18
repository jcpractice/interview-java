package com.signicat.interview.security.core.userdetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Getter
@Setter
public class UserGroups implements Serializable {
    private final Long id;
    private final String name;

}
