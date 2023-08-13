package com.reactive.repository;

import com.reactive.entities.DiecastCar;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author u2cc
 */
public interface DiecastCarRepository extends ReactiveCrudRepository<DiecastCar, Long> {
    @Query(value = "SELECT * FROM diecast_cars d WHERE d.brand IN (:brands) ")
    Flux<DiecastCar> findCarByBrandList(@Param("brands") List<String> brands);
}
