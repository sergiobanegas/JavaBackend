package springskeleton.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import springskeleton.model.User;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    User findOneByEmail(String email);

    User findOneByEmailAndEnabledTrue(String email);

    void deleteByEnabledFalseAndCreatedAtLessThan(Date expirationDate);

    @Query("SELECT user FROM User user WHERE user.id <> :adminId AND user.enabled = 1 " +
            "AND (:email IS NULL OR user.email LIKE CONCAT('%', :email, '%'))")
    Page<User> findUsersToAdmin(Pageable pageable, @Param("adminId") long adminId, @Param("email") String email);

}
