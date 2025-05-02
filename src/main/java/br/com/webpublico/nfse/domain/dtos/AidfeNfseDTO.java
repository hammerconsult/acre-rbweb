package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoAidfeNfseDTO;

import java.util.Date;

public class AidfeNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private PrestadorServicoNfseDTO prestadorServicos;
    private UserNfseDTO usuarioEmpresa;
    private Integer numero;
    private Date solicitadoEm;
    private Integer quantidade;
    private Long numeroInicial;
    private Long numeroFinal;
    private String observacao;
    private SituacaoAidfeNfseDTO situacaoAIDFE;
    private Date deferidoEm;
    private Integer quantidadeDeferida;
    private String observacaoDeferimento;
    private PessoaNfseDTO usuarioPrefeitura;
    private Integer versao;

    public AidfeNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrestadorServicoNfseDTO getPrestadorServicos() {
        return prestadorServicos;
    }

    public void setPrestadorServicos(PrestadorServicoNfseDTO prestadorServicos) {
        this.prestadorServicos = prestadorServicos;
    }

    public UserNfseDTO getUsuarioEmpresa() {
        return usuarioEmpresa;
    }

    public void setUsuarioEmpresa(UserNfseDTO usuarioEmpresa) {
        this.usuarioEmpresa = usuarioEmpresa;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getSolicitadoEm() {
        return solicitadoEm;
    }

    public void setSolicitadoEm(Date solicitadoEm) {
        this.solicitadoEm = solicitadoEm;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Long getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(Long numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public Long getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(Long numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public SituacaoAidfeNfseDTO getSituacaoAIDFE() {
        return situacaoAIDFE;
    }

    public void setSituacaoAIDFE(SituacaoAidfeNfseDTO situacaoAIDFE) {
        this.situacaoAIDFE = situacaoAIDFE;
    }

    public Date getDeferidoEm() {
        return deferidoEm;
    }

    public void setDeferidoEm(Date deferidoEm) {
        this.deferidoEm = deferidoEm;
    }

    public Integer getQuantidadeDeferida() {
        return quantidadeDeferida;
    }

    public void setQuantidadeDeferida(Integer quantidadeDeferida) {
        this.quantidadeDeferida = quantidadeDeferida;
    }

    public String getObservacaoDeferimento() {
        return observacaoDeferimento;
    }

    public void setObservacaoDeferimento(String observacaoDeferimento) {
        this.observacaoDeferimento = observacaoDeferimento;
    }

    public PessoaNfseDTO getUsuarioPrefeitura() {
        return usuarioPrefeitura;
    }

    public void setUsuarioPrefeitura(PessoaNfseDTO usuarioPrefeitura) {
        this.usuarioPrefeitura = usuarioPrefeitura;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }


}
