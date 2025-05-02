/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Gustavo
 */
@Entity

@Audited
public class ParametroFaixaTributo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParamParcelamentoFaixa faixa;
    @ManyToOne
    private ParamParcelamentoTributo tributo;
    @Transient
    private Long criadoEm;

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ParamParcelamentoFaixa getFaixa() {
        return faixa;
    }

    public void setFaixa(ParamParcelamentoFaixa faixa) {
        this.faixa = faixa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParamParcelamentoTributo getTributo() {
        return tributo;
    }

    public void setTributo(ParamParcelamentoTributo tributo) {
        this.tributo = tributo;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }


}
