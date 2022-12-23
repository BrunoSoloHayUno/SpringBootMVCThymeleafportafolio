package com.portafolio.projects.SpringBootMVCPortafolio.service;

import com.portafolio.projects.SpringBootMVCPortafolio.models.Role;
import com.portafolio.projects.SpringBootMVCPortafolio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //Buscar nombre de usuario en nuestra base de datos
        com.portafolio.projects.SpringBootMVCPortafolio.models.User appUser = userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no existe!"));

        Set grantList = new HashSet();

        //Crear la lista de los roles/accessos que tienen el usuarios
        for (Role role: appUser.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getDescription());
            grantList.add(grantedAuthority);

        }

      UserDetails user = (UserDetails) new User(appUser.getUsername(), appUser.getPassword(), grantList);

        return user;
    }
}
