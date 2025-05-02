/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

public class CampoCelulaDeInformacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private CelulaDeInformacao celulaDeInformacao;

    private String campo;
    private Integer posicao;
    private Integer tamanho;
    private String descricao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public CampoCelulaDeInformacao() {
    }

    public CampoCelulaDeInformacao(CelulaDeInformacao celulaDeInformacao, String campo, Integer posicao, Integer tamanho, String descricao, Date dataRegistro) {
        this.celulaDeInformacao = celulaDeInformacao;
        this.campo = campo;
        this.posicao = posicao;
        this.tamanho = tamanho;
        this.descricao = descricao;
        this.dataRegistro = dataRegistro;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public CelulaDeInformacao getCelulaDeInformacao() {
        return celulaDeInformacao;
    }

    public void setCelulaDeInformacao(CelulaDeInformacao celulaDeInformacao) {
        this.celulaDeInformacao = celulaDeInformacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }

    public Integer getTamanho() {
        return tamanho;
    }

    public void setTamanho(Integer tamanho) {
        this.tamanho = tamanho;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CampoCelulaDeInformacao other = (CampoCelulaDeInformacao) obj;
        if (this.dataRegistro != other.dataRegistro && (this.dataRegistro == null || !this.dataRegistro.equals(other.dataRegistro))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.dataRegistro != null ? this.dataRegistro.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return campo + " - " + descricao;
    }

}
