package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoNegociacao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class TipoNegociacaoFacade extends AbstractFacade<TipoNegociacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoNegociacaoFacade() {
        super(TipoNegociacao.class);
    }

    public TipoNegociacao recuperaPorDescricao(String descricao) {
        Query q = em.createQuery("from TipoNegociacao tn where trim(lower(tn.descricao)) = :descricao").setParameter("descricao", descricao.trim().toLowerCase());
        List<TipoNegociacao> retorno = q.getResultList();
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        } else {
            return null;
        }
    }

    public List<TipoNegociacao> buscarTiposNegociacaoAtivas() {
        String sql = " select * from tiponegociacao where ativo = 1";
        Query q = em.createNativeQuery(sql, TipoNegociacao.class);
        return q.getResultList();
    }
}
