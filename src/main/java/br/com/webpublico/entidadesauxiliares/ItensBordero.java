/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edi
 */
public class ItensBordero {

    private ArrayList<BorderoPagamento> listaPagamentos;
    private ArrayList<BorderoPagamentoExtra> listaPagamentoExtra;
    private ArrayList<BorderoLiberacaoFinanceira> listaLiberacoes;
    private ArrayList<BorderoTransferenciaFinanceira> listaTransferencias;
    private ArrayList<BorderoTransferenciaMesmaUnidade> listaTransferenciaMesmaUnidades;
    private BorderoPagamentoExtra[] arrayPgtoExtra;
    private BorderoPagamento[] arrayPgto;
    private BorderoTransferenciaFinanceira[] arrayTransferenciaFinanceira;
    private BorderoTransferenciaMesmaUnidade[] arrayTransferenciaMesmaUnidade;
    private BorderoLiberacaoFinanceira[] arrayLiberacaoCotaFinanceira;

    //quando remover e tiver ID - para alterar a situação do item bordero
    private List<BorderoPagamento> listaPagamentosExclusao;
    private List<BorderoPagamentoExtra> listaPagamentoExtraExclusao;
    private List<BorderoTransferenciaFinanceira> listaTransferenciaFinanceiraExclusao;
    private List<BorderoTransferenciaMesmaUnidade> listaTransferenciaMesmaUnidadeExclusao;
    private List<BorderoLiberacaoFinanceira> listaLiberacaoFinanceiraExclusao;
    private Bordero bordero;

    public ItensBordero() {
        listaPagamentoExtraExclusao = new ArrayList<>();
        listaPagamentosExclusao = new ArrayList<>();
        listaTransferenciaFinanceiraExclusao = new ArrayList<>();
        listaTransferenciaMesmaUnidadeExclusao = new ArrayList<>();
        listaLiberacaoFinanceiraExclusao = new ArrayList<>();
    }

    public List<BorderoPagamento> getListaPagamentos() {
        return listaPagamentos;
    }

    public void setListaPagamentos(ArrayList<BorderoPagamento> listaPagamentos) {
        this.listaPagamentos = listaPagamentos;
    }

    public ArrayList<BorderoPagamentoExtra> getListaPagamentoExtra() {
        return listaPagamentoExtra;
    }

    public void setListaPagamentoExtra(ArrayList<BorderoPagamentoExtra> listaPagamentoExtra) {
        this.listaPagamentoExtra = listaPagamentoExtra;
    }

    public ArrayList<BorderoLiberacaoFinanceira> getListaLiberacoes() {
        return listaLiberacoes;
    }

    public void setListaLiberacoes(ArrayList<BorderoLiberacaoFinanceira> listaLiberacoes) {
        this.listaLiberacoes = listaLiberacoes;
    }

    public ArrayList<BorderoTransferenciaFinanceira> getListaTransferencias() {
        return listaTransferencias;
    }

    public void setListaTransferencias(ArrayList<BorderoTransferenciaFinanceira> listaTransferencias) {
        this.listaTransferencias = listaTransferencias;
    }

    public ArrayList<BorderoTransferenciaMesmaUnidade> getListaTransferenciaMesmaUnidades() {
        return listaTransferenciaMesmaUnidades;
    }

    public void setListaTransferenciaMesmaUnidades(ArrayList<BorderoTransferenciaMesmaUnidade> listaTransferenciaMesmaUnidades) {
        this.listaTransferenciaMesmaUnidades = listaTransferenciaMesmaUnidades;
    }

    public BorderoPagamentoExtra[] getArrayPgtoExtra() {
        return arrayPgtoExtra;
    }

    public void setArrayPgtoExtra(BorderoPagamentoExtra[] arrayPgtoExtra) {
        this.arrayPgtoExtra = arrayPgtoExtra;
    }

    public BorderoPagamento[] getArrayPgto() {
        return arrayPgto;
    }

    public void setArrayPgto(BorderoPagamento[] arrayPgto) {
        this.arrayPgto = arrayPgto;
    }

    public BorderoTransferenciaFinanceira[] getArrayTransferenciaFinanceira() {
        return arrayTransferenciaFinanceira;
    }

    public void setArrayTransferenciaFinanceira(BorderoTransferenciaFinanceira[] arrayTransferenciaFinanceira) {
        this.arrayTransferenciaFinanceira = arrayTransferenciaFinanceira;
    }

    public BorderoTransferenciaMesmaUnidade[] getArrayTransferenciaMesmaUnidade() {
        return arrayTransferenciaMesmaUnidade;
    }

    public void setArrayTransferenciaMesmaUnidade(BorderoTransferenciaMesmaUnidade[] arrayTransferenciaMesmaUnidade) {
        this.arrayTransferenciaMesmaUnidade = arrayTransferenciaMesmaUnidade;
    }

    public BorderoLiberacaoFinanceira[] getArrayLiberacaoCotaFinanceira() {
        return arrayLiberacaoCotaFinanceira;
    }

    public void setArrayLiberacaoCotaFinanceira(BorderoLiberacaoFinanceira[] arrayLiberacaoCotaFinanceira) {
        this.arrayLiberacaoCotaFinanceira = arrayLiberacaoCotaFinanceira;
    }

    public Bordero getBordero() {
        return bordero;
    }

    public void setBordero(Bordero bordero) {
        this.bordero = bordero;
    }

    public List<BorderoPagamento> getListaPagamentosExclusao() {
        return listaPagamentosExclusao;
    }

    public void setListaPagamentosExclusao(List<BorderoPagamento> listaPagamentosExclusao) {
        this.listaPagamentosExclusao = listaPagamentosExclusao;
    }

    public List<BorderoPagamentoExtra> getListaPagamentoExtraExclusao() {
        return listaPagamentoExtraExclusao;
    }

    public void setListaPagamentoExtraExclusao(List<BorderoPagamentoExtra> listaPagamentoExtraExclusao) {
        this.listaPagamentoExtraExclusao = listaPagamentoExtraExclusao;
    }

    public List<BorderoTransferenciaFinanceira> getListaTransferenciaFinanceiraExclusao() {
        return listaTransferenciaFinanceiraExclusao;
    }

    public void setListaTransferenciaFinanceiraExclusao(List<BorderoTransferenciaFinanceira> listaTransferenciaFinanceiraExclusao) {
        this.listaTransferenciaFinanceiraExclusao = listaTransferenciaFinanceiraExclusao;
    }

    public List<BorderoTransferenciaMesmaUnidade> getListaTransferenciaMesmaUnidadeExclusao() {
        return listaTransferenciaMesmaUnidadeExclusao;
    }

    public void setListaTransferenciaMesmaUnidadeExclusao(List<BorderoTransferenciaMesmaUnidade> listaTransferenciaMesmaUnidadeExclusao) {
        this.listaTransferenciaMesmaUnidadeExclusao = listaTransferenciaMesmaUnidadeExclusao;
    }

    public List<BorderoLiberacaoFinanceira> getListaLiberacaoFinanceiraExclusao() {
        return listaLiberacaoFinanceiraExclusao;
    }

    public void setListaLiberacaoFinanceiraExclusao(List<BorderoLiberacaoFinanceira> listaLiberacaoFinanceiraExclusao) {
        this.listaLiberacaoFinanceiraExclusao = listaLiberacaoFinanceiraExclusao;
    }
}
