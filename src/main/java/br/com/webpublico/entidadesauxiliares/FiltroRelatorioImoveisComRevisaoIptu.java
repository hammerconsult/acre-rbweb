package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoImovel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 21/07/15
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelatorioImoveisComRevisaoIptu implements Serializable {

    private String protocolo;
    private ProcessoCalculoIPTU processoCalculoIPTU;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private Date dataLancamentoInicial;
    private Date dataLancamentoFinal;
    private Date dataVencimentoInicial;
    private Date dataVencimentoFinal;
    private Date dataPagamentoInicial;
    private Date dataPagamentoFinal;
    private TipoImovel tipoImovel;
    private String setorInicial;
    private String setorFinal;
    private String quadraInicial;
    private String quadraFinal;
    private Bairro bairro;
    private Logradouro logradouro;
    private String cadastroInicial;
    private String cadastroFinal;
    private Pessoa proprietario;
    private Pessoa compromissario;
    private SituacaoParcela[] situacoes;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private Boolean detalhar;
    private Boolean cancelarProcesso;

    private StringBuilder filtrosUtilizados;

    public FiltroRelatorioImoveisComRevisaoIptu() {
        cadastroInicial = "100000000000000";
        cadastroFinal = "999999999999999";
        valorInicial = BigDecimal.ZERO;
        valorFinal = new BigDecimal("999999.99");
        detalhar = Boolean.FALSE;
        cancelarProcesso = Boolean.FALSE;
        filtrosUtilizados = new StringBuilder();
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public void setProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU) {
        this.processoCalculoIPTU = processoCalculoIPTU;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Date getDataLancamentoInicial() {
        return dataLancamentoInicial;
    }

    public void setDataLancamentoInicial(Date dataLancamentoInicial) {
        this.dataLancamentoInicial = dataLancamentoInicial;
    }

    public Date getDataLancamentoFinal() {
        return dataLancamentoFinal;
    }

    public void setDataLancamentoFinal(Date dataLancamentoFinal) {
        this.dataLancamentoFinal = dataLancamentoFinal;
    }

    public Date getDataVencimentoInicial() {
        return dataVencimentoInicial;
    }

    public void setDataVencimentoInicial(Date dataVencimentoInicial) {
        this.dataVencimentoInicial = dataVencimentoInicial;
    }

    public Date getDataVencimentoFinal() {
        return dataVencimentoFinal;
    }

    public void setDataVencimentoFinal(Date dataVencimentoFinal) {
        this.dataVencimentoFinal = dataVencimentoFinal;
    }

    public Date getDataPagamentoInicial() {
        return dataPagamentoInicial;
    }

    public void setDataPagamentoInicial(Date dataPagamentoInicial) {
        this.dataPagamentoInicial = dataPagamentoInicial;
    }

    public Date getDataPagamentoFinal() {
        return dataPagamentoFinal;
    }

    public void setDataPagamentoFinal(Date dataPagamentoFinal) {
        this.dataPagamentoFinal = dataPagamentoFinal;
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public String getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public String getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(String quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public String getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(String quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public Pessoa getProprietario() {
        return proprietario;
    }

    public void setProprietario(Pessoa proprietario) {
        this.proprietario = proprietario;
    }

    public Pessoa getCompromissario() {
        return compromissario;
    }

    public void setCompromissario(Pessoa compromissario) {
        this.compromissario = compromissario;
    }

    public SituacaoParcela[] getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(SituacaoParcela[] situacoes) {
        this.situacoes = situacoes;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Boolean getDetalhar() {
        return detalhar;
    }

    public void setDetalhar(Boolean detalhar) {
        this.detalhar = detalhar;
    }

    public StringBuilder getFiltrosUtilizados() {
        return filtrosUtilizados;
    }

    public void setFiltrosUtilizados(StringBuilder filtrosUtilizados) {
        this.filtrosUtilizados = filtrosUtilizados;
    }

    public Boolean getCancelarProcesso() {
        return cancelarProcesso;
    }

    public void setCancelarProcesso(Boolean cancelarProcesso) {
        this.cancelarProcesso = cancelarProcesso;
    }
}

