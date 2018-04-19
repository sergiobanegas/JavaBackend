package springskeleton;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import springskeleton.controller.AllControllersIntegrationTests;
import springskeleton.dao.AllDaosIntegrationTests;
import springskeleton.service.AllServicesIntegrationTests;

@RunWith(Suite.class)
@SuiteClasses({AllDaosIntegrationTests.class, AllServicesIntegrationTests.class, AllControllersIntegrationTests.class})
public class AllIntegrationTests {

}
