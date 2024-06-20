package com.open.openwikipedia.UserManagement.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.open.openwikipedia.UserManagement.mapper.UserMapper;
import com.open.openwikipedia.common.model.User;
import com.open.openwikipedia.common.model.dto.UserDataDTO;
import com.open.openwikipedia.common.repository.UserRepository;
import com.open.openwikipedia.security.service.UserDetailsImpl;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper mapper;

    public UserDataDTO findUser() throws IllegalArgumentException {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return mapper.toUserData(user);
    }

    public UserDataDTO findUserByEmail(String email) throws NoSuchElementException {
    
        User user = userRepository.findByEmail(email).get();
        return mapper.toUserData(user);
    }

    public UserDataDTO findUserByUsername(String username) throws NoSuchElementException {
    
        User user = userRepository.findByEmail(username).get();
        return mapper.toUserData(user);
    }
}
