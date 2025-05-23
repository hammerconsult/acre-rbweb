/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.interfaces.ObjetoLicitatorioContrato;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@Etiqueta("Contrato")
public class Contrato extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    private static final Logger logger = LoggerFactory.getLogger(Contrato.class);
    private static final Integer ANO_TERMO_ATE_2020 = 2020;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número do Contrato")
    @Tabelavel
    private String numeroContrato;

    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Etiqueta("Número do Termo")
    @Tabelavel
    private String numeroTermo;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Ano do Termo")
    private Exercicio exercicioContrato;

    @Etiqueta("Número do Processo")
    @Obrigatorio
    private Integer numeroProcesso;

    @Etiqueta("Ano do Processo")
    @Obrigatorio
    private Integer anoProcesso;

    @Etiqueta("Regime de Execução")
    private String regimeExecucao;

    @Obrigatorio
    @Etiqueta("Objeto")
    private String objeto;

    @Obrigatorio
    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @Etiqueta("Valor Atual do Contrato")
    private BigDecimal valorAtualContrato;

    @Etiqueta("Saldo Atual do Contrato")
    private BigDecimal saldoAtualContrato;

    @Etiqueta("Variação Atual do Contrato")
    private BigDecimal variacaoAtualContrato;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Aprovação")
    private Date dataAprovacao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Deferimento")
    private Date dataDeferimento;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Assinatura")
    private Date dataAssinatura;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Inicio de Vigência")
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Término de Vigência")
    private Date terminoVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Término de Vigência Atual")
    private Date terminoVigenciaAtual;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Execução")
    private Date inicioExecucao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Término de Execução")
    private Date terminoExecucao;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Término de Execução Atual")
    private Date terminoExecucaoAtual;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação do Contrato")
    private SituacaoContrato situacaoContrato;

    @Etiqueta("Observações")
    private String observacoes;

    @Transient
    @Tabelavel
    @Etiqueta("Unidade Administrativa")
    private HierarquiaOrganizacional unidadeAdministrativa;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Contratado")
    private Pessoa contratado;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Responsável Unidade Administrativa")
    private ContratoFP responsavelPrefeitura;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Responsável Unidade Administrativa")
    private PessoaFisica responsavelPrefeituraPf;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Responsável Fornecedor")
    private PessoaFisica responsavelEmpresa;

    @Etiqueta("Tipo de Aquisição")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    private TipoAquisicaoContrato tipoAquisicao;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo do Contrato")
    private TipoContrato tipoContrato;

    @Invisivel
    @Etiqueta("Execuções")
    @OneToMany(mappedBy = "contrato", orphanRemoval = true)
    private List<ExecucaoContrato> execucoes;

    @Invisivel
    @Etiqueta("Ordens de Serviço")
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrdemDeServicoContrato> ordensDeServico;

    @Invisivel
    @Etiqueta("Cauções")
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaucaoContrato> caucoes;

    @Etiqueta("Alterações Contratuais")
    @Transient
    private List<AlteracaoContratual> alteracoesContratuais;

    @Invisivel
    @Etiqueta("Ajustes")
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AjusteContrato> ajustes;

    @Invisivel
    @Etiqueta("Fiscais")
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FiscalContrato> fiscais;

    @Invisivel
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PenalidadeContrato> penalidades;

    @Invisivel
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Itens")
    private List<ItemContrato> itens;

    @Invisivel
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Prepostos")
    private List<ContatoContrato> contatos;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @Invisivel
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Documentos")
    private List<DocumentoContrato> documentos;

    @Invisivel
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Ordens de Compra")
    private List<OrdemDeCompraContrato> ordensDeCompra;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "contrato", orphanRemoval = true)
    @Invisivel
    @Etiqueta("Licitação")
    private ContratoLicitacao contratoLicitacao;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "contrato", orphanRemoval = true)
    @Invisivel
    @Etiqueta("Dispensa Licitação")
    private ContratoDispensaDeLicitacao contratoDispensaDeLicitacao;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "contrato", orphanRemoval = true)
    @Invisivel
    @Etiqueta("Registro de Preço Externo")
    private ContratoRegistroPrecoExterno contratoRegistroPrecoExterno;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "contrato", orphanRemoval = true)
    @Invisivel
    @Etiqueta("Recisão do Contrato")
    private RescisaoContrato rescisaoContrato;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "contrato", orphanRemoval = true)
    @Invisivel
    @Etiqueta("Contratos Vigente")
    private ContratosVigente contratosVigente;

    @Invisivel
    @Etiqueta("Unidades Contrato")
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadeContrato> unidades;

    private String localEntrega;
    @Invisivel
    @Etiqueta("Publicações")
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PublicacaoContrato> publicacoes;

    @Invisivel
    @Etiqueta("Gestores")
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GestorContrato> gestores;

    @Invisivel
    @Etiqueta("Fornecedores")
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FornecedorContrato> fornecedores;

    @Version
    private Long versao;
    private Boolean contratoConcessao;
    private Boolean reprocessado;

    @Etiqueta("Sequencial Criado pelo PNCP ao Realizar o Envio do Contrato")
    @Tabelavel(campoSelecionado = false)
    private String sequencialIdPncp;

    @Etiqueta("Id Criado pelo PNCP ao Realizar o Envio do Contrato")
    @Tabelavel(campoSelecionado = false)
    private String idPncp;

    public Contrato() {
        this.valorTotal = BigDecimal.ZERO;
        this.valorAtualContrato = BigDecimal.ZERO;
        this.saldoAtualContrato = BigDecimal.ZERO;
        this.variacaoAtualContrato = BigDecimal.ZERO;
        this.dataLancamento = new Date();
        this.unidades = Lists.newArrayList();
        this.caucoes = Lists.newArrayList();
        this.execucoes = Lists.newArrayList();
        this.alteracoesContratuais = Lists.newArrayList();
        publicacoes = Lists.newArrayList();
        itens = Lists.newArrayList();
        gestores = Lists.newArrayList();
        fiscais = Lists.newArrayList();
        fornecedores = Lists.newArrayList();
        reprocessado = false;
    }

    public Boolean getReprocessado() {
        return reprocessado;
    }

    public void setReprocessado(Boolean reprocessado) {
        this.reprocessado = reprocessado;
    }

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
    }

    public String getIdPncp() {
        return idPncp;
    }

    public void setIdPncp(String idPncp) {
        this.idPncp = idPncp;
    }

    public Boolean getContratoConcessao() {
        return contratoConcessao;
    }

    public void setContratoConcessao(Boolean contratoConcessao) {
        this.contratoConcessao = contratoConcessao;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(HierarquiaOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public List<UnidadeContrato> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeContrato> unidades) {
        this.unidades = unidades;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getNumeroTermo() {
        return numeroTermo;
    }

    public void setNumeroTermo(String numeroTermo) {
        this.numeroTermo = numeroTermo;
    }

    public Exercicio getExercicioContrato() {
        return exercicioContrato;
    }

    public void setExercicioContrato(Exercicio exercicioContrato) {
        this.exercicioContrato = exercicioContrato;
    }

    public Integer getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(Integer numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Integer getAnoProcesso() {
        return anoProcesso;
    }

    public void setAnoProcesso(Integer anoProcesso) {
        this.anoProcesso = anoProcesso;
    }

    public TipoAquisicaoContrato getTipoAquisicao() {
        return tipoAquisicao;
    }

    public void setTipoAquisicao(TipoAquisicaoContrato tipoAquisicao) {
        this.tipoAquisicao = tipoAquisicao;
    }

    public String getRegimeExecucao() {
        return regimeExecucao;
    }

    public void setRegimeExecucao(String regimeExecucao) {
        this.regimeExecucao = regimeExecucao;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorAtualContrato() {
        return valorAtualContrato;
    }

    public void setValorAtualContrato(BigDecimal valorAtualContrato) {
        this.valorAtualContrato = valorAtualContrato;
    }

    public BigDecimal getSaldoAtualContrato() {
        return saldoAtualContrato;
    }

    public void setSaldoAtualContrato(BigDecimal saldoAtualContrato) {
        this.saldoAtualContrato = saldoAtualContrato;
    }

    public BigDecimal getVariacaoAtualContrato() {
        return variacaoAtualContrato;
    }

    public void setVariacaoAtualContrato(BigDecimal variacaoAtualContrato) {
        this.variacaoAtualContrato = variacaoAtualContrato;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataAprovacao() {
        return dataAprovacao;
    }

    public void setDataAprovacao(Date dataAprovacao) {
        this.dataAprovacao = dataAprovacao;
    }

    public Date getDataDeferimento() {
        return dataDeferimento;
    }

    public void setDataDeferimento(Date dataDeferimento) {
        this.dataDeferimento = dataDeferimento;
    }

    public Date getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(Date dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getTerminoVigencia() {
        return terminoVigencia;
    }

    public void setTerminoVigencia(Date terminoVigencia) {
        this.terminoVigencia = terminoVigencia;
    }

    public Date getTerminoVigenciaAtual() {
        return terminoVigenciaAtual;
    }

    public void setTerminoVigenciaAtual(Date terminoVigenciaAtual) {
        this.terminoVigenciaAtual = terminoVigenciaAtual;
    }

    public Date getInicioExecucao() {
        return inicioExecucao;
    }

    public void setInicioExecucao(Date inicioExecucao) {
        this.inicioExecucao = inicioExecucao;
    }

    public Date getTerminoExecucao() {
        return terminoExecucao;
    }

    public void setTerminoExecucao(Date terminoExecucao) {
        this.terminoExecucao = terminoExecucao;
    }

    public Date getTerminoExecucaoAtual() {
        return terminoExecucaoAtual;
    }

    public void setTerminoExecucaoAtual(Date terminoExecucaoAtual) {
        this.terminoExecucaoAtual = terminoExecucaoAtual;
    }

    public SituacaoContrato getSituacaoContrato() {
        return situacaoContrato;
    }

    public void setSituacaoContrato(SituacaoContrato situacaoContrato) {
        this.situacaoContrato = situacaoContrato;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public TipoContrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(TipoContrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }


    public RescisaoContrato getRescisaoContrato() {
        return rescisaoContrato;
    }

    public void setRescisaoContrato(RescisaoContrato rescisaoContrato) {
        this.rescisaoContrato = rescisaoContrato;
    }

    public List<OrdemDeCompraContrato> getOrdensDeCompra() {
        return ordensDeCompra;
    }

    public void setOrdensDeCompra(List<OrdemDeCompraContrato> ordensDeCompra) {
        this.ordensDeCompra = ordensDeCompra;
    }

    public ContratoLicitacao getContratoLicitacao() {
        return contratoLicitacao;
    }

    public void setContratoLicitacao(ContratoLicitacao contratoLicitacao) {
        this.contratoLicitacao = contratoLicitacao;
    }

    public ContratoDispensaDeLicitacao getContratoDispensaDeLicitacao() {
        return contratoDispensaDeLicitacao;
    }

    public void setContratoDispensaDeLicitacao(ContratoDispensaDeLicitacao contratoDispensaDeLicitacao) {
        this.contratoDispensaDeLicitacao = contratoDispensaDeLicitacao;
    }

    public ContratoRegistroPrecoExterno getContratoRegistroPrecoExterno() {
        return contratoRegistroPrecoExterno;
    }

    public void setContratoRegistroPrecoExterno(ContratoRegistroPrecoExterno contratoRegistroPrecoExterno) {
        this.contratoRegistroPrecoExterno = contratoRegistroPrecoExterno;
    }

    public List<DocumentoContrato> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoContrato> documentos) {
        this.documentos = documentos;
    }

    public List<ItemContrato> getItens() {
        return itens;
    }

    public void setItens(List<ItemContrato> itens) {
        if (itens != null) {
            Collections.sort(itens);
        }
        this.itens = itens;
    }

    public ContratosVigente getContratosVigente() {
        return contratosVigente;
    }

    public void setContratosVigente(ContratosVigente contratosVigente) {
        this.contratosVigente = contratosVigente;
    }

    public List<PenalidadeContrato> getPenalidades() {
        return penalidades;
    }

    public void setPenalidades(List<PenalidadeContrato> penalidades) {
        this.penalidades = penalidades;
    }

    public List<CaucaoContrato> getCaucoes() {
        return caucoes;
    }

    public void setCaucoes(List<CaucaoContrato> caucoes) {
        this.caucoes = caucoes;
    }

    public List<ExecucaoContrato> getExecucoes() {
        return execucoes;
    }

    public void setExecucoes(List<ExecucaoContrato> execucoes) {
        this.execucoes = execucoes;
    }

    public List<OrdemDeServicoContrato> getOrdensDeServico() {
        return ordensDeServico;
    }

    public void setOrdensDeServico(List<OrdemDeServicoContrato> ordensDeServico) {
        this.ordensDeServico = ordensDeServico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.CONTRATO;
    }

    public List<AlteracaoContratual> getAlteracoesContratuais() {
        return alteracoesContratuais;
    }

    public void setAlteracoesContratuais(List<AlteracaoContratual> aditivos) {
        this.alteracoesContratuais = aditivos;
    }

    public List<AlteracaoContratual> getAditivos() {
        return alteracoesContratuais.stream().filter(AlteracaoContratual::isAditivo).collect(Collectors.toList());
    }

    public List<AlteracaoContratual> getApostilamentos() {
        return alteracoesContratuais.stream().filter(AlteracaoContratual::isApostilamento).collect(Collectors.toList());
    }

    public List<AjusteContrato> getAjustes() {
        return ajustes;
    }

    public void setAjustes(List<AjusteContrato> ajustesContrato) {
        this.ajustes = ajustesContrato;
    }

    public List<FiscalContrato> getFiscais() {
        return fiscais;
    }

    public void setFiscais(List<FiscalContrato> fiscais) {
        this.fiscais = fiscais;
    }

    public Pessoa getContratado() {
        return contratado;
    }

    public void setContratado(Pessoa contratado) {
        this.contratado = contratado;
    }

    public ContratoFP getResponsavelPrefeitura() {
        return responsavelPrefeitura;
    }

    public void setResponsavelPrefeitura(ContratoFP responsavelPrefeitura) {
        if (responsavelPrefeitura != null) {
            setResponsavelPrefeituraPf(responsavelPrefeitura.getMatriculaFP().getPessoa());
        }
        this.responsavelPrefeitura = responsavelPrefeitura;
    }

    public PessoaFisica getResponsavelPrefeituraPf() {
        return responsavelPrefeituraPf;
    }

    public void setResponsavelPrefeituraPf(PessoaFisica responsavelPrefeituraPf) {
        this.responsavelPrefeituraPf = responsavelPrefeituraPf;
    }

    public PessoaFisica getResponsavelEmpresa() {
        return responsavelEmpresa;
    }

    public void setResponsavelEmpresa(PessoaFisica responsavelEmpresa) {
        this.responsavelEmpresa = responsavelEmpresa;
    }

    public List<ContatoContrato> getContatos() {
        return contatos;
    }

    public void setContatos(List<ContatoContrato> contatos) {
        this.contatos = contatos;
    }

    public String getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(String localEntrega) {
        this.localEntrega = localEntrega;
    }

    public List<PublicacaoContrato> getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(List<PublicacaoContrato> publicacoes) {
        this.publicacoes = publicacoes;
    }

    public List<GestorContrato> getGestores() {
        return gestores;
    }

    public void setGestores(List<GestorContrato> gestores) {
        this.gestores = gestores;
    }

    public List<FornecedorContrato> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<FornecedorContrato> fornecedores) {
        this.fornecedores = fornecedores;
    }

    @Override
    public String toString() {
        try {
            if (!Strings.isNullOrEmpty(getNumeroContratoAno())) {
                return getNumeroContratoAno() + " - " + getNumeroAnoTermo() + " - " + getContratado();
            }
            return getNumeroAnoTermo() + " - " + getContratado();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public String toStringAutoComplete() {
        return toString();
    }

    public String getStringAutoCompleteObra() {
        try {
            return getNumeroAnoTermo() + " - " + getObjeto();
        } catch (NullPointerException ex) {
            return "";
        }
    }

    public void limparDadosGerais() {
        limparRelacionamentosObjetoLicitatorio();
        setNumeroProcesso(null);
        setAnoProcesso(null);
        setObjeto(null);
        setRegimeExecucao(null);
        setTipoContrato(null);
        setValorTotal(BigDecimal.ZERO);
        setValorAtualContrato(BigDecimal.ZERO);
        setVariacaoAtualContrato(BigDecimal.ZERO);
        setSaldoAtualContrato(BigDecimal.ZERO);
        setUnidadeAdministrativa(null);
        setContratado(null);
        setItens(null);
    }

    public BigDecimal getQuantidadeTotalItensEmContrato() {
        BigDecimal quantidadeTotal = BigDecimal.ZERO;
        if (itens == null) {
            return quantidadeTotal;
        }
        for (ItemContrato iten : itens) {
            if (iten.getQuantidadeTotalContrato() != null) {
                quantidadeTotal = quantidadeTotal.add(iten.getQuantidadeTotalContrato());
            }
        }
        return quantidadeTotal;
    }

    public String getNumeroContratoAno() {
        return numeroContrato + "/" + Util.getAnoDaData(dataLancamento);
    }

    public BigDecimal getValorTotalExecucao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExecucaoContrato ec : getExecucoes()) {
            total = total.add(ec.getValor());
        }
        return total;
    }

    public BigDecimal getValorTotalExecucaoLiquido() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExecucaoContrato ec : getExecucoes()) {
            total = total.add(ec.getValorExecucaoLiquido());
        }
        return total;
    }

    public BigDecimal getValorTotalItensContrato() {
        BigDecimal valor = BigDecimal.ZERO;
        try {
            for (ItemContrato item : getItens()) {
                valor = valor.add(item.getValorTotal());
            }
        } catch (NullPointerException npe) {
            logger.error(npe.getMessage());
        }
        return valor;
    }

    public boolean isTipoApuracaoPorLote() {
        if (isProcessoLicitatorio()) {
            return getLicitacao() != null && getLicitacao().getTipoApuracao().isPorLote();
        }
        if (isDeDispensaDeLicitacao()) {
            return getDispensaDeLicitacao() != null && getDispensaDeLicitacao().getTipoDeApuracao().isPorLote();
        }
        if (isDeRegistroDePrecoExterno()) {
            return getRegistroSolicitacaoMaterialExterno() != null && getRegistroSolicitacaoMaterialExterno().getTipoApuracao().isPorLote();
        }
        return false;
    }

    public boolean isTipoAvaliacaoMaiorDesconto() {
        if (isProcessoLicitatorio()) {
            return getLicitacao() != null && getLicitacao().getTipoAvaliacao().isMaiorDesconto();
        }
        return false;
    }

    public ObjetoLicitatorioContrato getObjetoAdequado() {
        if (getContratoLicitacao() != null) {
            return getContratoLicitacao();
        }
        if (getContratoDispensaDeLicitacao() != null) {
            return getContratoDispensaDeLicitacao();
        }
        if (getContratoRegistroPrecoExterno() != null) {
            return getContratoRegistroPrecoExterno();
        }
        if (getContratosVigente() != null) {
            return getContratosVigente();
        }
        return null;
    }

    /**
     * Utilizar sempre esses método para verificar o tipo de contrato
     */
    public Boolean isRegistroDePrecos() {
        try {
            return getObjetoAdequado().isRegistroDePrecos();
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    public boolean isMaterial() {
        try {
            return TipoContrato.MATERIAL.equals(getTipoContrato());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isServico() {
        try {
            return TipoContrato.SERVICO.equals(getTipoContrato());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isObrasEngenharia() {
        try {
            return TipoContrato.OBRAS_ENGENHARIA.equals(getTipoContrato());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isContratoVigente() {
        try {
            return getTipoAquisicao().isContratoVigente();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isDeLicitacao() {
        try {
            return getTipoAquisicao().isLicitacao();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isDeCompraDireta() {
        try {
            return TipoAquisicaoContrato.COMPRA_DIRETA.equals(getTipoAquisicao());
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isDeDispensaDeLicitacao() {
        try {
            return getTipoAquisicao().isDispensa();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isDeRegistroDePrecoExterno() {
        try {
            return getTipoAquisicao().isRegistroPrecoExterno();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isDeAdesaoAtaRegistroPrecoInterna() {
        try {
            return getTipoAquisicao().isAdesaoAtaInterna();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isDeIrp() {
        try {
            return getTipoAquisicao().isIrp();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isDeProcedimentosAuxiliares() {
        try {
            return getTipoAquisicao().isProcedimentosAuxiliares();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isProcessoLicitatorio() {
        return isDeLicitacao() || isDeAdesaoAtaRegistroPrecoInterna() || isDeIrp();
    }

    private void limparRelacionamentosObjetoLicitatorio() {
        setContratoLicitacao(null);
        setContratoDispensaDeLicitacao(null);
        setContratoRegistroPrecoExterno(null);
        setContratosVigente(null);
    }

    public ParticipanteIRP getParticipanteIRP() {
        try {
            return getContratoLicitacao().getParticipanteIRP();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public Licitacao getLicitacao() {
        try {
            return getContratoLicitacao().getLicitacao();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public SolicitacaoMaterialExterno getSolicitacaoAdesaoAtaInterna() {
        try {
            return getContratoLicitacao().getSolicitacaoMaterialExterno();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public DispensaDeLicitacao getDispensaDeLicitacao() {
        try {
            return getContratoDispensaDeLicitacao().getDispensaDeLicitacao();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public RegistroSolicitacaoMaterialExterno getRegistroSolicitacaoMaterialExterno() {
        try {
            return contratoRegistroPrecoExterno.getRegistroSolicitacaoMaterialExterno();
        } catch (NullPointerException npe) {
            return null;
        }
    }

    public void transferirDadosObjetoLicitatorio() {
        switch (getTipoAquisicao()) {
            case LICITACAO:
            case INTENCAO_REGISTRO_PRECO:
            case ADESAO_ATA_REGISTRO_PRECO_INTERNA:
            case PROCEDIMENTO_AUXILIARES:
                getContratoLicitacao().transferirObjetoLicitatorioParaContrato();
                break;
            case DISPENSA_DE_LICITACAO:
                getContratoDispensaDeLicitacao().transferirObjetoLicitatorioParaContrato();
                break;
            case REGISTRO_DE_PRECO_EXTERNO:
                getContratoRegistroPrecoExterno().transferirObjetoLicitatorioParaContrato();
                break;
        }
    }

    public boolean isRescindido() {
        return situacaoContrato.isRescindido();
    }

    public ModalidadeLicitacao getModalidadeLicitacao() {
        if (isDeDispensaDeLicitacao()) {
            return getDispensaDeLicitacao().getModalidade();

        } else if (isDeProcedimentosAuxiliares()) {
            return getLicitacao().getModalidadeLicitacao();

        } else if (isDeRegistroDePrecoExterno() && getContratoRegistroPrecoExterno() != null && getRegistroSolicitacaoMaterialExterno() != null) {
            return getRegistroSolicitacaoMaterialExterno().getModalidadeCarona();

        } else if (isProcessoLicitatorio()) {
            return getLicitacao().getModalidadeLicitacao();

        } else {
            return getContratosVigente().getModalidade();
        }
    }

    public TipoNaturezaDoProcedimentoLicitacao getTipoNaturezaDoProcedimentoLicitacao() {
        if (isDeDispensaDeLicitacao()) {
            return getContratoDispensaDeLicitacao().getSolicitacaoMaterial().getTipoNaturezaDoProcedimento();

        } else if (isDeProcedimentosAuxiliares()) {
            return getLicitacao().getNaturezaDoProcedimento();

        } else if (isProcessoLicitatorio()) {
            return getLicitacao().getNaturezaDoProcedimento();

        } else if (isDeRegistroDePrecoExterno() && getRegistroSolicitacaoMaterialExterno().getTipoModalidade() != null) {
            return getRegistroSolicitacaoMaterialExterno().getTipoModalidade();

        } else if (isContratoVigente() && getContratosVigente().getTipoNaturezaProcedimento() != null) {
            return getContratosVigente().getTipoNaturezaProcedimento();

        } else {
            return TipoNaturezaDoProcedimentoLicitacao.NAO_APLICAVEL;
        }
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public String getNumeroAnoTermo() {
        if (!Strings.isNullOrEmpty(numeroTermo)) {
            return numeroTermo + "/" + exercicioContrato.getAno();
        }
        return "";
    }

    public Date getPrazoEntrega() {
        return getTerminoVigencia();
    }

    public boolean isContratoDeferido() {
        return SituacaoContrato.DEFERIDO.equals(this.situacaoContrato);
    }

    public boolean isContratoEmElaboracao() {
        return SituacaoContrato.EM_ELABORACAO.equals(this.situacaoContrato);
    }

    public boolean isContratoAprovado() {
        return SituacaoContrato.APROVADO.equals(this.situacaoContrato);
    }

    public boolean podeReservarDotacaoPorDataReferencia(Date dataReferencia) {
        Boolean controle = Boolean.FALSE;
        if (getInicioExecucao() != null && getTerminoExecucao() != null) {
            int exercicioReferencia = DataUtil.getAno(dataReferencia);
            int exercicioInicioExecucao = DataUtil.getAno(getInicioExecucao());
            int exercicioTerminoExecucao = DataUtil.getAno(getTerminoExecucao());

            dataReferencia = DataUtil.dataSemHorario(dataReferencia);
            Date dataInicioExecucao = DataUtil.dataSemHorario(getInicioExecucao());
            Date dataTerminoExecucao = DataUtil.dataSemHorario(getTerminoExecucao());

            if (exercicioInicioExecucao <= exercicioReferencia
                && dataInicioExecucao.compareTo(dataReferencia) < 0
                && exercicioTerminoExecucao == exercicioReferencia
                && dataTerminoExecucao.compareTo(dataReferencia) >= 0) {
                controle = Boolean.TRUE;
            }
            if (exercicioInicioExecucao == exercicioReferencia
                && dataInicioExecucao.compareTo(dataReferencia) < 0
                && exercicioTerminoExecucao == exercicioReferencia
                && dataTerminoExecucao.compareTo(dataReferencia) >= 0) {
                controle = Boolean.TRUE;
            }
            if (exercicioInicioExecucao == exercicioReferencia
                && dataInicioExecucao.compareTo(dataReferencia) > 0
                && exercicioTerminoExecucao == exercicioReferencia
                && dataTerminoExecucao.compareTo(dataReferencia) >= 0) {
                controle = Boolean.TRUE;
            }
            if (exercicioInicioExecucao <= exercicioReferencia
                && exercicioTerminoExecucao > exercicioReferencia) {
                controle = Boolean.TRUE;
            }
            if (dataTerminoExecucao.compareTo(dataReferencia) < 0) {
                controle = Boolean.FALSE;
            }
            if (exercicioInicioExecucao > exercicioReferencia) {
                controle = Boolean.FALSE;
            }
        }
        return controle;
    }

    public Boolean isContratoAprovadoAte2020(Date dataAprovacao) {
        return dataAprovacao != null && DataUtil.getAno(dataAprovacao) <= ANO_TERMO_ATE_2020;
    }

    public Boolean isContratoAprovadoAte2020() {
        return isContratoAprovadoAte2020(getDataAprovacao());
    }

    public Boolean hasExecucoes() {
        return execucoes != null && !execucoes.isEmpty();
    }

    public Boolean hasItens() {
        return itens != null && !itens.isEmpty();
    }

    public Boolean hasAlteracoesContratuais() {
        return alteracoesContratuais != null && !alteracoesContratuais.isEmpty();
    }

    public boolean isTransferenciaFornecedor() {
        return !Util.isListNullOrEmpty(fornecedores) && fornecedores.size() > 1;
    }

}
