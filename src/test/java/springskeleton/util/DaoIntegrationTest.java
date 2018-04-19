package springskeleton.util;

import springskeleton.config.property.AdminUserProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import springskeleton.service.DatabaseSeederService;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import({DatabaseSeederService.class, AdminUserProperties.class})
public class DaoIntegrationTest {

    private DatabaseSeederService databaseSeederService;

    @Before
    public void seedDatabase() {
        this.databaseSeederService.seedDatabase();
    }

    @After
    public void cleanDatabase() {
        this.databaseSeederService.deleteAllExceptAdmin();
    }

    @Autowired
    public void setDatabaseSeederService(DatabaseSeederService databaseSeederService) {
        this.databaseSeederService = databaseSeederService;
    }

}
