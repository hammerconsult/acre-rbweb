package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class VOSimulacaoTransferenciaMovimentacoesBCI implements Serializable {

    private String inscricaoOrigem;
    private String inscricaoDestino;
    private List<VOCalculosSimulacaoTransferenciaBCI> calculos;
    private List<VORevisaoCalculoSimulacaoTransfMovBCI> revisoesDeCalculoIPTU;
    private List<VOCdaSimulacaoTransferenciaBCI> certidoesDividaAtiva;
    private List<VOSolicitacaoDoctoOficial> solicitacoesDoctoOficial;
    private List<VOIsencaoBci> isencoesBci;
    private List<VOProcessoDebito> processosDebito;
    private List<VODamTransferenciaBCI> dans;

    public VOSimulacaoTransferenciaMovimentacoesBCI() {
        calculos = Lists.newArrayList();

        certidoesDividaAtiva = Lists.newArrayList();
        revisoesDeCalculoIPTU = Lists.newArrayList();
        isencoesBci = Lists.newArrayList();
        solicitacoesDoctoOficial = Lists.newArrayList();
        processosDebito = Lists.newArrayList();
        dans = Lists.newArrayList();
    }

    public String getInscricaoOrigem() {
        return inscricaoOrigem;
    }

    public void setInscricaoOrigem(String inscricaoOrigem) {
        this.inscricaoOrigem = inscricaoOrigem;
    }

    public String getInscricaoDestino() {
        return inscricaoDestino;
    }

    public void setInscricaoDestino(String inscricaoDestino) {
        this.inscricaoDestino = inscricaoDestino;
    }

    public List<VOCalculosSimulacaoTransferenciaBCI> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<VOCalculosSimulacaoTransferenciaBCI> calculos) {
        this.calculos = calculos;
    }

    public List<VORevisaoCalculoSimulacaoTransfMovBCI> getRevisoesDeCalculoIPTU() {
        return revisoesDeCalculoIPTU;
    }

    public void setRevisoesDeCalculoIPTU(List<VORevisaoCalculoSimulacaoTransfMovBCI> revisoesDeCalculoIPTU) {
        this.revisoesDeCalculoIPTU = revisoesDeCalculoIPTU;
    }

    public List<VOCdaSimulacaoTransferenciaBCI> getCertidoesDividaAtiva() {
        return certidoesDividaAtiva;
    }

    public void setCertidoesDividaAtiva(List<VOCdaSimulacaoTransferenciaBCI> certidoesDividaAtiva) {
        this.certidoesDividaAtiva = certidoesDividaAtiva;
    }

    public List<VOSolicitacaoDoctoOficial> getSolicitacoesDoctoOficial() {
        return solicitacoesDoctoOficial;
    }

    public void setSolicitacoesDoctoOficial(List<VOSolicitacaoDoctoOficial> solicitacoesDoctoOficial) {
        this.solicitacoesDoctoOficial = solicitacoesDoctoOficial;
    }

    public List<VOIsencaoBci> getIsencoesBci() {
        return isencoesBci;
    }

    public void setIsencoesBci(List<VOIsencaoBci> isencoesBci) {
        this.isencoesBci = isencoesBci;
    }

    public List<VOProcessoDebito> getProcessosDebito() {
        return processosDebito;
    }

    public void setProcessosDebito(List<VOProcessoDebito> processosDebito) {
        this.processosDebito = processosDebito;
    }

    public List<VODamTransferenciaBCI> getDans() {
        return dans;
    }

    public void setDans(List<VODamTransferenciaBCI> dans) {
        this.dans = dans;
    }
}
