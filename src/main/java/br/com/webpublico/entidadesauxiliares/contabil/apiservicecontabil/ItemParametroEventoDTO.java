/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil;

import br.com.webpublico.enums.OperacaoClasseCredor;
import br.com.webpublico.enums.TagValor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


public class ItemParametroEventoDTO implements Serializable {


    private Long id;
    private BigDecimal valor;

    private List<ObjetoParametroDTO> objetoParametros;
    private TagValor tagValor;
    private OperacaoClasseCredor operacaoClasseCredor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ObjetoParametroDTO> getObjetoParametros() {
        return objetoParametros;
    }

    public void setObjetoParametros(List<ObjetoParametroDTO> objetoParametros) {
        this.objetoParametros = objetoParametros;
    }

    public TagValor getTagValor() {
        return tagValor;
    }

    public void setTagValor(TagValor tagValor) {
        this.tagValor = tagValor;
    }

    public OperacaoClasseCredor getOperacaoClasseCredor() {
        return operacaoClasseCredor;
    }

    public void setOperacaoClasseCredor(OperacaoClasseCredor operacaoClasseCredor) {
        this.operacaoClasseCredor = operacaoClasseCredor;
    }
}
