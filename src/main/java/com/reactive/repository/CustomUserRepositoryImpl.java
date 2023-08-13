package com.reactive.repository;

import com.reactive.entities.Role;
import com.reactive.entities.User;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

/**
 * @author u2cc
 */

@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private DatabaseClient client;

    public CustomUserRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Mono<User> findByUsername(String username) {
        String queryUser = "SELECT * FROM users WHERE username = :username";

        return client.sql(queryUser)
                .bind("username", username)
                .map((row, metadata) -> {
                    User user = new User();
                    user.setId(row.get("id", Long.class));
                    user.setUsername(row.get("username", String.class));
                    user.setFullname(row.get("fullname", String.class));
                    user.setPassword(row.get("password", String.class));
                    user.setCreated_at(row.get("created_at", ZonedDateTime.class));
                    user.setModified_at(row.get("modified_at", ZonedDateTime.class));
                    return user;
                }).first()
                .flatMap(user -> client.sql("SELECT r.id, r.name " +
                                "FROM roles r " +
                                "JOIN user_role_mapping urm ON r.id = urm.role_id " +
                                "WHERE urm.user_id = (SELECT id FROM users WHERE username = :username)")
                        .bind("username", username)
                        .map((row, metadata) -> {
                            Role role = new Role();
                            role.setId(row.get("id", Long.class));
                            role.setName(row.get("name", String.class));
                            return role;
                        }).all().collectList().map(roles -> {
                            user.setRoles(roles);
                            return user;
                        }));
    }
}
