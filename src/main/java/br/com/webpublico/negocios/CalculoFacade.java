package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.ValorDivida;
import br.com.webpublico.negocios.tributario.arrecadacao.CalculoExecutorDepoisDePagar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 27/07/13
 * Time: 11:06
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class CalculoFacade extends CalculoExecutorDepoisDePagar<Calculo> {

    protected static final Logger logger = LoggerFactory.getLogger(CalculoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CalculoFacade() {
        super(Calculo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Calculo recuperar(Object id) {
        return (Calculo) em.find(Calculo.class, id);
    }

    public Calculo retornaCalculoDoValorDivida(ValorDivida vd) {
        try {
            javax.persistence.Query q = em.createQuery(" select vd.calculo from ValorDivida vd where vd = :valordivida");
            q.setParameter("valordivida", vd);
            List<Calculo> resultado = q.getResultList();
            if (q.getResultList().isEmpty()) {
                return null;
            } else {
                Calculo cl = resultado.get(0);
                cl.getPessoas().size();
                return cl;
            }
        } catch (Exception ex) {
            logger.error("Erro ao buscar o calculo do valor divida {}", ex);
            return null;
        }
    }

    @Override
    public void depoisDePagar(Calculo calculo) {
        logger.debug("Pagando calculo de {} ", calculo.getTipoCalculo());
    }
}
