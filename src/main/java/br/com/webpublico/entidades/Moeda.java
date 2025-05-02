/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author terminal4
 */
@CorEntidade("#0000ff")
@GrupoDiagrama(nome = "Financeiro")
@Entity

@Audited
public class Moeda implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Column(length = 45)
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Índice Econômico")
    private IndiceEconomico indiceEconomico;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Integer ano;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Mês")
    @Obrigatorio
    private Integer mes;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Monetario
    private BigDecimal valor;

    public Moeda() {
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public IndiceEconomico getIndiceEconomico() {
        return indiceEconomico;
    }

    public void setIndiceEconomico(IndiceEconomico indiceEconomico) {
        this.indiceEconomico = indiceEconomico;
    }

    public Moeda(String descricao, IndiceEconomico indiceEconomico, Integer ano, Integer mes, BigDecimal valor) {
        this.descricao = descricao;
        this.indiceEconomico = indiceEconomico;
        this.ano = ano;
        this.mes = mes;
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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
        if (!(object instanceof Moeda)) {
            return false;
        }
        Moeda other = (Moeda) object;
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
