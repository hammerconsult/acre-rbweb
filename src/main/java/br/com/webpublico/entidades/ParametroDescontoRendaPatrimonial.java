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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andre
 */
@Entity
@Audited

@GrupoDiagrama(nome = "Tributario")
@Etiqueta(" Parâmetros de Descontos no Parcelamento de Dívida Ativa de Rendas Patrimoniais e CEASA")
@Table(name = "PARAMETRORENDAPATRIMONIAL")
public class ParametroDescontoRendaPatrimonial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Código")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private Long codigo;
    @Pesquisavel
    @Etiqueta("Descrição")
    @Tabelavel
    @Obrigatorio
    private String descricao;
    @OneToMany(mappedBy = "parametroDescontoRendaPatrimonial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemParametroDescontoRendaPatrimonial> itensParametroDescontoRendaPatrimonial;

    public ParametroDescontoRendaPatrimonial() {
        itensParametroDescontoRendaPatrimonial = new ArrayList<ItemParametroDescontoRendaPatrimonial>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<ItemParametroDescontoRendaPatrimonial> getItensParametroDescontoRendaPatrimonial() {
        return itensParametroDescontoRendaPatrimonial;
    }

    public void setItensParametroDescontoRendaPatrimonial(List<ItemParametroDescontoRendaPatrimonial> itensParametroDescontoRendaPatrimonial) {
        this.itensParametroDescontoRendaPatrimonial = itensParametroDescontoRendaPatrimonial;
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
        if (!(object instanceof ParametroDescontoRendaPatrimonial)) {
            return false;
        }
        ParametroDescontoRendaPatrimonial other = (ParametroDescontoRendaPatrimonial) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
