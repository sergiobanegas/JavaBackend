package springskeleton.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import springskeleton.config.property.FrontEndProperties;
import springskeleton.controller.exception.ServerErrorException;
import springskeleton.controller.wrapper.request.EmailRequest;
import springskeleton.controller.wrapper.request.ResetPasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import springskeleton.controller.exception.InvalidDataException;
import springskeleton.controller.wrapper.request.UserSignUpRequest;
import springskeleton.dao.AuthorizationDao;
import springskeleton.dao.UserConfirmationTokenDao;
import springskeleton.dao.UserDao;
import springskeleton.model.Authorization;
import springskeleton.model.Role;
import springskeleton.model.User;
import springskeleton.model.UserConfirmationToken;
import springskeleton.util.I18n;
import springskeleton.util.Mail;

@Service
public class AuthService {

    private UserDao userDao;

    private AuthorizationDao authorizationDao;

    private UserConfirmationTokenDao userConfirmationTokenDao;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private EmailService emailService;

    private FrontEndProperties frontEndProperties;

    private I18n i18n;

    @Autowired
    public AuthService(UserDao userDao, AuthorizationDao authorizationDao,
                       UserConfirmationTokenDao userConfirmationTokenDao,
                       BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService,
                       FrontEndProperties frontEndProperties, I18n i18n) {
        this.userDao = userDao;
        this.authorizationDao = authorizationDao;
        this.userConfirmationTokenDao = userConfirmationTokenDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
        this.frontEndProperties = frontEndProperties;
        this.i18n = i18n;
    }

    public void signUp(final UserSignUpRequest userSignUpRequest) throws InvalidDataException {
        final String encodedPassword = this.bCryptPasswordEncoder.encode(userSignUpRequest.getPassword());
        this.deleteExistingUser(userSignUpRequest);
        User user = new User(userSignUpRequest.getEmail(), encodedPassword, userSignUpRequest.getName(), userSignUpRequest.getGender());
        this.userDao.save(user);
        final String token = this.generateRandomToken();
        UserConfirmationToken userConfirmationToken = new UserConfirmationToken(token, user);
        this.userConfirmationTokenDao.save(userConfirmationToken);
        this.sendConfirmationEmailToUser(user, token);
    }

    public void confirm(final String token) throws InvalidDataException, ServerErrorException {
        this.checkIfTokenIsNotEmpty(token);
        UserConfirmationToken confirmationToken = this.userConfirmationTokenDao.findOneByToken(token);
        this.checkIfTokenExists(confirmationToken);
        User user = confirmationToken.getUser();
        CompletableFuture<Void> userSaving = this.saveUser(user);
        CompletableFuture<Void> userConfirmationTokenRemoval = this.deleteToken(confirmationToken);
        Future<Void> future = CompletableFuture.allOf(userSaving, userConfirmationTokenRemoval);
        this.sendUserWelcomeMail(user);
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public void sendConfirmationEmail(final String email) throws InvalidDataException, ServerErrorException {
        final User user = this.userDao.findOneByEmail(email);
        if (user.isEnabled()) {
            throw new InvalidDataException(this.i18n.get("account.already.validated"));
        }
        final UserConfirmationToken userConfirmationToken = this.userConfirmationTokenDao.findOneByUser(user);
        this.checkIfUserIsAlreadyRegistered(userConfirmationToken);
        this.sendConfirmationEmailToUser(user, userConfirmationToken.getToken());
    }

    public boolean existsUser(final String email) {
        return this.userDao.findOneByEmailAndEnabledTrue(email) != null;
    }

    public void resetPasswordRequest(final EmailRequest emailRequest) throws ServerErrorException {
        final User user = this.userDao.findOneByEmail(emailRequest.getEmail());
        final String token = this.generateRandomToken();
        UserConfirmationToken userConfirmationToken = new UserConfirmationToken(token, user);
        this.userConfirmationTokenDao.save(userConfirmationToken);
        this.sendResetPasswordRequest(user, token);
    }

    public void resetPassword(final String token, final ResetPasswordRequest resetPasswordRequest) {
        final UserConfirmationToken userConfirmationToken = this.userConfirmationTokenDao.findOneByToken(token);
        this.checkIfTokenExists(userConfirmationToken);
        this.updateUserPassword(resetPasswordRequest, userConfirmationToken);
        this.userConfirmationTokenDao.delete(userConfirmationToken);
    }

    private void deleteExistingUser(final UserSignUpRequest userSignUpRequest) {
        User user = this.userDao.findOneByEmail(userSignUpRequest.getEmail());
        if (user != null) {
            this.userDao.delete(user);
        }
    }

    private String generateRandomToken() {
        return UUID.randomUUID().toString();
    }

    private void sendUserWelcomeMail(final User user) throws ServerErrorException {
        Mail mail = new Mail();
        mail.setTo(user.getEmail());
        mail.setSubject(this.i18n.get("welcome.user", new Object[]{user.getName()}));
        mail.setContent(this.i18n.get("welcome.user", new Object[]{user.getName()}));
        mail.setTitle(this.i18n.get("welcome.user", new Object[]{user.getName()}));
        this.emailService.send(mail);
    }

    private CompletableFuture<Void> deleteToken(UserConfirmationToken confirmationToken) {
        return CompletableFuture.runAsync(() ->
                this.userConfirmationTokenDao.delete(confirmationToken)
        );
    }

    private CompletableFuture<Void> saveUser(User user) {
        return CompletableFuture.runAsync(() -> {
            user.setEnabled(true);
            this.userDao.save(user);
            this.authorizationDao.save(new Authorization(user, Role.USER));
        });
    }

    private void checkIfTokenIsNotEmpty(final String token) throws InvalidDataException {
        if (token == null) {
            throw new InvalidDataException(this.i18n.get("wrong.confirmation.code"));
        }
    }

    private void checkIfUserIsAlreadyRegistered(final UserConfirmationToken userConfirmationToken) throws InvalidDataException {
        if (userConfirmationToken == null) {
            throw new InvalidDataException(this.i18n.get("user.already.registered"));
        }
    }

    private void sendConfirmationEmailToUser(final User user, final String token) throws ServerErrorException {
        final String confirmationUrl = this.frontEndProperties.getUrl() + "/confirm-account/" + token;
        Mail mail = new Mail();
        mail.setTo(user.getEmail());
        mail.setSubject(this.i18n.get("register.confirmation"));
        mail.setTitle(this.i18n.get("register.confirmation"));
        mail.setContent(this.i18n.get("register.confirmation.mail.content", new Object[]{user.getName(), confirmationUrl}));
        this.emailService.send(mail);
    }

    private void sendResetPasswordRequest(final User user, final String token) throws ServerErrorException {
        final String resetPasswordUrl = this.frontEndProperties.getUrl() + "/reset-password/" + token;
        Mail mail = new Mail();
        mail.setTo(user.getEmail());
        mail.setSubject(this.i18n.get("reset.password"));
        mail.setTitle(this.i18n.get("reset.password"));
        mail.setContent(this.i18n.get("reset.password.content", new Object[]{user.getName(), resetPasswordUrl}));
        this.emailService.send(mail);
    }

    private void updateUserPassword(final ResetPasswordRequest resetPasswordRequest, final UserConfirmationToken userConfirmationToken) {
        User user = userConfirmationToken.getUser();
        user.setPassword(this.bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
        this.userDao.save(user);
    }

    private void checkIfTokenExists(final UserConfirmationToken userConfirmationToken) throws InvalidDataException {
        if (userConfirmationToken == null) {
            throw new InvalidDataException(this.i18n.get("wrong.confirmation.code"));
        }
    }

}
