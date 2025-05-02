package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class SubstituicaoObjetoCompraVo implements Comparable<SubstituicaoObjetoCompraVo> {

    private Long idItem;
    private Long idMovimento;
    private Long numeroItem;
    private Long numeroLote;
    private ObjetoCompra objetoCompraDe;
    private ObjetoCompra objetoCompraPara;
    private String especificacaoDe;
    private String especificacaoPara;
    private UnidadeMedida unidadeMedidaDe;
    private BigDecimal quantidadeDe;
    private UnidadeMedida unidadeMedidaPara;
    private BigDecimal quantidadePara;
    private GrupoContaDespesa grupoContaDespesaDe;
    private GrupoContaDespesa grupoContaDespesaPara;
    private List<SubstituicaoObjetoCompraMovimento> movimentos;

    public SubstituicaoObjetoCompraVo() {
        movimentos = Lists.newArrayList();
    }

    public Long getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Long idMovimento) {
        this.idMovimento = idMovimento;
    }

    public String getEspecificacaoDe() {
        return especificacaoDe;
    }

    public void setEspecificacaoDe(String especificacaoDe) {
        this.especificacaoDe = especificacaoDe;
    }

    public String getEspecificacaoPara() {
        return especificacaoPara;
    }

    public void setEspecificacaoPara(String especificacaoPara) {
        this.especificacaoPara = especificacaoPara;
    }

    public ObjetoCompra getObjetoCompraDe() {
        return objetoCompraDe;
    }

    public void setObjetoCompraDe(ObjetoCompra objetoCompraDe) {
        this.objetoCompraDe = objetoCompraDe;
    }

    public ObjetoCompra getObjetoCompraPara() {
        return objetoCompraPara;
    }

    public void setObjetoCompraPara(ObjetoCompra objetoCompraPara) {
        this.objetoCompraPara = objetoCompraPara;
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

    public List<SubstituicaoObjetoCompraMovimento> getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(List<SubstituicaoObjetoCompraMovimento> movimentos) {
        this.movimentos = movimentos;
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

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
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

    public TipoMascaraUnidadeMedida getTipoMascaraUnidadeMedida() {
        try {
            return unidadeMedidaPara.getMascaraQuantidade();
        } catch (Exception e) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubstituicaoObjetoCompraVo that = (SubstituicaoObjetoCompraVo) o;
        return Objects.equals(numeroItem, that.numeroItem) && Objects.equals(objetoCompraDe, that.objetoCompraDe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroItem, objetoCompraDe);
    }

    @Override
    public int compareTo(SubstituicaoObjetoCompraVo o) {
        try {
            return ComparisonChain.start().compare(getNumeroLote(), o.getNumeroLote()).compare(getNumeroItem(), o.getNumeroItem()).result();
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
