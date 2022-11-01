package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Person;
import ru.kata.spring.boot_security.demo.repositories.PeopleRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService implements UserDetailsService {
    private final PeopleRepository peopleRepository;

    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public boolean isAdminOrPerson(Authentication authentication, long id) {
        Person person = (Person) authentication.getPrincipal();
        boolean isAdmin = Objects.requireNonNull(person).getRoles().stream().map(x -> x.getRole().equals("ROLE_ADMIN")).findAny().orElse(false);
        Person per = findOne(id);
        if (person.getPassword().equals(per.getPassword()) || isAdmin) {
            return true;
        }
        return false;
    }

    public List<Person> findAll() {
        return peopleRepository.findAll();
    }

    public Person findOne(long id) {
        Optional<Person> byId = peopleRepository.findById(id);
        return byId.orElse(null);
    }

    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(long id, Person person) {
        person.setId(id);
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(long id) {
        Person one = findOne(id);
        one.getRoles().clear();
        peopleRepository.deleteById(id);
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> byName = peopleRepository.findByName(username);
        if (byName.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User with name: %s not found", username));
        }
        return byName.get();
    }

    @Transactional
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        Optional<Person> byName = peopleRepository.findByEmail(email);
        if (byName.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User with email: %s not found", email));
        }
        return byName.get();
    }
}
