package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSaldoGrupoBens;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensEstoque;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class AnaliseGrupoBemEstoque extends AssistenteBarraProgresso {

    private UnidadeOrganizacional unidadeOrganizacional;
    private GrupoMaterial grupoMaterial;
    private TipoEstoque tipoEstoque;
    private TipoOperacaoBensEstoque tipoOperacaoBensEstoque;
    private TipoLancamento tipoLancamento;
    private List<ReprocessamentoSaldoGrupoBens> movimentos;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoUltimo;
    private BigDecimal totalMovimentoCredito;
    private BigDecimal totalMovimentoDebito;

    public AnaliseGrupoBemEstoque() {

    }

    public AnaliseGrupoBemEstoque(ReprocessamentoSaldoGrupoBens reprocessamento) {
        this.unidadeOrganizacional = reprocessamento.getUnidadeOrganizacional();
        this.grupoMaterial = reprocessamento.getGrupoMaterial();
        this.tipoEstoque = reprocessamento.getTipoEstoque();
        this.tipoOperacaoBensEstoque = reprocessamento.getTipoOperacaoBensEstoque();
        this.tipoLancamento = reprocessamento.getTipoLancamento();
        this.movimentos = Lists.newArrayList();
        this.movimentos.add(reprocessamento);
        if (reprocessamento.getTipoOperacaoBensEstoque().equals(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE)) {
            this.totalMovimentoDebito = reprocessamento.getValor();
            this.totalMovimentoCredito = BigDecimal.ZERO;
        } else {
            this.totalMovimentoCredito = reprocessamento.getValor();
            this.totalMovimentoDebito = BigDecimal.ZERO;
        }
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public TipoEstoque getTipoEstoque() {
        return tipoEstoque;
    }

    public void setTipoEstoque(TipoEstoque tipoEstoque) {
        this.tipoEstoque = tipoEstoque;
    }

    public TipoOperacaoBensEstoque getTipoOperacaoBensEstoque() {
        return tipoOperacaoBensEstoque;
    }

    public void setTipoOperacaoBensEstoque(TipoOperacaoBensEstoque tipoOperacaoBensEstoque) {
        this.tipoOperacaoBensEstoque = tipoOperacaoBensEstoque;
    }

    public List<ReprocessamentoSaldoGrupoBens> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<ReprocessamentoSaldoGrupoBens> movimentos) {
        this.movimentos = movimentos;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getSaldoUltimo() {
        return saldoUltimo;
    }

    public void setSaldoUltimo(BigDecimal saldoUltimo) {
        this.saldoUltimo = saldoUltimo;
    }

    public BigDecimal getTotalMovimentoCredito() {
        return totalMovimentoCredito;
    }

    public void setTotalMovimentoCredito(BigDecimal totalMovimentoCredito) {
        this.totalMovimentoCredito = totalMovimentoCredito;
    }

    public BigDecimal getTotalMovimentoDebito() {
        return totalMovimentoDebito;
    }

    public void setTotalMovimentoDebito(BigDecimal totalMovimentoDebito) {
        this.totalMovimentoDebito = totalMovimentoDebito;
    }

    public TipoLancamento getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(TipoLancamento tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }

    public BigDecimal getSaldoAtual() {
        if (saldoUltimo != null) {
            return saldoUltimo;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalMovimentoAtual() {
        if (getTotalMovimentoCredito() != null && getTotalMovimentoDebito() != null) {
            BigDecimal movimentoAtual = getTotalMovimentoCredito().subtract(getTotalMovimentoDebito());
            BigDecimal saldoAnteriorSaldoDoDia = saldoAnterior != null ? saldoAnterior : BigDecimal.ZERO;
            BigDecimal totalMovimento = saldoAnteriorSaldoDoDia.add(movimentoAtual);
            return totalMovimento;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getDiferenca() {
        BigDecimal totalMovimento = getTotalMovimentoAtual();
        return totalMovimento.subtract(getSaldoAtual());
    }

}
