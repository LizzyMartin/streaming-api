package br.com.fiap.streaming.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserTest {

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
    }

    @Test
    public void testGetAndSetId() {
        String id = "123";
        user.setId(id);
        assertEquals(id, user.getId());
    }

    @Test
    public void testGetAndSetUsername() {
        String username = "john_doe";
        user.setUsername(username);
        assertEquals(username, user.getUsername());
    }

    @Test
    public void testGetAndSetPassword() {
        String password = "password123";
        user.setPassword(password);
        assertEquals(password, user.getPassword());
    }

    @Test
    public void testIsAndSetEnabled() {
        user.setEnabled(true);
        assertTrue(user.isEnabled());

        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    public void testIsAndSetAccountNonExpired() {
        user.setAccountNonExpired(true);
        assertTrue(user.isAccountNonExpired());

        user.setAccountNonExpired(false);
        assertFalse(user.isAccountNonExpired());
    }

    @Test
    public void testIsAndSetAccountNonLocked() {
        user.setAccountNonLocked(true);
        assertTrue(user.isAccountNonLocked());

        user.setAccountNonLocked(false);
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    public void testIsAndSetCredentialsNonExpired() {
        user.setCredentialsNonExpired(true);
        assertTrue(user.isCredentialsNonExpired());

        user.setCredentialsNonExpired(false);
        assertFalse(user.isCredentialsNonExpired());
    }

    @Test
    public void testGetAndSetFavoriteVideos() {
        Collection<String> favoriteVideos = Arrays.asList("video1", "video2", "video3");
        user.setFavoriteVideos(favoriteVideos);
        assertEquals(favoriteVideos, user.getFavoriteVideos());
    }

    @Test
    public void testGetAndSetHistory() {
        Collection<Video> history = Arrays.asList(new Video(), new Video(), new Video());
        user.setHistory(history);
        assertEquals(history, user.getHistory());
    }
}