package com.portafolio.projects.SpringBootMVCPortafolio.service;

import com.portafolio.projects.SpringBootMVCPortafolio.models.User;
import com.portafolio.projects.SpringBootMVCPortafolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
