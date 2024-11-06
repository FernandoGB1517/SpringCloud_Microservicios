package com.fernando.springcloud.msvc.oauth.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fernando.springcloud.msvc.oauth.models.User;

import io.micrometer.tracing.Tracer;

@Service
public class UserService implements UserDetailsService{

    private final Logger logger= LoggerFactory.getLogger(UserService.class);

    @Autowired
    private WebClient client;

    @Autowired
    private Tracer tracer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Ingresando al proceso de login UserService::loadUserByUsername con {}", username);

        Map<String, String> params = new HashMap<>();
        params.put("username", username);

        try {
            User user = client.get().uri("/username/{username}", params)
                .accept(MediaType.APPLICATION_JSON)
            .retrieve() 
                .bodyToMono(User.class)
                    .block();

            List<GrantedAuthority> roles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());

            logger.info("Se ha realizado el login con exit by username: {}", user);
            tracer.currentSpan().tag("success.login.message", "Se ha realizado el login con exit by username: " + user);

            return new org.springframework.security.core.userdetails.User(
                user.getUsername(), 
                user.getPassword(), 
                user.isEnabled(),
                true, 
                true, 
                true, 
                roles);

        } catch (WebClientResponseException e) {
            String error = "Error en el login, no existe el users '" + username + "'' en el sistema";

            logger.error(error);
            tracer.currentSpan().tag("error.login.message", error + " : " + e.getMessage());

            throw new UsernameNotFoundException(error);
        }
    }

}
