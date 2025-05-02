package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.math.BigDecimal;

/**
 * Created by Fabio on 29/06/2017.
 */
public class InadimplenciaParcelas {

    private Inadimplencia inadimplencia;
    private Long idParcela;
    private String descricaoDivida;
    private BigDecimal valor;
    private BigDecimal valorTotal;
    private BigDecimal imposto;
    private BigDecimal taxa;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal honorarios;
    private BigDecimal desconto;

    public InadimplenciaParcelas(Inadimplencia inadimplencia, ResultadoParcela resultadoParcela) {
        this.inadimplencia = inadimplencia;
        this.idParcela = resultadoParcela.getIdParcela();
        this.valor = resultadoParcela.getValorTotal();
        this.imposto = resultadoParcela.getValorImposto();
        this.taxa = resultadoParcela.getValorTaxa();
        this.juros = resultadoParcela.getValorJuros();
        this.multa = resultadoParcela.getValorMulta();
        this.correcao = resultadoParcela.getValorCorrecao();
        this.honorarios = resultadoParcela.getValorHonorarios();
        this.desconto = resultadoParcela.getValorDesconto();
        this.descricaoDivida = resultadoParcela.getDivida();
    }

    public InadimplenciaParcelas() {
        zeraValores();
    }

    public Inadimplencia getInadimplencia() {
        return inadimplencia;
    }

    public void setInadimplencia(Inadimplencia inadimplencia) {
        this.inadimplencia = inadimplencia;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getImposto() {
        return imposto;
    }

    public void setImposto(BigDecimal imposto) {
        this.imposto = imposto;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }

    public void setTaxa(BigDecimal taxa) {
        this.taxa = taxa;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getHonorarios() {
        return honorarios;
    }

    public void setHonorarios(BigDecimal honorarios) {
        this.honorarios = honorarios;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getDescricaoDivida() {
        return descricaoDivida;
    }

    public void setDescricaoDivida(String descricaoDivida) {
        this.descricaoDivida = descricaoDivida;
    }

    public void zeraValores() {
        valor = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        imposto = BigDecimal.ZERO;
        taxa = BigDecimal.ZERO;
        juros = BigDecimal.ZERO;
        multa = BigDecimal.ZERO;
        correcao = BigDecimal.ZERO;
        honorarios = BigDecimal.ZERO;
        desconto = BigDecimal.ZERO;
    }
}
