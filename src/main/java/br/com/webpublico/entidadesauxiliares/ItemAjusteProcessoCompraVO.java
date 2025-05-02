package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ItemAjusteProcessoCompraVO implements Comparable<ItemAjusteProcessoCompraVO> {

    private Long criadoEm;
    private Long idItem;
    private Boolean selecionado;
    private Long idProcesso;
    private Long numeroItem;
    private Long numeroLote;
    private ItemProcessoDeCompra itemProcessoCompra;
    private TipoControleLicitacao tipoControle;
    private TipoMascaraUnidadeMedida mascaraQuantidade;
    private TipoMascaraUnidadeMedida mascaraValor;
    private ObjetoCompra objetoCompra;
    private ObjetoCompra objetoCompraPara;
    private String especificacao;
    private String especificacaoPara;
    private UnidadeMedida unidadeMedidaDe;
    private UnidadeMedida unidadeMedidaPara;
    private BigDecimal quantidadeDe;
    private BigDecimal quantidadePara;
    private GrupoContaDespesa grupoContaDespesaDe;
    private GrupoContaDespesa grupoContaDespesaPara;
    private List<MovimentoAjusteProcessoCompraVO> movimentos;

    public ItemAjusteProcessoCompraVO() {
         movimentos = Lists.newArrayList();
         selecionado = false;
         criadoEm = System.nanoTime();
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public ItemProcessoDeCompra getItemProcessoCompra() {
        return itemProcessoCompra;
    }

    public void setItemProcessoCompra(ItemProcessoDeCompra itemProcessoCompra) {
        this.itemProcessoCompra = itemProcessoCompra;
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

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public List<MovimentoAjusteProcessoCompraVO> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<MovimentoAjusteProcessoCompraVO> movimentos) {
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

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getIdProcesso() {
        return idProcesso;
    }

    public void setIdProcesso(Long idProcesso) {
        this.idProcesso = idProcesso;
    }

    public ObjetoCompra getObjetoCompraPara() {
        return objetoCompraPara;
    }

    public void setObjetoCompraPara(ObjetoCompra objetoCompraPara) {
        this.objetoCompraPara = objetoCompraPara;
    }

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public String getEspecificacaoPara() {
        return especificacaoPara;
    }

    public void setEspecificacaoPara(String especificacaoPara) {
        this.especificacaoPara = especificacaoPara;
    }

    public UnidadeMedida getUnidadeMedidaDe() {
        return unidadeMedidaDe;
    }

    public void setUnidadeMedidaDe(UnidadeMedida unidadeMedidaDe) {
        this.unidadeMedidaDe = unidadeMedidaDe;
    }

    public BigDecimal getQuantidadeDe() {
        return quantidadeDe;
    }

    public void setQuantidadeDe(BigDecimal quantidadeDe) {
        this.quantidadeDe = quantidadeDe;
    }

    public UnidadeMedida getUnidadeMedidaPara() {
        return unidadeMedidaPara;
    }

    public void setUnidadeMedidaPara(UnidadeMedida unidadeMedidaPara) {
        this.unidadeMedidaPara = unidadeMedidaPara;
    }

    public BigDecimal getQuantidadePara() {
        return quantidadePara;
    }

    public void setQuantidadePara(BigDecimal quantidadePara) {
        this.quantidadePara = quantidadePara;
    }

    public GrupoContaDespesa getGrupoContaDespesaDe() {
        return grupoContaDespesaDe;
    }

    public void setGrupoContaDespesaDe(GrupoContaDespesa grupoContaDespesaDe) {
        this.grupoContaDespesaDe = grupoContaDespesaDe;
    }

    public GrupoContaDespesa getGrupoContaDespesaPara() {
        return grupoContaDespesaPara;
    }

    public void setGrupoContaDespesaPara(GrupoContaDespesa grupoContaDespesaPara) {
        this.grupoContaDespesaPara = grupoContaDespesaPara;
    }

    public boolean hasObjetoCompraPara() {
        return objetoCompraPara != null;
    }

    public boolean hasUnidadeMedidaPara() {
        return unidadeMedidaPara != null;
    }

    public boolean hasQuantidadePara() {
        return quantidadePara != null && quantidadePara.compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemAjusteProcessoCompraVO that = (ItemAjusteProcessoCompraVO) o;
        return Objects.equals(criadoEm, that.criadoEm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(criadoEm);
    }

    @Override
    public int compareTo(ItemAjusteProcessoCompraVO o) {
        try {
            return ComparisonChain.start().compare(getNumeroLote(), o.getNumeroLote()).compare(getNumeroItem(), o.getNumeroItem()).result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
