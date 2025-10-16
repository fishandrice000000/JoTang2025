package recruit.jotang2025.info_manager;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import recruit.jotang2025.info_manager.controller.UserController;
import recruit.jotang2025.info_manager.exception.UserNotFoundException;
import recruit.jotang2025.info_manager.pojo.User;
import recruit.jotang2025.info_manager.utils.AuthenticationUtils;

@SpringBootTest
@Transactional
public class UserApplicationTests {
    @Autowired
    UserController userController;

    User testNewUser;
    Authentication adminAuth;
    Authentication userAuth;

    @BeforeEach
    public void init() {
        adminAuth = AuthenticationUtils.generateAuthentication("1", "admin");
        userAuth = AuthenticationUtils.generateAuthentication("2", "user");

        testNewUser = new User();
        testNewUser.setUsername("test");
        testNewUser.setPassword("password");
        testNewUser.setEmail("test@test.com");
        testNewUser.setMobile("1145141919810");
    }

    @Test
    void testRegisterNoException() {
        AuthenticationUtils.setAuthentication(adminAuth);

        User newUser = userController.register(testNewUser).getBody();
        User foundUser = userController.queryUser(newUser.getUserId()).getBody().get(0);

        assertNotNull(newUser);
        assertNotNull(newUser.getUserId());
        assertEquals(User.Status.active, foundUser.getStatus());
        assertEquals(User.Role.user, foundUser.getRole());
    }

    @Test
    void testRegisterWithExeption() {
        AuthenticationUtils.setAuthentication(adminAuth);

        testNewUser.setMobile(null);
        testNewUser.setEmail(null);

        assertThrows(IllegalArgumentException.class, () -> userController.register(testNewUser));
        assertThrows(IllegalArgumentException.class, () -> userController.register(null));
    }

    @Test
    void testDeleteNoException() {
        AuthenticationUtils.setAuthentication(userAuth);

        userController.deleteUser(2L);

        AuthenticationUtils.setAuthentication(adminAuth);
        User foundUser = userController.queryUser(2L).getBody().get(0);

        assertEquals(User.Status.inactive, foundUser.getStatus());
    }

    @Test
    void testDeleteWithException() {
        AuthenticationUtils.setAuthentication(userAuth);

        assertThrows(UserNotFoundException.class, () -> userController.deleteUser(101010101L));
        assertThrows(AccessDeniedException.class, () -> userController.deleteUser(3L));
    }

    @Test
    void testUpdateNoException() {
        AuthenticationUtils.setAuthentication(adminAuth);

        User foundUser = userController.queryUser(2L).getBody().get(0);
        String beforeMobile = foundUser.getMobile();
        foundUser.setMobile("1111111111");

        userController.updateUser(foundUser);

        foundUser = userController.queryUser(2L).getBody().get(0);

        assertNotEquals(beforeMobile, foundUser.getMobile());
    }

    @Test
    void testUpdateWithException() {
        AuthenticationUtils.setAuthentication(adminAuth);

        User foundUser = userController.queryUser(3L).getBody().get(0);
        User newFoundUser = userController.queryUser(2L).getBody().get(0);
        User userWithIllegalChange = new User(newFoundUser.getUserId(), User.Role.admin, newFoundUser.getStatus(),
                newFoundUser.getUsername(), newFoundUser.getPassword(), newFoundUser.getEmail(),
                newFoundUser.getMobile(), newFoundUser.getCreateTime(), newFoundUser.getUpdateTime());

        AuthenticationUtils.setAuthentication(userAuth);
        assertThrows(AccessDeniedException.class, () -> userController.updateUser(foundUser));
        assertThrows(AccessDeniedException.class, () -> userController.updateUser(userWithIllegalChange));
        assertThrows(IllegalArgumentException.class, () -> userController.updateUser(null));
    }

    @Test
    void testQuery() {
        List<User> userById = userController.queryUser(1L).getBody();
        List<User> users = userController.queryUser(null).getBody();

        assertEquals(1, userById.size());
        assertNotEquals(1, users.size());
    }
}
