package com.signicat.interview.domain;

import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@Generated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserGroupDTO {
    private Long id;
    @NotBlank
    private String name;

    public UserGroupDTO(String name) {
        this.name = name;
    }
}
