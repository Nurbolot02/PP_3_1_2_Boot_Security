package ru.kata.spring.boot_security.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role findByRole(String role) {
        return roleRepository.findByRole(role);
    }

    @PostConstruct
    public void initRoles() {
        Optional<List<Role>> all = Optional.of(roleRepository.findAll());
        if (all.isPresent()) {
            List<Role> roles = all.get();
            if (roles.size() != 2) {
                roleRepository.save(new Role(1,"ROLE_USER"));
                roleRepository.save(new Role(2,"ROLE_ADMIN"));
            }
        }
    }
}
