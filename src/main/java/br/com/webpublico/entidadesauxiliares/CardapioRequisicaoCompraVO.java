package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Cardapio;
import br.com.webpublico.entidades.ExecucaoContratoItem;
import br.com.webpublico.entidades.ItemContrato;
import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.Perecibilidade;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class CardapioRequisicaoCompraVO {

    private Cardapio cardapio;
    private Operacoes operacao;
    private LocalEstoque localEstoquePai;
    private List<RequisicaoCompraGuiaItemVO> itensRequisicao;
    private List<RequisicaoCompraGuiaVO> requisicoes;

    private Perecibilidade perecibilidade;
    private Boolean materialComSaldo;
    private Boolean guiaLocalEstoquePai;

    private Map<ItemContrato, List<ExecucaoContratoItem>> mapItemContratoItensExecucao;

    public CardapioRequisicaoCompraVO() {
        itensRequisicao = Lists.newArrayList();
        requisicoes = Lists.newArrayList();
        guiaLocalEstoquePai = Boolean.FALSE;
        materialComSaldo = Boolean.FALSE;
        mapItemContratoItensExecucao = Maps.newHashMap();
    }

    public Cardapio getCardapio() {
        return cardapio;
    }

    public void setCardapio(Cardapio cardapio) {
        this.cardapio = cardapio;
    }


    public LocalEstoque getLocalEstoquePai() {
        return localEstoquePai;
    }

    public void setLocalEstoquePai(LocalEstoque localEstoquePai) {
        this.localEstoquePai = localEstoquePai;
    }

    public List<RequisicaoCompraGuiaItemVO> getItensRequisicao() {
        return itensRequisicao;
    }

    public void setItensRequisicao(List<RequisicaoCompraGuiaItemVO> itensRequisicao) {
        this.itensRequisicao = itensRequisicao;
    }

    public List<RequisicaoCompraGuiaVO> getRequisicoes() {
        return requisicoes;
    }

    public void setRequisicoes(List<RequisicaoCompraGuiaVO> requisicoes) {
        this.requisicoes = requisicoes;
    }

    public Map<ItemContrato, List<ExecucaoContratoItem>> getMapItemContratoItensExecucao() {
        return mapItemContratoItensExecucao;
    }

    public void setMapItemContratoItensExecucao(Map<ItemContrato, List<ExecucaoContratoItem>> mapItemContratoItensExecucao) {
        this.mapItemContratoItensExecucao = mapItemContratoItensExecucao;
    }

    public Operacoes getOperacao() {
        return operacao;
    }

    public void setOperacao(Operacoes operacao) {
        this.operacao = operacao;
    }

    public Perecibilidade getPerecibilidade() {
        return perecibilidade;
    }

    public void setPerecibilidade(Perecibilidade perecibilidade) {
        this.perecibilidade = perecibilidade;
    }


    public Boolean getMaterialComSaldo() {
        return materialComSaldo;
    }

    public void setMaterialComSaldo(Boolean materialComSaldo) {
        this.materialComSaldo = materialComSaldo;
    }

    public Boolean getGuiaLocalEstoquePai() {
        return guiaLocalEstoquePai;
    }

    public void setGuiaLocalEstoquePai(Boolean guiaLocalEstoquePai) {
        this.guiaLocalEstoquePai = guiaLocalEstoquePai;
    }
}
