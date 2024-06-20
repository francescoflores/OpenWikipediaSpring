package com.open.openwikipedia.UserManagement.mapper;

import org.springframework.stereotype.Component;

import com.open.openwikipedia.common.model.User;
import com.open.openwikipedia.common.model.dto.UserDataDTO;

@Component
public class UserMapper {
    public UserDataDTO toUserData(User user){
        UserDataDTO response = new UserDataDTO(user.getId(), user.getEmail(), user.getUsername(), user.getCreatedAt());
        return response;
    }
}
