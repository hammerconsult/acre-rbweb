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
import java.util.Date;

/**
 * @author claudio
 */
@Entity
@GrupoDiagrama(nome = "Alvara")
@Audited

public class SituacaoEconomicoCNAE implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EconomicoCNAE economicoCNAE;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;
    @Enumerated(EnumType.STRING)
    private Situacao situacao;
    @ManyToOne
    private Vistoria vistoria;
    @Transient
    private Long criadoEm;

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

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public EconomicoCNAE getEconomicoCNAE() {
        return economicoCNAE;
    }

    public void setEconomicoCNAE(EconomicoCNAE economicoCNAE) {
        this.economicoCNAE = economicoCNAE;
    }

    public Situacao getSituacao() {
        return situacao;
    }

    public void setSituacao(Situacao situacao) {
        this.situacao = situacao;
    }

    public SituacaoEconomicoCNAE() {
        criadoEm = System.nanoTime();
    }

    public Vistoria getVistoria() {
        return vistoria;
    }

    public void setVistoria(Vistoria vistoria) {
        this.vistoria = vistoria;
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
        return "br.com.webpublico.entidades.SituacaoEconomicoCNAE[ id=" + id + " ]";
    }

    public enum Situacao {

        EMBARGADO("Embargado"),
        NAO_LICENCIADO("Não Licenciado"),
        LICENCIADO("Licenciado");
        private String descricao;

        private Situacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }
}
