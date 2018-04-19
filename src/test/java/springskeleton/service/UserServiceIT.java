package springskeleton.service;

import springskeleton.config.property.AdminUserProperties;
import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.exception.NotFoundException;
import springskeleton.dao.UserDao;
import springskeleton.model.User;
import springskeleton.util.IntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserServiceIT extends IntegrationTest {

    private UserService userService;

    private UserDao userDao;

    private AdminUserProperties adminUserProperties;

    @Test
    public void shouldGetAnUser() {
        final User adminUser = this.userDao.findOneByEmail(adminUserProperties.getEmail());
        final User user = this.userDao.findAll().get(1);

        final User userFound = this.userService.getUser(adminUser.getId(), user.getId());

        assertEquals(userFound, user);
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionWhenUserNotExists() {
        this.userService.getUser(1L, 12341234L);
    }

    @Test(expected = InvalidDataException.class)
    public void shouldThrowExceptionWhenReturningAdmin() {
        this.userService.getUser(1L, 1L);
    }

    @Test
    public void shouldDeleteAnUser() {
        final String userEmail = "test4@test.com";

        this.userService.deleteUser(1L, this.userDao.findOneByEmail(userEmail).getId());

        assertNull(this.userDao.findOneByEmail(userEmail));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowExceptionWhenDeletingAnUserThatNotExists() {
        this.userService.deleteUser(1L, 12341234L);
    }

    @Test(expected = InvalidDataException.class)
    public void shouldThrowExceptionWhenUserDeletesHimself() {
        this.userService.deleteUser(1L, 1L);
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setAdminUserProperties(AdminUserProperties adminUserProperties) {
        this.adminUserProperties = adminUserProperties;
    }

}
