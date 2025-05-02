package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Mateus on 27/02/2015.
 */
public class DetalhamentoLiquidacaoItem {
    private BigDecimal quantidade;
    private String codigo;
    private String descricao;
    private BigDecimal valorNormal;
    private BigDecimal valorResto;
    private String parametro;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private Long idUnidade;
    private Long idOrgao;
    private Long idUnidadeGestora;
    private List<DetalhamentoLiquidacaoItem> itens;

    public DetalhamentoLiquidacaoItem() {
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
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

    public BigDecimal getValorNormal() {
        return valorNormal;
    }

    public void setValorNormal(BigDecimal valorNormal) {
        this.valorNormal = valorNormal;
    }

    public BigDecimal getValorResto() {
        return valorResto;
    }

    public void setValorResto(BigDecimal valorResto) {
        this.valorResto = valorResto;
    }

    public String getParametro() {
        return parametro;
    }

    public void setParametro(String parametro) {
        this.parametro = parametro;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public Long getIdOrgao() {
        return idOrgao;
    }

    public void setIdOrgao(Long idOrgao) {
        this.idOrgao = idOrgao;
    }

    public Long getIdUnidadeGestora() {
        return idUnidadeGestora;
    }

    public void setIdUnidadeGestora(Long idUnidadeGestora) {
        this.idUnidadeGestora = idUnidadeGestora;
    }


    public List<DetalhamentoLiquidacaoItem> getItens() {
        return itens;
    }

    public void setItens(List<DetalhamentoLiquidacaoItem> itens) {
        this.itens = itens;
    }
}
