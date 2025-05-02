/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
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
@Etiqueta("Trâmite Convênio de Receita")
public class TramiteConvenioRec implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Data do Trâmite")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    private Date dataTramite;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;
    @Invisivel
    @Etiqueta("Convênio de Receita")
    @ManyToOne
    @Tabelavel
    private ConvenioReceita convenioReceita;
    @Invisivel
    @Etiqueta("Ocorrência")
    @ManyToOne
    @Tabelavel
    private OcorrenciaConvenioDesp ocorrencia;
    @Invisivel
    @Transient
    private Long criadoEm;

    public TramiteConvenioRec() {
        criadoEm = System.nanoTime();
    }

    public TramiteConvenioRec(Date dataTramite, String descricao, ConvenioReceita convenioReceita, OcorrenciaConvenioDesp ocorrencia, Long criadoEm) {
        this.dataTramite = dataTramite;
        this.descricao = descricao;
        this.convenioReceita = convenioReceita;
        this.ocorrencia = ocorrencia;
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public OcorrenciaConvenioDesp getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(OcorrenciaConvenioDesp ocorrencia) {
        this.ocorrencia = ocorrencia;
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
        return descricao + " - " + ocorrencia + "(" + DataUtil.getDataFormatada(dataTramite) + ")";
    }
}
