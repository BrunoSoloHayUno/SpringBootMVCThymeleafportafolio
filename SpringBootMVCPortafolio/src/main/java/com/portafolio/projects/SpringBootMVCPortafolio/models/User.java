package com.portafolio.projects.SpringBootMVCPortafolio.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@Getter @Setter
@ToString @EqualsAndHashCode
public class User implements Serializable {

    private static final long serialVersionUID = 6833167247955613395L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name="native", strategy = "native")
    private Long id;

    @Column
    @NotBlank
    @Size(min=5,max=8,message="No se cumple las reglas del tamaño")
    private String firstName;

    @Column
    @NotBlank
    private String lastName;

    @Column(unique = true)
    @NotBlank @Email
    private String email;

    @Column(unique = true)
    @NotBlank
    private String username;

    @Column
    @NotBlank
    private String password;

    /*esta anotacion sirve para que el CRUD ignore hacer
    * cualquier operacion con esta variable.
    * en otras palabras no se va a guardar en la BD
    * se suele usar para omitir valores en la base de datos
    * que solo nesesitamos para campos especificos.*/
    @Transient
    private String confirmPassword;

    /*Busca el id del objeto "Role" y lo asigna como clave foránea*/
    @Size(min=1)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    /*A diferencia de otras colecciones de objetos Set obliga a los
     * objetos que contenga a tener un valor único siempre.*/
    private Set<Role> roles;

    public User (){
        super();
    }

    public User(Long id) {
        super();
        this.id = id;
    }
}
