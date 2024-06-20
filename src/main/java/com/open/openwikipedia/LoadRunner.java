package com.open.openwikipedia;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.open.openwikipedia.common.model.Article;
import com.open.openwikipedia.common.model.Role;
import com.open.openwikipedia.common.model.User;
import com.open.openwikipedia.common.repository.ArticleRepository;
import com.open.openwikipedia.common.repository.RoleRepository;
import com.open.openwikipedia.common.repository.UserRepository;

@Component
public class LoadRunner implements CommandLineRunner {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    public void resetCounters() {
        List<Article> articles = articleRepository.findAll();
        articles.forEach(article -> {
            article.setCounter(0L);
            articleRepository.save(article);
        });
    }

    @Override
    public void run(String... args) throws Exception {
        resetCounters();
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        if (!userRepository.existsByEmail("mario.rossi@fmail.com") && !userRepository.existsByUsername("mario_rossi")) {
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);

            User u_user = new User("mario_rossi", "mario.rossi@fmail.com", encoder.encode("mygoodpassword"));
            u_user.setRoles(roles);
            userRepository.save(u_user);
        }
    }
}
