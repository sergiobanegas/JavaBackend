package springskeleton.util;

import springskeleton.config.property.AdminUserProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import springskeleton.service.DatabaseSeederService;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({DatabaseSeederService.class, AdminUserProperties.class})
public class IntegrationTest {

    protected DatabaseSeederService databaseSeederService;

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
