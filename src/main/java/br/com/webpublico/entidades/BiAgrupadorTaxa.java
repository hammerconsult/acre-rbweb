/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "Tributário")
@Entity
@Audited
@Etiqueta("Agrupador de Taxas do BI")
public class BiAgrupadorTaxa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Pesquisavel
    @Etiqueta("Código")
    @Tabelavel
    @Obrigatorio
    private Long codigo;
    @Pesquisavel
    @Column(length = 70)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @OneToMany(mappedBy = "biAgrupadorTaxa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BiAgrupadorTaxaTributo> tributos;

    public BiAgrupadorTaxa() {
        this.tributos = Lists.newArrayList();
    }

    public BiAgrupadorTaxa(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public List<BiAgrupadorTaxaTributo> getTributos() {
        return tributos;
    }

    public void setTributos(List<BiAgrupadorTaxaTributo> tributos) {
        this.tributos = tributos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}
