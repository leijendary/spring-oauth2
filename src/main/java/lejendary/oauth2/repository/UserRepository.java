package lejendary.oauth2.repository;

import lejendary.oauth2.domain.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/25/2016
 *         Time: 5:01 PM
 */

@Repository
public class UserRepository implements IRepository<User> {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Criteria criteria() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(User.class);
    }

    @Override
    public User get(int i) {
        return null;
    }

    @Override
    public User get(String s) {
        return (User) criteria()
                .add(Restrictions.eq("username", s))
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public void delete(int i) {

    }

    @Override
    public List<User> list(String searchQuery, int pageNumber, String sortOrder, int limit) {
        return null;
    }

    @Override
    public Integer total(String searchQuery) {
        return null;
    }
}
