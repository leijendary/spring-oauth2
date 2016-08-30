package lejendary.oauth2.domain;

import lejendary.oauth2.util.AppConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 3:59 PM
 */

@Entity
@Table(name = "user", indexes = {
        @Index(name = "UK_user_username", columnList = "username"),
        @Index(name = "UK_user_email", columnList = "email")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractAuditingEntity implements UserDetails {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    @Pattern(regexp = AppConstants.LOGIN_REGEX)
    @Size(min = 1, max = 50)
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Size(max = 50)
    @Column(name = "middle_name", length = 50)
    private String middleName;

    @NotBlank
    @Size(max = 50)
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 100)
    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @OneToOne
    @JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_user_status_id"), nullable = false)
    private Status status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id"), foreignKey = @ForeignKey(name = "FK_user_authority_user_id"))
    @Column(length = 50)
    private Set<String> authority;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<String> authorities = getAuthority();
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>(authorities.size());
        grantedAuthorities.addAll(authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        return grantedAuthorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return getStatus() != null && getStatus().getCode() != null && !getStatus().getCode().equals(Status.EXPIRED);
    }

    @Override
    public boolean isAccountNonLocked() {
        return getStatus() != null && getStatus().getCode() != null && !getStatus().getCode().equals(Status.LOCKED);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isAccountNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return getStatus() != null && getStatus().getCode() != null && getStatus().getCode().equals(Status.ACTIVE);
    }
}
