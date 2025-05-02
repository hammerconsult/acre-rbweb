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

/**
 * @author Renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Ativa")
@Etiqueta("Linha do Livro")
public class LinhaDoLivroDividaAtiva implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long sequencia;
    private Long pagina;
    private Long numeroDaLinha;
    @ManyToOne
    private ItemInscricaoDividaAtiva itemInscricaoDividaAtiva;
    @ManyToOne
    private ItemLivroDividaAtiva itemLivroDividaAtiva;
    @ManyToOne(cascade = CascadeType.ALL)
    private TermoInscricaoDividaAtiva termoInscricaoDividaAtiva;
    @Transient
    @Invisivel
    private Long criadoEm;

    public LinhaDoLivroDividaAtiva() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TermoInscricaoDividaAtiva getTermoInscricaoDividaAtiva() {
        return termoInscricaoDividaAtiva;
    }

    public void setTermoInscricaoDividaAtiva(TermoInscricaoDividaAtiva termoInscricaoDividaAtiva) {
        this.termoInscricaoDividaAtiva = termoInscricaoDividaAtiva;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemInscricaoDividaAtiva getItemInscricaoDividaAtiva() {
        return itemInscricaoDividaAtiva;
    }

    public void setItemInscricaoDividaAtiva(ItemInscricaoDividaAtiva itemInscricaoDividaAtiva) {
        this.itemInscricaoDividaAtiva = itemInscricaoDividaAtiva;
    }

    public ItemLivroDividaAtiva getItemLivroDividaAtiva() {
        return itemLivroDividaAtiva;
    }

    public void setItemLivroDividaAtiva(ItemLivroDividaAtiva itemLivroDividaAtiva) {
        this.itemLivroDividaAtiva = itemLivroDividaAtiva;
    }

    public Long getNumeroDaLinha() {
        return numeroDaLinha;
    }

    public void setNumeroDaLinha(Long numeroDaLinha) {
        this.numeroDaLinha = numeroDaLinha;
    }

    public Long getPagina() {
        return pagina;
    }

    public void setPagina(Long pagina) {
        this.pagina = pagina;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
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
        return "Página: " + pagina + " Linha: " + numeroDaLinha;
    }
}
