package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class DemonstrativoReceitaNaturezaTipo {
    private Long id;
    private String codigo;
    private String descricao;
    private String tipoMovimento;
    private String tipoOperacao;
    private BigDecimal principal;
    private BigDecimal multaJurosMora;
    private BigDecimal dividaAtiva;
    private BigDecimal multaJurosMoraDividaAtiva;
    private BigDecimal nivel;
    private String orgao;
    private String unidade;
    private String unidadeGestora;
    private Long idOrgao;
    private Long idUnidade;
    private Long idUnidadeGestora;
    private String categoria;

    public DemonstrativoReceitaNaturezaTipo() {
        principal = BigDecimal.ZERO;
        multaJurosMora = BigDecimal.ZERO;
        dividaAtiva = BigDecimal.ZERO;
        multaJurosMoraDividaAtiva = BigDecimal.ZERO;
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

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public BigDecimal getPrincipal() {
        return principal;
    }

    public void setPrincipal(BigDecimal principal) {
        this.principal = principal;
    }

    public BigDecimal getMultaJurosMora() {
        return multaJurosMora;
    }

    public void setMultaJurosMora(BigDecimal multaJurosMora) {
        this.multaJurosMora = multaJurosMora;
    }

    public BigDecimal getDividaAtiva() {
        return dividaAtiva;
    }

    public void setDividaAtiva(BigDecimal dividaAtiva) {
        this.dividaAtiva = dividaAtiva;
    }

    public BigDecimal getMultaJurosMoraDividaAtiva() {
        return multaJurosMoraDividaAtiva;
    }

    public void setMultaJurosMoraDividaAtiva(BigDecimal multaJurosMoraDividaAtiva) {
        this.multaJurosMoraDividaAtiva = multaJurosMoraDividaAtiva;
    }

    public BigDecimal getNivel() {
        return nivel;
    }

    public void setNivel(BigDecimal nivel) {
        this.nivel = nivel;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Long getIdOrgao() {
        return idOrgao;
    }

    public void setIdOrgao(Long idOrgao) {
        this.idOrgao = idOrgao;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public Long getIdUnidadeGestora() {
        return idUnidadeGestora;
    }

    public void setIdUnidadeGestora(Long idUnidadeGestora) {
        this.idUnidadeGestora = idUnidadeGestora;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
