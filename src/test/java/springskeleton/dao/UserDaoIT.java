package springskeleton.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import springskeleton.config.property.AdminUserProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import springskeleton.model.Gender;
import springskeleton.model.Role;
import springskeleton.model.User;
import springskeleton.util.DaoIntegrationTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class UserDaoIT extends DaoIntegrationTest {

    private UserDao userDao;

    private AuthorizationDao authorizationDao;

    private AdminUserProperties adminUserProperties;

    @Test
    public void shouldReturnIfAnUserExists() {
        assertNotEquals(this.userDao.findOneByEmail(this.adminUserProperties.getEmail()), null);
    }

    @Test
    public void shouldSaveAnUser() {
        User user = new User("test20@test.com", "1234", "Test", Gender.MALE);

        this.userDao.save(user);

        assertNotEquals(this.userDao.findOneByEmail("test20@test.com"), null);
    }

    @Test
    public void shouldStoreTheRoles() {
        final User user = this.userDao.findOneByEmail(this.adminUserProperties.getEmail());

        final List<Role> roleList = this.authorizationDao.findRoleByUser(user);

        assertTrue(roleList.size() > 0);
    }

    @Test
    public void shouldReturnPaginatedUsers() {
        final Pageable pageable = new PageRequest(0, 2);
        long adminId = this.userDao.findOneByEmail(this.adminUserProperties.getEmail()).getId();

        final Page<User> userPage = this.userDao.findUsersToAdmin(pageable, adminId, null);

        final List<User> users = userPage.getContent();
        assertEquals(users.size(), 2);
        assertEquals(users.get(0).getEmail(), "test1@test.com");
        assertEquals(users.get(1).getEmail(), "test2@test.com");
        assertEquals(userPage.getTotalElements(), 3);
        assertEquals(userPage.getNumberOfElements(), 2);
        assertTrue(userPage.isFirst());
        assertFalse(userPage.isLast());
        assertEquals(userPage.getNumber(), 0);
    }

    @Test
    public void shouldReturnPaginatedUserByEmail() {
        final Pageable pageable = new PageRequest(0, 2);
        long adminId = this.userDao.findOneByEmail(this.adminUserProperties.getEmail()).getId();

        final Page<User> userPage = this.userDao.findUsersToAdmin(pageable, adminId, "test2");

        final List<User> users = userPage.getContent();
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getEmail(), "test2@test.com");
        assertEquals(userPage.getTotalElements(), 1);
        assertEquals(userPage.getNumberOfElements(), 1);
        assertTrue(userPage.isFirst());
        assertTrue(userPage.isLast());
        assertEquals(userPage.getNumber(), 0);
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setAuthorizationDao(AuthorizationDao authorizationDao) {
        this.authorizationDao = authorizationDao;
    }

    @Autowired
    public void setAdminUserProperties(AdminUserProperties adminUserProperties) {
        this.adminUserProperties = adminUserProperties;
    }

}
