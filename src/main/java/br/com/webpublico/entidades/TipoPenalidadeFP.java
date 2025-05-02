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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author peixe
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Tipo de Penalidade")
public class TipoPenalidadeFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Código")
    @Tabelavel
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Dias a Descontar")
    private Integer diasDescontar;

    private Boolean reiniciarContagem;


    public TipoPenalidadeFP() {
        diasDescontar = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getDiasDescontar() {
        return diasDescontar;
    }

    public void setDiasDescontar(Integer diasDescontar) {
        this.diasDescontar = diasDescontar;
    }

    public Boolean getReiniciarContagem() {
        return reiniciarContagem == null ? false : reiniciarContagem;
    }

    public void setReiniciarContagem(Boolean reiniciarContagem) {
        this.reiniciarContagem = reiniciarContagem;
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
        if (!(object instanceof TipoPenalidadeFP)) {
            return false;
        }
        TipoPenalidadeFP other = (TipoPenalidadeFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao +", Max. dias: "+diasDescontar;
    }
}
