package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;


public class TomadorServicoDTO implements Serializable, NfseDTO {

    private Long id;
    private PrestadorServicoNfseDTO prestador;
    private DadosPessoaisNfseDTO dadosPessoais;
    private Boolean ativo;

    public TomadorServicoDTO() {
        this.ativo = Boolean.TRUE;
    }

    public TomadorServicoDTO(Long id, PrestadorServicoNfseDTO prestador, DadosPessoaisNfseDTO dadosPessoais) {
        this();
        this.id = id;
        this.prestador = prestador;
        this.dadosPessoais = dadosPessoais;
    }

    public TomadorServicoDTO(Long id, String nomeRazaoSocial, String nomeFantasia, String cpfCnpj, String email, String apelido) {
        this();
        this.id = id;
        this.dadosPessoais = new DadosPessoaisNfseDTO(cpfCnpj, nomeRazaoSocial, nomeFantasia, email, apelido);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public DadosPessoaisNfseDTO getDadosPessoais() {
        return dadosPessoais;
    }

    public void setDadosPessoais(DadosPessoaisNfseDTO dadosPessoais) {
        this.dadosPessoais = dadosPessoais;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}
