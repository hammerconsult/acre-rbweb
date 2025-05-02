package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fabio on 10/04/2018.
 */
public class VOItemLoteBaixa implements Comparable<VOItemLoteBaixa> {

    private Long id;
    private Long idDam;
    private Long idLoteBaixa;
    private BigDecimal valorPago;
    private BigDecimal valorOriginalDam;
    private BigDecimal jurosDam;
    private BigDecimal multaDam;
    private BigDecimal correcaoDam;
    private BigDecimal honorariosDam;
    private BigDecimal descontoDam;
    private Date dataPagamento;
    private Date dataContabilizacao;
    private String conta;
    private String banco;
    private String codigoLote;
    private Long idBanco;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }

    public BigDecimal getValorPago() {
        return valorPago != null ? valorPago : BigDecimal.ZERO;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorOriginalDam() {
        return valorOriginalDam != null ? valorOriginalDam : BigDecimal.ZERO;
    }

    public void setValorOriginalDam(BigDecimal valorOriginalDam) {
        this.valorOriginalDam = valorOriginalDam;
    }

    public BigDecimal getJurosDam() {
        return jurosDam != null ? jurosDam : BigDecimal.ZERO;
    }

    public void setJurosDam(BigDecimal jurosDam) {
        this.jurosDam = jurosDam;
    }

    public BigDecimal getMultaDam() {
        return multaDam != null ? multaDam : BigDecimal.ZERO;
    }

    public void setMultaDam(BigDecimal multaDam) {
        this.multaDam = multaDam;
    }

    public BigDecimal getCorrecaoDam() {
        return correcaoDam != null ? correcaoDam : BigDecimal.ZERO;
    }

    public void setCorrecaoDam(BigDecimal correcaoDam) {
        this.correcaoDam = correcaoDam;
    }

    public BigDecimal getHonorariosDam() {
        return honorariosDam != null ? honorariosDam : BigDecimal.ZERO;
    }

    public void setHonorariosDam(BigDecimal honorariosDam) {
        this.honorariosDam = honorariosDam;
    }

    public BigDecimal getDescontoDam() {
        return descontoDam != null ? descontoDam : BigDecimal.ZERO;
    }

    public void setDescontoDam(BigDecimal descontoDam) {
        this.descontoDam = descontoDam;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public Long getIdBanco() {
        return idBanco;
    }

    public void setIdBanco(Long idBanco) {
        this.idBanco = idBanco;
    }

    public Date getDataContabilizacao() {
        return dataContabilizacao;
    }

    public void setDataContabilizacao(Date dataContabilizacao) {
        this.dataContabilizacao = dataContabilizacao;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public BigDecimal getTotalAcrescimosDamSemHonorarios() {
        return (getJurosDam().add(getMultaDam().add(getCorrecaoDam())));
    }

    public Long getIdLoteBaixa() {
        return idLoteBaixa;
    }

    public void setIdLoteBaixa(Long idLoteBaixa) {
        this.idLoteBaixa = idLoteBaixa;
    }

    public BigDecimal getTotal() {
        if (idDam != null) {
            return getValorOriginalDam().add(getTotalAcrescimosDamSemHonorarios()).add(getHonorariosDam()).subtract(getDescontoDam());
        }
        return BigDecimal.ZERO;
    }

    public String getBancoConta() {
        return getBanco() + " - " + getConta();
    }

    @Override
    public int compareTo(VOItemLoteBaixa o) {
        return this.getId().compareTo(o.getId());
    }
}
