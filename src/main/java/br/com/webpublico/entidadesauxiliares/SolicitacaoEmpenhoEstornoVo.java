package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Empenho;
import br.com.webpublico.entidades.SolicitacaoEmpenho;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SolicitacaoEmpenhoEstornoVo implements Serializable, Comparable<SolicitacaoEmpenhoEstornoVo> {

    private Empenho empenho;
    private SolicitacaoEmpenho solicitacaoEmpenho;
    private CategoriaOrcamentaria categoria;
    private BigDecimal saldo;
    private TipoEstornoExecucao tipoEstorno;
    private Boolean selecionado;
    private Boolean bloqueado;
    private List<SolicitacaoEmpenhoEstornoLoquidacaoVo> liquidacoes;
    private List<SolicitacaoEmpenhoEstornoItemVo> itens;

    public SolicitacaoEmpenhoEstornoVo(TipoEstornoExecucao tipoEstorno) {
        this.tipoEstorno = tipoEstorno;
        this.selecionado = false;
        this.bloqueado = false;
        this.itens = Lists.newArrayList();
        this.liquidacoes = Lists.newArrayList();
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public CategoriaOrcamentaria getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaOrcamentaria categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public TipoEstornoExecucao getTipoEstorno() {
        return tipoEstorno;
    }

    public void setTipoEstorno(TipoEstornoExecucao tipoEstorno) {
        this.tipoEstorno = tipoEstorno;
    }

    public List<SolicitacaoEmpenhoEstornoLoquidacaoVo> getLiquidacoes() {
        return liquidacoes;
    }

    public void setLiquidacoes(List<SolicitacaoEmpenhoEstornoLoquidacaoVo> liquidacoes) {
        this.liquidacoes = liquidacoes;
    }

    public List<SolicitacaoEmpenhoEstornoItemVo> getItens() {
        return itens;
    }

    public void setItens(List<SolicitacaoEmpenhoEstornoItemVo> itens) {
        this.itens = itens;
    }

    public boolean hasLiquidacoes() {
        return liquidacoes != null && !liquidacoes.isEmpty();
    }

    public BigDecimal getSaldoTotalComLiquidacoes() {
        return saldo.add(getSaldoTotalLiquidacao());
    }

    public BigDecimal getSaldoTotalLiquidacao() {
        BigDecimal total = BigDecimal.ZERO;
        for (SolicitacaoEmpenhoEstornoLoquidacaoVo vwLiquidacao : getLiquidacoes()) {
            if (vwLiquidacao.getSelecionado()) {
                total = total.add(vwLiquidacao.getSaldo());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalLiquidacao() {
        BigDecimal total = BigDecimal.ZERO;
        for (SolicitacaoEmpenhoEstornoLoquidacaoVo ex : getLiquidacoes()) {
            if (ex.getLiquidacao() != null) {
                total = total.add(ex.getLiquidacao().getValor());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalSaldoLiquidacao() {
        BigDecimal total = BigDecimal.ZERO;
        for (SolicitacaoEmpenhoEstornoLoquidacaoVo ex : getLiquidacoes()) {
            if (ex.getLiquidacao() != null) {
                total = total.add(ex.getLiquidacao().getSaldo());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalPagamentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (SolicitacaoEmpenhoEstornoLoquidacaoVo ex : getLiquidacoes()) {
            total = total.add(ex.getValorTotalPagamento());
        }
        return total;
    }

    public boolean isTipoEstornoEmpenho() {
        return TipoEstornoExecucao.EMPENHO.equals(tipoEstorno);
    }

    public boolean isTipoEstornoSolicitacaoEmpenho() {
        return TipoEstornoExecucao.SOLICITACAO_EMPENHO.equals(tipoEstorno);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SolicitacaoEmpenhoEstornoVo that = (SolicitacaoEmpenhoEstornoVo) o;
        return Objects.equals(solicitacaoEmpenho.getId(), that.solicitacaoEmpenho.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(solicitacaoEmpenho.getId());
    }

    @Override
    public int compareTo(SolicitacaoEmpenhoEstornoVo o) {
        if (o.getSolicitacaoEmpenho() != null && getSolicitacaoEmpenho() != null) {
            return getSolicitacaoEmpenho().getDataSolicitacao().compareTo(o.getSolicitacaoEmpenho().getDataSolicitacao());
        }
        return 0;
    }

    public enum TipoEstornoExecucao {
        EMPENHO, SOLICITACAO_EMPENHO;
    }
}


