package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.negocios.tributario.auxiliares.GeradorDeIds;
import br.com.webpublico.interfaces.AssitenteDoGeradorDeIds;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonIdIPTU implements Serializable, AssitenteDoGeradorDeIds {

    private final int PRIMEIRO = 0;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private GeradorDeIds geradorDeIds;
    private Integer quantidade;


    @PostConstruct
    private void init() {
        geradorDeIds = new GeradorDeIds(this);
    }

    @Lock(LockType.READ)
    public long getProximoID() {
        return geradorDeIds.getProximoId();
    }


    @Override
    public int getQuantidade() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JdbcDaoSupport getDao() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
