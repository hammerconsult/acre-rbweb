package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CaracterizadorDeBemImovel;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 02/09/14
 * Time: 18:25
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Levantamento de Bem Imóvel", genero = "M")
public class LevantamentoBemImovel extends SuperEntidade implements CaracterizadorDeBemImovel, PossuidorArquivo {

    public static final TipoAquisicaoBem[] tiposDeAquisicaoPermitidosNoCadastro = {
        TipoAquisicaoBem.ADJUDICACAO,
        TipoAquisicaoBem.COMPRA,
        TipoAquisicaoBem.CONTRATO,
        TipoAquisicaoBem.CONVENIO,
        TipoAquisicaoBem.CONSTRUCAO,
        TipoAquisicaoBem.DACAO,
        TipoAquisicaoBem.DESAPROPRIACAO,
        TipoAquisicaoBem.DOACAO,
        TipoAquisicaoBem.PERMUTA,
        TipoAquisicaoBem.USUCAPIAO
    };

    public static final TipoAquisicaoBem[] tiposDeAquisicaoQueRequeremEmpenho = {
        TipoAquisicaoBem.COMPRA,
        TipoAquisicaoBem.CONTRATO,
        TipoAquisicaoBem.CONVENIO,
        TipoAquisicaoBem.CONSTRUCAO,
        TipoAquisicaoBem.DESAPROPRIACAO
    };

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data do Levantamento")
    @Obrigatorio
    private Date dataLevantamento;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Usuário do Levantamento")
    @ManyToOne
    @Obrigatorio
    private UsuarioSistema usuarioLevantamento;

    @Etiqueta("Unidade Administrativa")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;

    @Etiqueta("Unidade Orçamentária")
    @Obrigatorio
    @Invisivel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo Patrimonial")
    @ManyToOne
    private GrupoBem grupoBem;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Registro Patrimonial")
    private String codigoPatrimonio;

    @Pesquisavel
    @Etiqueta("Número de Registro")
    private String numeroRegistro;

    @Pesquisavel
    @Etiqueta("BCI")
    private String bci;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição do Imóvel")
    private String descricaoImovel;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Localização")
    private String localizacao;

    private String observacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Condição de Ocupação")
    @ManyToOne
    private CondicaoDeOcupacao condicaoDeOcupacao;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Aquisição")
    @Enumerated(EnumType.STRING)
    private TipoAquisicaoBem tipoAquisicaoBem;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data de Aquisição")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAquisicao;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Monetario
    @Etiqueta("Valor Atual do Bem (R$)")
    private BigDecimal valorBem;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Pessoa")
    @ManyToOne
    private Pessoa fornecedor;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Estado de Conservação")
    @Enumerated(EnumType.STRING)
    private EstadoConservacaoBem estadoConservacaoBem;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Situação de Conservação")
    @Enumerated(EnumType.STRING)
    private SituacaoConservacaoBem situacaoConservacaoBem;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Obrigatorio
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorOrigemRecurso detentorOrigemRecurso;

    @OneToMany(mappedBy = "levantamentoBemImovel", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<DocumentoComprobatorioLevantamentoBemImovel> documentosComprobatorios;

    @ManyToOne(cascade = CascadeType.ALL)
    private Seguradora seguradora;

    @ManyToOne(cascade = CascadeType.ALL)
    private Garantia garantia;

    @Etiqueta(value = "Cadastro Imobiliário")
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoLevantamentoImovel situacaoLevantamento;

    public LevantamentoBemImovel() {
        this.detentorOrigemRecurso = new DetentorOrigemRecurso();
        this.estadoConservacaoBem = EstadoConservacaoBem.OPERACIONAL;
        this.situacaoConservacaoBem = SituacaoConservacaoBem.USO_NORMAL;
        this.situacaoLevantamento = SituacaoLevantamentoImovel.AGUARDANDO_EFETIVACAO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getDataDaOperacao() {
        return dataLevantamento;
    }

    @Override
    public Date getDataLancamento() {
        return dataAquisicao;
    }

    @Override
    public BigDecimal getValorDoBem() {
        return valorBem;
    }

    public Date getDataLevantamento() {
        return dataLevantamento;
    }

    public void setDataLevantamento(Date dataLevantamento) {
        this.dataLevantamento = dataLevantamento;
    }

    public UsuarioSistema getUsuarioLevantamento() {
        return usuarioLevantamento;
    }

    public void setUsuarioLevantamento(UsuarioSistema usuarioLevantamento) {
        this.usuarioLevantamento = usuarioLevantamento;
    }

    @Override
    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    @Override
    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    @Override
    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    @Override
    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
    }

    @Override
    public String getDescricaoDoBem() {
        return descricaoImovel;
    }

    @Override
    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(String numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    @Override
    public String getBci() {
        return cadastroImobiliario != null ? cadastroImobiliario.getNumeroCadastro() : bci;
    }

    public void setBci(String bci) {
        this.bci = bci;
    }

    public String getDescricaoImovel() {
        return descricaoImovel;
    }

    public void setDescricaoImovel(String descricaoImovel) {
        this.descricaoImovel = descricaoImovel;
    }

    @Override
    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public CondicaoDeOcupacao getCondicaoDeOcupacao() {
        return condicaoDeOcupacao;
    }

    public void setCondicaoDeOcupacao(CondicaoDeOcupacao condicaoDeOcupacao) {
        this.condicaoDeOcupacao = condicaoDeOcupacao;
    }

    @Override
    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    @Override
    public String getModelo() {
        return null;
    }

    @Override
    public String getMarca() {
        return null;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public BigDecimal getValorBem() {
        return valorBem;
    }

    public void setValorBem(BigDecimal valorBem) {
        this.valorBem = valorBem;
    }

    @Override
    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    @Override
    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    @Override
    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    @Override
    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return null;
    }

    @Override
    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return detentorOrigemRecurso;
    }

    public void setDetentorOrigemRecurso(DetentorOrigemRecurso detentorOrigemRecurso) {
        this.detentorOrigemRecurso = detentorOrigemRecurso;
    }

    public List<OrigemRecursoBem> getListaDeOriemRecursoBem() {
        return this.detentorOrigemRecurso.getListaDeOriemRecursoBem();
    }

    public void adicionarOrigemRecurso(OrigemRecursoBem origemRecursoBem) {
        this.detentorOrigemRecurso.adicionarOrigemRecurso(origemRecursoBem);
    }

    public List<DocumentoComprobatorioLevantamentoBemImovel> getDocumentosComprobatorios() {
        return documentosComprobatorios;
    }

    public void setDocumentosComprobatorios(List<DocumentoComprobatorioLevantamentoBemImovel> documentosComprobatorios) {
        this.documentosComprobatorios = documentosComprobatorios;
    }

    public Seguradora getSeguradora() {
        return seguradora;
    }

    public void setSeguradora(Seguradora seguradora) {
        this.seguradora = seguradora;
    }

    public SituacaoLevantamentoImovel getSituacaoLevantamento() {
        return situacaoLevantamento;
    }

    public void setSituacaoLevantamento(SituacaoLevantamentoImovel situacaoLevantamento) {
        this.situacaoLevantamento = situacaoLevantamento;
    }

    @Override
    public String toString() {
        return "Registro Patrimonial: " + codigoPatrimonio + " - Descrição: " + descricaoImovel;
    }

    public BigDecimal getValorTotalDosDocumentos() {
        BigDecimal total = BigDecimal.ZERO;

        for (DocumentoComprobatorioLevantamentoBemImovel documentosComprobatorio : documentosComprobatorios) {
            total = total.add(documentosComprobatorio.getValor());
        }

        return total;
    }

    public void validarRegrasDeNegocio() {
        ValidacaoException ve = new ValidacaoException();
        if (!detentorOrigemRecurso.temRecursos()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe pelo menos uma origem de recurso.");
        }
        if (valorBem != null && valorBem.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O valor do bem deve ser maior que 0 (zero).");
        }
        ve.lancarException();
    }

    public void podeAdicionarDocumento(DocumentoComprobatorioLevantamentoBemImovel documento) {
        BigDecimal valorComparativo = documentosComprobatorios.contains(documento) ? getValorTotalDosDocumentos() : getValorTotalDosDocumentos().add(documento.getValor());

        if (valorComparativo.compareTo(valorBem) > 0) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O valor total dos documentos comprobatórios deve ser menor ou igual ao valor do bem imóvel.");
            throw ve;
        }
    }

    public boolean requerEmpenhos() {
        for (TipoAquisicaoBem tab : tiposDeAquisicaoQueRequeremEmpenho) {
            if (tipoAquisicaoBem.equals(tab)) {
                return true;
            }
        }
        return false;
    }

    public Garantia getGarantia() {
        return garantia;
    }

    public void setGarantia(Garantia garantia) {
        this.garantia = garantia;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    @Override
    public List<BemNotaFiscal> getNotasFiscais() {
        return Lists.newArrayList();
    }
}
