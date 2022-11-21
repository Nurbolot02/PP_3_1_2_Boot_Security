package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kata.spring.boot_security.demo.models.Role;

public interface RoleRepository extends JpaRepository<Role, Byte> {
    public Role findByRole(String role);
}
