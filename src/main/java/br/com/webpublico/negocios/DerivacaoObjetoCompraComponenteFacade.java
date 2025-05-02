package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DerivacaoObjetoCompra;
import br.com.webpublico.entidades.DerivacaoObjetoCompraComponente;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class DerivacaoObjetoCompraComponenteFacade extends AbstractFacade<DerivacaoObjetoCompraComponente> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DerivacaoObjetoCompraComponenteFacade() { super(DerivacaoObjetoCompraComponente.class); }

    public List<DerivacaoObjetoCompraComponente> buscarComponentesPorDerivacao(DerivacaoObjetoCompra derivacao) {
        String sql =  " select dc.* from derivacaoobjcompracomp dc " +
            " where dc.derivacaoobjetocompra_id = :idDerivacao ";
        Query q = em.createNativeQuery(sql, DerivacaoObjetoCompraComponente.class);
        q.setParameter("idDerivacao", derivacao.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<>();
        }
    }
}
