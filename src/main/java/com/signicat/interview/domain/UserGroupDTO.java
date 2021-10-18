package com.signicat.interview.domain;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupDTO {
    //TODO need to check validation
    private Long id;
    @NotNull
    private String name;

    public UserGroupDTO(String name) {
        this.name = name;
    }
}
