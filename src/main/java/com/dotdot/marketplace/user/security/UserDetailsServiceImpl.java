package com.dotdot.marketplace.user.security;

import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.Role;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByLogin(username)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public UserDetails getUserDetails(){
        return (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal() ;
    }

    public Long getCurrentUserId() {
        UserDetails userDetails = getUserDetails();
        if (userDetails instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getUser().getId();
        }
        throw new IllegalStateException("Current user is not an instance of UserPrincipal");
    }

    public Set<UserRole> getCurrentUserRoles() {
        UserDetails userDetails = getUserDetails();
        if (userDetails instanceof UserPrincipal userPrincipal) {
            return getUserRoles(userPrincipal.getUser());
        }
        throw new IllegalStateException("Current user is not an instance of UserPrincipal");
    }

    public boolean hasRole(UserRole role) {
        return getCurrentUserRoles().contains(role);
    }

    public Set<UserRole> getUserRoles(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public boolean userHasRole(User user, UserRole userRole) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == userRole);
    }
}
