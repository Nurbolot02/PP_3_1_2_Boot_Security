package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.services.PeopleService;
import ru.kata.spring.boot_security.demo.services.RegistrationService;
import ru.kata.spring.boot_security.demo.util.EmailValidator;

import javax.validation.Valid;

/**
 * @author Neil Alishev
 */
@Controller
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final EmailValidator emailValidator;
    private final PeopleService peopleService;

    @Autowired
    public AuthController(RegistrationService registrationService, EmailValidator emailValidator, PeopleService peopleService) {
        this.registrationService = registrationService;
        this.emailValidator = emailValidator;
        this.peopleService = peopleService;
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") long id, Model model, Authentication authentication) {
        Person originPerson = peopleService.findOne(id);
        if (originPerson.equalsN(authentication.getPrincipal())) {
            model.addAttribute("person", originPerson);
            return "auth/show";
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") long id, Authentication authentication) {
        Person originPerson = peopleService.findOne(id);
        if (originPerson.equalsN(authentication.getPrincipal())) {
            model.addAttribute("person", originPerson);
            return "auth/edit";
        }
        return "redirect:/auth/login";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable("id") long id, Authentication authentication) {
        Person originPerson = peopleService.findOne(id);
        if (originPerson.equalsN(authentication.getPrincipal())) {
            if (!person.getEmail().equals(originPerson.getEmail())) {
                emailValidator.validate(person, bindingResult);
            }

            if (bindingResult.hasErrors())
                return "auth/edit";

            registrationService.update(id, person);
            return String.format("redirect:/auth/%d", id);
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("person") Person person) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String performRegistration(@ModelAttribute("person") @Valid Person person,
                                      BindingResult bindingResult) {
        emailValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "/auth/registration";
        registrationService.register(person);

        return "redirect:/auth/login";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id, Authentication authentication) {
        Person originPerson = peopleService.findOne(id);
        if (originPerson.equalsN(authentication.getPrincipal())) {
            peopleService.delete(id);
        }
        return "redirect:/auth/login";
    }
}
