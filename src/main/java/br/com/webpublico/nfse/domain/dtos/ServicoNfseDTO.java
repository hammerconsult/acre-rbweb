package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.ExigibilidadeNfseDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicoNfseDTO implements Serializable, NfseDTO {

    private Long id;
    private String codigo;
    private String descricao;
    private BigDecimal aliquota;
    private BigDecimal aliquotaIBPT;
    private Boolean construcaoCivil;
    private Boolean permiteRecolhimentoFora;
    private Boolean permiteDeducao;
    private BigDecimal percentualDeducao;
    private ExigibilidadeNfseDTO exigibilidade;
    private Boolean exclusivoSimplesNacional;
    private Boolean vetadoLC1162003;
    private Boolean permiteExportacao;

    public ServicoNfseDTO() {
    }

    public ServicoNfseDTO(Long id) {
        this.id = id;
    }

    public ServicoNfseDTO(Long id, String codigo, String descricao, BigDecimal aliquota, BigDecimal aliquotaIBPT) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.aliquota = aliquota;
        this.aliquotaIBPT = aliquotaIBPT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getAliquotaIBPT() {
        return aliquotaIBPT;
    }

    public void setAliquotaIBPT(BigDecimal aliquotaIBPT) {
        this.aliquotaIBPT = aliquotaIBPT;
    }

    public Boolean getConstrucaoCivil() {
        return construcaoCivil;
    }

    public void setConstrucaoCivil(Boolean construcaoCivil) {
        this.construcaoCivil = construcaoCivil;
    }

    public Boolean getPermiteRecolhimentoFora() {
        return permiteRecolhimentoFora;
    }

    public void setPermiteRecolhimentoFora(Boolean permiteRecolhimentoFora) {
        this.permiteRecolhimentoFora = permiteRecolhimentoFora;
    }

    public Boolean getPermiteDeducao() {
        return permiteDeducao;
    }

    public void setPermiteDeducao(Boolean permiteDeducao) {
        this.permiteDeducao = permiteDeducao;
    }

    public BigDecimal getPercentualDeducao() {
        return percentualDeducao;
    }

    public void setPercentualDeducao(BigDecimal percentualDeducao) {
        this.percentualDeducao = percentualDeducao;
    }

    public ExigibilidadeNfseDTO getExigibilidade() {
        return exigibilidade;
    }

    public void setExigibilidade(ExigibilidadeNfseDTO exigibilidadeNfseDTO) {
        this.exigibilidade = exigibilidadeNfseDTO;
    }

    public Boolean getExclusivoSimplesNacional() {
        return exclusivoSimplesNacional;
    }

    public void setExclusivoSimplesNacional(Boolean exclusivoSimplesNacional) {
        this.exclusivoSimplesNacional = exclusivoSimplesNacional;
    }

    public Boolean getVetadoLC1162003() {
        return vetadoLC1162003;
    }

    public void setVetadoLC1162003(Boolean vetadoLC1162003) {
        this.vetadoLC1162003 = vetadoLC1162003;
    }

    public Boolean getPermiteExportacao() {
        return permiteExportacao;
    }

    public void setPermiteExportacao(Boolean permiteExportacao) {
        this.permiteExportacao = permiteExportacao;
    }

    @Override
    public String toString() {
        return "ServicoSearchDTO{" +
            "id=" + id +
            ", codigo='" + codigo + '\'' +
            ", descricao='" + descricao + '\'' +
            ", aliquota=" + aliquota +
            ", aliquotaIBPT=" + aliquotaIBPT +
            '}';
    }
}
