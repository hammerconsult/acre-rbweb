package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.NFSAvulsa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by tributario on 27/10/2015.
 */
@Service
@Transactional
public class ServiceTratativaDeTransferenciaDeMovimentoDePessoa {
    private static final Logger logger = LoggerFactory.getLogger(ServiceTratativaDeTransferenciaDeMovimentoDePessoa.class);
    @PersistenceContext
    protected transient EntityManager em;

    public NFSAvulsa recuperarNFSAvulsaPeloCalculo(Calculo calculo) {

        StringBuilder hql = new StringBuilder();
        hql.append(" from NFSAvulsa where calculoNFSAvulsa.id = :idCalculo");

        Query q = em.createQuery(hql.toString());
        q.setParameter("idCalculo", calculo.getId());

        List lista = q.getResultList();

        if (!lista.isEmpty()) {
            return (NFSAvulsa) lista.get(0);
        }
        return null;
    }


}
