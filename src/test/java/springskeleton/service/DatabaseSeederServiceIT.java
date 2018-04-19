package springskeleton.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import springskeleton.config.property.AdminUserProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static springskeleton.config.ResourceNames.DATA_FOR_TESTING_FILE;

import springskeleton.dao.AuthorizationDao;
import springskeleton.dao.UserDao;
import springskeleton.model.User;
import springskeleton.util.IntegrationTest;

public class DatabaseSeederServiceIT extends IntegrationTest {

    private UserDao userDao;

    private AuthorizationDao authorizationDao;

    private AdminUserProperties adminUserProperties;

    @Test
    public void testCreateDefaultAdmin() {
        super.databaseSeederService.deleteAllExceptAdmin();
        final User admin = this.userDao.findOneByEmail(this.adminUserProperties.getEmail());

        assertEquals(1, this.userDao.count());
        assertNotNull(admin);
        assertEquals(1, this.authorizationDao.count());

        super.databaseSeederService.seedDatabase();
    }

    @Test
    public void testTpvTestDatabaseShouldBeParsed() {
        super.databaseSeederService.deleteAllExceptAdmin();

        final long previousUserCount = this.userDao.count();
        super.databaseSeederService.seedDatabase(DATA_FOR_TESTING_FILE);

        final User user = this.userDao.findOneByEmail(this.adminUserProperties.getEmail());

        assertNotNull(user);
        assertEquals(4, this.userDao.count() - previousUserCount);

        super.databaseSeederService.deleteAllExceptAdmin();
        super.databaseSeederService.seedDatabase();
    }

    @Test
    public void testExistentFile() {
        assertTrue(super.databaseSeederService.existsYamlFile(DATA_FOR_TESTING_FILE));
    }

    @Test
    public void testNonexistentFile() {
        assertFalse(super.databaseSeederService.existsYamlFile("nonexistent.yml"));
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
