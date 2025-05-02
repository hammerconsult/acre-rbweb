package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import com.google.common.collect.ComparisonChain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

public class MovimentoAjusteProcessoCompraVO implements Comparable<MovimentoAjusteProcessoCompraVO> {

    private Date data;
    private Integer ano;
    private String numero;
    private String descricao;
    private Conta contaDesdobrada;
    private Long idItem;
    private Long idProcesso;
    private String descricaoProcesso;
    private TipoMovimentoProcessoLicitatorio tipoProcesso;
    private TipoControleLicitacao tipoControle;
    private TipoControleLicitacao tipoControleOriginal;
    private BigDecimal quantidade;
    private BigDecimal quantidadeOriginal;
    private BigDecimal valorUnitario;
    private BigDecimal valorUnitarioOriginal;
    private BigDecimal valorTotal;

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long id) {
        this.idItem = id;
    }

    public String getDescricaoProcesso() {
        return descricaoProcesso;
    }

    public void setDescricaoProcesso(String descricaoProcesso) {
        this.descricaoProcesso = descricaoProcesso;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getQuantidadeOriginal() {
        return quantidadeOriginal;
    }

    public void setQuantidadeOriginal(BigDecimal quantidadeOriginal) {
        this.quantidadeOriginal = quantidadeOriginal;
    }

    public BigDecimal getValorUnitarioOriginal() {
        return valorUnitarioOriginal;
    }

    public void setValorUnitarioOriginal(BigDecimal valorUnitarioOriginal) {
        this.valorUnitarioOriginal = valorUnitarioOriginal;
    }

    public TipoMovimentoProcessoLicitatorio getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoMovimentoProcessoLicitatorio tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public TipoControleLicitacao getTipoControleOriginal() {
        return tipoControleOriginal;
    }

    public void setTipoControleOriginal(TipoControleLicitacao tipoControleOriginal) {
        this.tipoControleOriginal = tipoControleOriginal;
    }

    public boolean hasValorTotal(MovimentoAjusteProcessoCompraVO item) {
        return item.getValorTotal().compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getValorTotalCalculado(BigDecimal quantidade, BigDecimal valorUnitario) {
        return quantidade.multiply(valorUnitario).setScale(2, RoundingMode.HALF_EVEN);
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Conta getContaDesdobrada() {
        return contaDesdobrada;
    }

    public void setContaDesdobrada(Conta contaDesdobrada) {
        this.contaDesdobrada = contaDesdobrada;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovimentoAjusteProcessoCompraVO that = (MovimentoAjusteProcessoCompraVO) o;
        return Objects.equals(idProcesso, that.idProcesso);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProcesso);
    }

    @Override
    public int compareTo(MovimentoAjusteProcessoCompraVO o) {
        if (o.getIdProcesso() !=null && getIdProcesso() !=null) {
            return ComparisonChain.start().compare(getIdProcesso(), o.getIdProcesso()).result();
        }
        return 0;
    }
}
