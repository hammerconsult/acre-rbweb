package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class ServicoDeclaradoDTO {
    private Long idServico;
    private Long idItem;
    private BigDecimal aliquotaServico;
    private BigDecimal baseCalculo;
    private BigDecimal valorServico;
    private BigDecimal issCalculado;
    private Boolean issRetido;

    public ServicoDeclaradoDTO() {
    }

    public ServicoDeclaradoDTO(Object[] obj) {
        this.idItem = ((Number) obj[0]).longValue();
        this.idServico = ((Number) obj[1]).longValue();
        this.aliquotaServico = (BigDecimal) obj[2];
        this.baseCalculo = (BigDecimal) obj[3];
        this.valorServico = (BigDecimal) obj[4];
        this.issCalculado = (BigDecimal) obj[5];
        this.issRetido = obj[6] != null && ((BigDecimal) obj[6]).intValue() == 1;
    }

    public Long getIdServico() {
        return idServico;
    }

    public void setIdServico(Long idServico) {
        this.idServico = idServico;
    }

    public BigDecimal getAliquotaServico() {
        return aliquotaServico;
    }

    public void setAliquotaServico(BigDecimal aliquotaServico) {
        this.aliquotaServico = aliquotaServico;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public Boolean getIssRetido() {
        return issRetido;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
    }

    public BigDecimal getIssCalculado() {
        return issCalculado;
    }

    public void setIssCalculado(BigDecimal issCalculado) {
        this.issCalculado = issCalculado;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }
}
