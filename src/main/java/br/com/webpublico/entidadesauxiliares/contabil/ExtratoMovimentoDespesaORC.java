package br.com.webpublico.entidadesauxiliares.contabil;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ExtratoMovimentoDespesaORC {
    private Date dataInicial;
    private Date dataFinal;
    private Exercicio exercicio;
    private Boolean mostrarMovimentos;
    private BarraProgressoItens barraProgressoItens;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private AcaoPPA acaoPPA;
    private DespesaORC despesaORC;
    private FonteDespesaORC fonteDespesaORC;
    private List<SaldoFonteDespesaOcamentariaVO> saldos;
    private OperacaoORC operacaoORCSelecionada;
    private String classeOrigemSelecionada;
    private List<OperacaoORC> operacoesSelecionadas;
    private List<String> classesSelecionadas;
    private Boolean mostrarHistoricoTabela;

    private BigDecimal dotacao;
    private BigDecimal alteracao;
    private BigDecimal empenhado;
    private BigDecimal reservado;
    private BigDecimal reservadoPorLicitacao;
    private BigDecimal liquidado;
    private BigDecimal pago;
    private BigDecimal saldoAtual;

    public ExtratoMovimentoDespesaORC() {
        limparCampos();
    }

    public void limparCampos() {
        saldos = Lists.newArrayList();
        barraProgressoItens = new BarraProgressoItens();
        mostrarMovimentos = Boolean.TRUE;
        mostrarHistoricoTabela = Boolean.TRUE;
        dotacao = BigDecimal.ZERO;
        alteracao = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
        reservado = BigDecimal.ZERO;
        reservadoPorLicitacao = BigDecimal.ZERO;
        liquidado = BigDecimal.ZERO;
        pago = BigDecimal.ZERO;
        operacoesSelecionadas = Lists.newArrayList();
        classesSelecionadas = Lists.newArrayList();
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public Boolean getMostrarMovimentos() {
        return mostrarMovimentos;
    }

    public void setMostrarMovimentos(Boolean mostrarMovimentos) {
        this.mostrarMovimentos = mostrarMovimentos;
    }

    public BigDecimal getDotacao() {
        return dotacao;
    }

    public void setDotacao(BigDecimal dotacao) {
        this.dotacao = dotacao;
    }

    public BigDecimal getAlteracao() {
        return alteracao;
    }

    public void setAlteracao(BigDecimal alteracao) {
        this.alteracao = alteracao;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public BigDecimal getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(BigDecimal liquidado) {
        this.liquidado = liquidado;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public BigDecimal getReservado() {
        return reservado;
    }

    public void setReservado(BigDecimal reservado) {
        this.reservado = reservado;
    }

    public BigDecimal getReservadoPorLicitacao() {
        return reservadoPorLicitacao;
    }

    public void setReservadoPorLicitacao(BigDecimal reservadoPorLicitacao) {
        this.reservadoPorLicitacao = reservadoPorLicitacao;
    }

    public List<SaldoFonteDespesaOcamentariaVO> getSaldos() {
        if (saldos != null) {
            saldos.sort(Comparator.comparing(SaldoFonteDespesaOcamentariaVO::getDataSaldo));
        }
        return saldos;
    }

    public void setSaldos(List<SaldoFonteDespesaOcamentariaVO> saldos) {
        this.saldos = saldos;
    }

    public SaldoFonteDespesaOcamentariaVO getUltimoSaldo() {
        return saldos != null && !saldos.isEmpty() ? saldos.get(saldos.size() - 1) : null;
    }

    public BigDecimal getSaldoAtual() {
        if (saldoAtual == null) {
            BigDecimal dotacaoAtual = getSaldoDotacaoAtual();
            BigDecimal debito = (this.getEmpenhado().add(this.getReservado().add(this.getReservadoPorLicitacao())));
            saldoAtual = dotacaoAtual.subtract(debito);
        }
        return saldoAtual;
    }

    public BigDecimal getSaldoDotacaoAtual() {
        return this.getDotacao().add(getAlteracao());
    }

    public OperacaoORC getOperacaoORCSelecionada() {
        return operacaoORCSelecionada;
    }

    public void setOperacaoORCSelecionada(OperacaoORC operacaoORCSelecionada) {
        this.operacaoORCSelecionada = operacaoORCSelecionada;
    }

    public String getClasseOrigemSelecionada() {
        return classeOrigemSelecionada;
    }

    public void setClasseOrigemSelecionada(String classeOrigemSelecionada) {
        this.classeOrigemSelecionada = classeOrigemSelecionada;
    }

    public List<OperacaoORC> getOperacoesSelecionadas() {
        return operacoesSelecionadas;
    }

    public void setOperacoesSelecionadas(List<OperacaoORC> operacoesSelecionadas) {
        this.operacoesSelecionadas = operacoesSelecionadas;
    }

    public List<String> getClassesSelecionadas() {
        return classesSelecionadas;
    }

    public void setClassesSelecionadas(List<String> classesSelecionadas) {
        this.classesSelecionadas = classesSelecionadas;
    }

    public Boolean getMostrarHistoricoTabela() {
        if (mostrarHistoricoTabela == null) {
            mostrarHistoricoTabela = Boolean.FALSE;
        }
        return mostrarHistoricoTabela;
    }

    public void setMostrarHistoricoTabela(Boolean mostrarHistoricoTabela) {
        this.mostrarHistoricoTabela = mostrarHistoricoTabela;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public boolean hasMovimentosFiltrados() {
        return !operacoesSelecionadas.isEmpty() || !classesSelecionadas.isEmpty();
    }

    public void atualizarMovimentosFiltrados() {
        if (saldos != null && !saldos.isEmpty()) {
            for (SaldoFonteDespesaOcamentariaVO saldo : saldos) {
                saldo.setMovimentosFiltrados(Lists.newArrayList());
                for (MovimentoDespesaORCVO movimento : saldo.getMovimentos()) {
                    boolean canAdicionarPorOperacao = operacoesSelecionadas.isEmpty();
                    if (!operacoesSelecionadas.isEmpty()) {
                        for (OperacaoORC operacao : operacoesSelecionadas) {
                            if (operacao.equals(movimento.getOperacaoORC())) {
                                canAdicionarPorOperacao = true;
                                break;
                            }
                        }
                    }

                    boolean canAdicionarPorClasse = classesSelecionadas.isEmpty();
                    if (!classesSelecionadas.isEmpty()) {
                        for (String classe : classesSelecionadas) {
                            if (classe.equals(movimento.getClasseOrigem())) {
                                canAdicionarPorClasse = true;
                                break;
                            }
                        }
                    }

                    if (canAdicionarPorOperacao && canAdicionarPorClasse) {
                        Util.adicionarObjetoEmLista(saldo.getMovimentosFiltrados(), movimento);
                    }
                }
            }
        }
    }
}
