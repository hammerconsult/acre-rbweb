package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.Servico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.enums.TipoDeclaracaoMensalServico;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.enums.TipoParametroNfse;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ConfiguracaoNfse extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Quantidade de Tentativa de Acesso")
    private Integer tentativaAcessoIndevido;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorArquivoComposicao arquivoCertificadoDigital;
    private String senhaCertificadoDigital;
    @Temporal(TemporalType.DATE)
    private Date vencimentoCertificadoDigital;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ConfiguracaoNfseRelatorio configuracaoNfseRelatorio;
    @ManyToOne
    private Cidade cidade;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "configuracao", fetch = FetchType.EAGER)
    private List<ConfiguracaoNfseParametros> parametros;
    @OneToMany(mappedBy = "configuracaoNfse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConfiguracaoNfseDivida> dividasNfse;
    @ManyToOne
    private Servico padraoServicoPrestado;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ConfiguracaoIssForaMunicipio configuracaoIssForaMun;

    @ManyToOne(cascade = CascadeType.ALL)
    private ConfiguracaoNotaPremiada configuracaoNotaPremiada;

    @ManyToOne(cascade = CascadeType.ALL)
    private ConfiguracaoDesif configuracaoDesif;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ConfiguracaoNfseNacional configuracaoNfseNacional;

    @Transient
    private Boolean homologacao = Boolean.FALSE;

    public ConfiguracaoNfse() {
        super();
        configuracaoNfseRelatorio = new ConfiguracaoNfseRelatorio();
        configuracaoNotaPremiada = new ConfiguracaoNotaPremiada();
        parametros = Lists.newArrayList();
        dividasNfse = Lists.newArrayList();
        configuracaoIssForaMun = new ConfiguracaoIssForaMunicipio();
        configuracaoDesif = new ConfiguracaoDesif();
        configuracaoNfseNacional = new ConfiguracaoNfseNacional();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTentativaAcessoIndevido() {
        return tentativaAcessoIndevido;
    }

    public void setTentativaAcessoIndevido(Integer tentativaAcessoIndevido) {
        this.tentativaAcessoIndevido = tentativaAcessoIndevido;
    }

    public DetentorArquivoComposicao getArquivoCertificadoDigital() {
        return arquivoCertificadoDigital;
    }

    public void setArquivoCertificadoDigital(DetentorArquivoComposicao arquivoCertificadoDigital) {
        this.arquivoCertificadoDigital = arquivoCertificadoDigital;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return arquivoCertificadoDigital;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.arquivoCertificadoDigital = detentorArquivoComposicao;
    }

    public String getSenhaCertificadoDigital() {
        return senhaCertificadoDigital;
    }

    public void setSenhaCertificadoDigital(String senhaCertificadoDigital) {
        this.senhaCertificadoDigital = senhaCertificadoDigital;
    }

    public Date getVencimentoCertificadoDigital() {
        return vencimentoCertificadoDigital;
    }

    public void setVencimentoCertificadoDigital(Date vencimentoCertificadoDigital) {
        this.vencimentoCertificadoDigital = vencimentoCertificadoDigital;
    }

    public ConfiguracaoNfseRelatorio getConfiguracaoNfseRelatorio() {
        return configuracaoNfseRelatorio;
    }

    public void setConfiguracaoNfseRelatorio(ConfiguracaoNfseRelatorio configuracaoNfseRelatorio) {
        this.configuracaoNfseRelatorio = configuracaoNfseRelatorio;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getUrlNfse() {
        return getParametroString(getHomologacao() ? TipoParametroNfse.URL_APLICACAO_NFSE_HOMOLOGACAO : TipoParametroNfse.URL_APLICACAO_NFSE);
    }

    public String getUrlRest() {
        return getParametroString(getHomologacao() ? TipoParametroNfse.URL_REST_NFSE_HOMOLOGACAO : TipoParametroNfse.URL_REST_NFSE);
    }

    public String getUrlAutenticacaoNfse() {
        return getParametroString(getHomologacao() ? TipoParametroNfse.URL_AUTENTICACAO_NFSE_HOMOLOGACAO : TipoParametroNfse.URL_AUTENTICACAO_NFSE);
    }

    public List<ConfiguracaoNfseDivida> getDividasNfse() {
        return dividasNfse;
    }

    public void setDividasNfse(List<ConfiguracaoNfseDivida> dividasNfse) {
        this.dividasNfse = dividasNfse;
    }

    public List<ConfiguracaoNfseParametros> getParametros() {
        return parametros;
    }

    public ConfiguracaoNotaPremiada getConfiguracaoNotaPremiada() {
        return configuracaoNotaPremiada;
    }

    public void setConfiguracaoNotaPremiada(ConfiguracaoNotaPremiada configuracaoNotaPremiada) {
        this.configuracaoNotaPremiada = configuracaoNotaPremiada;
    }

    public void setParametros(List<ConfiguracaoNfseParametros> parametros) {
        this.parametros = parametros;
    }

    public Servico getPadraoServicoPrestado() {
        return padraoServicoPrestado;
    }

    public void setPadraoServicoPrestado(Servico padraoServicoPrestado) {
        this.padraoServicoPrestado = padraoServicoPrestado;
    }

    public boolean getParametroBoolean(TipoParametroNfse tipo) {
        String valor = getParametroString(tipo);
        return Strings.isNullOrEmpty(valor) ? false : Boolean.valueOf(valor);
    }

    public boolean hasParametro(TipoParametroNfse tipo) {
        return getParametroString(tipo) != null;
    }

    public String getParametroString(TipoParametroNfse tipo) {
        for (ConfiguracaoNfseParametros parametro : getParametros()) {
            if (tipo.equals(parametro.getTipoParametro())) {
                return parametro.getValor();
            }
        }
        return null;
    }


    public Integer getParametroInteger(TipoParametroNfse tipo) {
        for (ConfiguracaoNfseParametros parametro : getParametros()) {
            if (tipo.equals(parametro.getTipoParametro())) {
                return Integer.valueOf(parametro.getValor());
            }
        }
        return null;
    }

    public ConfiguracaoIssForaMunicipio getConfiguracaoIssForaMun() {
        return configuracaoIssForaMun;
    }

    public void setConfiguracaoIssForaMun(ConfiguracaoIssForaMunicipio configuracaoIssForaMun) {
        this.configuracaoIssForaMun = configuracaoIssForaMun;
    }

    public Boolean getHomologacao() {
        return homologacao == null ? Boolean.FALSE : homologacao;
    }

    public void setHomologacao(Boolean homologacao) {
        this.homologacao = homologacao;
    }

    public ConfiguracaoNfseDivida buscarConfiguracaoDivida(TipoMovimentoMensal tipoMovimentoMensal, TipoDeclaracaoMensalServico tipoDeclaracaoMensalServico) {
        if (dividasNfse == null || dividasNfse.isEmpty())
            return null;

        for (ConfiguracaoNfseDivida divida : dividasNfse) {
            if (divida.getTipoMovimentoMensal().equals(tipoMovimentoMensal)
                && divida.getTipoDeclaracaoMensalServico().equals(tipoDeclaracaoMensalServico)) {
                return divida;
            }
        }
        return null;
    }

    public ConfiguracaoDesif getConfiguracaoDesif() {
        return configuracaoDesif;
    }

    public void setConfiguracaoDesif(ConfiguracaoDesif configuracaoDesif) {
        this.configuracaoDesif = configuracaoDesif;
    }

    public ConfiguracaoNfseNacional getConfiguracaoNfseNacional() {
        return configuracaoNfseNacional;
    }

    public void setConfiguracaoNfseNacional(ConfiguracaoNfseNacional configuracaoNfseNacional) {
        this.configuracaoNfseNacional = configuracaoNfseNacional;
    }
}
