package com.signicat.interview.domain;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

import static com.signicat.interview.constants.Constraints.*;

@Data
@Getter
@Setter
@NoArgsConstructor

public class SubjectDTO {

    @NotBlank(message = "UserName can not be blank")
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "Username must be within "+USERNAME_MIN_LENGTH+" to "+USERNAME_MAX_LENGTH+" characters of length")
    private String userName;
    @Pattern(regexp = PASSWORD_PATTERN, message = "Password should match criteria")
    private String password;
    @NotBlank(message = "Email Address can not be blank")
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH, message = "email address  must be within"+EMAIL_MIN_LENGTH+" to "+EMAIL_MAX_LENGTH+" characters of length")
    @Email(regexp = EMAIL_PATTERN)
    private String email;
    private String profileType;
    private int status;
    private List<UserGroupDTO> userGroups;

}
