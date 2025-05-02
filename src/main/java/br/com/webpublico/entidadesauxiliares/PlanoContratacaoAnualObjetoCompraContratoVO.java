package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

public class PlanoContratacaoAnualObjetoCompraContratoVO {

    private Date dataAssinatura;
    private String descricaoContrato;
    private String unidadeAdm;
    private String especificacao;
    private String unidadeMedida;
    private BigDecimal valorUnitario;
    private Long idContrato;

    public PlanoContratacaoAnualObjetoCompraContratoVO() {
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public String getDescricaoContrato() {
        return descricaoContrato;
    }

    public void setDescricaoContrato(String descricaoContrato) {
        this.descricaoContrato = descricaoContrato;
    }

    public String getUnidadeAdm() {
        return unidadeAdm;
    }

    public void setUnidadeAdm(String unidadeAdm) {
        this.unidadeAdm = unidadeAdm;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Long getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(Long idContrato) {
        this.idContrato = idContrato;
    }
}
