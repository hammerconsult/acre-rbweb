package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class RBT12NfseDTO implements NfseDTO {

    private Long prestadorId;
    private Integer exercicio;
    private Integer mes;
    private Integer totalNotas;
    private BigDecimal totalServicos;
    private BigDecimal issqnProprio;
    private BigDecimal issqnRetido;
    private BigDecimal rbt12;

    public RBT12NfseDTO() {
        totalNotas = 0;
        totalServicos = BigDecimal.ZERO;
        issqnProprio = BigDecimal.ZERO;
        issqnRetido = BigDecimal.ZERO;
        rbt12 = BigDecimal.ZERO;
    }

    public Long getPrestadorId() {
        return prestadorId;
    }

    public void setPrestadorId(Long prestadorId) {
        this.prestadorId = prestadorId;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getTotalNotas() {
        return totalNotas;
    }

    public void setTotalNotas(Integer totalNotas) {
        this.totalNotas = totalNotas;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getIssqnProprio() {
        return issqnProprio;
    }

    public void setIssqnProprio(BigDecimal issqnProprio) {
        this.issqnProprio = issqnProprio;
    }

    public BigDecimal getIssqnRetido() {
        return issqnRetido;
    }

    public void setIssqnRetido(BigDecimal issqnRetido) {
        this.issqnRetido = issqnRetido;
    }

    public BigDecimal getRbt12() {
        return rbt12;
    }

    public void setRbt12(BigDecimal rbt12) {
        this.rbt12 = rbt12;
    }
}
