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
 * @author major
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Tramite Convênio de Despesa")

public class TramiteConvenioDesp implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Data")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    private Date dataTramite;
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;
    @Obrigatorio
    @Etiqueta("Convênio de Despesa")
    @ManyToOne
    private ConvenioDespesa convenioDespesa;
    @Obrigatorio
    @Etiqueta("Ocorrência")
    @ManyToOne
    @Tabelavel
    private OcorrenciaConvenioDesp ocorrenciaConvenioDesp;
    @Transient
    private Long criadoEm;

    public TramiteConvenioDesp() {
        criadoEm = System.nanoTime();
    }

    public TramiteConvenioDesp(Date dataTramite, String descricao, ConvenioDespesa convenioDespesa, OcorrenciaConvenioDesp ocorrenciaConvenioDesp) {
        this.dataTramite = dataTramite;
        this.descricao = descricao;
        this.convenioDespesa = convenioDespesa;
        this.ocorrenciaConvenioDesp = ocorrenciaConvenioDesp;
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

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

    public OcorrenciaConvenioDesp getOcorrenciaConvenioDesp() {
        return ocorrenciaConvenioDesp;
    }

    public void setOcorrenciaConvenioDesp(OcorrenciaConvenioDesp ocorrenciaConvenioDesp) {
        this.ocorrenciaConvenioDesp = ocorrenciaConvenioDesp;
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

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.TramiteDividaPublica[ id=" + id + " ]";
    }
}
