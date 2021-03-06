package lejendary.oauth2.repository;

import org.hibernate.Criteria;

import java.util.List;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/25/2016
 *         Time: 5:02 PM
 */

interface IRepository<T> {

    Criteria criteria();

    T get(int i);

    T get(String s);

    T save(T t);

    T update(T t);

    void delete(int i);

    List<T> list(String searchQuery, int pageNumber, String sortOrder, int limit);

    Integer total(String searchQuery);

}
