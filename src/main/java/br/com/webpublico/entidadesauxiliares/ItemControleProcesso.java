package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.enums.TipoProcesso;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ItemControleProcesso {

    private Long idItem;
    private Long idObjetoCompra;
    private Long numeroItem;
    private Long numeroLote;
    private ObjetoCompra objetoCompraProcesso;
    private String descricaoComplementarProcesso;
    private TipoControleLicitacao tipoControleOriginal;
    private TipoControleLicitacao tipoControle;
    private TipoProcesso tipoProcesso;
    private TipoMascaraUnidadeMedida mascaraQuantidade;
    private TipoMascaraUnidadeMedida mascaraValor;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private Boolean selecionado;
    private ObjetoCompra objetoCompraContrato;
    private String descricaoComplementarContrato;
    private List<ItemControleProcessoMovimento> movimentos;

    public ItemControleProcesso() {
         movimentos = Lists.newArrayList();
         selecionado = false;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public String getDescricaoComplementarContrato() {
        return descricaoComplementarContrato;
    }

    public void setDescricaoComplementarContrato(String descricaoComplementarContrato) {
        this.descricaoComplementarContrato = descricaoComplementarContrato;
    }

    public Long getIdObjetoCompra() {
        return idObjetoCompra;
    }

    public void setIdObjetoCompra(Long idObjetoCompra) {
        this.idObjetoCompra = idObjetoCompra;
    }

    public Long getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Long numeroItem) {
        this.numeroItem = numeroItem;
    }

    public Long getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(Long numeroLote) {
        this.numeroLote = numeroLote;
    }

    public ObjetoCompra getObjetoCompraProcesso() {
        return objetoCompraProcesso;
    }

    public void setObjetoCompraProcesso(ObjetoCompra objetoCompraProcesso) {
        this.objetoCompraProcesso = objetoCompraProcesso;
    }

    public String getDescricaoComplementarProcesso() {
        return descricaoComplementarProcesso;
    }

    public void setDescricaoComplementarProcesso(String descricaoComplementarProcesso) {
        this.descricaoComplementarProcesso = descricaoComplementarProcesso;
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

    public TipoProcesso getTipoProcesso() {
        return tipoProcesso;
    }

    public void setTipoProcesso(TipoProcesso tipoProcesso) {
        this.tipoProcesso = tipoProcesso;
    }

    public List<ItemControleProcessoMovimento> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<ItemControleProcessoMovimento> movimentos) {
        this.movimentos = movimentos;
    }

    public TipoMascaraUnidadeMedida getMascaraQuantidade() {
        return mascaraQuantidade;
    }

    public void setMascaraQuantidade(TipoMascaraUnidadeMedida mascaraQuantidade) {
        this.mascaraQuantidade = mascaraQuantidade;
    }

    public TipoMascaraUnidadeMedida getMascaraValor() {
        return mascaraValor;
    }

    public void setMascaraValor(TipoMascaraUnidadeMedida mascaraValor) {
        this.mascaraValor = mascaraValor;
    }

    public Boolean getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public ObjetoCompra getObjetoCompraContrato() {
        return objetoCompraContrato;
    }

    public void setObjetoCompraContrato(ObjetoCompra objetoCompraContrato) {
        this.objetoCompraContrato = objetoCompraContrato;
    }
}
