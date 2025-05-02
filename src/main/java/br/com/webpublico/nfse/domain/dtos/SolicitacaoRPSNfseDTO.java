package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoSolicitacaoRPSNfseDTO;

import java.util.Date;

public class SolicitacaoRPSNfseDTO implements NfseDTO {

    private Long id;
    private Date solicitadaEm;
    private Date analisadaEm;
    private int quantidadeSolicitada;
    private String observacaoSolicitacao;
    private String observacaoAnalise;
    private UserNfseDTO userEmpresa;
    private PrestadorServicoNfseDTO prestador;
    private PessoaNfseDTO userPrefeitura;
    private SituacaoSolicitacaoRPSNfseDTO situacao;


    public SolicitacaoRPSNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSolicitadaEm() {
        return solicitadaEm;
    }

    public void setSolicitadaEm(Date solicitadaEm) {
        this.solicitadaEm = solicitadaEm;
    }

    public Date getAnalisadaEm() {
        return analisadaEm;
    }

    public void setAnalisadaEm(Date analisadaEm) {
        this.analisadaEm = analisadaEm;
    }

    public int getQuantidadeSolicitada() {
        return quantidadeSolicitada;
    }

    public void setQuantidadeSolicitada(int quantidadeSolicitada) {
        this.quantidadeSolicitada = quantidadeSolicitada;
    }

    public String getObservacaoSolicitacao() {
        return observacaoSolicitacao;
    }

    public void setObservacaoSolicitacao(String observacaoSolicitacao) {
        this.observacaoSolicitacao = observacaoSolicitacao;
    }

    public String getObservacaoAnalise() {
        return observacaoAnalise;
    }

    public void setObservacaoAnalise(String observacaoAnalise) {
        this.observacaoAnalise = observacaoAnalise;
    }

    public UserNfseDTO getUserEmpresa() {
        return userEmpresa;
    }

    public void setUserEmpresa(UserNfseDTO userEmpresa) {
        this.userEmpresa = userEmpresa;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public PessoaNfseDTO getUserPrefeitura() {
        return userPrefeitura;
    }

    public void setUserPrefeitura(PessoaNfseDTO userPrefeitura) {
        this.userPrefeitura = userPrefeitura;
    }

    public SituacaoSolicitacaoRPSNfseDTO getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoSolicitacaoRPSNfseDTO situacao) {
        this.situacao = situacao;
    }
}
