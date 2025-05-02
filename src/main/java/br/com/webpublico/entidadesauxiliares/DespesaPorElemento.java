package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 16/12/2014.
 */
public class DespesaPorElemento {
    private Long id;
    private String descricaoSubAcao;
    private String descricaoConta;
    private BigDecimal valorJaneiro;
    private BigDecimal valorFevereiro;
    private BigDecimal valorMarco;
    private BigDecimal valorAbril;
    private BigDecimal valorMaio;
    private BigDecimal valorJunho;
    private BigDecimal valorJulho;
    private BigDecimal valorAgosto;
    private BigDecimal valorSetembro;
    private BigDecimal valorOutubro;
    private BigDecimal valorNovembro;
    private BigDecimal valorDezembro;
    private BigDecimal total;

    public DespesaPorElemento() {
    }

    public String getDescricaoSubAcao() {
        return descricaoSubAcao;
    }

    public void setDescricaoSubAcao(String descricaoSubAcao) {
        this.descricaoSubAcao = descricaoSubAcao;
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
    }

    public BigDecimal getValorJaneiro() {
        return valorJaneiro;
    }

    public void setValorJaneiro(BigDecimal valorJaneiro) {
        this.valorJaneiro = valorJaneiro;
    }

    public BigDecimal getValorFevereiro() {
        return valorFevereiro;
    }

    public void setValorFevereiro(BigDecimal valorFevereiro) {
        this.valorFevereiro = valorFevereiro;
    }

    public BigDecimal getValorMarco() {
        return valorMarco;
    }

    public void setValorMarco(BigDecimal valorMarco) {
        this.valorMarco = valorMarco;
    }

    public BigDecimal getValorAbril() {
        return valorAbril;
    }

    public void setValorAbril(BigDecimal valorAbril) {
        this.valorAbril = valorAbril;
    }

    public BigDecimal getValorMaio() {
        return valorMaio;
    }

    public void setValorMaio(BigDecimal valorMaio) {
        this.valorMaio = valorMaio;
    }

    public BigDecimal getValorJunho() {
        return valorJunho;
    }

    public void setValorJunho(BigDecimal valorJunho) {
        this.valorJunho = valorJunho;
    }

    public BigDecimal getValorJulho() {
        return valorJulho;
    }

    public void setValorJulho(BigDecimal valorJulho) {
        this.valorJulho = valorJulho;
    }

    public BigDecimal getValorAgosto() {
        return valorAgosto;
    }

    public void setValorAgosto(BigDecimal valorAgosto) {
        this.valorAgosto = valorAgosto;
    }

    public BigDecimal getValorSetembro() {
        return valorSetembro;
    }

    public void setValorSetembro(BigDecimal valorSetembro) {
        this.valorSetembro = valorSetembro;
    }

    public BigDecimal getValorOutubro() {
        return valorOutubro;
    }

    public void setValorOutubro(BigDecimal valorOutubro) {
        this.valorOutubro = valorOutubro;
    }

    public BigDecimal getValorNovembro() {
        return valorNovembro;
    }

    public void setValorNovembro(BigDecimal valorNovembro) {
        this.valorNovembro = valorNovembro;
    }

    public BigDecimal getValorDezembro() {
        return valorDezembro;
    }

    public void setValorDezembro(BigDecimal valorDezembro) {
        this.valorDezembro = valorDezembro;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
