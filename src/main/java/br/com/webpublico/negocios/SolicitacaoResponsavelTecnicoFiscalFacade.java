package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AprovacaoResponsavelTecnicoFiscal;
import br.com.webpublico.entidades.SolicitacaoResponsaveltecnicoFiscal;
import br.com.webpublico.enums.SituacaoSolicitacaoFiscal;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Desenvolvimento on 30/03/2016.
 */
@Stateless
public class SolicitacaoResponsavelTecnicoFiscalFacade extends AbstractFacade<SolicitacaoResponsaveltecnicoFiscal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ResponsavelTecnicoFiscalFacade responsavelTecnicoFiscalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SolicitacaoResponsavelTecnicoFiscalFacade() {
        super(SolicitacaoResponsaveltecnicoFiscal.class);
    }

    public ResponsavelTecnicoFiscalFacade getResponsavelTecnicoFiscalFacade() {
        return responsavelTecnicoFiscalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public SolicitacaoResponsaveltecnicoFiscal recuperar(Object id) {
        SolicitacaoResponsaveltecnicoFiscal sol = super.recuperar(id);
        sol.setAprovacoes(buscarAprovacoesDaSolicitacao(sol));
        return sol;
    }

    @Override
    public void salvarNovo(SolicitacaoResponsaveltecnicoFiscal entity) {
        entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoResponsaveltecnicoFiscal.class, "codigo"));
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(SolicitacaoResponsaveltecnicoFiscal entity) {
        entity.setSituacaoSolicitacao(SituacaoSolicitacaoFiscal.AGUARDANDO_APROVACAO);
        super.salvar(entity);
    }

    public List<SolicitacaoResponsaveltecnicoFiscal> buscarSolicitacoesSemAprovacao(String parte) {
        String sql = "SELECT sol.* " +
                     "  FROM solicresponsatecnicofiscal sol " +
                     " inner join responsaveltecnicofiscal resp on resp.id = sol.responsaveltecnicofiscal_id" +
                     " where sol.situacaosolicitacao = :sit  " +
                     "  and (to_char(sol.codigo) like :parte or lower(sol.descricao) like :parte " +
                     "      or to_char(sol.datasolicitacao,'dd/MM/yyyy') like :parte " +
                     "      or lower(resp.tiporesponsavel) like :parte or lower(resp.tipofiscal) like :parte)" +
                     " order by sol.codigo desc";
        Query q = em.createNativeQuery(sql, SolicitacaoResponsaveltecnicoFiscal.class);
        q.setParameter("sit", SituacaoSolicitacaoFiscal.AGUARDANDO_APROVACAO.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<AprovacaoResponsavelTecnicoFiscal> buscarAprovacoesDaSolicitacao(SolicitacaoResponsaveltecnicoFiscal solicitacao){
        return em.createQuery(" select a " +
            "                     from AprovacaoResponsavelTecnicoFiscal a " +
            " where a.solicitacao.id = :id_sol " +
            " order by a.codigo", AprovacaoResponsavelTecnicoFiscal.class).setParameter("id_sol", solicitacao.getId()).getResultList();
    }
}
