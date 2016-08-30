package lejendary.oauth2.domain;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 4:49 PM
 */

@Entity
@Table(name = "status")
@Data
public class Status implements Serializable {

    public static final long serialVersionUID = 1L;

    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";
    public static final String LOCKED = "LOCKED";
    public static final String EXPIRED = "EXPIRED";

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "code", length = 50, nullable = false)
    private String code;
}
