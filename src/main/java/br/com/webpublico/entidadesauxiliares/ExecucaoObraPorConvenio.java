package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by wellington on 23/05/2017.
 */
public class ExecucaoObraPorConvenio implements Serializable {

    private Long idConvenio;
    private String numeroConvenio;
    private Integer exercicioConvenio;
    private String objetoConvenio;
    private BigDecimal valorRepasseConvenio;
    private BigDecimal valorContrapartidaConvenio;
    private BigDecimal valorTotalConvenio;
    private Long idContrato;
    private Integer exercicioContrato;
    private Integer numeroContrato;
    private String objetoContrato;
    private String contratadoContrato;
    private BigDecimal valorTotalContrato;
    private BigDecimal valorRepasseConvenioContrato;
    private BigDecimal valorContrapartidaConvenioContrato;
    private BigDecimal valorTotalConvenioContrato;
    private BigDecimal valorAditivoContrato;
    private BigDecimal valorRepassePagoContrato;
    private BigDecimal valorContrapartidaPagoContrato;
    private BigDecimal valorTotalPagoContrato;
    private BigDecimal percentualPagoContrato;
    private BigDecimal valorMedicaoContrato;
    private BigDecimal percentualObraContrato;


    public ExecucaoObraPorConvenio() {
    }

    public ExecucaoObraPorConvenio(Object[] linha) {
        this.idConvenio = ((BigDecimal) linha[0]).longValue();
        this.numeroConvenio = (String) linha[1];
        this.exercicioConvenio = linha[2] != null ? ((BigDecimal) linha[2]).intValue() : null;
        this.objetoConvenio = (String) linha[3];
        this.valorRepasseConvenio = (BigDecimal) linha[4];
        this.valorContrapartidaConvenio = (BigDecimal) linha[5];
        this.valorTotalConvenio = (BigDecimal) linha[6];
        this.idContrato = ((BigDecimal) linha[7]).longValue();
        this.exercicioContrato = ((BigDecimal) linha[8]).intValue();
        this.numeroContrato = ((BigDecimal) linha[9]).intValue();
        this.objetoContrato = (String) linha[10];
        this.contratadoContrato = (String) linha[11];
        this.valorTotalContrato = (BigDecimal) linha[12];
    }

    public Long getIdConvenio() {
        return idConvenio;
    }

    public void setIdConvenio(Long idConvenio) {
        this.idConvenio = idConvenio;
    }

    public String getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(String numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public Integer getExercicioConvenio() {
        return exercicioConvenio;
    }

    public void setExercicioConvenio(Integer exercicioConvenio) {
        this.exercicioConvenio = exercicioConvenio;
    }

    public String getObjetoConvenio() {
        return objetoConvenio;
    }

    public void setObjetoConvenio(String objetoConvenio) {
        this.objetoConvenio = objetoConvenio;
    }

    public BigDecimal getValorRepasseConvenio() {
        return valorRepasseConvenio;
    }

    public void setValorRepasseConvenio(BigDecimal valorRepasseConvenio) {
        this.valorRepasseConvenio = valorRepasseConvenio;
    }

    public BigDecimal getValorContrapartidaConvenio() {
        return valorContrapartidaConvenio;
    }

    public void setValorContrapartidaConvenio(BigDecimal valorContrapartidaConvenio) {
        this.valorContrapartidaConvenio = valorContrapartidaConvenio;
    }

    public BigDecimal getValorTotalConvenio() {
        return valorTotalConvenio;
    }

    public void setValorTotalConvenio(BigDecimal valorTotalConvenio) {
        this.valorTotalConvenio = valorTotalConvenio;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }

    public Integer getExercicioContrato() {
        return exercicioContrato;
    }

    public void setExercicioContrato(Integer exercicioContrato) {
        this.exercicioContrato = exercicioContrato;
    }

    public Integer getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(Integer numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getObjetoContrato() {
        return objetoContrato;
    }

    public void setObjetoContrato(String objetoContrato) {
        this.objetoContrato = objetoContrato;
    }

    public String getContratadoContrato() {
        return contratadoContrato;
    }

    public void setContratadoContrato(String contratadoContrato) {
        this.contratadoContrato = contratadoContrato;
    }

    public BigDecimal getValorTotalContrato() {
        return valorTotalContrato;
    }

    public void setValorTotalContrato(BigDecimal valorTotalContrato) {
        this.valorTotalContrato = valorTotalContrato;
    }

    public BigDecimal getValorRepasseConvenioContrato() {
        return valorRepasseConvenioContrato;
    }

    public void setValorRepasseConvenioContrato(BigDecimal valorRepasseConvenioContrato) {
        this.valorRepasseConvenioContrato = valorRepasseConvenioContrato;
    }

    public BigDecimal getValorContrapartidaConvenioContrato() {
        return valorContrapartidaConvenioContrato;
    }

    public void setValorContrapartidaConvenioContrato(BigDecimal valorContrapartidaConvenioContrato) {
        this.valorContrapartidaConvenioContrato = valorContrapartidaConvenioContrato;
    }

    public void setValorTotalConvenioContrato(BigDecimal valorTotalConvenioContrato) {
        this.valorTotalConvenioContrato = valorTotalConvenioContrato;
    }

    public BigDecimal getValorAditivoContrato() {
        return valorAditivoContrato;
    }

    public void setValorAditivoContrato(BigDecimal valorAditivoContrato) {
        this.valorAditivoContrato = valorAditivoContrato;
    }

    public BigDecimal getValorTotalConvenioContrato() {
        valorTotalConvenioContrato = BigDecimal.ZERO;
        if (valorRepasseConvenioContrato != null) {
            valorTotalConvenioContrato = valorTotalConvenioContrato.add(valorRepasseConvenioContrato);
        }
        if (valorContrapartidaConvenioContrato != null) {
            valorTotalConvenioContrato = valorTotalConvenioContrato.add(valorContrapartidaConvenioContrato);
        }
        return valorTotalConvenioContrato;
    }

    public BigDecimal getValorRepassePagoContrato() {
        return valorRepassePagoContrato;
    }

    public void setValorRepassePagoContrato(BigDecimal valorRepassePagoContrato) {
        this.valorRepassePagoContrato = valorRepassePagoContrato;
    }

    public BigDecimal getValorContrapartidaPagoContrato() {
        return valorContrapartidaPagoContrato;
    }

    public void setValorContrapartidaPagoContrato(BigDecimal valorContrapartidaPagoContrato) {
        this.valorContrapartidaPagoContrato = valorContrapartidaPagoContrato;
    }

    public BigDecimal getValorTotalPagoContrato() {
        valorTotalPagoContrato = BigDecimal.ZERO;
        if (valorRepassePagoContrato != null) {
            valorTotalPagoContrato = valorTotalPagoContrato.add(valorRepassePagoContrato);
        }
        if (valorContrapartidaPagoContrato != null) {
            valorTotalPagoContrato = valorTotalPagoContrato.add(valorContrapartidaPagoContrato);
        }
        return valorTotalPagoContrato;
    }

    public void setValorTotalPagoContrato(BigDecimal valorTotalPagoContrato) {
        this.valorTotalPagoContrato = valorTotalPagoContrato;
    }

    public BigDecimal getValorMedicaoContrato() {
        return valorMedicaoContrato;
    }

    public void setValorMedicaoContrato(BigDecimal valorMedicaoContrato) {
        this.valorMedicaoContrato = valorMedicaoContrato;
    }

    public BigDecimal getPercentualPagoContrato() {
        percentualPagoContrato = getValorTotalPagoContrato().divide(getValorTotalContrato(), 4, BigDecimal.ROUND_UP);
        percentualPagoContrato = percentualPagoContrato.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_UP);
        return percentualPagoContrato;
    }

    public void setPercentualPagoContrato(BigDecimal percentualPagoContrato) {
        this.percentualPagoContrato = percentualPagoContrato;
    }

    public BigDecimal getPercentualObraContrato() {
        percentualObraContrato = getValorMedicaoContrato().divide(getValorTotalContrato(), 4, BigDecimal.ROUND_UP);
        percentualObraContrato = percentualObraContrato.multiply(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_UP);
        return percentualObraContrato;
    }

    public void setPercentualObraContrato(BigDecimal percentualObraContrato) {
        this.percentualObraContrato = percentualObraContrato;
    }
}
