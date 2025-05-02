/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Documento Licitação")
public class DoctoLicitacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Licitação")
    @ManyToOne
    @Pesquisavel
    private Licitacao licitacao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Modelo Documento")
    @ManyToOne
    @Pesquisavel
    private ModeloDocto modeloDocto;
    @OneToMany(mappedBy = "doctoLicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado=false)
    private List<VersaoDoctoLicitacao> listaDeVersoes;
    @Transient
    @Invisivel
    private Long criadoEm;

    public DoctoLicitacao() {
        criadoEm = System.nanoTime();
        listaDeVersoes = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public ModeloDocto getModeloDocto() {
        return modeloDocto;
    }

    public void setModeloDocto(ModeloDocto modeloDocto) {
        this.modeloDocto = modeloDocto;
    }

    public List<VersaoDoctoLicitacao> getListaDeVersoes() {
        return listaDeVersoes;
    }

    public void setListaDeVersoes(List<VersaoDoctoLicitacao> listaDeVersoes) {
        this.listaDeVersoes = listaDeVersoes;
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
        return "Documento Licitação = " + modeloDocto;
    }
}
