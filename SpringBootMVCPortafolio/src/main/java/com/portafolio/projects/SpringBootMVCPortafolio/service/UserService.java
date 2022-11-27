package com.portafolio.projects.SpringBootMVCPortafolio.service;

import com.portafolio.projects.SpringBootMVCPortafolio.dto.ChangePasswordForm;
import com.portafolio.projects.SpringBootMVCPortafolio.models.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

     Iterable<User> getAllUsers();

     User createUser(User user) throws Exception;

     User getUserById(Long id) throws Exception;

     User updateUser(User user) throws Exception;

     void deleteUser(Long id) throws Exception;

     User changePassword(ChangePasswordForm form) throws Exception;
}
