package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MGA
 * Date: 03/09/14
 * Time: 09:41
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "DOCTOCOMPROBLEVBEMIMOVEL")
@Etiqueta(value = "Documento Comprobatório", genero = "M")
public class DocumentoComprobatorioLevantamentoBemImovel extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Registro Patrimonial")
    @ManyToOne
    @Tabelavel
    private LevantamentoBemImovel levantamentoBemImovel;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 1)
    @Etiqueta("Número do Documento")
    private String numero;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta("Data do Documento")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataDocumento;

    @Obrigatorio
    @Etiqueta("Tipo do Documento")
    @Pesquisavel
    @Tabelavel(ordemApresentacao = 3)
    @ManyToOne
    private TipoDocumentoFiscal tipoDocumentoFiscal;

    @Pesquisavel
    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Unidade Administrativa")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;

    @Pesquisavel
    @Tabelavel(ordemApresentacao = 5)
    @Etiqueta("Unidade Orçamentária")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Etiqueta("Valor Referente ao Bem (R$)")
    @Tabelavel(ordemApresentacao = 6)
    @Monetario
    @Obrigatorio
    private BigDecimal valor;

    @Etiqueta("Série")
    private String serie;

    @Etiqueta("UF")
    @ManyToOne
    private UF uf;

    @Obrigatorio
    @Etiqueta("Data da Contabilização")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataContabilizacao;

    @Obrigatorio
    @Invisivel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataVinculoLevantamento;

    @Etiqueta("Tipo de Aquisição")
    @Enumerated(EnumType.STRING)
    private TipoAquisicaoBem tipoAquisicaoBem;

    @Etiqueta("Grupo Patrimonial")
    @ManyToOne
    private GrupoBem grupoBem;

    @OneToMany(mappedBy = "documentoComprobatorio", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<EmpenhoLevantamentoImovel> empenhos;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;

    private String observacao;

    public DocumentoComprobatorioLevantamentoBemImovel() {
        this.empenhos = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LevantamentoBemImovel getLevantamentoBemImovel() {
        return levantamentoBemImovel;
    }

    public void setLevantamentoBemImovel(LevantamentoBemImovel levantamentoBemImovel) {
        this.levantamentoBemImovel = levantamentoBemImovel;
    }

    public TipoDocumentoFiscal getTipoDocumentoFiscal() {
        return tipoDocumentoFiscal;
    }

    public void setTipoDocumentoFiscal(TipoDocumentoFiscal tipoDocumentoFiscal) {
        this.tipoDocumentoFiscal = tipoDocumentoFiscal;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public List<EmpenhoLevantamentoImovel> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<EmpenhoLevantamentoImovel> empenhos) {
        this.empenhos = empenhos;
    }

    public Date getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(Date dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    public Date getDataContabilizacao() {
        return dataContabilizacao;
    }

    public void setDataContabilizacao(Date dataContabilizacao) {
        this.dataContabilizacao = dataContabilizacao;
    }

    public Date getDataVinculoLevantamento() {
        return dataVinculoLevantamento;
    }

    public void setDataVinculoLevantamento(Date dataVinculoLevantamento) {
        this.dataVinculoLevantamento = dataVinculoLevantamento;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalAdministrativa() {
        return hierarquiaOrganizacionalAdministrativa;
    }

    public void setHierarquiaOrganizacionalAdministrativa(HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa) {
        this.hierarquiaOrganizacionalAdministrativa = hierarquiaOrganizacionalAdministrativa;

        if (this.hierarquiaOrganizacionalAdministrativa != null) {
            this.unidadeAdministrativa = this.hierarquiaOrganizacionalAdministrativa.getSubordinada();
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public void adicionarEmpenho(EmpenhoLevantamentoImovel eli) {
        eli.setDocumentoComprobatorio(this);
        eli.validarCamposObrigatorios();
        eli.validarRegrasDeNegocio();
        setEmpenhos(Util.adicionarObjetoEmLista(empenhos, eli));
    }

    public void validarRegrasDeNegocio(Date dataDeReferencia) {
        ValidacaoException ve = new ValidacaoException();

        if (!empenhos.isEmpty()) {
            if (!new BigDecimal(getValorTotalDocumentos().stripTrailingZeros().toPlainString()).equals(new BigDecimal(valor.stripTrailingZeros().toPlainString()))) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O valor do documento deve ser igual a soma dos valores de seus empenhos.");
            }
        } else if (levantamentoBemImovel.requerEmpenhos()) {
            if (dataDeReferencia == null) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data de referência deve ser informada nos parâmetros do patrimônio para completar este cadastro.");
            } else if (levantamentoBemImovel.getDataAquisicao().after(dataDeReferencia)) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Os empenhos deste documento devem ser informados.");
            }
        }

        if (valor != null && valor.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O valor referente ao bem deve ser maior que 0 (zero).");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public BigDecimal getValorTotalDocumentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (EmpenhoLevantamentoImovel empenho : empenhos) {
            total = total.add(empenho.getValorReferenteAoBem());
        }
        return total;
    }

    @Override
    public String toString() {
        return "Nº: " + numero + (dataDocumento == null ? "" : " Data: " + DataUtil.getDataFormatada(dataDocumento));
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
