/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Munif
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Frota")
@Etiqueta("Peça Instalada")
public class PecaInstalada implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ManutencaoObjetoFrota manutencaoObjetoFrota;
    @Tabelavel
    @Etiqueta("Foi substituição?")
    @Obrigatorio
    private Boolean foiSubstituicao;
    @Tabelavel
    @Etiqueta("Quantidade")
    @Obrigatorio
    private Integer quantidade;
    @ManyToOne
    private PecaObjetoFrota pecaObjetoFrota;
    private String descricao;
    @Transient
    private String descricaoPeca;
    @Transient
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private Contrato contrato;
    private String observacao;

    public PecaInstalada() {
        dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ManutencaoObjetoFrota getManutencaoObjetoFrota() {
        return manutencaoObjetoFrota;
    }

    public void setManutencaoObjetoFrota(ManutencaoObjetoFrota manutencaoObjetoFrota) {
        this.manutencaoObjetoFrota = manutencaoObjetoFrota;
    }

    public Boolean getFoiSubstituicao() {
        return foiSubstituicao;
    }

    public void setFoiSubstituicao(Boolean foiSubstituicao) {
        this.foiSubstituicao = foiSubstituicao;
    }

    public PecaObjetoFrota getPecaObjetoFrota() {
        return pecaObjetoFrota;
    }

    public void setPecaObjetoFrota(PecaObjetoFrota pecaObjetoFrota) {
        this.pecaObjetoFrota = pecaObjetoFrota;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getDescricaoPeca() {
        if (pecaObjetoFrota != null) {
            descricaoPeca = pecaObjetoFrota.getDescricao();
        } else {
            descricaoPeca = descricao;
        }
        return descricaoPeca;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PecaInstalada)) {
            return false;
        }
        PecaInstalada other = (PecaInstalada) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)) || (this.dataRegistro == null && other.dataRegistro != null) || (this.dataRegistro != null && !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return pecaObjetoFrota.toString();
    }
}
