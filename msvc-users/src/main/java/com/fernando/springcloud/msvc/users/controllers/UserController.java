package com.fernando.springcloud.msvc.users.controllers;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fernando.springcloud.msvc.users.entities.User;
import com.fernando.springcloud.msvc.users.services.IUserService;

@RestController
public class UserController {

    private final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        logger.info("UserController::findById: obteniendo user con id {}", id);
        return userService.findById(id).map(u -> ResponseEntity.ok(u))
            .orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable String username){
        logger.info("UserController::findByUsername login con {}", username);
        return userService.findByUsername(username).map(u -> ResponseEntity.ok(u))
            .orElseGet(()->ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> findAll() {
        logger.info("UserController::findAll listando usuarios");
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        logger.info("UserController::create: creando {}", user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(user));
        // return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@RequestBody User user, @PathVariable Long id) {
        logger.info("UserController::update: actualizando {}", user);
        return userService.update(user, id).map(userUpdated -> ResponseEntity.status(HttpStatus.CREATED).body(userUpdated))
            .orElseGet(() -> ResponseEntity.notFound().build());

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        logger.info("UserController::delete: eliminando user con id {}", id);
        return ResponseEntity.noContent().build();
    }
}
