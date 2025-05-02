package br.com.webpublico.negocios.tributario.singletons;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Map;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonGeradorIdentificacaoPatrimonio implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Class, Long> mapaCodigos;

    @Lock(LockType.WRITE)
    public Long getProximoCodigo(Class classe, String campo) {
        return null;
    }
}
