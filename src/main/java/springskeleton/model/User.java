package springskeleton.model;

import springskeleton.config.Endpoints;
import org.hibernate.validator.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import springskeleton.config.ResourceNames;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"}, allowGetters = true)
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = -5655701350656674238L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @NotBlank
    @Column(unique = true)
    private String email;

    @NonNull
    @NotBlank
    private String password;

    @NonNull
    @NotBlank
    private String name;

    @NonNull
    @NotBlank
    private String avatar = Endpoints.API + ResourceNames.RESOURCES + ResourceNames.USER + "/" + ResourceNames.AVATAR_FILE;

    @NonNull
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdAt = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt = new Date();

    private boolean enabled = false;

    private Language language;

    public boolean hasId(final Long id) {
        return this.id.equals(id);
    }

    @Override
    public boolean equals(final Object u) {
        if (this == u) return true;
        if (u == null || getClass() != u.getClass()) return false;
        User user = (User) u;
        return (user.getId().equals(this.id)
                && user.getEmail().equals(this.email)
                && user.getName().equals(this.name)
                && user.getGender().equals(this.gender)
                && user.isEnabled() == this.enabled
                && (this.language == null || user.getLanguage().equals(this.language))
                && user.getAvatar().equals(this.avatar));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, name, avatar, gender, createdAt, updatedAt, enabled, language);
    }
}
