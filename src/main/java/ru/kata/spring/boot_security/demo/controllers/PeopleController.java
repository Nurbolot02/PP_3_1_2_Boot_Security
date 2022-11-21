package ru.kata.spring.boot_security.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.services.PeopleService;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.util.EmailValidator;

import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Nurbolot Gulamidinov
 */
@Controller
@RequestMapping("/admin")
public class PeopleController {

    private final PeopleService peopleService;
    private final RoleService roleService;
    private final RegistrationService registrationService;
    private final EmailValidator emailValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, RoleService roleService, RegistrationService registrationService, EmailValidator emailValidator) {
        this.peopleService = peopleService;
        this.roleService = roleService;
        this.registrationService = registrationService;
        this.emailValidator = emailValidator;
    }

    @GetMapping()
    public String index(@ModelAttribute("person") Person person, Model model, Authentication authentication) {
        model.addAttribute("people", peopleService.findAll());
        model.addAttribute("currentPerson", (Person) authentication.getPrincipal());
        model.addAttribute("roles", roleService.getRoles());
        return "admin/index";
    }

    @GetMapping("/{id}")
    public String showAdmin(@PathVariable("id") long id, Model model) {
        model.addAttribute("person", peopleService.findById(id));
        return "admin/show";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult, Model model, Authentication authentication) {
        emailValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("people", peopleService.findAll());
            model.addAttribute("currentPerson", (Person) authentication.getPrincipal());
            model.addAttribute("roles", roleService.getRoles());
            return "/admin/indexNewUser";
        }


        registrationService.register(person);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") long id) {
        model.addAttribute("person", peopleService.findById(id));
        return "admin/edit";
    }

    @PatchMapping("/edit")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        Person originPerson = peopleService.findById(person.getId());
        if (!person.getEmail().equals(originPerson.getEmail())) {
            emailValidator.validate(person, bindingResult);
        }

        if (bindingResult.hasErrors())
            return "admin/index";

        registrationService.update(person.getId(), person);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id) {
        peopleService.delete(id);
        return "redirect:/admin";
    }


}
