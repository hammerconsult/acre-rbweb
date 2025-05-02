/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoNaturezaAtividadeFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class NaturezaAtividadeFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Tipo de Natureza Atividade")
    @Obrigatorio
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoNaturezaAtividadeFP tipoNaturezaAtividadeFP;
    @Etiqueta("Percentual")
    @Obrigatorio
    @Tabelavel
    private Double percentual;
    @Transient
    private Long criadoEm;

    public NaturezaAtividadeFP() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPercentual() {
        return percentual;
    }

    public void setPercentual(Double percentual) {
        this.percentual = percentual;
    }

    public TipoNaturezaAtividadeFP getTipoNaturezaAtividadeFP() {
        return tipoNaturezaAtividadeFP;
    }

    public void setTipoNaturezaAtividadeFP(TipoNaturezaAtividadeFP tipoNaturezaAtividadeFP) {
        this.tipoNaturezaAtividadeFP = tipoNaturezaAtividadeFP;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return tipoNaturezaAtividadeFP + " - " + percentual;
    }
}
