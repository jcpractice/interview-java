package com.signicat.interview.security.core.userdetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

/**
 * This is a simple implementation of OOB UserDetails interface in order to accommodate user group data.
 */
@Data
@AllArgsConstructor
public class UserDetailsExtended implements UserDetails {

    private final Long userId;
    private final String username;
    private final String password;
    private final String email;

    public Long getUserId() {
        return userId;
    }

    private final String profileType;
    private final Set<UserGroups> userGroups;
    private final Set<GrantedAuthority> authorities;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;
    private final boolean enabled;

    public UserDetailsExtended(Long id, String userName, String password, Set<UserGroups> userGroups, Collection<? extends GrantedAuthority> authorities) {
        /*
            In future if , Authorities comes into picture we can chnage the conversion and sort it accordingly
         */
        this(id, userName,password,"","",userGroups, Set.copyOf(authorities), true, true, true, true);
    }

}
