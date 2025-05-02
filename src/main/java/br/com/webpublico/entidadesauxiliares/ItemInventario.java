/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author terminal3
 */
public class ItemInventario implements Serializable {

    private String codigo;
    private BigDecimal qtdeConstatada;
    private Boolean encontrado;

    public ItemInventario() {
        this.encontrado = false;
        qtdeConstatada = BigDecimal.ZERO;
    }

    public ItemInventario(String codigo, BigDecimal qtdeConstatada) {
        this.codigo = codigo;
        this.qtdeConstatada = qtdeConstatada;
    }

    public Boolean getEncontrado() {
        return encontrado;
    }

    public void setEncontrado(Boolean encontrado) {
        this.encontrado = encontrado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getQtdeConstatada() {
        return qtdeConstatada;
    }

    public void setQtdeConstatada(BigDecimal qtdeConstatada) {
        this.qtdeConstatada = qtdeConstatada;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemInventario other = (ItemInventario) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        return hash;
    }
}
