package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSaldoGrupoBens;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensImoveis;
import br.com.webpublico.enums.TipoOperacaoBensMoveis;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class AnaliseGrupoBemImovel extends AssistenteBarraProgresso {

    private UnidadeOrganizacional unidadeOrganizacional;
    private GrupoBem grupoBem;
    private TipoGrupo tipoGrupo;
    private TipoOperacaoBensImoveis tipoOperacaoBensImoveis;
    private TipoLancamento tipoLancamento;
    private List<ReprocessamentoSaldoGrupoBens> movimentos;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoUltimo;
    private BigDecimal totalMovimentoCredito;
    private BigDecimal totalMovimentoDebito;

    public AnaliseGrupoBemImovel() {

    }

    public AnaliseGrupoBemImovel(ReprocessamentoSaldoGrupoBens reprocessamento) {
        this.unidadeOrganizacional = reprocessamento.getUnidadeOrganizacional();
        this.grupoBem = reprocessamento.getGrupoBem();
        this.tipoGrupo = reprocessamento.getTipoGrupo();
        this.tipoOperacaoBensImoveis = reprocessamento.getTipoOperacaoBensImoveis();
        this.tipoLancamento = reprocessamento.getTipoLancamento();
        this.movimentos = Lists.newArrayList();
        this.movimentos.add(reprocessamento);
        if (reprocessamento.getTipoOperacaoBensImoveis().equals(TipoOperacaoBensImoveis.AQUISICAO_BENS_IMOVEIS)) {
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

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public TipoOperacaoBensImoveis getTipoOperacaoBensImoveis() {
        return tipoOperacaoBensImoveis;
    }

    public void setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis tipoOperacaoBensImoveis) {
        this.tipoOperacaoBensImoveis = tipoOperacaoBensImoveis;
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

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
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
            BigDecimal totalMovimento = saldoAnteriorSaldoDoDia.subtract(movimentoAtual);
            return totalMovimento;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getDiferenca() {
        BigDecimal totalMovimento = getTotalMovimentoAtual();
        return totalMovimento.subtract(getSaldoAtual());
    }

}
