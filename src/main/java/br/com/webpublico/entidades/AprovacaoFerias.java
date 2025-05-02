/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author everton
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Aprovação de Programação de Férias")
public class AprovacaoFerias implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    @Tabelavel
    private String contrato;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Sugestão de Férias")
    private SugestaoFerias sugestaoFerias;
    @Etiqueta("Aprovado")
    @Tabelavel
    @Pesquisavel
    private Boolean aprovado;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Aprovação")
    private Date dataAprovacao;
    @Transient
    @Invisivel
    private Long criadoEm;

    public AprovacaoFerias() {
        criadoEm = System.nanoTime();
    }

    public AprovacaoFerias(SugestaoFerias sugestaoFerias, Boolean aprovado, Date dataAprovacao, String contrato) {
        criadoEm = System.nanoTime();
        this.sugestaoFerias = sugestaoFerias;
        this.aprovado = aprovado;
        this.dataAprovacao = dataAprovacao;
        this.contrato = contrato;
    }

    public Long getId() {
        return id;
    }

    public Boolean getAprovado() {
        if (dataAprovacao != null){
            aprovado = Boolean.TRUE;
        }
        return aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public SugestaoFerias getSugestaoFerias() {
        return sugestaoFerias;
    }

    public void setSugestaoFerias(SugestaoFerias sugestaoFerias) {
        this.sugestaoFerias = sugestaoFerias;
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

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
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
        if (dataAprovacao != null) {
            return Util.dateToString(dataAprovacao);
        } else {
            return "";
        }
    }

    public boolean isAprovada() {
        try {
            return getAprovado();
        } catch (NullPointerException npe) {
            return false;
        }
    }
}
