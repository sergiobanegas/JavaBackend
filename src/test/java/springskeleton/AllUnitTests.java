package springskeleton;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import springskeleton.controller.AllControllersUnitTests;
import springskeleton.service.AllServicesUnitTests;

@RunWith(Suite.class)
@SuiteClasses({AllServicesUnitTests.class, AllControllersUnitTests.class})
public class AllUnitTests {

}
