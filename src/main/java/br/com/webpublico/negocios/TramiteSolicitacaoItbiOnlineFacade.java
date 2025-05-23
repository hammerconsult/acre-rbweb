package br.com.webpublico.negocios;

import br.com.webpublico.entidades.SolicitacaoItbiOnline;
import br.com.webpublico.entidades.TramiteSolicitacaoItbiOnline;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TramiteSolicitacaoItbiOnlineFacade extends AbstractFacade<TramiteSolicitacaoItbiOnline> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TramiteSolicitacaoItbiOnlineFacade() {
        super(TramiteSolicitacaoItbiOnline.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<TramiteSolicitacaoItbiOnline> buscarTramitesPorSolicitacao(SolicitacaoItbiOnline solicitacao) {
        return em.createQuery("from TramiteSolicitacaoItbiOnline t " +
                " where t.solicitacaoItbiOnline = :solicitacao " +
                " order by t.dataRegistro desc ")
            .setParameter("solicitacao", solicitacao)
            .getResultList();
    }

    public TramiteSolicitacaoItbiOnline buscarUltimoTramiteDaSolicitacao(SolicitacaoItbiOnline solicitacao) {
        return (TramiteSolicitacaoItbiOnline) em.createQuery("from TramiteSolicitacaoItbiOnline t " +
                " where t.solicitacaoItbiOnline = :solicitacao " +
                " order by t.dataRegistro desc ")
            .setParameter("solicitacao", solicitacao)
            .setMaxResults(1)
            .getSingleResult();
    }
}
