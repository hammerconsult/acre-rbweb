package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "atividade_principal",
    "data_situacao",
    "complemento",
    "nome",
    "uf",
    "telefone",
    "atividades_secundarias",
    "qsa",
    "situacao",
    "bairro",
    "logradouro",
    "numero",
    "cep",
    "municipio",
    "abertura",
    "natureza_juridica",
    "cnpj",
    "ultima_atualizacao",
    "status",
    "tipo",
    "fantasia",
    "email",
    "efr",
    "motivo_situacao",
    "situacao_especial",
    "data_situacao_especial",
    "capital_social",
    "extra"
})
public class ConsultaReceita implements Serializable {

    @JsonProperty("atividade_principal")
    @Etiqueta("Atividade Principal")
    private List<AtividadePrincipal> atividadePrincipal = null;
    @JsonProperty("data_situacao")
    @Etiqueta("Data da Situação")
    private String dataSituacao;
    @JsonProperty("complemento")
    @Etiqueta("Complemento")
    private String complemento;
    @JsonProperty("nome")
    @Etiqueta("Nome")
    private String nome;
    @JsonProperty("uf")
    @Etiqueta("UF")
    private String uf;
    @JsonProperty("telefone")
    @Etiqueta("Telefone")
    private String telefone;
    @JsonProperty("atividades_secundarias")
    @Etiqueta("Atividades Secundárias")
    private List<AtividadesSecundaria> atividadesSecundarias = null;
    @JsonProperty("qsa")
    @Etiqueta("Quadro Societário")
    private List<Qsa> qsa = null;
    @JsonProperty("situacao")
    @Etiqueta("Situação")
    private String situacao;
    @Etiqueta("Bairro")
    @JsonProperty("bairro")
    private String bairro;
    @JsonProperty("logradouro")
    @Etiqueta("Logradouro")
    private String logradouro;
    @JsonProperty("numero")
    @Etiqueta("Número (Correio)")
    private String numero;
    @JsonProperty("cep")
    @Etiqueta("CEP")
    private String cep;
    @JsonProperty("municipio")
    @Etiqueta("Município")
    private String municipio;
    @JsonProperty("abertura")
    @Etiqueta("Abertura")
    private String abertura;
    @JsonProperty("natureza_juridica")
    @Etiqueta("Natureza Juridica")
    private String naturezaJuridica;
    @JsonProperty("cnpj")
    @Etiqueta("CNPJ")
    private String cnpj;
    @JsonProperty("ultima_atualizacao")
    @Etiqueta("Última Atualização")
    private String ultimaAtualizacao;
    @Etiqueta("Status")
    @JsonProperty("status")
    private String status;
    @JsonProperty("tipo")
    @Etiqueta("Tipo")
    private String tipo;
    @JsonProperty("fantasia")
    @Etiqueta("Nome Fantasia")
    private String fantasia;
    @JsonProperty("email")
    @Etiqueta("e-mail")
    private String email;
    @JsonProperty("efr")
    @Etiqueta("ERF")
    private String efr;
    @JsonProperty("motivo_situacao")
    @Etiqueta("Motivo da Situação")
    private String motivoSituacao;
    @JsonProperty("situacao_especial")
    @Etiqueta("Situação Especial")
    private String situacaoEspecial;
    @JsonProperty("data_situacao_especial")
    @Etiqueta("Data da Situação Especial")
    private String dataSituacaoEspecial;
    @JsonProperty("capital_social")
    @Etiqueta("Capital Social")
    private String capitalSocial;
    @JsonProperty("extra")
    @Etiqueta("Info. Extra")
    private Extra extra;
    @JsonIgnore
    @Etiqueta("Adicionais")
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("atividade_principal")
    public List<AtividadePrincipal> getAtividadePrincipal() {
        return atividadePrincipal;
    }

    @JsonProperty("atividade_principal")
    public void setAtividadePrincipal(List<AtividadePrincipal> atividadePrincipal) {
        this.atividadePrincipal = atividadePrincipal;
    }

    @JsonProperty("data_situacao")
    public String getDataSituacao() {
        return dataSituacao;
    }

    @JsonProperty("data_situacao")
    public void setDataSituacao(String dataSituacao) {
        this.dataSituacao = dataSituacao;
    }

    @JsonProperty("complemento")
    public String getComplemento() {
        return complemento;
    }

    @JsonProperty("complemento")
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    @JsonProperty("nome")
    public String getNome() {
        return nome;
    }

    @JsonProperty("nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    @JsonProperty("uf")
    public String getUf() {
        return uf;
    }

    @JsonProperty("uf")
    public void setUf(String uf) {
        this.uf = uf;
    }

    @JsonProperty("telefone")
    public String getTelefone() {
        return telefone;
    }

    @JsonProperty("telefone")
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @JsonProperty("atividades_secundarias")
    public List<AtividadesSecundaria> getAtividadesSecundarias() {
        return atividadesSecundarias;
    }

    @JsonProperty("atividades_secundarias")
    public void setAtividadesSecundarias(List<AtividadesSecundaria> atividadesSecundarias) {
        this.atividadesSecundarias = atividadesSecundarias;
    }

    @JsonProperty("qsa")
    public List<Qsa> getQsa() {
        return qsa;
    }

    @JsonProperty("qsa")
    public void setQsa(List<Qsa> qsa) {
        this.qsa = qsa;
    }

    @JsonProperty("situacao")
    public String getSituacao() {
        return situacao;
    }

    @JsonProperty("situacao")
    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    @JsonProperty("bairro")
    public String getBairro() {
        return bairro;
    }

    @JsonProperty("bairro")
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    @JsonProperty("logradouro")
    public String getLogradouro() {
        return logradouro;
    }

    @JsonProperty("logradouro")
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    @JsonProperty("numero")
    public String getNumero() {
        return numero;
    }

    @JsonProperty("numero")
    public void setNumero(String numero) {
        this.numero = numero;
    }

    @JsonProperty("cep")
    public String getCep() {
        return cep;
    }

    @JsonProperty("cep")
    public void setCep(String cep) {
        this.cep = cep;
    }

    @JsonProperty("municipio")
    public String getMunicipio() {
        return municipio;
    }

    @JsonProperty("municipio")
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    @JsonProperty("abertura")
    public String getAbertura() {
        return abertura;
    }

    @JsonProperty("abertura")
    public void setAbertura(String abertura) {
        this.abertura = abertura;
    }

    @JsonProperty("natureza_juridica")
    public String getNaturezaJuridica() {
        return naturezaJuridica;
    }

    @JsonProperty("natureza_juridica")
    public void setNaturezaJuridica(String naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    @JsonProperty("cnpj")
    public String getCnpj() {
        return cnpj;
    }

    @JsonProperty("cnpj")
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    @JsonProperty("ultima_atualizacao")
    public String getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    @JsonProperty("ultima_atualizacao")
    public void setUltimaAtualizacao(String ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("tipo")
    public String getTipo() {
        return tipo;
    }

    @JsonProperty("tipo")
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @JsonProperty("fantasia")
    public String getFantasia() {
        return fantasia;
    }

    @JsonProperty("fantasia")
    public void setFantasia(String fantasia) {
        this.fantasia = fantasia;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("efr")
    public String getEfr() {
        return efr;
    }

    @JsonProperty("efr")
    public void setEfr(String efr) {
        this.efr = efr;
    }

    @JsonProperty("motivo_situacao")
    public String getMotivoSituacao() {
        return motivoSituacao;
    }

    @JsonProperty("motivo_situacao")
    public void setMotivoSituacao(String motivoSituacao) {
        this.motivoSituacao = motivoSituacao;
    }

    @JsonProperty("situacao_especial")
    public String getSituacaoEspecial() {
        return situacaoEspecial;
    }

    @JsonProperty("situacao_especial")
    public void setSituacaoEspecial(String situacaoEspecial) {
        this.situacaoEspecial = situacaoEspecial;
    }

    @JsonProperty("data_situacao_especial")
    public String getDataSituacaoEspecial() {
        return dataSituacaoEspecial;
    }

    @JsonProperty("data_situacao_especial")
    public void setDataSituacaoEspecial(String dataSituacaoEspecial) {
        this.dataSituacaoEspecial = dataSituacaoEspecial;
    }

    @JsonProperty("capital_social")
    public String getCapitalSocial() {
        return capitalSocial;
    }

    @JsonProperty("capital_social")
    public void setCapitalSocial(String capitalSocial) {
        this.capitalSocial = capitalSocial;
    }

    @JsonProperty("extra")
    public Extra getExtra() {
        return extra;
    }

    @JsonProperty("extra")
    public void setExtra(Extra extra) {
        this.extra = extra;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonIgnore
    public PrestadorServicoNfseDTO toPrestadorNfseDTO() {
        PrestadorServicoNfseDTO dto = new PrestadorServicoNfseDTO(null,
            "", new PessoaNfseDTO(null, new DadosPessoaisNfseDTO()));

        dto.getPessoa().getDadosPessoais().setNomeFantasia(this.fantasia);
        dto.getPessoa().getDadosPessoais().setNomeRazaoSocial(this.nome);
        dto.getPessoa().getDadosPessoais().setEmail(this.email);
        dto.getPessoa().getDadosPessoais().setCpfCnpj(this.cnpj);
        dto.getPessoa().getDadosPessoais().setTelefone(this.telefone);
        dto.getPessoa().getDadosPessoais().setBairro(this.bairro);
        dto.getPessoa().getDadosPessoais().setLogradouro(this.logradouro);
        dto.getPessoa().getDadosPessoais().setNumero(this.numero);
        dto.getPessoa().getDadosPessoais().setCep(this.cep);
        dto.getPessoa().getDadosPessoais().setMunicipio(new MunicipioNfseDTO(this.municipio, this.uf));

//        for (Servico servico : this.getServico()) {
//            dto.getServicos().add((ServicoNfseDTO) servico.toNfseDto());
//        }
//        for (EconomicoCNAE cnae : getEconomicoCNAE()) {
//            dto.getCnaes().add((CnaeNfseDTO) cnae.getCnae().toNfseDto());
//        }
//
//        dto.setEnquadramentoFiscal(new EnquadramentoFiscalNfseDTO());
//
//        EnquadramentoFiscal enquadramentoVigente = this.getEnquadramentoVigente();
//        if (enquadramentoVigente != null) {
//            if (enquadramentoVigente.getPorte() != null) {
//                dto.getEnquadramentoFiscal()
//                    .setPorte(EnquadramentoFiscalNfseEnumerations.TipoPorte.valueOf(enquadramentoVigente.getPorte().name()));
//            }
//            if (enquadramentoVigente.getTipoContribuinte() != null) {
//                dto.getEnquadramentoFiscal()
//                    .setTipoContribuinte(EnquadramentoFiscalNfseEnumerations.TipoContribuinte.valueOf(enquadramentoVigente.getTipoContribuinte().name()));
//            }
//            if (enquadramentoVigente.getRegimeTributario() != null) {
//                dto.getEnquadramentoFiscal()
//                    .setRegimeTributario(EnquadramentoFiscalNfseEnumerations.RegimeTributario.valueOf(enquadramentoVigente.getRegimeTributario().name()));
//            }
//            if (enquadramentoVigente.getTipoIssqn() != null) {
//                dto.getEnquadramentoFiscal()
//                    .setTipoIss(EnquadramentoFiscalNfseEnumerations.TipoIssqn.valueOf(enquadramentoVigente.getTipoIssqn().name()));
//            }
//            if (enquadramentoVigente.getTipoIssqn() != null) {
//                dto.getEnquadramentoFiscal()
//                    .setClassificacaoAtividade(EnquadramentoFiscalNfseEnumerations.ClassificacaoAtividade.valueOf(enquadramentoVigente.getClassificacaoAtividade().name()));
//            }
//            if (enquadramentoVigente.getTipoNotaFiscalServico() != null) {
//                dto.getEnquadramentoFiscal()
//                    .setTipoNotaFiscal(EnquadramentoFiscalNfseEnumerations.TipoNotaFiscalServico.valueOf(enquadramentoVigente.getTipoNotaFiscalServico().name()));
//            }
//            dto.getEnquadramentoFiscal().setValorIssEstimado(enquadramentoVigente.getIssEstimado());
//            dto.getEnquadramentoFiscal().setSubstitutoTributario(enquadramentoVigente.getSubstitutoTributario());
//        }
        return dto;
    }

}
