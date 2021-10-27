package com.signicat.interview.response;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Generated
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExceptionResponse implements Serializable {
    private LocalDateTime date;
    private String message;

}
