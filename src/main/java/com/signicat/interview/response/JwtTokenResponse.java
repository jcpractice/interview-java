package com.signicat.interview.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@Generated
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenResponse implements Serializable {
    private String token;
}
