package springskeleton.dao;

import org.springframework.stereotype.Repository;

import springskeleton.model.Comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface CommentDao extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE :comment MEMBER OF c.replies")
    Comment findOneThatHasReply(@Param("comment") Comment comment);
}
