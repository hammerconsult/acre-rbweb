/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoIngressoBaixa;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author Claudio
 */
@Entity
@Audited

@Etiqueta("Tipo de Baixa de Bens")
public class TipoBaixaBens extends SuperEntidade {

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
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo do Bem")
    private TipoBem tipoBem;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Baixa")
    private TipoIngressoBaixa tipoIngressoBaixa;


    public TipoBaixaBens() {
        super();
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

    @Override
    public String toString() {
        return TipoIngressoBaixa.DESINCORPORACAO.equals(tipoIngressoBaixa) ? this.descricao + " (Desincorporação)" : this.descricao;
    }

    public String toStringAutoComplete() {
        return toString();
    }

    public TipoIngressoBaixa getTipoIngressoBaixa() {
        return tipoIngressoBaixa;
    }

    public void setTipoIngressoBaixa(TipoIngressoBaixa tipoIngressoBaixa) {
        this.tipoIngressoBaixa = tipoIngressoBaixa;
    }
}
