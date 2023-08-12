package com.reactive.repository;

import com.reactive.entities.DiecastCar;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author u2cc
 */
public interface DiecastCarRepository extends ReactiveCrudRepository<DiecastCar, Long> {

}
