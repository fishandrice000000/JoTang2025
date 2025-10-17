package recruit.jotang2025.info_manager;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import io.jsonwebtoken.Claims;
import recruit.jotang2025.info_manager.controller.LogInController;
import recruit.jotang2025.info_manager.pojo.User;
import recruit.jotang2025.info_manager.utils.JwtUtils;

@SpringBootTest
public class LogInApplicationTests {
    @Autowired
    private LogInController logInController;
    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void testLogInSuccessfully() {
        User emailUser = new User(); // userId: 2
        User mobileUser = new User(); // userId: 1
        ResponseEntity<String> emailEntity;
        ResponseEntity<String> mobileEntity;
        String jwtEmail;
        String jwtMobile;
        Claims payloadEmail;
        Claims payloadMobile;

        emailUser.setEmail("1472608489@qq.com");
        emailUser.setPassword("password");
        mobileUser.setMobile("19170833183");
        mobileUser.setPassword("password");

        emailEntity = logInController.logIn(emailUser);
        mobileEntity = logInController.logIn(mobileUser);

        jwtEmail = emailEntity.getBody();
        jwtMobile = mobileEntity.getBody();

        payloadEmail = jwtUtils.parse(jwtEmail);
        payloadMobile = jwtUtils.parse(jwtMobile);

        assertEquals("2", payloadEmail.get("sub"));
        assertEquals("1", payloadMobile.get("sub"));
    }

    @Test
    void testLogInUnsuccessfully() {
        User badPasswordUser = new User();
        User badEmailUser = new User();
        ResponseEntity<String> badPasswordEntity;
        ResponseEntity<String> badEmailEntity;

        badPasswordUser.setMobile("19170833183");
        badPasswordUser.setPassword("badpassword");
        badEmailUser.setEmail("bademail@bad.com");
        badEmailUser.setPassword("password");

        badPasswordEntity = logInController.logIn(badPasswordUser);
        badEmailEntity = logInController.logIn(badEmailUser);
        assertEquals(HttpStatusCode.valueOf(404), badPasswordEntity.getStatusCode());
        assertEquals(HttpStatusCode.valueOf(404), badEmailEntity.getStatusCode());
    }
}
