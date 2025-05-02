package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

public class LinhaCadastroOficioMEI extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private CadastroOficioMEI cadastroOficioMEI;

    private Integer numeroLinha;
    private String codigoRegistro;

    private String razaoSocial;
    private String juntaComercial;
    private String nomeReduzido;
    private String nomeFantasia;
    private String cnpj;
    private String nire;
    private String inscricaoEstadual;
    private String nomeResponsavel;
    private String telefone;
    private String email;
    private String logradouro;
    private Integer numero;
    private String complemento;
    private String bairro;
    private String cep;
    private String codigoMunicipio;
    private String UF;
    private BigDecimal capitalSocial;
    private boolean MEI;

    private String naturezaJuridica;
    private String CNAES;
    private CNAE CNAEPrimaria;
    private String inscricaoCadastral;
    private CadastroEconomico cadastroEconomico;
    private List<CNAE> cnaesSecundarias;
    private String erro;
    private String erroComCNPJ;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroOficioMEI getCadastroOficioMEI() {
        return cadastroOficioMEI;
    }

    public void setCadastroOficioMEI(CadastroOficioMEI cadastroOficioMEI) {
        this.cadastroOficioMEI = cadastroOficioMEI;
    }

    public Integer getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(Integer numeroLinha) {
        this.numeroLinha = numeroLinha;
    }

    public String getCodigoRegistro() {
        return codigoRegistro;
    }

    public void setCodigoRegistro(String codigoRegistro) {
        this.codigoRegistro = codigoRegistro;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getJuntaComercial() {
        return juntaComercial;
    }

    public void setJuntaComercial(String juntaComercial) {
        this.juntaComercial = juntaComercial;
    }

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public void setNomeReduzido(String nomeReduzido) {
        this.nomeReduzido = nomeReduzido;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNire() {
        return nire;
    }

    public void setNire(String nire) {
        this.nire = nire;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getNomeResponsavel() {
        return nomeResponsavel;
    }

    public void setNomeResponsavel(String nomeResponsavel) {
        this.nomeResponsavel = nomeResponsavel;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
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

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public String getUF() {
        return UF;
    }

    public void setUF(String UF) {
        this.UF = UF;
    }

    public BigDecimal getCapitalSocial() {
        return capitalSocial;
    }

    public void setCapitalSocial(BigDecimal capitalSocial) {
        this.capitalSocial = capitalSocial;
    }

    public boolean isMEI() {
        return MEI;
    }

    public void setMEI(boolean MEI) {
        this.MEI = MEI;
    }

    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public String getCNAES() {
        return CNAES;
    }

    public void setCNAES(String CNAES) {
        this.CNAES = CNAES;
    }

    public CNAE getCNAEPrimaria() {
        return CNAEPrimaria;
    }

    public void setCNAEPrimaria(CNAE CNAEPrimaria) {
        this.CNAEPrimaria = CNAEPrimaria;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<CNAE> getCnaesSecundarias() {
        return cnaesSecundarias;
    }

    public void setCnaesSecundarias(List<CNAE> cnaesSecundarias) {
        this.cnaesSecundarias = cnaesSecundarias;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getErroComCNPJ() {
        return erroComCNPJ;
    }

    public void setErroComCNPJ(String erroComCNPJ) {
        this.erroComCNPJ = erroComCNPJ;
    }
}
