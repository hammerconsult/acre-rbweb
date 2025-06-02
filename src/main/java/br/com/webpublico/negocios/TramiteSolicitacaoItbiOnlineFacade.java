package br.com.webpublico.negocios;

import br.com.webpublico.entidades.SolicitacaoItbiOnline;
import br.com.webpublico.entidades.TramiteSolicitacaoItbiOnline;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
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

    @Override
    public TramiteSolicitacaoItbiOnline recuperar(Object id) {
        TramiteSolicitacaoItbiOnline tramite = super.recuperar(id);
        if (tramite.getDocumentos() != null) Hibernate.initialize(tramite.getDocumentos());
        return tramite;
    }

    public List<TramiteSolicitacaoItbiOnline> buscarTramitesPorSolicitacao(SolicitacaoItbiOnline solicitacao) {
        List<BigDecimal> idsTramites = em.createNativeQuery("select t.id from tramitesolicitacaoitbionline t " +
                " where t.solicitacaoitbionline_id = :idSolicitacao " +
                " order by t.dataregistro desc ")
            .setParameter("idSolicitacao", solicitacao.getId())
            .getResultList();

        List<TramiteSolicitacaoItbiOnline> tramites = Lists.newArrayList();
        for (BigDecimal idTramite : idsTramites) {
            tramites.add(recuperar(idTramite.longValue()));
        }
        return tramites;
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
