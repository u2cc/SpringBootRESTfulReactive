package com.reactive.security;

import com.reactive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author u2cc
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.cors(ServerHttpSecurity.CorsSpec::disable).csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(
                (authorize) -> authorize.pathMatchers("/**").permitAll()
        );
        return http.build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {

        return new ReactiveUserDetailsService() {
            @Override
            public Mono<UserDetails> findByUsername(String username) {
                    return userRepository.findByUsername(username).map(user -> {
//                        List<GrantedAuthority> authorities = user.getRoles()
//                                .stream()
//                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
//                                .collect(Collectors.toList());
//
//                        return User.withUsername(user.getUsername())
//                                .password(user.getPassword())
//                                .authorities(authorities) // Set authorities for the user
//                                .build();
//                         we do not need to add authorities here as we are doing so in User class itself by overriding getAuthorities
                        return User.withUserDetails(user).passwordEncoder(passwordEncoder::encode).build();
                    }).switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found: " + username)));

            }
        };

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
