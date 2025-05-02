package br.com.webpublico.negocios.rh.portal;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.portal.ProgramacaoFeriasPortal;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.SugestaoFeriasFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoEmailFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.EmailService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @Author peixe on 08/11/2016  08:35.
 */
@Stateless
public class ProgramacaoFeriasPortalFacade extends AbstractFacade<ProgramacaoFeriasPortal> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SugestaoFeriasFacade sugestaoFeriasFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoEmailFacade configuracaoEmailFacade;

    public ProgramacaoFeriasPortalFacade() {
        super(ProgramacaoFeriasPortal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfiguracaoRHFacade getConfiguracaoRHFacade() {
        return configuracaoRHFacade;
    }

    public List<ProgramacaoFeriasPortal> buscarProgramacoesPorPeriodoAquisitivo(PeriodoAquisitivoFL periodo) {
        Query q = em.createQuery("from ProgramacaoFeriasPortal p where p.periodoAquisitivoFL = :periodo");
        q.setParameter("periodo", periodo);
        return q.getResultList();
    }

    @Override
    public void salvar(ProgramacaoFeriasPortal entity) {
        ProgramacaoFeriasPortal programacao = em.merge(entity);
        criarNotificacao(programacao);
    }

    private void criarNotificacao(ProgramacaoFeriasPortal programacao) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(montarDescricao(programacao));
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTipoNotificacao(TipoNotificacao.PROGRAMACAO_FERIAS_PORTAL);
        notificacao.setTitulo("Solicitação de Programação de Férias - Portal");
        notificacao.setLink("/programacao-ferias-portal/ver/" + programacao.getId());
        NotificacaoService.getService().notificar(notificacao);
    }

    private String montarDescricao(ProgramacaoFeriasPortal programacao) {
        String descricao = "Solicitada a Sugestão da Programação de Férias do(a) Servidor(a) " + programacao.getPeriodoAquisitivoFL().getContratoFP() + ", no período de  " + DataUtil.getDataFormatada(programacao.getDataInicio()) + " à " + DataUtil.getDataFormatada(programacao.getDataFim());
        return descricao;
    }

    public void tornarProgramacaoProtalEfetivaParaSugestaoFerias(ProgramacaoFeriasPortal selecionado) throws MessagingException {
        validarRegistroExistente(selecionado);
        SugestaoFerias sugestaoFerias = criarSugestao(selecionado);
        AprovacaoFerias aprovacao = criarAprovacaoFerias(sugestaoFerias);
        em.merge(selecionado);
        SugestaoFerias sugestao = sugestaoFeriasFacade.merge(sugestaoFerias);
        aprovacao.setSugestaoFerias(sugestao);
        em.merge(aprovacao);
        String mensagem = montarMensagem(true, selecionado);
        enviarEmail(mensagem, selecionado.getPeriodoAquisitivoFL().getContratoFP());
    }

    private AprovacaoFerias criarAprovacaoFerias(SugestaoFerias sugestaoFerias) {
        AprovacaoFerias aprovacaoFerias = new AprovacaoFerias();
        aprovacaoFerias.setAprovado(true);
        aprovacaoFerias.setSugestaoFerias(sugestaoFerias);
        aprovacaoFerias.setDataAprovacao(sistemaFacade.getDataOperacao());
        return aprovacaoFerias;
    }


    public void rejeitarSugestaoPortal(ProgramacaoFeriasPortal selecionado) throws MessagingException {
        String mensagem = montarMensagem(false, selecionado);
        enviarEmail(mensagem, selecionado.getPeriodoAquisitivoFL().getContratoFP());
        remover(selecionado);
    }

    private String montarMensagem(boolean aceito, ProgramacaoFeriasPortal programacao) {
        String nome = programacao.getPeriodoAquisitivoFL().getContratoFP().getMatriculaFP().getPessoa().getNome();
        String mensagem = "Olá " + nome + ", a sua solicitação de programação de férias realizado pelo portal, solicitada no período de "
            + DataUtil.getDataFormatada(programacao.getDataInicio()) + " à " + DataUtil.getDataFormatada(programacao.getDataFim());
        if (aceito) {
            mensagem += " foi aceito pelo gestor responsável.";
        } else {
            mensagem += " não foi aceito pelo gestor responsável.";
        }
        return mensagem;
    }

    private void enviarEmail(String mensagem, ContratoFP contratoFP) throws MessagingException {
        if (configuracaoRHFacade.recuperarConfiguracaoRHVigente().getNotificarFeriasPortal()) {
            String email = contratoFP.getMatriculaFP().getPessoa().getEmail();
            EmailService.getInstance().enviarEmail(email, "Solicitação de Férias - Portal", mensagem);
        }
    }

    private SugestaoFerias criarSugestao(ProgramacaoFeriasPortal selecionado) {
        SugestaoFerias sugestao = new SugestaoFerias();
        sugestao.setAbonoPecunia(selecionado.getAbonoPecunia());
        sugestao.setDataInicio(selecionado.getDataInicio());
        sugestao.setDataFim(selecionado.getDataFim());
        sugestao.setPeriodoAquisitivoFL(selecionado.getPeriodoAquisitivoFL());
        sugestao.setDiasAbono(selecionado.getDiasAbono());
        return sugestao;
    }

    private void validarRegistroExistente(ProgramacaoFeriasPortal selecionado) {
        ValidacaoException val = new ValidacaoException();
        logger.debug("{}", selecionado.getPeriodoAquisitivoFL().getSugestaoFerias());
        if (sugestaoFeriasFacade.buscaFiltrandoPeriodoAquisitivoFL(selecionado.getPeriodoAquisitivoFL()) != null) {
            val.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma Programação de Férias para o Periodo Aquisitivo Informado.");
        }
        val.lancarException();
    }
}

