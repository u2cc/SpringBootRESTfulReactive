package com.reactive.repository;

import com.reactive.entities.User;
import reactor.core.publisher.Mono;

/**
 * @author u2cc
 */
public interface CustomUserRepository {
    Mono<User> findByUsername(String username);
}
