package com.reactive.controller;

import com.reactive.entities.DiecastCar;
import com.reactive.entities.User;
import com.reactive.services.DiecastCarService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    @PostMapping(path = "/getUserWithRoles" , produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> getUserWithRoles(@RequestParam(value="username") String username) {
        return diecastCarService.getUserWithRoles(username);
    }

    @RequestMapping(path = "/findDiecastCarsByBrand", method = RequestMethod.GET)
    //@ResponseStatus(HttpStatus.OK)
    public Flux<DiecastCar> findDiecastCarsByBrands(//@ApiParam(value="List of brands", defaultValue="Matchbox, Tongas")
                                                    @Parameter(description = "Brands to search") @RequestParam(value="brands", defaultValue = "Matchbox, Hot Wheels") List<String> brands){
        return diecastCarService.findDiecastCarsByBrands(brands);
    }

}
