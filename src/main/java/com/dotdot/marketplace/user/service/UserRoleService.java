package com.dotdot.marketplace.user.service;

import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;

import java.util.Set;

public interface UserRoleService {

    User createUserWithRoles(String login, String password, String fullName, Set<UserRole> userRoles);

    void addRoleToUser(Long userId, UserRole userRole);

    void removeRoleFromUser(Long userId, UserRole userRole);

    boolean userHasRole(Long userId, UserRole userRole);

    Set<UserRole> getUserRoles(Long userId);
}