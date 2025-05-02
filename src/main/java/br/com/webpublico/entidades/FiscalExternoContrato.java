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

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Contratos")
@Entity
@Audited
public class FiscalExternoContrato extends FiscalContrato {

    @ManyToOne
    @Etiqueta("Contrato Firmado")
    private Contrato contratoFiscal;

    public Contrato getContratoFiscal() {
        return contratoFiscal;
    }

    public void setContratoFiscal(Contrato contratoFiscal) {
        this.contratoFiscal = contratoFiscal;
    }

    @Override
    public String getResponsavel() {
        if (this.contratoFiscal != null) {
            return "" + this.contratoFiscal.getContratado();
        }
        return "";
    }
}
