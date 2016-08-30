package lejendary.oauth2.repository.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 3:50 PM
 */

@Repository
public class UtilRepository {

    private final SessionFactory sessionFactory;

    @Inject
    public UtilRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Date getDatabaseDate() {
        Session session = sessionFactory.getCurrentSession();
        return (Date) session.createSQLQuery("select NOW()")
                .uniqueResult();
    }

}
