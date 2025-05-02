package br.com.webpublico.seguranca.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractCadastroService<T> implements Serializable /*implements CadastroService<T> */ {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    protected transient EntityManager entityManager;

    //@Override
    public T save(T entity) {
        checkNotNull(entity);
        return entityManager.merge(entity);
    }

    //@Override
    public T remove(T entity) {
        checkNotNull(entity);
        entityManager.remove(entity);
        return entity;
    }

}
