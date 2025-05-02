package br.com.webpublico.entidades.rh.esocial;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.enums.rh.esocial.*;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by William on 05/06/2018.
 */
@Entity
@Audited
@Etiqueta("Configuração do Empregador E-Social")
@Table(name = "CONFIGEMPREGADORESOCIAL")
public class ConfiguracaoEmpregadorESocial extends SuperEntidade implements PossuidorArquivo, IHistoricoEsocial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Entidade")
    private Entidade entidade;
    @OneToMany(mappedBy = "configEmpregadorEsocial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemConfiguracaoEmpregadorESocial> itemConfiguracaoEmpregadorESocial = Lists.newArrayList();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private DetentorArquivoComposicao certificado;
    @Obrigatorio
    @Etiqueta("Classificação Tributária")
    @Enumerated(EnumType.STRING)
    private ClassificacaoTributariaESocial classificacaoTributaria;
    @Obrigatorio
    @Etiqueta("Senha do Certificado")
    private String senhaCertificado;

    private String numeroSiafi;
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Etiqueta("Final de Vigência")
    private Date finalVigencia;
    @Enumerated(EnumType.STRING)
    private OptouPontoEletronico optouPontoEletronico;

    private Boolean indicativoEntidadeEducativa;
    private Boolean indicativoTrabalhoTemporario;
    private Integer numeroRegTrabalhoTemporario;
    //dados isenção
    private String siglaNomeLeiCertificado;
    private String numeroCertificado;
    private Date dataEmissaoCertificado;
    private Date dataVencimentoCertificado;
    private String numeroProtocoloRenovacao;
    private Date dataProtocoloRenovacao;
    private Date dataDou;
    private Integer paginaDou;
    @Obrigatorio
    @ManyToOne
    private PessoaFisica responsavel;
    private Boolean enteFederativoResponsavel;
    @ManyToOne
    private PessoaJuridica enteFederativo;
    private Boolean possuiPrevidenciaPropria;
    @Enumerated(EnumType.STRING)
    private EsferaDoPoder poderSubteto;
    private BigDecimal valorSubtetoEnteFederativo;
    @ManyToOne
    private PessoaJuridica softwareHouse;
    @ManyToOne
    private PessoaFisica responsavelSotwareHouse;
    @ManyToOne
    private PessoaJuridica entidadeEducativa;
    @ManyToOne
    private CodigoAliquotaFPAEsocial codigoAliquotaFPAEsocial;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private SituacaoPessoaJuridicaESocial situacaoPessoaJuridicaESocial;
    @Obrigatorio
    @Etiqueta("Tipo de Ambiente")
    @Enumerated(EnumType.STRING)
    private TipoAmbienteESocial tipoAmbienteESocial;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Início da Obrigatoriedade do envio 1º Fase")
    private Date dataInicioObrigatoriedadeFase1;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Início da Obrigatoriedade do envio 2º Fase")
    private Date dataInicioObrigatoriedadeFase2;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Início da Obrigatoriedade do envio 3º Fase")
    private Date dataInicioObrigatoriedadeFase3;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Início da Obrigatoriedade do envio 4º Fase")
    private Date dataInicioObrigatoriedadeFase4;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Início da Obrigatoriedade do envio 4º Fase")
    private Date dataInicioObrigatoriedadeS2400;

    //Estabelecimento
    private Boolean emiteAvisoPrevio;
    @Enumerated(EnumType.STRING)
    private TipoPontoEletronicoESocial tipoPontoEletronicoESocial;
    @Enumerated(EnumType.STRING)
    private IndicativoMenorAprendizESocial indicativoMenorAprendiz;
    private Boolean menorAprendizEducativo;

    @Enumerated(EnumType.STRING)
    private TipoContratacaoDeficienciaESocial tipoContratacaoDeficiencia;

    @Enumerated(EnumType.STRING)
    private TipoLotacaoTributariaESocial tipoLotacaoTributaria;
    @ManyToOne
    private ProcessoAdministrativoJudicial processoJudAlteracaoRat;
    @ManyToOne
    private ProcessoAdministrativoJudicial processoJudAlteracaoFap;
    @ManyToOne
    private ProcessoAdministrativoJudicial processoJudMenorAprendiz;
    @ManyToOne
    private ProcessoAdministrativoJudicial processoJudPCD;
    private Boolean criarNotificacaoFase1;
    private Boolean criarNotificacaoFase2;
    private Boolean criarNotificacaoFase3;
    private Boolean criarNotificacaoFase4;
    private Date dataInicioReinf;
    private Date dataFimReinf;

    @ManyToOne
    private PessoaFisica responsavelReinf;

    @OneToMany(mappedBy = "empregador", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<IndicativoContribuicao> itemIndicativoContribuicao = Lists.newArrayList();

    @Transient
    private String identificacaoTabela;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public List<ItemConfiguracaoEmpregadorESocial> getItemConfiguracaoEmpregadorESocial() {
        return itemConfiguracaoEmpregadorESocial;
    }

    public void setItemConfiguracaoEmpregadorESocial(List<ItemConfiguracaoEmpregadorESocial> itemConfiguracaoEmpregadorESocial) {
        this.itemConfiguracaoEmpregadorESocial = itemConfiguracaoEmpregadorESocial;
    }

    public String getIdentificacaoTabela() {
        return identificacaoTabela;
    }

    public void setIdentificacaoTabela(String identificacaoTabela) {
        this.identificacaoTabela = identificacaoTabela;
    }

    public Boolean getCriarNotificacaoFase1() {
        return criarNotificacaoFase1 != null ? criarNotificacaoFase1 : false;
    }

    public void setCriarNotificacaoFase1(Boolean criarNotificacaoFase1) {
        this.criarNotificacaoFase1 = criarNotificacaoFase1;
    }

    public Boolean getCriarNotificacaoFase2() {
        return criarNotificacaoFase2 != null ? criarNotificacaoFase2 : false;
    }

    public void setCriarNotificacaoFase2(Boolean criarNotificacaoFase2) {
        this.criarNotificacaoFase2 = criarNotificacaoFase2;
    }

    public Boolean getCriarNotificacaoFase3() {
        return criarNotificacaoFase3 != null ? criarNotificacaoFase3 : false;
    }

    public void setCriarNotificacaoFase3(Boolean criarNotificacaoFase3) {
        this.criarNotificacaoFase3 = criarNotificacaoFase3;
    }

    public Boolean getCriarNotificacaoFase4() {
        return criarNotificacaoFase4 != null ? criarNotificacaoFase4 : false;
    }

    public void setCriarNotificacaoFase4(Boolean criarNotificacaoFase4) {
        this.criarNotificacaoFase4 = criarNotificacaoFase4;
    }

    public Boolean getEmiteAvisoPrevio() {
        return emiteAvisoPrevio != null ? emiteAvisoPrevio : false;
    }

    public void setEmiteAvisoPrevio(Boolean emiteAvisoPrevio) {
        this.emiteAvisoPrevio = emiteAvisoPrevio;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public CodigoAliquotaFPAEsocial getCodigoAliquotaFPAEsocial() {
        return codigoAliquotaFPAEsocial;
    }

    public void setCodigoAliquotaFPAEsocial(CodigoAliquotaFPAEsocial codigoAliquotaFPAEsocial) {
        this.codigoAliquotaFPAEsocial = codigoAliquotaFPAEsocial;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return certificado;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        certificado = detentorArquivoComposicao;
    }

    public ClassificacaoTributariaESocial getClassificacaoTributaria() {
        return classificacaoTributaria;
    }

    public void setClassificacaoTributaria(ClassificacaoTributariaESocial classificacaoTributaria) {
        this.classificacaoTributaria = classificacaoTributaria;
    }

    public PessoaJuridica getEntidadeEducativa() {
        return entidadeEducativa;
    }

    public void setEntidadeEducativa(PessoaJuridica entidadeEducativa) {
        this.entidadeEducativa = entidadeEducativa;
    }

    public String getSenhaCertificado() {
        return senhaCertificado;
    }

    public void setSenhaCertificado(String senhaCertificado) {
        this.senhaCertificado = senhaCertificado;
    }

    public DetentorArquivoComposicao getCertificado() {
        return certificado;
    }

    public void setCertificado(DetentorArquivoComposicao certificado) {
        this.certificado = certificado;
    }

    public String getNumeroSiafi() {
        return numeroSiafi;
    }

    public void setNumeroSiafi(String numeroSiafi) {
        this.numeroSiafi = numeroSiafi;
    }

    public OptouPontoEletronico getOptouPontoEletronico() {
        return optouPontoEletronico;
    }

    public void setOptouPontoEletronico(OptouPontoEletronico optouPontoEletronico) {
        this.optouPontoEletronico = optouPontoEletronico;
    }

    public Boolean getIndicativoEntidadeEducativa() {
        return indicativoEntidadeEducativa;
    }

    public void setIndicativoEntidadeEducativa(Boolean indicativoEntidadeEducativa) {
        this.indicativoEntidadeEducativa = indicativoEntidadeEducativa;
    }

    public Boolean getIndicativoTrabalhoTemporario() {
        return indicativoTrabalhoTemporario == null ? false : indicativoTrabalhoTemporario;
    }

    public void setIndicativoTrabalhoTemporario(Boolean indicativoTrabalhoTemporario) {
        this.indicativoTrabalhoTemporario = indicativoTrabalhoTemporario;
    }

    public Integer getNumeroRegTrabalhoTemporario() {
        return numeroRegTrabalhoTemporario;
    }

    public void setNumeroRegTrabalhoTemporario(Integer numeroRegTrabalhoTemporario) {
        this.numeroRegTrabalhoTemporario = numeroRegTrabalhoTemporario;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public Boolean getEnteFederativoResponsavel() {
        return enteFederativoResponsavel;
    }

    public void setEnteFederativoResponsavel(Boolean enteFederativoResponsavel) {
        this.enteFederativoResponsavel = enteFederativoResponsavel;
    }

    public PessoaJuridica getEnteFederativo() {
        return enteFederativo;
    }

    public void setEnteFederativo(PessoaJuridica enteFederativo) {
        this.enteFederativo = enteFederativo;
    }

    public Boolean getPossuiPrevidenciaPropria() {
        return possuiPrevidenciaPropria;
    }

    public void setPossuiPrevidenciaPropria(Boolean possuiPrevidenciaPropria) {
        this.possuiPrevidenciaPropria = possuiPrevidenciaPropria;
    }

    public EsferaDoPoder getPoderSubteto() {
        return poderSubteto;
    }

    public void setPoderSubteto(EsferaDoPoder poderSubteto) {
        this.poderSubteto = poderSubteto;
    }

    public BigDecimal getValorSubtetoEnteFederativo() {
        return valorSubtetoEnteFederativo;
    }

    public void setValorSubtetoEnteFederativo(BigDecimal valorSubtetoEnteFederativo) {
        this.valorSubtetoEnteFederativo = valorSubtetoEnteFederativo;
    }

    public TipoAmbienteESocial getTipoAmbienteESocial() {
        return tipoAmbienteESocial;
    }

    public void setTipoAmbienteESocial(TipoAmbienteESocial tipoAmbienteESocial) {
        this.tipoAmbienteESocial = tipoAmbienteESocial;
    }

    public String getSiglaNomeLeiCertificado() {
        return siglaNomeLeiCertificado;
    }

    public void setSiglaNomeLeiCertificado(String siglaNomeLeiCertificado) {
        this.siglaNomeLeiCertificado = siglaNomeLeiCertificado;
    }

    public String getNumeroCertificado() {
        return numeroCertificado;
    }

    public void setNumeroCertificado(String numeroCertificado) {
        this.numeroCertificado = numeroCertificado;
    }

    public Date getDataEmissaoCertificado() {
        return dataEmissaoCertificado;
    }

    public void setDataEmissaoCertificado(Date dataEmissaoCertificado) {
        this.dataEmissaoCertificado = dataEmissaoCertificado;
    }

    public Date getDataVencimentoCertificado() {
        return dataVencimentoCertificado;
    }

    public void setDataVencimentoCertificado(Date dataVencimentoCertificado) {
        this.dataVencimentoCertificado = dataVencimentoCertificado;
    }

    public String getNumeroProtocoloRenovacao() {
        return numeroProtocoloRenovacao;
    }

    public void setNumeroProtocoloRenovacao(String numeroProtocoloRenovacao) {
        this.numeroProtocoloRenovacao = numeroProtocoloRenovacao;
    }

    public Date getDataProtocoloRenovacao() {
        return dataProtocoloRenovacao;
    }

    public void setDataProtocoloRenovacao(Date dataProtocoloRenovacao) {
        this.dataProtocoloRenovacao = dataProtocoloRenovacao;
    }

    public Date getDataDou() {
        return dataDou;
    }

    public void setDataDou(Date dataDou) {
        this.dataDou = dataDou;
    }

    public Integer getPaginaDou() {
        return paginaDou;
    }

    public void setPaginaDou(Integer paginaDou) {
        this.paginaDou = paginaDou;
    }

    public PessoaJuridica getSoftwareHouse() {
        return softwareHouse;
    }

    public void setSoftwareHouse(PessoaJuridica softwareHouse) {
        this.softwareHouse = softwareHouse;
    }

    public PessoaFisica getResponsavelSotwareHouse() {
        return responsavelSotwareHouse;
    }

    public void setResponsavelSotwareHouse(PessoaFisica responsavelSotwareHouse) {
        this.responsavelSotwareHouse = responsavelSotwareHouse;
    }

    public SituacaoPessoaJuridicaESocial getSituacaoPessoaJuridicaESocial() {
        return situacaoPessoaJuridicaESocial;
    }

    public void setSituacaoPessoaJuridicaESocial(SituacaoPessoaJuridicaESocial situacaoPessoaJuridicaESocial) {
        this.situacaoPessoaJuridicaESocial = situacaoPessoaJuridicaESocial;
    }

    public Boolean dadosIsencoesObrigatorios() {
        return getSiglaNomeLeiCertificado() != null
            && getNumeroCertificado() != null && getDataEmissaoCertificado() != null && getDataVencimentoCertificado() != null;
    }

    public Boolean nenhumCampoPrenchidoDadosIsencao() {
        return getSiglaNomeLeiCertificado() == null
            && getNumeroCertificado() == null && getDataEmissaoCertificado() == null && getDataVencimentoCertificado() == null
            && getNumeroProtocoloRenovacao() == null
            && getDataProtocoloRenovacao() == null && getDataDou() == null && getPaginaDou() == null;
    }

    public Boolean dadosIsencoesObrigatoriosNaoPrenchidos() {
        return getSiglaNomeLeiCertificado() == null
            || getNumeroCertificado() == null || getDataEmissaoCertificado() == null || getDataVencimentoCertificado() == null;
    }

    public Boolean dadosIsencoesNaoObrigatorios() {
        return getNumeroProtocoloRenovacao() != null
            || getDataProtocoloRenovacao() != null || getDataDou() != null || getPaginaDou() != null;
    }

    public TipoPontoEletronicoESocial getTipoPontoEletronicoESocial() {
        return tipoPontoEletronicoESocial;
    }

    public void setTipoPontoEletronicoESocial(TipoPontoEletronicoESocial tipoPontoEletronicoESocial) {
        this.tipoPontoEletronicoESocial = tipoPontoEletronicoESocial;
    }

    public IndicativoMenorAprendizESocial getIndicativoMenorAprendiz() {
        return indicativoMenorAprendiz;
    }

    public void setIndicativoMenorAprendiz(IndicativoMenorAprendizESocial indicativoMenorAprendiz) {
        this.indicativoMenorAprendiz = indicativoMenorAprendiz;
    }

    public Boolean getMenorAprendizEducativo() {
        return menorAprendizEducativo != null ? menorAprendizEducativo : false;
    }

    public void setMenorAprendizEducativo(Boolean menorAprendizEducativo) {
        this.menorAprendizEducativo = menorAprendizEducativo;
    }

    public TipoContratacaoDeficienciaESocial getTipoContratacaoDeficiencia() {
        return tipoContratacaoDeficiencia;
    }

    public void setTipoContratacaoDeficiencia(TipoContratacaoDeficienciaESocial tipoContratacaoDeficiencia) {
        this.tipoContratacaoDeficiencia = tipoContratacaoDeficiencia;
    }

    public TipoLotacaoTributariaESocial getTipoLotacaoTributaria() {
        return tipoLotacaoTributaria;
    }

    public void setTipoLotacaoTributaria(TipoLotacaoTributariaESocial tipoLotacaoTributaria) {
        this.tipoLotacaoTributaria = tipoLotacaoTributaria;
    }

    public ProcessoAdministrativoJudicial getProcessoJudAlteracaoRat() {
        return processoJudAlteracaoRat;
    }

    public void setProcessoJudAlteracaoRat(ProcessoAdministrativoJudicial processoJudAlteracaoRat) {
        this.processoJudAlteracaoRat = processoJudAlteracaoRat;
    }

    public ProcessoAdministrativoJudicial getProcessoJudAlteracaoFap() {
        return processoJudAlteracaoFap;
    }

    public void setProcessoJudAlteracaoFap(ProcessoAdministrativoJudicial processoJudAlteracaoFap) {
        this.processoJudAlteracaoFap = processoJudAlteracaoFap;
    }

    public ProcessoAdministrativoJudicial getProcessoJudMenorAprendiz() {
        return processoJudMenorAprendiz;
    }

    public void setProcessoJudMenorAprendiz(ProcessoAdministrativoJudicial processoJudMenorAprendiz) {
        this.processoJudMenorAprendiz = processoJudMenorAprendiz;
    }

    public ProcessoAdministrativoJudicial getProcessoJudPCD() {
        return processoJudPCD;
    }

    public void setProcessoJudPCD(ProcessoAdministrativoJudicial processoJudPCD) {
        this.processoJudPCD = processoJudPCD;
    }

    public Date getDataInicioObrigatoriedadeFase1() {
        return dataInicioObrigatoriedadeFase1;
    }

    public void setDataInicioObrigatoriedadeFase1(Date dataInicioObrigatoriedadeFase1) {
        this.dataInicioObrigatoriedadeFase1 = dataInicioObrigatoriedadeFase1;
    }

    public Date getDataInicioObrigatoriedadeFase2() {
        return dataInicioObrigatoriedadeFase2;
    }

    public void setDataInicioObrigatoriedadeFase2(Date dataInicioObrigatoriedadeFase2) {
        this.dataInicioObrigatoriedadeFase2 = dataInicioObrigatoriedadeFase2;
    }

    public Date getDataInicioObrigatoriedadeFase3() {
        return dataInicioObrigatoriedadeFase3;
    }

    public void setDataInicioObrigatoriedadeFase3(Date dataInicioObrigatoriedadeFase3) {
        this.dataInicioObrigatoriedadeFase3 = dataInicioObrigatoriedadeFase3;
    }

    public Date getDataInicioObrigatoriedadeFase4() {
        return dataInicioObrigatoriedadeFase4;
    }

    public void setDataInicioObrigatoriedadeFase4(Date dataInicioObrigatoriedadeFase4) {
        this.dataInicioObrigatoriedadeFase4 = dataInicioObrigatoriedadeFase4;
    }

    public Date getDataInicioReinf() {
        return dataInicioReinf;
    }

    public void setDataInicioReinf(Date dataInicioReinf) {
        this.dataInicioReinf = dataInicioReinf;
    }

    public Date getDataFimReinf() {
        return dataFimReinf;
    }

    public void setDataFimReinf(Date dataFimReinf) {
        this.dataFimReinf = dataFimReinf;
    }

    public Date getDataInicioObrigatoriedadeS2400() {
        return dataInicioObrigatoriedadeS2400;
    }

    public void setDataInicioObrigatoriedadeS2400(Date dataInicioObrigatoriedadeS2400) {
        this.dataInicioObrigatoriedadeS2400 = dataInicioObrigatoriedadeS2400;
    }

    public PessoaFisica getResponsavelReinf() {
        return responsavelReinf;
    }

    public void setResponsavelReinf(PessoaFisica responsavelReinf) {
        this.responsavelReinf = responsavelReinf;
    }

    public List<IndicativoContribuicao> getItemIndicativoContribuicao() {
        return itemIndicativoContribuicao;
    }

    public void setItemIndicativoContribuicao(List<IndicativoContribuicao> itemIndicativoContribuicao) {
        this.itemIndicativoContribuicao = itemIndicativoContribuicao;
    }

    @Override
    public String toString() {
        return entidade + "";
    }

    @Override
    public String getDescricaoCompleta() {
        return toString();
    }

    @Override
    public String getIdentificador() {
        return this.id.toString();
    }
}
