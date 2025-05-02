package br.com.webpublico.entidadesauxiliares.rh.portal;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author peixe on 02/06/2017  16:49.
 */
public class FuncaoGratificadaPortal {
    private BigDecimal vinculoFPId;
    private Date inicioVigencia;
    private Date finalVigencia;
    private String descricao;
    private String categoria;
    private BigDecimal valor;

    public FuncaoGratificadaPortal() {
    }

    public BigDecimal getVinculoFPId() {
        return vinculoFPId;
    }

    public void setVinculoFPId(BigDecimal vinculoFPId) {
        this.vinculoFPId = vinculoFPId;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}
