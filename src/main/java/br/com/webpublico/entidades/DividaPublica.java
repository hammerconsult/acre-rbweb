/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.NaturezaDebito;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@Entity

@Audited
@Etiqueta("Dívida Pública")
@GrupoDiagrama(nome = "Contabil")
public class DividaPublica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private String numero;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data de Homologação")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataHomologacao;
    @Etiqueta("Prazo de Carência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date prazoCarencia;
    @Etiqueta("Prazo de Amortização")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date prazoAmortizacao;
    @Etiqueta("Prazo Total")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date prazoTotal;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa")
    @ManyToOne
    private Pessoa pessoa;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    @Etiqueta("Classe Juros")
    private ClasseCredor classeCredorJuros;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    @Etiqueta("Classe Outros Encargos")
    private ClasseCredor classeCredorOutros;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Valor Original do Contrato (R$)")
    @Monetario
    private BigDecimal valorNominal;
    @Pesquisavel
    @Etiqueta("Número do Protocolo")
    private String numeroProtocolo;
    @Pesquisavel
    @Etiqueta("Número Documento/Contrato/Processo")
    @Tabelavel(campoSelecionado = false)
    private String numeroDocContProc;
    @Pesquisavel
    @Etiqueta("Data de Início")
    @Tabelavel(campoSelecionado = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInicio;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Data Final")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFim;
    private String garantias;
    @Pesquisavel
    @Etiqueta("Taxa de Juros")
    @Tabelavel(campoSelecionado = false)
    private BigDecimal taxaJuros;
    @Etiqueta("Taxa de Juros de Administrçao")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal taxaAdministracao;
    @Etiqueta("Taxa de Juros de Risco")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal taxaRisco;
    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String descricaoDivida;
    @Pesquisavel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Procurador")
    private Pessoa procurador;
    @Pesquisavel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Advogado")
    private Pessoa advogado;
    @Pesquisavel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Tribunal")
    private Tribunal tribunal;
    @Pesquisavel
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Unidade Judiciaria")
    private UnidadeJudiciaria unidadeJudiciaria;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Número do Processo Judicial")
    private String numProcessoJudicial;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Número do Processo de Execução")
    private String numProcessoExecucao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Natureza Débito")
    private NaturezaDebito naturezaDebito;
    @Invisivel
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Tramites")
    private List<TramiteDividaPublica> tramites;
    @Invisivel
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Parcelas")
    private List<ParcelaDividaPublica> parcelas;
    @Invisivel
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Beneficiarios")
    private List<PessoaDividaPublica> beneficiarios;
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Unidades")
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadeDividaPublica> unidades;
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Contas Financeiras")
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubContaDividaPublica> contasFinanceiras;
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Atos Legais")
    private List<DividaPublicaAtoLegal> atosLegais;
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Contas Receitas")
    private List<DividaPublicaContaReceita> contasReceitas;
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Arquivo")
    private List<ArquivoDividaPublica> arquvios;
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Liberação de Recursos")
    private List<LiberacaoRecurso> liberacaoRecursos;
    @Etiqueta("Indicador Econômico Moeda")
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    @Pesquisavel
    private IndicadorEconomico indicadorEconomico;
    @Etiqueta("Indicador Econômico Percentual")
    @ManyToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private IndicadorEconomico indicadorPercentual;
    @Etiqueta("Indicadores Economicos")
    @Tabelavel(campoSelecionado = false)
    @OneToMany(mappedBy = "dividaPublica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DividaPublicaValorIndicadorEconomico> valorIndicadoresEconomicos;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Natureza da Dívida Pública")
    private CategoriaDividaPublica categoriaDividaPublica;
    @Etiqueta("Periodicidade")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Periodicidade periodicidade;
    @Transient
    private Long criadoEm;
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta(value = "Quantidade de Meses")
    private Integer quantidadeMeses;
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta(value = "Valor de Contrapartida (R$)")
    private BigDecimal contraPartida;
    @Pesquisavel
    @Etiqueta("Número Antigo")
    @Tabelavel(campoSelecionado = false)
    private Long numeroAntigo;
    @ManyToOne
    @Etiqueta("Exercício")
    @Tabelavel(campoSelecionado = false)
    private Exercicio exercicio;
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Saldo (R$)")
    private BigDecimal saldo;
    @Etiqueta("Prazo Total")
    private BigDecimal prazoTotalDivida;
    @Etiqueta("Prazo Total")
    private BigDecimal prazoAmortizacaoDivida;
    @Etiqueta("Prazo Total")
    private BigDecimal prazoCarenciaDivida;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Nome da Dívida")
    private String nomeDivida;

    public DividaPublica() {
        criadoEm = System.nanoTime();
        tramites = new ArrayList<TramiteDividaPublica>();
        parcelas = new ArrayList<ParcelaDividaPublica>();
        beneficiarios = new ArrayList<PessoaDividaPublica>();
        unidades = new ArrayList<UnidadeDividaPublica>();
        arquvios = new ArrayList<ArquivoDividaPublica>();
        contasFinanceiras = new ArrayList<SubContaDividaPublica>();
        valorNominal = new BigDecimal(BigInteger.ZERO);
        contraPartida = new BigDecimal(BigInteger.ZERO);
        prazoAmortizacaoDivida = new BigDecimal(BigInteger.ZERO);
        prazoCarenciaDivida = new BigDecimal(BigInteger.ZERO);
        prazoTotalDivida = new BigDecimal(BigInteger.ZERO);
        taxaAdministracao = BigDecimal.ZERO;
        taxaJuros = BigDecimal.ZERO;
        taxaRisco = BigDecimal.ZERO;
        atosLegais = new ArrayList<DividaPublicaAtoLegal>();
        contasReceitas = new ArrayList<DividaPublicaContaReceita>();
        valorIndicadoresEconomicos = new ArrayList<DividaPublicaValorIndicadorEconomico>();
        dataInicio = new Date();
    }

    public Periodicidade getPeriodicidade() {
        return periodicidade;
    }

    public void setPeriodicidade(Periodicidade periodicidade) {
        this.periodicidade = periodicidade;
    }

    public ClasseCredor getClasseCredorPorSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        if (subTipoDespesa != null) {
            if (subTipoDespesa.equals(SubTipoDespesa.VALOR_PRINCIPAL)) {
                return classeCredor;
            } else if (subTipoDespesa.equals(SubTipoDespesa.JUROS)) {
                return classeCredorJuros;
            } else if (subTipoDespesa.equals(SubTipoDespesa.OUTROS_ENCARGOS)) {
                return classeCredorOutros;
            }
        }
        return null;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public CategoriaDividaPublica getCategoriaDividaPublica() {
        return categoriaDividaPublica;
    }

    public void setCategoriaDividaPublica(CategoriaDividaPublica categoriaDividaPublica) {
        this.categoriaDividaPublica = categoriaDividaPublica;
    }

    public Pessoa getAdvogado() {
        return advogado;
    }

    public void setAdvogado(Pessoa advogado) {
        this.advogado = advogado;
    }

    public UnidadeJudiciaria getUnidadeJudiciaria() {
        return unidadeJudiciaria;
    }

    public void setUnidadeJudiciaria(UnidadeJudiciaria unidadeJudiciaria) {
        this.unidadeJudiciaria = unidadeJudiciaria;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataHomologacao() {
        return dataHomologacao;
    }

    public void setDataHomologacao(Date dataHomologacao) {
        this.dataHomologacao = dataHomologacao;
    }

    public String getDescricaoDivida() {
        return descricaoDivida;
    }

    public void setDescricaoDivida(String descricaoDivida) {
        this.descricaoDivida = descricaoDivida;
    }

    public String getNumeroDocContProc() {
        return numeroDocContProc;
    }

    public void setNumeroDocContProc(String numeroDocContProc) {
        this.numeroDocContProc = numeroDocContProc;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public List<TramiteDividaPublica> getTramites() {
        return tramites;
    }

    public void setTramites(List<TramiteDividaPublica> tramites) {
        this.tramites = tramites;
    }

    public BigDecimal getValorNominal() {
        return valorNominal;
    }

    public void setValorNominal(BigDecimal valorNominal) {
        this.valorNominal = valorNominal;
    }

    public Pessoa getProcurador() {
        return procurador;
    }

    public void setProcurador(Pessoa procurador) {
        this.procurador = procurador;
    }

    public List<ParcelaDividaPublica> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ParcelaDividaPublica> parcelas) {
        this.parcelas = parcelas;
    }

    public List<PessoaDividaPublica> getBeneficiarios() {
        return beneficiarios;
    }

    public void setBeneficiarios(List<PessoaDividaPublica> beneficiarios) {
        this.beneficiarios = beneficiarios;
    }

    public NaturezaDebito getNaturezaDebito() {
        return naturezaDebito;
    }

    public void setNaturezaDebito(NaturezaDebito naturezaDebito) {
        this.naturezaDebito = naturezaDebito;
    }

    public String getNumProcessoExecucao() {
        return numProcessoExecucao;
    }

    public void setNumProcessoExecucao(String numProcessoExecucao) {
        this.numProcessoExecucao = numProcessoExecucao;
    }

    public String getNumProcessoJudicial() {
        return numProcessoJudicial;
    }

    public void setNumProcessoJudicial(String numProcessoJudicial) {
        this.numProcessoJudicial = numProcessoJudicial;
    }

    public Tribunal getTribunal() {
        return tribunal;
    }

    public void setTribunal(Tribunal tribunal) {
        this.tribunal = tribunal;
    }

    public List<UnidadeDividaPublica> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeDividaPublica> unidades) {
        this.unidades = unidades;
    }

    public List<SubContaDividaPublica> getContasFinanceiras() {
        return contasFinanceiras;
    }

    public void setContasFinanceiras(List<SubContaDividaPublica> contasFinanceiras) {
        this.contasFinanceiras = contasFinanceiras;
    }

    public NaturezaDividaPublica getNaturezaDividaPublica() {
        if (this.categoriaDividaPublica != null) {
            return this.getCategoriaDividaPublica().getNaturezaDividaPublica();
        }
        return null;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getGarantias() {
        return garantias;
    }

    public void setGarantias(String garantias) {
        this.garantias = garantias;
    }

    public List<DividaPublicaAtoLegal> getAtosLegais() {
        return atosLegais;
    }

    public void setAtosLegais(List<DividaPublicaAtoLegal> atosLegais) {
        this.atosLegais = atosLegais;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<DividaPublicaContaReceita> getContasReceitas() {
        return contasReceitas;
    }

    public void setContasReceitas(List<DividaPublicaContaReceita> contasReceitas) {
        this.contasReceitas = contasReceitas;
    }

    public Integer getQuantidadeMeses() {
        return quantidadeMeses;
    }

    public void setQuantidadeMeses(Integer quantidadeMeses) {
        this.quantidadeMeses = quantidadeMeses;
    }

    public BigDecimal getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(BigDecimal taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public BigDecimal getTaxaAdministracao() {
        return taxaAdministracao;
    }

    public void setTaxaAdministracao(BigDecimal taxaAdministracao) {
        this.taxaAdministracao = taxaAdministracao;
    }

    public BigDecimal getTaxaRisco() {
        return taxaRisco;
    }

    public void setTaxaRisco(BigDecimal taxaRisco) {
        this.taxaRisco = taxaRisco;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public BigDecimal getContraPartida() {
        return contraPartida;
    }

    public void setContraPartida(BigDecimal contraPartida) {
        this.contraPartida = contraPartida;
    }

    public IndicadorEconomico getIndicadorEconomico() {
        return indicadorEconomico;
    }

    public void setIndicadorEconomico(IndicadorEconomico indicadorEconomico) {
        this.indicadorEconomico = indicadorEconomico;
    }

    public List<DividaPublicaValorIndicadorEconomico> getValorIndicadoresEconomicos() {
        return valorIndicadoresEconomicos;
    }

    public void setValorIndicadoresEconomicos(List<DividaPublicaValorIndicadorEconomico> valorIndicadoresEconomicos) {
        this.valorIndicadoresEconomicos = valorIndicadoresEconomicos;
    }

    public Long getNumeroAntigo() {
        return numeroAntigo;
    }

    public void setNumeroAntigo(Long numeroAntigo) {
        this.numeroAntigo = numeroAntigo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public IndicadorEconomico getIndicadorPercentual() {
        return indicadorPercentual;
    }

    public void setIndicadorPercentual(IndicadorEconomico indicadorEconomicoPercentual) {
        this.indicadorPercentual = indicadorEconomicoPercentual;
    }

    public List<ArquivoDividaPublica> getArquvios() {
        return arquvios;
    }

    public void setArquvios(List<ArquivoDividaPublica> arquvios) {
        this.arquvios = arquvios;
    }

    public BigDecimal getPrazoTotalDivida() {
        return prazoTotalDivida;
    }

    public void setPrazoTotalDivida(BigDecimal prazoTotalDivida) {
        this.prazoTotalDivida = prazoTotalDivida;
    }

    public BigDecimal getPrazoAmortizacaoDivida() {
        return prazoAmortizacaoDivida;
    }

    public void setPrazoAmortizacaoDivida(BigDecimal prazoAmortizacaoDivida) {
        this.prazoAmortizacaoDivida = prazoAmortizacaoDivida;
    }

    public BigDecimal getPrazoCarenciaDivida() {
        return prazoCarenciaDivida;
    }

    public void setPrazoCarenciaDivida(BigDecimal prazoCarenciaDivida) {
        this.prazoCarenciaDivida = prazoCarenciaDivida;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Date getPrazoCarencia() {
        return prazoCarencia;
    }

    public void setPrazoCarencia(Date prazoCarencia) {
        this.prazoCarencia = prazoCarencia;
    }

    public Date getPrazoAmortizacao() {
        return prazoAmortizacao;
    }

    public void setPrazoAmortizacao(Date prazoAmortizacao) {
        this.prazoAmortizacao = prazoAmortizacao;
    }

    public Date getPrazoTotal() {
        return prazoTotal;
    }

    public void setPrazoTotal(Date prazoTotal) {
        this.prazoTotal = prazoTotal;
    }

    public List<LiberacaoRecurso> getLiberacaoRecursos() {
        return liberacaoRecursos;
    }

    public void setLiberacaoRecursos(List<LiberacaoRecurso> liberacaoRecursos) {
        this.liberacaoRecursos = liberacaoRecursos;
    }

    public ClasseCredor getClasseCredorOutros() {
        return classeCredorOutros;
    }

    public void setClasseCredorOutros(ClasseCredor classeCredorOutros) {
        this.classeCredorOutros = classeCredorOutros;
    }

    public ClasseCredor getClasseCredorJuros() {
        return classeCredorJuros;
    }

    public void setClasseCredorJuros(ClasseCredor classeCredorJuros) {
        this.classeCredorJuros = classeCredorJuros;
    }

    public String getNomeDivida() {
        return nomeDivida;
    }

    public void setNomeDivida(String nomeDivida) {
        this.nomeDivida = nomeDivida;
    }

    @Override
    public String toString() {
        try {
            return numero + " - " + nomeDivida;
        } catch (Exception ex) {
            return "";
        }
    }

    public String toStringAutoComplete() {
        int tamanho = 70;

        if (categoriaDividaPublica.getNaturezaDividaPublica().equals(NaturezaDividaPublica.OPERACAO_CREDITO)) {
            return numero + " - " + numeroDocContProc + " - " + (descricaoDivida.length() >= tamanho ? descricaoDivida.substring(0, tamanho) + "..." : descricaoDivida) + " " + Util.formataValor(valorNominal);
        }
        if (categoriaDividaPublica.getNaturezaDividaPublica().equals(NaturezaDividaPublica.PRECATORIO)) {
            return numeroDocContProc + " - " + (descricaoDivida.length() >= tamanho ? descricaoDivida.substring(0, tamanho) + "..." : descricaoDivida) + " " + Util.formataValor(valorNominal);
        } else {
            if (numero != null && descricaoDivida != null) {
                return numero + " - " + (descricaoDivida.length() >= tamanho ? descricaoDivida.substring(0, tamanho) + "..." : descricaoDivida) + " " + Util.formataValor(valorNominal);
            }
        }
        return "";
    }

    public String toStringDividaPublica() {
        int tamanho = 400;

        return numero + " - " + (descricaoDivida.length() >= tamanho ? descricaoDivida.substring(0, tamanho) + "..." : descricaoDivida) + Util.formataValor(valorNominal);
    }
}
