package com.dotdot.marketplace.user.usermapper;

import com.dotdot.marketplace.user.dto.UserRequestDto;
import com.dotdot.marketplace.user.entity.User;
import com.dotdot.marketplace.user.entity.UserRole;

public class UserMapper {

    public static User toEntity(UserRequestDto dto) {
        if (dto == null) return null;

        User user = new User();
        user.setLogin(dto.getLogin());
        user.setFullName(dto.getFullName());
        user.setPassword(dto.getPassword());
        user.setRole(UserRole.USER);
        return user;
    }

}
