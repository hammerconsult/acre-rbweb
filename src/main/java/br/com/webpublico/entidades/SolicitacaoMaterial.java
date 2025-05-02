/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @author lucas
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Solicitação de Compra")

public class SolicitacaoMaterial extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao {

    private static final long serialVersionUID = 1L;
    private static final String MSG_REGISTRO_NOVO = "Avaliação criada automaticamente pelo sistema para a nova solicitação de compra.";
    private static final String MSG_REGISTRO_ALTERADO = "Avaliação criada automaticamente pelo sistema devido a alteração na solicitação de compra.";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo da Solicitação")
    private TipoSolicitacao tipoSolicitacao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Tipo de Objeto de Compra")
    @Enumerated(EnumType.STRING)
    private TipoObjetoCompra tipoObjetoCompra;

    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Subtipo de Objeto de Compra")
    @Enumerated(EnumType.STRING)
    private SubTipoObjetoCompra subTipoObjetoCompra;

    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Solicitação")
    @Column(name = "DATASOLICITACAO")
    @Obrigatorio
    private Date dataSolicitacao;

    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private Integer numero;

    @Tabelavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Length(maximo = 3000)
    @Etiqueta("Solução Sugerida")
    private String descricao;

    @Etiqueta("Justificativa da Necessidade")
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private String justificativa;

    @Length(maximo = 255)
    @Etiqueta("Justificativa")
    @Tabelavel
    private String justificativaCotacao;

    @Etiqueta("Forma de Pagamento")
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private String formaDePagamento;

    @Etiqueta("Local de Entrega")
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private String localDeEntrega;

    @Etiqueta("Prazo de Entrega")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Positivo
    @Obrigatorio
    private Integer prazoDeEntrega;

    @Etiqueta("Tipo do prazo de entrega")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoEntrega;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private CriterioProcessamentoPrecoItemCotacao criterio;

    @ManyToOne
    @JoinColumn(name = "SOLICITANTE_ID")
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Solicitante")
    private PessoaFisica solicitante;

    @Etiqueta("Modalidade da Licitação")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private ModalidadeLicitacao modalidadeLicitacao;

    @Etiqueta("Motivo")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String motivo;

    @Etiqueta("Prazo de Execução")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Positivo
    @Obrigatorio
    private Integer prazoExecucao;

    @Etiqueta("Tipo Prazo de Execução")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoDeExecucao;

    @Etiqueta("Unidade Administrativa")
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacaoMaterial")
    private CriterioTecnicaSolicitacao criterioTecnicaSolicitacao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacaoMaterial")
    private CriterioPrecoSolicitacao criterioPrecoSolicitacao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacaoMaterial")
    private ClassificacaoFinalSolicitacao classificacaoFinalSolicitacao;

    @OneToMany(mappedBy = "solicitacaoMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteSolicitacaoMaterial> loteSolicitacaoMaterials;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacao")
    private List<AvaliacaoSolicitacaoDeCompra> avaliacoes;

    @OneToMany(mappedBy = "solicitacaoMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnidadeSolicitacaoMaterial> unidadesParticipantes;

    @OneToMany(mappedBy = "solicitacaoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitacaoCompraDotacao> dotacoes;

    @Etiqueta("Prazo de Vigência")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Positivo
    private Integer vigencia;

    @Etiqueta("Tipo Prazo de Vigência")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoDeVigencia;

    @Etiqueta("Observações")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    private TipoNaturezaDoProcedimentoLicitacao tipoNaturezaDoProcedimento;

    @Etiqueta("Tipo Avaliação")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoAvaliacaoLicitacao tipoAvaliacao;

    @Etiqueta("Tipo Apuração")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoApuracaoLicitacao tipoApuracao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Cotação")
    private Cotacao cotacao;

    @Etiqueta("Criado Por")
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private UsuarioSistema usuarioCriacao;

    @Etiqueta("Data do Orçamento da Obra")
    @Pesquisavel
    private Date dataOrcamentoObra;

    @OneToMany(mappedBy = "solicitacaoMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitacaoMaterialDocumentoOficial> documentosOficiais;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @Etiqueta("Orçamento Sigiloso")
    private Boolean orcamentoSigiloso;

    @Etiqueta("Justificativa do Sigilo")
    private String justificativaSigilo;

    @Etiqueta("Modo de Disputa")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private ModoDisputa modoDisputa;

    @Etiqueta("Amparo Legal")
    @Obrigatorio
    @ManyToOne
    private AmparoLegal amparoLegal;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Modo da Solicitação")
    private ModoSolicitacaoCompra modoSolicitacao;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Instrumento Convocatório")
    private InstrumentoConvocatorio instrumentoConvocatorio;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Grau de Prioridade")
    private TipoGrauPrioridade tipoGrauPrioridade;

    @Etiqueta("Descrição da Necessidade")
    @Length(maximo = 3000)
    @Obrigatorio
    private String descricaoNecessidade;

    @Etiqueta("Justificativa da Apuração")
    @Length(maximo = 3000)
    private String justificativaApuracao;

    @Etiqueta("Justificativa Presencial")
    private String justificativaPresencial;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Obrigatorio
    @Etiqueta("Inicio da Execução")
    private Date inicioExecucao;

    @Etiqueta("Descrição Complementar")
    @Length(maximo = 3000)
    private String descricaoComplementarPrazo;

    @Etiqueta("Contratações Correlatas")
    @Length(maximo = 3000)
    private String contratacoesCorrelatas;

    @Etiqueta("Contratações Interdependentes")
    @Length(maximo = 3000)
    private String contratacoesIndependentes;

    public SolicitacaoMaterial() {
        this.dataSolicitacao = new Date();
        this.loteSolicitacaoMaterials = Lists.newArrayList();
        this.dotacoes = Lists.newArrayList();
        documentosOficiais = Lists.newArrayList();
    }

    public ModoSolicitacaoCompra getModoSolicitacao() {
        return modoSolicitacao;
    }

    public void setModoSolicitacao(ModoSolicitacaoCompra modoSolicitacao) {
        this.modoSolicitacao = modoSolicitacao;
    }

    public InstrumentoConvocatorio getInstrumentoConvocatorio() {
        return instrumentoConvocatorio;
    }

    public void setInstrumentoConvocatorio(InstrumentoConvocatorio instrumentoConvocatorio) {
        this.instrumentoConvocatorio = instrumentoConvocatorio;
    }

    public static String getMsgRegistroNovo() {
        return MSG_REGISTRO_NOVO;
    }

    public static String getMsgRegistroAlterado() {
        return MSG_REGISTRO_ALTERADO;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return subTipoObjetoCompra;
    }

    public void setSubTipoObjetoCompra(SubTipoObjetoCompra subTipoObjetoCompra) {
        this.subTipoObjetoCompra = subTipoObjetoCompra;
    }

    public List<AvaliacaoSolicitacaoDeCompra> getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(List<AvaliacaoSolicitacaoDeCompra> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public TipoPrazo getTipoPrazoEntrega() {
        return tipoPrazoEntrega;
    }

    public void setTipoPrazoEntrega(TipoPrazo tipoPrazoEntrega) {
        this.tipoPrazoEntrega = tipoPrazoEntrega;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(String formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoSolicitacao getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public void setTipoSolicitacao(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getLocalDeEntrega() {
        return localDeEntrega;
    }

    public void setLocalDeEntrega(String localDeEntrega) {
        this.localDeEntrega = localDeEntrega;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getPrazoDeEntrega() {
        return prazoDeEntrega;
    }

    public void setPrazoDeEntrega(Integer prazoDeEntrega) {
        this.prazoDeEntrega = prazoDeEntrega;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public Integer getPrazoExecucao() {
        return prazoExecucao;
    }

    public void setPrazoExecucao(Integer prazoExecucao) {
        this.prazoExecucao = prazoExecucao;
    }

    public TipoPrazo getTipoPrazoDeExecucao() {
        return tipoPrazoDeExecucao;
    }

    public void setTipoPrazoDeExecucao(TipoPrazo tipoPrazoDeExecucao) {
        this.tipoPrazoDeExecucao = tipoPrazoDeExecucao;
    }

    public CriterioTecnicaSolicitacao getCriterioTecnicaSolicitacao() {
        return criterioTecnicaSolicitacao;
    }

    public void setCriterioTecnicaSolicitacao(CriterioTecnicaSolicitacao criterioTecnicaSolicitacao) {
        this.criterioTecnicaSolicitacao = criterioTecnicaSolicitacao;
    }

    public CriterioPrecoSolicitacao getCriterioPrecoSolicitacao() {
        return criterioPrecoSolicitacao;
    }

    public void setCriterioPrecoSolicitacao(CriterioPrecoSolicitacao criterioPrecoSolicitacao) {
        this.criterioPrecoSolicitacao = criterioPrecoSolicitacao;
    }

    public ClassificacaoFinalSolicitacao getClassificacaoFinalSolicitacao() {
        return classificacaoFinalSolicitacao;
    }

    public void setClassificacaoFinalSolicitacao(ClassificacaoFinalSolicitacao classificacaoFinalSolicitacao) {
        this.classificacaoFinalSolicitacao = classificacaoFinalSolicitacao;
    }

    public List<LoteSolicitacaoMaterial> getLoteSolicitacaoMaterials() {
        if (loteSolicitacaoMaterials != null) {
            Collections.sort(loteSolicitacaoMaterials, new Comparator<LoteSolicitacaoMaterial>() {
                @Override
                public int compare(LoteSolicitacaoMaterial o1, LoteSolicitacaoMaterial o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
        }
        return loteSolicitacaoMaterials;
    }

    public void setLoteSolicitacaoMaterials(List<LoteSolicitacaoMaterial> loteSolicitacaoMaterials) {
        this.loteSolicitacaoMaterials = loteSolicitacaoMaterials;
    }

    public PessoaFisica getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(PessoaFisica solicitante) {
        this.solicitante = solicitante;
    }

    public Integer getVigencia() {
        return vigencia;
    }

    public void setVigencia(Integer vigencia) {
        this.vigencia = vigencia;
    }

    public TipoPrazo getTipoPrazoDeVigencia() {
        return tipoPrazoDeVigencia;
    }

    public void setTipoPrazoDeVigencia(TipoPrazo tipoPrazoDeVigencia) {
        this.tipoPrazoDeVigencia = tipoPrazoDeVigencia;
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

    public List<UnidadeSolicitacaoMaterial> getUnidadesParticipantes() {
        return unidadesParticipantes;
    }

    public void setUnidadesParticipantes(List<UnidadeSolicitacaoMaterial> unidadesParticipantes) {
        this.unidadesParticipantes = unidadesParticipantes;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public TipoNaturezaDoProcedimentoLicitacao getTipoNaturezaDoProcedimento() {
        return tipoNaturezaDoProcedimento;
    }

    public void setTipoNaturezaDoProcedimento(TipoNaturezaDoProcedimentoLicitacao tipoNaturezaDoProcedimento) {
        this.tipoNaturezaDoProcedimento = tipoNaturezaDoProcedimento;
    }

    public CriterioProcessamentoPrecoItemCotacao getCriterio() {
        return criterio;
    }

    public void setCriterio(CriterioProcessamentoPrecoItemCotacao criterio) {
        this.criterio = criterio;
    }

    public String getJustificativaPresencial() {
        return justificativaPresencial;
    }

    public void setJustificativaPresencial(String justificativaPresencial) {
        this.justificativaPresencial = justificativaPresencial;
    }

    public Cotacao getCotacao() {
        return cotacao;
    }

    public void setCotacao(Cotacao cotacao) {
        this.cotacao = cotacao;
    }

    public List<SolicitacaoCompraDotacao> getDotacoes() {
        return dotacoes;
    }

    public void setDotacoes(List<SolicitacaoCompraDotacao> dotacoes) {
        this.dotacoes = dotacoes;
    }

    @Override
    public String toString() {
        return "Nº - " + numero + "/" + exercicio.getAno() + " " + descricao;
    }

    public String toStringAutoComplete() {
        StringBuilder sb = new StringBuilder("");
        String meio = " - ";
        if (numero != null) {
            sb.append("Nº ").append(this.numero).append("/");
        }
        if (exercicio != null) {
            sb.append(this.exercicio).append(meio);
        }
        if (descricao != null) {
            sb.append(this.getDescricaoReduzida());
        }
        return sb.toString();
    }

    public String getDescricaoReduzida() {
        if (descricao != null && descricao.length() > 100) {
            return descricao.substring(0, 99) + "...";
        }
        return descricao;
    }

    public Boolean isExercicioProcessoDiferenteExercicioAtual(Date dataOperacao) {
        try {
            return Util.isExercicioLogadoDiferenteExercicioAtual(dataOperacao);
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    public Boolean isRegistroDePrecos() {
        try {
            if (TipoNaturezaDoProcedimentoLicitacao.getTiposDeNaturezaDeRegistroDePreco().contains(getTipoNaturezaDoProcedimento())) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    public Boolean isDispensaDeLicitacao() {
        try {
            return getModalidadeLicitacao().isDispensaLicitacao() || getModalidadeLicitacao().isInexigibilidade();
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    public Boolean isTecnicaEPreco() {
        try {
            return TipoAvaliacaoLicitacao.TECNICA_PRECO.equals(getTipoAvaliacao());
        } catch (NullPointerException npe) {
            return Boolean.FALSE;
        }
    }

    public BigDecimal getValorTotalLotes() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasLotes()) {
            for (LoteSolicitacaoMaterial lote : getLoteSolicitacaoMaterials()) {
                total = total.add(lote.getValor());
            }
        }
        return total;
    }

    public boolean hasNaturezaProcecimento() {
        return getModalidadeLicitacao() != null
            && (getModalidadeLicitacao().isPregao()
            || getModalidadeLicitacao().isRDC()
            || getModalidadeLicitacao().isConcorrencia()
            || getModalidadeLicitacao().isLeilao()
            || getModalidadeLicitacao().isInexigibilidade()
            || getModalidadeLicitacao().isDispensaLicitacao()
            || getModalidadeLicitacao().isCredenciamento());
    }

    public boolean hasLotes() {
        return this.getLoteSolicitacaoMaterials() != null && !this.getLoteSolicitacaoMaterials().isEmpty();
    }

    public void limparLotes() {
        loteSolicitacaoMaterials.clear();
    }

    public String getJustificativaCotacao() {
        return justificativaCotacao;
    }

    public void setJustificativaCotacao(String justificativaCotacao) {
        this.justificativaCotacao = justificativaCotacao;
    }

    public UsuarioSistema getUsuarioCriacao() {
        return usuarioCriacao;
    }

    public void setUsuarioCriacao(UsuarioSistema usuarioCriacao) {
        this.usuarioCriacao = usuarioCriacao;
    }

    public Date getDataOrcamentoObra() {
        return dataOrcamentoObra;
    }

    public void setDataOrcamentoObra(Date dataOrcamentoObra) {
        this.dataOrcamentoObra = dataOrcamentoObra;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
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
        return TipoMovimentoProcessoLicitatorio.SOLICITACAO_COMPRA;
    }

    public List<SolicitacaoMaterialDocumentoOficial> getDocumentosOficiais() {
        return documentosOficiais;
    }

    public void setDocumentosOficiais(List<SolicitacaoMaterialDocumentoOficial> documentosOficiais) {
        this.documentosOficiais = documentosOficiais;
    }

    public Boolean getOrcamentoSigiloso() {
        return orcamentoSigiloso;
    }

    public void setOrcamentoSigiloso(Boolean orcamentoSigiloso) {
        this.orcamentoSigiloso = orcamentoSigiloso;
    }

    public String getJustificativaSigilo() {
        return justificativaSigilo;
    }

    public void setJustificativaSigilo(String justificativaSigilo) {
        this.justificativaSigilo = justificativaSigilo;
    }

    public ModoDisputa getModoDisputa() {
        return modoDisputa;
    }

    public void setModoDisputa(ModoDisputa modoDisputa) {
        this.modoDisputa = modoDisputa;
    }

    public AmparoLegal getAmparoLegal() {
        return amparoLegal;
    }

    public void setAmparoLegal(AmparoLegal amparoLegal) {
        this.amparoLegal = amparoLegal;
    }

    public TipoGrauPrioridade getTipoGrauPrioridade() {
        return tipoGrauPrioridade;
    }

    public void setTipoGrauPrioridade(TipoGrauPrioridade tipoGrauPrioridade) {
        this.tipoGrauPrioridade = tipoGrauPrioridade;
    }

    public String getDescricaoNecessidade() {
        return descricaoNecessidade;
    }

    public void setDescricaoNecessidade(String descricaoNecessidade) {
        this.descricaoNecessidade = descricaoNecessidade;
    }

    public String getJustificativaApuracao() {
        return justificativaApuracao;
    }

    public void setJustificativaApuracao(String justificativaApuracao) {
        this.justificativaApuracao = justificativaApuracao;
    }

    public Date getInicioExecucao() {
        return inicioExecucao;
    }

    public void setInicioExecucao(Date inicioExecucao) {
        this.inicioExecucao = inicioExecucao;
    }

    public String getDescricaoComplementarPrazo() {
        return descricaoComplementarPrazo;
    }

    public void setDescricaoComplementarPrazo(String descricaoComplementarPrazo) {
        this.descricaoComplementarPrazo = descricaoComplementarPrazo;
    }

    public String getContratacoesCorrelatas() {
        return contratacoesCorrelatas;
    }

    public void setContratacoesCorrelatas(String contratacoesCorrelatas) {
        this.contratacoesCorrelatas = contratacoesCorrelatas;
    }

    public String getContratacoesIndependentes() {
        return contratacoesIndependentes;
    }

    public void setContratacoesIndependentes(String contratacoesIndependentes) {
        this.contratacoesIndependentes = contratacoesIndependentes;
    }

    public AvaliacaoSolicitacaoDeCompra getAvaliacaoAtual() {
        AvaliacaoSolicitacaoDeCompra avaliacaoAtual = null;
        if (getAvaliacoes() != null && !getAvaliacoes().isEmpty()) {
            for (AvaliacaoSolicitacaoDeCompra avaliacao : getAvaliacoes()) {
                if (avaliacaoAtual == null || avaliacaoAtual.getId().compareTo(avaliacao.getId()) > 0) {
                    avaliacaoAtual = avaliacao;
                }
            }
        }
        return avaliacaoAtual;
    }
}
