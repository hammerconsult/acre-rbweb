package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.ConfiguracaoFaltasInjustificadas;
import br.com.webpublico.entidades.ConfiguracaoRHClasseCredor;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.rh.dirf.DirfUsuario;
import br.com.webpublico.entidades.rh.webservices.ConfiguracaoWebServiceRH;
import br.com.webpublico.entidadesauxiliares.rh.portal.ConfiguracaoRHDTO;
import br.com.webpublico.enums.TipoDePlataforma;
import br.com.webpublico.enums.rh.administracaopagamento.TipoTercoFeriasAutomatico;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @Author peixe on 29/09/2015  11:22.
 */

@Entity
@Audited
@GrupoDiagrama(nome = "Configuração RH")
@Etiqueta("Configurações RH")
public class ConfiguracaoRH extends SuperEntidade implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Temporal(value = TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Final de Vigência")
    @Temporal(value = TemporalType.DATE)
    private Date finalVigencia;

    @Etiqueta("Configurações Holerite")
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private ConfiguracaoHolerite configuracaoHolerite;

    @Etiqueta("Configurações de faltas injustificadas")
    @Pesquisavel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoRH", orphanRemoval = true)
    private List<ConfiguracaoFaltasInjustificadas> configuracoesFaltasInjustificadas;

    @Etiqueta("Configurações Folha De Pagamento")
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private ConfiguracaoFP configuracaoFP;

    @Etiqueta("Configurações de Recisão")
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private ConfiguracaoRescisao configuracaoRescisao;

    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private ConfiguracaoCreditoSalario configuracaoCreditoSalario;
    @Etiqueta("Configurações de Previdência")
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private ConfiguracaoPrevidencia configuracaoPrevidencia;

    @Etiqueta("Configurações de Relatório")
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private ConfiguracaoRelatorio configuracaoRelatorio;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoRH", orphanRemoval = true)
    private List<HoraExtraUnidade> horaExtraUnidade;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoRH", orphanRemoval = true)
    private List<ConfiguracaoWebServiceRH> configuracaoWebServiceRH;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoRH", orphanRemoval = true)
    private List<ConfiguracaoRHClasseCredor> configuracoesClassesCredor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoRH", orphanRemoval = true)
    private List<DirfCodigo> itemDirfCodigo;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "configuracaoRH", orphanRemoval = true)
    private List<DirfUsuario> itemDirfUsuario;

    private BigDecimal horaExtraMaximaPadrao;

    private Integer diferencaDiasContratoPA;

    private Boolean notificarFeriasPortal;

    private Boolean mostrarDadosRHPortal;

    private Integer diasApresentacaoDocPortal;

    private Boolean integrarPortalTransparencia;

    private Boolean permitirAcessoPortal;

    private Boolean logESocial;

    private String vencimentoBasePortal;

    private Boolean folhaComplementarSemNormal;

    private String urlPortalServidorProducao;
    private String urlPortalServidorHomologacao;

    private Boolean creditoSalarioProducao;

    @Temporal(TemporalType.DATE)
    private Date dataObrigatoriedadeApuracaoIR;

    @Enumerated(EnumType.STRING)
    private TipoDePlataforma tipoDePlataforma;

    private Boolean exibirFolhaPortalAoEfetivar;
    private Boolean exibirMenuAvaliacao;
    private Boolean envioAutomaticoDadosPonto;
    @Enumerated(EnumType.STRING)
    private TipoTercoFeriasAutomatico tipoTercoFeriasAutomatico;


    public ConfiguracaoRH() {
        configuracaoHolerite = new ConfiguracaoHolerite();
        configuracaoFP = new ConfiguracaoFP();
        configuracaoRescisao = new ConfiguracaoRescisao();
        configuracaoPrevidencia = new ConfiguracaoPrevidencia();
        notificarFeriasPortal = false;
        mostrarDadosRHPortal = false;
        exibirMenuAvaliacao = false;
        configuracaoCreditoSalario = new ConfiguracaoCreditoSalario();
        configuracaoRelatorio = new ConfiguracaoRelatorio();
        horaExtraUnidade = Lists.newArrayList();
        configuracaoWebServiceRH = Lists.newArrayList();
        configuracoesClassesCredor = Lists.newArrayList();
        itemDirfCodigo = Lists.newArrayList();
        itemDirfUsuario = Lists.newArrayList();
    }

    public ConfiguracaoRH(Boolean permitirAcessoPortal) {
        this.permitirAcessoPortal = permitirAcessoPortal;
    }

    public List<ConfiguracaoWebServiceRH> getConfiguracaoWebServiceRH() {
        return configuracaoWebServiceRH;
    }

    public void setConfiguracaoWebServiceRH(List<ConfiguracaoWebServiceRH> configuracaoWebServiceRH) {
        this.configuracaoWebServiceRH = configuracaoWebServiceRH;
    }

    public Boolean getLogESocial() {
        return logESocial != null ? logESocial : false;
    }

    public void setLogESocial(Boolean logESocial) {
        this.logESocial = logESocial;
    }

    public Boolean getPermitirAcessoPortal() {
        return permitirAcessoPortal;
    }

    public void setPermitirAcessoPortal(Boolean permitirAcessoPortal) {
        this.permitirAcessoPortal = permitirAcessoPortal;
    }

    public List<HoraExtraUnidade> getHoraExtraUnidade() {
        return horaExtraUnidade;
    }

    public void setHoraExtraUnidade(List<HoraExtraUnidade> horaExtraUnidade) {
        this.horaExtraUnidade = horaExtraUnidade;
    }

    public ConfiguracaoFP getConfiguracaoFP() {
        return configuracaoFP;
    }

    public void setConfiguracaoFP(ConfiguracaoFP configuracaoFP) {
        this.configuracaoFP = configuracaoFP;
    }

    public Boolean getFolhaComplementarSemNormal() {
        return folhaComplementarSemNormal != null ? folhaComplementarSemNormal : false;
    }

    public List<DirfUsuario> getItemDirfUsuario() {
        return itemDirfUsuario;
    }

    public void setItemDirfUsuario(List<DirfUsuario> itemDirfUsuario) {
        this.itemDirfUsuario = itemDirfUsuario;
    }

    public void setFolhaComplementarSemNormal(Boolean folhaComplementarSemNormal) {
        this.folhaComplementarSemNormal = folhaComplementarSemNormal;
    }

    public Boolean getMostrarDadosRHPortal() {
        return mostrarDadosRHPortal == null ? false : mostrarDadosRHPortal;
    }

    public void setMostrarDadosRHPortal(Boolean mostrarDadosRHPortal) {
        this.mostrarDadosRHPortal = mostrarDadosRHPortal;
    }

    public List<DirfCodigo> getItemDirfCodigo() {
        return itemDirfCodigo;
    }

    public void setItemDirfCodigo(List<DirfCodigo> itemDirfCodigo) {
        this.itemDirfCodigo = itemDirfCodigo;
    }

    public Integer getDiferencaDiasContratoPA() {
        return diferencaDiasContratoPA;
    }

    public void setDiferencaDiasContratoPA(Integer diferencaDiasContratoPA) {
        this.diferencaDiasContratoPA = diferencaDiasContratoPA;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getHoraExtraMaximaPadrao() {
        return horaExtraMaximaPadrao;
    }

    public void setHoraExtraMaximaPadrao(BigDecimal horaExtraMaximaPadrao) {
        this.horaExtraMaximaPadrao = horaExtraMaximaPadrao;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return finalVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public ConfiguracaoHolerite getConfiguracaoHolerite() {
        return configuracaoHolerite;
    }

    public void setConfiguracaoHolerite(ConfiguracaoHolerite configuracaoHolerite) {
        this.configuracaoHolerite = configuracaoHolerite;
    }

    public List<ConfiguracaoFaltasInjustificadas> getConfiguracoesFaltasInjustificadas() {
        return configuracoesFaltasInjustificadas;
    }

    public void setConfiguracoesFaltasInjustificadas(List<ConfiguracaoFaltasInjustificadas> configuracoesFaltasInjustificadas) {
        this.configuracoesFaltasInjustificadas = configuracoesFaltasInjustificadas;
    }

    public ConfiguracaoRescisao getConfiguracaoRescisao() {
        return configuracaoRescisao;
    }

    public void setConfiguracaoRescisao(ConfiguracaoRescisao configuracaoRescisao) {
        this.configuracaoRescisao = configuracaoRescisao;
    }

    public ConfiguracaoCreditoSalario getConfiguracaoCreditoSalario() {
        return configuracaoCreditoSalario;
    }

    public void setConfiguracaoCreditoSalario(ConfiguracaoCreditoSalario configuracaoCreditoSalario) {
        this.configuracaoCreditoSalario = configuracaoCreditoSalario;
    }

    public ConfiguracaoPrevidencia getConfiguracaoPrevidencia() {
        return configuracaoPrevidencia;
    }

    public void setConfiguracaoPrevidencia(ConfiguracaoPrevidencia configuracaoPrevidencia) {
        this.configuracaoPrevidencia = configuracaoPrevidencia;
    }

    public ConfiguracaoRelatorio getConfiguracaoRelatorio() {
        return configuracaoRelatorio;
    }

    public void setConfiguracaoRelatorio(ConfiguracaoRelatorio configuracaoRelatorio) {
        this.configuracaoRelatorio = configuracaoRelatorio;
    }

    public Boolean getNotificarFeriasPortal() {
        return notificarFeriasPortal == null ? false : notificarFeriasPortal;
    }

    public void setNotificarFeriasPortal(Boolean notificarFeriasPortal) {
        this.notificarFeriasPortal = notificarFeriasPortal;
    }

    public Integer getDiasApresentacaoDocPortal() {
        return diasApresentacaoDocPortal;
    }

    public void setDiasApresentacaoDocPortal(Integer diasApresentacaoDocPortal) {
        this.diasApresentacaoDocPortal = diasApresentacaoDocPortal;
    }

    public Boolean getIntegrarPortalTransparencia() {
        return integrarPortalTransparencia;
    }

    public void setIntegrarPortalTransparencia(Boolean integrarPortalTransparencia) {
        this.integrarPortalTransparencia = integrarPortalTransparencia;
    }

    @Override
    public String toString() {
        return "Configuração de " + DataUtil.getDataFormatada(inicioVigencia);
    }

    public List<ConfiguracaoFaltasInjustificadas> getConfiguracaoFaltasInjustificadasOrdenadas() {
        if (configuracoesFaltasInjustificadas == null) {
            return null;
        }
        Collections.sort(configuracoesFaltasInjustificadas);
        return configuracoesFaltasInjustificadas;
    }

    public List<ConfiguracaoFaltasInjustificadas> getConfiguracaoFaltasInjustificadasOrdenadasDecrescentemente() {
        if (configuracoesFaltasInjustificadas == null) {
            return configuracoesFaltasInjustificadas;
        }
        Collections.reverse(getConfiguracaoFaltasInjustificadasOrdenadas());
        return configuracoesFaltasInjustificadas;
    }

    public ConfiguracaoRHDTO toConfiguracaoRHDTO() {
        ConfiguracaoRHDTO configuracaoRHDTO = new ConfiguracaoRHDTO();
        configuracaoRHDTO.setId(this.id);
        configuracaoRHDTO.setPermitirAcessoPortal(this.permitirAcessoPortal);
        configuracaoRHDTO.setExibirMenuAvaliacao(this.exibirMenuAvaliacao);
        return configuracaoRHDTO;
    }

    public String getVencimentoBasePortal() {
        return vencimentoBasePortal;
    }

    public void setVencimentoBasePortal(String vencimentoBasePortal) {
        this.vencimentoBasePortal = vencimentoBasePortal;
    }

    public List<ConfiguracaoRHClasseCredor> getConfiguracoesClassesCredor() {
        return configuracoesClassesCredor;
    }

    public void setConfiguracoesClassesCredor(List<ConfiguracaoRHClasseCredor> configuracoesClassesCredor) {
        this.configuracoesClassesCredor = configuracoesClassesCredor;
    }

    public String getUrlPortalServidorProducao() {
        return urlPortalServidorProducao;
    }

    public void setUrlPortalServidorProducao(String urlPortalServidorProducao) {
        this.urlPortalServidorProducao = urlPortalServidorProducao;
    }

    public String getUrlPortalServidorHomologacao() {
        return urlPortalServidorHomologacao;
    }

    public void setUrlPortalServidorHomologacao(String urlPortalServidorHomologacao) {
        this.urlPortalServidorHomologacao = urlPortalServidorHomologacao;
    }

    public Boolean getCreditoSalarioProducao() {
        return creditoSalarioProducao == null ? Boolean.FALSE : creditoSalarioProducao;
    }

    public void setCreditoSalarioProducao(Boolean creditoSalarioProducao) {
        this.creditoSalarioProducao = creditoSalarioProducao;
    }

    public TipoDePlataforma getTipoDePlataforma() {
        return tipoDePlataforma;
    }

    public void setTipoDePlataforma(TipoDePlataforma tipoDePlataforma) {
        this.tipoDePlataforma = tipoDePlataforma;
    }

    public Boolean getExibirFolhaPortalAoEfetivar() {
        return exibirFolhaPortalAoEfetivar == null ? Boolean.FALSE : exibirFolhaPortalAoEfetivar;
    }

    public void setExibirFolhaPortalAoEfetivar(Boolean exibirFolhaPortalAoEfetivar) {
        this.exibirFolhaPortalAoEfetivar = exibirFolhaPortalAoEfetivar;
    }

    public Date getDataObrigatoriedadeApuracaoIR() {
        return dataObrigatoriedadeApuracaoIR;
    }

    public void setDataObrigatoriedadeApuracaoIR(Date dataObrigatoriedadeApuracaoIR) {
        this.dataObrigatoriedadeApuracaoIR = dataObrigatoriedadeApuracaoIR;
    }

    public Boolean getExibirMenuAvaliacao() {
        return exibirMenuAvaliacao;
    }

    public void setExibirMenuAvaliacao(Boolean exibirMenuAvaliacao) {
        this.exibirMenuAvaliacao = exibirMenuAvaliacao;
    }

    public Boolean getEnvioAutomaticoDadosPonto() {
        if (envioAutomaticoDadosPonto == null) {
            return false;
        }
        return envioAutomaticoDadosPonto;
    }

    public void setEnvioAutomaticoDadosPonto(Boolean envioAutomaticoDadosPonto) {
        this.envioAutomaticoDadosPonto = envioAutomaticoDadosPonto;
    }

    public TipoTercoFeriasAutomatico getTipoTercoFeriasAutomatico() {
        return tipoTercoFeriasAutomatico;
    }

    public void setTipoTercoFeriasAutomatico(TipoTercoFeriasAutomatico tipoTercoFeriasAutomatico) {
        this.tipoTercoFeriasAutomatico = tipoTercoFeriasAutomatico;
    }
}
