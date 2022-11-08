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
    private final RegistrationService registrationService;
    private final EmailValidator emailValidator;

    @Autowired
    public PeopleController(PeopleService peopleService, RegistrationService registrationService, EmailValidator emailValidator) {
        this.peopleService = peopleService;
        this.registrationService = registrationService;
        this.emailValidator = emailValidator;
    }

    @GetMapping()
    public String index(Model model, Authentication authentication) {
        model.addAttribute("people", peopleService.findAll());
        model.addAttribute("currentPerson", authentication.getPrincipal());
        return "admin/index";
    }

    @GetMapping("/index2")
    public String index2(Model model, Authentication authentication) {
        model.addAttribute("person", peopleService.findAll());
        model.addAttribute("currentPerson", authentication.getPrincipal());
        return "admin/index2";
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


    @GetMapping("/assets/css/{code}.css")
    @ResponseBody
    public ResponseEntity<String> styles(@PathVariable("code") String code) throws IOException {
        StringBuffer sb = new StringBuffer();
        // получаем содержимое файла из папки ресурсов в виде потока
        try (
                InputStream is = getClass().getClassLoader().getResourceAsStream("static/assets/css/" + code + ".css");
                // преобразуем поток в строку
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        ) {

            String line = null;
            while ((line = bf.readLine()) != null) {
                sb.append(line + "\n");
            }
        }


        // создаем объект, в котором будем хранить HTTP заголовки
        final HttpHeaders httpHeaders = new HttpHeaders();
        // добавляем заголовок, который хранит тип содержимого
        httpHeaders.add("Content-Type", "text/css; charset=utf-8");
        // возвращаем HTTP ответ, в который передаем тело ответа, заголовки и статус 200 Ok
        return new ResponseEntity<String>(sb.toString(), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/assets/js/{code}.js")
    @ResponseBody
    public ResponseEntity<String> js(@PathVariable("code") String code) throws IOException {
        StringBuffer sb = new StringBuffer();
        // получаем содержимое файла из папки ресурсов в виде потока
        try (
                InputStream is = getClass().getClassLoader().getResourceAsStream("static/assets/js/" + code + ".js");
                // преобразуем поток в строку
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        ) {

            String line = null;
            while ((line = bf.readLine()) != null) {
                sb.append(line + "\n");
            }
        }


        // создаем объект, в котором будем хранить HTTP заголовки
        final HttpHeaders httpHeaders = new HttpHeaders();
        // добавляем заголовок, который хранит тип содержимого
        httpHeaders.add("Content-Type", "text/css; charset=utf-8");
        // возвращаем HTTP ответ, в который передаем тело ответа, заголовки и статус 200 Ok
        return new ResponseEntity<String>(sb.toString(), httpHeaders, HttpStatus.OK);
    }
}
