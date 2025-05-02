package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.interfaces.ItemLicitatorioContrato;
import com.google.common.base.Strings;
import org.apache.derby.vti.IFastPath;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Table(name = "ITEMCONTRATOADESAOATAINT")
public class ItemContratoAdesaoAtaInterna extends SuperEntidade implements ItemLicitatorioContrato {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ItemContrato itemContrato;
    @ManyToOne
    private ItemPropostaFornecedor itemPropostaFornecedor;
    @ManyToOne
    private ItemSolicitacaoExterno itemSolicitacaoExterno;

    public ItemContratoAdesaoAtaInterna() {
    }

    public ItemContratoAdesaoAtaInterna(ItemContrato itemContrato, ItemPropostaFornecedor itemPropostaFornecedor, ItemSolicitacaoExterno itemSolicitacaoExterno) {
        this.itemContrato = itemContrato;
        this.itemPropostaFornecedor = itemPropostaFornecedor;
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

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        return itemPropostaFornecedor;
    }

    public void setItemPropostaFornecedor(ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemPropostaFornecedor = itemPropostaFornecedor;
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
        return getItemSolicitacaoExterno().getItemProcessoCompra().getLoteProcessoDeCompra().getNumero();
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
        if (itemContrato.getObjetoCompraContrato() != null) {
            return itemContrato.getObjetoCompraContrato();
        }
        return getItemSolicitacaoExterno().getObjetoCompra();
    }

    @Override
    public ItemProcessoDeCompra getItemProcessoCompra() {
        return getItemSolicitacaoExterno().getItemProcessoCompra();
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
        return getItemSolicitacaoExterno().getTipoControle();
    }

    @Override
    public UnidadeMedida getUnidadeMedida() {
        return getItemSolicitacaoExterno().getUnidadeMedida();
    }
}
