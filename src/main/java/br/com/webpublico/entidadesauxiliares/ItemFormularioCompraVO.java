package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoBeneficio;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class ItemFormularioCompraVO implements Serializable, Comparable<ItemFormularioCompraVO> {

    public static final BigDecimal MAX_PERCENTUAL_COTA_RESERVADA = new BigDecimal("25");
    public static final BigDecimal CEM = new BigDecimal("100");

    private LoteFormularioCompraVO loteFormularioCompraVO;
    private Integer numero;
    private ObjetoCompra objetoCompra;
    private String descricaoComplementar;
    private BigDecimal quantidade;
    private BigDecimal quantidadeSemBenecifio;
    private BigDecimal quantidadeCotaReservadaME;
    private BigDecimal valorTotal;
    private BigDecimal valorUnitario;
    private BigDecimal percentualCotaReservada;
    private UnidadeMedida unidadeMedida;
    private TipoControleLicitacao tipoControle;
    private TipoBeneficio tipoBeneficio;
    private Boolean exclusivoME;
    private ItemFormularioCompraVO itemReferencialCotaReserv;
    private ItemIntencaoRegistroPreco itemIRP;
    private ItemLoteFormularioCotacao itemFormulario;
    private ItemCotacao itemCotacao;
    private Long criadoEm;
    private List<ValorCotacao> valoresCotacao;

    public ItemFormularioCompraVO() {
        quantidade = BigDecimal.ZERO;
        quantidadeSemBenecifio = BigDecimal.ZERO;
        quantidadeCotaReservadaME = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        exclusivoME = false;
        tipoBeneficio = TipoBeneficio.SEM_BENEFICIO;
        tipoControle = TipoControleLicitacao.QUANTIDADE;
        percentualCotaReservada = BigDecimal.ZERO;
        valoresCotacao = Lists.newArrayList();
        criadoEm = System.nanoTime();
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public ItemLoteFormularioCotacao getItemFormulario() {
        return itemFormulario;
    }

    public void setItemFormulario(ItemLoteFormularioCotacao itemFormulario) {
        this.itemFormulario = itemFormulario;
    }

    public ItemCotacao getItemCotacao() {
        return itemCotacao;
    }

    public void setItemCotacao(ItemCotacao itemCotacao) {
        this.itemCotacao = itemCotacao;
    }

    public LoteFormularioCompraVO getLoteFormularioCompraVO() {
        return loteFormularioCompraVO;
    }

    public void setLoteFormularioCompraVO(LoteFormularioCompraVO loteFormularioCompraVO) {
        this.loteFormularioCompraVO = loteFormularioCompraVO;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public String getDescricao() {
        if (objetoCompra != null) {
            return objetoCompra.toString();
        }
        return "";
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int compareTo(ItemFormularioCompraVO o) {
        return this.getNumero().compareTo(o.getNumero());
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public TipoBeneficio getTipoBeneficio() {
        return tipoBeneficio;
    }

    public void setTipoBeneficio(TipoBeneficio tipoBeneficio) {
        this.tipoBeneficio = tipoBeneficio;
    }

    public BigDecimal getQuantidadeSemBenecifio() {
        return quantidadeSemBenecifio;
    }

    public void setQuantidadeSemBenecifio(BigDecimal quantidadeSemBenecifio) {
        this.quantidadeSemBenecifio = quantidadeSemBenecifio;
    }

    public BigDecimal getQuantidadeCotaReservadaME() {
        return quantidadeCotaReservadaME;
    }

    public void setQuantidadeCotaReservadaME(BigDecimal quantidadeCotaReservadaME) {
        this.quantidadeCotaReservadaME = quantidadeCotaReservadaME;
    }

    public Boolean getExclusivoME() {
        return exclusivoME;
    }

    public void setExclusivoME(Boolean exclusivoME) {
        this.exclusivoME = exclusivoME;
    }


    public boolean isTipoControlePorValor() {
        return tipoControle != null && tipoControle.isTipoControlePorValor();
    }

    public boolean isTipoControlePorQuantidade() {
        return tipoControle != null && tipoControle.isTipoControlePorQuantidade();
    }

    public boolean hasQuantidadeCotaReservada() {
        return quantidadeCotaReservadaME != null && quantidadeCotaReservadaME.compareTo(BigDecimal.ZERO) > 0;
    }

    public ItemFormularioCompraVO getItemReferencialCotaReserv() {
        return itemReferencialCotaReserv;
    }

    public void setItemReferencialCotaReserv(ItemFormularioCompraVO itemReferencialCotaReserv) {
        this.itemReferencialCotaReserv = itemReferencialCotaReserv;
    }

    public BigDecimal getPercentualCotaReservada() {
        return percentualCotaReservada;
    }

    public void setPercentualCotaReservada(BigDecimal percentualCotaReservada) {
        this.percentualCotaReservada = percentualCotaReservada;
    }

    public ItemIntencaoRegistroPreco getItemIRP() {
        return itemIRP;
    }

    public void setItemIRP(ItemIntencaoRegistroPreco itemIRP) {
        this.itemIRP = itemIRP;
    }

    public List<ValorCotacao> getValoresCotacao() {
        return valoresCotacao;
    }

    public void setValoresCotacao(List<ValorCotacao> valoresCotacao) {
        this.valoresCotacao = valoresCotacao;
    }

    public boolean hasQuantidadeTotal() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasQuantidadeSemBeneficio() {
        return quantidadeSemBenecifio != null && quantidadeSemBenecifio.compareTo(BigDecimal.ZERO) > 0;
    }

    public void calcularValorTotal() {
        if (quantidade != null && valorUnitario != null) {
            setValorTotal(quantidade.multiply(valorUnitario).setScale(2, RoundingMode.HALF_EVEN));
        }
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public static ItemFormularioCompraVO fromIRP(ItemIntencaoRegistroPreco item, LoteFormularioCompraVO loteVO) {
        ItemFormularioCompraVO itemVO = new ItemFormularioCompraVO();
        itemVO.setLoteFormularioCompraVO(loteVO);
        itemVO.setNumero(item.getNumero());
        itemVO.setDescricaoComplementar(item.getDescricaoComplementar());
        itemVO.setObjetoCompra(item.getObjetoCompra());
        itemVO.setTipoControle(item.getTipoControle());
        itemVO.setTipoBeneficio(item.getTipoBeneficio());
        itemVO.setUnidadeMedida(item.getUnidadeMedida());
        itemVO.setQuantidade(item.getQuantidade());
        itemVO.setValorTotal(item.getValor());
        itemVO.setItemIRP(item);
        return itemVO;
    }

    public static ItemFormularioCompraVO fromFormulario(ItemLoteFormularioCotacao item, LoteFormularioCompraVO loteVO) {
        ItemFormularioCompraVO itemVO = new ItemFormularioCompraVO();
        itemVO.setLoteFormularioCompraVO(loteVO);
        itemVO.setNumero(item.getNumero());
        itemVO.setDescricaoComplementar(item.getDescricaoComplementar());
        itemVO.setObjetoCompra(item.getObjetoCompra());
        itemVO.setTipoControle(item.getTipoControle());
        itemVO.setTipoBeneficio(item.getTipoBeneficio());
        itemVO.setUnidadeMedida(item.getUnidadeMedida());
        itemVO.setQuantidade(item.getQuantidade());
        itemVO.setValorTotal(item.getValor());
        itemVO.setItemFormulario(item);
        return itemVO;
    }

    public static ItemFormularioCompraVO fromCotacao(ItemCotacao item, LoteFormularioCompraVO loteVO) {
        ItemFormularioCompraVO itemVO = new ItemFormularioCompraVO();
        itemVO.setLoteFormularioCompraVO(loteVO);
        itemVO.setNumero(item.getNumero());
        itemVO.setDescricaoComplementar(item.getDescricaoComplementar());
        itemVO.setObjetoCompra(item.getObjetoCompra());
        itemVO.setTipoControle(item.getTipoControle());
        itemVO.setTipoBeneficio(item.getTipoBeneficio());
        itemVO.setUnidadeMedida(item.getUnidadeMedida());
        itemVO.setQuantidade(item.getQuantidade());
        itemVO.setValorUnitario(item.getValorUnitario());
        itemVO.setValorTotal(item.getValorTotal());
        itemVO.setItemCotacao(item);
        itemVO.setValoresCotacao(item.getValoresCotacao());
        return itemVO;
    }

    public static Map<ItemCotacao, ItemCotacao> getMapItemSemBeneficioItemCotaReservadaFromCotacao(List<LoteCotacao> lotes) {
        Map<ItemCotacao, ItemCotacao> mapItemSemBenefItemCotaRes = Maps.newHashMap();
        lotes.forEach(lote -> {
            lote.getItens().forEach(item -> {
                if (item.getItemReferencialCotaReserv() != null) {
                    mapItemSemBenefItemCotaRes.put(item.getItemReferencialCotaReserv(), item);
                }
            });
        });
        return mapItemSemBenefItemCotaRes;
    }

    public static Map<ItemLoteFormularioCotacao, ItemLoteFormularioCotacao> getMapItemSemBeneficioItemCotaReservadaFromFormulario(List<LoteFormularioCotacao> lotes) {
        Map<ItemLoteFormularioCotacao, ItemLoteFormularioCotacao> mapItemSemBenefItemCotaRes = Maps.newHashMap();
        lotes.forEach(lote -> {
            lote.getItensLoteFormularioCotacao().forEach(item -> {
                if (item.getItemReferencialCotaReserv() != null) {
                    mapItemSemBenefItemCotaRes.put(item.getItemReferencialCotaReserv(), item);
                }
            });
        });
        return mapItemSemBenefItemCotaRes;
    }

    public static Map<ItemIntencaoRegistroPreco, ItemIntencaoRegistroPreco> getMapItemSemBeneficioItemCotaReservadaFromIRP(List<LoteIntencaoRegistroPreco> lotes) {
        Map<ItemIntencaoRegistroPreco, ItemIntencaoRegistroPreco> mapItemSemBenefItemCotaRes = Maps.newHashMap();
        lotes.forEach(lote -> {
            lote.getItens().forEach(item -> {
                if (item.getItemReferencialCotaReserv() != null) {
                    mapItemSemBenefItemCotaRes.put(item.getItemReferencialCotaReserv(), item);
                }
            });
        });
        return mapItemSemBenefItemCotaRes;
    }

    public static Map<ItemFormularioCompraVO, ItemFormularioCompraVO> getMapItemSemBeneficioItemCotaReservada(List<LoteFormularioCompraVO> lotes) {
        Map<ItemFormularioCompraVO, ItemFormularioCompraVO> mapItemSemBenefItemCotaRes = Maps.newHashMap();
        lotes.forEach(lote -> {
            lote.getItens().forEach(item -> {
                if (item.getItemReferencialCotaReserv() != null) {
                    mapItemSemBenefItemCotaRes.put(item.getItemReferencialCotaReserv(), item);
                }
            });
        });
        return mapItemSemBenefItemCotaRes;
    }

}

