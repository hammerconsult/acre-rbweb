/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author gustavo
 */
public class ValorDividaRetornoBD {

    private BigDecimal id;
    private Date emissao;
    private BigDecimal valor;
    private BigDecimal divida_id;
    private BigDecimal entidade_id;
    private BigDecimal exercicio_id;
    private BigDecimal calculo_id;
    private Date dataQuitacao;
    private String codigoImovel;
    private String codigoLote;

    @Override
    public String toString() {
        return "id=" + id + ",\n "
                + "emissao=" + emissao + ",\n "
                + "valor=" + valor + ",\n "
                + "divida_id=" + divida_id + ", \n"
                + "entidade_id=" + entidade_id + ", \n"
                + "exercicio_id=" + exercicio_id + ", \n"
                + "calculo_id=" + calculo_id + ", \n"
                + "dataQuitacao=" + dataQuitacao;
    }

    public String getCodigoImovel() {
        return codigoImovel;
    }

    public void setCodigoImovel(String codigoImovel) {
        this.codigoImovel = codigoImovel;
    }

    public BigDecimal getCalculo_id() {
        return calculo_id;
    }

    public void setCalculo_id(BigDecimal calculo_id) {
        this.calculo_id = calculo_id;
    }

    public Date getDataQuitacao() {
        return dataQuitacao;
    }

    public void setDataQuitacao(Date dataQuitacao) {
        this.dataQuitacao = dataQuitacao;
    }

    public BigDecimal getDivida_id() {
        return divida_id;
    }

    public void setDivida_id(BigDecimal divida_id) {
        this.divida_id = divida_id;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public BigDecimal getEntidade_id() {
        return entidade_id;
    }

    public void setEntidade_id(BigDecimal entidade_id) {
        this.entidade_id = entidade_id;
    }

    public BigDecimal getExercicio_id() {
        return exercicio_id;
    }

    public void setExercicio_id(BigDecimal exercicio_id) {
        this.exercicio_id = exercicio_id;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public ValorDividaRetornoBD(BigDecimal id, Date emissao, BigDecimal valor, BigDecimal divida_id, BigDecimal entidade_id, BigDecimal exercicio_id, BigDecimal calculo_id, Date dataQuitacao, String codigoImovel, String codigoLote) {
        this.id = id;
        this.emissao = emissao;
        this.valor = valor;
        this.divida_id = divida_id;
        this.entidade_id = entidade_id;
        this.exercicio_id = exercicio_id;
        this.calculo_id = calculo_id;
        this.dataQuitacao = dataQuitacao;
        this.codigoImovel = codigoImovel;
        this.codigoLote = codigoLote;
    }
}
