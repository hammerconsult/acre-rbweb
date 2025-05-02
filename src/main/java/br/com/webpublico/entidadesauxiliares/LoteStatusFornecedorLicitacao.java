package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.ComparisonChain;

import java.math.BigDecimal;
import java.util.List;

public class LoteStatusFornecedorLicitacao implements Comparable<LoteStatusFornecedorLicitacao> {

    private Integer numero;
    private String descricao;
    private Boolean selecionado;
    private SituacaoItemProcessoDeCompra situacao;
    private List<ItemStatusFornecedorLicitacaoVo> itens;

    public LoteStatusFornecedorLicitacao(Integer numero, String descricao, SituacaoItemProcessoDeCompra situacao) {
        this.numero = numero;
        this.descricao = descricao;
        this.selecionado = false;
        this.situacao = situacao;
        this.itens = Lists.newArrayList();
    }

    public SituacaoItemProcessoDeCompra getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoItemProcessoDeCompra situacao) {
        this.situacao = situacao;
    }

    public List<ItemStatusFornecedorLicitacaoVo> getItens() {
        return itens;
    }

    public void setItens(List<ItemStatusFornecedorLicitacaoVo> itens) {
        this.itens = itens;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemStatusFornecedorLicitacaoVo item : itens) {
            if (item.getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorValor()) {
                total = total.add(item.getValorUnitario());
            } else {
                total = total.add(item.getItemProcessoCompra().getQuantidade().multiply(item.getValorUnitario()));
            }
        }
        return total;
    }

    @Override
    public int compareTo(LoteStatusFornecedorLicitacao o) {
        try {
            return ComparisonChain.start().compare(getNumero(), o.getNumero()).result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
