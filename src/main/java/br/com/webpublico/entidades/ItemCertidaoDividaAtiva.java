package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Divida Ativa")
@Etiqueta("")
public class ItemCertidaoDividaAtiva implements Serializable, Comparable<ItemCertidaoDividaAtiva> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Certidão")
    private CertidaoDividaAtiva certidao;
    @ManyToOne
    @Tabelavel
    private ItemInscricaoDividaAtiva itemInscricaoDividaAtiva;
    @Invisivel
    @Transient
    private Long criadoEm;

    public ItemCertidaoDividaAtiva() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CertidaoDividaAtiva getCertidao() {
        return certidao;
    }

    public void setCertidao(CertidaoDividaAtiva certidao) {
        this.certidao = certidao;
    }

    public ItemInscricaoDividaAtiva getItemInscricaoDividaAtiva() {
        return itemInscricaoDividaAtiva;
    }

    public void setItemInscricaoDividaAtiva(ItemInscricaoDividaAtiva itemInscricaoDividaAtiva) {
        this.itemInscricaoDividaAtiva = itemInscricaoDividaAtiva;
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
    public int compareTo(ItemCertidaoDividaAtiva o) {
        int retorno = this.itemInscricaoDividaAtiva.getInscricaoDividaAtiva().getExercicio().getAno();
        return 0;
    }

    @Override
    public String toString() {
        return new StringBuilder(certidao.getNumero()+"").append("/").append(certidao.getExercicio().getAno()).toString();
    }
}
