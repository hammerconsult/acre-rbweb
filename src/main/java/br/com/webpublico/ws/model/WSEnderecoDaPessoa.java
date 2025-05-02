package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.enums.TipoEndereco;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WSEnderecoDaPessoa implements Serializable {
    private Long id;
    private String cep;
    private String logradouro;
    private String complemento;
    private String bairro;
    private String localidade;
    private String uf;
    private String numero;
    private TipoEndereco tipoEndereco;
    private Boolean principal;

    public WSEnderecoDaPessoa() {
    }

    public WSEnderecoDaPessoa(EnderecoCorreio enderecoCorreio) {
        this.id = enderecoCorreio.getId();
        this.cep = enderecoCorreio.getCep();
        this.logradouro = enderecoCorreio.getLogradouro();
        this.complemento = enderecoCorreio.getComplemento();
        this.bairro = enderecoCorreio.getBairro();
        this.localidade = enderecoCorreio.getLocalidade();
        this.uf = enderecoCorreio.getUf();
        this.numero = enderecoCorreio.getNumero();
        this.tipoEndereco = enderecoCorreio.getTipoEndereco();
        this.principal = enderecoCorreio.getPrincipal();
    }

    @JsonIgnore
    public static void copyEnderecoPortalToEnderecoCorreio(EnderecoCorreio correio, WSEnderecoDaPessoa wsEndereco) {
        correio.setCep(wsEndereco.getCep());
        correio.setLogradouro(wsEndereco.getLogradouro());
        correio.setComplemento(wsEndereco.getComplemento());
        correio.setBairro(wsEndereco.getBairro());
        correio.setLocalidade(wsEndereco.getLocalidade());
        correio.setUf(wsEndereco.getUf());
        correio.setNumero(wsEndereco.getNumero());
        correio.setTipoEndereco(wsEndereco.getTipoEndereco());
        correio.setPrincipal(wsEndereco.getPrincipal());
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

}
