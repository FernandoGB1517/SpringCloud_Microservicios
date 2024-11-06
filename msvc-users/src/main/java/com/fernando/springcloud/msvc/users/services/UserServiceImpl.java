package com.fernando.springcloud.msvc.users.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernando.springcloud.msvc.users.entities.Role;
import com.fernando.springcloud.msvc.users.entities.User;
import com.fernando.springcloud.msvc.users.repositories.RoleRepository;
import com.fernando.springcloud.msvc.users.repositories.UserRepository;


@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

     @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRoles(getRoles(user));
        user.setEnabled(true);
        
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(User user, Long id) {
        Optional<User> userOptional = this.findById(id);

        return userOptional.map(userDb -> {
            userDb.setEmail(user.getEmail());
            userDb.setUsername(user.getUsername());

            if(user.isEnabled() == null){
                userDb.setEnabled(true);
            }else{
                userDb.setEnabled(user.isEnabled());
            }

            userDb.setRoles(getRoles(user));

            return Optional.of(userRepository.save(userDb));
            
        }).orElseGet(()-> Optional.empty());
        
        
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private List<Role> getRoles(User user) {
        List<Role> roles = new ArrayList<>();
        Optional<Role> roleOptional = roleRepository.findByName("ROLE_USER");
        // roleOptional.ifPresent(role -> roles.add(role));
        roleOptional.ifPresent(roles::add);

        if(user.isAdmin()){
            Optional<Role> adminRoleOptional = roleRepository.findByName("ROLE_ADMIN");
            // adminRoleOptional.ifPresent(role -> roles.add(role));
            adminRoleOptional.ifPresent(roles::add);
        }

        return roles;
    }

}

