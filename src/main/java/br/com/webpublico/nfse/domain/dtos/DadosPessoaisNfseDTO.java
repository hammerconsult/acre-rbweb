package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.tributario.enumeration.RegimeTributarioNfseDTO;
import br.com.webpublico.webreportdto.dto.tributario.TipoIssqnNfseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Strings;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DadosPessoaisNfseDTO implements NfseDTO, Cloneable {

    private Long idPessoa;
    private Long id;
    private String apelido;
    private String cpfCnpj;
    private String inscricaoEstadualRg;
    private String nomeRazaoSocial;
    private String nomeFantasia;
    private String email;
    private String site;
    private String telefone;
    private String celular;
    private String cep;
    private String numero;
    private String logradouro;
    private String bairro;
    private String complemento;
    private String numeroIdentificacao;
    private MunicipioNfseDTO municipio;
    private PaisNfseDTO pais;
    private String inscricaoMunicipal;
    private TipoIssqnNfseDTO tipoIssqn;
    private RegimeTributarioNfseDTO regimeTributario;

    public DadosPessoaisNfseDTO() {
    }

    public DadosPessoaisNfseDTO(String cpfCnpj, String nomeRazaoSocial, String nomeFantasia, String email, String apelido) {
        this.setCpfCnpj(cpfCnpj);
        this.setNomeRazaoSocial(nomeRazaoSocial);
        this.setNomeFantasia(nomeFantasia);
        this.setEmail(email);
        this.setApelido(apelido);
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        if (!Strings.isNullOrEmpty(cpfCnpj)) {
            this.cpfCnpj = cpfCnpj.replaceAll("[^a-zA-Z0-9]+", "");
        }
    }

    public String getInscricaoEstadualRg() {
        return inscricaoEstadualRg;
    }

    public void setInscricaoEstadualRg(String inscricaoEstadualRg) {
        this.inscricaoEstadualRg = inscricaoEstadualRg;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public MunicipioNfseDTO getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioNfseDTO municipio) {
        this.municipio = municipio;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    @JsonIgnore
    public boolean isFisica() {
        return cpfCnpj != null && cpfCnpj.length() == 11;
    }

    @JsonIgnore
    public boolean isJuridica() {
        return cpfCnpj != null && cpfCnpj.length() == 14;
    }

    public PaisNfseDTO getPais() {
        return pais;
    }

    public void setPais(PaisNfseDTO pais) {
        this.pais = pais;
    }

    public String getNumeroIdentificacao() {
        return numeroIdentificacao;
    }

    public void setNumeroIdentificacao(String numeroIdentificacao) {
        this.numeroIdentificacao = numeroIdentificacao;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public TipoIssqnNfseDTO getTipoIssqn() {
        return tipoIssqn;
    }

    public void setTipoIssqn(TipoIssqnNfseDTO tipoIssqn) {
        this.tipoIssqn = tipoIssqn;
    }

    public RegimeTributarioNfseDTO getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributarioNfseDTO regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    @Override
    public DadosPessoaisNfseDTO clone() throws CloneNotSupportedException {
        DadosPessoaisNfseDTO cloneable = new DadosPessoaisNfseDTO();
        cloneable.cpfCnpj = this.cpfCnpj;
        cloneable.idPessoa = this.idPessoa;
        cloneable.id = this.id;
        cloneable.inscricaoEstadualRg = this.inscricaoEstadualRg;
        cloneable.nomeRazaoSocial = this.nomeRazaoSocial;
        cloneable.nomeFantasia = this.nomeFantasia;
        cloneable.email = this.email;
        cloneable.telefone = this.telefone;
        cloneable.celular = this.celular;
        cloneable.cep = this.cep;
        cloneable.numero = this.numero;
        cloneable.logradouro = this.logradouro;
        cloneable.bairro = this.bairro;
        cloneable.complemento = this.complemento;
        cloneable.municipio = this.municipio;
        cloneable.apelido = this.apelido;
        cloneable.pais = this.pais;
        cloneable.numeroIdentificacao = this.numeroIdentificacao;
        cloneable.tipoIssqn = this.tipoIssqn;
        cloneable.regimeTributario = this.regimeTributario;
        return cloneable;
    }

    @Override
    public String toString() {
        return "DadosPessoaisNfseDTO{" +
            "idPessoa=" + idPessoa +
            ", id=" + id +
            ", cpfCnpj='" + cpfCnpj + '\'' +
            ", inscricaoEstadualRg='" + inscricaoEstadualRg + '\'' +
            ", nomeRazaoSocial='" + nomeRazaoSocial + '\'' +
            ", nomeFantasia='" + nomeFantasia + '\'' +
            ", email='" + email + '\'' +
            ", telefone='" + telefone + '\'' +
            ", celular='" + celular + '\'' +
            ", cep='" + cep + '\'' +
            ", numero='" + numero + '\'' +
            ", logradouro='" + logradouro + '\'' +
            ", bairro='" + bairro + '\'' +
            ", complemento='" + complemento + '\'' +
            ", municipio=" + municipio +
            ", pais=" + pais +
            ", numeroIdentificacao=" + numeroIdentificacao +
            ", apelido=" + apelido +
            '}';
    }
}
