package ru.kata.spring.boot_security.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.services.PeopleService;
@Component
public class EmailValidator implements Validator {
    private final PeopleService peopleService;

    @Autowired
    public EmailValidator(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;

        try {
            peopleService.loadUserByEmail(person.getEmail());
        } catch (Exception ignored) {
            return; // все ок, пользователь не найден
        }

        errors.rejectValue("email", "", "Человек с такой почтой уже существует");
    }
}
