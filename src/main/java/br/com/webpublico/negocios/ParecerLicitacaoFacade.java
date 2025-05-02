package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.ParecerLicitacao;
import br.com.webpublico.enums.TipoParecerLicitacao;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created with IntelliJ IDEA.
 * User: JULIO CESAR-MGA
 * Date: 19/03/14
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ParecerLicitacaoFacade extends AbstractFacade<ParecerLicitacao> {

    private static final Logger logger = LoggerFactory.getLogger(ParecerLicitacaoFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TipoDocumentoAnexoFacade tipoDocumentoAnexoFacade;

    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;


    public ParecerLicitacaoFacade() {
        super(ParecerLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public Integer retornaProximaSequencia(Licitacao licitacao) {
        return ((Long) em.createQuery("select count(p)+1 From ParecerLicitacao p where p.licitacao.id = :id").setParameter("id", licitacao.getId()).getResultList().get(0)).intValue();
    }

    public Licitacao salvarLicitacao(Licitacao licitacao) {
        return em.merge(licitacao);
    }

    @Override
    public ParecerLicitacao recuperar(Object id) {
        ParecerLicitacao entity = super.recuperar(id);
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        return entity;
    }

    public ParecerLicitacao recuperaIdParecer(Licitacao licitacao) {
        return (ParecerLicitacao) em.createQuery("select p from ParecerLicitacao p where p.licitacao.id = :id order by p.id desc  ").setParameter("id", licitacao.getId()).setMaxResults(1).getResultList().get(0);
    }

    public boolean recuperarParecerDoTipoJuridicoEdital(Licitacao licitacao) {
        String sql = "select p from ParecerLicitacao p where p.licitacao.id = :id and p.tipoParecerLicitacao = '" + TipoParecerLicitacao.JURIDICO_EDITAL.name() + "' ";

        Query q = em.createQuery(sql);
        q.setParameter("id", licitacao.getId());
        q.setMaxResults(1);

        return q.getResultList().isEmpty() ? false : true;
    }

    public Integer recuperaUltimoNumero() {
        String hql = "select coalesce(max(pl.numero), 0) from ParecerLicitacao pl";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        Integer numero = (Integer) q.getSingleResult();
        if (numero != null) {
            return numero + 1;
        } else {
            return 1;
        }
    }

    public ParecerLicitacao salvarRetornando(ParecerLicitacao entity) {
        try {
            return getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
        return null;
    }

    public TipoDocumentoAnexoFacade getTipoDocumentoAnexoFacade() {
        return tipoDocumentoAnexoFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }
}
