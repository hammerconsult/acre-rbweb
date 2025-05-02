/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
public class TramiteDividaPublica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Data")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    private Date dataTramite;
    @Obrigatorio
    @Etiqueta("Dívida Pública")
    @ManyToOne
    private DividaPublica dividaPublica;
    @Obrigatorio
    @Etiqueta("Ocorrência")
    @ManyToOne
    @Tabelavel
    private OcorrenciaDividaPublica ocorrenciaDividaPublica;
    @Transient
    private Long criadoEm;

    public TramiteDividaPublica() {
        dataTramite = new Date();
        criadoEm = System.nanoTime();
    }

    public TramiteDividaPublica(Date dataTramite, DividaPublica dividaPublica, OcorrenciaDividaPublica ocorrenciaDividaPublica) {
        this.dataTramite = dataTramite;
        this.dividaPublica = dividaPublica;
        this.ocorrenciaDividaPublica = ocorrenciaDividaPublica;
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

    public Date getDataTramite() {
        return dataTramite;
    }

    public void setDataTramite(Date dataTramite) {
        this.dataTramite = dataTramite;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public OcorrenciaDividaPublica getOcorrenciaDividaPublica() {
        return ocorrenciaDividaPublica;
    }

    public void setOcorrenciaDividaPublica(OcorrenciaDividaPublica ocorrenciaDividaPublica) {
        this.ocorrenciaDividaPublica = ocorrenciaDividaPublica;
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
        return "br.com.webpublico.entidades.TramiteDividaPublica[ id=" + id + " ]";
    }
}
