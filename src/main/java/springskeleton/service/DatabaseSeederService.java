package springskeleton.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import springskeleton.config.property.AdminUserProperties;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import static springskeleton.config.ResourceNames.DATA_FOR_TESTING_FILE;

import springskeleton.dao.AuthorizationDao;
import springskeleton.dao.CommentDao;
import springskeleton.dao.UserConfirmationTokenDao;
import springskeleton.dao.UserDao;
import springskeleton.model.Authorization;
import springskeleton.model.Comment;
import springskeleton.model.Role;
import springskeleton.model.User;
import springskeleton.util.TestPopulationData;

@Service
@Transactional
public class DatabaseSeederService {

    private ApplicationContext appContext;

    private UserDao userDao;

    private CommentDao commentDao;

    private AuthorizationDao authorizationDao;

    private UserConfirmationTokenDao userConfirmationTokenDao;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private AdminUserProperties adminUserProperties;

    @Autowired
    public DatabaseSeederService(ApplicationContext appContext, UserDao userDao, CommentDao commentDao,
                                 AuthorizationDao authorizationDao,
                                 UserConfirmationTokenDao userConfirmationTokenDao,
                                 BCryptPasswordEncoder bCryptPasswordEncoder,
                                 AdminUserProperties adminUserProperties) {
        this.appContext = appContext;
        this.userDao = userDao;
        this.commentDao = commentDao;
        this.authorizationDao = authorizationDao;
        this.userConfirmationTokenDao = userConfirmationTokenDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.adminUserProperties = adminUserProperties;
    }

    @PostConstruct
    public void createDefaultAdmin() {
        User adminSaved = this.userDao.findOneByEmail(adminUserProperties.getEmail());
        if (adminSaved == null) {
            User user = new User();
            user.setEmail(this.adminUserProperties.getEmail());
            user.setPassword(this.adminUserProperties.getPassword());
            user.setName(this.adminUserProperties.getName());
            user.setGender(this.adminUserProperties.getGender());
            this.saveAdmin(user);
        }
    }

    public void seedDatabase() {
        this.seedDatabase(DATA_FOR_TESTING_FILE);
    }

    public void seedDatabase(final String ymlFileName) {
        assert ymlFileName != null && !ymlFileName.isEmpty();
        final Constructor constructor = new Constructor(TestPopulationData.class);
        final Yaml yamlParser = new Yaml(constructor);
        final Resource resource = this.appContext.getResource(ymlFileName);
        InputStream input;
        try {
            input = resource.getInputStream();
            final TestPopulationData populationData = yamlParser.load(input);
            this.saveUsers(populationData);
            this.saveComments(populationData);
        } catch (IOException e) {
            LogManager.getLogger(this.getClass().getSimpleName())
                    .error("File " + ymlFileName + " doesn't exist or it can't be opened");
        }
    }

    public boolean existsYamlFile(final String fileName) {
        return this.appContext.getResource(fileName).exists();
    }

    public void deleteAllExceptAdmin() {
        this.authorizationDao.deleteAll();
        this.deleteComments();
        this.commentDao.deleteAll();
        this.userConfirmationTokenDao.deleteAll();
        this.userDao.deleteAll();
        this.createDefaultAdmin();
    }

    private void saveAdmin(User adminUserData) {
        final String password = this.bCryptPasswordEncoder.encode(adminUserData.getPassword());
        User admin = new User(adminUserData.getEmail(), password, adminUserData.getName(), adminUserData.getGender());
        admin.setEnabled(true);
        this.userDao.save(admin);
        this.authorizationDao.save(new Authorization(admin, Role.ADMIN));
    }

    private void saveComments(final TestPopulationData populationData) {
        List<Comment> comments = populationData.getCommentList();
        this.commentDao.save(comments);
    }

    private void saveUsers(final TestPopulationData populationData) {
        List<User> users = populationData.getUserList();
        for (User user : users) {
            user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
            this.userDao.save(user);
            this.authorizationDao.save(new Authorization(user, Role.USER));
        }
    }

    private void deleteComments() {
        List<Comment> comments = this.commentDao.findAll();
        for (Comment comment : comments) {
            comment.getReplies().clear();
        }
    }

}
