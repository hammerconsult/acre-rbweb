package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 28/01/2016.
 */
@Entity
@Audited
@Table(name = "SOLICITACAOINCORPORACAOMOV")
@Etiqueta("Solicitação de Incorporação de Bens Móveis")
public class SolicitacaoIncorporacaoMovel extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Código")
    @CodigoGeradoAoConcluir
    @Pesquisavel
    @Tabelavel
    private Long codigo;

    @Etiqueta("Situação")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private SituacaoEventoBem situacao;

    @Etiqueta("Responsável")
    @Obrigatorio
    @ManyToOne
    private UsuarioSistema responsavel;

    @Etiqueta("Unidade Administrativa")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;

    @Tabelavel
    @Transient
    @Etiqueta("Unidade Administrativa")
    private String descricaoUnidadeAdm;

    @Etiqueta("Unidade Orçamentária")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Tabelavel
    @Transient
    @Etiqueta("Unidade Orçamentária")
    private String descricaoUnidadeOrc;

    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Lançamento")
    private Date dataSolicitacao;

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

    @Etiqueta("Fornecedor")
    @ManyToOne
    private Pessoa fornecedor;

    @Tabelavel
    @Etiqueta("Fornecedor")
    @Transient
    private String descricaoFornecedor;

    @Pesquisavel
    @Etiqueta("Nota de Empenho")
    private Integer notaEmpenho;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Nota de Empenho")
    private Date dataNotaEmpenho;

    @Etiqueta("Nota Fiscal")
    @Pesquisavel
    private String notaFiscal;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Nota Fiscal")
    private Date dataNotaFiscal;

    @Pesquisavel
    @Etiqueta("Observação")
    private String observacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Etiqueta("Documento de Adjudicação")
    private String documentoAdjudicacao;

    @OneToOne(cascade = CascadeType.ALL)
    private DetentorOrigemRecurso detentorOrigemRecurso;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacao")
    private List<DocumetoComprobatorioIncorporacaoMovel> documetosComprobatorio;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacao")
    private List<ItemSolicitacaoIncorporacaoMovel> itensSolicitacaoIncorporacaoMovel;

    public SolicitacaoIncorporacaoMovel() {
        super();
        this.detentorOrigemRecurso = new DetentorOrigemRecurso();
        this.documetosComprobatorio = new ArrayList<>();
        this.itensSolicitacaoIncorporacaoMovel = new ArrayList<>();
        this.situacao = SituacaoEventoBem.EM_ELABORACAO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public SituacaoEventoBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEventoBem situacao) {
        this.situacao = situacao;
    }

    public UsuarioSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UsuarioSistema responsavel) {
        this.responsavel = responsavel;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public Date getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(Date dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Integer getNotaEmpenho() {
        return notaEmpenho;
    }

    public void setNotaEmpenho(Integer notaEmpenho) {
        this.notaEmpenho = notaEmpenho;
    }

    public Date getDataNotaEmpenho() {
        return dataNotaEmpenho;
    }

    public void setDataNotaEmpenho(Date dataNotaEmpenho) {
        this.dataNotaEmpenho = dataNotaEmpenho;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public Date getDataNotaFiscal() {
        return dataNotaFiscal;
    }

    public void setDataNotaFiscal(Date dataNotaFiscal) {
        this.dataNotaFiscal = dataNotaFiscal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return detentorOrigemRecurso;
    }

    public void setDetentorOrigemRecurso(DetentorOrigemRecurso detentorOrigemRecurso) {
        this.detentorOrigemRecurso = detentorOrigemRecurso;
    }

    public List<DocumetoComprobatorioIncorporacaoMovel> getDocumetosComprobatorio() {
        return documetosComprobatorio;
    }

    public void setDocumetosComprobatorio(List<DocumetoComprobatorioIncorporacaoMovel> documetosComprobatorio) {
        this.documetosComprobatorio = documetosComprobatorio;
    }

    public List<ItemSolicitacaoIncorporacaoMovel> getItensSolicitacaoIncorporacaoMovel() {
        return itensSolicitacaoIncorporacaoMovel;
    }

    public void setItensSolicitacaoIncorporacaoMovel(List<ItemSolicitacaoIncorporacaoMovel> itensSolicitacaoIncorporacaoMovel) {
        this.itensSolicitacaoIncorporacaoMovel = itensSolicitacaoIncorporacaoMovel;
    }

    public String getDocumentoAdjudicacao() {
        return documentoAdjudicacao;
    }

    public void setDocumentoAdjudicacao(String documentoAdjudicacao) {
        this.documentoAdjudicacao = documentoAdjudicacao;
    }

    public String getDescricaoFornecedor() {
        return descricaoFornecedor;
    }

    public void setDescricaoFornecedor(String descricaoFornecedor) {
        this.descricaoFornecedor = descricaoFornecedor;
    }

    public String getDescricaoUnidadeAdm() {
        return descricaoUnidadeAdm;
    }

    public void setDescricaoUnidadeAdm(String descricaoUnidadeAdm) {
        this.descricaoUnidadeAdm = descricaoUnidadeAdm;
    }

    public String getDescricaoUnidadeOrc() {
        return descricaoUnidadeOrc;
    }

    public void setDescricaoUnidadeOrc(String descricaoUnidadeOrc) {
        this.descricaoUnidadeOrc = descricaoUnidadeOrc;
    }

    public void validarNegocio(Date dataAtual) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (TipoAquisicaoBem.COMPRA.equals(tipoAquisicaoBem)) {
            if (notaEmpenho == null
                || dataNotaEmpenho == null
                || notaFiscal == null
                || (notaFiscal != null && notaFiscal.isEmpty()) || dataNotaFiscal == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Quando o tipo de aquisição for Compra, os campos Nota de Empenho, Data Nota de Empenho, Nota Fiscal e Data Nota Fiscal devem ser preenchidos.");
            }
        }

        if (notaEmpenho != null && notaEmpenho.compareTo(Integer.valueOf(0)) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O número da nota de empenho deve ser positivo.");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dateHoje = null;
        try {
            dateHoje = sdf.parse(sdf.format(dataAtual));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dataAquisicao != null && dataAquisicao.after(dateHoje)) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data da aquisição deve ser anterior ou igual a data atual.");
        }

        if (dataNotaEmpenho != null && dataNotaEmpenho.after(dataAquisicao)) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data da nota de empenho deve ser anterior ou igual a data de aquisição.");
        }

        if (dataNotaFiscal != null && dataNotaEmpenho != null) {
            if (dataNotaFiscal.compareTo(dataNotaEmpenho) < 0) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data da nota fiscal deve ser igual ou posterior a data da nota do empenho.");
            }
        }

        if (dataNotaFiscal != null && dataNotaFiscal.compareTo(dataAquisicao) > 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data da nota fiscal deve ser anterior ou igual a data de aquisição.");
        }

        if (!detentorOrigemRecurso.temRecursos()) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Adicione ao menos uma origem de recurso.");
        }

        if (TipoAquisicaoBem.ADJUDICACAO.equals(this.getTipoAquisicaoBem()) && (this.getDocumentoAdjudicacao() == null || this.getDocumentoAdjudicacao().isEmpty())) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Informe o documento da adjudicação.");
        }

        if (this.getUnidadeAdministrativa() == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Unidade Administrativa é obrigatório!");
        }

        if (this.getUnidadeOrcamentaria() == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Unidade Orçamentária é obrigatório!");
        }

        if (this.tipoAquisicaoBem == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Tipo de Aquisição é obrigatório!");
        }

        if (this.dataAquisicao == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Data de Aquisição é obrigatório!");
        }

        if (TipoAquisicaoBem.DOACAO.equals(this.tipoAquisicaoBem) && (this.observacao == null || this.observacao.trim().isEmpty())) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Observação é obrigatório!");
        }

        if (itensSolicitacaoIncorporacaoMovel == null || itensSolicitacaoIncorporacaoMovel.isEmpty()) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "Informe ao menos um bem para continuar.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public boolean isTipoAquisicaoBemAdjudicacao() {
        return TipoAquisicaoBem.ADJUDICACAO.equals(this.getTipoAquisicaoBem());
    }

    public boolean isEmElaboracao() {
        return SituacaoEventoBem.EM_ELABORACAO.equals(situacao);
    }

    public boolean isRecusado() {
        return SituacaoEventoBem.RECUSADO.equals(situacao);
    }

    public boolean isFinalizada() {
        return SituacaoEventoBem.FINALIZADO.equals(situacao);
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + DataUtil.getDataFormatada(dataSolicitacao) + " - " + responsavel;
        } catch (Exception ex) {
            return "";
        }
    }
}
