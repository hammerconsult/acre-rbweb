/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;

/**
 * @author venon
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Contabil")
@Etiqueta(value = "Conta Auxiliar")
public class ContaAuxiliar extends Conta implements Serializable {

    private String temp;
    @ManyToOne
    private ContaContabil contaContabil;

    @ManyToOne
    private TipoContaAuxiliar tipoContaAuxiliar;

    public ContaAuxiliar() {
        super();
        super.setdType("ContaAuxiliar");
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public ContaContabil getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(ContaContabil contaContabil) {
        this.contaContabil = contaContabil;
    }

    public TipoContaAuxiliar getTipoContaAuxiliar() {
        return tipoContaAuxiliar;
    }

    public void setTipoContaAuxiliar(TipoContaAuxiliar tipoContaAuxiliar) {
        this.tipoContaAuxiliar = tipoContaAuxiliar;
    }
}
