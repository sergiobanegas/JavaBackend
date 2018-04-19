package springskeleton.dao;

import springskeleton.model.UserEmailChangeConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserEmailChangeConfirmationTokenDao extends JpaRepository<UserEmailChangeConfirmationToken, Long> {

    UserEmailChangeConfirmationToken findOneByToken(String token);

    void deleteByExpirationDateLessThan(Date expirationDate);

}
