package lejendary.oauth2.repository;

import lejendary.oauth2.domain.Client;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/26/2016
 *         Time: 4:38 PM
 */

@Repository
public class ClientRepository implements IRepository<Client> {

    private final SessionFactory sessionFactory;

    @Autowired
    public ClientRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Criteria criteria() {
        Session session = sessionFactory.getCurrentSession();
        return session.createCriteria(Client.class);
    }

    @Override
    public Client get(int i) {
        return null;
    }

    @Override
    public Client get(String s) {
        return (Client) criteria()
                .add(Restrictions.eq("clientId", s))
                .setMaxResults(1)
                .uniqueResult();
    }

    @Override
    public Client save(Client client) {
        return null;
    }

    @Override
    public Client update(Client client) {
        return null;
    }

    @Override
    public void delete(int i) {
    }

    @Override
    public List<Client> list(String searchQuery, int pageNumber, String sortOrder, int limit) {
        return null;
    }

    @Override
    public Integer total(String searchQuery) {
        return null;
    }
}
