/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

/**
 * @author wiplash
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Unidades de Medida do Convênio de Receita")
public class ConvenioReceitaUnidMedida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Convênio de Receita")
    @ManyToOne
    private ConvenioReceita convenioReceita;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;
    @Obrigatorio
    @Etiqueta("Unidade Medida")
    @ManyToOne
    @Tabelavel
    private UnidadeMedida unidadeMedida;
    @Obrigatorio
    @Etiqueta("Meta Física")
    @Tabelavel
    private BigDecimal metaFisica;
    @Etiqueta("Quantidade")
    private Integer qtdeMetaFisica;
    @Transient
    private Long criadoEm;

    public ConvenioReceitaUnidMedida() {
        criadoEm = System.nanoTime();
        metaFisica = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getMetaFisica() {
        return metaFisica;
    }

    public void setMetaFisica(BigDecimal metaFisica) {
        this.metaFisica = metaFisica;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQtdeMetaFisica() {
        return qtdeMetaFisica;
    }

    public void setQtdeMetaFisica(Integer qtdeMetaFisica) {
        this.qtdeMetaFisica = qtdeMetaFisica;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return unidadeMedida + " - " + descricao + " - " + metaFisica;
    }
}
