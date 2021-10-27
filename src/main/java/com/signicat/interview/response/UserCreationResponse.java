package com.signicat.interview.response;

import lombok.*;

import java.io.Serializable;

@Data
@Generated
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserCreationResponse implements Serializable {
    private Long userId;
    private String message;
}
