package com.picpaysimplificado.repositories;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import javax.swing.text.html.parser.Entity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Should find user by document")
    void findUserByDocument() {
        String document = "99999999901";
        UserDTO data = new UserDTO("John", "Doe", document, new BigDecimal(10), "teste@gamil.com", "4444", UserType.COMMON);
        this.createUser(data);

        Optional<User> result = userRepository.findUserByDocument(document);

        assertThat(result.isPresent()).isTrue();
    }

    private User createUser(UserDTO data) {
        User user = new User(data);
        entityManager.persist(user);
        return user;
    }


}