/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.util.anotacoes.Tabelavel;

import java.io.Serializable;

/**
 * @author everton
 */
public class ItemConferenciaDeFaltas implements Serializable {

    @Tabelavel
    private ContratoFP contrato;
    @Tabelavel
    private Double totalDias;

    public ItemConferenciaDeFaltas(ContratoFP contrato, double totalDias) {
        this.contrato = contrato;
        this.totalDias = totalDias;
    }

    public ContratoFP getContrato() {
        return contrato;
    }

    public void setContrato(ContratoFP contrato) {
        this.contrato = contrato;
    }

    public double getTotalDias() {
        return totalDias;
    }

    public Integer getTotalDiasInteger() {
        return totalDias.intValue();
    }

    public void setTotalDias(double totalDias) {
        this.totalDias = totalDias;
    }
}
