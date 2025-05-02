package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CaracterizadorDeBemMovel;
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
 * User: Desenvolvimento
 * Date: 08/06/15
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Table(name = "BEMMOVELPROPRIEDADETERCEIR")
public class BemMovelPropriedadeTerceiro extends SuperEntidade implements CaracterizadorDeBemMovel, PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Unidade Administrativa")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Unidade Orçamentária")
    @Obrigatorio
    @Invisivel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Localização")
    private String localizacao;

    /*@Pesquisavel
    @Tabelavel
    @Etiqueta("Hierarquia Administrativa")
    @Obrigatorio*/
    @Invisivel
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaAdministrativa;

    /*@Pesquisavel
    @Tabelavel
    @Etiqueta("Hierarquia Orçamentária")
    @Obrigatorio*/
    @Invisivel
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Registro Patrimonial")
    private String codigoPatrimonio;

    @Pesquisavel
    @Etiqueta("Registro Patrimonial")
    private String codigoAnterior;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Item Patrimonial")
    @ManyToOne
    private ObjetoCompra item;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição do Bem")
    private String descricaoBem;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Marca")
    private String marca;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Modelo")
    private String modelo;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Estado de Conservação")
    @Enumerated(EnumType.STRING)
    private EstadoConservacaoBem estadoConservacaoBem;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Situação de Conservação")
    @Enumerated(EnumType.STRING)
    private SituacaoConservacaoBem situacaoConservacaoBem;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Data de Aquisição")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAquisicao;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @UFM
    @Etiqueta("Valor do Bem (R$)")
    private BigDecimal valorBem;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Fornecedor")
    @ManyToOne
    private Pessoa fornecedor;

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

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data do Levantamento")
    private Date dataLevantamento;

    private String documentoAdjudicacao;

    @Transient
    @Invisivel
    private GrupoBem grupoBem;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Grupo")
    private TipoGrupo tipoGrupo;

    @ManyToOne(cascade = CascadeType.ALL)
    private DetentorOrigemRecurso detentorOrigemRecurso;

    @ManyToOne
    private Contrato contrato;

    @ManyToOne
    @Obrigatorio
    private TipoVinculoBem tipoVinculoBem;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public BemMovelPropriedadeTerceiro() {
        super();
        this.detentorOrigemRecurso = new DetentorOrigemRecurso();
        this.dataLevantamento = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    @Override
    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return detentorOrigemRecurso;
    }

    public void setDetentorOrigemRecurso(DetentorOrigemRecurso detentorOrigemRecurso) {
        this.detentorOrigemRecurso = detentorOrigemRecurso;
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
    public ObjetoCompra getObjetoCompra() {
        return item;
    }

    @Override
    public String getRegistroAnterior() {
        return codigoAnterior;
    }

    @Override
    public GrupoBem getGrupoBem() {
        return this.grupoBem;
    }

    @Override
    public void setGrupoBem(GrupoBem gb) {
        this.grupoBem = gb;
    }

    @Override
    public String getDescricaoDoBem() {
        return descricaoBem;
    }

    @Override
    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return this.item.getGrupoObjetoCompra();
    }

    @Override
    public BigDecimal getValorDoBem() {
        return this.valorBem;
    }

    @Override
    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio.trim();
    }

    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    public ObjetoCompra getItem() {
        return item;
    }

    public void setItem(ObjetoCompra item) {
        this.item = item;
    }

    @Override
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    @Override
    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @Override
    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    @Override
    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return TipoAquisicaoBem.COMODATO;
    }

    @Override
    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
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

    @Override
    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataLevantamento() {
        return dataLevantamento;
    }

    public void setDataLevantamento(Date dataLevantamento) {
        this.dataLevantamento = dataLevantamento;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getDocumentoAdjudicacao() {
        return documentoAdjudicacao;
    }

    public void setDocumentoAdjudicacao(String documentoAjudicacao) {
        this.documentoAdjudicacao = documentoAjudicacao;
    }

    @Override
    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public TipoVinculoBem getTipoVinculoBem() {
        return tipoVinculoBem;
    }

    public void setTipoVinculoBem(TipoVinculoBem tipoVinculoBem) {
        this.tipoVinculoBem = tipoVinculoBem;
    }

    public void validarNegocio(Date dataAtual) {
        ValidacaoException ve = new ValidacaoException();

        if (valorBem == null || valorBem.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O valor do bem deve ser positivo.");
        }

        if (notaEmpenho != null && notaEmpenho.compareTo(Integer.valueOf(0)) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O número da nota de empenho deve ser positivo.");
        }

        if (!dataAquisicao.before(dataAtual)) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data da aquisição deve ser anterior a data atual.");
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

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public List<BemNotaFiscal> getNotasFiscais() {
        return Lists.newArrayList();
    }

    @Override
    public String toString() {
        return (modelo != null ? modelo : "") + " " + (marca != null ? marca : "");
    }
}
