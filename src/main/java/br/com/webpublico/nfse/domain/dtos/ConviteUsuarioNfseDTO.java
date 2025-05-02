package br.com.webpublico.nfse.domain.dtos;

import java.util.Date;


public class ConviteUsuarioNfseDTO implements NfseDTO {

    private DadosPessoaisNfseDTO dadosPessoais;

    private PrestadorServicoNfseDTO prestadorServico;

    private Date convidadoEm;

    public ConviteUsuarioNfseDTO() {
    }

    public DadosPessoaisNfseDTO getDadosPessoais() {
        return dadosPessoais;
    }

    public void setDadosPessoais(DadosPessoaisNfseDTO dadosPessoais) {
        this.dadosPessoais = dadosPessoais;
    }

    public PrestadorServicoNfseDTO getPrestadorServico() {
        return prestadorServico;
    }

    public void setPrestadorServico(PrestadorServicoNfseDTO prestadorServico) {
        this.prestadorServico = prestadorServico;
    }

    public Date getConvidadoEm() {
        return convidadoEm;
    }

    public void setConvidadoEm(Date convidadoEm) {
        this.convidadoEm = convidadoEm;
    }
}
