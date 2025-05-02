package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.util.anotacoes.AtributoIntegracaoSit;


public class CompromissarioIntegracaoSit {

    @AtributoIntegracaoSit(nome = "compromissario_celular")
    private String compromissarioCelular;
    @AtributoIntegracaoSit(nome = "compromissario_cpf_cnpj")
    private String compromissarioCpfCnpj;
    @AtributoIntegracaoSit(nome = "compromissario_documento")
    private String compromissarioDocumento;
    @AtributoIntegracaoSit(nome = "compromissario_email")
    private String compromissarioEmail;
    @AtributoIntegracaoSit(nome = "compromissario_endereco_bairro")
    private String compromissarioEnderecoBairro;
    @AtributoIntegracaoSit(nome = "compromissario_endereco_cep")
    private String compromissarioEnderecoCep;
    @AtributoIntegracaoSit(nome = "compromissario_endereco_complemento")
    private String compromissarioEnderecoComplemento;
    @AtributoIntegracaoSit(nome = "compromissario_endereco_logradouro")
    private String compromissarioEnderecoLogradouro;
    @AtributoIntegracaoSit(nome = "compromissario_endereco_municipio")
    private String compromissarioEnderecoMunicipio;
    @AtributoIntegracaoSit(nome = "compromissario_endereco_numero")
    private String compromissarioEnderecoNumero;
    @AtributoIntegracaoSit(nome = "compromissario_endereco_uf")
    private String compromissarioEnderecoUf;
    @AtributoIntegracaoSit(nome = "compromissario_fax")
    private String compromissarioFax;
    @AtributoIntegracaoSit(nome = "compromissario_id")
    private Integer compromissarioId;
    @AtributoIntegracaoSit(nome = "compromissario_inscricao_estadual")
    private String compromissarioInscricaoEstadual;
    @AtributoIntegracaoSit(nome = "compromissario_inscricao_municipal")
    private String compromissarioInscricaoMunicipal;
    @AtributoIntegracaoSit(nome = "compromissario_nome")
    private String compromissarioNome;
    @AtributoIntegracaoSit(nome = "compromissario_nome_fantasia")
    private String compromissarioNomeFantasia;
    @AtributoIntegracaoSit(nome = "compromissario_orgao_expedidor")
    private String compromissarioOrgaoExpedidor;
    @AtributoIntegracaoSit(nome = "compromissario_telefone")
    private String compromissarioTelefone;
    @AtributoIntegracaoSit(nome = "compromissario_tipo")
    private String compromissarioTipo;
    @AtributoIntegracaoSit(nome = "compromissario_tipo_documento")
    private String compromissarioTipoDocumento;
    @AtributoIntegracaoSit(nome = "compromissario_proporcao")
    private Double compromissarioProprocao;
    private AutonomaIntegracaoSit autonomaSit;
    private Pessoa pessoa;
    public String getCompromissarioCelular() {
        return compromissarioCelular;
    }

    public void setCompromissarioCelular(String compromissarioCelular) {
        this.compromissarioCelular = compromissarioCelular;
    }

    public String getCompromissarioCpfCnpj() {
        return compromissarioCpfCnpj;
    }

    public void setCompromissarioCpfCnpj(String compromissarioCpfCnpj) {
        this.compromissarioCpfCnpj = compromissarioCpfCnpj;
    }

    public String getCompromissarioDocumento() {
        return compromissarioDocumento;
    }

    public void setCompromissarioDocumento(String compromissarioDocumento) {
        this.compromissarioDocumento = compromissarioDocumento;
    }

    public String getCompromissarioEmail() {
        return compromissarioEmail;
    }

    public void setCompromissarioEmail(String compromissarioEmail) {
        this.compromissarioEmail = compromissarioEmail;
    }

    public String getCompromissarioEnderecoBairro() {
        return compromissarioEnderecoBairro;
    }

    public void setCompromissarioEnderecoBairro(String compromissarioEnderecoBairro) {
        this.compromissarioEnderecoBairro = compromissarioEnderecoBairro;
    }

    public String getCompromissarioEnderecoCep() {
        return compromissarioEnderecoCep;
    }

    public void setCompromissarioEnderecoCep(String compromissarioEnderecoCep) {
        this.compromissarioEnderecoCep = compromissarioEnderecoCep;
    }

    public String getCompromissarioEnderecoComplemento() {
        return compromissarioEnderecoComplemento;
    }

    public void setCompromissarioEnderecoComplemento(String compromissarioEnderecoComplemento) {
        this.compromissarioEnderecoComplemento = compromissarioEnderecoComplemento;
    }

    public String getCompromissarioEnderecoLogradouro() {
        return compromissarioEnderecoLogradouro;
    }

    public void setCompromissarioEnderecoLogradouro(String compromissarioEnderecoLogradouro) {
        this.compromissarioEnderecoLogradouro = compromissarioEnderecoLogradouro;
    }

    public String getCompromissarioEnderecoMunicipio() {
        return compromissarioEnderecoMunicipio;
    }

    public void setCompromissarioEnderecoMunicipio(String compromissarioEnderecoMunicipio) {
        this.compromissarioEnderecoMunicipio = compromissarioEnderecoMunicipio;
    }

    public String getCompromissarioEnderecoNumero() {
        return compromissarioEnderecoNumero;
    }

    public void setCompromissarioEnderecoNumero(String compromissarioEnderecoNumero) {
        this.compromissarioEnderecoNumero = compromissarioEnderecoNumero;
    }

    public String getCompromissarioEnderecoUf() {
        return compromissarioEnderecoUf;
    }

    public void setCompromissarioEnderecoUf(String compromissarioEnderecoUf) {
        this.compromissarioEnderecoUf = compromissarioEnderecoUf;
    }

    public String getCompromissarioFax() {
        return compromissarioFax;
    }

    public void setCompromissarioFax(String compromissarioFax) {
        this.compromissarioFax = compromissarioFax;
    }

    public Integer getCompromissarioId() {
        return compromissarioId;
    }

    public void setCompromissarioId(Integer compromissarioId) {
        this.compromissarioId = compromissarioId;
    }

    public String getCompromissarioInscricaoEstadual() {
        return compromissarioInscricaoEstadual;
    }

    public void setCompromissarioInscricaoEstadual(String compromissarioInscricaoEstadual) {
        this.compromissarioInscricaoEstadual = compromissarioInscricaoEstadual;
    }

    public String getCompromissarioInscricaoMunicipal() {
        return compromissarioInscricaoMunicipal;
    }

    public void setCompromissarioInscricaoMunicipal(String compromissarioInscricaoMunicipal) {
        this.compromissarioInscricaoMunicipal = compromissarioInscricaoMunicipal;
    }

    public String getCompromissarioNome() {
        return compromissarioNome;
    }

    public void setCompromissarioNome(String compromissarioNome) {
        this.compromissarioNome = compromissarioNome;
    }

    public String getCompromissarioNomeFantasia() {
        return compromissarioNomeFantasia;
    }

    public void setCompromissarioNomeFantasia(String compromissarioNomeFantasia) {
        this.compromissarioNomeFantasia = compromissarioNomeFantasia;
    }

    public String getCompromissarioOrgaoExpedidor() {
        return compromissarioOrgaoExpedidor;
    }

    public void setCompromissarioOrgaoExpedidor(String compromissarioOrgaoExpedidor) {
        this.compromissarioOrgaoExpedidor = compromissarioOrgaoExpedidor;
    }

    public String getCompromissarioTelefone() {
        return compromissarioTelefone;
    }

    public void setCompromissarioTelefone(String compromissarioTelefone) {
        this.compromissarioTelefone = compromissarioTelefone;
    }

    public String getCompromissarioTipo() {
        return compromissarioTipo;
    }

    public void setCompromissarioTipo(String compromissarioTipo) {
        this.compromissarioTipo = compromissarioTipo;
    }

    public String getCompromissarioTipoDocumento() {
        return compromissarioTipoDocumento;
    }

    public void setCompromissarioTipoDocumento(String compromissarioTipoDocumento) {
        this.compromissarioTipoDocumento = compromissarioTipoDocumento;
    }

    public AutonomaIntegracaoSit getAutonomaSit() {
        return autonomaSit;
    }

    public void setAutonomaSit(AutonomaIntegracaoSit autonomaSit) {
        this.autonomaSit = autonomaSit;
    }

    public Double getCompromissarioProprocao() {
        return compromissarioProprocao;
    }

    public void setCompromissarioProprocao(Double compromissarioProprocao) {
        this.compromissarioProprocao = compromissarioProprocao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
}
