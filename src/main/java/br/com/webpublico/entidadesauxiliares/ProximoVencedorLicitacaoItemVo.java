package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemPregao;
import br.com.webpublico.entidades.LancePregao;
import br.com.webpublico.entidades.LicitacaoFornecedor;
import br.com.webpublico.entidades.administrativo.licitacao.ProximoVencedorLicitacao;
import com.google.common.collect.ComparisonChain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ProximoVencedorLicitacaoItemVo implements Serializable, Comparable<ProximoVencedorLicitacaoItemVo> {

    private ItemPregao itemPregao;
    private ProximoVencedorLicitacao proximoVencedorLicitacao;
    private List<PropostaFornecedorProximoVencedorVo> proximosVencedores;
    private PropostaFornecedorProximoVencedorVo proximoVencedor;
    private BigDecimal valorLanceAtual;
    private BigDecimal valorProximoLance;
    private LancePregao lanceVencedor;
    private LicitacaoFornecedor licitacaoFornecedor;
    private Boolean loteDistribuido;

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public ProximoVencedorLicitacao getProximoVencedorLicitacao() {
        return proximoVencedorLicitacao;
    }

    public void setProximoVencedorLicitacao(ProximoVencedorLicitacao proximoVencedorLicitacao) {
        this.proximoVencedorLicitacao = proximoVencedorLicitacao;
    }

    public PropostaFornecedorProximoVencedorVo getProximoVencedor() {
        return proximoVencedor;
    }

    public void setProximoVencedor(PropostaFornecedorProximoVencedorVo proximoVencedor) {
        this.proximoVencedor = proximoVencedor;
    }

    public BigDecimal getValorLanceAtual() {
        return valorLanceAtual;
    }

    public void setValorLanceAtual(BigDecimal valorLanceAtual) {
        this.valorLanceAtual = valorLanceAtual;
    }

    public BigDecimal getValorProximoLance() {
        return valorProximoLance;
    }

    public void setValorProximoLance(BigDecimal valorProximoLance) {
        this.valorProximoLance = valorProximoLance;
    }

    public LancePregao getLanceVencedor() {
        return lanceVencedor;
    }

    public void setLanceVencedor(LancePregao lanceVencedor) {
        this.lanceVencedor = lanceVencedor;
    }

    public LicitacaoFornecedor getLicitacaoFornecedor() {
        return licitacaoFornecedor;
    }

    public void setLicitacaoFornecedor(LicitacaoFornecedor licitacaoFornecedor) {
        this.licitacaoFornecedor = licitacaoFornecedor;
    }

    public List<PropostaFornecedorProximoVencedorVo> getProximosVencedores() {
        return proximosVencedores;
    }

    public void setProximosVencedores(List<PropostaFornecedorProximoVencedorVo> proximosVencedores) {
        this.proximosVencedores = proximosVencedores;
    }

    public boolean hasItemSelecionado() {
        return proximoVencedor != null && proximoVencedor.getPropostaFornecedor() != null;
    }

    public boolean hasValorProximoLance() {
        return valorProximoLance != null && valorLanceAtual.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasPropostaNoLance() {
        return proximosVencedores != null && !proximosVencedores.isEmpty();
    }

    public Boolean getLoteDistribuido() {
        return loteDistribuido;
    }

    public void setLoteDistribuido(Boolean loteDistribuido) {
        this.loteDistribuido = loteDistribuido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProximoVencedorLicitacaoItemVo that = (ProximoVencedorLicitacaoItemVo) o;
        return Objects.equals(itemPregao, that.itemPregao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemPregao);
    }

    @Override
    public int compareTo(ProximoVencedorLicitacaoItemVo o) {
        int comparacao = 0;
        if (getProximoVencedorLicitacao().getLicitacao().isTipoApuracaoPrecoLote() && getItemPregao().getItemPregaoLoteProcesso() != null) {
            comparacao = getItemPregao().getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getNumero().compareTo(o.getItemPregao().getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getNumero());
        }
        if (comparacao == 0) {
            if (getProximoVencedorLicitacao().getLicitacao().isTipoApuracaoPrecoItem() && getItemPregao().getItemPregaoItemProcesso() != null) {
                comparacao = ComparisonChain.start().compare(getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero(), o.getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero())
                    .compare(getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra().getNumero(), o.getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra().getNumero()).result();
            }
        }
        return comparacao;
    }
}
