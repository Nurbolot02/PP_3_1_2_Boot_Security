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
import ru.kata.spring.boot_security.demo.util.PersonValidator;

import javax.validation.Valid;

/**
 * @author Nurbolot Gulamidinov
 */
@Controller
@RequestMapping("/admin")
public class PeopleController {

    private final PeopleService peopleService;
    private final RegistrationService registrationService;
    private final PersonValidator personValidator;
    private final EmailValidator emailValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, RegistrationService registrationService, PersonValidator personValidator, EmailValidator emailValidator) {
        this.peopleService = peopleService;
        this.registrationService = registrationService;
        this.personValidator = personValidator;
        this.emailValidator = emailValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("person", peopleService.findAll());
        return "admin/index";
    }

    @GetMapping("/{id}")
    public String showAdmin(@PathVariable("id") long id, Model model) {
        model.addAttribute("person", peopleService.findOne(id));
        return "admin/show";
    }

    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "admin/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        emailValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors())
            return "admin/new";

        registrationService.register(person);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") long id) {
        model.addAttribute("person", peopleService.findOne(id));
        return "admin/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult,
                         @PathVariable("id") long id) {
        Person originPerson = peopleService.findOne(id);
        if (!person.getName().equals(originPerson.getName())) {
            personValidator.validate(person, bindingResult);
        }
        if (!person.getEmail().equals(originPerson.getEmail())) {
            emailValidator.validate(person, bindingResult);
        }

        if (bindingResult.hasErrors())
            return "admin/edit";

        registrationService.update(id, person);
        return "redirect:/admin";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") long id) {
        peopleService.delete(id);
        return "redirect:/admin";
    }
}
