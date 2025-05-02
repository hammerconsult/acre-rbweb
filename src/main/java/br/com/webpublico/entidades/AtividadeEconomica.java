/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import org.hibernate.envers.Audited;

/**
 *
 * @author terminal4
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity

@Audited
@Etiqueta("Atividade Econômica")
public class AtividadeEconomica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    @Etiqueta("Código")
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Column(length = 45)
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @Column(length = 255)
    @Size(max = 255)
    @Etiqueta("Descrição Detalhada")
    @Pesquisavel
    private String descricaoDetalhada;
    @Tabelavel
    @Numerico
    @Etiqueta("Alíquota para ISS Mensal")
    @Pesquisavel
    private BigDecimal aliquotaIssHomologado;
    @Tabelavel
    @Numerico
    @Etiqueta("Alíquota para ISS Fixo")
    @Pesquisavel
    private BigDecimal aliquotaIssFixo;
    @Tabelavel
    @Numerico
    @Etiqueta("Alíquota para Alvará")
    @Pesquisavel
    private BigDecimal aliquotaAlvara;

    public AtividadeEconomica() {
    }

    public AtividadeEconomica(String descricao, String descricaoDetalhada, BigDecimal aliquotaIssHomologado, BigDecimal aliquotaIssFixo, BigDecimal aliquotaAlvara) {
        this.descricao = descricao;
        this.descricaoDetalhada = descricaoDetalhada;
        this.aliquotaIssHomologado = aliquotaIssHomologado;
        this.aliquotaIssFixo = aliquotaIssFixo;
        this.aliquotaAlvara = aliquotaAlvara;
    }

    public BigDecimal getAliquotaAlvara() {
        return aliquotaAlvara;
    }

    public void setAliquotaAlvara(BigDecimal aliquotaAlvara) {
        this.aliquotaAlvara = aliquotaAlvara;
    }

    public BigDecimal getAliquotaIssFixo() {
        return aliquotaIssFixo;
    }

    public void setAliquotaIssFixo(BigDecimal aliquotaIssFixo) {
        this.aliquotaIssFixo = aliquotaIssFixo;
    }

    public BigDecimal getAliquotaIssHomologado() {
        return aliquotaIssHomologado;
    }

    public void setAliquotaIssHomologado(BigDecimal aliquotaIssHomologado) {
        this.aliquotaIssHomologado = aliquotaIssHomologado;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof AtividadeEconomica)) {
            return false;
        }
        AtividadeEconomica other = (AtividadeEconomica) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
