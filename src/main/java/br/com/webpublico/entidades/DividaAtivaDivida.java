/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Ativa")
public class DividaAtivaDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private InscricaoDividaAtiva inscricaoDividaAtiva;
    @ManyToOne
    private Divida divida;
    @Transient
    private Long criadoEm;

    public DividaAtivaDivida() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public InscricaoDividaAtiva getInscricaoDividaAtiva() {
        return inscricaoDividaAtiva;
    }

    public void setInscricaoDividaAtiva(InscricaoDividaAtiva inscricaoDividaAtiva) {
        this.inscricaoDividaAtiva = inscricaoDividaAtiva;
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
        return "br.com.webpublico.entidades.DividaAtivaDivida[ id=" + id + " ]";
    }
}
