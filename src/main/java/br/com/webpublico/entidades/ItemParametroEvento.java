/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoClasseCredor;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author major
 */
@Entity

@Audited
public class ItemParametroEvento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valor;
    //    @OneToMany(mappedBy = "itemParametroEvento", cascade = CascadeType.ALL)
    @Transient
    private List<ObjetoParametro> objetoParametros;
    @ManyToOne
    private ParametroEvento parametroEvento;
    @Enumerated(EnumType.STRING)
    private TagValor tagValor;
    @Enumerated(EnumType.STRING)
    private OperacaoClasseCredor operacaoClasseCredor;
    @Transient
    private Long criadoEm;

    public ItemParametroEvento() {
        objetoParametros = new ArrayList<ObjetoParametro>();
    }

    public ItemParametroEvento(BigDecimal valor, List<ObjetoParametro> objetoParametros, ParametroEvento parametroEvento, TagValor tagValor, Long criadoEm) {
        this.valor = valor;
        this.objetoParametros = objetoParametros;
        this.parametroEvento = parametroEvento;
        this.tagValor = tagValor;
        this.criadoEm = criadoEm;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ObjetoParametro> getObjetoParametros() {
        return objetoParametros;
    }

    public List<ObjetoParametro> getObjetoParametrosCredito() {
        List<ObjetoParametro> retorno = new ArrayList<>();
        for (ObjetoParametro objetoParametro : getObjetoParametros()) {
            if (objetoParametro.getTipoObjetoParametro().equals(ObjetoParametro.TipoObjetoParametro.AMBOS) || objetoParametro.getTipoObjetoParametro().equals(ObjetoParametro.TipoObjetoParametro.CREDITO)) {
                retorno.add(objetoParametro);
            }
        }
        return retorno;
    }

    public List<ObjetoParametro> getObjetoParametrosDebito() {
        List<ObjetoParametro> retorno = new ArrayList<>();
        for (ObjetoParametro objetoParametro : getObjetoParametros()) {
            if (objetoParametro.getTipoObjetoParametro().equals(ObjetoParametro.TipoObjetoParametro.AMBOS) || objetoParametro.getTipoObjetoParametro().equals(ObjetoParametro.TipoObjetoParametro.DEBITO)) {
                retorno.add(objetoParametro);
            }
        }

        return retorno;
    }

    public void setObjetoParametros(List<ObjetoParametro> objetoParametros) {
        this.objetoParametros = objetoParametros;
    }

    public ParametroEvento getParametroEvento() {
        return parametroEvento;
    }

    public void setParametroEvento(ParametroEvento parametroEvento) {
        this.parametroEvento = parametroEvento;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ItemParametroEvento[ id=" + id + " ]";
    }
}
