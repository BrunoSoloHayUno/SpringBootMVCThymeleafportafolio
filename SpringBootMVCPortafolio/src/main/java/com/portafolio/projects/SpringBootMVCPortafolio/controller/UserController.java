package com.portafolio.projects.SpringBootMVCPortafolio.controller;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.portafolio.projects.SpringBootMVCPortafolio.models.User;
import com.portafolio.projects.SpringBootMVCPortafolio.repository.RoleRepository;
import com.portafolio.projects.SpringBootMVCPortafolio.service.UserService;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

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

        //Mira siguiente sección para más información.
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
        }catch(Exception e){
            model.addAttribute("listErrorMessage", e.getMessage());
        }
        return getUserForm(model);
    }

    @PostMapping("/editUser")
    public String postEditUserForm(@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model){
        if(result.hasErrors()){
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
            model.addAttribute("editMode", "true");
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
            }
        }

        model.addAttribute("userList", userService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
        return "user-form/user-view";
    }

    @PostMapping("/userForm")
    public String postUserForm(@Valid @ModelAttribute("userForm")User user, BindingResult result, ModelMap model){
        if(result.hasErrors()){
            model.addAttribute("userForm", user);
            model.addAttribute("formTab", "active");
        }
        else{
            try {
                userService.createUser(user);
                model.addAttribute("userForm", new User());
                model.addAttribute("listTab", "active");
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
}
