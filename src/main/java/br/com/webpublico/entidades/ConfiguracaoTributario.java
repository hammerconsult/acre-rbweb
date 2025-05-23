/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoVerificacaoDebitoAlvara;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.domain.dtos.ConfiguracaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Terminal-2
 */
@Entity

@Audited
public class ConfiguracaoTributario extends ConfiguracaoModulo implements Serializable, NfseEntity, PossuidorArquivo {

    private static final long serialVersionUID = 1L;
    /*
     * Configuração geral do tributário, utilizada em todo o módulo
     */
    @ManyToOne
    private Cidade cidade;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date vigencia;
    /*
     * Configuração do Cadastro Imobiliário
     */
    private Integer numDigitosDistrito;
    private Integer numDigitosSetor;
    private Integer numDigitosQuadra;
    private Integer numDigitosLote;
    private Integer numDigitosUnidade;
    @OneToMany(mappedBy = "configuracaoTributario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoConfiguradoBCI> eventosBCI;
    @OneToOne(mappedBy = "configuracaoTributario", cascade = CascadeType.ALL)
    private ConfiguracaoPatrimonioLote configuracaoPatrimonioLote;

    @OneToMany(mappedBy = "configuracaoTributario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoWebService> itemConfiguracaoWebService;

    private Integer intervaloSit;

    private String formulaValorVenal;
    private String formulaFracaoIdeal;
    private String formulaValorVenalConstrucao;
    private String formulaValorMetragem;
    private String formulaQualidadeConstrucao;
    private String bibliotecaFormulas;
    /*
     * Configurações da Dívida
     */
    @ManyToOne
    private Divida dividaNFSAvulsa;
    @ManyToOne
    private Divida dividaNFSAvulsaPessoa;
    @ManyToOne
    private Divida dividaIPTU;
    @ManyToOne
    private Divida dividaISSHomologado;
    @ManyToOne
    private Divida dividaISSFixo;
    @ManyToOne
    private Divida dividaISSEstimado;
    @ManyToOne
    private Divida dividaSimplesNacional;
    @ManyToOne
    private Divida dividaRendasPatrimoniais;
    @ManyToOne
    private Tributo tributoRendasPatrimoniais;
    @ManyToOne
    private Divida dividaDoctoOficial;
    @ManyToOne
    private Divida dividaDoctoOfcMobiliario;
    @ManyToOne
    private Divida dividaDoctoOfcImobiliario;
    @ManyToOne
    private Divida dividaDoctoOfcRural;
    @ManyToOne
    private Divida dividaInconsistencia;
    @ManyToOne
    private Divida dividaAutoInfracaoISS;
    @ManyToOne
    private Divida dividaMultaFiscalizacao;
    @ManyToOne
    private Divida dividaTaxaDivCadImobiliario;
    @ManyToOne
    private Divida dividaTaxaDivCadMobiliario;
    @ManyToOne
    private Divida dividaTaxaDivCadRural;
    @ManyToOne
    private Divida dividaTaxaDivContribuinte;
    @ManyToOne
    private Divida dividaContribuicaoMelhoria;
    @ManyToOne
    private Divida dividaContratoConcessao;
    @ManyToOne
    private Tributo tributoNFSAvulsa;
    @ManyToOne
    private Tributo tributoISS;
    @ManyToOne
    private Tributo tributoJuros;
    @ManyToOne
    private Tributo tributoMultaISS;
    @ManyToOne
    private Tributo tributoCorrecaoMonetaria;
    @ManyToOne
    private Tributo tributoTaxaSobreISS;
    @ManyToOne
    private Tributo tributoHabitese;
    @ManyToOne
    private Tributo tributoInconsistencia;
    @ManyToOne
    private Tributo tributoContratoConcessao;
    @ManyToOne
    private Pessoa contribuinteInconsistencia;
    private BigDecimal aliquotaISSQN; //Parâmetros da Nota Fiscal Avulsa
    private Boolean verificaDebito; //Parâmetros da Nota Fiscal Avulsa
    private Boolean emiteSemPagamento; //Parâmetros da Nota Fiscal Avulsa
    private Boolean validarIssqnFixo; //Parâmetros da Nota Fiscal Avulsa
    private Boolean usaCadOriginalDesmembramento;
    @OneToMany(mappedBy = "configuracaoTributario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LotacaoVistoriadoraConfigTribRendas> rendasLotacoesVistoriadoras;
    @ManyToOne
    private TipoDoctoOficial rendasTipoDoctoOficialPF;
    @ManyToOne
    private TipoDoctoOficial rendasTipoDoctoOficialPJ;
    @ManyToOne
    private LotacaoVistoriadora ceasaLotacaoVistoriadora;
    @ManyToOne
    private Divida ceasaDividaContrato;
    @ManyToOne
    private Tributo ceasaTributoContrato;
    @ManyToOne
    private Divida ceasaDividaLicitacao;
    @ManyToOne
    private Tributo ceasaTributoLicitacao;
    @ManyToOne
    private TipoDoctoOficial ceasaDoctoOficialContrato;
    private BigDecimal multaAcessoriaSemMovimento;
    private BigDecimal multaAcessoriaComMovimento;
    @ManyToOne
    private Tributo tributoMultaAcessoria;
    @ManyToOne
    private Divida dividaMultaAcessoria;
    @OneToMany(mappedBy = "configuracao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BancoContaConfTributario> bancosArrecadacao;
    private Integer faixaRecuperacaoDam;//controla o cache de número de DAMs na memória
    @ManyToOne
    @Etiqueta(value = "Lei para Juros")
    private AtoLegal leiJuros;
    @ManyToOne
    @Etiqueta(value = "Lei para Multas")
    private AtoLegal leiMultas;
    @ManyToOne
    @Etiqueta(value = "Lei para Correção Monetária")
    private AtoLegal leiCorrecaoMonetaria;

    //Alvara
    private Integer qtdeDiasVencAlvaraProvisorio;
    private Integer qtdeDiasCancelarNfsa;
    @ManyToOne
    private Tributo tributoAlvaraLocalizacao;
    @ManyToOne
    private Tributo tributoAlvaraFuncionamento;
    @ManyToOne
    private Tributo tributoAlvaraSanitario;
    @ManyToOne
    private Tributo tributoAlvaraFuncProv;
    @ManyToOne
    private Tributo tributoAlvaraSaniProv;
    @ManyToOne
    private Divida dividaAlvaraLocalizacao;
    @ManyToOne
    private Divida dividaAlvaraFuncionamento;
    @ManyToOne
    private Divida dividaAlvaraFuncProvisorio;
    @ManyToOne
    private Divida dividaAlvaraSanitario;
    @ManyToOne
    private Divida dividaAlvaraSaniProvisorio;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configuracaoTributario")
    private List<ConfiguracaoIrregularidadesDoAlvara> irregularidadesDoAlvara;
    private String mensagemRodapeAlvara;
    private Integer quantidadeExerciciosAnteriores;
    @Enumerated(EnumType.STRING)
    private TipoVerificacaoDebitoAlvara tipoVerificacaoDebitoAlvara;
    @Enumerated(EnumType.STRING)
    private Mes mesVencimentoRenovaAlvaraFunc;
    @Enumerated(EnumType.STRING)
    private Mes mesVencimentoRenovaAlvaraLoc;

    //Rateio
    @ManyToOne
    private Tributo tributoRateio;

    /*DívidaContribuição Melhoria*/
    @ManyToOne
    private Tributo tributoContribuicaoMelhoria;

    /*ISS Fixo*/
    private BigDecimal qtdeUfmIssFixoProfSuperior;
    private BigDecimal qtdeUfmIssFixoProfMedio;
    private BigDecimal qtdeUfmIssFixoProfDemais;
    @Enumerated(EnumType.STRING)
    private Mes mesVencimentoIssFixo;

    private String textoMalaDiretaIptu;
    @ManyToOne
    @Etiqueta("Exercício Portal Web")
    private Exercicio exercicioPortal;
    private Boolean canEmitirCarneIPTUPortal;

    private Boolean ativaAlteracaoCadastral;
    private String enderecoEmail;
    private String secretariaEmail;
    private String bairroEmail;
    private String cepEmail;
    private String telefoneEmail;
    private String rodapePadraoEmail;
    private String rodapeRbTransEmail;
    private String textoSolicitacaoITBI;

    @OneToOne
    private Servico servicoNaoIndentificadoIss;

    /*Configurações Solicitação de Cadastro*/
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    private String assunto;
    private String urlPortalContribuinte;
    private String conteudo;
    private Boolean verificaAidf;
    private String assinaturaCertidaoAtividadeCmc;
    private int quantidadeAnosPrescricao;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao certificadoNfse;
    private String senhaCertificadoNfse;
    @ManyToOne
    private TipoDoctoOficial tipoDoctoOficialBaixaCmc;

    @OneToMany(mappedBy = "configuracaoTributario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoAlvara> configuracaoAlvara;
    @OneToMany(mappedBy = "configuracaoTributario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroValorAlvaraAmbiental> parametrosValorAlvaraAmbiental;

    private Integer anoCnaeBaixoRiscoA;
    private Integer anoCnaeBaixoRiscoB;
    private Integer anoCnaeAltoRisco;

    private String codigoApresentante;
    private String nomeApresentante;
    private Long seqIncialRemessaProtesto;
    private String linkPlanoDiretor;

    @OneToOne(cascade = CascadeType.ALL)
    private ConfiguracaoTributarioBI configuracaoTributarioBI;

    public ConfiguracaoTributario() {
        bancosArrecadacao = Lists.newLinkedList();
        eventosBCI = Lists.newArrayList();
        irregularidadesDoAlvara = Lists.newArrayList();
        configuracaoPatrimonioLote = new ConfiguracaoPatrimonioLote();
        itemConfiguracaoWebService = Lists.newArrayList();
        configuracaoAlvara = Lists.newArrayList();
        parametrosValorAlvaraAmbiental = Lists.newArrayList();
        configuracaoTributarioBI = new ConfiguracaoTributarioBI();
    }

    public Divida getDividaAlvaraSanitario() {
        return dividaAlvaraSanitario;
    }

    public void setDividaAlvaraSanitario(Divida dividaAlvaraSanitario) {
        this.dividaAlvaraSanitario = dividaAlvaraSanitario;
    }

    public Divida getDividaNFSAvulsa() {
        return dividaNFSAvulsa;
    }

    public void setDividaNFSAvulsa(Divida dividaNFSAvulsa) {
        this.dividaNFSAvulsa = dividaNFSAvulsa;
    }

    public Tributo getTributoNFSAvulsa() {
        return tributoNFSAvulsa;
    }

    public void setTributoNFSAvulsa(Tributo tributoNFSAvulsa) {
        this.tributoNFSAvulsa = tributoNFSAvulsa;
    }

    public Tributo getTributoAlvaraFuncProv() {
        return tributoAlvaraFuncProv;
    }

    public void setTributoAlvaraFuncProv(Tributo tributoAlvaraProvisorio) {
        this.tributoAlvaraFuncProv = tributoAlvaraProvisorio;
    }

    public List<ConfiguracaoWebService> getItemConfiguracaoWebService() {
        return itemConfiguracaoWebService;
    }

    public void setItemConfiguracaoWebService(List<ConfiguracaoWebService> itemConfiguracaoWebService) {
        this.itemConfiguracaoWebService = itemConfiguracaoWebService;
    }

    public Tributo getTributoAlvaraSaniProv() {
        return tributoAlvaraSaniProv;
    }

    public void setTributoAlvaraSaniProv(Tributo tributoAlvaraSaniProv) {
        this.tributoAlvaraSaniProv = tributoAlvaraSaniProv;
    }

    public Divida getDividaContribuicaoMelhoria() {
        return dividaContribuicaoMelhoria;
    }

    public void setDividaContribuicaoMelhoria(Divida dividaContribuicaoMelhoria) {
        this.dividaContribuicaoMelhoria = dividaContribuicaoMelhoria;
    }

    public Tributo getTributoAlvaraSanitario() {
        return tributoAlvaraSanitario;
    }

    public void setTributoAlvaraSanitario(Tributo tributoAlvaraVigilancia) {
        this.tributoAlvaraSanitario = tributoAlvaraVigilancia;
    }

    public Divida getDividaAlvaraSaniProvisorio() {
        return dividaAlvaraSaniProvisorio;
    }

    public void setDividaAlvaraSaniProvisorio(Divida dividaAlvaraSaniProvisorio) {
        this.dividaAlvaraSaniProvisorio = dividaAlvaraSaniProvisorio;
    }

    public Divida getDividaAlvaraFuncionamento() {
        return dividaAlvaraFuncionamento;
    }

    public void setDividaAlvaraFuncionamento(Divida dividaAlvaraFuncionamento) {
        this.dividaAlvaraFuncionamento = dividaAlvaraFuncionamento;
    }

    public Divida getDividaAlvaraFuncProvisorio() {
        return dividaAlvaraFuncProvisorio;
    }

    public void setDividaAlvaraFuncProvisorio(Divida dividaAlvaraFuncProvisorio) {
        this.dividaAlvaraFuncProvisorio = dividaAlvaraFuncProvisorio;
    }

    public Tributo getTributoAlvaraFuncionamento() {
        return tributoAlvaraFuncionamento;
    }

    public void setTributoAlvaraFuncionamento(Tributo tributoAlvaraFuncionamento) {
        this.tributoAlvaraFuncionamento = tributoAlvaraFuncionamento;
    }

    public Tributo getTributoAlvaraLocalizacao() {
        return tributoAlvaraLocalizacao;
    }

    public void setTributoAlvaraLocalizacao(Tributo tributoAlvaraLocalizacao) {
        this.tributoAlvaraLocalizacao = tributoAlvaraLocalizacao;
    }

    public Divida getDividaAlvaraLocalizacao() {
        return dividaAlvaraLocalizacao;
    }

    public void setDividaAlvaraLocalizacao(Divida dividaAlvaraLocalizacao) {
        this.dividaAlvaraLocalizacao = dividaAlvaraLocalizacao;
    }

    public Mes getMesVencimentoRenovaAlvaraFunc() {
        return mesVencimentoRenovaAlvaraFunc;
    }

    public void setMesVencimentoRenovaAlvaraFunc(Mes mesVencimentoRenovaAlvaraFunc) {
        this.mesVencimentoRenovaAlvaraFunc = mesVencimentoRenovaAlvaraFunc;
    }

    public Mes getMesVencimentoRenovaAlvaraLoc() {
        return mesVencimentoRenovaAlvaraLoc;
    }

    public void setMesVencimentoRenovaAlvaraLoc(Mes mesVencimentoRenovaAlvaraLoc) {
        this.mesVencimentoRenovaAlvaraLoc = mesVencimentoRenovaAlvaraLoc;
    }

    public Divida getDividaSimplesNacional() {
        return dividaSimplesNacional;
    }

    public void setDividaSimplesNacional(Divida dividaSimplesNacional) {
        this.dividaSimplesNacional = dividaSimplesNacional;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Integer getNumDigitosDistrito() {
        return numDigitosDistrito;
    }

    public void setNumDigitosDistrito(Integer numDigitosDistrito) {
        this.numDigitosDistrito = numDigitosDistrito;
    }

    public Integer getNumDigitosLote() {
        return numDigitosLote;
    }

    public void setNumDigitosLote(Integer numDigitosLote) {
        this.numDigitosLote = numDigitosLote;
    }

    public Integer getNumDigitosQuadra() {
        return numDigitosQuadra;
    }

    public void setNumDigitosQuadra(Integer numDigitosQuadra) {
        this.numDigitosQuadra = numDigitosQuadra;
    }

    public Servico getServicoNaoIndentificadoIss() {
        return servicoNaoIndentificadoIss;
    }

    public void setServicoNaoIndentificadoIss(Servico servicoNaoIndentificadoIss) {
        this.servicoNaoIndentificadoIss = servicoNaoIndentificadoIss;
    }

    public Integer getNumDigitosSetor() {
        return numDigitosSetor;
    }

    public void setNumDigitosSetor(Integer numDigitosSetor) {
        this.numDigitosSetor = numDigitosSetor;
    }

    public Integer getNumDigitosUnidade() {
        return numDigitosUnidade;
    }

    public void setNumDigitosUnidade(Integer numDigitosUnidade) {
        this.numDigitosUnidade = numDigitosUnidade;
    }

    public Date getVigencia() {
        return vigencia;
    }

    public void setVigencia(Date vigencia) {
        this.vigencia = vigencia;
    }

    public String getFormulaQualidadeConstrucao() {
        return formulaQualidadeConstrucao;
    }

    public void setFormulaQualidadeConstrucao(String formulaQualidadeConstrucao) {
        this.formulaQualidadeConstrucao = formulaQualidadeConstrucao;
    }

    public String getFormulaValorMetragem() {
        return formulaValorMetragem;
    }

    public void setFormulaValorMetragem(String formulaValorMetragem) {
        this.formulaValorMetragem = formulaValorMetragem;
    }

    public String getFormulaValorVenal() {
        return formulaValorVenal;
    }

    public void setFormulaValorVenal(String formulaValorVenal) {
        this.formulaValorVenal = formulaValorVenal;
    }

    public String getBibliotecaFormulas() {
        return bibliotecaFormulas;
    }

    public void setBibliotecaFormulas(String bibliotecaFormulas) {
        this.bibliotecaFormulas = bibliotecaFormulas;
    }

    public String getFormulaFracaoIdeal() {
        return formulaFracaoIdeal;
    }

    public void setFormulaFracaoIdeal(String formulaFracaoIdeal) {
        this.formulaFracaoIdeal = formulaFracaoIdeal;
    }

    public Divida getDividaIPTU() {
        return dividaIPTU;
    }

    public void setDividaIPTU(Divida dividaIPTU) {
        this.dividaIPTU = dividaIPTU;
    }

    public Divida getDividaISSHomologado() {
        return dividaISSHomologado;
    }

    public void setDividaISSHomologado(Divida dividaISS) {
        this.dividaISSHomologado = dividaISS;
    }

    public List<ConfiguracaoIrregularidadesDoAlvara> getIrregularidadesDoAlvara() {
        return irregularidadesDoAlvara;
    }

    public void setIrregularidadesDoAlvara(List<ConfiguracaoIrregularidadesDoAlvara> irregularidadesDoAlvara) {
        this.irregularidadesDoAlvara = irregularidadesDoAlvara;
    }

    public Divida getDividaRendasPatrimoniais() {
        return dividaRendasPatrimoniais;
    }

    public void setDividaRendasPatrimoniais(Divida dividaRendasPatrimoniais) {
        this.dividaRendasPatrimoniais = dividaRendasPatrimoniais;
    }

    public Tributo getTributoISS() {
        return tributoISS;
    }

    public void setTributoISS(Tributo tributoISS) {
        this.tributoISS = tributoISS;
    }

    public Tributo getTributoJuros() {
        return tributoJuros;
    }

    public void setTributoJuros(Tributo tributoJuros) {
        this.tributoJuros = tributoJuros;
    }

    public Tributo getTributoMultaISS() {
        return tributoMultaISS;
    }

    public void setTributoMultaISS(Tributo tributoMultaISS) {
        this.tributoMultaISS = tributoMultaISS;
    }

    public Tributo getTributoCorrecaoMonetaria() {
        return tributoCorrecaoMonetaria;
    }

    public void setTributoCorrecaoMonetaria(Tributo tributoCorrecaoMonetaria) {
        this.tributoCorrecaoMonetaria = tributoCorrecaoMonetaria;
    }

    public Tributo getTributoRendasPatrimoniais() {
        return tributoRendasPatrimoniais;
    }

    public void setTributoRendasPatrimoniais(Tributo tributoRendasPatrimoniais) {
        this.tributoRendasPatrimoniais = tributoRendasPatrimoniais;
    }

    public String getFormulaValorVenalConstrucao() {
        return formulaValorVenalConstrucao;
    }

    public void setFormulaValorVenalConstrucao(String formulaValorVenalConstrucao) {
        this.formulaValorVenalConstrucao = formulaValorVenalConstrucao;
    }

    public Divida getDividaISSEstimado() {
        return dividaISSEstimado;
    }

    public void setDividaISSEstimado(Divida dividaISSEstimado) {
        this.dividaISSEstimado = dividaISSEstimado;
    }

    public Divida getDividaISSFixo() {
        return dividaISSFixo;
    }

    public void setDividaISSFixo(Divida dividaISSFixo) {
        this.dividaISSFixo = dividaISSFixo;
    }

    public Integer getQtdeDiasVencAlvaraProvisorio() {
        return qtdeDiasVencAlvaraProvisorio;
    }

    public void setQtdeDiasVencAlvaraProvisorio(Integer qtdeDiasVencAlvaraProvisorio) {
        this.qtdeDiasVencAlvaraProvisorio = qtdeDiasVencAlvaraProvisorio;
    }

    public Integer getQtdeDiasCancelarNfsa() {
        return qtdeDiasCancelarNfsa;
    }

    public void setQtdeDiasCancelarNfsa(Integer qtdeDiasCancelasNfsa) {
        this.qtdeDiasCancelarNfsa = qtdeDiasCancelasNfsa;
    }

    public String getMensagemRodapeAlvara() {
        return mensagemRodapeAlvara;
    }

    public void setMensagemRodapeAlvara(String mensagemRodapeAlvara) {
        this.mensagemRodapeAlvara = mensagemRodapeAlvara;
    }

    public Divida getDividaDoctoOficial() {
        return dividaDoctoOficial;
    }

    public void setDividaDoctoOficial(Divida dividaDoctoOficial) {
        this.dividaDoctoOficial = dividaDoctoOficial;
    }

    public Pessoa getContribuinteInconsistencia() {
        return contribuinteInconsistencia;
    }

    public void setContribuinteInconsistencia(Pessoa contribuinteInconsistencia) {
        this.contribuinteInconsistencia = contribuinteInconsistencia;
    }

    public Divida getDividaInconsistencia() {
        return dividaInconsistencia;
    }

    public void setDividaInconsistencia(Divida dividaInconsistencia) {
        this.dividaInconsistencia = dividaInconsistencia;
    }

    public Tributo getTributoInconsistencia() {
        return tributoInconsistencia;
    }

    public void setTributoInconsistencia(Tributo tributoInconsistencia) {
        this.tributoInconsistencia = tributoInconsistencia;
    }

    public Divida getDividaAutoInfracaoISS() {
        return dividaAutoInfracaoISS;
    }

    public void setDividaAutoInfracaoISS(Divida dividaAutoInfracaoISS) {
        this.dividaAutoInfracaoISS = dividaAutoInfracaoISS;
    }

    public BigDecimal getAliquotaISSQN() {
        return aliquotaISSQN;
    }

    public void setAliquotaISSQN(BigDecimal aliquotaISSQN) {
        this.aliquotaISSQN = aliquotaISSQN;
    }

    public Boolean getEmiteSemPagamento() {
        return emiteSemPagamento;
    }

    public void setEmiteSemPagamento(Boolean emiteSemPagamento) {
        this.emiteSemPagamento = emiteSemPagamento;
    }

    public Boolean getValidarIssqnFixo() {
        return validarIssqnFixo != null ? validarIssqnFixo : Boolean.FALSE;
    }

    public void setValidarIssqnFixo(Boolean validarIssqnFixo) {
        this.validarIssqnFixo = validarIssqnFixo;
    }

    public Boolean getVerificaDebito() {
        return verificaDebito;
    }

    public void setVerificaDebito(Boolean verificaDebito) {
        this.verificaDebito = verificaDebito;
    }

    public Divida getDividaNFSAvulsaPessoa() {
        return dividaNFSAvulsaPessoa;
    }

    public void setDividaNFSAvulsaPessoa(Divida dividaNFSAvulsaPessoa) {
        this.dividaNFSAvulsaPessoa = dividaNFSAvulsaPessoa;
    }

    public Tributo getTributoTaxaSobreISS() {
        return tributoTaxaSobreISS;
    }

    public void setTributoTaxaSobreISS(Tributo tributoTaxaSobreISS) {
        this.tributoTaxaSobreISS = tributoTaxaSobreISS;
    }

    public Divida getDividaMultaFiscalizacao() {
        return dividaMultaFiscalizacao;
    }

    public void setDividaMultaFiscalizacao(Divida dividaMultaFiscalizacao) {
        this.dividaMultaFiscalizacao = dividaMultaFiscalizacao;
    }

    public Divida getDividaTaxaDivCadImobiliario() {
        return dividaTaxaDivCadImobiliario;
    }

    public void setDividaTaxaDivCadImobiliario(Divida dividaTaxaDivCadImobiliario) {
        this.dividaTaxaDivCadImobiliario = dividaTaxaDivCadImobiliario;
    }

    public Divida getDividaTaxaDivCadMobiliario() {
        return dividaTaxaDivCadMobiliario;
    }

    public void setDividaTaxaDivCadMobiliario(Divida dividaTaxaDivCadMobiliario) {
        this.dividaTaxaDivCadMobiliario = dividaTaxaDivCadMobiliario;
    }

    public Divida getDividaTaxaDivCadRural() {
        return dividaTaxaDivCadRural;
    }

    public void setDividaTaxaDivCadRural(Divida dividaTaxaDivCadRural) {
        this.dividaTaxaDivCadRural = dividaTaxaDivCadRural;
    }

    public Divida getDividaTaxaDivContribuinte() {
        return dividaTaxaDivContribuinte;
    }

    public void setDividaTaxaDivContribuinte(Divida dividaTaxaDivContribuinte) {
        this.dividaTaxaDivContribuinte = dividaTaxaDivContribuinte;
    }

    public Boolean getUsaCadOriginalDesmembramento() {
        return usaCadOriginalDesmembramento;
    }

    public void setUsaCadOriginalDesmembramento(Boolean usaCadOriginalDesmembramento) {
        this.usaCadOriginalDesmembramento = usaCadOriginalDesmembramento;
    }

    public List<LotacaoVistoriadoraConfigTribRendas> getRendasLotacoesVistoriadoras() {
        if (rendasLotacoesVistoriadoras == null) rendasLotacoesVistoriadoras = Lists.newArrayList();
        return rendasLotacoesVistoriadoras;
    }

    public void setRendasLotacoesVistoriadoras(List<LotacaoVistoriadoraConfigTribRendas> rendasLotacoesVistoriadoras) {
        this.rendasLotacoesVistoriadoras = rendasLotacoesVistoriadoras;
    }

    public TipoDoctoOficial getRendasTipoDoctoOficialPF() {
        return rendasTipoDoctoOficialPF;
    }

    public void setRendasTipoDoctoOficialPF(TipoDoctoOficial rendasTipoDoctoOficialPF) {
        this.rendasTipoDoctoOficialPF = rendasTipoDoctoOficialPF;
    }

    public TipoDoctoOficial getRendasTipoDoctoOficialPJ() {
        return rendasTipoDoctoOficialPJ;
    }

    public void setRendasTipoDoctoOficialPJ(TipoDoctoOficial rendasTipoDoctoOficialPJ) {
        this.rendasTipoDoctoOficialPJ = rendasTipoDoctoOficialPJ;
    }

    public LotacaoVistoriadora getCeasaLotacaoVistoriadora() {
        return ceasaLotacaoVistoriadora;
    }

    public void setCeasaLotacaoVistoriadora(LotacaoVistoriadora ceasaLotacaoVistoriadora) {
        this.ceasaLotacaoVistoriadora = ceasaLotacaoVistoriadora;
    }

    public Divida getCeasaDividaContrato() {
        return ceasaDividaContrato;
    }

    public void setCeasaDividaContrato(Divida ceasaDividaContrato) {
        this.ceasaDividaContrato = ceasaDividaContrato;
    }

    public Tributo getCeasaTributoContrato() {
        return ceasaTributoContrato;
    }

    public void setCeasaTributoContrato(Tributo ceasaTributoContrato) {
        this.ceasaTributoContrato = ceasaTributoContrato;
    }

    public Divida getCeasaDividaLicitacao() {
        return ceasaDividaLicitacao;
    }

    public void setCeasaDividaLicitacao(Divida ceasaDividaLicitacao) {
        this.ceasaDividaLicitacao = ceasaDividaLicitacao;
    }

    public Tributo getCeasaTributoLicitacao() {
        return ceasaTributoLicitacao;
    }

    public void setCeasaTributoLicitacao(Tributo ceasaTributoLicitacao) {
        this.ceasaTributoLicitacao = ceasaTributoLicitacao;
    }

    public TipoDoctoOficial getCeasaDoctoOficialContrato() {
        return ceasaDoctoOficialContrato;
    }

    public void setCeasaDoctoOficialContrato(TipoDoctoOficial ceasaDoctoOficialContrato) {
        this.ceasaDoctoOficialContrato = ceasaDoctoOficialContrato;
    }

    public List<BancoContaConfTributario> getBancosArrecadacao() {
        return bancosArrecadacao;
    }

    public void setBancosArrecadacao(List<BancoContaConfTributario> bancosArrecadacao) {
        this.bancosArrecadacao = bancosArrecadacao;
    }

    public Integer getFaixaRecuperacaoDam() {
        return faixaRecuperacaoDam;
    }

    public void setFaixaRecuperacaoDam(Integer faixaRecuperacaoDam) {
        this.faixaRecuperacaoDam = faixaRecuperacaoDam;
    }

    public AtoLegal getLeiJuros() {
        return leiJuros;
    }

    public void setLeiJuros(AtoLegal leiJuros) {
        this.leiJuros = leiJuros;
    }

    public AtoLegal getLeiMultas() {
        return leiMultas;
    }

    public void setLeiMultas(AtoLegal leiMultas) {
        this.leiMultas = leiMultas;
    }

    public AtoLegal getLeiCorrecaoMonetaria() {
        return leiCorrecaoMonetaria;
    }

    public void setLeiCorrecaoMonetaria(AtoLegal leiCorrecaoMonetaria) {
        this.leiCorrecaoMonetaria = leiCorrecaoMonetaria;
    }

    public List<EventoConfiguradoBCI> getEventosBCI() {
        return eventosBCI;
    }

    public void setEventosBCI(List<EventoConfiguradoBCI> eventosBCI) {
        this.eventosBCI = eventosBCI;
    }

    public BigDecimal getMultaAcessoriaComMovimento() {
        return multaAcessoriaComMovimento;
    }

    public void setMultaAcessoriaComMovimento(BigDecimal multaAcessoriaComMovimento) {
        this.multaAcessoriaComMovimento = multaAcessoriaComMovimento;
    }

    public BigDecimal getMultaAcessoriaSemMovimento() {
        return multaAcessoriaSemMovimento;
    }

    public void setMultaAcessoriaSemMovimento(BigDecimal multaAcessoriaSemMovimento) {
        this.multaAcessoriaSemMovimento = multaAcessoriaSemMovimento;
    }

    public Divida getDividaMultaAcessoria() {
        return dividaMultaAcessoria;
    }

    public void setDividaMultaAcessoria(Divida dividaMultaAcessoria) {
        this.dividaMultaAcessoria = dividaMultaAcessoria;
    }

    public Tributo getTributoMultaAcessoria() {
        return tributoMultaAcessoria;
    }

    public void setTributoMultaAcessoria(Tributo tributoMultaAcessoria) {
        this.tributoMultaAcessoria = tributoMultaAcessoria;
    }

    public Tributo getTributoRateio() {
        return tributoRateio;
    }

    public void setTributoRateio(Tributo tributoRateio) {
        this.tributoRateio = tributoRateio;
    }

    public Tributo getTributoContribuicaoMelhoria() {
        return tributoContribuicaoMelhoria;
    }

    public void setTributoContribuicaoMelhoria(Tributo tributoContribuicaoMelhoria) {
        this.tributoContribuicaoMelhoria = tributoContribuicaoMelhoria;
    }

    public ConfiguracaoPatrimonioLote getConfiguracaoPatrimonioLote() {
        return configuracaoPatrimonioLote;
    }

    public void setConfiguracaoPatrimonioLote(ConfiguracaoPatrimonioLote configuracaoPatrimonioLote) {
        this.configuracaoPatrimonioLote = configuracaoPatrimonioLote;
    }

    public BigDecimal getQtdeUfmIssFixoProfSuperior() {
        if (qtdeUfmIssFixoProfSuperior == null) {
            qtdeUfmIssFixoProfSuperior = BigDecimal.ZERO;
        }
        return qtdeUfmIssFixoProfSuperior;
    }

    public void setQtdeUfmIssFixoProfSuperior(BigDecimal qtdeUfmIssFixoProfSuperior) {
        this.qtdeUfmIssFixoProfSuperior = qtdeUfmIssFixoProfSuperior;
    }

    public BigDecimal getQtdeUfmIssFixoProfMedio() {
        if (qtdeUfmIssFixoProfMedio == null) {
            qtdeUfmIssFixoProfMedio = BigDecimal.ZERO;
        }
        return qtdeUfmIssFixoProfMedio;
    }

    public void setQtdeUfmIssFixoProfMedio(BigDecimal qtdeUfmIssFixoProfMedio) {
        this.qtdeUfmIssFixoProfMedio = qtdeUfmIssFixoProfMedio;
    }

    public BigDecimal getQtdeUfmIssFixoProfDemais() {
        if (qtdeUfmIssFixoProfDemais == null) {
            qtdeUfmIssFixoProfDemais = BigDecimal.ZERO;
        }
        return qtdeUfmIssFixoProfDemais;
    }

    public void setQtdeUfmIssFixoProfDemais(BigDecimal qtdeUfmIssFixoProfDemais) {
        this.qtdeUfmIssFixoProfDemais = qtdeUfmIssFixoProfDemais;
    }

    public Divida getDividaDoctoOfcMobiliario() {
        return dividaDoctoOfcMobiliario;
    }

    public void setDividaDoctoOfcMobiliario(Divida dividaDoctoOfcMobiliario) {
        this.dividaDoctoOfcMobiliario = dividaDoctoOfcMobiliario;
    }

    public Divida getDividaDoctoOfcImobiliario() {
        return dividaDoctoOfcImobiliario;
    }

    public void setDividaDoctoOfcImobiliario(Divida dividaDoctoOfcImobiliario) {
        this.dividaDoctoOfcImobiliario = dividaDoctoOfcImobiliario;
    }

    public Divida getDividaDoctoOfcRural() {
        return dividaDoctoOfcRural;
    }

    public void setDividaDoctoOfcRural(Divida dividaDoctoOfcRural) {
        this.dividaDoctoOfcRural = dividaDoctoOfcRural;
    }

    public Integer getIntervaloSit() {
        return intervaloSit;
    }

    public void setIntervaloSit(Integer intervaloSit) {
        this.intervaloSit = intervaloSit;
    }

    public String getTextoMalaDiretaIptu() {
        return textoMalaDiretaIptu;
    }

    public void setTextoMalaDiretaIptu(String textoMalaDiretaIptu) {
        this.textoMalaDiretaIptu = textoMalaDiretaIptu;
    }

    public Integer getQuantidadeExerciciosAnteriores() {
        return quantidadeExerciciosAnteriores != null ? quantidadeExerciciosAnteriores : 0;
    }

    public void setQuantidadeExerciciosAnteriores(Integer quantidadeExerciciosAnteriores) {
        this.quantidadeExerciciosAnteriores = quantidadeExerciciosAnteriores;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getAssinaturaCertidaoAtividadeCmc() {
        return assinaturaCertidaoAtividadeCmc;
    }

    public void setAssinaturaCertidaoAtividadeCmc(String assinaturaCertidaoAtividadeCmc) {
        this.assinaturaCertidaoAtividadeCmc = assinaturaCertidaoAtividadeCmc;
    }

    public TipoDoctoOficial getTipoDoctoOficialBaixaCmc() {
        return tipoDoctoOficialBaixaCmc;
    }

    public void setTipoDoctoOficialBaixaCmc(TipoDoctoOficial tipoDoctoOficialBaixaCmc) {
        this.tipoDoctoOficialBaixaCmc = tipoDoctoOficialBaixaCmc;
    }

    public boolean getVerificaAidf() {
        return verificaAidf != null ? verificaAidf : false;
    }

    public void setVerificaAidf(boolean verificaAidf) {
        this.verificaAidf = verificaAidf;
    }

    @Override
    public String toString() {
        return cidade.getNome();
    }

    public Exercicio getExercicioPortal() {
        return exercicioPortal;
    }

    public void setExercicioPortal(Exercicio exercicioPortal) {
        this.exercicioPortal = exercicioPortal;
    }

    public DetentorArquivoComposicao getCertificadoNfse() {
        return certificadoNfse;
    }

    public void setCertificadoNfse(DetentorArquivoComposicao certificado) {
        this.certificadoNfse = certificado;
    }

    public String getSenhaCertificadoNfse() {
        return senhaCertificadoNfse;
    }

    public void setSenhaCertificadoNfse(String senhaCertificadoNfse) {
        this.senhaCertificadoNfse = senhaCertificadoNfse;
    }

    public int getQuantidadeAnosPrescricao() {
        return quantidadeAnosPrescricao;
    }

    public void setQuantidadeAnosPrescricao(int quantidadeAnosPrescricao) {
        this.quantidadeAnosPrescricao = quantidadeAnosPrescricao;
    }

    public String getUrlPortalContribuinte() {
        return urlPortalContribuinte;
    }

    public void setUrlPortalContribuinte(String urlPortalContribuinte) {
        this.urlPortalContribuinte = urlPortalContribuinte;
    }

    @Override
    public NfseDTO toNfseDto() {
        ConfiguracaoNfseDTO configuracaoNfseDTO = new ConfiguracaoNfseDTO(getId(), cidade.toNfseDto(), true);
        configuracaoNfseDTO.setIdsDividasIss(Lists.<Long>newArrayList());
        configuracaoNfseDTO.getIdsDividasIss().add(dividaISSEstimado.getId());
        configuracaoNfseDTO.getIdsDividasIss().add(dividaISSFixo.getId());
        configuracaoNfseDTO.getIdsDividasIss().add(dividaISSHomologado.getId());
        configuracaoNfseDTO.getIdsDividasIss().add(dividaAutoInfracaoISS.getId());
        configuracaoNfseDTO.getIdsDividasIss().add(dividaMultaFiscalizacao.getId());
        return configuracaoNfseDTO;
    }

    public TipoVerificacaoDebitoAlvara getTipoVerificacaoDebitoAlvara() {
        return tipoVerificacaoDebitoAlvara;
    }

    public void setTipoVerificacaoDebitoAlvara(TipoVerificacaoDebitoAlvara tipoVerificacaoDebitoAlvara) {
        this.tipoVerificacaoDebitoAlvara = tipoVerificacaoDebitoAlvara;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return certificadoNfse;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.certificadoNfse = detentorArquivoComposicao;
    }

    public Mes getMesVencimentoIssFixo() {
        return mesVencimentoIssFixo;
    }

    public void setMesVencimentoIssFixo(Mes mesVencimentoIssFixo) {
        this.mesVencimentoIssFixo = mesVencimentoIssFixo;
    }

    public Boolean getAtivaAlteracaoCadastral() {
        return ativaAlteracaoCadastral;
    }

    public void setAtivaAlteracaoCadastral(Boolean ativaAlteracaoCadastral) {
        this.ativaAlteracaoCadastral = ativaAlteracaoCadastral;
    }

    public String getEnderecoEmail() {
        return enderecoEmail;
    }

    public void setEnderecoEmail(String enderecoEmail) {
        this.enderecoEmail = enderecoEmail;
    }

    public String getSecretariaEmail() {
        return secretariaEmail;
    }

    public void setSecretariaEmail(String secretariaEmail) {
        this.secretariaEmail = secretariaEmail;
    }

    public String getBairroEmail() {
        return bairroEmail;
    }

    public void setBairroEmail(String bairroEmail) {
        this.bairroEmail = bairroEmail;
    }

    public String getCepEmail() {
        return cepEmail;
    }

    public void setCepEmail(String cepEmail) {
        this.cepEmail = cepEmail;
    }

    public String getTelefoneEmail() {
        return telefoneEmail;
    }

    public void setTelefoneEmail(String telefoneEmail) {
        this.telefoneEmail = telefoneEmail;
    }

    public String getRodapePadraoEmail() {
        return rodapePadraoEmail;
    }

    public void setRodapePadraoEmail(String rodapePadraoEmail) {
        this.rodapePadraoEmail = rodapePadraoEmail;
    }

    public String getRodapeRbTransEmail() {
        return rodapeRbTransEmail;
    }

    public void setRodapeRbTransEmail(String rodapeRbTransEmail) {
        this.rodapeRbTransEmail = rodapeRbTransEmail;
    }

    public String getTextoSolicitacaoITBI() {
        return textoSolicitacaoITBI;
    }

    public void setTextoSolicitacaoITBI(String textoSolicitacaoITBI) {
        this.textoSolicitacaoITBI = textoSolicitacaoITBI;
    }

    public Tributo getTributoHabitese() {
        return tributoHabitese;
    }

    public void setTributoHabitese(Tributo tributoHabitese) {
        this.tributoHabitese = tributoHabitese;
    }

    public List<ConfiguracaoAlvara> getConfiguracaoAlvara() {
        return configuracaoAlvara;
    }

    public void setConfiguracaoAlvara(List<ConfiguracaoAlvara> configuracaoAlvara) {
        this.configuracaoAlvara = configuracaoAlvara;
    }

    public Integer getAnoCnaeBaixoRiscoA() {
        return anoCnaeBaixoRiscoA;
    }

    public void setAnoCnaeBaixoRiscoA(Integer anoCnaeBaixoRiscoA) {
        this.anoCnaeBaixoRiscoA = anoCnaeBaixoRiscoA;
    }

    public Integer getAnoCnaeBaixoRiscoB() {
        return anoCnaeBaixoRiscoB;
    }

    public void setAnoCnaeBaixoRiscoB(Integer anoCnaeBaixoRiscoB) {
        this.anoCnaeBaixoRiscoB = anoCnaeBaixoRiscoB;
    }

    public Integer getAnoCnaeAltoRisco() {
        return anoCnaeAltoRisco;
    }

    public void setAnoCnaeAltoRisco(Integer anoCnaeAltoRisco) {
        this.anoCnaeAltoRisco = anoCnaeAltoRisco;
    }

    public String getCodigoApresentante() {
        return codigoApresentante;
    }

    public void setCodigoApresentante(String codigoApresentante) {
        this.codigoApresentante = codigoApresentante;
    }

    public String getNomeApresentante() {
        return nomeApresentante;
    }

    public void setNomeApresentante(String nomeApresentante) {
        this.nomeApresentante = nomeApresentante;
    }

    public List<ParametroValorAlvaraAmbiental> getParametrosValorAlvaraAmbiental() {
        return parametrosValorAlvaraAmbiental;
    }

    public void setParametrosValorAlvaraAmbiental(List<ParametroValorAlvaraAmbiental> parametrosValorAlvaraAmbiental) {
        this.parametrosValorAlvaraAmbiental = parametrosValorAlvaraAmbiental;
    }

    public Boolean getCanEmitirCarneIPTUPortal() {
        return canEmitirCarneIPTUPortal != null ? canEmitirCarneIPTUPortal : Boolean.FALSE;
    }

    public void setCanEmitirCarneIPTUPortal(Boolean canEmitirCarneIPTUPortal) {
        this.canEmitirCarneIPTUPortal = canEmitirCarneIPTUPortal;
    }

    public Long getSeqIncialRemessaProtesto() {
        return seqIncialRemessaProtesto != null ? seqIncialRemessaProtesto : 1L;
    }

    public void setSeqIncialRemessaProtesto(Long seqIncialRemessaProtesto) {
        this.seqIncialRemessaProtesto = seqIncialRemessaProtesto;
    }

    public Divida getDividaContratoConcessao() {
        return dividaContratoConcessao;
    }

    public void setDividaContratoConcessao(Divida dividaContratoConcessao) {
        this.dividaContratoConcessao = dividaContratoConcessao;
    }

    public Tributo getTributoContratoConcessao() {
        return tributoContratoConcessao;
    }

    public void setTributoContratoConcessao(Tributo tributoContratoConcessao) {
        this.tributoContratoConcessao = tributoContratoConcessao;
    }

    public String getLinkPlanoDiretor() {
        return linkPlanoDiretor;
    }

    public void setLinkPlanoDiretor(String linkPlanoDiretor) {
        this.linkPlanoDiretor = linkPlanoDiretor;
    }

    public ConfiguracaoTributarioBI getConfiguracaoTributarioBI() {
        return configuracaoTributarioBI;
    }

    public void setConfiguracaoTributarioBI(ConfiguracaoTributarioBI configuracaoTributarioBI) {
        this.configuracaoTributarioBI = configuracaoTributarioBI;
    }
}
