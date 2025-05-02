package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class VOCalculosSimulacaoTransferenciaBCI implements Serializable {
    private Long id;
    private String divida;
    private String situacaoParcela;
    private String processoCalculo;
    private String tipoCalculo;
    private Integer exercicio;
    private BigDecimal valorOriginal;
    private String agrupador;

    public VOCalculosSimulacaoTransferenciaBCI() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDivida() {
        return divida;
    }

    public void setDivida(String divida) {
        this.divida = divida;
    }

    public String getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(String situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public String getProcessoCalculo() {
        return processoCalculo;
    }

    public void setProcessoCalculo(String processoCalculo) {
        this.processoCalculo = processoCalculo;
    }

    public String getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(String tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public String getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(String agrupador) {
        this.agrupador = agrupador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOCalculosSimulacaoTransferenciaBCI that = (VOCalculosSimulacaoTransferenciaBCI) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
