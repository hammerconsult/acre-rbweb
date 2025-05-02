/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AverbacaoTempoServico;
import br.com.webpublico.entidades.ContratoFP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Peixe
 */
public class AverbacaoAgrupado implements Serializable {

    private ContratoFP contratoFP;
    private List<AverbacaoTempoServico> averbacoes;

    public AverbacaoAgrupado() {
        averbacoes = new ArrayList<>();
    }

    public AverbacaoAgrupado(ContratoFP contratoFP, List<AverbacaoTempoServico> averbacoes) {
        this.contratoFP = contratoFP;
        this.averbacoes = averbacoes;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public List<AverbacaoTempoServico> getAverbacoes() {
        return averbacoes;
    }

    public void setAverbacoes(List<AverbacaoTempoServico> averbacoes) {
        this.averbacoes = averbacoes;
    }

}
