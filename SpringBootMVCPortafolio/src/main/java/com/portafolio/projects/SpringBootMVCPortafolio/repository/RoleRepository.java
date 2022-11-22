package com.portafolio.projects.SpringBootMVCPortafolio.repository;

import com.portafolio.projects.SpringBootMVCPortafolio.models.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {


}
