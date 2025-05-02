package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.ProcessoJudicial;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 22/07/15
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelacaoLancamentoDividaAtiva extends AbstractFiltroRelacaoLancamentoDebito {


    private Integer exercicioCertidaoInicial;
    private Integer exercicioCertidaoFinal;
    private String numeroCertidaoInicial;
    private String numeroCertidaoFinal;
    private String numeroAjuizamento;
    private TipoCadastroTributario tipoCadastroTributario;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private Integer ajuizado;
    private Integer protestado;
    private ProcessoJudicial.Situacao situacaoProcessoJudicial;

    public FiltroRelacaoLancamentoDividaAtiva() {
        setContribuintes(new ArrayList<Pessoa>());
        setSituacoes(new SituacaoParcela[]{SituacaoParcela.EM_ABERTO});
        setTipoRelatorio(TipoRelatorio.RESUMIDO);
        setSomenteTotalizador(Boolean.FALSE);
        this.ajuizado = 2;
        this.protestado = 2;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }


    public String getNumeroCertidaoInicial() {
        return numeroCertidaoInicial;
    }

    public void setNumeroCertidaoInicial(String numeroCertidaoInicial) {
        this.numeroCertidaoInicial = numeroCertidaoInicial;
    }

    public String getNumeroCertidaoFinal() {
        return numeroCertidaoFinal;
    }

    public void setNumeroCertidaoFinal(String numeroCertidaoFinal) {
        this.numeroCertidaoFinal = numeroCertidaoFinal;
    }

    public String getNumeroAjuizamento() {
        return numeroAjuizamento;
    }

    public void setNumeroAjuizamento(String numeroAjuizamento) {
        this.numeroAjuizamento = numeroAjuizamento;
    }

    public Integer getExercicioCertidaoInicial() {
        return exercicioCertidaoInicial;
    }

    public void setExercicioCertidaoInicial(Integer exercicioCertidaoInicial) {
        this.exercicioCertidaoInicial = exercicioCertidaoInicial;
    }

    public Integer getExercicioCertidaoFinal() {
        return exercicioCertidaoFinal;
    }

    public void setExercicioCertidaoFinal(Integer exercicioCertidaoFinal) {
        this.exercicioCertidaoFinal = exercicioCertidaoFinal;
    }

    public Integer getAjuizado() {
        return ajuizado;
    }

    public void setAjuizado(Integer ajuizado) {
        this.ajuizado = ajuizado;
    }

    public Integer getProtestado() {
        return protestado;
    }

    public void setProtestado(Integer protestado) {
        this.protestado = protestado;
    }

    public ProcessoJudicial.Situacao getSituacaoProcessoJudicial() {
        return situacaoProcessoJudicial;
    }

    public void setSituacaoProcessoJudicial(ProcessoJudicial.Situacao situacaoProcessoJudicial) {
        this.situacaoProcessoJudicial = situacaoProcessoJudicial;
    }

    public void processaMudancaTipoCadastro() {
        inscricaoInicial = "";
        inscricaoFinal = "";
        setPessoa(null);
        setContribuintes(Lists.<Pessoa>newArrayList());
    }

    public void processarMudancaAjuizamento() {
        numeroAjuizamento = null;
    }
}
