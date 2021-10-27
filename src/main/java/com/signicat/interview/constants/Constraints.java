package com.signicat.interview.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constraints {
    public static final int USERNAME_MIN_LENGTH = 4;
    public static final int USERNAME_MAX_LENGTH = 16;
    public static final String EMAIL_PATTERN = ".+@.+\\..{2,}";
    public static final String PASSWORD_PATTERN= "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    public static final int EMAIL_MIN_LENGTH = 6;
    public static final int EMAIL_MAX_LENGTH = 64;
}
