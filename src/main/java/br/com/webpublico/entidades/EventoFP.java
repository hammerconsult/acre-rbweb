/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.administracaodepagamento.BloqueioFuncionalidade;
import br.com.webpublico.entidades.rh.administracaodepagamento.EventoFPTipoFolha;
import br.com.webpublico.entidades.rh.esocial.EventoFPEmpregador;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoClassificacaoConsignacao;
import br.com.webpublico.enums.rh.TipoLancamentoFPSimplificado;
import br.com.webpublico.enums.rh.TipoNaturezaRefenciaCalculo;
import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.enums.rh.previdencia.TipoContribuicaoBBPrev;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.interfaces.PrefixoNomeFormulasConstantesScriptFP;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Evento Folha de Pagamento")
public class EventoFP implements Serializable, IHistoricoEsocial {

    public static final String FORMULA_VALOR_PADRAO = "return 0;";
    public static final String REGRA_VALOR_PADRAO = "return false;";
    public static final String VALOR_INTEGRAL_VALOR_PADRAO = "return 0;";
    public static final String REFERENCIA_VALOR_PADRAO = "return 0;";
    public static final String BASE_FORMULA_VALOR_PADRAO = "return 0;";
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Código")
    @Pesquisavel
    /**
     * Código utilizado nas impressões dos documentos.
     *
     */
    private String codigo;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição Reduzida")
    private String descricaoReduzida;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Evento")
    @Enumerated(EnumType.STRING)
    private TipoEventoFP tipoEventoFP;
    @Obrigatorio
    @Etiqueta("Regra")
    /**
     * Script que determinará se o evento será utilizado ou não (deve retornar
     * true ou false).
     *
     */
    private String regra;
    @Obrigatorio
    @Etiqueta("Fórmula")
    /**
     * Script que será executado caso a regra seja true e que retornará o valor
     * calculado para este evento.
     *
     */
    private String formula;

    @Transient
    private Integer codigoInt;
    /**
     * Atributo utilizado para calcular a referência do evento na geração das
     * fichas financeiras para calcular a quantidade de referência do evento (30
     * horas extra, 25 dias trabalhados, etc...)
     */
    @Etiqueta("Referência")
    private String referencia;
    /**
     * Atributo utilizado na visualização da referência calculada a partir deste
     * evento. Por exemplo: 30 dias, 20 %, 50 horas. Dias, % e horas são os
     * complementos da referência.
     */
    @Pesquisavel
    @Etiqueta("Complemento Referência")
    private String complementoReferencia;
    /**
     * Atributo utilizado para determinar o valor total do evento, o qual NÃO
     * será necessariamente vinculado a ficha financeira (pode haver descontos,
     * por exemplo, que reduzem o valor integral).
     */
    @Etiqueta("Fórmula Valor Integral")
    private String formulaValorIntegral;
    @Column(name = "automatico")
    @Etiqueta("Automático")
    private Boolean automatico;
    @Pesquisavel
    @Etiqueta("Unidade de Referência")
    private String unidadeReferencia;
    @Enumerated(EnumType.STRING)
    private TipoExecucaoEP tipoExecucaoEP;
    @Etiqueta("Tipo de Base")
    @Enumerated(EnumType.STRING)
    private TipoBase tipoBase;
    @Etiqueta("Valor Base de Cálculo")
    private String valorBaseDeCalculo;
    @OneToMany(mappedBy = "eventoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemBaseFP> itensBasesFPs;
    @Transient
    private List<BaseFP> baseFPs;
    @Etiqueta("Ativo")
    @Pesquisavel
    private Boolean ativo;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Lançamento FP")
    private TipoLancamentoFP tipoLancamentoFP;
    @Etiqueta("Quantificação")
    private BigDecimal quantificacao;
    @Etiqueta("Cálculo retroativo")
    private Boolean calculoRetroativo;
    @Pesquisavel
    @Etiqueta("Verba Fixa")
    private Boolean verbaFixa;
    @Etiqueta("Não Permite Lançamento")
    private Boolean naoPermiteLancamento;
    private Boolean apuracaoIR;
    @Enumerated(EnumType.STRING)
    private TipoCalculo13 tipoCalculo13;
    @Transient
    private Integer ordenacaoTipoEventoFP;
    @Transient
    private Long criadoEm;
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Tipo Base FP")
    private TipoBaseFP tipoBaseFP;
    @ManyToOne
    private ReferenciaFP referenciaFP;
    @Invisivel
    private Boolean estornoFerias = false;
    @Etiqueta("Ordem Processamento")
    @Obrigatorio
    private Integer ordemProcessamento;
    @Etiqueta("Consignação")
    private Boolean consignacao;
    @Etiqueta("Data do Registro")
    @Temporal(TemporalType.DATE)
    private Date dataRegistro;
    @Etiqueta("Data de Alteração")
    @Temporal(TemporalType.DATE)
    private Date dataAlteracao;
    @Etiqueta("Tipo Previdência Folha de Pagamento")
    @ManyToOne
    private TipoPrevidenciaFP tipoPrevidenciaFP;
    @Enumerated(EnumType.STRING)
    private TipoDeConsignacao tipoDeConsignacao;
    @Etiqueta("Bloqueio de Férias")
    @Enumerated(EnumType.STRING)
    private BloqueioFerias bloqueioFerias;
    @Etiqueta("Bloqueio de Licença Prêmio")
    @Enumerated(EnumType.STRING)
    private BloqueioFerias bloqueioLicencaPremio;
    private Boolean proporcionalizaDiasTrab;
    private Boolean arredondarValor;
    @ManyToOne
    private EventoFP eventoFPAgrupador;
    @Enumerated(EnumType.STRING)
    private IdentificacaoEventoFP identificacaoEventoFP;
    private Boolean propMesesTrabalhados;

    @OneToMany(mappedBy = "eventoFP", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoFPEmpregador> itensEventoFPEmpregador;

    @Enumerated(EnumType.STRING)
    private TipoClassificacaoConsignacao tipoClassificacaoConsignacao;

    @ManyToOne
    @Etiqueta("Entidade")
    private Entidade entidade;

    private BigDecimal valorMaximoLancamento;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "eventoFP")
    private List<EventoFPTipoFolha> tiposFolha;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventoFP", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<BloqueioFuncionalidade> bloqueiosFuncionalidade;
    @ManyToOne
    private BaseFP baseFP;
    @Enumerated(EnumType.STRING)
    private TipoLancamentoFPSimplificado tipoLancamentoFPSimplificado;

    @Etiqueta("Buscar Somente em Dezembro")
    private Boolean ultimoAcumuladoEmDezembro;

    @Enumerated(EnumType.STRING)
    private TipoNaturezaRefenciaCalculo naturezaRefenciaCalculo;
    @ManyToOne
    private ClassificacaoVerba classificacaoVerba;

    @Transient
    private Date dataInicioFiltroVerba;
    @Transient
    private Date dataFimFiltroVerba;
    private Boolean controleCargoLotacao;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "eventoFP")
    private List<EventoFPUnidade> eventoFPUnidade;

    @Transient
    private List<EventoESocialDTO> eventosEsocial;

    @Etiqueta("Exibir na ficha financeira")
    private Boolean exibirNaFichaFinanceira;

    @Etiqueta("Verba de Remuneração Principal")
    private Boolean remuneracaoPrincipal;

    private Boolean naoEnviarVerbaSicap;

    @Etiqueta("Exibir na Ficha RPA")
    private Boolean exibirNaFichaRPA;

    @Enumerated
    @Transient
    private TipoOperacaoESocial tipoOperacaoESocial;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Contribuição BBPrev")
    private TipoContribuicaoBBPrev tipoContribuicaoBBPrev;
    private String codigoContribuicaoBBPrev;

    public EventoFP() {
        ativo = true;
        arredondarValor = false;
        proporcionalizaDiasTrab = true;
        valorMaximoLancamento = BigDecimal.ZERO;
        calculoRetroativo = true;
        verbaFixa = Boolean.FALSE;
        naoPermiteLancamento = Boolean.FALSE;
        criadoEm = System.nanoTime();
        tipoCalculo13 = TipoCalculo13.NAO;
        estornoFerias = false;
        consignacao = false;
        regra = "return false;";
        formula = "return 0;";
        formulaValorIntegral = "return 0;";
        referencia = "return 0;";
        valorBaseDeCalculo = "return 0;";
        baseFPs = new ArrayList<>();
        propMesesTrabalhados = false;
        tiposFolha = Lists.newLinkedList();
        bloqueiosFuncionalidade = Lists.newLinkedList();
        eventoFPUnidade = Lists.newArrayList();
        itensEventoFPEmpregador = Lists.newArrayList();
        exibirNaFichaFinanceira = false;
        remuneracaoPrincipal = false;
    }


    public EventoFP(String descricao, TipoEventoFP tipoEventoFP) {
        this.descricao = descricao;
        this.tipoEventoFP = tipoEventoFP;
    }

    public EventoFP(Long id, String codigo, String descricao, TipoEventoFP tipoEventoFP) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.tipoEventoFP = tipoEventoFP;
    }

    public List<EventoFPTipoFolha> getTiposFolha() {
        return tiposFolha;
    }

    public void setTiposFolha(List<EventoFPTipoFolha> tiposFolha) {
        this.tiposFolha = tiposFolha;
    }

    public Boolean getPropMesesTrabalhados() {
        return propMesesTrabalhados == null ? false : propMesesTrabalhados;
    }

    public void setPropMesesTrabalhados(Boolean propMesesTrabalhados) {
        this.propMesesTrabalhados = propMesesTrabalhados;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public TipoLancamentoFPSimplificado getTipoLancamentoFPSimplificado() {
        return tipoLancamentoFPSimplificado;
    }

    public void setTipoLancamentoFPSimplificado(TipoLancamentoFPSimplificado tipoLancamentoFPSimplificado) {
        this.tipoLancamentoFPSimplificado = tipoLancamentoFPSimplificado;
    }

    public BigDecimal getValorMaximoLancamento() {
        return valorMaximoLancamento;
    }

    public void setValorMaximoLancamento(BigDecimal valorMaximoLancamento) {
        this.valorMaximoLancamento = valorMaximoLancamento;
    }

    public EventoFP getEventoFPAgrupador() {
        return eventoFPAgrupador;
    }

    public void setEventoFPAgrupador(EventoFP eventoFPAgrupador) {
        this.eventoFPAgrupador = eventoFPAgrupador;
    }

    public Boolean getArredondarValor() {
        return arredondarValor == null ? false : arredondarValor;
    }

    public void setArredondarValor(Boolean arredondarValor) {
        this.arredondarValor = arredondarValor;
    }

    public Boolean getProporcionalizaDiasTrab() {
        return proporcionalizaDiasTrab == null ? false : proporcionalizaDiasTrab;
    }

    public void setProporcionalizaDiasTrab(Boolean proporcionalizaDiasTrab) {
        this.proporcionalizaDiasTrab = proporcionalizaDiasTrab;
    }

    public Date getDataInicioFiltroVerba() {
        return dataInicioFiltroVerba;
    }

    public void setDataInicioFiltroVerba(Date dataInicioFiltroVerba) {
        this.dataInicioFiltroVerba = dataInicioFiltroVerba;
    }

    public Date getDataFimFiltroVerba() {
        return dataFimFiltroVerba;
    }

    public void setDataFimFiltroVerba(Date dataFimFiltroVerba) {
        this.dataFimFiltroVerba = dataFimFiltroVerba;
    }

    public List<BaseFP> getBaseFPs() {
        return baseFPs;
    }

    public void setBaseFPs(List<BaseFP> baseFPs) {
        this.baseFPs = baseFPs;
    }

    public TipoDeConsignacao getTipoDeConsignacao() {
        return tipoDeConsignacao;
    }

    public void setTipoDeConsignacao(TipoDeConsignacao tipoDeConsignacao) {
        this.tipoDeConsignacao = tipoDeConsignacao;
    }

    public Integer getOrdemProcessamento() {
        return ordemProcessamento;
    }

    public void setOrdemProcessamento(Integer ordemProcessamento) {
        this.ordemProcessamento = ordemProcessamento;
    }

    public Boolean getConsignacao() {
        if (consignacao == null) return false;
        return consignacao;
    }

    public void setConsignacao(Boolean consignacao) {
        this.consignacao = consignacao;
    }

    public IdentificacaoEventoFP getIdentificacaoEventoFP() {
        return identificacaoEventoFP;
    }

    public void setIdentificacaoEventoFP(IdentificacaoEventoFP identificacaoEventoFP) {
        this.identificacaoEventoFP = identificacaoEventoFP;
    }

    public List<EventoFPUnidade> getEventoFPUnidade() {
        return eventoFPUnidade;
    }

    public void setEventoFPUnidade(List<EventoFPUnidade> eventoFPUnidade) {
        this.eventoFPUnidade = eventoFPUnidade;
    }

    public Boolean getEstornoFerias() {
        return estornoFerias;
    }

    public void setEstornoFerias(Boolean estornoFerias) {
        this.estornoFerias = estornoFerias;
    }

    public TipoCalculo13 getTipoCalculo13() {
        return tipoCalculo13;
    }

    public void setTipoCalculo13(TipoCalculo13 tipoCalculo13) {
        this.tipoCalculo13 = tipoCalculo13;
    }

    public String getUnidadeReferencia() {
        return unidadeReferencia;
    }

    public void setUnidadeReferencia(String unidadeReferencia) {
        this.unidadeReferencia = unidadeReferencia;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoBase getTipoBase() {
        return tipoBase;
    }

    public void setTipoBase(TipoBase tipoBase) {
        this.tipoBase = tipoBase;
    }

    public String getDescricaoReduzida() {
        return descricaoReduzida;
    }

    public void setDescricaoReduzida(String descricaoReduzida) {
        this.descricaoReduzida = descricaoReduzida;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoEventoFP getTipoEventoFP() {
        return tipoEventoFP;
    }

    public void setTipoEventoFP(TipoEventoFP tipoEventoFP) {
        this.tipoEventoFP = tipoEventoFP;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Boolean getUltimoAcumuladoEmDezembro() {
        return ultimoAcumuladoEmDezembro == null ? false : ultimoAcumuladoEmDezembro;
    }

    public void setUltimoAcumuladoEmDezembro(Boolean ultimoAcumuladoEmDezembro) {
        this.ultimoAcumuladoEmDezembro = ultimoAcumuladoEmDezembro;
    }

    public Boolean getControleCargoLotacao() {
        return controleCargoLotacao != null ? controleCargoLotacao : false;
    }

    public void setControleCargoLotacao(Boolean controleCargoLotacao) {
        this.controleCargoLotacao = controleCargoLotacao;
    }

    public String getFormulaCalculo() {
        if (formula == null) {
            return FORMULA_VALOR_PADRAO;
        }
        if (formula.equals("")) {
            return FORMULA_VALOR_PADRAO;
        }
        return formula;
    }

    public String getRegraCalculo() {
        if (regra == null) {
            return REGRA_VALOR_PADRAO;
        }
        if (regra.equals("")) {
            return REGRA_VALOR_PADRAO;
        }
        return regra;
    }

    public String getReferenciaCalculo() {
        if (referencia == null) {
            return REFERENCIA_VALOR_PADRAO;
        }
        if (referencia.equals("")) {
            return REFERENCIA_VALOR_PADRAO;
        }
        return referencia;
    }

    public String getFormulaValorIntegralCalculo() {
        if (formulaValorIntegral == null) {
            return VALOR_INTEGRAL_VALOR_PADRAO;
        }
        if (formulaValorIntegral.equals("")) {
            return VALOR_INTEGRAL_VALOR_PADRAO;
        }
        return formulaValorIntegral;
    }

    public String getBaseFormulaCalculo() {
        if (valorBaseDeCalculo == null) {
            return BASE_FORMULA_VALOR_PADRAO;
        }
        if (valorBaseDeCalculo.equals("")) {
            return BASE_FORMULA_VALOR_PADRAO;
        }
        return valorBaseDeCalculo;
    }

    public String getRegra() {
        return regra;
    }

    public void setRegra(String regra) {
        this.regra = regra;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCodigoInt() {
        return Integer.parseInt(codigo);
    }

    public void setCodigoInt(Integer codigoInt) {
        this.codigoInt = codigoInt;
    }

    public String getComplementoReferencia() {
        return complementoReferencia;
    }

    public void setComplementoReferencia(String complementoReferencia) {
        this.complementoReferencia = complementoReferencia;
    }

    public String getFormulaValorIntegral() {
        return formulaValorIntegral;
    }

    public void setFormulaValorIntegral(String formulaValorIntegral) {
        this.formulaValorIntegral = formulaValorIntegral;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public TipoExecucaoEP getTipoExecucaoEP() {
        return tipoExecucaoEP;
    }

    public void setTipoExecucaoEP(TipoExecucaoEP tipoExecucaoEP) {
        this.tipoExecucaoEP = tipoExecucaoEP;
    }

    public String getValorBaseDeCalculo() {
        return valorBaseDeCalculo;
    }

    public void setValorBaseDeCalculo(String valorBaseDeCalculo) {
        this.valorBaseDeCalculo = valorBaseDeCalculo;
    }

    public List<ItemBaseFP> getItensBasesFPs() {
        return itensBasesFPs;
    }

    public void setItensBasesFPs(List<ItemBaseFP> itensBasesFPs) {
        this.itensBasesFPs = itensBasesFPs;
    }

    public Boolean getAtivo() {
        return ativo != null ? ativo : false;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getCalculoRetroativo() {
        if (calculoRetroativo == null) return false;
        return calculoRetroativo;
    }

    public void setCalculoRetroativo(Boolean calculoRetroativo) {
        this.calculoRetroativo = calculoRetroativo;
    }

    public Boolean getVerbaFixa() {
        return verbaFixa != null ? verbaFixa : Boolean.FALSE;
    }

    public void setVerbaFixa(Boolean verbaFixa) {
        this.verbaFixa = verbaFixa;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoBaseFP getTipoBaseFP() {
        return tipoBaseFP;
    }

    public void setTipoBaseFP(TipoBaseFP tipoBaseFP) {
        this.tipoBaseFP = tipoBaseFP;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }

    public BloqueioFerias getBloqueioFerias() {
        return bloqueioFerias;
    }

    public void setBloqueioFerias(BloqueioFerias bloqueioFerias) {
        this.bloqueioFerias = bloqueioFerias;
    }

    public BloqueioFerias getBloqueioLicencaPremio() {
        return bloqueioLicencaPremio;
    }

    public void setBloqueioLicencaPremio(BloqueioFerias bloqueioLicencaPremio) {
        this.bloqueioLicencaPremio = bloqueioLicencaPremio;
    }

    public List<BloqueioFuncionalidade> getBloqueiosFuncionalidade() {
        return bloqueiosFuncionalidade;
    }

    public void setBloqueiosFuncionalidade(List<BloqueioFuncionalidade> bloqueiosFuncionalidade) {
        this.bloqueiosFuncionalidade = bloqueiosFuncionalidade;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        String s = null;
        if (codigo != null && !codigo.isEmpty()) {
            s = codigo;
        }
        if (descricao != null && !descricao.isEmpty()) {
            s += " - " + descricao;
        }
        if (tipoEventoFP != null) {
            s += " - " + tipoEventoFP;
        }
        return s;
    }

    public ClassificacaoVerba getClassificacaoVerba() {
        return classificacaoVerba;
    }

    public void setClassificacaoVerba(ClassificacaoVerba classificacaoVerba) {
        this.classificacaoVerba = classificacaoVerba;
    }

    public String nomeFuncaoRegra() {
        return PrefixoNomeFormulasConstantesScriptFP.PREFIXO_NOME_REGRA + codigo;
    }

    public String nomeFuncaoFormula() {
        return PrefixoNomeFormulasConstantesScriptFP.PREFIXO_NOME_FORMULA + codigo;
    }

    public String nomeReferencia() {
        return PrefixoNomeFormulasConstantesScriptFP.PREFIXO_NOME_REFERENCIA + codigo;
    }

    public String nomeValorIntegral() {
        return PrefixoNomeFormulasConstantesScriptFP.PREFIXO_NOME_VALOR_INTEGRAL + codigo;
    }

    public String nomeValorBaseDeCalculo() {
        return PrefixoNomeFormulasConstantesScriptFP.PREFIXO_NOME_VALOR_BASE_DE_CALCULO + codigo;
    }

    public Boolean getAutomatico() {
        return automatico != null ? automatico : false;
    }

    public void setAutomatico(Boolean automatico) {
        this.automatico = automatico;
    }

    public BigDecimal getQuantificacao() {
        return quantificacao;
    }

    public void setQuantificacao(BigDecimal quantificacao) {
        this.quantificacao = quantificacao;
    }

    public TipoLancamentoFP getTipoLancamentoFP() {
        return tipoLancamentoFP;
    }

    public void setTipoLancamentoFP(TipoLancamentoFP tipoLancamentoFP) {
        this.tipoLancamentoFP = tipoLancamentoFP;
    }

    public Boolean getNaoPermiteLancamento() {
        return naoPermiteLancamento;
    }

    public void setNaoPermiteLancamento(Boolean naoPermiteLancamento) {
        this.naoPermiteLancamento = naoPermiteLancamento;
    }

    public ReferenciaFP getReferenciaFP() {
        return referenciaFP;
    }

    public void setReferenciaFP(ReferenciaFP referenciaFP) {
        this.referenciaFP = referenciaFP;
    }

    public TipoNaturezaRefenciaCalculo getNaturezaRefenciaCalculo() {
        return naturezaRefenciaCalculo;
    }

    public void setNaturezaRefenciaCalculo(TipoNaturezaRefenciaCalculo naturezaRefenciaCalculo) {
        this.naturezaRefenciaCalculo = naturezaRefenciaCalculo;
    }

    public TipoClassificacaoConsignacao getTipoClassificacaoConsignacao() {
        return tipoClassificacaoConsignacao;
    }

    public void setTipoClassificacaoConsignacao(TipoClassificacaoConsignacao tipoClassificacaoConsignacao) {
        this.tipoClassificacaoConsignacao = tipoClassificacaoConsignacao;
    }

    public List<EventoESocialDTO> getEventosEsocial() {
        return eventosEsocial;
    }

    public void setEventosEsocial(List<EventoESocialDTO> eventosEsocial) {
        this.eventosEsocial = eventosEsocial;
    }

    public Boolean getExibirNaFichaFinanceira() {
        return exibirNaFichaFinanceira;
    }

    public void setExibirNaFichaFinanceira(Boolean exibirNaFichaFinanceira) {
        this.exibirNaFichaFinanceira = exibirNaFichaFinanceira;
    }

    public List<EventoFPEmpregador> getItensEventoFPEmpregador() {
        return itensEventoFPEmpregador;
    }

    public void setItensEventoFPEmpregador(List<EventoFPEmpregador> itensEventoFPEmpregador) {
        this.itensEventoFPEmpregador = itensEventoFPEmpregador;
    }

    public Boolean getApuracaoIR() {
        return apuracaoIR != null ? apuracaoIR : false;
    }

    public void setApuracaoIR(Boolean apuracaoIR) {
        this.apuracaoIR = apuracaoIR;
    }

    public Integer getOrdenacaoTipoEventoFP() {
        if (tipoEventoFP == TipoEventoFP.VANTAGEM) {
            return 1;
        } else if (tipoEventoFP == TipoEventoFP.DESCONTO) {
            return 2;
        } else if (tipoEventoFP == TipoEventoFP.INFORMATIVO) {
            return 3;
        }
        return 0;
    }

    public Boolean isVantagem() {
        if (TipoEventoFP.VANTAGEM.equals(tipoEventoFP)) {
            return true;
        }
        return false;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Boolean getRemuneracaoPrincipal() {
        return remuneracaoPrincipal == null ? false : remuneracaoPrincipal;
    }

    public void setRemuneracaoPrincipal(Boolean remuneracaoPrincipal) {
        this.remuneracaoPrincipal = remuneracaoPrincipal;
    }

    public Boolean getNaoEnviarVerbaSicap() {
        return naoEnviarVerbaSicap == null ? false : naoEnviarVerbaSicap;
    }

    public void setNaoEnviarVerbaSicap(Boolean naoEnviarVerbaSicap) {
        this.naoEnviarVerbaSicap = naoEnviarVerbaSicap;
    }

    public Boolean getExibirNaFichaRPA() {
        return exibirNaFichaRPA != null ? exibirNaFichaRPA : false;
    }

    public void setExibirNaFichaRPA(Boolean exibirNaFichaRPA) {
        this.exibirNaFichaRPA = exibirNaFichaRPA;
    }

    public TipoOperacaoESocial getTipoOperacaoESocial() {
        return tipoOperacaoESocial;
    }

    public void setTipoOperacaoESocial(TipoOperacaoESocial tipoOperacaoESocial) {
        this.tipoOperacaoESocial = tipoOperacaoESocial;
    }

    public TipoContribuicaoBBPrev getTipoContribuicaoBBPrev() {
        return tipoContribuicaoBBPrev;
    }

    public void setTipoContribuicaoBBPrev(TipoContribuicaoBBPrev tipoContribuicaoBBPrev) {
        this.tipoContribuicaoBBPrev = tipoContribuicaoBBPrev;
    }

    public String getCodigoContribuicaoBBPrev() {
        return codigoContribuicaoBBPrev;
    }

    public void setCodigoContribuicaoBBPrev(String codigoContribuicaoBBPrev) {
        this.codigoContribuicaoBBPrev = codigoContribuicaoBBPrev;
    }

    @Override
    public String getDescricaoCompleta() {
        return this.toString();
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }

    public String getCodigoAndDescricao() {
        String evento = null;
        if (codigo != null && !codigo.isEmpty()) {
            evento = codigo;
        }
        if (descricao != null && !descricao.isEmpty()) {
            evento += " - " + descricao;
        }
        return evento;
    }

    public String toStringAutoComplete() {
        String retorno = toString();
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }
}
