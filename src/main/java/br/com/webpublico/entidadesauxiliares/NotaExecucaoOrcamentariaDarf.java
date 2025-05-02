package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class NotaExecucaoOrcamentariaDarf {
    private String tipoGuia;
    private Date dataVencimento;
    private String codigoTributo;
    private String codigoIdentificador;
    private BigDecimal valorPrincipal;
    private Date periodoApuracao;

    public NotaExecucaoOrcamentariaDarf() {
    }

    public String getTipoGuia() {
        return tipoGuia;
    }

    public void setTipoGuia(String tipoGuia) {
        this.tipoGuia = tipoGuia;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getCodigoTributo() {
        return codigoTributo;
    }

    public void setCodigoTributo(String codigoTributo) {
        this.codigoTributo = codigoTributo;
    }

    public String getCodigoIdentificador() {
        return codigoIdentificador;
    }

    public void setCodigoIdentificador(String codigoIdentificador) {
        this.codigoIdentificador = codigoIdentificador;
    }

    public BigDecimal getValorPrincipal() {
        return valorPrincipal;
    }

    public void setValorPrincipal(BigDecimal valorPrincipal) {
        this.valorPrincipal = valorPrincipal;
    }

    public Date getPeriodoApuracao() {
        return periodoApuracao;
    }

    public void setPeriodoApuracao(Date periodoApuracao) {
        this.periodoApuracao = periodoApuracao;
    }
}
