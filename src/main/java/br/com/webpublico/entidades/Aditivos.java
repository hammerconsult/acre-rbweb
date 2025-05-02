/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Paschualleto
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Aditivos")
public class Aditivos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    private String numero;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Valor Aditivo")
    private BigDecimal valorAditivo;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Valor da Contrapartida")
    private BigDecimal valorContrapartida;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Vigência Inicial")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataIniVigencia;
    @Tabelavel
    @Etiqueta("Vigência Final")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFimVigencia;

    public Aditivos() {
        valorContrapartida = BigDecimal.ZERO;
        valorAditivo = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorAditivo() {
        return valorAditivo;
    }

    public void setValorAditivo(BigDecimal valorAditivo) {
        this.valorAditivo = valorAditivo;
    }

    public BigDecimal getValorContrapartida() {
        return valorContrapartida;
    }

    public void setValorContrapartida(BigDecimal valorContrapartida) {
        this.valorContrapartida = valorContrapartida;
    }

    public Date getDataIniVigencia() {
        return dataIniVigencia;
    }

    public void setDataIniVigencia(Date dataIniVigencia) {
        this.dataIniVigencia = dataIniVigencia;
    }

    public Date getDataFimVigencia() {
        return dataFimVigencia;
    }

    public void setDataFimVigencia(Date dataFimVigencia) {
        this.dataFimVigencia = dataFimVigencia;
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
        if (!(object instanceof Aditivos)) {
            return false;
        }
        Aditivos other = (Aditivos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return numero + " - " + descricao + " (valor: " + Util.formataValor(valorAditivo) + ")";
    }
}
