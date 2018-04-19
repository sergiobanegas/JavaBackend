package springskeleton.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import springskeleton.config.property.AdminUserProperties;
import springskeleton.controller.exception.InvalidDataException;
import springskeleton.dao.UserConfirmationTokenDao;
import springskeleton.dao.UserDao;
import springskeleton.model.User;
import springskeleton.model.UserConfirmationToken;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import springskeleton.controller.wrapper.request.UserSignUpRequest;
import springskeleton.model.Gender;
import springskeleton.util.IntegrationTest;

public class AuthServiceIT extends IntegrationTest {

    private AuthService authService;

    private UserDao userDao;

    private UserConfirmationTokenDao userConfirmationTokenDao;

    private AdminUserProperties adminUserProperties;

    @Test
    public void shouldReturnIfAnUserExists() {
        assertTrue(this.authService.existsUser(adminUserProperties.getEmail()));
    }

    @Test
    public void shouldSignUpAnUser() {
        final String email = "test-save-user@test.com";
        final UserSignUpRequest userRequest = new UserSignUpRequest(email, "1234", "Test",
                Gender.MALE);

        this.authService.signUp(userRequest);

        User user = this.userDao.findOneByEmail(email);
        UserConfirmationToken userToken = this.userConfirmationTokenDao.findOneByUser(user);
        assertFalse(user.isEnabled());
        this.authService.confirm(userToken.getToken());
        user = this.userDao.findOne(user.getId());
        assertNotNull(userToken);
        assertTrue(this.authService.existsUser(userRequest.getEmail()));
        assertTrue(user.isEnabled());
    }

    @Test
    public void shouldDeleteExistingDisabledUserWhenSigningUp() {
        final String email = "test-save-disabled-user-duplicated@test.com";
        final UserSignUpRequest userRequest = new UserSignUpRequest(email, "1234", "Test",
                Gender.MALE);

        this.createUserAndCheckIfDisabled(email, userRequest);
        this.createUserAndCheckIfDisabled(email, userRequest);
    }

    @Test(expected = InvalidDataException.class)
    public void shouldThrowExceptionWhenConfirmationOfInvalidCode() {
        this.authService.confirm("1234");
    }

    @Test(expected = InvalidDataException.class)
    public void shouldThrowExceptionWhenConfirmationOfEnabledUser() {
        this.authService.sendConfirmationEmail("test3@test.com");
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setUserConfirmationTokenDao(UserConfirmationTokenDao userConfirmationTokenDao) {
        this.userConfirmationTokenDao = userConfirmationTokenDao;
    }

    @Autowired
    public void setAdminUserProperties(AdminUserProperties adminUserProperties) {
        this.adminUserProperties = adminUserProperties;
    }

    private void createUserAndCheckIfDisabled(String email, UserSignUpRequest userRequest) {
        this.authService.signUp(userRequest);

        final User user = this.userDao.findOneByEmail(email);
        assertFalse(this.authService.existsUser(email));
        assertFalse(user.isEnabled());
    }

}
