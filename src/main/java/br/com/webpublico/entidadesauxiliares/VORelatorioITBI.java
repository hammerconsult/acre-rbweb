package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 18/04/2017.
 */
public class VORelatorioITBI {
    private Integer numeroITBI;
    private Integer anoExercicio;
    private Date dataLancamento;
    private String imovel;
    private List<VORelatorioITBIPessoa> adquirentes;
    private List<VORelatorioITBIPessoa> transmitentes;
    private String situacao;
    private BigDecimal baseCalculo;
    private BigDecimal valorITBI;
    private BigDecimal percentualPago;
    private BigDecimal valorPago;
    private Date dataEmissaoLaudo;
    private String usuarioLaudo;

    public VORelatorioITBI() {
        adquirentes = Lists.newArrayList();
        transmitentes = Lists.newArrayList();
    }

    public Integer getNumeroITBI() {
        return numeroITBI;
    }

    public void setNumeroITBI(Integer numeroITBI) {
        this.numeroITBI = numeroITBI;
    }

    public Integer getAnoExercicio() {
        return anoExercicio;
    }

    public void setAnoExercicio(Integer anoExercicio) {
        this.anoExercicio = anoExercicio;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getImovel() {
        return imovel;
    }

    public void setImovel(String imovel) {
        this.imovel = imovel;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getValorITBI() {
        return valorITBI;
    }

    public void setValorITBI(BigDecimal valorITBI) {
        this.valorITBI = valorITBI;
    }

    public BigDecimal getPercentualPago() {
        return valorITBI.compareTo(BigDecimal.ZERO) > 0 ? valorPago.divide(valorITBI, 10, BigDecimal.ROUND_HALF_EVEN) : BigDecimal.ZERO;
    }

    public void setPercentualPago(BigDecimal percentualPago) {
        this.percentualPago = percentualPago;
    }

    public List<VORelatorioITBIPessoa> getTransmitentes() {
        return transmitentes;
    }

    public void setTransmitentes(List<VORelatorioITBIPessoa> transmitentes) {
        this.transmitentes = transmitentes;
    }

    public List<VORelatorioITBIPessoa> getAdquirentes() {
        return adquirentes;
    }

    public void setAdquirentes(List<VORelatorioITBIPessoa> adquirentes) {
        this.adquirentes = adquirentes;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public Date getDataEmissaoLaudo() {
        return dataEmissaoLaudo;
    }

    public void setDataEmissaoLaudo(Date dataEmissaoLaudo) {
        this.dataEmissaoLaudo = dataEmissaoLaudo;
    }

    public String getUsuarioLaudo() {
        return usuarioLaudo;
    }

    public void setUsuarioLaudo(String usuarioLaudo) {
        this.usuarioLaudo = usuarioLaudo;
    }
}
