/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CancelamentoParcelamento;
import br.com.webpublico.entidades.ParcelasCancelamentoParcelamento;
import br.com.webpublico.entidades.ProcessoParcelamento;

import java.util.List;

public class DemonstrativoCancelamentoParcelamento {
    private ProcessoParcelamento parcelamento;
    private CancelamentoParcelamento cancelamentoParcelamento;
    private List<ParcelasCancelamentoParcelamento> originais;
    private List<ParcelasCancelamentoParcelamento> pagas;
    private List<ParcelasCancelamentoParcelamento> pagasAtualizadas;
    private List<ParcelasCancelamentoParcelamento> originaisAtualizadas;
    private List<ParcelasCancelamentoParcelamento> abatidas;
    private List<ParcelasCancelamentoParcelamento> abertas;

    public ProcessoParcelamento getParcelamento() {
        return parcelamento;
    }

    public void setParcelamento(ProcessoParcelamento parcelamento) {
        this.parcelamento = parcelamento;
    }

    public CancelamentoParcelamento getCancelamentoParcelamento() {
        return cancelamentoParcelamento;
    }

    public void setCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        this.cancelamentoParcelamento = cancelamentoParcelamento;
    }

    public List<ParcelasCancelamentoParcelamento> getOriginais() {
        return originais;
    }

    public void setOriginais(List<ParcelasCancelamentoParcelamento> originais) {
        this.originais = originais;
    }

    public List<ParcelasCancelamentoParcelamento> getOriginaisAtualizadas() {
        return originaisAtualizadas;
    }

    public void setOriginaisAtualizadas(List<ParcelasCancelamentoParcelamento> originaisAtualizadas) {
        this.originaisAtualizadas = originaisAtualizadas;
    }

    public List<ParcelasCancelamentoParcelamento> getPagas() {
        return pagas;
    }

    public void setPagas(List<ParcelasCancelamentoParcelamento> pagas) {
        this.pagas = pagas;
    }

    public List<ParcelasCancelamentoParcelamento> getAbatidas() {
        return abatidas;
    }

    public void setAbatidas(List<ParcelasCancelamentoParcelamento> abatidas) {
        this.abatidas = abatidas;
    }

    public List<ParcelasCancelamentoParcelamento> getAbertas() {
        return abertas;
    }

    public void setAbertas(List<ParcelasCancelamentoParcelamento> abertas) {
        this.abertas = abertas;
    }

    public List<ParcelasCancelamentoParcelamento> getPagasAtualizadas() {
        return pagasAtualizadas;
    }

    public void setPagasAtualizadas(List<ParcelasCancelamentoParcelamento> pagasAtualizadas) {
        this.pagasAtualizadas = pagasAtualizadas;
    }
}
