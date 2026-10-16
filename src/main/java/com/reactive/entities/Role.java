package com.reactive.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import java.util.HashSet;
import java.util.Set;

/**
 * @author u2cc
 */
@Table(name="roles")
@Getter
@Setter
public class Role {
    @Id
    @Column("id")
    private Long id;
    private String name;

//    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
    @Transient
    @JsonIgnore
    //Need to break otherwise infinite recursion as a result of mutual reference between users and roles
    private Set<User> users = new HashSet<>();

    public Role() { }

    public Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
