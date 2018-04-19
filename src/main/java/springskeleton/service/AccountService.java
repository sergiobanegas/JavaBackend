package springskeleton.service;

import springskeleton.config.Endpoints;
import springskeleton.config.property.FrontEndProperties;
import springskeleton.controller.wrapper.request.ChangePasswordRequest;
import springskeleton.dao.UserConfirmationTokenDao;
import springskeleton.dao.UserEmailChangeConfirmationTokenDao;
import springskeleton.model.Language;
import springskeleton.model.UserConfirmationToken;
import springskeleton.model.UserEmailChangeConfirmationToken;
import springskeleton.util.AuthUtils;
import springskeleton.util.FileUtils;
import springskeleton.util.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import springskeleton.config.ResourceNames;
import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.wrapper.request.UserPatchRequest;
import springskeleton.dao.UserDao;
import springskeleton.model.User;
import springskeleton.util.I18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class AccountService {

    private UserDao userDao;

    private I18n i18n;

    private FileUtils fileUtils;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private FrontEndProperties frontEndProperties;

    private UserConfirmationTokenDao userConfirmationTokenDao;

    private UserEmailChangeConfirmationTokenDao userEmailChangeConfirmationTokenDao;

    private EmailService emailService;

    private AuthUtils authUtils;

    @Autowired
    public AccountService(UserDao userDao, I18n i18n, FileUtils fileUtils,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          FrontEndProperties frontEndProperties,
                          UserConfirmationTokenDao userConfirmationTokenDao,
                          UserEmailChangeConfirmationTokenDao userEmailChangeConfirmationTokenDao,
                          EmailService emailService, AuthUtils authUtils) {
        this.userDao = userDao;
        this.i18n = i18n;
        this.fileUtils = fileUtils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.frontEndProperties = frontEndProperties;
        this.userConfirmationTokenDao = userConfirmationTokenDao;
        this.userEmailChangeConfirmationTokenDao = userEmailChangeConfirmationTokenDao;
        this.emailService = emailService;
        this.authUtils = authUtils;
    }

    public User getAccount(final long id) throws ServerErrorException {
        final User user = this.userDao.findOne(id);
        if (user == null) {
            throw new ServerErrorException("Error obtaining the logged user");
        }
        return user;
    }

    public User updateAvatar(final MultipartFile file, final Long id) throws InvalidDataException, ServerErrorException {
        User user = this.userDao.findOne(id);
        if (!this.fileUtils.isFileAnImage(file)) {
            throw new InvalidDataException(this.i18n.get("invalid.image.type"));
        }
        this.fileUtils.uploadFile(file, ResourceNames.USER + "/" + id, ResourceNames.AVATAR_FILE);
        user.setAvatar(Endpoints.API + Endpoints.FILES + Endpoints.USER + "/" + user.getId() + Endpoints.AVATAR);
        this.userDao.save(user);
        return user;
    }

    public void deleteRequest(final Long id) throws ServerErrorException {
        final String token = UUID.randomUUID().toString();
        User user = this.userDao.findOne(id);
        UserConfirmationToken userConfirmationToken = new UserConfirmationToken(token, user);
        this.userConfirmationTokenDao.save(userConfirmationToken);
        this.sendDeleteAccountMail(user, token);
    }

    public void delete(final String token, final HttpServletRequest request, final HttpServletResponse response) throws InvalidDataException {
        UserConfirmationToken userConfirmationToken = this.userConfirmationTokenDao.findOneByToken(token);
        this.checkIfIsValidToken(userConfirmationToken);
        User user = userConfirmationToken.getUser();
        this.deleteUser(userConfirmationToken, user);
        this.removeAuthCookies(request, response);
    }

    public User update(final Long id, final UserPatchRequest newData, final HttpServletRequest request, HttpServletResponse response) throws ServerErrorException, InvalidDataException {
        User user = this.userDao.findOne(id);
        boolean modified = false;
        if (!newData.isEmpty()) {
            if (newData.getName() != null) {
                user.setName(newData.getName());
                modified = true;
            }
            if (newData.getGender() != null) {
                user.setGender(newData.getGender());
                modified = true;
            }
            if (newData.getEmail() != null) {
                final String token = UUID.randomUUID().toString();
                this.userEmailChangeConfirmationTokenDao.save(new UserEmailChangeConfirmationToken(token, newData.getEmail(), user));
                this.sendChangeEmailMail(user, newData.getEmail(), token);
                modified = true;
            }
            final Language language = newData.getLanguage();
            if (language != null) {
                user.setLanguage(language);
                this.authUtils.updateLanguageInCookies(request, response, user);
                modified = true;
            }
        }
        return modified ? this.userDao.save(user) : user;
    }

    public void changePassword(Long userId, ChangePasswordRequest changePasswordRequest) throws InvalidDataException {
        User user = this.userDao.findOne(userId);
        this.checkIfPasswordsAreEquals(changePasswordRequest);
        if (BCrypt.checkpw(changePasswordRequest.getOldPassword(), user.getPassword())) {
            user.setPassword(this.bCryptPasswordEncoder.encode(changePasswordRequest.getPassword()));
            this.userDao.save(user);
        } else {
            throw new InvalidDataException(this.i18n.get("incorrect.old.password"));
        }
    }

    public String changeEmail(final Long userId, final String token) {
        UserEmailChangeConfirmationToken confirmationToken = this.userEmailChangeConfirmationTokenDao.findOneByToken(token);
        if (confirmationToken == null) {
            throw new InvalidDataException(this.i18n.get("wrong.confirmation.code"));
        }
        User user = confirmationToken.getUser();
        if (!user.getId().equals(userId)) {
            throw new InvalidDataException(this.i18n.get("wrong.confirmation.code"));
        }
        user.setEmail(confirmationToken.getEmail());
        this.userDao.save(user);
        this.userEmailChangeConfirmationTokenDao.delete(confirmationToken);
        return confirmationToken.getEmail();
    }

    private void sendDeleteAccountMail(User user, String token) throws ServerErrorException {
        final String deleteAccountUrl = this.frontEndProperties.getUrl() + "/account/delete/" + token;
        Mail mail = new Mail();
        mail.setTo(user.getEmail());
        mail.setSubject(this.i18n.get("delete.account"));
        mail.setTitle(this.i18n.get("delete.account"));
        mail.setContent(this.i18n.get("delete.account.content", new Object[]{user.getName(), deleteAccountUrl}));
        this.emailService.send(mail);
    }

    private void removeAuthCookies(HttpServletRequest request, HttpServletResponse response) {
        if (this.authUtils.requestHasAuthCookie(request)) {
            this.authUtils.removeAuthCookie(request, response);
            this.authUtils.removeUserInfoCookie(request, response);
        }
    }

    private void deleteUser(UserConfirmationToken userConfirmationToken, User user) {
        this.userConfirmationTokenDao.delete(userConfirmationToken);
        this.userDao.delete(user);
    }

    private void checkIfIsValidToken(final UserConfirmationToken userConfirmationToken) throws InvalidDataException {
        if (userConfirmationToken == null) {
            throw new InvalidDataException(this.i18n.get("wrong.confirmation.code"));
        }
    }

    private void checkIfPasswordsAreEquals(final ChangePasswordRequest changePasswordRequest) throws InvalidDataException {
        if (changePasswordRequest.hasSamePasswords()) {
            throw new InvalidDataException(this.i18n.get("same.passwords.error"));
        }
    }

    private void sendChangeEmailMail(final User user, final String email, final String token) {
        final String changeEmailUrl = this.frontEndProperties.getUrl() + "/account/edit/email/" + token;
        Mail mail = new Mail();
        mail.setTo(user.getEmail());
        mail.setSubject(this.i18n.get("change.email"));
        mail.setTitle(this.i18n.get("change.email"));
        mail.setContent(this.i18n.get("change.email.content", new Object[]{user.getName(), email, changeEmailUrl}));
        this.emailService.send(mail);
    }

}
