package lejendary.oauth2.domain;

import lejendary.oauth2.security.SecurityUtils;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 3:41 PM
 */

@MappedSuperclass
@Getter
@Setter
abstract class AbstractAuditingEntity {

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", nullable = false, updatable = false)
    private Date dateCreated;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id", updatable = false)
    private User createdBy;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated", nullable = false)
    private Date dateUpdated;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "updated_by", referencedColumnName = "id", updatable = false)
    private User updatedBy;

    @PrePersist
    protected void onCreate() {
        createdBy = updatedBy = SecurityUtils.getCurrentUserLogin();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedBy = SecurityUtils.getCurrentUserLogin();
    }

}
