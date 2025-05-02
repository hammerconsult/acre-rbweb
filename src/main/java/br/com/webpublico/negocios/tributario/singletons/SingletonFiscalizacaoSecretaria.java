package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.negocios.SistemaFacade;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonFiscalizacaoSecretaria implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Integer numero;
    @EJB
    private SistemaFacade sistemaFacade;

    @PostConstruct
    private void init() {
        numero = ((BigDecimal) em.createNativeQuery("select coalesce(max(numero),0) from Denuncia where exercicio_id = :ano")
                .setParameter("ano", sistemaFacade.getExercicioCorrente().getId())
                .getSingleResult()).intValue();
    }

    @Lock(LockType.WRITE)
    public Integer getProximoCodigoDenuncia() {
        numero = numero + 1;
        return numero;
    }
}
