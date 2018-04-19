package springskeleton.dao;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({UserDaoIT.class, CommentDaoIT.class})
public class AllDaosIntegrationTests {

}
