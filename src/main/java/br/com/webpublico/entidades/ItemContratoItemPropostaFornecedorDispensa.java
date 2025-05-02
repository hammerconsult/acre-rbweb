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
@Table(name = "ITEMCONTRATOITEMPROPDISP")
public class ItemContratoItemPropostaFornecedorDispensa extends SuperEntidade implements ItemLicitatorioContrato {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ItemContrato itemContrato;
    @ManyToOne
    @JoinColumn(name = "ITEMPROPFORNECDISPENSA_ID")
    private ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa;

    public ItemContratoItemPropostaFornecedorDispensa() {
    }

    public ItemContratoItemPropostaFornecedorDispensa(ItemContrato itemContrato, ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa) {
        this.itemContrato = itemContrato;
        this.itemPropostaFornecedorDispensa = itemPropostaFornecedorDispensa;
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

    public ItemPropostaFornecedorDispensa getItemPropostaFornecedorDispensa() {
        return itemPropostaFornecedorDispensa;
    }

    public void setItemPropostaFornecedorDispensa(ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa) {
        this.itemPropostaFornecedorDispensa = itemPropostaFornecedorDispensa;
    }

    @Override
    public BigDecimal getQuantidadeLicitada() {
        return getItemPropostaFornecedorDispensa().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getQuantidade();
    }

    @Override
    public Integer getNumeroLote() {
        return getItemPropostaFornecedorDispensa().getLotePropostaFornecedorDispensa().getLoteProcessoDeCompra().getNumero();
    }

    @Override
    public Integer getNumeroItem() {
        return getItemPropostaFornecedorDispensa().getItemProcessoDeCompra().getNumero();
    }

    @Override
    public String getDescricao() {
        return getObjetoCompra().getDescricao();
    }

    @Override
    public String getDescricaoComplementar() {
        if (!Strings.isNullOrEmpty(itemContrato.getDescricaoComplementar())) {
            return itemContrato.getDescricaoComplementar();
        } else if (!Strings.isNullOrEmpty(getItemPropostaFornecedorDispensa().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar())) {
            return getItemPropostaFornecedorDispensa().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar();
        }
        return getObjetoCompra().getDescricao();
    }

    @Override
    public ObjetoCompra getObjetoCompra() {
        if (itemContrato.getObjetoCompraContrato() !=null){
            return itemContrato.getObjetoCompraContrato();
        }
        return getItemPropostaFornecedorDispensa().getItemProcessoDeCompra().getObjetoCompra();
    }

    @Override
    public ItemProcessoDeCompra getItemProcessoCompra() {
        return getItemPropostaFornecedorDispensa().getItemProcessoDeCompra();
    }

    @Override
    public String getModelo() {
        return getItemPropostaFornecedorDispensa().getModelo();
    }

    @Override
    public String getMarca() {
        return getItemPropostaFornecedorDispensa().getMarca();
    }

    @Override
    public TipoControleLicitacao getTipoControle() {
        return getItemPropostaFornecedorDispensa().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle();
    }

    @Override
    public UnidadeMedida getUnidadeMedida() {
        return getItemPropostaFornecedorDispensa().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida();
    }
}
