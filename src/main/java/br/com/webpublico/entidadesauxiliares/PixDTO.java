package br.com.webpublico.entidadesauxiliares;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class PixDTO implements Serializable {

    private Long idDam;
    private Long numeroConvenio;
    private Long quantidadeSegundoExpiracao;
    private BigDecimal valorOriginalSolicitacao;
    private String cpfDevedor;
    private String cnpjDevedor;
    private String nomeDevedor;
    private String indicadorCodigoBarras;
    private String codigoGuiaRecebimento;
    private String codigoSolicitacaoBancoCentralBrasil;
    private String descricaoSolicitacaoPagamento;

    private String chaveAcesso;
    private String authorization;

    private Integer numeroVersaoSolicitacaoPagamento;
    private String timestampCriacaoSolicitacao;
    private String estadoSolicitacao;
    private String codigoConciliacaoSolicitante;
    private String linkQrCode;
    private String qrCode;

    private Date vencimento;

    @JsonProperty(value = "erros")
    private List<OcorrenciasPixDTO> ocorrencias;

    public PixDTO() {
        this.ocorrencias = Lists.newArrayList();
    }

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }

    public Long getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(Long numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public Long getQuantidadeSegundoExpiracao() {
        return quantidadeSegundoExpiracao;
    }

    public void setQuantidadeSegundoExpiracao(Long quantidadeSegundoExpiracao) {
        this.quantidadeSegundoExpiracao = quantidadeSegundoExpiracao;
    }

    public BigDecimal getValorOriginalSolicitacao() {
        return valorOriginalSolicitacao;
    }

    public void setValorOriginalSolicitacao(BigDecimal valorOriginalSolicitacao) {
        this.valorOriginalSolicitacao = valorOriginalSolicitacao;
    }

    public String getCpfDevedor() {
        return cpfDevedor;
    }

    public void setCpfDevedor(String cpfDevedor) {
        this.cpfDevedor = cpfDevedor;
    }

    public String getNomeDevedor() {
        return nomeDevedor;
    }

    public void setNomeDevedor(String nomeDevedor) {
        this.nomeDevedor = nomeDevedor;
    }

    public String getIndicadorCodigoBarras() {
        return indicadorCodigoBarras;
    }

    public void setIndicadorCodigoBarras(String indicadorCodigoBarras) {
        this.indicadorCodigoBarras = indicadorCodigoBarras;
    }

    public String getCodigoGuiaRecebimento() {
        return codigoGuiaRecebimento;
    }

    public void setCodigoGuiaRecebimento(String codigoGuiaRecebimento) {
        this.codigoGuiaRecebimento = codigoGuiaRecebimento;
    }

    public String getCodigoSolicitacaoBancoCentralBrasil() {
        return codigoSolicitacaoBancoCentralBrasil;
    }

    public void setCodigoSolicitacaoBancoCentralBrasil(String codigoSolicitacaoBancoCentralBrasil) {
        this.codigoSolicitacaoBancoCentralBrasil = codigoSolicitacaoBancoCentralBrasil;
    }

    public String getDescricaoSolicitacaoPagamento() {
        return descricaoSolicitacaoPagamento;
    }

    public void setDescricaoSolicitacaoPagamento(String descricaoSolicitacaoPagamento) {
        this.descricaoSolicitacaoPagamento = descricaoSolicitacaoPagamento;
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public Integer getNumeroVersaoSolicitacaoPagamento() {
        return numeroVersaoSolicitacaoPagamento;
    }

    public void setNumeroVersaoSolicitacaoPagamento(Integer numeroVersaoSolicitacaoPagamento) {
        this.numeroVersaoSolicitacaoPagamento = numeroVersaoSolicitacaoPagamento;
    }

    public String getTimestampCriacaoSolicitacao() {
        return timestampCriacaoSolicitacao;
    }

    public void setTimestampCriacaoSolicitacao(String timestampCriacaoSolicitacao) {
        this.timestampCriacaoSolicitacao = timestampCriacaoSolicitacao;
    }

    public String getEstadoSolicitacao() {
        return estadoSolicitacao;
    }

    public void setEstadoSolicitacao(String estadoSolicitacao) {
        this.estadoSolicitacao = estadoSolicitacao;
    }

    public String getCodigoConciliacaoSolicitante() {
        return codigoConciliacaoSolicitante;
    }

    public void setCodigoConciliacaoSolicitante(String codigoConciliacaoSolicitante) {
        this.codigoConciliacaoSolicitante = codigoConciliacaoSolicitante;
    }

    public String getLinkQrCode() {
        return linkQrCode;
    }

    public void setLinkQrCode(String linkQrCode) {
        this.linkQrCode = linkQrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public List<OcorrenciasPixDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<OcorrenciasPixDTO> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public String getCnpjDevedor() {
        return cnpjDevedor;
    }

    public void setCnpjDevedor(String cnpjDevedor) {
        this.cnpjDevedor = cnpjDevedor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PixDTO pixDTO = (PixDTO) o;
        return Objects.equal(idDam, pixDTO.idDam) && Objects.equal(codigoGuiaRecebimento, pixDTO.codigoGuiaRecebimento);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idDam, codigoGuiaRecebimento);
    }
}
