package br.com.webpublico.ws.model;

import br.com.webpublico.arquivo.dto.ArquivoDTO;
import br.com.webpublico.enums.CategoriaHabilitacao;
import br.com.webpublico.enums.Sexo;
import br.com.webpublico.enums.TipoEmpresa;
import br.com.webpublico.pessoa.dto.CnaeDTO;
import br.com.webpublico.pessoa.dto.ContaCorrenteBancariaDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Buzatto on 02/03/2017.
 */
public class WsSolicitacaoCadastro implements Serializable {

    private String cpf;
    private String nome;
    private String nomeTratamento;
    private Date dataNascimento;
    private String rg;
    protected String orgaoEmissao;
    protected WSUF ufRG;
    private Date emissaoRG;
    private Sexo sexo;
    private String email;
    private String homePage;
    private WSNivelEscolaridade nivelEsolaridade;
    private WSProfissao profissao;
    private String nomeMae;
    private String nomePai;
    private String telefoneResidencial;
    private String telefoneCelular;
    private String cnpj;
    private String razaoSocial;
    private String nomeFantasia;
    private String inscricaoEstadual;
    private TipoEmpresa tipoEmpresa;
    private String telefoneComercial;
    private String cei;
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String numero;
    private String numeroCnh;
    private Date validadeCnh;
    private CategoriaHabilitacao categoriaCnh;
    private List<CnaeDTO> cnaes;
    private List<WSPessoa> socios;
    private String pisPasep;
    private WSUF ufPisPasep;
    private List<ContaCorrenteBancariaDTO> contasBancarias;
    private Long idUnidadeOrganizacional;
    private List<ArquivoDTO> arquivosObrigatorios;
    private String key;
    private String mensagem;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeTratamento() {
        return nomeTratamento;
    }

    public void setNomeTratamento(String nomeTratamento) {
        this.nomeTratamento = nomeTratamento;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getOrgaoEmissao() {
        return orgaoEmissao;
    }

    public void setOrgaoEmissao(String orgaoEmissao) {
        this.orgaoEmissao = orgaoEmissao;
    }

    public WSUF getUfRG() {
        return ufRG;
    }

    public void setUfRG(WSUF ufRG) {
        this.ufRG = ufRG;
    }

    public Date getEmissaoRG() {
        return emissaoRG;
    }

    public void setEmissaoRG(Date emissaoRG) {
        this.emissaoRG = emissaoRG;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public WSNivelEscolaridade getNivelEsolaridade() {
        return nivelEsolaridade;
    }

    public void setNivelEsolaridade(WSNivelEscolaridade nivelEsolaridade) {
        this.nivelEsolaridade = nivelEsolaridade;
    }

    public WSProfissao getProfissao() {
        return profissao;
    }

    public void setProfissao(WSProfissao profissao) {
        this.profissao = profissao;
    }

    public String getNomeMae() {
        return nomeMae;
    }

    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }

    public String getNomePai() {
        return nomePai;
    }

    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }

    public String getTelefoneResidencial() {
        return telefoneResidencial;
    }

    public void setTelefoneResidencial(String telefoneResidencial) {
        this.telefoneResidencial = telefoneResidencial;
    }

    public String getTelefoneCelular() {
        return telefoneCelular;
    }

    public void setTelefoneCelular(String telefoneCelular) {
        this.telefoneCelular = telefoneCelular;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public TipoEmpresa getTipoEmpresa() {
        return tipoEmpresa;
    }

    public void setTipoEmpresa(TipoEmpresa tipoEmpresa) {
        this.tipoEmpresa = tipoEmpresa;
    }

    public String getTelefoneComercial() {
        return telefoneComercial;
    }

    public void setTelefoneComercial(String telefoneComercial) {
        this.telefoneComercial = telefoneComercial;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
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

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public boolean temCpf() {
        return cpf != null;
    }

    public boolean temEmail() {
        return email != null && !email.isEmpty();
    }

    public boolean hasNivelEsolaridade() {
        return nivelEsolaridade != null;
    }

    public boolean hasProfissao() {
        return profissao != null;
    }

    public boolean hasRg() {
        return rg != null && !rg.trim().isEmpty();
    }

    public boolean hasPisPasep() {
        return pisPasep != null && !pisPasep.trim().isEmpty();
    }

    public boolean hasTipoEmpresa() {
        return tipoEmpresa != null;
    }

    public String getNumeroCnh() {
        return numeroCnh;
    }

    public void setNumeroCnh(String numeroCnh) {
        this.numeroCnh = numeroCnh;
    }

    public Date getValidadeCnh() {
        return validadeCnh;
    }

    public void setValidadeCnh(Date validadeCnh) {
        this.validadeCnh = validadeCnh;
    }

    public CategoriaHabilitacao getCategoriaCnh() {
        return categoriaCnh;
    }

    public void setCategoriaCnh(CategoriaHabilitacao categoriaCnh) {
        this.categoriaCnh = categoriaCnh;
    }

    public List<CnaeDTO> getCnaes() {
        return cnaes;
    }

    public void setCnaes(List<CnaeDTO> cnaes) {
        this.cnaes = cnaes;
    }

    public List<WSPessoa> getSocios() {
        return socios;
    }

    public void setSocios(List<WSPessoa> socios) {
        this.socios = socios;
    }

    public List<ContaCorrenteBancariaDTO> getContasBancarias() {
        return contasBancarias;
    }

    public void setContasBancarias(List<ContaCorrenteBancariaDTO> contasBancarias) {
        this.contasBancarias = contasBancarias;
    }

    public Long getIdUnidadeOrganizacional() {
        return idUnidadeOrganizacional;
    }

    public void setIdUnidadeOrganizacional(Long idUnidadeOrganizacional) {
        this.idUnidadeOrganizacional = idUnidadeOrganizacional;
    }

    public List<ArquivoDTO> getArquivosObrigatorios() {
        return arquivosObrigatorios;
    }

    public void setArquivosObrigatorios(List<ArquivoDTO> arquivosObrigatorios) {
        this.arquivosObrigatorios = arquivosObrigatorios;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(String pisPasep) {
        this.pisPasep = pisPasep;
    }

    public WSUF getUfPisPasep() {
        return ufPisPasep;
    }

    public void setUfPisPasep(WSUF ufPisPasep) {
        this.ufPisPasep = ufPisPasep;
    }

    public String getCei() {
        return cei;
    }

    public void setCei(String cei) {
        this.cei = cei;
    }

    @Override
    public String toString() {
        return "WsSolicitacaoCadastro{" +
            "cpf='" + cpf + '\'' +
            ", nome='" + nome + '\'' +
            ", dataNascimento=" + dataNascimento +
            ", rg='" + rg + '\'' +
            ", orgaoEmissao='" + orgaoEmissao + '\'' +
            ", ufRG=" + ufRG +
            ", sexo=" + sexo +
            ", email='" + email + '\'' +
            ", homePage='" + homePage + '\'' +
            ", nivelEsolaridade=" + nivelEsolaridade +
            ", profissao=" + profissao +
            ", nomeMae='" + nomeMae + '\'' +
            ", nomePai='" + nomePai + '\'' +
            ", telefoneResidencial='" + telefoneResidencial + '\'' +
            ", telefoneCelular='" + telefoneCelular + '\'' +
            ", cnpj='" + cnpj + '\'' +
            ", razaoSocial='" + razaoSocial + '\'' +
            ", nomeFantasia='" + nomeFantasia + '\'' +
            ", inscricaoEstadual='" + inscricaoEstadual + '\'' +
            ", tipoEmpresa=" + tipoEmpresa +
            ", telefoneComercial='" + telefoneComercial + '\'' +
            ", cep='" + cep + '\'' +
            ", logradouro='" + logradouro + '\'' +
            ", complemento='" + complemento + '\'' +
            ", bairro='" + bairro + '\'' +
            ", localidade='" + localidade + '\'' +
            ", uf='" + uf + '\'' +
            ", numero='" + numero + '\'' +
            ", numeroCnh='" + numeroCnh + '\'' +
            ", validadeCnh=" + validadeCnh +
            ", categoriaCnh=" + categoriaCnh +
            '}';
    }
}
