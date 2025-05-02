package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.collect.Lists;

import java.util.List;

public class VOParametrosRelatorioConferenciaSefip {

    private List<ParametrosRelatorioConferenciaSefip> itens;
    private List<ParametrosRelatorioConferenciaSefip> itensServidor;
    private List<ParametrosRelatorioConferenciaSefip> itensPrestador;

    private List<ParametrosRelatorioConferenciaSefip> itensServidorNormal;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorFerias;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorRecisao;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorComplementar;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorAdiantamentoNormal;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorSalario13;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorAdiantamentoSalario13;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorAdiantamentoFerias;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorRecisaoComplementar;
    private List<ParametrosRelatorioConferenciaSefip> itensServidorManual;

    public VOParametrosRelatorioConferenciaSefip() {
        itens = Lists.newArrayList();
        itensServidor = Lists.newArrayList();
        itensPrestador = Lists.newArrayList();
        itensServidorNormal = Lists.newArrayList();
        itensServidorFerias = Lists.newArrayList();
        itensServidorRecisao = Lists.newArrayList();
        itensServidorComplementar = Lists.newArrayList();
        itensServidorAdiantamentoNormal = Lists.newArrayList();
        itensServidorSalario13 = Lists.newArrayList();
        itensServidorAdiantamentoSalario13 = Lists.newArrayList();
        itensServidorAdiantamentoFerias = Lists.newArrayList();
        itensServidorRecisaoComplementar = Lists.newArrayList();
        itensServidorManual = Lists.newArrayList();
    }

    public List<ParametrosRelatorioConferenciaSefip> getItens() {
        return itens;
    }

    public void setItens(List<ParametrosRelatorioConferenciaSefip> itens) {
        this.itens = itens;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidor() {
        return itensServidor;
    }

    public void setItensServidor(List<ParametrosRelatorioConferenciaSefip> itensServidor) {
        this.itensServidor = itensServidor;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensPrestador() {
        return itensPrestador;
    }

    public void setItensPrestador(List<ParametrosRelatorioConferenciaSefip> itensPrestador) {
        this.itensPrestador = itensPrestador;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorNormal() {
        return itensServidorNormal;
    }

    public void setItensServidorNormal(List<ParametrosRelatorioConferenciaSefip> itensServidorNormal) {
        this.itensServidorNormal = itensServidorNormal;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorFerias() {
        return itensServidorFerias;
    }

    public void setItensServidorFerias(List<ParametrosRelatorioConferenciaSefip> itensServidorFerias) {
        this.itensServidorFerias = itensServidorFerias;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorRecisao() {
        return itensServidorRecisao;
    }

    public void setItensServidorRecisao(List<ParametrosRelatorioConferenciaSefip> itensServidorRecisao) {
        this.itensServidorRecisao = itensServidorRecisao;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorComplementar() {
        return itensServidorComplementar;
    }

    public void setItensServidorComplementar(List<ParametrosRelatorioConferenciaSefip> itensServidorComplementar) {
        this.itensServidorComplementar = itensServidorComplementar;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorAdiantamentoNormal() {
        return itensServidorAdiantamentoNormal;
    }

    public void setItensServidorAdiantamentoNormal(List<ParametrosRelatorioConferenciaSefip> itensServidorAdiantamentoNormal) {
        this.itensServidorAdiantamentoNormal = itensServidorAdiantamentoNormal;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorSalario13() {
        return itensServidorSalario13;
    }

    public void setItensServidorSalario13(List<ParametrosRelatorioConferenciaSefip> itensServidorSalario13) {
        this.itensServidorSalario13 = itensServidorSalario13;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorAdiantamentoSalario13() {
        return itensServidorAdiantamentoSalario13;
    }

    public void setItensServidorAdiantamentoSalario13(List<ParametrosRelatorioConferenciaSefip> itensServidorAdiantamentoSalario13) {
        this.itensServidorAdiantamentoSalario13 = itensServidorAdiantamentoSalario13;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorAdiantamentoFerias() {
        return itensServidorAdiantamentoFerias;
    }

    public void setItensServidorAdiantamentoFerias(List<ParametrosRelatorioConferenciaSefip> itensServidorAdiantamentoFerias) {
        this.itensServidorAdiantamentoFerias = itensServidorAdiantamentoFerias;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorRecisaoComplementar() {
        return itensServidorRecisaoComplementar;
    }

    public void setItensServidorRecisaoComplementar(List<ParametrosRelatorioConferenciaSefip> itensServidorRecisaoComplementar) {
        this.itensServidorRecisaoComplementar = itensServidorRecisaoComplementar;
    }

    public List<ParametrosRelatorioConferenciaSefip> getItensServidorManual() {
        return itensServidorManual;
    }

    public void setItensServidorManual(List<ParametrosRelatorioConferenciaSefip> itensServidorManual) {
        this.itensServidorManual = itensServidorManual;
    }

}
