package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.util.anotacoes.AtributoIntegracaoSit;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: jujuba
 * Date: 22/01/15
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class ProprietarioIntegracaoSit implements Serializable {
    @AtributoIntegracaoSit(nome = "proprietario_celular")
    private String proprietarioCelular;
    @AtributoIntegracaoSit(nome = "proprietario_cpf_cnpj")
    private String proprietarioCpfCnpj;
    @AtributoIntegracaoSit(nome = "proprietario_documento")
    private String proprietarioDocumento;
    @AtributoIntegracaoSit(nome = "proprietario_email")
    private String proprietarioEmail;
    @AtributoIntegracaoSit(nome = "proprietario_endereco_bairro")
    private String proprietarioEnderecoBairro;
    @AtributoIntegracaoSit(nome = "proprietario_endereco_cep")
    private String proprietarioEnderecoCep;
    @AtributoIntegracaoSit(nome = "proprietario_endereco_complemento")
    private String proprietarioEnderecoComplemento;
    @AtributoIntegracaoSit(nome = "proprietario_endereco_logradouro")
    private String proprietarioEnderecoLogradouro;
    @AtributoIntegracaoSit(nome = "proprietario_endereco_municipio")
    private String proprietarioEnderecoMunicipio;
    @AtributoIntegracaoSit(nome = "proprietario_endereco_numero")
    private String proprietarioEnderecoNumero;
    @AtributoIntegracaoSit(nome = "proprietario_endereco_uf")
    private String proprietarioEnderecoUf;
    @AtributoIntegracaoSit(nome = "proprietario_fax")
    private String proprietarioFax;
    @AtributoIntegracaoSit(nome = "proprietario_id")
    private Integer proprietarioId;
    @AtributoIntegracaoSit(nome = "proprietario_inscricao_estadual")
    private String proprietarioInscricaoEstadual;
    @AtributoIntegracaoSit(nome = "proprietario_inscricao_municipal")
    private String proprietarioInscricaoMunicipal;
    @AtributoIntegracaoSit(nome = "proprietario_nome")
    private String proprietarioNome;
    @AtributoIntegracaoSit(nome = "proprietario_nome_fantasia")
    private String proprietarioNomeFantasia;
    @AtributoIntegracaoSit(nome = "proprietario_orgao_expedidor")
    private String proprietarioOrgaoExpedidor;
    @AtributoIntegracaoSit(nome = "proprietario_telefone")
    private String proprietarioTelefone;
    @AtributoIntegracaoSit(nome = "proprietario_tipo")
    private String proprietarioTipo;
    @AtributoIntegracaoSit(nome = "proprietario_tipo_documento")
    private String proprietarioTipoDocumento;
    @AtributoIntegracaoSit(nome = "proprietario_proporcao")
    private Double proporcao;
    private AutonomaIntegracaoSit autonomaSit;
    private Pessoa pessoa;

    public ProprietarioIntegracaoSit() {
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Double getProporcao() {
        return proporcao;
    }

    public void setProporcao(Double proporcao) {
        this.proporcao = proporcao;
    }

    public AutonomaIntegracaoSit getAutonomaSit() {
        return autonomaSit;
    }

    public void setAutonomaSit(AutonomaIntegracaoSit autonomaSit) {
        this.autonomaSit = autonomaSit;
    }

    public String getProprietarioCelular() {
        return proprietarioCelular;
    }

    public void setProprietarioCelular(String proprietarioCelular) {
        this.proprietarioCelular = proprietarioCelular;
    }

    public String getProprietarioCpfCnpj() {
        return proprietarioCpfCnpj;
    }

    public void setProprietarioCpfCnpj(String proprietarioCpfCnpj) {
        this.proprietarioCpfCnpj = proprietarioCpfCnpj;
    }

    public String getProprietarioDocumento() {
        return proprietarioDocumento;
    }

    public void setProprietarioDocumento(String proprietarioDocumento) {
        this.proprietarioDocumento = proprietarioDocumento;
    }

    public String getProprietarioEmail() {
        return proprietarioEmail;
    }

    public void setProprietarioEmail(String proprietarioEmail) {
        this.proprietarioEmail = proprietarioEmail;
    }

    public String getProprietarioEnderecoBairro() {
        return proprietarioEnderecoBairro;
    }

    public void setProprietarioEnderecoBairro(String proprietarioEnderecoBairro) {
        this.proprietarioEnderecoBairro = proprietarioEnderecoBairro;
    }

    public String getProprietarioEnderecoCep() {
        return proprietarioEnderecoCep;
    }

    public void setProprietarioEnderecoCep(String proprietarioEnderecoCep) {
        this.proprietarioEnderecoCep = proprietarioEnderecoCep;
    }

    public String getProprietarioEnderecoComplemento() {
        return proprietarioEnderecoComplemento;
    }

    public void setProprietarioEnderecoComplemento(String proprietarioEnderecoComplemento) {
        this.proprietarioEnderecoComplemento = proprietarioEnderecoComplemento;
    }

    public String getProprietarioEnderecoLogradouro() {
        return proprietarioEnderecoLogradouro;
    }

    public void setProprietarioEnderecoLogradouro(String proprietarioEnderecoLogradouro) {
        this.proprietarioEnderecoLogradouro = proprietarioEnderecoLogradouro;
    }

    public String getProprietarioEnderecoMunicipio() {
        return proprietarioEnderecoMunicipio;
    }

    public void setProprietarioEnderecoMunicipio(String proprietarioEnderecoMunicipio) {
        this.proprietarioEnderecoMunicipio = proprietarioEnderecoMunicipio;
    }

    public String getProprietarioEnderecoNumero() {
        return proprietarioEnderecoNumero;
    }

    public void setProprietarioEnderecoNumero(String proprietarioEnderecoNumero) {
        this.proprietarioEnderecoNumero = proprietarioEnderecoNumero;
    }

    public String getProprietarioEnderecoUf() {
        return proprietarioEnderecoUf;
    }

    public void setProprietarioEnderecoUf(String proprietarioEnderecoUf) {
        this.proprietarioEnderecoUf = proprietarioEnderecoUf;
    }

    public String getProprietarioFax() {
        return proprietarioFax;
    }

    public void setProprietarioFax(String proprietarioFax) {
        this.proprietarioFax = proprietarioFax;
    }

    public Integer getProprietarioId() {
        return proprietarioId;
    }

    public void setProprietarioId(Integer proprietarioId) {
        this.proprietarioId = proprietarioId;
    }

    public String getProprietarioInscricaoEstadual() {
        return proprietarioInscricaoEstadual;
    }

    public void setProprietarioInscricaoEstadual(String proprietarioInscricaoEstadual) {
        this.proprietarioInscricaoEstadual = proprietarioInscricaoEstadual;
    }

    public String getProprietarioInscricaoMunicipal() {
        return proprietarioInscricaoMunicipal;
    }

    public void setProprietarioInscricaoMunicipal(String proprietarioInscricaoMunicipal) {
        this.proprietarioInscricaoMunicipal = proprietarioInscricaoMunicipal;
    }

    public String getProprietarioNome() {
        return proprietarioNome;
    }

    public void setProprietarioNome(String proprietarioNome) {
        this.proprietarioNome = proprietarioNome;
    }

    public String getProprietarioNomeFantasia() {
        return proprietarioNomeFantasia;
    }

    public void setProprietarioNomeFantasia(String proprietarioNomeFantasia) {
        this.proprietarioNomeFantasia = proprietarioNomeFantasia;
    }

    public String getProprietarioOrgaoExpedidor() {
        return proprietarioOrgaoExpedidor;
    }

    public void setProprietarioOrgaoExpedidor(String proprietarioOrgaoExpedidor) {
        this.proprietarioOrgaoExpedidor = proprietarioOrgaoExpedidor;
    }

    public String getProprietarioTelefone() {
        return proprietarioTelefone;
    }

    public void setProprietarioTelefone(String proprietarioTelefone) {
        this.proprietarioTelefone = proprietarioTelefone;
    }

    public String getProprietarioTipo() {
        return proprietarioTipo;
    }

    public void setProprietarioTipo(String proprietarioTipo) {
        this.proprietarioTipo = proprietarioTipo;
    }

    public String getProprietarioTipoDocumento() {
        return proprietarioTipoDocumento;
    }

    public void setProprietarioTipoDocumento(String proprietarioTipoDocumento) {
        this.proprietarioTipoDocumento = proprietarioTipoDocumento;
    }
}
