package br.com.webpublico.util;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoMovimentoAdministrativo;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.exception.MovimentacaoBemException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
import com.google.common.collect.Lists;
import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;

import javax.persistence.OptimisticLockException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class AssistenteMovimentacaoBens extends AssistenteBarraProgresso implements Serializable {

    public static final String MSG_ERRO_SITUACOES_IGUAIS = " já se encontra com o estado e situação de conservação igual ao selecionado para a solicitação de alteração.";
    private List<LogErroEfetivacaoBaixaBem> logErrosEfetivacaoBaixa;
    private UnidadeOrganizacional unidadeOrganizacional;
    private Date dataLancamento;
    private SituacaoMovimentoAdministrativo situacaoMovimento;
    private TipoEventoBem tipoEventoBem;
    private EstadoBem estadoInicial;
    private EstadoBem estadoResultante;
    private Bem bem;
    private ConfigMovimentacaoBem configMovimentacaoBem;
    private Operacoes operacao;
    private Boolean bloquearAcoesTela;
    private Boolean simularContabilizacao;
    private List<BemVo> bensMovimentadosVo; //bens salvos
    private List<BemVo> bensDisponiveisVo;
    private List<BemVo> bensSelecionadosVo;

    private List<Bem> bensSalvos;
    private List<Bem> bensDisponiveis;
    private List<Bem> bensSelecionados;
    private List<Bem> bensBloqueados;
    private Future<List<BemVo>> futurePesquisaBemVo;
    private CompletableFuture<List<Bem>> completableFuturePesquisaBemVo;
    private Future<AssistenteMovimentacaoBens> futureSalvar;
    private CompletableFuture<AssistenteMovimentacaoBens> completableFuture;
    private List<Number> idsBensBloqueados;
    private List<UnidadeOrganizacional> unidades;
    private List<SolicitacaoAlienacao> solicitacoesAlienacao;
    private List<LoteTransferenciaBem> transferenciasBens;

    private Boolean temErros;

    public AssistenteMovimentacaoBens(Date dataLancamento, Operacoes operacao) {
        super();
        temErros = Boolean.FALSE;
        bloquearAcoesTela = Boolean.FALSE;
        simularContabilizacao = Boolean.FALSE;
        zerarContadoresProcesso();
        solicitacoesAlienacao = Lists.newArrayList();
        transferenciasBens = Lists.newArrayList();
        bensMovimentadosVo = Lists.newArrayList();
        bensSelecionadosVo = Lists.newArrayList();
        bensDisponiveisVo = Lists.newArrayList();

        bensSalvos = Lists.newArrayList();
        bensBloqueados = Lists.newArrayList();
        bensSelecionados = Lists.newArrayList();
        bensDisponiveis = Lists.newArrayList();
        logErrosEfetivacaoBaixa = Lists.newArrayList();
        idsBensBloqueados = Lists.newArrayList();
        unidades = Lists.newArrayList();
        this.operacao = operacao;
        this.dataLancamento = dataLancamento;
    }

    public void lancarMensagemBensBloqueioSingleton() {
        MovimentacaoBemException ve = new MovimentacaoBemException();
        if (hasInconsistencias() && getBloquearAcoesTela()) {
            for (String msg : getMensagens()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(msg);
            }
        }
        getMensagens().clear();
        ve.lancarException();
    }

    public void descobrirETratarException(Exception e) {
        e = (Exception) BuscaCausa.desenrolarException(e);
        if (e.getClass().equals(OptimisticLockException.class) ||
            e.getClass().equals(SQLIntegrityConstraintViolationException.class) ||
            e.getClass().equals(StaleStateException.class) ||
            e.getClass().equals(StaleObjectStateException.class)) {
            FacesUtil.addOperacaoNaoRealizada("O movimento já foi salvo por outro usuário! Atualize a página e tente novamente.");
        } else {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível completar a operação solicitada. Verifique os passos executados e tente novamente. Se o problema persistir entre em contato com o suporte técnico.");
        }
    }

    public void configurarInicializacao(String descricaoProcesso, int quantidadeRegistros) {
        zerarContadoresProcesso();
        setDescricaoProcesso(descricaoProcesso);
        setTotal(quantidadeRegistros);
    }

    public boolean hasBensMovimentadosVo() {
        return bensMovimentadosVo != null && !bensMovimentadosVo.isEmpty();
    }

    public boolean hasBensSelecionadoVo() {
        return bensSelecionadosVo != null && !bensSelecionadosVo.isEmpty();
    }

    public boolean hasBensDisponivel() {
        return bensDisponiveisVo != null && !bensDisponiveisVo.isEmpty();
    }

    public Boolean mostrarBotaoSelecionarTodos() {
        try {
            return bensDisponiveisVo.size() != bensSelecionadosVo.size();
        } catch (NullPointerException ex) {
            return Boolean.FALSE;
        }
    }

    public Boolean bemSelecionado(BemVo bem) {
        return bensSelecionadosVo.contains(bem);
    }

    public void desselecionarTodosBens() {
        bensSelecionadosVo.clear();
    }

    public void desselecionarBem(BemVo bemVo) {
        bensSelecionadosVo.remove(bemVo);
    }

    public void selecionarBem(BemVo bem) {
        try {
            validarSelecaoBem(Lists.newArrayList(bem));
            bensSelecionadosVo.add(bem);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void selecionarTodosBens() {
        try {
            desselecionarTodosBens();
            bensSelecionadosVo.addAll(bensDisponiveisVo);
            validarSelecaoBem(bensDisponiveisVo);
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    private void validarSelecaoBem(List<BemVo> bens) {
        if (tipoEventoBem != null && tipoEventoBem.isSolicitacaoAlteracaoConservacao()) {
            ValidacaoException ve = new ValidacaoException();
            for (BemVo bem : bens) {
                SolicitacaoAlteracaoConservacaoBem selecionado = (SolicitacaoAlteracaoConservacaoBem) getSelecionado();
                if (selecionado.getEstadoConservacao().equals(bem.getEstadoResultante().getEstadoBem())
                    && selecionado.getSituacaoConservacao().equals(bem.getEstadoResultante().getSituacaoConservacaoBem())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O bemVo " + bem.getBem().getIdentificacao() + ":" + MSG_ERRO_SITUACOES_IGUAIS);
                    desselecionarBem(bem);
                }
            }
            ve.lancarException();
        }
    }

    public TipoEventoBem getTipoEventoBem() {
        return tipoEventoBem;
    }

    public void setTipoEventoBem(TipoEventoBem tipoEventoBem) {
        this.tipoEventoBem = tipoEventoBem;
    }

    public Future<AssistenteMovimentacaoBens> getFutureSalvar() {
        return futureSalvar;
    }

    public void setFutureSalvar(Future<AssistenteMovimentacaoBens> futureSalvar) {
        this.futureSalvar = futureSalvar;
    }

    public CompletableFuture<AssistenteMovimentacaoBens> getCompletableFuture() {
        return completableFuture;
    }

    public void setCompletableFuture(CompletableFuture<AssistenteMovimentacaoBens> completableFuture) {
        this.completableFuture = completableFuture;
    }

    public CompletableFuture<List<Bem>> getCompletableFuturePesquisaBemVo() {
        return completableFuturePesquisaBemVo;
    }

    public void setCompletableFuturePesquisaBem(CompletableFuture<List<Bem>> completableFuturePesquisaBemVo) {
        this.completableFuturePesquisaBemVo = completableFuturePesquisaBemVo;
    }

    public Future<List<BemVo>> getFuturePesquisaBemVo() {
        return futurePesquisaBemVo;
    }

    public void setFuturePesquisaBemVo(Future<List<BemVo>> futurePesquisaBemVo) {
        this.futurePesquisaBemVo = futurePesquisaBemVo;
    }

    public EstadoBem getEstadoInicial() {
        return estadoInicial;
    }

    public void setEstadoInicial(EstadoBem estadoInicial) {
        this.estadoInicial = estadoInicial;
    }

    public EstadoBem getEstadoResultante() {
        return estadoResultante;
    }

    public void setEstadoResultante(EstadoBem estadoResultante) {
        this.estadoResultante = estadoResultante;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public String getMensagemBensBloqueadoSingleton() {
        return "Estão sendo gerados novos movimentos para um ou mais bens selecionados. Por favor, aguarde alguns instantes e execute o processo novamente.";
    }

    public List<BemVo> getBensMovimentadosVo() {
        return bensMovimentadosVo;
    }

    public void setBensMovimentadosVo(List<BemVo> bensMovimentadosVo) {
        this.bensMovimentadosVo = bensMovimentadosVo;
    }

    public List<BemVo> getBensDisponiveisVo() {
        return bensDisponiveisVo;
    }

    public void setBensDisponiveisVo(List<BemVo> bensDisponiveisVo) {
        this.bensDisponiveisVo = bensDisponiveisVo;
    }

    public List<BemVo> getBensSelecionadosVo() {
        return bensSelecionadosVo;
    }

    public void setBensSelecionadosVo(List<BemVo> bensSelecionadosVo) {
        this.bensSelecionadosVo = bensSelecionadosVo;
    }

    public List<SolicitacaoAlienacao> getSolicitacoesAlienacao() {
        return solicitacoesAlienacao;
    }

    public void setSolicitacoesAlienacao(List<SolicitacaoAlienacao> solicitacoesAlienacao) {
        this.solicitacoesAlienacao = solicitacoesAlienacao;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public ConfigMovimentacaoBem getConfigMovimentacaoBem() {
        return configMovimentacaoBem;
    }

    public void setConfigMovimentacaoBem(ConfigMovimentacaoBem configMovimentacaoBem) {
        this.configMovimentacaoBem = configMovimentacaoBem;
    }

    public List<LogErroEfetivacaoBaixaBem> getLogErrosEfetivacaoBaixa() {
        return logErrosEfetivacaoBaixa;
    }

    public void setLogErrosEfetivacaoBaixa(List<LogErroEfetivacaoBaixaBem> logErrosEfetivacaoBaixa) {
        this.logErrosEfetivacaoBaixa = logErrosEfetivacaoBaixa;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public boolean isOperacaoNovo() {
        return Operacoes.NOVO.equals(operacao);
    }

    public boolean isOperacaoEditar() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public boolean isOperacaoVer() {
        return Operacoes.VER.equals(operacao);
    }

    public boolean hasInconsistencias() {
        return getMensagens() != null && !getMensagens().isEmpty();
    }

    public List<Bem> getBensSalvos() {
        return bensSalvos;
    }

    public void setBensSalvos(List<Bem> bensSalvos) {
        this.bensSalvos = bensSalvos;
    }

    public List<Bem> getBensBloqueados() {
        return bensBloqueados;
    }

    public void setBensBloqueados(List<Bem> bensBloqueados) {
        this.bensBloqueados = bensBloqueados;
    }

    public List<Bem> getBensSelecionados() {
        return bensSelecionados;
    }

    public void setBensSelecionados(List<Bem> bensSelecionados) {
        this.bensSelecionados = bensSelecionados;
    }

    public List<Bem> getBensDisponiveis() {
        return bensDisponiveis;
    }

    public void setBensDisponiveis(List<Bem> bensDisponiveis) {
        this.bensDisponiveis = bensDisponiveis;
    }

    public Boolean getBloquearAcoesTela() {
        return bloquearAcoesTela;
    }

    public void setBloquearAcoesTela(Boolean bloquearAcoesTela) {
        this.bloquearAcoesTela = bloquearAcoesTela;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<UnidadeOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public Boolean getSimularContabilizacao() {
        return simularContabilizacao;
    }

    public void setSimularContabilizacao(Boolean simularContabilizacao) {
        this.simularContabilizacao = simularContabilizacao;
    }

    public List<Number> getIdsBensBloqueados() {
        return idsBensBloqueados;
    }

    public void setIdsBensBloqueados(List<Number> idsBensBloqueados) {
        this.idsBensBloqueados = idsBensBloqueados;
    }

    public SituacaoMovimentoAdministrativo getSituacaoMovimento() {
        return situacaoMovimento;
    }

    public void setSituacaoMovimento(SituacaoMovimentoAdministrativo situacaoMovimento) {
        this.situacaoMovimento = situacaoMovimento;
    }

    public List<LoteTransferenciaBem> getTransferenciasBens() {
        return transferenciasBens;
    }

    public void setTransferenciasBens(List<LoteTransferenciaBem> transferenciasBens) {
        this.transferenciasBens = transferenciasBens;
    }

    public BigDecimal getValorTotalAjusteInicial() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasBensMovimentadosVo()) {
            for (BemVo bem : bensMovimentadosVo) {
                total = total.add(bem.getValorAjusteInicial());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjuste() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasBensMovimentadosVo()) {
            for (BemVo bem : bensMovimentadosVo) {
                total = total.add(bem.getValorAjuste());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjusteFinal() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasBensMovimentadosVo()) {
            for (BemVo bem : bensMovimentadosVo) {
                total = total.add(bem.getValorAjusteFinal());
            }
        }
        return total;
    }

    public Boolean getTemErros() {
        return temErros;
    }

    public void setTemErros(Boolean temErros) {
        this.temErros = temErros;
    }
}
