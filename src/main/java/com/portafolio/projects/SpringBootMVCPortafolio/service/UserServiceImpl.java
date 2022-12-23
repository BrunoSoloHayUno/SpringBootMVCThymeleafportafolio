package com.portafolio.projects.SpringBootMVCPortafolio.service;

import com.portafolio.projects.SpringBootMVCPortafolio.Exception.CustomFieldValidationException;
import com.portafolio.projects.SpringBootMVCPortafolio.Exception.UsernameOrIdNotFound;
import com.portafolio.projects.SpringBootMVCPortafolio.dto.ChangePasswordForm;
import com.portafolio.projects.SpringBootMVCPortafolio.models.User;
import com.portafolio.projects.SpringBootMVCPortafolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    private boolean checkUsernameAvailable(User user) throws Exception{
        Optional<User> userFound = userRepository.findByUsername(user.getUsername());
        if (userFound.isPresent()){
            throw new CustomFieldValidationException("Nombre de usuario no disponible.","username");
        }
        return true;
    }

    private boolean checkPasswordValid(User user) throws Exception {
        if (user.getConfirmPassword() == null || user.getConfirmPassword().isEmpty()){
            throw new CustomFieldValidationException("Confirmar contraseña es obligatorio","confirmPassword");
        }

        if (!user.getPassword().equals(user.getConfirmPassword())){
            throw new CustomFieldValidationException("La contraseña escrita no es igual en ambos campos.","password");
        }
        return true;
    }

    @Override
    public User createUser(User user) throws Exception {
        if (checkUsernameAvailable(user) && checkPasswordValid(user)){

            String encodePassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodePassword);

            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public User getUserById(Long id) throws UsernameOrIdNotFound {
        return userRepository.findById(id).orElseThrow(() -> new UsernameOrIdNotFound("El Ide del usuario no existe."));
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    public User updateUser(User fromUser) throws Exception {
        User toUser = getUserById(fromUser.getId());
        mapUser(fromUser, toUser);
        return userRepository.save(toUser);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void deleteUser(Long id) throws UsernameOrIdNotFound {
        User user = null;
        try {
            user = userRepository.findById(id)
                    .orElseThrow(() -> new Exception("Usuario no encontrado -"+this.getClass().getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        userRepository.delete(user);
    }

    public boolean isLoggedUserADMIN(){
        return loggedUserHasRole("ROLE_ADMIN");
    }

    public boolean loggedUserHasRole(String role) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails loggedUser = null;
        Object roles = null;
        if (principal instanceof UserDetails) {
            loggedUser = (UserDetails) principal;

            roles = loggedUser.getAuthorities().stream()
                    .filter(x -> role.equals(x.getAuthority() ))
                    .findFirst().orElse(null); //loggedUser = null;
        }
        return roles != null ?true :false;
    }

    public User getLoggedUser() throws Exception {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserDetails loggedUser = null;

        if (principal instanceof UserDetails){
            loggedUser = (UserDetails) principal;
        }

        User myUser = userRepository
                .findByUsername(loggedUser.getUsername()).orElseThrow(() -> new Exception("Problemas obteniendo el usuario de esta sección."));

        return myUser;
    }

    @Override
    public User changePassword(ChangePasswordForm form) throws Exception {
        User storedUser = userRepository
                .findById(form.getId())
                .orElseThrow(() -> new Exception("Usuario no encontrado en ChangePassword -"
                        +this.getClass().getName()));


        if(!isLoggedUserADMIN() && form.getCurrentPassword().equals(storedUser.getPassword())) {
            throw new Exception("Contraseña actual incorrecta.");
        }

        if (form.getCurrentPassword().equals(form.getNewPassword())) {
            throw new Exception("La nueva contraseña debe ser diferente a la actual!");
        }

        if(!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new Exception("La nueva contraseña y la contraseña actual no coinciden!");
        }

        String encodePassword = bCryptPasswordEncoder.encode(form.getNewPassword());
        storedUser.setPassword(encodePassword);
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
