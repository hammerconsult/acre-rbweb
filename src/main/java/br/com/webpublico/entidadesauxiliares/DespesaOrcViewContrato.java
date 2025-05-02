/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContaDeDestinacao;
import br.com.webpublico.entidades.FonteDeRecursos;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
public class DespesaOrcViewContrato {
    private Date dataVinculada;
    private VwHierarquiaDespesaORC vwHierarquiaDespesaORC;
    private ContaDeDestinacao contaDeDestinacao;
    private FonteDeRecursos fonteDeRecursos;
    private BigDecimal valorReservado;

    public DespesaOrcViewContrato(){
    }

    public DespesaOrcViewContrato(Date dataVinculada, VwHierarquiaDespesaORC vwHierarquiaDespesaORC) {
        this.dataVinculada = dataVinculada;
        this.vwHierarquiaDespesaORC = vwHierarquiaDespesaORC;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Date getDataVinculada() {
        return dataVinculada;
    }

    public void setDataVinculada(Date dataVinculada) {
        this.dataVinculada = dataVinculada;
    }

    public VwHierarquiaDespesaORC getVwHierarquiaDespesaORC() {
        return vwHierarquiaDespesaORC;
    }

    public void setVwHierarquiaDespesaORC(VwHierarquiaDespesaORC vwHierarquiaDespesaORC) {
        this.vwHierarquiaDespesaORC = vwHierarquiaDespesaORC;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }
}
