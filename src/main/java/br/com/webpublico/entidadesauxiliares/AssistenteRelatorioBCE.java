package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.controlerelatorio.RelatorioCadastroEconomicoControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

public class AssistenteRelatorioBCE implements Serializable {

    public static final int RELATORIO_COMPLETO = 1;
    public static final int RELATORIO_SIMPLIFICADO = 0;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioCadastroEconomicoControlador.class);
    private final AbstractReport abstractReport;

    private Integer tipoRelatorio = RELATORIO_SIMPLIFICADO;

    private String inscricaoCadastralInicial;
    private String inscricaoCadastralFinal;
    private String numeroInicial;
    private String numeroFinal;
    private String ocupacao;
    private String classifAtividade;
    private String ordenacao;
    private String ordemSql;
    private String cpfCnpj;
    private CNAE cnae;
    private Logradouro logradouro;
    private NaturezaJuridica naturezaJuridica;
    private UsuarioSistema usuarioSistema;
    private CadastroEconomico cadastroEconomico;
    private CadastroEconomicoImpressaoHist cadastroEconomicoImpressaoHist;
    private GrauDeRiscoDTO grauDeRisco;
    private RegimeTributario regimeTributario;
    private TipoIssqn tipoIss;
    private TipoCadastroTributario tipoCadastroTributario;
    private TipoAutonomo tipoAutonomo;
    private TipoPorte porte;
    private SituacaoCadastralCadastroEconomico situacao;
    private Boolean mei;
    private Boolean substitutoTributario;
    private Date dataCadastroEmpresa;
    private StringBuilder where;
    private StringBuilder criterios;

    private String chave;
    private InputStream qrCode;

    public AssistenteRelatorioBCE() {
        ordenacao = "S";
        cpfCnpj = null;
        tipoCadastroTributario = null;
        inscricaoCadastralInicial = "1";
        inscricaoCadastralFinal = "9999999999";

        abstractReport = AbstractReport.getAbstractReport();
        abstractReport.setGeraNoDialog(Boolean.TRUE);
    }

    public static int getRelatorioCompleto() {
        return RELATORIO_COMPLETO;
    }

    public static int getRelatorioSimplificado() {
        return RELATORIO_SIMPLIFICADO;
    }

    public static Logger getLogger() {
        return logger;
    }

    public AbstractReport getAbstractReport() {
        return abstractReport;
    }

    public Integer getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(Integer tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public String getInscricaoCadastralInicial() {
        return inscricaoCadastralInicial;
    }

    public void setInscricaoCadastralInicial(String inscricaoCadastralInicial) {
        this.inscricaoCadastralInicial = inscricaoCadastralInicial;
    }

    public String getInscricaoCadastralFinal() {
        return inscricaoCadastralFinal;
    }

    public void setInscricaoCadastralFinal(String inscricaoCadastralFinal) {
        this.inscricaoCadastralFinal = inscricaoCadastralFinal;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(String ocupacao) {
        this.ocupacao = ocupacao;
    }

    public String getClassifAtividade() {
        return classifAtividade;
    }

    public void setClassifAtividade(String classifAtividade) {
        this.classifAtividade = classifAtividade;
    }

    public String getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(String ordenacao) {
        this.ordenacao = ordenacao;
    }

    public String getOrdemSql() {
        return ordemSql;
    }

    public void setOrdemSql(String ordemSql) {
        this.ordemSql = ordemSql;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public CadastroEconomicoImpressaoHist getCadastroEconomicoImpressaoHist() {
        return cadastroEconomicoImpressaoHist;
    }

    public void setCadastroEconomicoImpressaoHist(CadastroEconomicoImpressaoHist cadastroEconomicoImpressaoHist) {
        this.cadastroEconomicoImpressaoHist = cadastroEconomicoImpressaoHist;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public RegimeTributario getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributario regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    public TipoIssqn getTipoIss() {
        return tipoIss;
    }

    public void setTipoIss(TipoIssqn tipoIss) {
        this.tipoIss = tipoIss;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public TipoAutonomo getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(TipoAutonomo tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public TipoPorte getPorte() {
        return porte;
    }

    public void setPorte(TipoPorte porte) {
        this.porte = porte;
    }

    public SituacaoCadastralCadastroEconomico getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoCadastralCadastroEconomico situacao) {
        this.situacao = situacao;
    }

    public Boolean getMei() {
        return mei;
    }

    public void setMei(Boolean mei) {
        this.mei = mei;
    }

    public Boolean getSubstitutoTributario() {
        return substitutoTributario;
    }

    public void setSubstitutoTributario(Boolean substitutoTributario) {
        this.substitutoTributario = substitutoTributario;
    }

    public Date getDataCadastroEmpresa() {
        return dataCadastroEmpresa;
    }

    public void setDataCadastroEmpresa(Date dataCadastroEmpresa) {
        this.dataCadastroEmpresa = dataCadastroEmpresa;
    }

    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public StringBuilder getCriterios() {
        return criterios;
    }

    public void setCriterios(StringBuilder criterios) {
        this.criterios = criterios;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public InputStream getQrCode() {
        return qrCode;
    }

    public void setQrCode(InputStream qrCode) {
        this.qrCode = qrCode;
    }
}
