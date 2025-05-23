/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Claudio
 */
@Entity
@Audited
@Etiqueta("Tipo de Ingresso de Bens")
public class TipoIngresso extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo do Bem")
    private TipoBem tipoBem;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Ingresso")
    private TipoIngressoBaixa tipoIngressoBaixa;

    public TipoIngresso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public TipoIngressoBaixa getTipoIngressoBaixa() {
        return tipoIngressoBaixa;
    }

    public void setTipoIngressoBaixa(TipoIngressoBaixa tipoIngressoBaixa) {
        this.tipoIngressoBaixa = tipoIngressoBaixa;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String toStringAutoComplete() {
        return toString();
    }
}
