package com.reactive.services;

import com.reactive.entities.DiecastCar;
import com.reactive.repository.DiecastCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * @author u2cc
 */

@Component
public class DiecastCarService {
    @Autowired
    private DiecastCarRepository diecastCarRepository;

    public Flux<DiecastCar> list() {
        return diecastCarRepository.findAll();
    }
}
