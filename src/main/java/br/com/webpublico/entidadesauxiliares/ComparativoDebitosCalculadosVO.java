package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ComparativoDebitosCalculadosVO {

    private String tipoCadastro;
    private String contribuinte;
    private String cadastro;
    private Integer unidade;
    private BigDecimal valorAnterior;
    private BigDecimal valorAtual;
    private BigDecimal diferenca;
    private BigDecimal percentagem;
    private Long idCalculoAnterior;
    private Long idValorDividaAnterior;
    private Long idCalculoAtual;
    private Long idValorDividaAtual;
    private List<ItemComparativoDebitosCalculadosVO> itens;

    public ComparativoDebitosCalculadosVO() {
        this.valorAnterior = BigDecimal.ZERO;
        this.valorAtual = BigDecimal.ZERO;
        this.itens = Lists.newArrayList();
    }

    public void setValor(BigDecimal valor, boolean atual) {
        if (atual)
            this.valorAtual = valor;
        else
            this.valorAnterior = valor;
    }

    public BigDecimal getValor(boolean atual) {
        if (atual)
            return valorAtual;
        return valorAnterior;
    }

    public void setIdCalculo(Long idCalculo, boolean atual) {
        if (atual)
            this.idCalculoAtual = idCalculo;
        else
            this.idCalculoAnterior = idCalculo;
    }

    public Long getIdCalculo(boolean atual) {
        if (atual)
            return idCalculoAtual;
        return idCalculoAnterior;
    }

    public Long getIdValorDividaAnterior() {
        return idValorDividaAnterior;
    }

    public void setIdValorDividaAnterior(Long idValorDividaAnterior) {
        this.idValorDividaAnterior = idValorDividaAnterior;
    }

    public Long getIdValorDividaAtual() {
        return idValorDividaAtual;
    }

    public void setIdValorDividaAtual(Long idValorDividaAtual) {
        this.idValorDividaAtual = idValorDividaAtual;
    }

    public String getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(String tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(String contribuinte) {
        this.contribuinte = contribuinte;
    }

    public String getCadastro() {
        return cadastro;
    }

    public void setCadastro(String cadastro) {
        this.cadastro = cadastro;
    }

    public Integer getUnidade() {
        return unidade;
    }

    public void setUnidade(Integer unidade) {
        this.unidade = unidade;
    }

    public BigDecimal getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(BigDecimal valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public BigDecimal getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(BigDecimal valorAtual) {
        this.valorAtual = valorAtual;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public BigDecimal getPercentagem() {
        return percentagem;
    }

    public void setPercentagem(BigDecimal percentagem) {
        this.percentagem = percentagem;
    }

    public Long getIdCalculoAnterior() {
        return idCalculoAnterior;
    }

    public void setIdCalculoAnterior(Long idCalculoAnterior) {
        this.idCalculoAnterior = idCalculoAnterior;
    }

    public Long getIdCalculoAtual() {
        return idCalculoAtual;
    }

    public void setIdCalculoAtual(Long idCalculoAtual) {
        this.idCalculoAtual = idCalculoAtual;
    }

    public List<ItemComparativoDebitosCalculadosVO> getItens() {
        return itens;
    }

    public void setItens(List<ItemComparativoDebitosCalculadosVO> itens) {
        this.itens = itens;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ComparativoDebitosCalculadosVO other = (ComparativoDebitosCalculadosVO) obj;
        return cadastro.equals(other.getCadastro()) && String.valueOf(unidade).equals(String.valueOf(other.getUnidade()));
    }

    public void setIdValorDivida(Long idValorDivida, Boolean isAtual) {
        if (isAtual)
            setIdValorDividaAtual(idValorDivida);
        else
            setIdValorDividaAnterior(idValorDivida);
    }
}
