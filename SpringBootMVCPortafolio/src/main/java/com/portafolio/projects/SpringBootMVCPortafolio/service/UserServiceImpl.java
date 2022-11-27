package com.portafolio.projects.SpringBootMVCPortafolio.service;

import com.portafolio.projects.SpringBootMVCPortafolio.dto.ChangePasswordForm;
import com.portafolio.projects.SpringBootMVCPortafolio.models.User;
import com.portafolio.projects.SpringBootMVCPortafolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    private boolean checkUsernameAvailable(User user) throws Exception{
        Optional<User> userFound = userRepository.findByUsername(user.getUsername());
        if (userFound.isPresent()){
            throw new Exception("Nombre de usuario no disponible.");
        }
        return true;
    }

    private boolean checkPasswordValid(User user) throws Exception {
        if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()){
            throw new Exception("Confirmar contraseña es obligatorio");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())){
            throw new Exception("La contraseña escrita no es igual en ambos campos.");
        }
        return true;
    }

    @Override
    public User createUser(User user) throws Exception {
        if (checkUsernameAvailable(user) && checkPasswordValid(user)){
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("El usuario no existe."));
        return user;
    }

    @Override
    public User updateUser(User fromUser) throws Exception {
        User toUser = getUserById(fromUser.getId());
        mapUser(fromUser, toUser);
        return userRepository.save(toUser);
    }

    @Override
    public void deleteUser(Long id) throws Exception {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception("Usuario no encontrado -"+this.getClass().getName()));

        userRepository.delete(user);
    }

    @Override
    public User changePassword(ChangePasswordForm form) throws Exception {
        User storedUser = userRepository
                .findById(form.getId())
                .orElseThrow(() -> new Exception("Usuario no encontrado en ChangePassword -"
                        +this.getClass().getName()));


        if(!form.getCurrentPassword().equals(storedUser.getPassword())) {
            throw new Exception("Contraseña actual incorrecta.");
        }

        if (form.getCurrentPassword().equals(form.getNewPassword())) {
            throw new Exception("La nueva contraseña debe ser diferente a la actual!");
        }

        if(!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new Exception("La nueva contraseña y la contraseña actual no coinciden!");
        }

        storedUser.setPassword(form.getNewPassword());
        return userRepository.save(storedUser);
    }

    /**
     * Mapea todo menos la contraseña.
     * @param from
     * @param to
     */
    protected void mapUser(User from,User to) {
        to.setUsername(from.getUsername());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setRoles(from.getRoles());
    }
}
