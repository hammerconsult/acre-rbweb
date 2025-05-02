package br.com.webpublico.entidadesauxiliares;

import com.beust.jcommander.internal.Lists;

import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class BoletimCadastroEconomico implements Serializable {
    private Long id;
    private Date dataAbertura;
    private String usuarioCadastro;
    private String tipoAutonomo;
    private String situacaoCadastral;
    private String nome;
    private String nomeReduzido;
    private String cpfCnpj;
    private String inscricaoEstadual;
    private String porte;
    private String tipoIssqn;
    private String classAtividade;
    private String tipoNaturezaJuridica;
    private String descNatureza;
    private String autonomo;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cep;
    private String mensagemDispensa;
    private BigDecimal areaUtilizacao;

    private String inscricaoMobiliaria;
    private String chaveAutenticidade;
    private String caracteristicasAdicionais;
    private InputStream qrCode;
    private Boolean dispensado;

    private List<BoletimCadastroEconomicoDados> dados;
    private List<BoletimCadastroEconomicoEndereco> enderecos;
    private List<BoletimCadastroEconomicoCadastro> cadastros;
    private List<BoletimCadastroEconomicoJuntaComercial> juntasComerciais;
    private List<BoletimCadastroEconomicoCNAE> cnaesPrimarios;
    private List<BoletimCadastroEconomicoCNAE> cnaesSecundarios;
    private List<BoletimCadastroEconomicoHorarios> horarios;
    private List<BoletimCadastroEconomicoCaracteristicasFunc> caracteristicasFunc;
    private List<BoletimCadastroEconomicoEnquadramentoFiscal> enquadramentosFiscais;

    public BoletimCadastroEconomico() {
        dados = Lists.newArrayList();
        enderecos = Lists.newArrayList();
        cadastros = Lists.newArrayList();
        juntasComerciais = Lists.newArrayList();
        cnaesPrimarios = Lists.newArrayList();
        cnaesSecundarios = Lists.newArrayList();
        horarios = Lists.newArrayList();
        caracteristicasFunc = Lists.newArrayList();
        enquadramentosFiscais = Lists.newArrayList();
        dispensado = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(Date dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public String getUsuarioCadastro() {
        return usuarioCadastro;
    }

    public void setUsuarioCadastro(String usuarioCadastro) {
        this.usuarioCadastro = usuarioCadastro;
    }

    public String getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(String tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public String getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(String situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getPorte() {
        return porte;
    }

    public void setPorte(String porte) {
        this.porte = porte;
    }

    public String getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(String tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public String getClassAtividade() {
        return classAtividade;
    }

    public void setClassAtividade(String classAtividade) {
        this.classAtividade = classAtividade;
    }

    public String getTipoNaturezaJuridica() {
        return tipoNaturezaJuridica;
    }

    public void setTipoNaturezaJuridica(String tipoNaturezaJuridica) {
        this.tipoNaturezaJuridica = tipoNaturezaJuridica;
    }

    public String getDescNatureza() {
        return descNatureza;
    }

    public void setDescNatureza(String descNatureza) {
        this.descNatureza = descNatureza;
    }

    public String getAutonomo() {
        return autonomo;
    }

    public void setAutonomo(String autonomo) {
        this.autonomo = autonomo;
    }

    public String getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(String tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getMensagemDispensa() {
        return mensagemDispensa;
    }

    public void setMensagemDispensa(String mensagemDispensa) {
        this.mensagemDispensa = mensagemDispensa;
    }

    public BigDecimal getAreaUtilizacao() {
        return areaUtilizacao;
    }

    public void setAreaUtilizacao(BigDecimal areaUtilizacao) {
        this.areaUtilizacao = areaUtilizacao;
    }

    public String getInscricaoMobiliaria() {
        return inscricaoMobiliaria;
    }

    public void setInscricaoMobiliaria(String inscricaoMobiliaria) {
        this.inscricaoMobiliaria = inscricaoMobiliaria;
    }

    public String getChaveAutenticidade() {
        return chaveAutenticidade;
    }

    public void setChaveAutenticidade(String chaveAutenticidade) {
        this.chaveAutenticidade = chaveAutenticidade;
    }

    public String getCaracteristicasAdicionais() {
        return caracteristicasAdicionais;
    }

    public void setCaracteristicasAdicionais(String caracteristicasAdicionais) {
        this.caracteristicasAdicionais = caracteristicasAdicionais;
    }

    public InputStream getQrCode() {
        return qrCode;
    }

    public void setQrCode(InputStream qrCode) {
        this.qrCode = qrCode;
    }

    public List<BoletimCadastroEconomicoDados> getDados() {
        return dados;
    }

    public void setDados(List<BoletimCadastroEconomicoDados> dados) {
        this.dados = dados;
    }

    public List<BoletimCadastroEconomicoEndereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<BoletimCadastroEconomicoEndereco> enderecos) {
        this.enderecos = enderecos;
    }

    public List<BoletimCadastroEconomicoCadastro> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<BoletimCadastroEconomicoCadastro> cadastros) {
        this.cadastros = cadastros;
    }

    public List<BoletimCadastroEconomicoJuntaComercial> getJuntasComerciais() {
        return juntasComerciais;
    }

    public void setJuntasComerciais(List<BoletimCadastroEconomicoJuntaComercial> juntasComerciais) {
        this.juntasComerciais = juntasComerciais;
    }

    public List<BoletimCadastroEconomicoCNAE> getCnaesPrimarios() {
        return cnaesPrimarios;
    }

    public void setCnaesPrimarios(List<BoletimCadastroEconomicoCNAE> cnaesPrimarios) {
        this.cnaesPrimarios = cnaesPrimarios;
    }

    public List<BoletimCadastroEconomicoCNAE> getCnaesSecundarios() {
        return cnaesSecundarios;
    }

    public void setCnaesSecundarios(List<BoletimCadastroEconomicoCNAE> cnaesSecundarios) {
        this.cnaesSecundarios = cnaesSecundarios;
    }

    public List<BoletimCadastroEconomicoHorarios> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<BoletimCadastroEconomicoHorarios> horarios) {
        this.horarios = horarios;
    }

    public List<BoletimCadastroEconomicoCaracteristicasFunc> getCaracteristicasFunc() {
        return caracteristicasFunc;
    }

    public void setCaracteristicasFunc(List<BoletimCadastroEconomicoCaracteristicasFunc> caracteristicasFunc) {
        this.caracteristicasFunc = caracteristicasFunc;
    }

    public List<BoletimCadastroEconomicoEnquadramentoFiscal> getEnquadramentosFiscais() {
        return enquadramentosFiscais;
    }

    public void setEnquadramentosFiscais(List<BoletimCadastroEconomicoEnquadramentoFiscal> enquadramentosFiscais) {
        this.enquadramentosFiscais = enquadramentosFiscais;
    }

    public Boolean getDispensado() {
        return dispensado;
    }

    public void setDispensado(Boolean dispensado) {
        this.dispensado = dispensado;
    }
}
