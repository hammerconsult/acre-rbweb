/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.FeriadoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ResultadoValidacao;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Licitação")
public class Licitacao extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Resumo do Objeto")
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Pesquisavel
    @Length(maximo = 255)
    private String resumoDoObjeto;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número da Modalidade")
    private Integer numero;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Processo Licitatório")
    private Integer numeroLicitacao;

    @Etiqueta("Tipo de Avaliação")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private TipoAvaliacaoLicitacao tipoAvaliacao;

    @Etiqueta("Tipo de Apuração")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private TipoApuracaoLicitacao tipoApuracao;

    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Modalidade da Licitação")
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    private ModalidadeLicitacao modalidadeLicitacao;

    @OneToOne
    @Pesquisavel
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    private Exercicio exercicioModalidade;

    @Etiqueta("Natureza do Procedimento")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private TipoNaturezaDoProcedimentoLicitacao naturezaDoProcedimento;

    @Invisivel
    @Etiqueta("Status da Licitação")
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataStatus asc")
    @Tabelavel(campoSelecionado = false)
    private List<StatusLicitacao> listaDeStatusLicitacao;

    @Invisivel
    @Etiqueta("Pareceres da Licitação")
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<ParecerLicitacao> pareceres;

    @Invisivel
    @Etiqueta("Recursos da Licitação")
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<RecursoLicitacao> listaDeRecursoLicitacao;

    @Invisivel
    @Etiqueta("Publicações da Licitação")
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<PublicacaoLicitacao> publicacoes;

    @Invisivel
    @Etiqueta("Documentos de Habilitação da Licitação")
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<DoctoHabLicitacao> documentosProcesso;

    @Invisivel
    @Etiqueta("Membros de Comissão Participantes")
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<LicitacaoMembroComissao> listaDeLicitacaoMembroComissao;

    @Invisivel
    @Etiqueta("Fornecedores da Licitação")
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<LicitacaoFornecedor> fornecedores;

    @Invisivel
    @Etiqueta("Documentos da Licitação")
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<DoctoLicitacao> listaDeDocumentos;

    @Invisivel
    @OneToMany(mappedBy = "licitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadeLicitacao> unidades;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Processo de Compra")
    @Pesquisavel
    @Tabelavel
    private ProcessoDeCompra processoDeCompra;

    @Obrigatorio
    @Etiqueta("Comissão")
    @ManyToOne
    @Tabelavel
    private Comissao comissao;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Exercicio")
    @ManyToOne
    @Pesquisavel
    private Exercicio exercicio;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Emissão")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Date emitidaEm;

    @Etiqueta("Data de Abertura")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Date abertaEm;

    @Etiqueta("Data de Encerramento")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Date encerradaEm;

    @Monetario
    @Etiqueta("Valor Máximo")
    @Tabelavel(campoSelecionado = false)
    private BigDecimal valorMaximo;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Homologação")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Date homologadaEm;

    @Etiqueta("Local de Entrega")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Length(maximo = 255)
    private String localDeEntrega;

    @Etiqueta("Regime de Execução")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private RegimeDeExecucao regimeDeExecucao;

    @Etiqueta("Forma de Pagamento")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Length(maximo = 3000)
    private String formaDePagamento;

    @Etiqueta("Período de Execução")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Integer periodoDeExecucao;

    @Etiqueta("Tipo Prazo de Execução")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoExecucao;

    @Etiqueta("Prazo de Vigência")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Integer prazoDeVigencia;

    @Etiqueta("Tipo Prazo de Vigência")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoVigencia;

    @Invisivel
    @Transient
    private StatusLicitacao statusAtual;
    @Invisivel
    @Transient
    private List<DoctoHabilitacao> listaDeDoctoHabilitacao;
    @Transient
    private HierarquiaOrganizacional unidadeAdministrativa;

    @OneToOne
    private MembroComissao pregoeiroResponsavel;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @Length(maximo = 3000)
    @Etiqueta("Justificativa")
    private String justificativa;

    @ManyToOne
    @Etiqueta("Responsável")
    private PessoaFisica responsavel;

    @Length(maximo = 3000)
    @Etiqueta("Fundamentação Legal")
    private String fundamentacaoLegal;

    @Etiqueta("Link do Sistema Origem")
    @Length(maximo = 512)
    private String linkSistemaOrigem;

    @Etiqueta("Sequencial Criado pelo PNCP ao Realizar o Envio da Licitação")
    @Tabelavel(campoSelecionado = false)
    private String sequencialIdPncp;

    @Etiqueta("Id Criado pelo PNCP ao Realizar o Envio da Licitação")
    @Tabelavel(campoSelecionado = false)
    private String idPncp;

    public Licitacao() {
        pareceres = Lists.newArrayList();
        publicacoes = Lists.newArrayList();
        listaDeRecursoLicitacao = Lists.newArrayList();
        listaDeStatusLicitacao = Lists.newArrayList();
        documentosProcesso = Lists.newArrayList();
        listaDeLicitacaoMembroComissao = Lists.newArrayList();
        fornecedores = Lists.newArrayList();
        listaDeDoctoHabilitacao = Lists.newArrayList();
        listaDeDocumentos = Lists.newArrayList();
        unidades = Lists.newArrayList();
    }

    public Licitacao(Long id, Integer numero, ProcessoDeCompra processoDeCompra, Exercicio exercicio, Integer contratos, StatusLicitacao statusLicitacao) {
        this.id = id;
        this.numero = numero;
        this.processoDeCompra = processoDeCompra;
        this.exercicio = exercicio;
        this.statusAtual = statusLicitacao;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getFundamentacaoLegal() {
        return fundamentacaoLegal;
    }

    public void setFundamentacaoLegal(String fundamentacaoLegal) {
        this.fundamentacaoLegal = fundamentacaoLegal;
    }

    public String getLinkSistemaOrigem() {
        return linkSistemaOrigem;
    }

    public void setLinkSistemaOrigem(String linkSistemaOrigem) {
        this.linkSistemaOrigem = linkSistemaOrigem;
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

    public Integer getPrazoDeVigencia() {
        return prazoDeVigencia;
    }

    public void setPrazoDeVigencia(Integer prazoDeVigencia) {
        this.prazoDeVigencia = prazoDeVigencia;
    }

    public TipoPrazo getTipoPrazoVigencia() {
        return tipoPrazoVigencia;
    }

    public void setTipoPrazoVigencia(TipoPrazo tipoPrazoVigencia) {
        this.tipoPrazoVigencia = tipoPrazoVigencia;
    }

    public List<LicitacaoFornecedor> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<LicitacaoFornecedor> listaDeLicitacaoFornecedor) {
        this.fornecedores = listaDeLicitacaoFornecedor;
    }

    public List<LicitacaoMembroComissao> getListaDeLicitacaoMembroComissao() {
        return listaDeLicitacaoMembroComissao;
    }

    public void setListaDeLicitacaoMembroComissao(List<LicitacaoMembroComissao> listaDeLicitacaoMembroComissao) {
        this.listaDeLicitacaoMembroComissao = listaDeLicitacaoMembroComissao;
    }

    public Integer getPeriodoDeExecucao() {
        return periodoDeExecucao;
    }

    public void setPeriodoDeExecucao(Integer periodoDeExecucao) {
        this.periodoDeExecucao = periodoDeExecucao;
    }

    public TipoPrazo getTipoPrazoExecucao() {
        return tipoPrazoExecucao;
    }

    public void setTipoPrazoExecucao(TipoPrazo tipoPrazoExecucao) {
        this.tipoPrazoExecucao = tipoPrazoExecucao;
    }

    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(String formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    public String getLocalDeEntrega() {
        return localDeEntrega;
    }

    public void setLocalDeEntrega(String localDeEntrega) {
        this.localDeEntrega = localDeEntrega;
    }

    public RegimeDeExecucao getRegimeDeExecucao() {
        return regimeDeExecucao;
    }

    public void setRegimeDeExecucao(RegimeDeExecucao regimeDeExecucao) {
        this.regimeDeExecucao = regimeDeExecucao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroLicitacao() {
        return numeroLicitacao;
    }

    public void setNumeroLicitacao(Integer numeroLicitacao) {
        this.numeroLicitacao = numeroLicitacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getResumoDoObjeto() {
        return resumoDoObjeto;
    }

    public void setResumoDoObjeto(String resumoDoObjeto) {
        this.resumoDoObjeto = resumoDoObjeto;
    }

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public Comissao getComissao() {
        return comissao;
    }

    public void setComissao(Comissao comissao) {
        this.comissao = comissao;
    }

    public TipoApuracaoLicitacao getTipoApuracao() {
        return tipoApuracao;
    }

    public void setTipoApuracao(TipoApuracaoLicitacao tipoApuracao) {
        this.tipoApuracao = tipoApuracao;
    }

    public TipoAvaliacaoLicitacao getTipoAvaliacao() {
        return tipoAvaliacao;
    }

    public void setTipoAvaliacao(TipoAvaliacaoLicitacao tipoAvaliacao) {
        this.tipoAvaliacao = tipoAvaliacao;
    }

    public List<StatusLicitacao> getListaDeStatusLicitacao() {
        return listaDeStatusLicitacao;
    }

    public void setListaDeStatusLicitacao(List<StatusLicitacao> listaDeStatusLicitacao) {
        this.listaDeStatusLicitacao = listaDeStatusLicitacao;
    }

    public List<ParecerLicitacao> getPareceres() {
        return pareceres;
    }

    public void setPareceres(List<ParecerLicitacao> listaDeParecerLicitacao) {
        this.pareceres = listaDeParecerLicitacao;
    }

    public List<RecursoLicitacao> getListaDeRecursoLicitacao() {
        return listaDeRecursoLicitacao;
    }

    public void setListaDeRecursoLicitacao(List<RecursoLicitacao> listaDeRecursoLicitacao) {
        this.listaDeRecursoLicitacao = listaDeRecursoLicitacao;
    }

    public ProcessoDeCompra getProcessoDeCompra() {
        return processoDeCompra;
    }

    public void setProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        this.processoDeCompra = processoDeCompra;
    }

    public List<PublicacaoLicitacao> getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(List<PublicacaoLicitacao> listaDePublicacaoLicitacao) {
        this.publicacoes = listaDePublicacaoLicitacao;
    }

    public List<DoctoHabLicitacao> getDocumentosProcesso() {
        return documentosProcesso;
    }

    public void setDocumentosProcesso(List<DoctoHabLicitacao> listaDeDoctoHabLicitacao) {
        this.documentosProcesso = listaDeDoctoHabLicitacao;
    }

    public List<DoctoHabilitacao> getListaDeDoctoHabilitacao() {
        if (listaDeDoctoHabilitacao.isEmpty() && !documentosProcesso.isEmpty()) {
            for (DoctoHabLicitacao doctoHabLicitacao : documentosProcesso) {
                listaDeDoctoHabilitacao.add(doctoHabLicitacao.getDoctoHabilitacao());
            }
        }

        return listaDeDoctoHabilitacao;
    }

    public LicitacaoFornecedor getLicitacaoFornecedor(Pessoa fornecedor) {
        for (LicitacaoFornecedor lf : fornecedores) {
            if (lf.getEmpresa().equals(fornecedor)) {
                return lf;
            }
        }

        return null;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getAbertaEm() {
        return abertaEm;
    }

    public void setAbertaEm(Date abertaEm) {
        this.abertaEm = abertaEm;
    }

    public Date getEncerradaEm() {
        return encerradaEm;
    }

    public void setEncerradaEm(Date encerradaEm) {
        this.encerradaEm = encerradaEm;
    }

    public Date getEmitidaEm() {
        return emitidaEm;
    }

    public void setEmitidaEm(Date emitidaEm) {
        this.emitidaEm = emitidaEm;
    }

    public Date getHomologadaEm() {
        try {
            if (this.getStatusAtualLicitacao().getTipoSituacaoLicitacao().equals(TipoSituacaoLicitacao.HOMOLOGADA)) {
                homologadaEm = this.getStatusAtualLicitacao().getDataStatus();
            }
            return homologadaEm;
        } catch (Exception e) {
            return homologadaEm;
        }
    }

    public void setHomologadaEm(Date homologadaEm) {
        this.homologadaEm = homologadaEm;
    }

    public TipoNaturezaDoProcedimentoLicitacao getNaturezaDoProcedimento() {
        return naturezaDoProcedimento;
    }

    public void setNaturezaDoProcedimento(TipoNaturezaDoProcedimentoLicitacao naturezaDoProcedimentoLicitacao) {
        this.naturezaDoProcedimento = naturezaDoProcedimentoLicitacao;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public MembroComissao getPregoeiroResponsavel() {
        return pregoeiroResponsavel;
    }

    public void setPregoeiroResponsavel(MembroComissao pregoeiroResponsavel) {
        this.pregoeiroResponsavel = pregoeiroResponsavel;
    }

    public List<DoctoLicitacao> getListaDeDocumentos() {
        return listaDeDocumentos;
    }

    public void setListaDeDocumentos(List<DoctoLicitacao> listaDeDocumentos) {
        this.listaDeDocumentos = listaDeDocumentos;
    }

    public List<UnidadeLicitacao> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeLicitacao> unidades) {
        this.unidades = unidades;
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(HierarquiaOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    @Override
    public String toString() {
        return toStringAutoComplete();
    }

    public String toStringAutoComplete() {
        String descricao = "";

        try {
            descricao = "Nº " + this.getNumeroLicitacao() + "/" + this.getExercicio() + " - " + this.getResumoDoObjeto();

            if (descricao.length() > 100) {
                descricao = descricao.substring(0, 100) + "...";
                return descricao;
            }
        } catch (Exception e) {
        }
        return descricao;
    }

    public String toStringNumeroExercicio() {
        return "Nº " + this.getNumeroLicitacao() + "/" + this.getExercicio();
    }

    public String toStringAutoCompleteComModalidade() {
        String descricao = "";
        try {
            descricao = "Processo de Licitação " + numeroLicitacao + "/" + exercicio + " - Modalidade " + (modalidadeLicitacao != null ? modalidadeLicitacao.getDescricao() : "") + " " + numero + "/" + exercicioModalidade + " - " + resumoDoObjeto;
            if (descricao.length() > 100) {
                descricao = descricao.substring(0, 100) + "...";
                return descricao;
            }
        } catch (Exception e) {
            return "";
        }
        return descricao;
    }

    public Exercicio getExercicioModalidade() {
        return exercicioModalidade;
    }

    public void setExercicioModalidade(Exercicio exercicioModalidade) {
        this.exercicioModalidade = exercicioModalidade;
    }

    public StatusLicitacao getStatusAtual() {
        return statusAtual;
    }

    public void setStatusAtual(StatusLicitacao statusAtual) {
        this.statusAtual = statusAtual;
    }

    public StatusLicitacao getStatusAtualLicitacao() {
        try {
            Collections.sort(this.listaDeStatusLicitacao, new Comparator<StatusLicitacao>() {
                @Override
                public int compare(StatusLicitacao sl1, StatusLicitacao sl2) {
                    return sl1.getNumero().compareTo(sl2.getNumero());
                }
            });

            if (this.listaDeStatusLicitacao != null && !this.listaDeStatusLicitacao.isEmpty()) {
                setStatusAtual(this.listaDeStatusLicitacao.get(this.listaDeStatusLicitacao.size() - 1));
                return getStatusAtual();
            } else {
                return getStatusPadrao();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getStatusPadrao();
        }
    }

    public StatusLicitacao getStatusAnteriorAoAtual() {
        return listaDeStatusLicitacao.get(listaDeStatusLicitacao.indexOf(getStatusAtualLicitacao()) - 1);
    }

    private StatusLicitacao getStatusPadrao() {
        if (statusAtual != null) {
            return statusAtual;
        }
        StatusLicitacao statusRetorno = new StatusLicitacao();
        statusRetorno.setNumero(0l);
        return statusRetorno;
    }

    public int calculaESomarHashDeCadaMembroDaComissao() {
        int hash = 0;

        for (LicitacaoMembroComissao licitacaoMembroComissao : this.getListaDeLicitacaoMembroComissao()) {
            hash += licitacaoMembroComissao.getMembroComissao().hashCode();
        }

        return hash;
    }

    public Boolean jaPassouPelaSituacaoDe(TipoSituacaoLicitacao tipoSituacao) {
        if (tipoSituacao == null) {
            return Boolean.FALSE;
        }

        for (StatusLicitacao statusLicitacaoDaVez : this.listaDeStatusLicitacao) {
            try {
                if (statusLicitacaoDaVez.getTipoSituacaoLicitacao().equals(tipoSituacao)) {
                    return Boolean.TRUE;
                }
            } catch (Exception e) {
                return Boolean.FALSE;
            }
        }

        return Boolean.FALSE;
    }

    public boolean possuiMembroComissao(MembroComissao membroComissao) {
        try {
            for (LicitacaoMembroComissao licitacaoMembroComissao : listaDeLicitacaoMembroComissao) {
                if (licitacaoMembroComissao.getMembroComissao().equals(membroComissao)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isRegistroDePrecos() {
        try {
            if (TipoNaturezaDoProcedimentoLicitacao.REGISTRO_DE_PRECOS.equals(getNaturezaDoProcedimento())
                || TipoNaturezaDoProcedimentoLicitacao.PRESENCIAL_COM_REGISTRO_DE_PRECO.equals(getNaturezaDoProcedimento())
                || TipoNaturezaDoProcedimentoLicitacao.ELETRONICO_COM_REGISTRO_DE_PRECO.equals(getNaturezaDoProcedimento())
                || TipoNaturezaDoProcedimentoLicitacao.ABERTA_COM_REGISTRO_DE_PRECO.equals(getNaturezaDoProcedimento())
                || TipoNaturezaDoProcedimentoLicitacao.FECHADA_COM_REGISTRO_DE_PRECO.equals(getNaturezaDoProcedimento())
                || TipoNaturezaDoProcedimentoLicitacao.COMBINADO_COM_REGISTRO_DE_PRECO.equals(getNaturezaDoProcedimento())) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    public boolean requerFornecedorHabilitado() {
        return getModalidadeLicitacao() != null &&
            (getModalidadeLicitacao().isConvite() || getModalidadeLicitacao().isTomadaPrecos() || getModalidadeLicitacao().isConcorrencia());
    }

    public Boolean isHomologada() {
        try {
            return TipoSituacaoLicitacao.HOMOLOGADA.equals(this.getStatusAtualLicitacao().getTipoSituacaoLicitacao());
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean isEmAndamento() {
        try {
            return TipoSituacaoLicitacao.ANDAMENTO.equals(this.getStatusAtualLicitacao().getTipoSituacaoLicitacao());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdjudicada() {
        try {
            return TipoSituacaoLicitacao.ADJUDICADA.equals(this.getStatusAtualLicitacao().getTipoSituacaoLicitacao());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isCredenciamento() {
        return this.getModalidadeLicitacao() != null && this.getModalidadeLicitacao().isCredenciamento();
    }

    public boolean isPregao() {
        return this.getModalidadeLicitacao() != null && this.getModalidadeLicitacao().isPregao();
    }

    public boolean isConcorrencia() {
        return this.getModalidadeLicitacao() != null && this.getModalidadeLicitacao().isConcorrencia();
    }

    public boolean isRDC() {
        return this.getModalidadeLicitacao() != null && this.getModalidadeLicitacao().isRDC();
    }

    public boolean isRDCPregao() {
        return isRDC() && TipoNaturezaDoProcedimentoLicitacao.getNaturezaTipoRDCPregao().contains(this.getNaturezaDoProcedimento());
    }

    public boolean isRDCMapaComparativo() {
        return isRDC() && TipoNaturezaDoProcedimentoLicitacao.getNaturezaTipoRDCCertame().contains(this.getNaturezaDoProcedimento());
    }

    public Boolean isTecnicaEPreco() {
        try {
            return TipoAvaliacaoLicitacao.TECNICA_PRECO.equals(getTipoAvaliacao());
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    public boolean isTipoApuracaoPrecoItem() {
        return TipoApuracaoLicitacao.ITEM.equals(this.getTipoApuracao());
    }

    public boolean isTipoApuracaoPrecoLote() {
        return TipoApuracaoLicitacao.LOTE.equals(this.getTipoApuracao());
    }

    public PublicacaoLicitacao getUltimaPublicacaoAdicionada() {
        PublicacaoLicitacao ultima = this.getPublicacoes().get(0);
        for (PublicacaoLicitacao publicacaoLicitacao : this.getPublicacoes()) {
            if (publicacaoLicitacao.getDataPublicacao().after(ultima.getDataPublicacao())) {
                ultima = publicacaoLicitacao;
            }
        }
        return ultima;
    }

    public boolean ultimaPublicacaoEhDoTipoEdital() {
        return TipoPublicacaoLicitacao.EDITAL.equals(getUltimaPublicacaoAdicionada().getTipoPublicacaoLicitacao());
    }

    public boolean temPublicacaoAdicionada() {
        return this.getPublicacoes() != null && !this.getPublicacoes().isEmpty();
    }

    public LicitacaoFornecedor getFornecedorVencedorDoPregao(ItemPregao itemPregao) {
        for (LicitacaoFornecedor licitacaoFornecedor : this.getFornecedores()) {
            if (licitacaoFornecedor.getEmpresa().equals(itemPregao.getFornecedorDoLanceVencedor())) {
                return licitacaoFornecedor;
            }
        }
        return null;
    }

    public boolean temEstaPublicacaoAdicionada(PublicacaoLicitacao publicacaoLicitacaoSelecionada) {
        return this.getPublicacoes().contains(publicacaoLicitacaoSelecionada);
    }

    public boolean isLicitacaoAberta() {
        return this.getAbertaEm() != null;
    }

    public String getTipoApuracaoDescricao() {
        try {
            return this.getTipoApuracao().getDescricao().toUpperCase();
        } catch (NullPointerException npe) {
            return "Tipo apuração não encontrado.";
        }
    }

    public RecursoStatus getRecursoDoStatusAtualDaLicitacaoSelecionada() {
        try {
            return this.getStatusAtualLicitacao().getRecursoStatus();
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("Ocorreu erro ao recuperar o recurso do status atual.");
        }
    }

    public LicitacaoFornecedor getLicitacaoFornecedorPorEmpresa(Pessoa empresa) {
        for (LicitacaoFornecedor licitacaoFornecedor : fornecedores) {
            if (licitacaoFornecedor.getEmpresa().equals(empresa)) {
                return licitacaoFornecedor;
            }
        }
        throw new ExcecaoNegocioGenerica("Nenhum fornecedor encontrado para empresa " + empresa.getNome());
    }

    public boolean isPregoeiroMembroComissao() {
        if (this.getListaDeLicitacaoMembroComissao() != null) {
            for (LicitacaoMembroComissao licitacaoMembroComissao : this.getListaDeLicitacaoMembroComissao()) {
                if (licitacaoMembroComissao.getMembroComissao().equals(this.getPregoeiroResponsavel())) {
                    return true;
                }
            }
        }

        return false;
    }

    public StatusLicitacao recuperarUltimoStatusDaLicitacao() {
        return this.getListaDeStatusLicitacao().get(this.getListaDeStatusLicitacao().size() - 1);
    }

    public boolean isStatusLicitacaoHomologada(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.HOMOLOGADA.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean isStatusLicitacaoAndamento(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.ANDAMENTO.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean isStatusLicitacaoAdjucada(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.ADJUDICADA.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean isStatusLicitacaoAnulada(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.ANULADA.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean isStatusLicitacaoCancelada(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.CANCELADA.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean isStatusLicitacaoDeserta(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.DESERTA.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean isStatusLicitacaoEmRecurso(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.EM_RECURSO.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean isStatusLicitacaoFracassada(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.FRACASSADA.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean isStatusLicitacaoRevogada(StatusLicitacao sl) {
        return TipoSituacaoLicitacao.REVOGADA.equals(sl.getTipoSituacaoLicitacao());
    }

    public boolean hasFornecedores() {
        return fornecedores != null && !fornecedores.isEmpty();
    }

    public boolean hasPublicacoes() {
        return publicacoes != null && !publicacoes.isEmpty();
    }

    public boolean hasPareceres() {
        return pareceres != null && !pareceres.isEmpty();
    }

    public boolean hasDocumentos() {
        return listaDeDocumentos != null && !listaDeDocumentos.isEmpty();
    }

    public boolean hasDocumentosProcesso() {
        return documentosProcesso != null && !documentosProcesso.isEmpty();
    }

    public Date incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasUteis(Date dataDePublicacao, int prazoEmDias, FeriadoFacade feriadoFacade) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataDePublicacao);

        int diasNaoUtil = 0;
        List<Date> listaDiasNaoUteis = new ArrayList<>();

        for (int i = 0; i < prazoEmDias; i++) {

            c.add(Calendar.DAY_OF_MONTH, 1);

            if (DataUtil.ehDiaNaoUtil(c.getTime(), feriadoFacade)) {
                listaDiasNaoUteis.add(c.getTime());
                diasNaoUtil++;
            }
        }

        c.add(Calendar.DAY_OF_MONTH, diasNaoUtil);
        while (DataUtil.ehDiaNaoUtil(c.getTime(), feriadoFacade)) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        return c.getTime();
    }

    public Date incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasCorridos(Date dataDePublicacao, int prazoEmDias) {
        Calendar c = Calendar.getInstance();
        c.setTime(dataDePublicacao);


        c.add(Calendar.DAY_OF_MONTH, prazoEmDias);

        return c.getTime();
    }

    public Date getPrazoMinimoParaAberturaDaLicitacao(Licitacao licitacao, FeriadoFacade feriadoFacade) {
        if (licitacao.getPublicacoes() == null || licitacao.getPublicacoes().isEmpty()) {
            return null;
        }
        Date dataPublicacao = licitacao.getUltimaPublicacaoAdicionada().getDataPublicacao();
        switch (licitacao.getModalidadeLicitacao()) {
            case PREGAO:
                return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasUteis(dataPublicacao, 8, feriadoFacade);
            case CONVITE:
                return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasUteis(dataPublicacao, 5, feriadoFacade);
            case TOMADA_PRECO:
                if (TipoAvaliacaoLicitacao.MELHOR_TECNICA.equals(licitacao.getTipoAvaliacao()) ||
                    TipoAvaliacaoLicitacao.TECNICA_PRECO.equals(licitacao.getTipoAvaliacao())) {
                    return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasCorridos(dataPublicacao, 30);
                } else {
                    return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasCorridos(dataPublicacao, 15);
                }
            case CONCORRENCIA:
                if (RegimeDeExecucao.EMPREITADA_INTEGRAL.equals(licitacao.getRegimeDeExecucao()) || TipoAvaliacaoLicitacao.MELHOR_TECNICA.equals(licitacao.getTipoAvaliacao())
                    || TipoAvaliacaoLicitacao.TECNICA_PRECO.equals(licitacao.getTipoAvaliacao())) {
                    return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasCorridos(dataPublicacao, 45);
                } else {
                    return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasCorridos(dataPublicacao, 30);
                }
            case CONCURSO:
                return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasUteis(dataPublicacao, 45, feriadoFacade);
            case DISPENSA_LICITACAO:
                return null;
            case INEXIGIBILIDADE:
                return null;
            case RDC:
                switch (licitacao.getProcessoDeCompra().getTipoSolicitacao()) {
                    case COMPRA_SERVICO: {
                        if (licitacao.getTipoAvaliacao().equals(TipoAvaliacaoLicitacao.MENOR_PRECO) ||
                            licitacao.getTipoAvaliacao().equals(TipoAvaliacaoLicitacao.MAIOR_DESCONTO)) {
                            return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasUteis(dataPublicacao, 5, feriadoFacade);
                        } else {
                            return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasUteis(dataPublicacao, 10, feriadoFacade);
                        }
                    }
                    case OBRA_SERVICO_DE_ENGENHARIA: {
                        if (licitacao.getTipoAvaliacao().equals(TipoAvaliacaoLicitacao.MENOR_PRECO) ||
                            licitacao.getTipoAvaliacao().equals(TipoAvaliacaoLicitacao.MAIOR_DESCONTO)) {
                            return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasUteis(dataPublicacao, 15, feriadoFacade);
                        } else {
                            return incrementaPrazoAPartirDaDataDePublicacaoSomenteDiasUteis(dataPublicacao, 30, feriadoFacade);
                        }
                    }
                }
                return null;
        }
        return null;
    }

    public String getNumeroAnoLicitacao() {
        try {
            return "Nº " + this.getNumeroLicitacao() + "/" + this.getExercicio();
        } catch (Exception ex) {
            return "";
        }
    }

    public String getResumoObjetoSubstrig() {
        return this.resumoDoObjeto.length() > 150 ? this.resumoDoObjeto.substring(0, 150) + "..." : resumoDoObjeto;
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
        if (isCredenciamento()) {
            return TipoMovimentoProcessoLicitatorio.CREDENCIAMENTO;
        }
        return TipoMovimentoProcessoLicitatorio.LICITACAO;
    }
}
