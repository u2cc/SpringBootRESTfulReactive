package com.reactive.services;

import com.reactive.entities.DiecastCar;
import com.reactive.entities.DiecastCarAudit;
import com.reactive.entities.User;
import com.reactive.exception.DiecastCarAlreadyExistsException;
import com.reactive.exception.DiecastCarNotFoundException;
import com.reactive.repository.DiecastCarAuditRepository;
import com.reactive.repository.DiecastCarRepository;
import com.reactive.repository.UserRepository;
import com.reactive.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private DiecastCarAuditRepository diecastCarAuditRepository;

    private static final String DELETE = "D";
    private static final String INSERT = "I";
    private static final String UPDATE = "U";

    public Flux<DiecastCar> list() {
        return diecastCarRepository.findAll();
    }

    public Mono<User> getUserWithRoles(String username) {return userRepository.findByUsername(username);}

    public Flux<DiecastCar> findDiecastCarsByBrands(List<String> brands) {return diecastCarRepository.findCarByBrandList(brands);}

    @Transactional
    public Mono<String> addDieCastCar(DiecastCar diecastCar) {
        if (diecastCar.getId() != null) {
            return diecastCarRepository.existsById(diecastCar.getId()).flatMap(exists -> {
                if (exists) {
                    return Mono.error(new DiecastCarAlreadyExistsException());
                } else {
                    return Mono.error(new DiecastCarNotFoundException());
//                    return diecastCarRepository.save(diecastCar).doOnError(System.out::println)
//                            .flatMap(savedDiecastCar -> {
//                                DiecastCarAudit diecastCarAudit = Util.toDiecastCarAudit(savedDiecastCar);
//                                diecastCarAudit.setUser("BZLN388");
//                                diecastCarAudit.setAction(INSERT);
//                                return diecastCarAuditRepository.save(diecastCarAudit);
//                            })
//
//                            .doOnError(System.out::println).thenReturn("Transaction Completed");
                }
            });
        } else {
            return diecastCarRepository.save(diecastCar).doOnError(System.out::println)
                    .flatMap(savedDiecastCar -> {
                        DiecastCarAudit diecastCarAudit = Util.toDiecastCarAudit(savedDiecastCar);
                        diecastCarAudit.setUser("BZLN388");
                        diecastCarAudit.setAction(INSERT);
                        return diecastCarAuditRepository.save(diecastCarAudit);
                    })
                    .doOnError(System.out::println).thenReturn("Transaction Completed");
            //.thenReturn(diecastCar).switchIfEmpty(Mono.error(new DiecastCarSaveException()));
        }
    }
}
