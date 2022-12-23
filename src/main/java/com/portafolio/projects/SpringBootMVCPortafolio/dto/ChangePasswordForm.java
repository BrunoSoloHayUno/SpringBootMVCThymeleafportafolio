package com.portafolio.projects.SpringBootMVCPortafolio.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@ToString @EqualsAndHashCode
public class ChangePasswordForm {

    @NotNull
    private Long id;

    @NotBlank(message = "Este campo no debe estar vació.")
    public String currentPassword;

    public String newPassword;

    public String confirmPassword;

    public ChangePasswordForm (){

    }

    public ChangePasswordForm(Long id) {
        this.id = id;
    }
}
