package com.reactive.services;

import com.reactive.entities.DiecastCar;
import com.reactive.entities.User;
import com.reactive.repository.DiecastCarRepository;
import com.reactive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author u2cc
 */

@Component
public class DiecastCarService {
    @Autowired
    private DiecastCarRepository diecastCarRepository;

    @Autowired
    private UserRepository userRepository;

    public Flux<DiecastCar> list() {
        return diecastCarRepository.findAll();
    }

    public Mono<User> getUserWithRoles(String username) {return userRepository.findByUsername(username);}

    public Flux<DiecastCar> findDiecastCarsByBrands(List<String> brands) {return diecastCarRepository.findCarByBrandList(brands);}
}
