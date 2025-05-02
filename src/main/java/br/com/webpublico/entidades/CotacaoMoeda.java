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
 * @author Munif
 */
@CorEntidade("#ff0000")
@GrupoDiagrama(nome = "Financeiro")
@Entity

@Audited
@Etiqueta("Cotação de Moeda")
public class CotacaoMoeda implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Tabelavel
    @Obrigatorio
    private Integer ano;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Mês")
    private Integer Mes;
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private Moeda moeda;
    @Tabelavel
    @Obrigatorio
    @Monetario
    private BigDecimal valor;

    public CotacaoMoeda() {
    }

    public CotacaoMoeda(Integer ano, Integer Mes, Moeda moeda, BigDecimal valor) {
        this.ano = ano;
        this.Mes = Mes;
        this.moeda = moeda;
        this.valor = valor;
    }

    public Integer getMes() {
        return Mes;
    }

    public void setMes(Integer Mes) {
        this.Mes = Mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Moeda getMoeda() {
        return moeda;
    }

    public void setMoeda(Moeda moeda) {
        this.moeda = moeda;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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
        if (!(object instanceof CotacaoMoeda)) {
            return false;
        }
        CotacaoMoeda other = (CotacaoMoeda) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Ano: " + ano + " - Mês: " + Mes + " - Moeda: " + moeda + " - Valor: " + valor;
    }
}
