package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AprovacaoSolicitacaoIncorporacaoMovel;
import br.com.webpublico.entidades.SolicitacaoIncorporacaoMovel;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.SituacaoAprovacao;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by Desenvolvimento on 02/02/2016.
 */
@Stateless
public class AprovacaoSolicitacaoIncorporacaoMovelFacade extends AbstractFacade<AprovacaoSolicitacaoIncorporacaoMovel> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SolicitacaoIncorporacaoMovelFacade incorporacaoMovelFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;

    public AprovacaoSolicitacaoIncorporacaoMovelFacade() {
        super(AprovacaoSolicitacaoIncorporacaoMovel.class);
    }

    @Override
    public AprovacaoSolicitacaoIncorporacaoMovel recuperar(Object id) {
        AprovacaoSolicitacaoIncorporacaoMovel aprovacao = em.find(AprovacaoSolicitacaoIncorporacaoMovel.class, id);
        if (aprovacao.getDetentorArquivoComposicao() != null) {
            aprovacao.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return aprovacao;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(AprovacaoSolicitacaoIncorporacaoMovel entity) {
        movimentarSolicitacaoIncorporacao(entity);
        super.salvarNovo(entity);
    }

    public AprovacaoSolicitacaoIncorporacaoMovel salvarRetornando(AprovacaoSolicitacaoIncorporacaoMovel entity) {
        movimentarSolicitacaoIncorporacao(entity);
        return em.merge(entity);
    }

    private void movimentarSolicitacaoIncorporacao(AprovacaoSolicitacaoIncorporacaoMovel entity) {
        SolicitacaoIncorporacaoMovel solicitacao = entity.getSolicitacao();
        if (SituacaoAprovacao.REJEITADO.equals(entity.getSituacaoAprovacao())) {
            solicitacao.setSituacao(SituacaoEventoBem.RECUSADO);
        } else {
            solicitacao.setSituacao(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        }
        incorporacaoMovelFacade.salvar(solicitacao);
    }

    public Boolean isAprovacaoIncorporacaoEfetivada(SolicitacaoIncorporacaoMovel incorporacaoMovel) {
        if (incorporacaoMovel.getId() == null)
            return false;

        String sql = " select sol.id from SOLICITACAOINCORPORACAOMOV sol " +
            "inner join APROVACAOSOLINCORPMOVEL aprovacao on sol.ID = aprovacao.SOLICITACAO_ID " +
            "inner join EFETSOLICIINCORPOMOVEL efe on sol.ID = efe.SOLICITACAOINCORPORACAO_ID " +
            "where sol.id = :idSolicitacao ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", incorporacaoMovel.getId());
        q.setMaxResults(1);
        return q.getResultList().isEmpty();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SolicitacaoIncorporacaoMovelFacade getIncorporacaoMovelFacade() {
        return incorporacaoMovelFacade;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }
}
