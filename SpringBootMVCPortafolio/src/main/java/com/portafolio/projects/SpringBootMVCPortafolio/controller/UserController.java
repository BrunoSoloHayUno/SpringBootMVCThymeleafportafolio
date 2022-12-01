package com.portafolio.projects.SpringBootMVCPortafolio.controller;

import com.portafolio.projects.SpringBootMVCPortafolio.Exception.CustomFieldValidationException;
import com.portafolio.projects.SpringBootMVCPortafolio.Exception.UsernameOrIdNotFound;
import com.portafolio.projects.SpringBootMVCPortafolio.dto.ChangePasswordForm;
import com.portafolio.projects.SpringBootMVCPortafolio.models.User;
import com.portafolio.projects.SpringBootMVCPortafolio.repository.RoleRepository;
import com.portafolio.projects.SpringBootMVCPortafolio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Controller
public class UserController {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserService userService;

    @GetMapping({"/","login"})
    public String index(){
        return "index";
    }

    @GetMapping("/userForm")
    public String getUserForm(Model model){
        model.addAttribute("userForm", new User());
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("listTab", "active");
        return "user-form/user-view";
    }

    @GetMapping("/editUser/{id}")
    public String getEditUserForm(Model model, @PathVariable(name = "id") Long id) throws Exception{
        User user = userService.getUserById(id);

        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("userForm", user);
        model.addAttribute("formTab", "active");

        //Activa el tab del formulario.
        model.addAttribute("editMode", true);


        model.addAttribute("passwordForm", new ChangePasswordForm(user.getId()));
        return "user-form/user-view";
    }

    @GetMapping("/userForm/cancel")
    public String cancelEditUser(ModelMap model){
        return "redirect:/userForm";
    }

    @GetMapping("deleteUser/{id}")
    public String deleteUser(Model model, @PathVariable(name = "id")Long id){
        try{
            userService.deleteUser(id);
        }catch(UsernameOrIdNotFound uoin){
            model.addAttribute("listErrorMessage", uoin.getMessage());
        }
        return getUserForm(model);
    }

    @PostMapping("/editUser")
    public String postEditUserForm(@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model){
        if(result.hasErrors()){
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
            model.addAttribute("editMode", "true");
            model.addAttribute("passwordForm",new ChangePasswordForm(user.getId()));
        }else{
            try{
                userService.updateUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");
            }catch (Exception e){
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab", "active");
                model.addAttribute("userList", userService.getAllUsers());
                model.addAttribute("editMode", "true");
                model.addAttribute("passwordForm",new ChangePasswordForm(user.getId()));
            }
        }

        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form/user-view";
    }

    @PostMapping("/userForm")
    public String createUser(@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model){
        if(result.hasErrors()){
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
        }
        else{
            try {
                userService.createUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");
            }catch(CustomFieldValidationException cfve){
                    result.rejectValue(cfve.getFieldName(), null, cfve.getMessage());
                    model.addAttribute("userForm", user);
                    model.addAttribute("formTab","active");
                    model.addAttribute("userList", userService.getAllUsers());
                    model.addAttribute("roles",roleRepository.findAll());
            }
            catch(Exception e){
                model.addAttribute("formErrorMessage", e.getMessage());
                model.addAttribute("userForm", user);
                model.addAttribute("formTab","active");
                model.addAttribute("userList", userService.getAllUsers());
                model.addAttribute("roles", roleRepository.findAll());
            }
        }
        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form/user-view";
    }

    @PostMapping("editUser/changePassword")
    public ResponseEntity<String> postEditUserChangePassword(@Valid @RequestBody ChangePasswordForm form, Errors errors){

        try{
            //If error, just return a 400 bad request, along with the error message.
           if(errors.hasErrors()){
               String resultado = errors.getAllErrors()
                       .stream().map(x -> x.getDefaultMessage())
                       .collect(Collectors.joining(""));

               throw new Exception(resultado);
           }
           userService.changePassword(form);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("success");
    }
}
