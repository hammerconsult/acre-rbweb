package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.ItemProcessoDeCompra;
import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidades.UnidadeMedida;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoControleLicitacao;

import java.io.Serializable;
import java.math.BigDecimal;

public interface ItemLicitatorioContrato extends Serializable {

    public BigDecimal getQuantidadeLicitada();

    public Integer getNumeroLote();

    public Integer getNumeroItem();

    public String getDescricao();

    public String getDescricaoComplementar();

    public String getModelo();

    public ObjetoCompra getObjetoCompra();

    public String getMarca();

    public TipoControleLicitacao getTipoControle();

    public UnidadeMedida getUnidadeMedida();

    public ItemProcessoDeCompra getItemProcessoCompra();
}
