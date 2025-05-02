/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author munif
 */
@GrupoDiagrama(nome = "Tributario")
@Entity

public class ItemRecebimentoDivida implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal valor;
    @ManyToOne
    private RecebimentoDivida recebimentoDivida;
    @ManyToOne
    private ParcelaValorDivida parcelaValorDivida;
    @ManyToOne
    private LancamentoContaBancaria lancamentoContaBancaria;

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
        if (!(object instanceof ItemRecebimentoDivida)) {
            return false;
        }
        ItemRecebimentoDivida other = (ItemRecebimentoDivida) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.novas.ItemRecebimentoDivida[ id=" + id + " ]";
    }
}
