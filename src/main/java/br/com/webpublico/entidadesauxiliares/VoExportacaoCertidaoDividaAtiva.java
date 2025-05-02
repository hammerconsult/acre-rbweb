package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoCertidaoDA;

import java.io.Serializable;
import java.math.BigDecimal;

public class VoExportacaoCertidaoDividaAtiva implements Serializable {

    private Long id;
    private Integer exercicio;
    private Long numero;
    private String cpfCnpj;
    private String nomeRazaoSocial;
    private String inscricaoCadastral;
    private String situacao;
    private BigDecimal valorAtualizado;
    private Integer quantidadeParcelamentos;

    public VoExportacaoCertidaoDividaAtiva() {
        valorAtualizado = BigDecimal.ZERO;
        quantidadeParcelamentos = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getValorAtualizado() {
        return valorAtualizado;
    }

    public void setValorAtualizado(BigDecimal valorAtualizado) {
        this.valorAtualizado = valorAtualizado;
    }

    public Integer getQuantidadeParcelamentos() {
        return quantidadeParcelamentos;
    }

    public void setQuantidadeParcelamentos(Integer quantidadeParcelamentos) {
        this.quantidadeParcelamentos = quantidadeParcelamentos;
    }

    public static VoExportacaoCertidaoDividaAtiva fromObject(Object[] object) {
        VoExportacaoCertidaoDividaAtiva vo = new VoExportacaoCertidaoDividaAtiva();
        vo.setId(((Number) object[0]).longValue());
        vo.setExercicio(((Number) object[1]).intValue());
        vo.setNumero(((Number) object[2]).longValue());
        vo.setCpfCnpj((String) object[3]);
        vo.setNomeRazaoSocial((String) object[4]);
        vo.setInscricaoCadastral((String) object[5]);
        vo.setSituacao(SituacaoCertidaoDA.valueOf((String) object[6]).getDescricao());
        vo.setQuantidadeParcelamentos(object[7] != null ? ((Number) object[7]).intValue() : 0);
        return vo;
    }
}
