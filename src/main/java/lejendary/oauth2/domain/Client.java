package lejendary.oauth2.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/26/2016
 *         Time: 1:23 PM
 */

@Entity
@Table(name = "client", indexes = {
        @Index(name = "UK_client_name", columnList = "name"),
        @Index(name = "UK_client_client_id", columnList = "client_id"),
        @Index(name = "UK_client_client_secret", columnList = "client_secret"),
        @Index(name = "IDX_client_name", columnList = "name")
})
@Data
@EqualsAndHashCode(callSuper = true)
public class Client extends AbstractAuditingEntity implements ClientDetails {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Size(max = 1000)
    @Column(name = "description", length = 1000, columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Size(max = 255)
    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @NotBlank
    @Size(max = 255)
    @Column(name = "client_secret", nullable = false, unique = true)
    private String clientSecret;

    @NotBlank
    @Size(max = 255)
    @Column(name = "redirect_uri", nullable = false)
    private String redirectUri;

    @OneToOne
    @JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "FK_client_status_id"), nullable = false)
    private Status status;

    @Override
    public Set<String> getResourceIds() {
        return Collections.singleton("1");
    }

    @Override
    public boolean isSecretRequired() {
        return false;
    }

    @Override
    public boolean isScoped() {
        return false;
    }

    @Override
    public Set<String> getScope() {
        Set<String> scopes = new HashSet<>();
        scopes.add("read");

        return scopes;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        Set<String> grantTypes = new HashSet<>();
        grantTypes.add("password");
        grantTypes.add("refresh_token");
        return grantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return Collections.singleton(this.redirectUri);
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Set<String> authorities = Collections.singleton("ADMIN");
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>(authorities.size());
        grantedAuthorities.addAll(authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

        return grantedAuthorities;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return 1000;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return 1000;
    }

    @Override
    public boolean isAutoApprove(String s) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return new HashMap<>();
    }
}
