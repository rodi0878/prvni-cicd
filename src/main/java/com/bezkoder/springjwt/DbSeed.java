package com.bezkoder.springjwt;

import com.bezkoder.springjwt.models.ERole;
import com.bezkoder.springjwt.models.Role;
import com.bezkoder.springjwt.models.User;
import com.bezkoder.springjwt.repository.RoleRepository;
import com.bezkoder.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DbSeed implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("SEEEDING DDDDBBBBB");
        Role ruser = new Role(ERole.ROLE_USER);
        Role rmod = new Role(ERole.ROLE_MODERATOR);
        Role radmin = new Role(ERole.ROLE_ADMIN);
        roleRepository.save(ruser);
        roleRepository.save(rmod);
        roleRepository.save(radmin);

        User user = new User("user", "user@domain.com", passwordEncoder.encode("password"));
        user.getRoles().add(ruser);
        user.getRoles().add(radmin);
        userRepository.save(user);

    }

}
