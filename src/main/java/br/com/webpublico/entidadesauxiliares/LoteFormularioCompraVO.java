package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LoteFormularioCompraVO implements Serializable, Comparable<LoteFormularioCompraVO> {

    private Integer numero;
    private String descricao;
    private TipoControleLicitacao tipoControle;
    private BigDecimal valorTotal;
    private LoteIntencaoRegistroPreco loteIRP;
    private LoteFormularioCotacao loteFormulario;
    private LoteCotacao loteCotacao;
    private List<ItemFormularioCompraVO> itens;
    private Long criadoEm;

    public LoteFormularioCompraVO() {
        super();
        itens = Lists.newArrayList();
        criadoEm = System.nanoTime();
    }

    public LoteIntencaoRegistroPreco getLoteIRP() {
        return loteIRP;
    }

    public void setLoteIRP(LoteIntencaoRegistroPreco loteIRP) {
        this.loteIRP = loteIRP;
    }

    public LoteFormularioCotacao getLoteFormulario() {
        return loteFormulario;
    }

    public void setLoteFormulario(LoteFormularioCotacao loteFormulario) {
        this.loteFormulario = loteFormulario;
    }

    public LoteCotacao getLoteCotacao() {
        return loteCotacao;
    }

    public void setLoteCotacao(LoteCotacao loteCotacao) {
        this.loteCotacao = loteCotacao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ItemFormularioCompraVO> getItens() {
        return itens;
    }

    public void setItens(List<ItemFormularioCompraVO> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public boolean hasItens() {
        return !Util.isListNullOrEmpty(getItens());
    }

    public void calcularValorTotal() {
        valorTotal = BigDecimal.ZERO;
        if (hasItens()) {
            itens.forEach(item -> valorTotal = valorTotal.add(item.getValorTotal()));
        }
    }

    public boolean isTipoControlePorValor() {
        return tipoControle != null && tipoControle.isTipoControlePorValor();
    }

    public boolean isTipoControlePorQuantidade() {
        return tipoControle != null && tipoControle.isTipoControlePorQuantidade();
    }

    public List<ItemFormularioCompraVO> getItensValorReferencia() {
        List<ItemFormularioCompraVO> toReturn = Lists.newArrayList();
        if (hasItens()) {
            Collections.sort(itens);
            if (loteCotacao.getCotacao().getFormularioCotacao().getTipoSolicitacao().isObraServicoEngenharia()) {
                toReturn.addAll(itens);
            } else {
                for (ItemFormularioCompraVO itemCotacao : itens) {
                    if (itemCotacao.getObjetoCompra().isObjetoValorReferencia()) {
                        toReturn.add(itemCotacao);
                    }
                }
            }
        }
        return toReturn;
    }

    public static LoteFormularioCompraVO fromIrp(LoteIntencaoRegistroPreco lote) {
        LoteFormularioCompraVO loteVO = new LoteFormularioCompraVO();
        loteVO.setNumero(lote.getNumero());
        loteVO.setDescricao(lote.getDescricao());
        loteVO.setValorTotal(lote.getValorTotal());
        loteVO.setLoteIRP(lote);
        return loteVO;
    }

    public static LoteFormularioCompraVO fromFormulario(LoteFormularioCotacao lote) {
        LoteFormularioCompraVO loteVO = new LoteFormularioCompraVO();
        loteVO.setNumero(lote.getNumero());
        loteVO.setDescricao(lote.getDescricao());
        loteVO.setValorTotal(lote.getValorTotal());
        loteVO.setLoteFormulario(lote);
        return loteVO;
    }

    public static LoteFormularioCompraVO fromCotacao(LoteCotacao lote) {
        LoteFormularioCompraVO loteVO = new LoteFormularioCompraVO();
        loteVO.setNumero(lote.getNumero());
        loteVO.setDescricao(lote.getDescricao());
        loteVO.setValorTotal(lote.getValor());
        loteVO.setLoteCotacao(lote);
        return loteVO;
    }

    public static List<LoteFormularioCompraVO> popularFormularioCompraVOFromFormulario(List<LoteFormularioCotacao> lotesFormulario) {
        Map<ItemLoteFormularioCotacao, ItemLoteFormularioCotacao> mapItemSemBeneficioItemCotaRes = ItemFormularioCompraVO.getMapItemSemBeneficioItemCotaReservadaFromFormulario(lotesFormulario);
        List<LoteFormularioCompraVO> lotesVO = Lists.newArrayList();

        lotesFormulario.forEach(loteForm -> {
            LoteFormularioCompraVO loteVO = LoteFormularioCompraVO.fromFormulario(loteForm);

            loteForm.getItensLoteFormularioCotacao()
                .stream()
                .filter(item -> !item.getTipoBeneficio().isCotaReservadaME())
                .forEach(itemForm -> {
                    ItemFormularioCompraVO itemSemBenef = ItemFormularioCompraVO.fromFormulario(itemForm, loteVO);
                    loteVO.getItens().add(itemSemBenef);
                    loteVO.setTipoControle(itemForm.getTipoControle());

                    ItemLoteFormularioCotacao itemCotaReservada = mapItemSemBeneficioItemCotaRes.get(itemForm);
                    if (itemCotaReservada != null) {
                        ItemFormularioCompraVO itemCotaReservadaVO = ItemFormularioCompraVO.fromFormulario(itemCotaReservada, loteVO);
                        itemCotaReservadaVO.setItemReferencialCotaReserv(itemSemBenef);

                        itemSemBenef.setQuantidadeSemBenecifio(itemForm.getQuantidade());
                        itemSemBenef.setQuantidadeCotaReservadaME(itemCotaReservada.getQuantidade());

                        loteVO.getItens().add(itemCotaReservadaVO);
                    }
                });
            lotesVO.add(loteVO);
        });
        return lotesVO;
    }

    public static List<LoteFormularioCompraVO> popularFormularioCompraVOFromCotacao(List<LoteCotacao> lotes) {
        Map<ItemCotacao, ItemCotacao> mapItemSemBeneficioItemCotaRes = ItemFormularioCompraVO.getMapItemSemBeneficioItemCotaReservadaFromCotacao(lotes);
        List<LoteFormularioCompraVO> lotesVO = Lists.newArrayList();

        lotes.forEach(lote -> {
            LoteFormularioCompraVO loteVO = LoteFormularioCompraVO.fromCotacao(lote);

            lote.getItens()
                .stream()
                .filter(item -> !item.getTipoBeneficio().isCotaReservadaME())
                .forEach(itemCot -> {
                    ItemFormularioCompraVO itemSemBenef = ItemFormularioCompraVO.fromCotacao(itemCot, loteVO);
                    loteVO.getItens().add(itemSemBenef);
                    loteVO.setTipoControle(itemCot.getTipoControle());

                    ItemCotacao itemCotaReservada = mapItemSemBeneficioItemCotaRes.get(itemCot);
                    if (itemCotaReservada != null) {
                        ItemFormularioCompraVO itemCotaReservadaVO = ItemFormularioCompraVO.fromCotacao(itemCotaReservada, loteVO);
                        itemCotaReservadaVO.setItemReferencialCotaReserv(itemSemBenef);

                        itemSemBenef.setQuantidadeSemBenecifio(itemCot.getQuantidade());
                        itemSemBenef.setQuantidadeCotaReservadaME(itemCotaReservada.getQuantidade());

                        loteVO.getItens().add(itemCotaReservadaVO);
                    }
                });
            lotesVO.add(loteVO);
        });
        return lotesVO;
    }

    public static List<LoteFormularioCompraVO> popularFormularioCompraVOFromIRP(List<LoteIntencaoRegistroPreco> lotesIRP) {
        Map<ItemIntencaoRegistroPreco, ItemIntencaoRegistroPreco> mapItemSemBeneficioItemCotaRes = ItemFormularioCompraVO.getMapItemSemBeneficioItemCotaReservadaFromIRP(lotesIRP);
        List<LoteFormularioCompraVO> lotesVO = Lists.newArrayList();

        lotesIRP.forEach(lote -> {
            LoteFormularioCompraVO loteVO = LoteFormularioCompraVO.fromIrp(lote);

            lote.getItens()
                .stream()
                .filter(item -> !item.getTipoBeneficio().isCotaReservadaME())
                .forEach(itemIrp -> {
                    ItemFormularioCompraVO itemSemBenef = ItemFormularioCompraVO.fromIRP(itemIrp, loteVO);
                    loteVO.getItens().add(itemSemBenef);
                    loteVO.setTipoControle(itemIrp.getTipoControle());

                    ItemIntencaoRegistroPreco itemCotaReservada = mapItemSemBeneficioItemCotaRes.get(itemIrp);
                    if (itemCotaReservada != null) {
                        ItemFormularioCompraVO itemCotaReservadaVO = ItemFormularioCompraVO.fromIRP(itemCotaReservada, loteVO);
                        itemCotaReservadaVO.setItemReferencialCotaReserv(itemSemBenef);

                        itemSemBenef.setQuantidadeSemBenecifio(itemIrp.getQuantidade());
                        itemSemBenef.setQuantidadeCotaReservadaME(itemCotaReservada.getQuantidade());

                        loteVO.getItens().add(itemCotaReservadaVO);
                    }
                });
            lotesVO.add(loteVO);
        });
        return lotesVO;
    }

    @Override
    public int compareTo(LoteFormularioCompraVO o) {
        return this.getNumero().compareTo(o.getNumero());
    }
}
