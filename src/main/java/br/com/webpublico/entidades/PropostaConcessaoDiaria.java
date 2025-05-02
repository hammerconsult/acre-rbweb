/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Where;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@Etiqueta("Concessão de Diária")
@GrupoDiagrama(nome = "ReservaDeDotacao")
@Audited
@Entity
public class PropostaConcessaoDiaria extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @ManyToOne
    @Etiqueta("Unidade Organizacional Administrativa")
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @ManyToOne
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    private String codigo;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Data")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLancamento;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Proposta")
    private TipoProposta tipoProposta;

    @Obrigatorio
    @ManyToOne
    @Etiqueta("Elemento de Despesa")
    private DespesaORC despesaORC;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Fonte de Recurso")
    private FonteDespesaORC fonteDespesaORC;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Pessoa")
    private Pessoa pessoaFisica;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    private ClasseCredor classeCredor;

    @Etiqueta("Conta Bancária")
    @ManyToOne
    @Obrigatorio
    private ContaCorrenteBancPessoa contaCorrenteBanc;

    @Etiqueta("Base Legal")
    @Obrigatorio
    @ManyToOne
    private ConfiguracaoDiaria configuracaoDiaria;

    @Etiqueta("Classe de Diária")
    @Obrigatorio
    @ManyToOne
    private ClasseDiaria classeDiaria;

    @Obrigatorio
    @Etiqueta("Tipo de Viagem")
    @Enumerated(EnumType.STRING)
    private TipoViagem tipoViagem;

    @Etiqueta("Objetivo")
    private String objetivo;

    @Etiqueta("Observação")
    private String observacao;

    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Etiqueta("Situação")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private SituacaoDiaria situacao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação da Diária")
    private SituacaoPropostaConcessaoDiaria situacaoDiaria;

    /**
     * Referente ao relatório de viagem
     */
    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date inicioExec;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date fimExec;
    @Etiqueta("Hora de Saída")
    private String horaSaidaExecutado;
    @Etiqueta("Minuto de Saída")
    private String minutoSaidaExecutado;
    @Etiqueta("Hora de Chegada")
    private String horaChegadaExecutado;
    @Etiqueta("Minuto de Chegada")
    private String minutoChegadaExecutado;
    @Etiqueta("Justificativa")
    private String justificativa;
    @Etiqueta("Descrição das atividades")
    private String descricaoAtividades;
    @Etiqueta("Documentos Anexados")
    private String documentosAnexados;
    @Etiqueta("Etinerario Executado")
    private String etinerarioExec;
    @Invisivel
    @OneToMany(mappedBy = "propostaConcessaoDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiariaPrestacaoConta> diariaPrestacaoContas;

    @ManyToOne
    private Arquivo declaracao;
    @ManyToOne
    private Arquivo qualificacaoColaborador;

    /**
     * Deferir diária
     */
    @ManyToOne
    @Etiqueta("Responsável")
    private Pessoa responsavel;
    @Invisivel
    @OneToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;

    @Etiqueta("Aprovado")
    private Boolean aprovado;
    @Etiqueta("Valor Contabilizado")
    private BigDecimal valorContabilizado;
    private Boolean empenhado;
    private String razao;

    /*
     *Dados da Passagem
     * */
    private Boolean compradaPeloMunicipio;
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Fornecedor")
    private Pessoa fornecedor;
    @Etiqueta("Número do Contrato")
    @Pesquisavel
    private String numeroContrato;
    @OneToMany(mappedBy = "propostaConcessaoDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "despesaCusteadaTerceiro = 0")
    private List<DiariaArquivo> arquivos;

    @OneToMany(mappedBy = "propostaConcessaoDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ViagemDiaria> viagens;
    @OneToMany(mappedBy = "propostaConcessaoDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "despesaCusteadaTerceiro = 1")
    private List<DiariaArquivo> arquivosCusteadosTeceiros;

    @Enumerated(EnumType.STRING)
    private TipoDespesaCusteadaTerceiro tipoDespesaCusteadaTerceiro;
    private Boolean indenizacaoComLocomocao;
    @Etiqueta("Valor da Passagem")
    @Monetario
    @Positivo
    private BigDecimal valorPassagem;

    @ManyToOne
    @Etiqueta("Assessor Técnico de Autoridade Acompanhada")
    private PropostaConcessaoDiaria propostaConcessaoDiaria;
    @Version
    private Long versao;
    @OneToMany(mappedBy = "propostaConcessaoDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesdobramentoPropostaConcessao> desdobramentos;

    public PropostaConcessaoDiaria() {
        diariaPrestacaoContas = Lists.newArrayList();
        valor = BigDecimal.ZERO;
        aprovado = false;
        dataLancamento = new Date();
        valorContabilizado = BigDecimal.ZERO;
        situacaoDiaria = SituacaoPropostaConcessaoDiaria.A_COMPROVAR;
        situacao = SituacaoDiaria.ABERTO;
        arquivos = Lists.newArrayList();
        arquivosCusteadosTeceiros = Lists.newArrayList();
        valorPassagem = BigDecimal.ZERO;
        viagens = Lists.newArrayList();
        horaSaidaExecutado = "";
        minutoSaidaExecutado = "";
        horaChegadaExecutado = "";
        minutoChegadaExecutado = "";
        desdobramentos = Lists.newArrayList();
        compradaPeloMunicipio = Boolean.TRUE;
    }

    public BigDecimal getValorContabilizado() {
        return valorContabilizado;
    }

    public void setValorContabilizado(BigDecimal valorContabilizado) {
        this.valorContabilizado = valorContabilizado;
    }

    public String getDocumentosAnexados() {
        return documentosAnexados;
    }

    public void setDocumentosAnexados(String documentosAnexados) {
        this.documentosAnexados = documentosAnexados;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public boolean isAprovado() {
        return aprovado;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public ContaCorrenteBancPessoa getContaCorrenteBanc() {
        return contaCorrenteBanc;
    }

    public void setContaCorrenteBanc(ContaCorrenteBancPessoa contaCorrenteBanc) {
        this.contaCorrenteBanc = contaCorrenteBanc;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Pessoa getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(Pessoa pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public ClasseDiaria getClasseDiaria() {
        return classeDiaria;
    }

    public void setClasseDiaria(ClasseDiaria classeDiaria) {
        this.classeDiaria = classeDiaria;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Boolean getAprovado() {
        return aprovado == null ? Boolean.FALSE : aprovado;
    }

    public void setAprovado(Boolean aprovado) {
        this.aprovado = aprovado;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<DiariaPrestacaoConta> getDiariaPrestacaoContas() {
        return diariaPrestacaoContas;
    }

    public void setDiariaPrestacaoContas(List<DiariaPrestacaoConta> diariaPrestacaoContas) {
        this.diariaPrestacaoContas = diariaPrestacaoContas;
    }

    public String getEtinerarioExec() {
        return etinerarioExec;
    }

    public void setEtinerarioExec(String etinerarioExec) {
        this.etinerarioExec = etinerarioExec;
    }

    public Date getFimExec() {
        return fimExec;
    }

    public void setFimExec(Date fimExec) {
        this.fimExec = fimExec;
    }

    public Date getInicioExec() {
        return inicioExec;
    }

    public void setInicioExec(Date inicioExec) {
        this.inicioExec = inicioExec;
    }

    public String getDescricaoAtividades() {
        return descricaoAtividades;
    }

    public void setDescricaoAtividades(String descricaoAtividades) {
        this.descricaoAtividades = descricaoAtividades;
    }

    public TipoProposta getTipoProposta() {
        return tipoProposta;
    }

    public void setTipoProposta(TipoProposta tipoProposta) {
        this.tipoProposta = tipoProposta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public SituacaoPropostaConcessaoDiaria getSituacaoDiaria() {
        return situacaoDiaria;
    }

    public void setSituacaoDiaria(SituacaoPropostaConcessaoDiaria situacaoDiaria) {
        this.situacaoDiaria = situacaoDiaria;
    }

    public Boolean getEmpenhado() {
        return empenhado == null ? Boolean.FALSE : empenhado;
    }

    public void setEmpenhado(Boolean empenhado) {
        this.empenhado = empenhado;
    }

    public SituacaoDiaria getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDiaria situacao) {
        this.situacao = situacao;
    }

    public Pessoa getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public String getRazao() {
        return razao;
    }

    public void setRazao(String razao) {
        this.razao = razao;
    }

    public Arquivo getDeclaracao() {
        return declaracao;
    }

    public void setDeclaracao(Arquivo declaracao) {
        this.declaracao = declaracao;
    }

    public Arquivo getQualificacaoColaborador() {
        return qualificacaoColaborador;
    }

    public void setQualificacaoColaborador(Arquivo qualificacaoColaborador) {
        this.qualificacaoColaborador = qualificacaoColaborador;
    }

    public ConfiguracaoDiaria getConfiguracaoDiaria() {
        return configuracaoDiaria;
    }

    public void setConfiguracaoDiaria(ConfiguracaoDiaria configuracaoDiaria) {
        this.configuracaoDiaria = configuracaoDiaria;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public List<DiariaArquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<DiariaArquivo> arquivos) {
        this.arquivos = arquivos;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.pessoaFisica + " - " + Util.formataValor(valor) + " - " + DataUtil.getDataFormatada(dataLancamento);
    }

    public TipoViagem getTipoViagem() {
        return tipoViagem;
    }

    public void setTipoViagem(TipoViagem tipoViagem) {
        this.tipoViagem = tipoViagem;
    }

    public List<DiariaArquivo> getArquivosCusteadosTeceiros() {
        return arquivosCusteadosTeceiros;
    }

    public void setArquivosCusteadosTeceiros(List<DiariaArquivo> arquivosCusteadosTeceiros) {
        this.arquivosCusteadosTeceiros = arquivosCusteadosTeceiros;
    }

    public TipoDespesaCusteadaTerceiro getTipoDespesaCusteadaTerceiro() {
        return tipoDespesaCusteadaTerceiro;
    }

    public void setTipoDespesaCusteadaTerceiro(TipoDespesaCusteadaTerceiro tipoDespesaCusteadaTerceiro) {
        this.tipoDespesaCusteadaTerceiro = tipoDespesaCusteadaTerceiro;
    }

    public Boolean getIndenizacaoComLocomocao() {
        return indenizacaoComLocomocao;
    }

    public void setIndenizacaoComLocomocao(Boolean indenizacaoComLocomocao) {
        this.indenizacaoComLocomocao = indenizacaoComLocomocao;
    }

    public BigDecimal getValorPassagem() {
        return valorPassagem;
    }

    public void setValorPassagem(BigDecimal valorPassagem) {
        this.valorPassagem = valorPassagem;
    }

    public List<ViagemDiaria> getViagens() {
        return viagens;
    }

    public void setViagens(List<ViagemDiaria> viagens) {
        this.viagens = viagens;
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getHoraSaidaExecutado() {
        return horaSaidaExecutado == null ? "" : horaSaidaExecutado;
    }

    public void setHoraSaidaExecutado(String horaSaidaExecutado) {
        this.horaSaidaExecutado = horaSaidaExecutado;
    }

    public String getMinutoSaidaExecutado() {
        return minutoSaidaExecutado == null ? "" : minutoSaidaExecutado;
    }

    public void setMinutoSaidaExecutado(String minutoSaidaExecutado) {
        this.minutoSaidaExecutado = minutoSaidaExecutado;
    }

    public String getHoraChegadaExecutado() {
        return horaChegadaExecutado == null ? "" : horaChegadaExecutado;
    }

    public void setHoraChegadaExecutado(String horaChegadaExecutado) {
        this.horaChegadaExecutado = horaChegadaExecutado;
    }

    public String getMinutoChegadaExecutado() {
        return minutoChegadaExecutado == null ? "" : minutoChegadaExecutado;
    }

    public void setMinutoChegadaExecutado(String minutoChegadaExecutado) {
        this.minutoChegadaExecutado = minutoChegadaExecutado;
    }

    public List<DesdobramentoPropostaConcessao> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<DesdobramentoPropostaConcessao> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public Boolean getCompradaPeloMunicipio() {
        return compradaPeloMunicipio != null ? compradaPeloMunicipio : Boolean.FALSE;
    }

    public void setCompradaPeloMunicipio(Boolean compradaPeloMunicipio) {
        this.compradaPeloMunicipio = compradaPeloMunicipio;
    }

    public BigDecimal getValorTotalDesdobramentos() {
        BigDecimal valor = BigDecimal.ZERO;
        for (DesdobramentoPropostaConcessao desdobramento : desdobramentos) {
            valor = valor.add(desdobramento.getValor());
        }
        return valor;
    }

    public BigDecimal getSaldoTotalDesdobramentos() {
        BigDecimal valor = BigDecimal.ZERO;
        for (DesdobramentoPropostaConcessao desdobramento : desdobramentos) {
            valor = valor.add(desdobramento.getSaldo());
        }
        return valor;
    }
}
