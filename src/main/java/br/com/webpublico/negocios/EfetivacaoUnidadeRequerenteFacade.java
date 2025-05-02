package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EfetivacaoUnidadeRequerente;
import br.com.webpublico.entidades.SolicitacaoUnidadeRequerente;
import br.com.webpublico.entidades.UnidadeDistribuidora;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.SituacaoSolicitacaoUnidadeRequerente;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class EfetivacaoUnidadeRequerenteFacade extends AbstractFacade<EfetivacaoUnidadeRequerente> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SolicitacaoUnidadeRequerenteFacade solicitacaoUnidadeRequerenteFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EfetivacaoUnidadeRequerenteFacade() {
        super(EfetivacaoUnidadeRequerente.class);
    }

    @Override
    public EfetivacaoUnidadeRequerente recuperar(Object id) {
        EfetivacaoUnidadeRequerente entity = super.recuperar(id);
        SolicitacaoUnidadeRequerente solicitacao = solicitacaoUnidadeRequerenteFacade.recuperar(entity.getSolicitacao().getId());
        entity.setSolicitacao(solicitacao);
        return entity;
    }

    public EfetivacaoUnidadeRequerente salvarEfetivacao(EfetivacaoUnidadeRequerente entity) {
        movimentarSolicitacao(entity);
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(EfetivacaoUnidadeRequerente.class, "codigo"));
        }
        return em.merge(entity);
    }

    private void movimentarSolicitacao(EfetivacaoUnidadeRequerente entity) {
        SolicitacaoUnidadeRequerente solicitacao = entity.getSolicitacao();
        SituacaoSolicitacaoUnidadeRequerente situacao = SituacaoSolicitacaoUnidadeRequerente.APROVADA.equals(entity.getSituacao()) ? SituacaoSolicitacaoUnidadeRequerente.EFETIVADA : SituacaoSolicitacaoUnidadeRequerente.REJEITADA;
        solicitacao.setSituacao(situacao);
        em.merge(solicitacao);
    }

    public SolicitacaoUnidadeRequerenteFacade getSolicitacaoUnidadeRequerenteFacade() {
        return solicitacaoUnidadeRequerenteFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
