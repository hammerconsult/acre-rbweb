/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "Materiais")
@Audited
@Entity

/**
 *
 * Representa a cota mensal de determinado material que um centro de custo (Conta) pode utilizar.
 *
 * @author arthur
 */
@Etiqueta("Cota de Material por Centro de Custo")
public class CotaMaterialCentroDeCusto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    private UnidadeOrganizacional centroDeCusto;
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Material")
    private Material material;
    @Pesquisavel
    @Etiqueta("Mês")
    @Obrigatorio
    @Tabelavel
    private Integer mes;
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Ano")
    @Tabelavel
    private Integer ano;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;
    @Transient
    @Invisivel
    private Long criadoEm;

    public CotaMaterialCentroDeCusto() {
        this.criadoEm = System.nanoTime();
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public UnidadeOrganizacional getCentroDeCusto() {
        return centroDeCusto;
    }

    public void setCentroDeCusto(UnidadeOrganizacional centroDeCusto) {
        this.centroDeCusto = centroDeCusto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
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
        return material.toString();
    }
}
