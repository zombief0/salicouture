package com.norman.couture.security.component;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUtils {
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String ROLE_CLAIM = "role";
    public static final String FIRST_NAME_CLAIM = "firstName";
    public static final String LAST_NAME_CLAIM = "lastName";
    public static final String LOGIN_CLAIM = "login";
}
