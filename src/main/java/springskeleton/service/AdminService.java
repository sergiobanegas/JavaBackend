package springskeleton.service;

import java.util.Date;

import springskeleton.dao.UserEmailChangeConfirmationTokenDao;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import springskeleton.dao.UserDao;

@Service
public class AdminService {

    private UserDao userDao;

    private UserEmailChangeConfirmationTokenDao userEmailChangeConfirmationTokenDao;

    @Autowired
    public AdminService(UserDao userDao, UserEmailChangeConfirmationTokenDao userEmailChangeConfirmationTokenDao) {
        this.userDao = userDao;
        this.userEmailChangeConfirmationTokenDao = userEmailChangeConfirmationTokenDao;
    }

    public void deleteUnconfirmedUsers() {
        this.userDao.deleteByEnabledFalseAndCreatedAtLessThan(this.getYesterday());
    }

    public void deleteUnconfirmedTokens() {
        this.userEmailChangeConfirmationTokenDao.deleteByExpirationDateLessThan(new Date());
    }

    private Date getYesterday() {
        return new DateTime().minusDays(1).toDate();
    }

}
