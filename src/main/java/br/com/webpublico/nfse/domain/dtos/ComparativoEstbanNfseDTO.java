package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.CadastroEconomico;

import java.math.BigDecimal;
import java.util.Date;

public class ComparativoEstbanNfseDTO {

    private Date competencia;
    private CadastroEconomico cadastroEconomico;
    private BigDecimal valorEstban;
    private BigDecimal valorNfse;
    private BigDecimal diferenca;

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public BigDecimal getValorEstban() {
        return valorEstban != null ? valorEstban : BigDecimal.ZERO;
    }

    public void setValorEstban(BigDecimal valorEstban) {
        this.valorEstban = valorEstban;
    }

    public BigDecimal getValorNfse() {
        return valorNfse != null ? valorNfse : BigDecimal.ZERO;
    }

    public void setValorNfse(BigDecimal valorNfse) {
        this.valorNfse = valorNfse;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }
}
