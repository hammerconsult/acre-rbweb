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
@Table(name = "ITEMCONTRATOITEMPROPFORNEC")
public class ItemContratoItemPropostaFornecedor extends SuperEntidade implements ItemLicitatorioContrato {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ItemContrato itemContrato;
    @ManyToOne
    private ItemPropostaFornecedor itemPropostaFornecedor;

    public ItemContratoItemPropostaFornecedor() {
    }

    public ItemContratoItemPropostaFornecedor(ItemContrato itemContrato, ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemContrato = itemContrato;
        this.itemPropostaFornecedor = itemPropostaFornecedor;
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

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        return itemPropostaFornecedor;
    }

    public void setItemPropostaFornecedor(ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemPropostaFornecedor = itemPropostaFornecedor;
    }

    @Override
    public BigDecimal getQuantidadeLicitada() {
        return getItemProcessoCompra().getItemSolicitacaoMaterial().getQuantidade();
    }

    @Override
    public Integer getNumeroLote() {
        return getItemProcessoCompra().getLoteProcessoDeCompra().getNumero();
    }

    @Override
    public Integer getNumeroItem() {
        return getItemProcessoCompra().getNumero();
    }

    @Override
    public String getDescricao() {
        return getObjetoCompra().getDescricao();
    }

    @Override
    public String getDescricaoComplementar() {
        if (!Strings.isNullOrEmpty(itemContrato.getDescricaoComplementar())) {
            return itemContrato.getDescricaoComplementar();
        } else if (!Strings.isNullOrEmpty(getItemProcessoCompra().getDescricaoComplementar())) {
            return getItemProcessoCompra().getDescricaoComplementar();
        }
        return getObjetoCompra().getDescricao();
    }

    @Override
    public ObjetoCompra getObjetoCompra() {
        if (itemContrato.getObjetoCompraContrato() != null) {
            return itemContrato.getObjetoCompraContrato();
        }
        return getItemPropostaFornecedor().getObjetoCompra();
    }

    @Override
    public ItemProcessoDeCompra getItemProcessoCompra() {
        return getItemPropostaFornecedor().getItemProcessoDeCompra();
    }

    @Override
    public String getModelo() {
        return getItemPropostaFornecedor().getModelo();
    }

    @Override
    public String getMarca() {
        return getItemPropostaFornecedor().getMarca();
    }

    @Override
    public TipoControleLicitacao getTipoControle() {
        return getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().getTipoControle();
    }

    @Override
    public UnidadeMedida getUnidadeMedida() {
        return getItemProcessoCompra().getItemSolicitacaoMaterial().getUnidadeMedida();
    }
}
