package springskeleton.service;

import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.exception.NotFoundException;
import springskeleton.dao.UserDao;
import springskeleton.model.User;
import springskeleton.util.I18n;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserDao userDao;

    private I18n i18n;

    @Autowired
    public UserService(UserDao userDao, I18n i18n) {
        this.userDao = userDao;
        this.i18n = i18n;
    }

    public Page<User> getUsersPaginated(final Pageable pageable, final long adminId, final String email) {
        return this.userDao.findUsersToAdmin(pageable, adminId, email);
    }

    public User getUser(final long adminId, final Long userId) throws NotFoundException, InvalidDataException {
        if (adminId == userId) {
            throw new InvalidDataException(this.i18n.get("error.administer.admin"));
        }
        return this.findUser(userId);
    }

    public void deleteUser(final long adminId, final long id) throws InvalidDataException {
        if (adminId == id) {
            throw new InvalidDataException(this.i18n.get("error.delete.admin.user"));
        }
        User user = this.findUser(id);
        this.userDao.delete(user);
    }

    private User findUser(final long id) throws NotFoundException {
        final User user = this.userDao.findOne(id);
        if (user == null) {
            throw new NotFoundException(this.i18n.get("user.not.exists", new Object[]{id}));
        }
        return user;
    }

}
