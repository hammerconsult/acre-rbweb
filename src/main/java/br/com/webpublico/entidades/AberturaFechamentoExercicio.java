package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/09/14
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Abertura e Fechamento de Exercicio")
@Table(name = "ABERTURAFECHAEXERCICIO")
public class AberturaFechamentoExercicio extends SuperEntidade implements Serializable {
    private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Date dataGeracao;
    @Etiqueta("Exercicio")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Descrição")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private String descricao;
    //INSCRIÇÃO DE RESTO A PAGAR
    @Where(clause = "tipoRestos = 'PROCESSADOS'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescricaoEmpenho> prescricaoEmpenhosProcessados;
    @Where(clause = "tipoRestos = 'NAO_PROCESSADOS'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrescricaoEmpenho> prescricaoEmpenhosNaoProcessados;
    @Where(clause = "tipoRestos = 'PROCESSADOS'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InscricaoEmpenho> inscricaoEmpenhosProcessados;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "tipoRestos = 'NAO_PROCESSADOS'")
    private List<InscricaoEmpenho> inscricaoEmpenhosNaoProcessados;
    //RECEITA
    @Where(clause = "tipoReceita = 'RECEITA_A_REALIZAR'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaFechamentoExercicio> receitasARealizar;
    @Where(clause = "tipoReceita = 'REESTIMATIVA'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaFechamentoExercicio> receitasReestimativas;
    @Where(clause = "tipoReceita = 'DEDUCAO_INICIAL_RECEITA'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaFechamentoExercicio> receitasDeducaoPrevisaoInicial;
    @Where(clause = "tipoReceita = 'DEDUCAO_RECEITA_REALIZADA'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaFechamentoExercicio> receitasDeducaoReceitaRealizada;
    @Where(clause = "tipoReceita = 'RECEITA_REALIZADA'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaFechamentoExercicio> receitasRealizada;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaExtraFechamentoExercicio> receitasExtras;
    //DOTACAO
    @Where(clause = "tipo = 'DOTACAO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> dotacoes;
    @Where(clause = "tipo = 'SUPLEMENTAR'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> creditosAdicionaisSuplementares;
    @Where(clause = "tipo = 'ESPECIAL'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> creditosAdicionaisEspecial;
    @Where(clause = "tipo = 'EXTRAORDINARIO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> creditosAdicionaisExtraordinario;
    @Where(clause = "tipo = 'ANULACAO_DOTACAO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> anulacaoDotacao;
    @Where(clause = "tipo = 'SUPERAVIT'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> cretitosAdicionaisPorSuperavitFinanceira;
    @Where(clause = "tipo = 'EXCESSO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> cretitosAdicionaisPorExcessoDeArrecadacao;
    @Where(clause = "tipo = 'ANULACAO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> cretitosAdicionaisPorAnulacaoDeDotacao;
    @Where(clause = "tipo = 'OPERACAO_CREDITO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> cretitosAdicionaisPorOperacaoDeCredito;
    @Where(clause = "tipo = 'RESERVA_CONTIGENCIA'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> cretitosAdicionaisPorReservaDeContigencia;
    @Where(clause = "tipo = 'ANULACAO_CREDITO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DotacaoFechamentoExercicio> cretitosAdicionaisPorAnulacaoDeCredito;
    //RESTOS A PAGAR
    @Where(clause = "tipo = 'EMPENHO_A_LIQUIDAR_NAO_PROCESSADOS'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DespesaFechamentoExercicio> empenhosALiquidarInscritosRestoAPagarNaoProcessados;
    @Where(clause = "tipo = 'EMPENHO_A_LIQUIDAR_PROCESSADOS'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DespesaFechamentoExercicio> empenhosLiquidadosInscritosRestoAPagarProcessados;
    @Where(clause = "tipo = 'CREDITO_EMPENHADO_PAGO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DespesaFechamentoExercicio> empenhosCreditoEmpenhadoPago;
    @Where(clause = "tipo = 'PAGO_RESTO_PAGAR_PROCESSADO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DespesaFechamentoExercicio> pagoDosRestosAPagarProcessados;
    @Where(clause = "tipo = 'PAGO_RESTO_PAGAR_NAO_PROCESSADO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DespesaFechamentoExercicio> pagoDosRestosAPagarNaoProcessados;
    @Where(clause = "tipo = 'CANCELADO_RESTO_PAGAR_PROCESSADO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DespesaFechamentoExercicio> canceladoDosRestosAPagarProcessados;
    @Where(clause = "tipo = 'CANCELADO_RESTO_PAGAR_NAO_PROCESSADO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DespesaFechamentoExercicio> canceladoDosRestosAPagarNaoProcessados;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteObrigacaoAPagar> obrigacoesAPagar;
    //FONTES
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FonteDeRecursoFechamentoExercicio> destinacaoDeRecurso;
    //CONTAS CONTABEIS
    @Where(clause = "tipo = 'RESULTADO_DIMINUTIVO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContaFechamentoExercicio> resultadoDiminutivoExercicio;
    @Where(clause = "tipo = 'RESULTADO_AUMENTATIVO'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContaFechamentoExercicio> resultadoAumentativoExercicio;
    //PLANO DE CONTAS
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanoDeContasFechamentoExercicio> planoDeContas;
    //CONFIGURACOES
    @Where(clause = "tipo = 'RECEITA'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoEventoFechamentoExercicio> configuracoes;
    //OCCS
    @Where(clause = "tipo = 'RECEITA'")
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OCCFechamentoExercicio> occs;
    //TRANSPORTE DE SALDO
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteDeSaldoFechamentoExercicio> transporteDeSaldo;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteSaldoFinanceiro> transporteDeSaldoFinanceiro;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteCreditoReceber> transporteDeSaldoCreditoReceber;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteDividaAtiva> transporteDeSaldoDividaAtiva;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteExtra> transporteDeSaldoExtra;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteSaldoDividaPublica> transporteDeSaldoDividaPublica;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteSaldoContaAuxiliarDetalhada> transporteDeSaldoDeContasAuxiliares;
    // ABERTURA
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AberturaInscricaoResto> inscricaoRestoPagarProcessados;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AberturaInscricaoResto> inscricaoRestoPagarNaoProcessados;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteDeSaldoAbertura> transferenciaResultadoPublico;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteDeSaldoAbertura> transferenciaResultadoPrivado;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteDeSaldoAbertura> transferenciaAjustesPublico;
    @OneToMany(mappedBy = "aberturaFechamentoExercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransporteDeSaldoAbertura> transferenciaAjustesPrivado;

    public AberturaFechamentoExercicio() {
        prescricaoEmpenhosProcessados = Lists.newArrayList();
        prescricaoEmpenhosNaoProcessados = Lists.newArrayList();
        inscricaoEmpenhosProcessados = Lists.newArrayList();
        inscricaoEmpenhosNaoProcessados = Lists.newArrayList();
        receitasARealizar = Lists.newArrayList();
        receitasReestimativas = Lists.newArrayList();
        receitasDeducaoPrevisaoInicial = Lists.newArrayList();
        receitasDeducaoReceitaRealizada = Lists.newArrayList();
        receitasRealizada = Lists.newArrayList();
        receitasExtras = Lists.newArrayList();
        dotacoes = Lists.newArrayList();
        creditosAdicionaisSuplementares = Lists.newArrayList();
        creditosAdicionaisEspecial = Lists.newArrayList();
        creditosAdicionaisExtraordinario = Lists.newArrayList();
        anulacaoDotacao = Lists.newArrayList();
        cretitosAdicionaisPorSuperavitFinanceira = Lists.newArrayList();
        cretitosAdicionaisPorExcessoDeArrecadacao = Lists.newArrayList();
        cretitosAdicionaisPorAnulacaoDeDotacao = Lists.newArrayList();
        cretitosAdicionaisPorOperacaoDeCredito = Lists.newArrayList();
        cretitosAdicionaisPorReservaDeContigencia = Lists.newArrayList();
        cretitosAdicionaisPorAnulacaoDeCredito = Lists.newArrayList();
        empenhosALiquidarInscritosRestoAPagarNaoProcessados = Lists.newArrayList();
        empenhosLiquidadosInscritosRestoAPagarProcessados = Lists.newArrayList();
        empenhosCreditoEmpenhadoPago = Lists.newArrayList();
        pagoDosRestosAPagarProcessados = Lists.newArrayList();
        pagoDosRestosAPagarNaoProcessados = Lists.newArrayList();
        canceladoDosRestosAPagarProcessados = Lists.newArrayList();
        canceladoDosRestosAPagarNaoProcessados = Lists.newArrayList();
        destinacaoDeRecurso = Lists.newArrayList();
        resultadoDiminutivoExercicio = Lists.newArrayList();
        resultadoAumentativoExercicio = Lists.newArrayList();
        planoDeContas = Lists.newArrayList();
        configuracoes = Lists.newArrayList();
        occs = Lists.newArrayList();
        transporteDeSaldo = Lists.newArrayList();
        transporteDeSaldoFinanceiro = Lists.newArrayList();
        transporteDeSaldoCreditoReceber = Lists.newArrayList();
        transporteDeSaldoDividaAtiva = Lists.newArrayList();
        transporteDeSaldoExtra = Lists.newArrayList();
        transporteDeSaldoDividaPublica = Lists.newArrayList();
        inscricaoRestoPagarProcessados = Lists.newArrayList();
        inscricaoRestoPagarNaoProcessados = Lists.newArrayList();
        transferenciaResultadoPublico = Lists.newArrayList();
        transferenciaResultadoPrivado = Lists.newArrayList();
        transferenciaAjustesPublico = Lists.newArrayList();
        transferenciaAjustesPrivado = Lists.newArrayList();
        obrigacoesAPagar = Lists.newArrayList();
        transporteDeSaldoDeContasAuxiliares = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<PrescricaoEmpenho> getPrescricaoEmpenhosProcessados() {
        return prescricaoEmpenhosProcessados;
    }

    public void setPrescricaoEmpenhosProcessados(List<PrescricaoEmpenho> prescricaoEmpenhosProcessados) {
        this.prescricaoEmpenhosProcessados = prescricaoEmpenhosProcessados;
    }

    public List<PrescricaoEmpenho> getPrescricaoEmpenhosNaoProcessados() {
        return prescricaoEmpenhosNaoProcessados;
    }

    public void setPrescricaoEmpenhosNaoProcessados(List<PrescricaoEmpenho> prescricaoEmpenhosNaoProcessados) {
        this.prescricaoEmpenhosNaoProcessados = prescricaoEmpenhosNaoProcessados;
    }

    public List<InscricaoEmpenho> getInscricaoEmpenhosProcessados() {
        return inscricaoEmpenhosProcessados;
    }

    public void setInscricaoEmpenhosProcessados(List<InscricaoEmpenho> inscricaoEmpenhosProcessados) {
        this.inscricaoEmpenhosProcessados = inscricaoEmpenhosProcessados;
    }

    public List<InscricaoEmpenho> getInscricaoEmpenhosNaoProcessados() {
        return inscricaoEmpenhosNaoProcessados;
    }

    public void setInscricaoEmpenhosNaoProcessados(List<InscricaoEmpenho> inscricaoEmpenhosNaoProcessados) {
        this.inscricaoEmpenhosNaoProcessados = inscricaoEmpenhosNaoProcessados;
    }

    public List<ReceitaFechamentoExercicio> getReceitasARealizar() {
        return receitasARealizar;
    }

    public void setReceitasARealizar(List<ReceitaFechamentoExercicio> receitasARealizar) {
        this.receitasARealizar = receitasARealizar;
    }

    public List<ReceitaFechamentoExercicio> getReceitasReestimativas() {
        return receitasReestimativas;
    }

    public void setReceitasReestimativas(List<ReceitaFechamentoExercicio> receitasReestimativas) {
        this.receitasReestimativas = receitasReestimativas;
    }

    public List<ReceitaFechamentoExercicio> getReceitasDeducaoPrevisaoInicial() {
        return receitasDeducaoPrevisaoInicial;
    }

    public void setReceitasDeducaoPrevisaoInicial(List<ReceitaFechamentoExercicio> receitasDeducaoPrevisaoInicial) {
        this.receitasDeducaoPrevisaoInicial = receitasDeducaoPrevisaoInicial;
    }

    public List<ReceitaFechamentoExercicio> getReceitasDeducaoReceitaRealizada() {
        return receitasDeducaoReceitaRealizada;
    }

    public void setReceitasDeducaoReceitaRealizada(List<ReceitaFechamentoExercicio> receitasDeducaoReceitaRealizada) {
        this.receitasDeducaoReceitaRealizada = receitasDeducaoReceitaRealizada;
    }

    public List<ReceitaFechamentoExercicio> getReceitasRealizada() {
        return receitasRealizada;
    }

    public void setReceitasRealizada(List<ReceitaFechamentoExercicio> receitasRealizada) {
        this.receitasRealizada = receitasRealizada;
    }

    public List<DotacaoFechamentoExercicio> getDotacoes() {
        return dotacoes;
    }

    public void setDotacoes(List<DotacaoFechamentoExercicio> dotacoes) {
        this.dotacoes = dotacoes;
    }

    public List<DotacaoFechamentoExercicio> getCreditosAdicionaisSuplementares() {
        return creditosAdicionaisSuplementares;
    }

    public void setCreditosAdicionaisSuplementares(List<DotacaoFechamentoExercicio> creditosAdicionaisSuplementares) {
        this.creditosAdicionaisSuplementares = creditosAdicionaisSuplementares;
    }

    public List<DotacaoFechamentoExercicio> getCreditosAdicionaisEspecial() {
        return creditosAdicionaisEspecial;
    }

    public void setCreditosAdicionaisEspecial(List<DotacaoFechamentoExercicio> creditosAdicionaisEspecial) {
        this.creditosAdicionaisEspecial = creditosAdicionaisEspecial;
    }

    public List<DotacaoFechamentoExercicio> getCreditosAdicionaisExtraordinario() {
        return creditosAdicionaisExtraordinario;
    }

    public void setCreditosAdicionaisExtraordinario(List<DotacaoFechamentoExercicio> creditosAdicionaisExtraordinario) {
        this.creditosAdicionaisExtraordinario = creditosAdicionaisExtraordinario;
    }

    public List<DotacaoFechamentoExercicio> getAnulacaoDotacao() {
        return anulacaoDotacao;
    }

    public void setAnulacaoDotacao(List<DotacaoFechamentoExercicio> anulacaoDotacao) {
        this.anulacaoDotacao = anulacaoDotacao;
    }

    public List<DotacaoFechamentoExercicio> getCretitosAdicionaisPorSuperavitFinanceira() {
        return cretitosAdicionaisPorSuperavitFinanceira;
    }

    public void setCretitosAdicionaisPorSuperavitFinanceira(List<DotacaoFechamentoExercicio> cretitosAdicionaisPorSuperavitFinanceira) {
        this.cretitosAdicionaisPorSuperavitFinanceira = cretitosAdicionaisPorSuperavitFinanceira;
    }

    public List<DotacaoFechamentoExercicio> getCretitosAdicionaisPorExcessoDeArrecadacao() {
        return cretitosAdicionaisPorExcessoDeArrecadacao;
    }

    public void setCretitosAdicionaisPorExcessoDeArrecadacao(List<DotacaoFechamentoExercicio> cretitosAdicionaisPorExcessoDeArrecadacao) {
        this.cretitosAdicionaisPorExcessoDeArrecadacao = cretitosAdicionaisPorExcessoDeArrecadacao;
    }

    public List<DotacaoFechamentoExercicio> getCretitosAdicionaisPorAnulacaoDeDotacao() {
        return cretitosAdicionaisPorAnulacaoDeDotacao;
    }

    public void setCretitosAdicionaisPorAnulacaoDeDotacao(List<DotacaoFechamentoExercicio> cretitosAdicionaisPorAnulacaoDeDotacao) {
        this.cretitosAdicionaisPorAnulacaoDeDotacao = cretitosAdicionaisPorAnulacaoDeDotacao;
    }

    public List<DotacaoFechamentoExercicio> getCretitosAdicionaisPorOperacaoDeCredito() {
        return cretitosAdicionaisPorOperacaoDeCredito;
    }

    public void setCretitosAdicionaisPorOperacaoDeCredito(List<DotacaoFechamentoExercicio> cretitosAdicionaisPorOperacaoDeCredito) {
        this.cretitosAdicionaisPorOperacaoDeCredito = cretitosAdicionaisPorOperacaoDeCredito;
    }

    public List<DotacaoFechamentoExercicio> getCretitosAdicionaisPorReservaDeContigencia() {
        return cretitosAdicionaisPorReservaDeContigencia;
    }

    public void setCretitosAdicionaisPorReservaDeContigencia(List<DotacaoFechamentoExercicio> cretitosAdicionaisPorReservaDeContigencia) {
        this.cretitosAdicionaisPorReservaDeContigencia = cretitosAdicionaisPorReservaDeContigencia;
    }

    public List<DotacaoFechamentoExercicio> getCretitosAdicionaisPorAnulacaoDeCredito() {
        return cretitosAdicionaisPorAnulacaoDeCredito;
    }

    public void setCretitosAdicionaisPorAnulacaoDeCredito(List<DotacaoFechamentoExercicio> cretitosAdicionaisPorAnulacaoDeCredito) {
        this.cretitosAdicionaisPorAnulacaoDeCredito = cretitosAdicionaisPorAnulacaoDeCredito;
    }

    public List<DespesaFechamentoExercicio> getEmpenhosALiquidarInscritosRestoAPagarNaoProcessados() {
        return empenhosALiquidarInscritosRestoAPagarNaoProcessados;
    }

    public void setEmpenhosALiquidarInscritosRestoAPagarNaoProcessados(List<DespesaFechamentoExercicio> empenhosALiquidarInscritosRestoAPagarNaoProcessados) {
        this.empenhosALiquidarInscritosRestoAPagarNaoProcessados = empenhosALiquidarInscritosRestoAPagarNaoProcessados;
    }

    public List<DespesaFechamentoExercicio> getEmpenhosLiquidadosInscritosRestoAPagarProcessados() {
        return empenhosLiquidadosInscritosRestoAPagarProcessados;
    }

    public void setEmpenhosLiquidadosInscritosRestoAPagarProcessados(List<DespesaFechamentoExercicio> empenhosLiquidadosInscritosRestoAPagarProcessados) {
        this.empenhosLiquidadosInscritosRestoAPagarProcessados = empenhosLiquidadosInscritosRestoAPagarProcessados;
    }

    public List<DespesaFechamentoExercicio> getEmpenhosCreditoEmpenhadoPago() {
        return empenhosCreditoEmpenhadoPago;
    }

    public void setEmpenhosCreditoEmpenhadoPago(List<DespesaFechamentoExercicio> empenhosCreditoEmpenhadoPago) {
        this.empenhosCreditoEmpenhadoPago = empenhosCreditoEmpenhadoPago;
    }

    public List<DespesaFechamentoExercicio> getPagoDosRestosAPagarProcessados() {
        return pagoDosRestosAPagarProcessados;
    }

    public void setPagoDosRestosAPagarProcessados(List<DespesaFechamentoExercicio> pagoDosRestosAPagarProcessados) {
        this.pagoDosRestosAPagarProcessados = pagoDosRestosAPagarProcessados;
    }

    public List<DespesaFechamentoExercicio> getPagoDosRestosAPagarNaoProcessados() {
        return pagoDosRestosAPagarNaoProcessados;
    }

    public void setPagoDosRestosAPagarNaoProcessados(List<DespesaFechamentoExercicio> pagoDosRestosAPagarNaoProcessados) {
        this.pagoDosRestosAPagarNaoProcessados = pagoDosRestosAPagarNaoProcessados;
    }

    public List<DespesaFechamentoExercicio> getCanceladoDosRestosAPagarProcessados() {
        return canceladoDosRestosAPagarProcessados;
    }

    public void setCanceladoDosRestosAPagarProcessados(List<DespesaFechamentoExercicio> canceladoDosRestosAPagarProcessados) {
        this.canceladoDosRestosAPagarProcessados = canceladoDosRestosAPagarProcessados;
    }

    public List<DespesaFechamentoExercicio> getCanceladoDosRestosAPagarNaoProcessados() {
        return canceladoDosRestosAPagarNaoProcessados;
    }

    public void setCanceladoDosRestosAPagarNaoProcessados(List<DespesaFechamentoExercicio> canceladoDosRestosAPagarNaoProcessados) {
        this.canceladoDosRestosAPagarNaoProcessados = canceladoDosRestosAPagarNaoProcessados;
    }

    public List<FonteDeRecursoFechamentoExercicio> getDestinacaoDeRecurso() {
        return destinacaoDeRecurso;
    }

    public void setDestinacaoDeRecurso(List<FonteDeRecursoFechamentoExercicio> destinacaoDeRecurso) {
        this.destinacaoDeRecurso = destinacaoDeRecurso;
    }

    public List<ContaFechamentoExercicio> getResultadoDiminutivoExercicio() {
        return resultadoDiminutivoExercicio;
    }

    public void setResultadoDiminutivoExercicio(List<ContaFechamentoExercicio> resultadoDiminutivoExercicio) {
        this.resultadoDiminutivoExercicio = resultadoDiminutivoExercicio;
    }

    public List<ContaFechamentoExercicio> getResultadoAumentativoExercicio() {
        return resultadoAumentativoExercicio;
    }

    public void setResultadoAumentativoExercicio(List<ContaFechamentoExercicio> resultadoAumentativoExercicio) {
        this.resultadoAumentativoExercicio = resultadoAumentativoExercicio;
    }

    public List<PlanoDeContasFechamentoExercicio> getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(List<PlanoDeContasFechamentoExercicio> planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public List<ConfiguracaoEventoFechamentoExercicio> getConfiguracoes() {
        return configuracoes;
    }

    public void setConfiguracoes(List<ConfiguracaoEventoFechamentoExercicio> configuracoes) {
        this.configuracoes = configuracoes;
    }

    public List<OCCFechamentoExercicio> getOccs() {
        return occs;
    }

    public void setOccs(List<OCCFechamentoExercicio> occs) {
        this.occs = occs;
    }

    public List<TransporteDeSaldoFechamentoExercicio> getTransporteDeSaldo() {
        return transporteDeSaldo;
    }

    public void setTransporteDeSaldo(List<TransporteDeSaldoFechamentoExercicio> transporteDeSaldo) {
        this.transporteDeSaldo = transporteDeSaldo;
    }

    public List<AberturaInscricaoResto> getInscricaoRestoPagarProcessados() {
        return inscricaoRestoPagarProcessados;
    }

    public void setInscricaoRestoPagarProcessados(List<AberturaInscricaoResto> inscricaoRestoPagarProcessados) {
        this.inscricaoRestoPagarProcessados = inscricaoRestoPagarProcessados;
    }

    public List<AberturaInscricaoResto> getInscricaoRestoPagarNaoProcessados() {
        return inscricaoRestoPagarNaoProcessados;
    }

    public void setInscricaoRestoPagarNaoProcessados(List<AberturaInscricaoResto> inscricaoRestoPagarNaoProcessados) {
        this.inscricaoRestoPagarNaoProcessados = inscricaoRestoPagarNaoProcessados;
    }

    public List<TransporteDeSaldoAbertura> getTransferenciaResultadoPublico() {
        return transferenciaResultadoPublico;
    }

    public void setTransferenciaResultadoPublico(List<TransporteDeSaldoAbertura> transferenciaResultado) {
        this.transferenciaResultadoPublico = transferenciaResultado;
    }

    public List<TransporteDeSaldoAbertura> getTransferenciaAjustesPublico() {
        return transferenciaAjustesPublico;
    }

    public void setTransferenciaAjustesPublico(List<TransporteDeSaldoAbertura> transferenciaAjustes) {
        this.transferenciaAjustesPublico = transferenciaAjustes;
    }

    public List<TransporteDeSaldoAbertura> getTransferenciaResultadoPrivado() {
        return transferenciaResultadoPrivado;
    }

    public void setTransferenciaResultadoPrivado(List<TransporteDeSaldoAbertura> transferenciaResultadoPrivado) {
        this.transferenciaResultadoPrivado = transferenciaResultadoPrivado;
    }

    public List<TransporteDeSaldoAbertura> getTransferenciaAjustesPrivado() {
        return transferenciaAjustesPrivado;
    }

    public void setTransferenciaAjustesPrivado(List<TransporteDeSaldoAbertura> transferenciaAjustesPrivado) {
        this.transferenciaAjustesPrivado = transferenciaAjustesPrivado;
    }

    public List<TransporteSaldoFinanceiro> getTransporteDeSaldoFinanceiro() {
        return transporteDeSaldoFinanceiro;
    }

    public void setTransporteDeSaldoFinanceiro(List<TransporteSaldoFinanceiro> transporteDeSaldoFinanceiro) {
        this.transporteDeSaldoFinanceiro = transporteDeSaldoFinanceiro;
    }

    public List<TransporteCreditoReceber> getTransporteDeSaldoCreditoReceber() {
        return transporteDeSaldoCreditoReceber;
    }

    public void setTransporteDeSaldoCreditoReceber(List<TransporteCreditoReceber> transporteDeSaldoCreditoReceber) {
        this.transporteDeSaldoCreditoReceber = transporteDeSaldoCreditoReceber;
    }

    public List<TransporteDividaAtiva> getTransporteDeSaldoDividaAtiva() {
        return transporteDeSaldoDividaAtiva;
    }

    public void setTransporteDeSaldoDividaAtiva(List<TransporteDividaAtiva> transporteDeSaldoDividaAtiva) {
        this.transporteDeSaldoDividaAtiva = transporteDeSaldoDividaAtiva;
    }

    public List<TransporteExtra> getTransporteDeSaldoExtra() {
        return transporteDeSaldoExtra;
    }

    public void setTransporteDeSaldoExtra(List<TransporteExtra> transporteDeSaldoExtra) {
        this.transporteDeSaldoExtra = transporteDeSaldoExtra;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(dataGeracao) + "(" + exercicio.getAno() + ")" + " - " + descricao;
    }

    public List<ReceitaExtraFechamentoExercicio> getReceitasExtras() {
        return receitasExtras;
    }

    public void setReceitasExtras(List<ReceitaExtraFechamentoExercicio> receitasExtras) {
        this.receitasExtras = receitasExtras;
    }

    public List<TransporteSaldoDividaPublica> getTransporteDeSaldoDividaPublica() {
        return transporteDeSaldoDividaPublica;
    }

    public void setTransporteDeSaldoDividaPublica(List<TransporteSaldoDividaPublica> transporteDeSaldoDividaPublica) {
        this.transporteDeSaldoDividaPublica = transporteDeSaldoDividaPublica;
    }

    public List<TransporteObrigacaoAPagar> getObrigacoesAPagar() {
        return obrigacoesAPagar;
    }

    public void setObrigacoesAPagar(List<TransporteObrigacaoAPagar> obrigacoesAPagar) {
        this.obrigacoesAPagar = obrigacoesAPagar;
    }

    public List<TransporteSaldoContaAuxiliarDetalhada> getTransporteDeSaldoDeContasAuxiliares() {
        return transporteDeSaldoDeContasAuxiliares;
    }

    public void setTransporteDeSaldoDeContasAuxiliares(List<TransporteSaldoContaAuxiliarDetalhada> transporteDeSaldoDeContasAuxiliares) {
        this.transporteDeSaldoDeContasAuxiliares = transporteDeSaldoDeContasAuxiliares;
    }
}
