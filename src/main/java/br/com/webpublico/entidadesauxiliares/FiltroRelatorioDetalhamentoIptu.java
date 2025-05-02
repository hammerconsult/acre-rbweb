package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoImovel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 21/07/15
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class FiltroRelatorioDetalhamentoIptu implements Serializable {

    private ProcessoCalculoIPTU processoCalculoIPTU;
    private String setorInicial;
    private String setorFinal;
    private Quadra quadra;
    private Bairro bairro;
    private String cadastroInicial;
    private String cadastroFinal;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private Logradouro logradouro;
    private TipoImovel tipoImovel;
    private Exercicio exercicio;
    private Boolean ativo;
    private Boolean consideraDesconto;
    private Boolean somenteEfetivados;
    private StringBuilder filtrosUtilizados;

    public FiltroRelatorioDetalhamentoIptu() {
        cadastroInicial = "100000000000000";
        cadastroFinal = "999999999999999";
        valorInicial = BigDecimal.ZERO;
        valorFinal = new BigDecimal("999999999999.99");
        ativo = true;
        consideraDesconto = true;
        somenteEfetivados = true;
    }

    public FiltroRelatorioDetalhamentoIptu(Exercicio exercicio) {
        this();
        this.exercicio = exercicio;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public void setProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU) {
        this.processoCalculoIPTU = processoCalculoIPTU;
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

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
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

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getConsideraDesconto() {
        return consideraDesconto != null ? consideraDesconto : false;
    }

    public void setConsideraDesconto(Boolean consideraDesconto) {
        this.consideraDesconto = consideraDesconto;
    }

    public Boolean getSomenteEfetivados() {
        return somenteEfetivados;
    }

    public void setSomenteEfetivados(Boolean somenteEfetivados) {
        this.somenteEfetivados = somenteEfetivados;
    }

    public StringBuilder getFiltrosUtilizados() {
        return filtrosUtilizados;
    }

    public void setFiltrosUtilizados(StringBuilder filtrosUtilizados) {
        this.filtrosUtilizados = filtrosUtilizados;
    }
}

