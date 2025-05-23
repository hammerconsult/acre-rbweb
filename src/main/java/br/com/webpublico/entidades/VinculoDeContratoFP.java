/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author andre
 */
@Entity
@Audited

@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Vínculo de Contrato")
public class VinculoDeContratoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Long codigo;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private String descricao;
    @Etiqueta("Percentual de Aposentadoria")
    @Tabelavel
    private BigDecimal percentualAposentadoria;
    @Etiqueta("Categoria SEFIP")
    @Tabelavel
    @ManyToOne
    private CategoriaSEFIP categoriaSEFIP;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CategoriaSEFIP getCategoriaSEFIP() {
        return categoriaSEFIP;
    }

    public void setCategoriaSEFIP(CategoriaSEFIP categoriaSEFIP) {
        this.categoriaSEFIP = categoriaSEFIP;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPercentualAposentadoria() {
        return percentualAposentadoria;
    }

    public void setPercentualAposentadoria(BigDecimal percentualAposentadoria) {
        this.percentualAposentadoria = percentualAposentadoria;
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
        if (!(object instanceof VinculoDeContratoFP)) {
            return false;
        }
        VinculoDeContratoFP other = (VinculoDeContratoFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getCodigo() + " - " + getDescricao() + " - " + getPercentualAposentadoria() + "%";
    }
}
