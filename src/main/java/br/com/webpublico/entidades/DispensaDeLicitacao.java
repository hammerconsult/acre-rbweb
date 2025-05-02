package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 09/07/14
 * Time: 17:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Dispensa/Inexigibilidade")
public class DispensaDeLicitacao extends SuperEntidade implements Serializable, ValidadorEntidade, EntidadeDetendorDocumentoLicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Processo de Compra")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ProcessoDeCompra processoDeCompra;

    @Etiqueta("Situação")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private SituacaoDispensaDeLicitacao situacao;

    @Etiqueta("Modalidade")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private ModalidadeLicitacao modalidade;

    @Etiqueta("Natureza do Procedimento")
    @Enumerated(EnumType.STRING)
    private TipoNaturezaDoProcedimentoLicitacao naturezaProcedimento;

    @Etiqueta("Número da Modalidade")
    @Tabelavel
    @Pesquisavel
    private Integer numeroDaDispensa;

    @Etiqueta("Exercício da Dispensa")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Exercicio exercicioDaDispensa;

    @Etiqueta("Responsável")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Tipo de Avaliação")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoAvaliacaoLicitacao tipoDeAvaliacao;

    @Etiqueta("Tipo de Apuração")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoApuracaoLicitacao tipoDeApuracao;

    @Etiqueta("Resumo do Objeto")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String resumoDoObjeto;

    @Etiqueta("Justificativa")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String justificativa;

    @Etiqueta("Fundamentação Legal")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String fundamentacaoLegal;

    @Etiqueta("Regime de Execução")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private RegimeDeExecucao regimeDeExecucao;

    @Etiqueta("Valor Máximo da Compra")
    @Monetario
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private BigDecimal valorMaximo;

    @Etiqueta("Prazo de Execução")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Integer prazoDeExecucao;

    @Etiqueta("Tipo Prazo de Execução")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoDeExecucao;

    @Etiqueta("Prazo de Execução")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Integer prazoDeVigencia;

    @Etiqueta("Tipo Prazo de Vigência")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoDeVigencia;

    @Etiqueta("Local de Entrega")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String localDeEntrega;

    @Etiqueta("Forma de Pagamento")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private String formaDePagamento;

    @Etiqueta("Data da Publicação do Extrato")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataDaDispensa;

    @Etiqueta("Data de Encerramento")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel(campoSelecionado = false)
    private Date encerradaEm;

    @Invisivel
    @Transient
    private Long criadoEm;

    @Transient
    private HierarquiaOrganizacional unidadeAdministrativa;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dispensaDeLicitacao", orphanRemoval = true)
    private List<DocumentoDispensaDeLicitacao> documentos;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dispensaDeLicitacao", orphanRemoval = true)
    private List<ParecerDispensaDeLicitacao> pareceres;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dispensaDeLicitacao", orphanRemoval = true)
    private List<PublicacaoDispensaDeLicitacao> publicacoes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dispensaDeLicitacao", orphanRemoval = true)
    private List<FornecedorDispensaDeLicitacao> fornecedores;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @Etiqueta("Link do Sistema Origem")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Length(maximo = 512)
    private String linkSistemaOrigem;

    @Etiqueta("Sequencial Criado pelo PNCP ao Realizar o Envio da Licitação")
    @Tabelavel(campoSelecionado = false)
    private String sequencialIdPncp;

    @Etiqueta("Id Criado pelo PNCP ao Realizar o Envio da Licitação")
    @Tabelavel(campoSelecionado = false)
    private String idPncp;

    public DispensaDeLicitacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoDeCompra getProcessoDeCompra() {
        return processoDeCompra;
    }

    public void setProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        this.processoDeCompra = processoDeCompra;
    }

    public ModalidadeLicitacao getModalidade() {
        return modalidade;
    }

    public void setModalidade(ModalidadeLicitacao modalidade) {
        this.modalidade = modalidade;
    }

    public TipoNaturezaDoProcedimentoLicitacao getNaturezaProcedimento() {
        return naturezaProcedimento;
    }

    public void setNaturezaProcedimento(TipoNaturezaDoProcedimentoLicitacao naturezaProcedimento) {
        this.naturezaProcedimento = naturezaProcedimento;
    }

    public Integer getNumeroDaDispensa() {
        return numeroDaDispensa;
    }

    public void setNumeroDaDispensa(Integer numeroDaDispensa) {
        this.numeroDaDispensa = numeroDaDispensa;
    }

    public Exercicio getExercicioDaDispensa() {
        return exercicioDaDispensa;
    }

    public void setExercicioDaDispensa(Exercicio exercicioDaDispensa) {
        this.exercicioDaDispensa = exercicioDaDispensa;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoAvaliacaoLicitacao getTipoDeAvaliacao() {
        return tipoDeAvaliacao;
    }

    public void setTipoDeAvaliacao(TipoAvaliacaoLicitacao tipoDeAvaliacao) {
        this.tipoDeAvaliacao = tipoDeAvaliacao;
    }

    public TipoApuracaoLicitacao getTipoDeApuracao() {
        return tipoDeApuracao;
    }

    public void setTipoDeApuracao(TipoApuracaoLicitacao tipoDeApuracao) {
        this.tipoDeApuracao = tipoDeApuracao;
    }

    public String getResumoDoObjeto() {
        return resumoDoObjeto;
    }

    public void setResumoDoObjeto(String resumoDoObjeto) {
        if (resumoDoObjeto != null) {
            resumoDoObjeto = Util.cortarString(resumoDoObjeto, 255);
        }
        this.resumoDoObjeto = resumoDoObjeto;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        if (justificativa != null) {
            justificativa = Util.cortarString(justificativa, 255);
        }
        this.justificativa = justificativa;
    }

    public String getFundamentacaoLegal() {
        return fundamentacaoLegal;
    }

    public void setFundamentacaoLegal(String fundamentacaoLegal) {
        if (fundamentacaoLegal != null) {
            fundamentacaoLegal = Util.cortarString(fundamentacaoLegal, 255);
        }
        this.fundamentacaoLegal = fundamentacaoLegal;
    }

    public RegimeDeExecucao getRegimeDeExecucao() {
        return regimeDeExecucao;
    }

    public void setRegimeDeExecucao(RegimeDeExecucao regimeDeExecucao) {
        this.regimeDeExecucao = regimeDeExecucao;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Integer getPrazoDeExecucao() {
        return prazoDeExecucao;
    }

    public void setPrazoDeExecucao(Integer prazoDeExecucao) {
        this.prazoDeExecucao = prazoDeExecucao;
    }

    public TipoPrazo getTipoPrazoDeExecucao() {
        return tipoPrazoDeExecucao;
    }

    public void setTipoPrazoDeExecucao(TipoPrazo tipoPrazoDeExecucao) {
        this.tipoPrazoDeExecucao = tipoPrazoDeExecucao;
    }

    public Integer getPrazoDeVigencia() {
        return prazoDeVigencia;
    }

    public void setPrazoDeVigencia(Integer prazoDeVigencia) {
        this.prazoDeVigencia = prazoDeVigencia;
    }

    public TipoPrazo getTipoPrazoDeVigencia() {
        return tipoPrazoDeVigencia;
    }

    public void setTipoPrazoDeVigencia(TipoPrazo tipoPrazoDeVigencia) {
        this.tipoPrazoDeVigencia = tipoPrazoDeVigencia;
    }

    public String getLocalDeEntrega() {
        return localDeEntrega;
    }

    public void setLocalDeEntrega(String localDeEntrega) {
        this.localDeEntrega = localDeEntrega;
    }

    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public void setFormaDePagamento(String formaDePagamento) {
        this.formaDePagamento = formaDePagamento;
    }

    public Date getDataDaDispensa() {
        return dataDaDispensa;
    }

    public void setDataDaDispensa(Date dataDaDispensa) {
        this.dataDaDispensa = dataDaDispensa;
    }

    public Date getEncerradaEm() {
        return encerradaEm;
    }

    public void setEncerradaEm(Date encerradaEm) {
        this.encerradaEm = encerradaEm;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<DocumentoDispensaDeLicitacao> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<DocumentoDispensaDeLicitacao> documentos) {
        this.documentos = documentos;
    }

    public List<ParecerDispensaDeLicitacao> getPareceres() {
        return pareceres;
    }

    public void setPareceres(List<ParecerDispensaDeLicitacao> pareceres) {
        this.pareceres = pareceres;
    }

    public List<PublicacaoDispensaDeLicitacao> getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(List<PublicacaoDispensaDeLicitacao> publicacoes) {
        this.publicacoes = publicacoes;
    }

    public List<FornecedorDispensaDeLicitacao> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<FornecedorDispensaDeLicitacao> fornecedores) {
        this.fornecedores = fornecedores;
    }

    public SituacaoDispensaDeLicitacao getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoDispensaDeLicitacao situacao) {
        this.situacao = situacao;
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
        return TipoMovimentoProcessoLicitatorio.DISPENSA_LICITACAO_INEXIGIBILIDADE;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public String toString() {
        return "Nº " + numeroDaDispensa + " / " + exercicioDaDispensa;
    }

    public String toStringAutoComplete() {
        return "Nº " + numeroDaDispensa + " - " + exercicioDaDispensa + " - " + resumoDoObjeto;
    }

    public boolean isDispensaEmElaboracao() {
        return SituacaoDispensaDeLicitacao.EM_ELABORACAO.equals(this.getSituacao());
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(HierarquiaOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
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
}
