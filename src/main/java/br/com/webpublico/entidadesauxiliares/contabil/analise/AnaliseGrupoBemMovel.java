package br.com.webpublico.entidadesauxiliares.contabil.analise;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSaldoGrupoBens;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class AnaliseGrupoBemMovel extends AssistenteBarraProgresso {

    private UnidadeOrganizacional unidadeOrganizacional;
    private GrupoBem grupoBem;
    private TipoGrupo tipoGrupo;
    private TipoOperacaoBensMoveis tipoOperacaoBensMoveis;
    private NaturezaTipoGrupoBem natureza;
    private TipoLancamento tipoLancamento;
    private boolean receitaRealizada;
    private List<ReprocessamentoSaldoGrupoBens> movimentos;
    private BigDecimal saldoAnterior;
    private BigDecimal saldoUltimo;
    private BigDecimal totalMovimentoCredito;
    private BigDecimal totalMovimentoDebito;

    public AnaliseGrupoBemMovel() {

    }
    public AnaliseGrupoBemMovel(ReprocessamentoSaldoGrupoBens reprocessamento) {
        this.unidadeOrganizacional = reprocessamento.getUnidadeOrganizacional();
        this.grupoBem = reprocessamento.getGrupoBem();
        this.tipoGrupo = reprocessamento.getTipoGrupo();
        this.tipoOperacaoBensMoveis = reprocessamento.getTipoOperacaoBensMoveis();
        this.tipoLancamento = reprocessamento.getTipoLancamento();
        this.receitaRealizada = reprocessamento.isReceitaRealizada();
        this.movimentos = Lists.newArrayList();
        this.movimentos.add(reprocessamento);
        if (reprocessamento.getTipoOperacaoBensMoveis().equals(TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS)) {
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

    public TipoOperacaoBensMoveis getTipoOperacaoBensMoveis() {
        return tipoOperacaoBensMoveis;
    }

    public void setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis tipoOperacaoBensMoveis) {
        this.tipoOperacaoBensMoveis = tipoOperacaoBensMoveis;
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

    public boolean isReceitaRealizada() {
        return receitaRealizada;
    }

    public void setReceitaRealizada(boolean receitaRealizada) {
        this.receitaRealizada = receitaRealizada;
    }

    public NaturezaTipoGrupoBem getNatureza() {
        return natureza;
    }

    public void setNatureza(NaturezaTipoGrupoBem natureza) {
        this.natureza = natureza;
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
