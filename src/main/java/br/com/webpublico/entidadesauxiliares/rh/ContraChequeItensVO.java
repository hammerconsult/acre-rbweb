package br.com.webpublico.entidadesauxiliares.rh;

import java.math.BigDecimal;

/**
 * Created by tharlyson on 17/01/20.
 */
public class ContraChequeItensVO {

    private String codigo;
    private String descricao;
    private BigDecimal referencia;
    private BigDecimal valor;
    private String tipoEventoFP;
    private String competencia;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getReferencia() {
        return referencia;
    }

    public void setReferencia(BigDecimal referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getTipoEventoFP() {
        return tipoEventoFP;
    }

    public void setTipoEventoFP(String tipoEventoFP) {
        this.tipoEventoFP = tipoEventoFP;
    }

    public String getCompetencia() {
        return competencia;
    }

    public void setCompetencia(String competencia) {
        this.competencia = competencia;
    }
}
