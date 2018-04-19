package springskeleton.controller;

import springskeleton.dao.UserDao;
import springskeleton.model.Language;
import springskeleton.model.User;
import springskeleton.util.ControllerTest;
import springskeleton.util.RequestData;
import springskeleton.util.ResponseData;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.Cookie;

import springskeleton.config.Endpoints;
import springskeleton.config.property.JWTProperties;
import springskeleton.controller.wrapper.request.UserSignUpRequest;
import springskeleton.model.Gender;

public class AuthControllerIT extends ControllerTest {

    private JWTProperties jwtConfig;

    private UserDao userDao;

    @Before
    public void init() {
        super.setUpHttpUtils();
    }

    @Test
    public void shouldAuthAndReturnAuthCookie() throws Exception {
        final ResponseData response = super.httpUtils.loginWithAdmin();
        final Cookie authCookie = response.getCookie(this.jwtConfig.getCookieName());
        final Cookie userInfoCookie = response.getCookie(this.jwtConfig.getUserInfoCookieName());

        JSONObject userInfo = super.httpUtils.getJSONObjectFromBase64(userInfoCookie.getValue());

        assertTrue(response.isOk());
        assertNotNull(authCookie);
        assertTrue(userInfo.getString(this.jwtConfig.getRolesClaim()).contains("ROLE_ADMIN"));
    }

    @Test
    public void shouldSignUp() throws Exception {
        final UserSignUpRequest userSignUpRequest = new UserSignUpRequest("testsave@gmail.com", "password", "Name",
                Gender.FEMALE);
        RequestData requestData = new RequestData(Endpoints.AUTH + Endpoints.SIGN_UP);
        requestData.setContentType(MediaType.APPLICATION_JSON);
        requestData.setContent(userSignUpRequest);

        ResponseData response = super.httpUtils.postRequest(requestData);

        assertTrue(response.isOk());
    }

    @Test
    public void shouldAuthorizeEndpointWhenValidRoleIsGiven() throws Exception {
        final Cookie authCookie = super.httpUtils.loginWithAdmin().getCookie(this.jwtConfig.getCookieName());
        RequestData requestData = new RequestData(Endpoints.USERS);
        requestData.addCookie(authCookie);

        ResponseData response = super.httpUtils.getRequest(requestData);

        assertTrue(response.isOk());
    }

    @Test
    public void shouldNotAuthorizeEndpointWhenNoAuthCookieIsGiven() throws Exception {
        ResponseData response = super.httpUtils.getRequest(new RequestData(Endpoints.AUTH + Endpoints.USERS));

        assertTrue(response.isUnauthorized());
    }

    @Test
    public void shouldNotAuthorizeEndpointWhenInvalidRoleIsGiven() throws Exception {
        ResponseData responseLogin = super.httpUtils.loginWithCredentials("test1@test.com", "1234");
        RequestData requestData = new RequestData(Endpoints.USERS);
        requestData.addCookie(responseLogin.getCookie(this.jwtConfig.getCookieName()));

        ResponseData response = super.httpUtils.getRequest(requestData);

        assertTrue(response.isForbidden());
    }

    @Test
    public void shouldReturnUserCookieIfSetWhenAuth() throws Exception {
        final String userEmail = super.adminUserProperties.getEmail();
        User user = this.userDao.findOneByEmail(userEmail);
        user.setLanguage(Language.ES);
        this.userDao.save(user);

        ResponseData response = super.httpUtils.loginWithCredentials(userEmail, "admin");

        final Cookie authCookie = response.getCookie(this.jwtConfig.getCookieName());
        final Cookie userInfoCookie = response.getCookie(this.jwtConfig.getUserInfoCookieName());
        JSONObject authCookieJSON = super.httpUtils.getJSONObjectFromBase64(authCookie.getValue().split("\\.")[1]);
        JSONObject userInfoCookieJSON = super.httpUtils.getJSONObjectFromBase64(userInfoCookie.getValue());
        assertEquals(authCookieJSON.getString(this.jwtConfig.getLanguageClaim()), "ES");
        assertTrue(userInfoCookieJSON.getString(this.jwtConfig.getLanguageClaim()).contains("ES"));
    }

    @Test
    public void shouldLogout() throws Exception {
        ResponseData responseLogin = super.httpUtils.loginWithCredentials("test1@test.com", "1234");
        Cookie authCookie = responseLogin.getCookie(this.jwtConfig.getCookieName());
        RequestData requestData = new RequestData(Endpoints.AUTH + Endpoints.LOGOUT);
        requestData.addCookie(authCookie);

        final ResponseData response = super.httpUtils.getRequest(requestData);

        assertTrue(response.isOk());
        assertEquals(response.getCookie(this.jwtConfig.getCookieName()).getMaxAge(), 0);
    }

    @Autowired
    public void setJwtConfig(JWTProperties jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

}
