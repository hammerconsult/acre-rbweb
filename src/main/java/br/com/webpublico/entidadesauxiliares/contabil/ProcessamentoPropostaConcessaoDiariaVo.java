package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.ViagemDiaria;

import java.math.BigDecimal;

public class ProcessamentoPropostaConcessaoDiariaVo {
    private BigDecimal valorDaDiaria;
    private BigDecimal totalValorMeia;
    private BigDecimal totalIntegral;
    private Integer diferenca;
    private Double quantidadeDiarias;
    private ViagemDiaria viagemDiaria;

    public ProcessamentoPropostaConcessaoDiariaVo() {
        valorDaDiaria = BigDecimal.ZERO;
        totalValorMeia = BigDecimal.ZERO;
        totalIntegral = BigDecimal.ZERO;
        diferenca = 0;
        quantidadeDiarias = 0D;
    }

    public BigDecimal getValorDaDiaria() {
        return valorDaDiaria;
    }

    public void setValorDaDiaria(BigDecimal valorDaDiaria) {
        this.valorDaDiaria = valorDaDiaria;
    }

    public BigDecimal getTotalValorMeia() {
        return totalValorMeia;
    }

    public void setTotalValorMeia(BigDecimal totalValorMeia) {
        this.totalValorMeia = totalValorMeia;
    }

    public BigDecimal getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(BigDecimal totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public Integer getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(Integer diferenca) {
        this.diferenca = diferenca;
    }

    public Double getQuantidadeDiarias() {
        return quantidadeDiarias;
    }

    public void setQuantidadeDiarias(Double quantidadeDiarias) {
        this.quantidadeDiarias = quantidadeDiarias;
    }

    public ViagemDiaria getViagemDiaria() {
        return viagemDiaria;
    }

    public void setViagemDiaria(ViagemDiaria viagemDiaria) {
        this.viagemDiaria = viagemDiaria;
    }
}
