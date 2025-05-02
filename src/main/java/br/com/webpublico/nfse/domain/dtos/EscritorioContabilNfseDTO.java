package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;


public class EscritorioContabilNfseDTO implements Serializable, NfseDTO {

    private Long id;
    private Long codigo;
    private String crcEscritorio;
    private String crcContador;
    private DadosPessoaisNfseDTO dadosPessoais;
    private DadosPessoaisNfseDTO responsavel;

    public EscritorioContabilNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DadosPessoaisNfseDTO getDadosPessoais() {
        return dadosPessoais;
    }

    public void setDadosPessoais(DadosPessoaisNfseDTO dadosPessoais) {
        this.dadosPessoais = dadosPessoais;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }


    public String getCrcEscritorio() {
        return crcEscritorio;
    }

    public void setCrcEscritorio(String crcEscritorio) {
        this.crcEscritorio = crcEscritorio;
    }

    public String getCrcContador() {
        return crcContador;
    }

    public void setCrcContador(String crcContador) {
        this.crcContador = crcContador;
    }

    public DadosPessoaisNfseDTO getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(DadosPessoaisNfseDTO responsavel) {
        this.responsavel = responsavel;
    }
}
