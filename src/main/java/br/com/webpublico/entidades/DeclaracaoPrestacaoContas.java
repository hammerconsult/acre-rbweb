/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.TipoDeclaracaoPrestacaoContas;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Declarações/Prestações de Contas")
public class DeclaracaoPrestacaoContas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Código")
    @Pesquisavel
    private Integer codigo;
    //    @Tabelavel
////    @Obrigatorio
//    @Etiqueta("Descrição")
//    @Pesquisavel
//    private String descricao;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Declaração/Prestação de Conta")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoDeclaracaoPrestacaoContas tipoDeclaracaoPrestacaoContas;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Categoria Declaração")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Column(name = "CATEGORIADECLARACAO")
    private CategoriaDeclaracaoPrestacaoContas categoriaDeclaracaoPrestacaoContas;
    @ManyToOne
    private UnidadeGestora unidadeGestora;
    @OneToMany(mappedBy = "declaracaoPrestacaoContas")
    @Etiqueta("Configuração de Entidades para Declarações e Prestações de Contas")
    @Invisivel
    private List<EntidadeDPContas> entidadesDPContas;
    @Transient
    @Invisivel
    private Long criadoEm;

    public DeclaracaoPrestacaoContas() {
        this.criadoEm = System.nanoTime();
    }

    public List<EntidadeDPContas> getEntidadesDPContas() {
        return entidadesDPContas;
    }

    public void setEntidadesDPContas(List<EntidadeDPContas> entidadesDPContas) {
        this.entidadesDPContas = entidadesDPContas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

//    public String getDescricao() {
//        return descricao;
//    }
//
//    public void setDescricao(String descricao) {
//        this.descricao = descricao;
//    }

    public TipoDeclaracaoPrestacaoContas getTipoDeclaracaoPrestacaoContas() {
        return tipoDeclaracaoPrestacaoContas;
    }

    public void setTipoDeclaracaoPrestacaoContas(TipoDeclaracaoPrestacaoContas tipoDeclaracaoPrestacaoContas) {
        this.tipoDeclaracaoPrestacaoContas = tipoDeclaracaoPrestacaoContas;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public CategoriaDeclaracaoPrestacaoContas getCategoriaDeclaracaoPrestacaoContas() {
        return categoriaDeclaracaoPrestacaoContas;
    }

    public void setCategoriaDeclaracaoPrestacaoContas(CategoriaDeclaracaoPrestacaoContas categoriaDeclaracaoPrestacaoContas) {
        this.categoriaDeclaracaoPrestacaoContas = categoriaDeclaracaoPrestacaoContas;
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
        try {
            return codigo + " - " + categoriaDeclaracaoPrestacaoContas.getDescricao() + " (" + tipoDeclaracaoPrestacaoContas.getDescricao() + ")";
        } catch (NullPointerException npe) {
            return codigo+"";
        }
    }

}
