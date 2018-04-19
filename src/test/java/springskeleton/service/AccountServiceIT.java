package springskeleton.service;

import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.wrapper.request.ChangePasswordRequest;
import springskeleton.controller.wrapper.request.UserPatchRequest;
import springskeleton.dao.UserDao;
import springskeleton.model.Gender;
import springskeleton.model.User;
import springskeleton.util.IntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class AccountServiceIT extends IntegrationTest {

    private AccountService accountService;

    private UserDao userDao;

    @Test
    public void shouldReturnAccountInfo() {
        final String userEmail = "test2@test.com";
        final String newName = "Louis";
        final Gender newGender = Gender.MALE;
        UserPatchRequest userPatchRequest = this.buildUserPatchRequest(newName, newGender);

        Long userId = this.userDao.findOneByEmail(userEmail).getId();

        User returnedUser = this.accountService.update(userId, userPatchRequest, null, null);
        User updatedUser = this.userDao.findOneByEmail(userEmail);
        assertEquals(returnedUser.getName(), newName);
        assertEquals(returnedUser.getGender(), newGender);
        assertEquals(updatedUser.getName(), newName);
        assertEquals(updatedUser.getGender(), newGender);
    }

    @Test
    public void shouldChangePassword() {
        final String userEmail = "test2@test.com";
        final String newPassword = "Madrid2018";
        final String oldPassword = "1234";
        ChangePasswordRequest changePasswordRequest = this.buildChangePasswordRequest(oldPassword, newPassword);
        Long userId = this.userDao.findOneByEmail(userEmail).getId();

        this.accountService.changePassword(userId, changePasswordRequest);

        User user = this.userDao.findOneByEmail(userEmail);
        assertTrue(BCrypt.checkpw(newPassword, user.getPassword()));
    }

    @Test(expected = InvalidDataException.class)
    public void shouldThrowAnErrorIfOldPasswordIsIncorrect() {
        final String userEmail = "test2@test.com";
        final String newPassword = "1234";
        final String oldPassword = "4321";
        ChangePasswordRequest changePasswordRequest = this.buildChangePasswordRequest(oldPassword, newPassword);
        Long userId = this.userDao.findOneByEmail(userEmail).getId();

        this.accountService.changePassword(userId, changePasswordRequest);
    }

    @Test(expected = InvalidDataException.class)
    public void shouldThrowAnErrorIfPasswordsAreEquals() {
        final String userEmail = "test2@test.com";
        final String newPassword = "1234";
        ChangePasswordRequest changePasswordRequest = this.buildChangePasswordRequest(newPassword, newPassword);
        Long userId = this.userDao.findOneByEmail(userEmail).getId();

        this.accountService.changePassword(userId, changePasswordRequest);
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    private UserPatchRequest buildUserPatchRequest(final String newName, final Gender newGender) {
        UserPatchRequest userPatchRequest = new UserPatchRequest();
        userPatchRequest.setName(newName);
        userPatchRequest.setGender(newGender);
        return userPatchRequest;
    }

    private ChangePasswordRequest buildChangePasswordRequest(final String oldPassword, final String newPassword) {
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setOldPassword(oldPassword);
        changePasswordRequest.setPassword(newPassword);
        return changePasswordRequest;
    }

}
