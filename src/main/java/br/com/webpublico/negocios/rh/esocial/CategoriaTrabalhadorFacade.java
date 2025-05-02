package br.com.webpublico.negocios.rh.esocial;

import br.com.webpublico.entidades.rh.esocial.CategoriaTrabalhador;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by William on 05/09/2018.
 */
@Stateless
public class CategoriaTrabalhadorFacade extends AbstractFacade<CategoriaTrabalhador> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public CategoriaTrabalhadorFacade() {
        super(CategoriaTrabalhador.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<CategoriaTrabalhador> getCategoriaTrabalhadorPrestadorServico() {
        String sql = " select * from CategoriaTrabalhador where codigo in :codigosPrestador ";
        Query q = em.createNativeQuery(sql, CategoriaTrabalhador.class);
        q.setParameter("codigosPrestador", getCodigoCategoriaPrestador());
        return q.getResultList();
    }

    private List<String> getCodigoCategoriaPrestador() {
        List<String> codigos = Lists.newArrayList();
        codigos.add("305");
        codigos.add("410");
        codigos.add("701");
        codigos.add("711");
        codigos.add("712");
        codigos.add("721");
        codigos.add("722");
        codigos.add("723");
        codigos.add("731");
        codigos.add("734");
        codigos.add("738");
        codigos.add("741");
        codigos.add("751");
        codigos.add("761");
        codigos.add("781");
        codigos.add("901");
        codigos.add("902");
        codigos.add("903");
        codigos.add("904");
        codigos.add("905");
        return codigos;
    }
}
