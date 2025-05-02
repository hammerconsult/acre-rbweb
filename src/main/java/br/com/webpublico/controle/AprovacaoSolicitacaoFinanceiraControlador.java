package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.controlerelatorio.RelatorioAcompanhamentoLiberacaoFinanceira;
import br.com.webpublico.controlerelatorio.RelatorioSolicitacaoCotaFinanceiraControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.FiltroAprovacaoSolicitacaoFinanceira;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.LiberacaoCotaFinanceiraFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.SolicitacaoCotaFinanceiraFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 23/07/14
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "aprovacaoSolicitacaoFinanceiraControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "aprovar-solicitacao-financeira",
        pattern = "/aprovacao-solicitacao-financeira/#{aprovacaoSolicitacaoFinanceiraControlador.id}/notificacao/#{aprovacaoSolicitacaoFinanceiraControlador.notificao}/",
        viewId = "/faces/financeiro/orcamentario/solicitacaocotafinanceira/aprovar-solicitacao.xhtml"),

    @URLMapping(id = "aprovar-nova-solicitacao-financeira",
        pattern = "/aprovacao-solicitacao-financeira/",
        viewId = "/faces/financeiro/orcamentario/solicitacaocotafinanceira/aprovacao.xhtml"),

    @URLMapping(id = "lista-aprovar-solicitacao-financeira",
        pattern = "/lista-aprovar-solicitacao-financeira/",
        viewId = "/faces/financeiro/orcamentario/solicitacaocotafinanceira/lista-aprovacao.xhtml")
})

public class AprovacaoSolicitacaoFinanceiraControlador extends AbstractReport implements Serializable {

    @EJB
    private SolicitacaoCotaFinanceiraFacade solicitacaoCotaFinanceiraFacade;
    @EJB
    private LiberacaoCotaFinanceiraFacade liberacaoCotaFinanceiraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Long id;
    private Long notificao;
    private SolicitacaoCotaFinanceira solicitacaoCotaFinanceira;
    private Notificacao notificacaoRecuperada;
    private Boolean cancelarSaldoRestante;
    private BigDecimal valorAprovado;
    private List<LiberacaoCotaFinanceira> liberacoes;
    private Boolean mostrarAutoCompleteLiberacao;

    private FiltroAprovacaoSolicitacaoFinanceira filtroAprovacao;

    @URLAction(mappingId = "aprovar-solicitacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        solicitacaoCotaFinanceira = solicitacaoCotaFinanceiraFacade.recuperar(id);
        definirSolicitacao();
        if (notificao != null || notificao != 0) {
//            notificacaoRecuperada = NotificacaoService.getService().recuperar(notificao);
        }
        mostrarAutoCompleteLiberacao = Boolean.FALSE;
    }

    @URLAction(mappingId = "aprovar-nova-solicitacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaAprovacao() {
        mostrarAutoCompleteLiberacao = Boolean.TRUE;
    }

    @URLAction(mappingId = "lista-aprovar-solicitacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void listar() {
        filtroAprovacao = new FiltroAprovacaoSolicitacaoFinanceira();
        Date dataOperacao = sistemaFacade.getDataOperacao();
        filtroAprovacao.setDataInicial(DataUtil.primeiroDiaMes(dataOperacao).getTime());
        filtroAprovacao.setDataFinal(dataOperacao);
        filtroAprovacao.setExercicio(sistemaFacade.getExercicioCorrente());
        filtroAprovacao.setPendente(Boolean.TRUE);
    }

    private void recuperarLiberacoes() {
        if (liberacoes == null) {
            liberacoes = Lists.newArrayList();
        }
        if (solicitacaoCotaFinanceira != null) {
            liberacoes = liberacaoCotaFinanceiraFacade.buscarLiberacaoPorSolicitacao(solicitacaoCotaFinanceira);
        }
    }

    public Boolean orcamentoVigente() {
        return solicitacaoCotaFinanceira != null
            && solicitacaoCotaFinanceira.getResultanteIndependente() != null
            && ResultanteIndependente.RESULTANTE_EXECUCAO_ORCAMENTARIA.equals(solicitacaoCotaFinanceira.getResultanteIndependente());
    }

    public void aprovarSolicitacao() {
        try {
            validarSolicitacao();
            solicitacaoCotaFinanceira = solicitacaoCotaFinanceiraFacade.aprovarSolicitacao(solicitacaoCotaFinanceira, valorAprovado);
            FacesUtil.addOperacaoRealizada(" Solicitação " + solicitacaoCotaFinanceira.toString() + " aprovada com sucesso.");
            limparNotificacoes();
            redirecionarParaVerSolicitacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            solicitacaoCotaFinanceiraFacade.getSingletonConcorrenciaContabil().desbloquear(solicitacaoCotaFinanceira);
            descobrirETratarException(ex);
        }
    }

    protected void descobrirETratarException(Exception e) {
        Throwable t = BuscaCausa.desenrolarException(e);
        if (t.getClass().equals(SQLIntegrityConstraintViolationException.class) || e.getClass().equals(StaleStateException.class)) {
            FacesUtil.addOperacaoNaoRealizada("O movimento já foi salvo por outro usuário! Atualize a página e tente novamente.");

        } else if (t.getClass().equals(OptimisticLockException.class) || e.getClass().equals(StaleObjectStateException.class)) {
            FacesUtil.addOperacaoNaoRealizada("O movimento já foi salvo por outro usuário! Atualize a página e tente novamente.");

        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }


    private void limparNotificacoes() {
        try {
            solicitacaoCotaFinanceiraFacade.movimentarNoficiacoes(solicitacaoCotaFinanceira, notificacaoRecuperada, valorAprovado);
        } catch (Exception e) {
            FacesUtil.addError("Operação Não Realizada ao criar as notificações!", e.getMessage());
        }
    }

    private void redirecionarParaVerSolicitacao() {
        FacesUtil.redirecionamentoInterno("/solicitacao-financeira/ver/" + solicitacaoCotaFinanceira.getId() + "/");
    }

    private void validarSolicitacao() {
        validarCampoSolicitacao();
        validarValorAprovado();
    }

    private void validarValorAprovado() {
        ValidacaoException ve = new ValidacaoException();
        String mensagem = "O Valor Aprovado deve ser menor que o Saldo Restante a ser Aprovado.";

        if (StatusSolicitacaoCotaFinanceira.A_APROVAR.equals(solicitacaoCotaFinanceira.getStatus()) && solicitacaoCotaFinanceira.getSaldoAprovar().compareTo(BigDecimal.ZERO) == 0) {
            solicitacaoCotaFinanceira.setSaldoAprovar(solicitacaoCotaFinanceira.getValorSolicitado());
            mensagem = "O Valor a aprovado deve ser menor ou igual ao valor solicitado.";
        }
        if (valorAprovado.compareTo(solicitacaoCotaFinanceira.getSaldoAprovar()) > 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagem);
        }
        if (!solicitacaoCotaFinanceira.getExercicio().equals(getSistemaFacade().getExercicioCorrente())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício da solicitação financeira (" + solicitacaoCotaFinanceira.getExercicio().getAno() + ") é diferente do exercício logado (" + getSistemaFacade().getExercicioCorrente().getAno() + ").");
        }
        ve.lancarException();
    }

    public void selecionarSolicitacao(ActionEvent event) {
        solicitacaoCotaFinanceira = (SolicitacaoCotaFinanceira) event.getComponent().getAttributes().get("objeto");
        if (solicitacaoCotaFinanceira != null) {
            definirSolicitacao();
        }
    }

    public void cancelarSolicitacao() {
        try {
            validarCancelarSolicitacao();
            solicitacaoCotaFinanceira = solicitacaoCotaFinanceiraFacade.salvarCancelamento(solicitacaoCotaFinanceira, notificacaoRecuperada);
            FacesUtil.addOperacaoRealizada(" Solicitação cancelada com sucesso.");
            retornarParaListaSolicitacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void retornarParaListaSolicitacao() {
        FacesUtil.redirecionamentoInterno("/solicitacao-financeira/listar/");
    }

    private void validarCancelarSolicitacao() {
        validarCampoSolicitacao();
        validarCampos();
    }

    private void validarCampoSolicitacao() {
        ValidacaoException ve = new ValidacaoException();
        if (solicitacaoCotaFinanceira == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Solicitação Financeira deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (!cancelarSaldoRestante) {
            ve.adicionarMensagemDeCampoObrigatorio("Para cancelar a solicitação é necessário selecionar a opção 'Cancelar Saldo Restante' e informar os campos obrigatórios.");
        } else {
            if (solicitacaoCotaFinanceira.getDataCancelamento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Cancelamento deve ser informado.");
            }
            if (solicitacaoCotaFinanceira.getMotivoCancelamento() == null || solicitacaoCotaFinanceira.getMotivoCancelamento().trim().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Motivo do Cancelamento deve ser informado.");
            }
        }
        ve.lancarException();
    }

    public List<SolicitacaoCotaFinanceira> completarSolicitacao(String parte) {
        return solicitacaoCotaFinanceiraFacade.buscarSolicitacaoPendentesAprovacaoPorUnidadeAndExercicio(parte.trim(),
            solicitacaoCotaFinanceiraFacade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente(),
            solicitacaoCotaFinanceiraFacade.getSistemaFacade().getExercicioCorrente());
    }

    public void definirSolicitacao() {
        if (orcamentoVigente()) {
            solicitacaoCotaFinanceiraFacade.recuperarSaldoOrcamentarioSolicitacao(solicitacaoCotaFinanceira.getUnidadeOrganizacional(), solicitacaoCotaFinanceira.getDtSolicitacao(), solicitacaoCotaFinanceira.getElementosDespesa());
        }
        valorAprovado = solicitacaoCotaFinanceira.getSaldoAprovar();
        if (valorAprovado.compareTo(BigDecimal.ZERO) == 0) {
            valorAprovado = solicitacaoCotaFinanceira.getValorSolicitado();
        }
        cancelarSaldoRestante = Boolean.FALSE;
        recuperarLiberacoes();
    }

    public void gerarRelatorio() {
        try {
            if (solicitacaoCotaFinanceira != null) {
                RelatorioAcompanhamentoLiberacaoFinanceira controlador = (RelatorioAcompanhamentoLiberacaoFinanceira) Util.getControladorPeloNome("relatorioAcompanhamentoLiberacaoFinanceira");
                int mesInicial = DataUtil.getMes(getSistemaFacade().getDataOperacao()) - 1;
                int mesFinal = mesInicial + 1;
                String mesFinalString = mesFinal >= 10 ? mesFinal + "" : "0" + mesFinal;
                controlador.setExibirContasBancarias(Boolean.TRUE);
                controlador.setMesReferencia(Mes.getMesToInt(Integer.valueOf(mesFinalString)));
                controlador.setHierarquias(new ArrayList<HierarquiaOrganizacional>());
                controlador.getHierarquias().add(recuperarHierarquia(solicitacaoCotaFinanceira.getUnidadeOrganizacional()));
                controlador.gerarRelatorio();
            } else {
                FacesUtil.addOperacaoNaoRealizada("Selecione uma solicitação para gerar o relatório de Acompanhamento de Liberação Financeira.");
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private HierarquiaOrganizacional recuperarHierarquia(UnidadeOrganizacional unidadeOrganizacional) {
        return solicitacaoCotaFinanceiraFacade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), unidadeOrganizacional, getSistemaFacade().getDataOperacao());
    }

    public BigDecimal getValorAprovado() {
        return valorAprovado;
    }

    public void setValorAprovado(BigDecimal valorAprovado) {
        this.valorAprovado = valorAprovado;
    }

    public Notificacao getNotificacaoRecuperada() {
        return notificacaoRecuperada;
    }

    public void setNotificacaoRecuperada(Notificacao notificacaoRecuperada) {
        this.notificacaoRecuperada = notificacaoRecuperada;
    }

    public SolicitacaoCotaFinanceira getSolicitacaoCotaFinanceira() {
        return solicitacaoCotaFinanceira;
    }

    public void setSolicitacaoCotaFinanceira(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        this.solicitacaoCotaFinanceira = solicitacaoCotaFinanceira;
    }

    public Long getNotificao() {
        return notificao;
    }

    public void setNotificao(Long notificao) {
        this.notificao = notificao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getCancelarSaldoRestante() {
        return cancelarSaldoRestante;
    }

    public void setCancelarSaldoRestante(Boolean cancelarSaldoRestante) {
        this.cancelarSaldoRestante = cancelarSaldoRestante;
    }

    public Boolean mostrarLiberacoes() {
        if (liberacoes != null && !liberacoes.isEmpty()) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public List<LiberacaoCotaFinanceira> getLiberacoes() {
        return liberacoes;
    }

    public void setLiberacoes(List<LiberacaoCotaFinanceira> liberacoes) {
        this.liberacoes = liberacoes;
    }

    public Boolean getMostrarAutoCompleteLiberacao() {
        return mostrarAutoCompleteLiberacao;
    }

    public void setMostrarAutoCompleteLiberacao(Boolean mostrarAutoCompleteLiberacao) {
        this.mostrarAutoCompleteLiberacao = mostrarAutoCompleteLiberacao;
    }

    public void prepararCanelamentoSolicitacao() {
        solicitacaoCotaFinanceira.setDataCancelamento(getSistemaFacade().getDataOperacao());
    }

    public FiltroAprovacaoSolicitacaoFinanceira getFiltroAprovacao() {
        return filtroAprovacao;
    }

    public void setFiltroAprovacao(FiltroAprovacaoSolicitacaoFinanceira filtroAprovacao) {
        this.filtroAprovacao = filtroAprovacao;
    }

    public void limparSolicitacoes() {
        filtroAprovacao.setSolicitacoes(Lists.<SolicitacaoCotaFinanceira>newArrayList());
    }

    public void buscarSolicitacoes() {
        try {
            limparSolicitacoes();
            List<SolicitacaoCotaFinanceira> solicitacaoCotaFinanceiras = solicitacaoCotaFinanceiraFacade.buscarSolicitacaoPendentesAprovacao(filtroAprovacao);
            filtroAprovacao.setSolicitacoes(solicitacaoCotaFinanceiras);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void visualizarSolicitacao(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {
        FacesUtil.redirecionamentoInterno("/aprovacao-solicitacao-financeira/" + solicitacaoCotaFinanceira.getId() + "/notificacao/0/");
    }

    public void gerarRelatorioSolicitacoes() {
        try {
            RelatorioSolicitacaoCotaFinanceiraControlador relatorioSolicitacaoCotaFinanceiraControlador = (RelatorioSolicitacaoCotaFinanceiraControlador) Util.getControladorPeloNome("relatorioSolicitacaoCotaFinanceiraControlador");
            relatorioSolicitacaoCotaFinanceiraControlador.setDataInicial(filtroAprovacao.getDataInicial());
            relatorioSolicitacaoCotaFinanceiraControlador.setDataFinal(filtroAprovacao.getDataFinal());
            relatorioSolicitacaoCotaFinanceiraControlador.setListaUnidades(filtroAprovacao.getUnidades());
            relatorioSolicitacaoCotaFinanceiraControlador.setUnidadeGestora(filtroAprovacao.getUnidadeGestora());
            relatorioSolicitacaoCotaFinanceiraControlador.setApresentacao(ApresentacaoRelatorio.CONSOLIDADO);
            relatorioSolicitacaoCotaFinanceiraControlador.gerarRelatorio(String.valueOf(TipoRelatorioDTO.PDF));
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Ocorreu um erro ao gerar o relatório: " + e.getMessage());
        }
    }
}
