package ru.kata.spring.boot_security.demo.models;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Nurbolot Gulamidinov
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Person")
public class Person implements UserDetails {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotEmpty(message = "first name should not be empty")
    @Size(min = 3, max = 30, message = "first name should be between 3 and 30 characters")
    @Column(name = "first_name")
    private String firstName;

    @NotEmpty(message = "last name should not be empty")
    @Size(min = 3, max = 30, message = "last name should be between 3 and 30 characters")
    @Column(name = "last_name")
    private String lastName;


    @Min(value = 0, message = "age should be grater than 0")
    private byte age;

    @Column(name = "email")
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    @Size(min = 8, max = 100, message = "Password should be between 8 and 100 characters")
    @NotEmpty
    private String password;


    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Role> roles;

    public void createRoles(Role role) {
        this.roles = new ArrayList<Role>();
        roles.add(role);
    }

    public void createRoles(List<Role> roles) {
        this.roles = new ArrayList<Role>(roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && age == person.age && Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName) && Objects.equals(email, person.email) && Objects.equals(password, person.password) && Objects.equals(roles, person.roles);
    }

    public boolean equalsN(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && age == person.age && firstName.equals(person.firstName) && lastName.equals(person.lastName) && email.equals(person.email) && password.equals(person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, age, email, password, roles);
    }
}