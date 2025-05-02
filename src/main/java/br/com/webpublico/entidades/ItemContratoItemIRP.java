package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.interfaces.ItemLicitatorioContrato;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
public class ItemContratoItemIRP extends SuperEntidade implements ItemLicitatorioContrato {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ItemContrato itemContrato;
    @ManyToOne
    private ItemPropostaFornecedor itemPropostaFornecedor;
    @ManyToOne
    private ItemParticipanteIRP itemParticipanteIRP;

    public ItemContratoItemIRP() {
    }

    public ItemContratoItemIRP(ItemContrato itemContrato, ItemPropostaFornecedor itemPropostaFornecedor, ItemParticipanteIRP itemParticipanteIRP) {
        this.itemContrato = itemContrato;
        this.itemPropostaFornecedor = itemPropostaFornecedor;
        this.itemParticipanteIRP = itemParticipanteIRP;
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

    public ItemParticipanteIRP getItemParticipanteIRP() {
        return itemParticipanteIRP;
    }

    public void setItemParticipanteIRP(ItemParticipanteIRP itemParticipanteIRP) {
        this.itemParticipanteIRP = itemParticipanteIRP;
    }

    @Override
    public BigDecimal getQuantidadeLicitada() {
        return getItemParticipanteIRP().getQuantidade();
    }

    @Override
    public Integer getNumeroLote() {
        return getItemPropostaFornecedor().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero();
    }

    @Override
    public Integer getNumeroItem() {
        return getItemPropostaFornecedor().getItemProcessoDeCompra().getNumero();
    }

    @Override
    public String getDescricao() {
        return getObjetoCompra().getDescricao();
    }

    @Override
    public String getDescricaoComplementar() {
        if (!Strings.isNullOrEmpty(itemContrato.getDescricaoComplementar())) {
            return itemContrato.getDescricaoComplementar();
        } else if (!Strings.isNullOrEmpty(getItemPropostaFornecedor().getItemProcessoDeCompra().getDescricaoComplementar())) {
            return getItemPropostaFornecedor().getItemProcessoDeCompra().getDescricaoComplementar();
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
