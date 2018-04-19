package springskeleton.dao;

import org.springframework.stereotype.Repository;

import springskeleton.model.Authorization;
import springskeleton.model.Role;
import springskeleton.model.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface AuthorizationDao extends JpaRepository<Authorization, Long> {

    @Query("SELECT authorization.role FROM Authorization authorization WHERE authorization.user = ?1")
    List<Role> findRoleByUser(User user);

}
