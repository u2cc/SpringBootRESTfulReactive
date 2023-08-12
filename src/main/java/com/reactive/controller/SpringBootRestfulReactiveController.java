package com.reactive.controller;

import com.reactive.entities.DiecastCar;
import com.reactive.services.DiecastCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author u2cc
 */

@RestController
@RequestMapping("/reactive")
public class SpringBootRestfulReactiveController {

    @Autowired
    private DiecastCarService diecastCarService;

    @GetMapping(value = "/getAllDiecastCars", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<DiecastCar> getAll() {
        return diecastCarService.list();
    }

}
