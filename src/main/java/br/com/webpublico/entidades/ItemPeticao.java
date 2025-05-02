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
import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author gustavo
 */
@Entity

@Audited
@GrupoDiagrama(nome = "DividaAtiva")
public class ItemPeticao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CertidaoDividaAtiva certidaoDividaAtiva;
    @ManyToOne
    private Peticao peticao;
    private BigDecimal valorOriginal;
    private BigDecimal atualizado;
    @Transient
    private Long criadoEm;

    public ItemPeticao() {
        criadoEm = System.nanoTime();
    }

    public Peticao getPeticao() {
        return peticao;
    }

    public void setPeticao(Peticao peticao) {
        this.peticao = peticao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CertidaoDividaAtiva getCertidaoDividaAtiva() {
        return certidaoDividaAtiva;
    }

    public void setCertidaoDividaAtiva(CertidaoDividaAtiva certidaoDividaAtiva) {
        this.certidaoDividaAtiva = certidaoDividaAtiva;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public BigDecimal getAtualizado() {
        return atualizado;
    }

    public void setAtualizado(BigDecimal atualizado) {
        this.atualizado = atualizado;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
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
        return "br.com.webpublico.entidades.ItemPeticao[ id=" + id + " ]";
    }

}
