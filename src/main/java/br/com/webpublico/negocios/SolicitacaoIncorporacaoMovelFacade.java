package br.com.webpublico.negocios;

import br.com.webpublico.enums.administrativo.SituacaoAprovacao;
import br.com.webpublico.entidades.AprovacaoSolicitacaoIncorporacaoMovel;
import br.com.webpublico.entidades.DocumetoComprobatorioIncorporacaoMovel;
import br.com.webpublico.entidades.SolicitacaoIncorporacaoMovel;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Util;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Desenvolvimento on 29/01/2016.
 */
@Stateless
public class SolicitacaoIncorporacaoMovelFacade extends AbstractFacade<SolicitacaoIncorporacaoMovel> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public SolicitacaoIncorporacaoMovelFacade() {
        super(SolicitacaoIncorporacaoMovel.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SolicitacaoIncorporacaoMovel recuperar(Object id) {
        SolicitacaoIncorporacaoMovel sol = super.recuperar(id);
        sol.getItensSolicitacaoIncorporacaoMovel().size();
        sol.getDetentorOrigemRecurso().getListaDeOriemRecursoBem().size();
        sol.getDocumetosComprobatorio().size();
        if (sol.getDocumetosComprobatorio().size() > 0) {
            for (DocumetoComprobatorioIncorporacaoMovel doc : sol.getDocumetosComprobatorio()) {
                if (doc.getDetentorArquivo() != null && doc.getDetentorArquivo().getArquivosComposicao() != null) {
                    doc.getDetentorArquivo().getArquivosComposicao().size();
                }
            }
        }
        if (sol.getDetentorArquivoComposicao() != null) {
            sol.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return sol;
    }

    public List<SolicitacaoIncorporacaoMovel> completaSolicitacaoIncorporacaoMovelSemAprovacao(String parte) {
        String hql = "select s " +
            "           from SolicitacaoIncorporacaoMovel s " +
            "         where (to_char(lower(s.codigo)) like :parte " +
            "               or lower(s.unidadeAdministrativa.descricao) like :parte)" +
            "              and s.situacao = :sit ";
        Query q = em.createQuery(hql, SolicitacaoIncorporacaoMovel.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("sit", SituacaoEventoBem.CONCLUIDO);
        return q.getResultList();
    }

    public List<SolicitacaoIncorporacaoMovel> completaSolicitacaoIncorporacaoMovelComAprovacao(String parte) {
        String hql = "select s " +
            "           from SolicitacaoIncorporacaoMovel s " +
            "         where (to_char(lower(s.codigo)) like :parte " +
            "               or lower(s.unidadeAdministrativa.descricao) like :parte)" +
            "              and s.situacao = :sit ";
        Query q = em.createQuery(hql, SolicitacaoIncorporacaoMovel.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("sit", SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        return q.getResultList();
    }

    public SolicitacaoIncorporacaoMovel concluirSolicitacao(SolicitacaoIncorporacaoMovel selecionado) {
        Util.validarCampos(selecionado);
        selecionado.setSituacao(SituacaoEventoBem.CONCLUIDO);
        if (selecionado.getCodigo() == null) {
            selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoIncorporacaoMovel.class, "codigo"));
        }
        return salvarRetornando(selecionado);
    }

    public Boolean isSolicitacaoIncorporacaoAprovada(SolicitacaoIncorporacaoMovel incorporacaoMovel, SituacaoAprovacao aprovacao) {
        if (incorporacaoMovel.getId() == null)
            return false;

        String sql = "select sol.id from SOLICITACAOINCORPORACAOMOV sol " +
            "inner join APROVACAOSOLINCORPMOVEL aprovacao on sol.ID = aprovacao.SOLICITACAO_ID " +
            "where sol.id = :idSolicitacao and aprovacao.situacaoAprovacao = :situacao";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", incorporacaoMovel.getId());
        q.setParameter("situacao", aprovacao.name());
        q.setMaxResults(1);
        return q.getResultList().isEmpty();
    }

    public SolicitacaoIncorporacaoMovel salvarRetornando(SolicitacaoIncorporacaoMovel selecionado) {
        return em.merge(selecionado);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoObjetoCompraGrupoBemFacade getGrupoObjetoCompraGrupoBemFacade() {
        return grupoObjetoCompraGrupoBemFacade;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public AprovacaoSolicitacaoIncorporacaoMovel buscarAprovavaoSolicitacaoDaSolicitacaoDeIncorporacao(SolicitacaoIncorporacaoMovel soliticacao) {
        String sql = "select * from APROVACAOSOLINCORPMOVEL where SOLICITACAO_ID = :soliticacao and SITUACAOAPROVACAO = :situacao";
        Query q = em.createNativeQuery(sql, AprovacaoSolicitacaoIncorporacaoMovel.class);
        q.setParameter("soliticacao", soliticacao.getId());
        q.setParameter("situacao", SituacaoAprovacao.REJEITADO.name());
        if (!q.getResultList().isEmpty()) {
            return (AprovacaoSolicitacaoIncorporacaoMovel) q.getResultList().get(0);
        }
        return null;
    }
}
