package springskeleton.dao;

import org.springframework.stereotype.Repository;

import springskeleton.model.User;
import springskeleton.model.UserConfirmationToken;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserConfirmationTokenDao extends JpaRepository<UserConfirmationToken, Long> {

    UserConfirmationToken findOneByToken(String token);

    UserConfirmationToken findOneByUser(User user);

}
