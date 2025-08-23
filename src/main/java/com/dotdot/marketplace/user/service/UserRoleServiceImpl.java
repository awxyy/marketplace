package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.user.entity.Role;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;
import com.dotdot.marketplace.user.repository.RoleRepository;
import com.dotdot.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserRoleServiceImpl implements UserRoleService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User createUserWithRoles(String login, String password, String fullName, Set<UserRole> userRoles) {
        User user = User.builder()
                .login(login)
                .password(password)
                .fullName(fullName)
                .build();

        userRoles.forEach(userRole -> {
            Role role = roleRepository.findByName(userRole)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + userRole));
            this.addRoleToUser(user, role);
        });

        return userRepository.save(user);
    }

    @Override
    public void addRoleToUser(Long userId, UserRole userRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findByName(userRole)
                .orElseThrow(() -> new RuntimeException("Role not found: " + userRole));
        
        this.addRoleToUser(user, role);
        userRepository.save(user);
    }

    @Override
    public void removeRoleFromUser(Long userId, UserRole userRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findByName(userRole)
                .orElseThrow(() -> new RuntimeException("Role not found: " + userRole));
        
        this.removeRoleFromUser(user, role);
        userRepository.save(user);
    }

    @Override
    public boolean userHasRole(Long userId, UserRole userRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return this.hasRole(user, userRole);
    }

    @Override
    public Set<UserRole> getUserRoles(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return this.getUserRoles(user);
    }

    private Set<UserRole> getUserRoles(User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(java.util.stream.Collectors.toSet());
    }

    private boolean hasRole(User user, UserRole userRole) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getName() == userRole);
    }

    private void addRoleToUser(User user, Role role) {
        user.getRoles().add(role);
    }

    private void removeRoleFromUser(User user, Role role) {
        user.getRoles().remove(role);
    }
}
