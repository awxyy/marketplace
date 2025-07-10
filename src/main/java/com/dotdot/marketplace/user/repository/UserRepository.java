package com.dotdot.marketplace.user.repository;


import com.dotdot.marketplace.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
//@repository?
public interface UserRepository  extends JpaRepository<User, Long> {
}
