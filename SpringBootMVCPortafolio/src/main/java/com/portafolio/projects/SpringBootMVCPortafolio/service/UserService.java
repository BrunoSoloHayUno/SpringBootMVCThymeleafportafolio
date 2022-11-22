package com.portafolio.projects.SpringBootMVCPortafolio.service;

import com.portafolio.projects.SpringBootMVCPortafolio.models.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public Iterable<User> getAllUsers();
}
