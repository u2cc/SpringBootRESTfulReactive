package com.reactive.repository;

import com.reactive.entities.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author u2cc
 */
public interface UserRepository extends ReactiveCrudRepository<User, Long>, CustomUserRepository {
}
