package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.interfaces.ItemMovimentoEstoque;

import java.math.BigDecimal;
import java.util.Date;

public class ItemConversaoUnidadeMedidaVO implements ItemMovimentoEstoque {

    private ItemConversaoUnidadeMedidaMaterial itemConversao;
    private TipoOperacao tipoOperacao;
    private LocalEstoqueOrcamentario localEstoqueOrcamentario;

    public ItemConversaoUnidadeMedidaVO(ItemConversaoUnidadeMedidaMaterial itemConversao, TipoOperacao tipoOperacao, LocalEstoqueOrcamentario localEstoqueOrcamentario) {
        this.itemConversao = itemConversao;
        this.tipoOperacao = tipoOperacao;
        this.localEstoqueOrcamentario = localEstoqueOrcamentario;
    }

    public boolean isSaida(){
        return TipoOperacao.DEBITO.equals(tipoOperacao);
    }

    @Override
    public Long getIdOrigem() {
        return itemConversao.getConversaoUnidadeMedida().getId();
    }

    @Override
    public Integer getNumeroItem() {
        return itemConversao.getNumeroItem();
    }

    @Override
    public Date getDataMovimento() {
        return itemConversao.getConversaoUnidadeMedida().getDataMovimento();
    }

    @Override
    public BigDecimal getValorTotal() {
        return isSaida() ? itemConversao.getValorTotalSaida() : itemConversao.getValorTotalEntrada();
    }

    @Override
    public BigDecimal getQuantidade() {
        return isSaida() ? itemConversao.getQuantidadeSaida() : itemConversao.getQuantidadeEntrada();
    }

    @Override
    public BigDecimal getValorUnitario() {
        return isSaida() ? itemConversao.getValorUnitarioSaida() : itemConversao.getValorUnitarioEntrada();
    }

    @Override
    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return localEstoqueOrcamentario;
    }

    @Override
    public LoteMaterial getLoteMaterial() {
        return null;
    }

    @Override
    public Material getMaterial() {
        return isSaida() ? itemConversao.getMaterialSaida() : itemConversao.getMaterialEntrada();
    }

    @Override
    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    @Override
    public TipoEstoque getTipoEstoque() {
        return TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO;
    }

    @Override
    public String getDescricaoDaOperacao() {
        String descricao = isSaida() ? "Saída" : "Entrada";
        return descricao + " por Conversão Unidade de Medida n° " + itemConversao.getConversaoUnidadeMedida().getNumero();
    }

    @Override
    public Boolean ehEstorno() {
        return false;
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return itemConversao.getConversaoUnidadeMedida().getUnidadeOrcamentaria();
    }

    @Override
    public TipoBaixaBens getTipoBaixaBens() {
        return null;
    }

    @Override
    public TipoIngresso getTipoIngressoBens() {
        return null;
    }

    public ItemConversaoUnidadeMedidaMaterial getItemConversao() {
        return itemConversao;
    }

    public void setItemConversao(ItemConversaoUnidadeMedidaMaterial itemConversao) {
        this.itemConversao = itemConversao;
    }
}
