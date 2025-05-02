/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Ativa")
@Etiqueta("Item do Livro")
public class ItemLivroDividaAtiva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne(cascade = CascadeType.ALL)
    private LivroDividaAtiva livroDividaAtiva;
    @ManyToOne
    private InscricaoDividaAtiva inscricaoDividaAtiva;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemLivroDividaAtiva")
    private List<LinhaDoLivroDividaAtiva> linhasDoLivro;
    private Boolean processado;
    @Transient
    @Invisivel
    private Long criadoEm;
    @Transient
    private Long idInscricaoDividaAtiva;

    public ItemLivroDividaAtiva() {
        this.criadoEm = System.nanoTime();
        linhasDoLivro = new ArrayList<>();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InscricaoDividaAtiva getInscricaoDividaAtiva() {
        return inscricaoDividaAtiva;
    }

    public void setInscricaoDividaAtiva(InscricaoDividaAtiva inscricaoDividaAtiva) {
        this.inscricaoDividaAtiva = inscricaoDividaAtiva;
    }

    public List<LinhaDoLivroDividaAtiva> getLinhasDoLivro() {
        return linhasDoLivro;
    }

    public void setLinhasDoLivro(List<LinhaDoLivroDividaAtiva> linhasDoLivro) {
        this.linhasDoLivro = linhasDoLivro;
    }

    public LivroDividaAtiva getLivroDividaAtiva() {
        return livroDividaAtiva;
    }

    public void setLivroDividaAtiva(LivroDividaAtiva livroDividaAtiva) {
        this.livroDividaAtiva = livroDividaAtiva;
    }

    public Boolean isProcessado() {
        return processado == null ? false : processado;
    }

    public Boolean getProcessado() {
        return isProcessado();
    }

    public void setProcessado(Boolean processado) {
        this.processado = processado;
    }

    public Long getIdInscricaoDividaAtiva() {
        return idInscricaoDividaAtiva;
    }

    public void setIdInscricaoDividaAtiva(Long idInscricaoDividaAtiva) {
        this.idInscricaoDividaAtiva = idInscricaoDividaAtiva;
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
        if (inscricaoDividaAtiva != null) {
            return this.inscricaoDividaAtiva.getNumero() + " - " + this.inscricaoDividaAtiva.getExercicio().getAno();
        } else {
            return idInscricaoDividaAtiva.toString();
        }
    }
}
