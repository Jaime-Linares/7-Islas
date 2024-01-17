package org.juego.siete.islas.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import org.juego.siete.islas.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AuthoritiesServiceTest {

    @Autowired
    private AuthoritiesService authService;

    @Test
    void shouldFindAllAuthorities() {
        List<Authorities> auths = (List<Authorities>) this.authService.findAll();
        assertEquals(2, auths.size());
    }

    @Test
    void shouldFindAuthoritiesByAuthority() {
        Authorities auth = this.authService.findByAuthority("ADMIN");
        assertEquals("ADMIN", auth.getAuthority());
    }

    @Test
    void shouldNotFindAuthoritiesByIncorrectAuthority() {
        assertThrows(ResourceNotFoundException.class, () -> this.authService.findByAuthority("authnotexists"));
    }

    @Test
    @Transactional
    void shouldInsertAuthorities() {
        int count = ((Collection<Authorities>) this.authService.findAll()).size();

        Authorities auth = new Authorities();
        auth.setAuthority("PLAYER");

        this.authService.saveAuthorities(auth);
        assertNotEquals(0, auth.getId().longValue());
        assertNotNull(auth.getId());

        int finalCount = ((Collection<Authorities>) this.authService.findAll()).size();
        assertEquals(count + 1, finalCount);
    }
}
