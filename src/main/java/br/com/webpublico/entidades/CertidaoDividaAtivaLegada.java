package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 22/06/15
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class CertidaoDividaAtivaLegada extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CertidaoDividaAtiva certidaoDividaAtiva;
    private String numeroLegado;
    private Integer anoLegado;

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

    public String getNumeroLegado() {
        return numeroLegado;
    }

    public void setNumeroLegado(String numeroLegado) {
        this.numeroLegado = numeroLegado;
    }

    public Integer getAnoLegado() {
        return anoLegado;
    }

    public void setAnoLegado(Integer anoLegado) {
        this.anoLegado = anoLegado;
    }

    public String getNumeroLegadoFormatado() {
        return numeroLegado + "/" + anoLegado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CertidaoDividaAtivaLegada that = (CertidaoDividaAtivaLegada) o;

        if (numeroLegado != null ? !numeroLegado.equals(that.numeroLegado) : that.numeroLegado != null) return false;
        return !(anoLegado != null ? !anoLegado.equals(that.anoLegado) : that.anoLegado != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (numeroLegado != null ? numeroLegado.hashCode() : 0);
        result = 31 * result + (anoLegado != null ? anoLegado.hashCode() : 0);
        return result;
    }
}
