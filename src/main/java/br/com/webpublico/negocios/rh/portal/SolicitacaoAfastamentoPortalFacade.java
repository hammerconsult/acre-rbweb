package br.com.webpublico.negocios.rh.portal;

import br.com.webpublico.entidades.Afastamento;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.rh.portal.RHStatusSolicitacaoAfastamentoPortal;
import br.com.webpublico.entidades.rh.portal.SolicitacaoAfastamentoPortal;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AfastamentoFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class SolicitacaoAfastamentoPortalFacade extends AbstractFacade<SolicitacaoAfastamentoPortal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AfastamentoFacade afastamentoFacade;

    public SolicitacaoAfastamentoPortalFacade() {
        super(SolicitacaoAfastamentoPortal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SolicitacaoAfastamentoPortal recuperar(Object id) {
        SolicitacaoAfastamentoPortal solicitacao = super.recuperar(id);
        if (solicitacao.getDetentorArquivoComposicao() != null && solicitacao.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            for (ArquivoComposicao arquivoComposicao : solicitacao.getDetentorArquivoComposicao().getArquivosComposicao()) {
                Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
            }
        }
        return solicitacao;
    }

    public void salvarECriarNotificacao(SolicitacaoAfastamentoPortal entity) {
        SolicitacaoAfastamentoPortal solicitacao = em.merge(entity);
        criarNotificacao(solicitacao);
    }

    public void salvarSolicitacaoEAfastamento(SolicitacaoAfastamentoPortal entity) {
        entity.getAfastamento().setSolicitacaoAfastamento(entity);
        Afastamento afastamento = em.merge(entity.getAfastamento());
        entity.setAfastamento(afastamento);
        em.merge(entity);
    }

    private void criarNotificacao(SolicitacaoAfastamentoPortal solicitacao) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(montarDescricao(solicitacao));
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTipoNotificacao(TipoNotificacao.SOLICITACAO_AFASTAMENTO);
        notificacao.setTitulo("Solicitação de Afastamento - Portal");
        notificacao.setLink("/solicitacao-afastamento/ver/" + solicitacao.getId());
        NotificacaoService.getService().notificar(notificacao);
    }

    private String montarDescricao(SolicitacaoAfastamentoPortal solicitacao) {
        return "Solicitação de afastamento do(a) Servidor(a) " + solicitacao.getContratoFP() + ", no período de  " + DataUtil.getDataFormatada(solicitacao.getDataInicio()) + " à " + DataUtil.getDataFormatada(solicitacao.getDataFim());
    }

    public List<SolicitacaoAfastamentoPortal> buscarSolicitacoesAfastamentos(Long contratoId) {
        String hql = "select s from SolicitacaoAfastamentoPortal s" +
            " where  s.contratoFP.id =:contratoId" +
            " order by s.dataInicio desc, s.status asc";
        Query q = em.createQuery(hql);
        q.setParameter("contratoId", contratoId);

        try {
            List<SolicitacaoAfastamentoPortal> solicitacoes = q.getResultList();
            for (SolicitacaoAfastamentoPortal solicitacao : solicitacoes) {
                if (solicitacao.getDetentorArquivoComposicao() != null
                    && solicitacao.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
                    Hibernate.initialize(solicitacao.getDetentorArquivoComposicao().getArquivosComposicao());
                    for (ArquivoComposicao arquivoComposicao : solicitacao.getDetentorArquivoComposicao().getArquivosComposicao()) {
                        if (arquivoComposicao.getArquivo() != null) {
                            Hibernate.initialize(arquivoComposicao.getArquivo().getPartes());
                        }
                    }
                }
            }
            return solicitacoes;
        } catch (NoResultException e) {
            return Lists.newArrayList();
        }
    }

    public AfastamentoFacade getAfastamentoFacade() {
        return afastamentoFacade;
    }

    public SolicitacaoAfastamentoPortal buscarSolicitacaoConcomitanteComStatusEmAnaliseOuAprovado(SolicitacaoAfastamentoPortal solicitacao) {
        String hql = "from SolicitacaoAfastamentoPortal sa where sa.contratoFP = :contratoFP "
            + " and sa.status in (:emAnaliseOuAprovado) "
            + " and ((:dataInicio between sa.dataInicio and coalesce(sa.dataFim,:dataInicio)) or (:dataFim between sa.dataInicio and coalesce(sa.dataFim,:dataFim))"
            + " or (sa.dataInicio between :dataInicio and :dataFim) or (sa.dataFim between :dataInicio and :dataFim))";
        if (solicitacao.getId() != null) {
            hql += " and sa.id != :id";
        }
        try {
            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", solicitacao.getContratoFP());
            q.setParameter("dataInicio", solicitacao.getDataInicio());
            q.setParameter("dataFim", solicitacao.getDataFim());
            q.setParameter("emAnaliseOuAprovado", Lists.newArrayList(RHStatusSolicitacaoAfastamentoPortal.EM_ANALISE, RHStatusSolicitacaoAfastamentoPortal.APROVADO));
            if (solicitacao.getId() != null) {
                q.setParameter("id", solicitacao.getId());
            }
            q.setMaxResults(1);

            return (SolicitacaoAfastamentoPortal) q.getSingleResult();
        } catch (Exception nr) {
            return null;
        }
    }

    public List<SolicitacaoAfastamentoPortal> buscarSolicitacoesAfastamentoPortalPorContrato(ContratoFP contrato) {
        String sql = " select solicitacao.* from SolicitacaoAfastamentoPortal solicitacao " +
            " where solicitacao.CONTRATOFP_ID =  :contrato ";
        Query q = em.createNativeQuery(sql, SolicitacaoAfastamentoPortal.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}

