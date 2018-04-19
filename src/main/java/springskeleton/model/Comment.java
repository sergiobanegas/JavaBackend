package springskeleton.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.validator.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "comments")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Comment implements Serializable {

    private static final long serialVersionUID = -5655701350656674238L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User author;

    @NonNull
    @NotBlank
    private String content;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Comment> replies = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt = new Date();

    public void addReply(Comment comment) {
        this.replies.add(comment);
    }

    public boolean containsReply(final Comment comment) {
        return this.replies.stream().anyMatch(c -> c.getId() == comment.getId());
    }

    public void removeReply(final Comment comment) {
        this.replies.remove(comment);
    }

    public boolean hasAuthor(Long id) {
        return this.author.hasId(id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return (comment.getId().equals(this.id)
                && comment.getAuthor().equals(this.author)
                && comment.getContent().equals(this.content));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, content, replies, createdAt, updatedAt);
    }
}
