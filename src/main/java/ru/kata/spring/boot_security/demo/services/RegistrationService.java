package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.PeopleRepository;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.Optional;

/**
 * @author Neil Alishev
 */
@Service
public class RegistrationService {

    private final PeopleRepository peopleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(PeopleRepository peopleRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.peopleRepository = peopleRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(Person person) {

        person.setPassword(passwordEncoder.encode(person.getPassword()));

        person.addRole(roleRepository.findByRole("ROLE_USER"));
        if ( person.role() != null && person.role().equals("ROLE_ADMIN")) {
            person.addRole(roleRepository.findByRole(person.role()));
        }
        peopleRepository.save(person);
    }

    @Transactional
    public void update(long id, Person person) {
        Optional<Person> byId1 = peopleRepository.findById(id);
        if (byId1.isPresent()) {
            if (person.getId() != 1) {
                person.setPassword(passwordEncoder.encode(person.getPassword()));
                person.addRole(roleRepository.findByRole("ROLE_USER"));
                if (person.role().equals("ROLE_ADMIN")) {
                    person.addRole(roleRepository.findByRole(person.role()));
                }
                peopleRepository.save(person);
            }
        }
    }
}
