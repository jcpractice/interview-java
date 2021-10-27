package com.signicat.interview.domain;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Generated
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginDTO {
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
}
