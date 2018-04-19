package springskeleton.config;

public class ResourceNames {

    private static final String MAIN_PACKAGE = "springskeleton";

    public static final String CONTROLLERS = MAIN_PACKAGE + ".controller";

    public static final String DAOS = MAIN_PACKAGE + ".dao";

    public static final String MODELS = MAIN_PACKAGE + ".model";

    private static final String CLASSPATH = "classpath:";

    public static final String DATA_FOR_TESTING_FILE = CLASSPATH + "data-for-testing.yml";
    
    public static final String RESOURCES = "/resources";

    public static final String PUBLIC = "src/main/resources/public" + RESOURCES;

    public static final String USER = "/user";

    public static final String AVATAR_FILE = "avatar.png";

}
