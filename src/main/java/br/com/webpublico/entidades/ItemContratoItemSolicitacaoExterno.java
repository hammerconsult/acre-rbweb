/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.interfaces.ItemLicitatorioContrato;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@Table(name = "ITEMCONTRATOITEMSOLEXT")
public class ItemContratoItemSolicitacaoExterno extends SuperEntidade implements ItemLicitatorioContrato {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private ItemContrato itemContrato;

    @ManyToOne
    private ItemSolicitacaoExterno itemSolicitacaoExterno;

    public ItemContratoItemSolicitacaoExterno() {
    }

    public ItemContratoItemSolicitacaoExterno(ItemContrato itemContrato, ItemSolicitacaoExterno itemSolicitacaoExterno) {
        this.itemContrato = itemContrato;
        this.itemSolicitacaoExterno = itemSolicitacaoExterno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public ItemSolicitacaoExterno getItemSolicitacaoExterno() {
        return itemSolicitacaoExterno;
    }

    public void setItemSolicitacaoExterno(ItemSolicitacaoExterno itemSolicitacaoExterno) {
        this.itemSolicitacaoExterno = itemSolicitacaoExterno;
    }

    @Override
    public BigDecimal getQuantidadeLicitada() {
        return getItemSolicitacaoExterno().getQuantidade();
    }

    @Override
    public Integer getNumeroLote() {
        return 1;
    }

    @Override
    public Integer getNumeroItem() {
        return getItemSolicitacaoExterno().getNumero();
    }

    @Override
    public String getDescricao() {
        return getObjetoCompra().getDescricao();
    }

    @Override
    public String getDescricaoComplementar() {
        if (!Strings.isNullOrEmpty(itemContrato.getDescricaoComplementar())) {
            return itemContrato.getDescricaoComplementar();
        } else if (!Strings.isNullOrEmpty(getItemSolicitacaoExterno().getDescricaoComplementar())) {
            return getItemSolicitacaoExterno().getDescricaoComplementar();
        }
        return getObjetoCompra().getDescricao();
    }

    @Override
    public ObjetoCompra getObjetoCompra() {
        if (itemContrato.getObjetoCompraContrato() !=null){
            return itemContrato.getObjetoCompraContrato();
        }
        return getItemSolicitacaoExterno().getObjetoCompra();
    }

    @Override
    public ItemProcessoDeCompra getItemProcessoCompra() {
        return null;
    }

    @Override
    public String getModelo() {
        return getItemSolicitacaoExterno().getModelo();
    }

    @Override
    public String getMarca() {
        return "";
    }

    @Override
    public TipoControleLicitacao getTipoControle() {
        try {
            return getItemSolicitacaoExterno().getTipoControle();
        } catch (NullPointerException e) {
            return TipoControleLicitacao.QUANTIDADE;
        }
    }

    @Override
    public UnidadeMedida getUnidadeMedida() {
        return getItemSolicitacaoExterno().getUnidadeMedida();
    }
}
