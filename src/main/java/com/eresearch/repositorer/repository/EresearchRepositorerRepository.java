package com.eresearch.repositorer.repository;


import java.util.Collection;

public interface EresearchRepositorerRepository<T, R, ID> {

    boolean deleteAll();

    boolean delete(String filename);

    Collection<R> find(String filename, boolean fullFetch);

    void store(T data);

    Collection<R> findAll(boolean fullFetch);

    Collection<R> find(boolean fullFetch, ID id);
}
