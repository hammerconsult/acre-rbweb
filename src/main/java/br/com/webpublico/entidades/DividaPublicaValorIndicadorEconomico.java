/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

/**
 *
 * @author wiplash
 */
@Entity

@Audited
@Etiqueta("Dívida Pública Valor Indicador Econômico")
@GrupoDiagrama(nome = "Contabil")
@Table(name = "DividaPublicaValorIndic")
public class DividaPublicaValorIndicadorEconomico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Dívida Pública")
    @ManyToOne
    private DividaPublica dividaPublica;
    @Obrigatorio
    @Etiqueta("Valor Indicador Economico")
    @ManyToOne
    @Tabelavel
    private ValorIndicadorEconomico valorIndicadorEconomico;
    @ManyToOne
    @Etiqueta("Moeda")
    private IndicadorEconomico moeda;
    @ManyToOne
    private IndicadorEconomico indexador;
    @Pesquisavel
    @Etiqueta("Valor")
    @Monetario
    private BigDecimal valorIndexador;
    @Transient
    private Long criadoEm;

    public DividaPublicaValorIndicadorEconomico() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public ValorIndicadorEconomico getValorIndicadorEconomico() {
        return valorIndicadorEconomico;
    }

    public void setValorIndicadorEconomico(ValorIndicadorEconomico valorIndicadorEconomico) {
        this.valorIndicadorEconomico = valorIndicadorEconomico;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public IndicadorEconomico getMoeda() {
        return moeda;
    }

    public void setMoeda(IndicadorEconomico moeda) {
        this.moeda = moeda;
    }

    public IndicadorEconomico getIndexador() {
        return indexador;
    }

    public void setIndexador(IndicadorEconomico indexador) {
        this.indexador = indexador;
    }

    public BigDecimal getValorIndexador() {
        return valorIndexador;
    }

    public void setValorIndexador(BigDecimal valorIndexador) {
        this.valorIndexador = valorIndexador;
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
        return valorIndicadorEconomico.toString();
    }


}
