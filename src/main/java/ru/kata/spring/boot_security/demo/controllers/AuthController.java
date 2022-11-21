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
        try {
            Person originPerson = peopleService.findById(id);
            if (originPerson.equalsN(authentication.getPrincipal())) {
                model.addAttribute("user", originPerson);
                return "/user/index";
            }
        } catch (NullPointerException e) {
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/user")
    public String user(Model model, Authentication authentication) {
        try {
            Person person = (Person) authentication.getPrincipal();
            Person originPerson = peopleService.findById(person.getId());
            if (originPerson.equalsN(person)) {
                model.addAttribute("user", originPerson);
                return "/user/index";
            }
        } catch (NullPointerException e) {
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
}
