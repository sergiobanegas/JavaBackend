package springskeleton.controller;

import springskeleton.config.Endpoints;
import springskeleton.config.property.JWTProperties;
import springskeleton.controller.wrapper.request.ChangePasswordRequest;
import springskeleton.controller.wrapper.request.UserPatchRequest;
import springskeleton.controller.wrapper.response.AccountResponse;
import springskeleton.dao.UserDao;
import springskeleton.model.Gender;
import springskeleton.model.User;
import springskeleton.util.ControllerTest;
import springskeleton.util.RequestData;
import springskeleton.util.ResponseData;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.servlet.http.Cookie;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class AccountControllerIT extends ControllerTest {

    private UserDao userDao;

    private JWTProperties jwtConfig;

    @Before
    public void init() {
        super.setUpHttpUtils();
    }

    @Test
    public void shouldReturnAccountData() throws Exception {
        final ResponseData responseLogin = super.httpUtils.loginWithAdmin();
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        RequestData requestData = new RequestData(Endpoints.ACCOUNT);
        requestData.addCookie(authCookie);
        requestData.setContentType(MediaType.APPLICATION_JSON);

        final ResponseData response = super.httpUtils.getRequest(requestData);

        final AccountResponse accountResponse = (AccountResponse) response.mapContent(AccountResponse.class);
        assertTrue(response.isOk());
        assertEquals(accountResponse.getEmail(), super.adminUserProperties.getEmail());
        assertEquals(accountResponse.getName(), super.adminUserProperties.getPassword());
        assertEquals(accountResponse.getGender(), Gender.FEMALE);
    }

    @Test
    public void shouldUpdateAccount() throws Exception {
        final String newName = "Louis";
        final Gender newGender = Gender.MALE;
        final UserPatchRequest userPatchRequest = this.buildUserPatchRequest(newName, newGender);
        final ResponseData responseLogin = super.httpUtils.loginWithCredentials("test2@test.com", "1234");
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        RequestData requestData = new RequestData(Endpoints.ACCOUNT);
        requestData.setContentType(MediaType.APPLICATION_JSON);
        requestData.addCookie(authCookie);
        requestData.setContent(userPatchRequest);

        final ResponseData response = super.httpUtils.patchRequest(requestData);

        final AccountResponse accountResponse = (AccountResponse) response.mapContent(AccountResponse.class);
        assertTrue(response.isOk());
        assertEquals(accountResponse.getName(), newName);
        assertEquals(accountResponse.getGender(), newGender);
    }

    @Test
    public void shouldChangePassword() throws Exception {
        final String newPassword = "Madrid2018";
        final ChangePasswordRequest changePasswordRequest = this.buildChangePasswordRequest("1234", newPassword);
        final ResponseData responseLogin = super.httpUtils.loginWithCredentials("test2@test.com", "1234");
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());

        final ResponseData response = this.requestChangePassword(changePasswordRequest, authCookie);

        final User user = this.userDao.findOneByEmail("test2@test.com");
        assertTrue(response.isOk());
        assertTrue(BCrypt.checkpw(newPassword, user.getPassword()));
    }

    @Test
    public void shouldThrowAnErrorIfOldPasswordIsIncorrect() throws Exception {
        final ChangePasswordRequest changePasswordRequest = this.buildChangePasswordRequest("4321", "1111");
        final ResponseData responseLogin = super.httpUtils.loginWithCredentials("test3@test.com", "1234");
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());

        final ResponseData response = this.requestChangePassword(changePasswordRequest, authCookie);

        assertTrue(response.isBadRequest());
    }

    @Test
    public void shouldThrowAnErrorIfPasswordsAreEquals() throws Exception {
        final ChangePasswordRequest changePasswordRequest = this.buildChangePasswordRequest("1234", "1234");
        final ResponseData responseLogin = super.httpUtils.loginWithCredentials("test3@test.com", "1234");
        final Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());

        final ResponseData response = this.requestChangePassword(changePasswordRequest, authCookie);

        assertTrue(response.isBadRequest());
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setJwtConfig(JWTProperties jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    private UserPatchRequest buildUserPatchRequest(final String newName, final Gender newGender) {
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setName(newName);
        userPatchRequest.setGender(newGender);
        return userPatchRequest;
    }

    private ChangePasswordRequest buildChangePasswordRequest(final String oldPassword, final String newPassword) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(oldPassword);
        changePasswordRequest.setPassword(newPassword);
        return changePasswordRequest;
    }

    private ResponseData requestChangePassword(final ChangePasswordRequest changePasswordRequest, final Cookie authCookie) throws Exception {
        RequestData requestData = new RequestData(Endpoints.ACCOUNT + Endpoints.PASSWORD);
        requestData.setContentType(MediaType.APPLICATION_JSON);
        requestData.addCookie(authCookie);
        requestData.setContent(changePasswordRequest);
        return super.httpUtils.putRequest(requestData);
    }

}
