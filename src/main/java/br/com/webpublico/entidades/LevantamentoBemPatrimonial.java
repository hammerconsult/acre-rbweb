/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CaracterizadorDeBemMovel;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Arthur Zavadski
 * @author Lucas Cheles
 */
@Entity
@Etiqueta(value = "Levantamento de Bens Móveis", genero = "M")
@Audited
public class LevantamentoBemPatrimonial extends SuperEntidade implements CaracterizadorDeBemMovel {

    public static final TipoAquisicaoBem[] tiposDeAquisicaoQueRequeremAprovacaoDoLevantamento = {
        TipoAquisicaoBem.ADJUDICACAO,
        TipoAquisicaoBem.APREENSAO,
        TipoAquisicaoBem.REDISTRIBUICAO,
        TipoAquisicaoBem.DACAO,
        TipoAquisicaoBem.AVALIACAO
    };

    public static final TipoAquisicaoBem[] tiposDeAquisicaoQueRequeremVerificacaoDaDataDeCorteParaAprovacaoDoLevantamento = {
        TipoAquisicaoBem.AVALIACAO
    };

    public static final TipoAquisicaoBem[] tiposDeAquisicaoPermitidosNoCadastro = {
        TipoAquisicaoBem.APREENSAO,
        TipoAquisicaoBem.AVALIACAO,
        TipoAquisicaoBem.COMPRA,
        TipoAquisicaoBem.COMODATO,
        TipoAquisicaoBem.CONVENIO,
        TipoAquisicaoBem.CONTRATO,
        TipoAquisicaoBem.DACAO,
        TipoAquisicaoBem.DOACAO,
        TipoAquisicaoBem.PERMUTA,
        TipoAquisicaoBem.REDISTRIBUICAO
    };
    private static final Logger logger = LoggerFactory.getLogger(LevantamentoBemPatrimonial.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Unidade Administrativa")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeAdministrativa;

    @Etiqueta("Unidade Orçamentária")
    @Obrigatorio
    @Invisivel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrcamentaria;

    /*@Pesquisavel
    @Tabelavel
    @Etiqueta("Hierarquia Administrativa")
    @Obrigatorio*/
    @Invisivel
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaAdministrativa;

    //    Usado na pesquisa genérica
    @Tabelavel
    @Transient
    @Etiqueta("Unidade Administrativa")
    private String descricaoUnidadeAdm;

    /*@Pesquisavel
    @Tabelavel
    @Etiqueta("Hierarquia Orçamentária")
    @Obrigatorio*/
    @Invisivel
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    //    Usado na pesquisa genérica
    @Tabelavel
    @Transient
    @Etiqueta("Unidade Orçamentária")
    private String descricaoUnidadeOrc;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Registro Patrimonial")
    @Obrigatorio
    private String codigoPatrimonio;

    @Pesquisavel
    @Etiqueta("Registro Patrimonial")
    private String codigoAnterior;

    @Obrigatorio
    @Etiqueta("Item Patrimonial")
    @ManyToOne
    private ObjetoCompra item;

    //    Utilizado na pesquisa generica
    @Tabelavel
    @Etiqueta("Item Patrimonial")
    @Transient
    private String descricaoItem;

    @Etiqueta("Grupo Patrimonial")
    @Obrigatorio
    @ManyToOne
    private GrupoBem grupoBem;

    @Tabelavel
    @Etiqueta("Grupo Patrimonial")
    @Transient
    private String descricaoGrupoBem;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Etiqueta("Tipo Grupo")
    private TipoGrupo tipoGrupo;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição do Bem")
    private String descricaoBem;

    @Pesquisavel
    @Etiqueta("Marca")
    private String marca;

    @Pesquisavel
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
    @Tabelavel
    @Etiqueta("Situação de Conservação")
    @Enumerated(EnumType.STRING)
    private SituacaoConservacaoBem situacaoConservacaoBem;

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
    @UFM
    @Etiqueta("Valor do Bem (R$)")
    private BigDecimal valorBem;

    @Pesquisavel
    @Etiqueta("Localização")
    private String localizacao;

    @Etiqueta("Fornecedor")
    @ManyToOne
    private Pessoa fornecedor;

    @Pesquisavel
    @Etiqueta("Nota de Empenho")
    @Obrigatorio
    private Integer notaEmpenho;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Nota de Empenho")
    private Date dataNotaEmpenho;

    @Etiqueta("Nota Fiscal")
    @Pesquisavel
    @Obrigatorio
    private String notaFiscal;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Nota Fiscal")
    @Obrigatorio
    private Date dataNotaFiscal;

    @Pesquisavel
    @Etiqueta("Observação")
    private String observacao;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data do Levantamento")
    private Date dataLevantamento;

    @OneToMany(mappedBy = "levantamentoBemPatrimonial", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrigemRecursoBem> listaDeOriemRecursoBem;

    /*Bem gerado na efetivação*/
    @ManyToOne
    private Bem bem;

    @ManyToOne(cascade = CascadeType.ALL)
    private Garantia garantia;

    @ManyToOne(cascade = CascadeType.ALL)
    private Seguradora seguradora;

    private String documentoAdjudicacao;

    //atributo para ordenação por unidade na efetivação
    @Transient
    @Invisivel
    private String codigo;

    //atributo para informação do erro na efetivação
    @Transient
    @Invisivel
    private String erro;

    @Transient
    private BigDecimal depreciacao;

    public LevantamentoBemPatrimonial() {
        super();
        this.listaDeOriemRecursoBem = new ArrayList<>();
    }

    // Construtor para Pesquisa
    public LevantamentoBemPatrimonial(LevantamentoBemPatrimonial lev, GrupoBem grupoBem) {
        this.setId(lev.getId());
        this.unidadeAdministrativa = lev.getUnidadeAdministrativa();
        this.unidadeOrcamentaria = lev.getUnidadeOrcamentaria();
        this.hierarquiaAdministrativa = lev.getHierarquiaAdministrativa();
        this.hierarquiaOrcamentaria = lev.getHierarquiaOrcamentaria();
        this.codigoPatrimonio = lev.getCodigoPatrimonio();
        this.codigoAnterior = lev.getCodigoAnterior();
        this.item = item;
        this.descricaoBem = lev.getDescricaoBem();
        this.marca = lev.getMarca();
        this.modelo = lev.getModelo();
        this.estadoConservacaoBem = lev.getEstadoConservacaoBem();
        this.situacaoConservacaoBem = lev.getSituacaoConservacaoBem();
        this.tipoAquisicaoBem = lev.getTipoAquisicaoBem();
        this.dataAquisicao = lev.getDataAquisicao();
        this.valorBem = lev.getValorBem();
        this.fornecedor = fornecedor;
        this.notaEmpenho = lev.getNotaEmpenho();
        this.dataNotaEmpenho = lev.getDataNotaEmpenho();
        this.notaFiscal = lev.getNotaFiscal();
        this.dataNotaFiscal = lev.getDataNotaFiscal();
        this.observacao = lev.getObservacao();
        this.localizacao = lev.getLocalizacao();
        this.dataLevantamento = lev.getDataLevantamento();
        this.tipoGrupo = lev.getTipoGrupo();
        this.codigo = lev.getCodigo();
        this.grupoBem = grupoBem;
    }

    public String getDescricaoGrupoBem() {
        return descricaoGrupoBem;
    }

    public void setDescricaoGrupoBem(String descricaoGrupoBem) {
        this.descricaoGrupoBem = descricaoGrupoBem;
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

    public String getDescricaoItem() {
        return descricaoItem;
    }

    public void setDescricaoItem(String descricaoItem) {
        this.descricaoItem = descricaoItem;
    }

    public BigDecimal getDepreciacao() {
        return depreciacao;
    }

    public void setDepreciacao(BigDecimal depreciacao) {
        this.depreciacao = depreciacao;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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
    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    @Override
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
    public List<BemNotaFiscal> getNotasFiscais() {
        List<BemNotaFiscal> notasFiscais = Lists.newArrayList();
        if (dataNotaFiscal != null || !StringUtil.tratarCampoVazio(notaFiscal).isEmpty()) {
            BemNotaFiscal bemNotaFiscal = new BemNotaFiscal();
            bemNotaFiscal.setDataNotaFiscal(dataNotaFiscal);
            bemNotaFiscal.setNumeroNotaFiscal(notaFiscal);
            if (dataNotaEmpenho != null || (notaEmpenho != null && notaEmpenho > 0)) {
                BemNotaFiscalEmpenho bemNotaFiscalEmpenho = new BemNotaFiscalEmpenho();
                bemNotaFiscalEmpenho.setBemNotaFiscal(bemNotaFiscal);
                bemNotaFiscalEmpenho.setNumeroEmpenho(notaEmpenho.toString());
                bemNotaFiscalEmpenho.setDataEmpenho(dataNotaEmpenho);
                bemNotaFiscal.getEmpenhos().add(bemNotaFiscalEmpenho);
            }
            notasFiscais.add(bemNotaFiscal);
        }
        return notasFiscais;
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

    @Override
    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return null;
    }

    public List<OrigemRecursoBem> getListaDeOriemRecursoBem() {
        return listaDeOriemRecursoBem;
    }

    public void setListaDeOriemRecursoBem(List<OrigemRecursoBem> listaDeOriemRecursoBem) {
        this.listaDeOriemRecursoBem = listaDeOriemRecursoBem;
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

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    @Override
    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

    public Seguradora getSeguradora() {
        return seguradora;
    }

    public void setSeguradora(Seguradora seguradora) {
        this.seguradora = seguradora;
    }

    public void validarNegocio(Date dataAtual, Date dataDeCorte) {
        ValidacaoException ve = new ValidacaoException();

        try {
            validarNegocio(dataAtual);
        } catch (ValidacaoException ex) {
            ve.getMensagens().addAll(ex.getMensagens());
        }

        if (tipoAquisicaoBem.equals(TipoAquisicaoBem.COMPRA)) {
            if (dataDeCorte != null) {
                if (DataUtil.dataSemHorario(dataAquisicao).compareTo(DataUtil.dataSemHorario(dataDeCorte)) >= 0) {
                    if (notaEmpenho == null || notaFiscal == null || (notaFiscal != null && notaFiscal.isEmpty()) || dataNotaFiscal == null) {
                        ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Os campos Nota de Empenho, Nota Fiscal e Data Nota Fiscal devem ser preenchidos.");
                    }
                }
            } else {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data de corte não foi definida nos parâmetros do patrimônio.");
            }
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void validarNegocio(Date dataAtual) {
        ValidacaoException ve = new ValidacaoException();

        if (valorBem == null || valorBem.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O valor do bem deve ser positivo.");
        }

        if (notaEmpenho != null && notaEmpenho.compareTo(Integer.valueOf(0)) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O número da nota de empenho deve ser positivo.");
        }

        if (!DataUtil.dataSemHorario(dataAquisicao).before(DataUtil.dataSemHorario(dataAtual))) {
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

        if (this.getListaDeOriemRecursoBem().isEmpty()) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Adicione ao menos uma origem de recurso.");
        }

        if (TipoAquisicaoBem.ADJUDICACAO.equals(this.getTipoAquisicaoBem()) && (this.getDocumentoAdjudicacao() == null || this.getDocumentoAdjudicacao().isEmpty())) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Informe o documento da adjudicação.");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @Override
    public String toString() {
        return (modelo != null ? modelo : "") + " " + (marca != null ? marca : "");
    }

    public boolean requerAprovacao() {
        for (TipoAquisicaoBem tipo : tiposDeAquisicaoQueRequeremAprovacaoDoLevantamento) {
            if (tipo.equals(tipoAquisicaoBem)) {
                return true;
            }
        }

        return false;
    }

    public boolean requerVerificacaoDataDeCorteParaAprovacao() {
        for (TipoAquisicaoBem tipo : tiposDeAquisicaoQueRequeremVerificacaoDaDataDeCorteParaAprovacaoDoLevantamento) {
            if (tipo.equals(tipoAquisicaoBem)) {
                return true;
            }
        }

        return false;
    }

    public boolean isTipoAquisicaoBemAdjudicacao() {
        return TipoAquisicaoBem.ADJUDICACAO.equals(this.getTipoAquisicaoBem());
    }

    public Garantia getGarantia() {
        return garantia;
    }

    public void setGarantia(Garantia garantia) {
        this.garantia = garantia;
    }
}
