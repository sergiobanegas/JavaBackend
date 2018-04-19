package springskeleton.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({AuthServiceIT.class, CommentServiceIT.class, DatabaseSeederServiceIT.class})
public class AllServicesIntegrationTests {

}
