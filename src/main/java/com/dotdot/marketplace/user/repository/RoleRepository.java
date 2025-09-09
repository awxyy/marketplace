package com.dotdot.marketplace.user.repository;

import com.dotdot.marketplace.user.entity.Role;
import com.dotdot.marketplace.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(UserRole name);
}
