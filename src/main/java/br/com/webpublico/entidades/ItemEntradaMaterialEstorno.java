package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ItemMovimentoEstoque;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@GrupoDiagrama(nome = "Materiais")
@Etiqueta("Item Entrada Material Estorno")
public class ItemEntradaMaterialEstorno extends SuperEntidade implements ItemMovimentoEstoque, Comparable<ItemEntradaMaterialEstorno> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ItemEntradaMaterial itemEntradaMaterial;

    @ManyToOne
    private MovimentoEstoque movimentoEstoque;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemEntradaMaterial getItemEntradaMaterial() {
        return itemEntradaMaterial;
    }

    public void setItemEntradaMaterial(ItemEntradaMaterial itemEntradaMaterial) {
        this.itemEntradaMaterial = itemEntradaMaterial;
    }

    public MovimentoEstoque getMovimentoEstoque() {
        return movimentoEstoque;
    }

    public void setMovimentoEstoque(MovimentoEstoque movimentoEstoque) {
        this.movimentoEstoque = movimentoEstoque;
    }

    @Override
    public int compareTo(ItemEntradaMaterialEstorno o) {
        if (itemEntradaMaterial.getNumeroItem() != null && o.getItemEntradaMaterial().getNumeroItem() != null) {
            return itemEntradaMaterial.getNumeroItem().compareTo(o.getItemEntradaMaterial().getNumeroItem());
        }
        return 0;
    }

    @Override
    public Long getIdOrigem() {
        return itemEntradaMaterial.getId();
    }

    @Override
    public Integer getNumeroItem() {
        return itemEntradaMaterial.getNumeroItem();
    }

    @Override
    public Date getDataMovimento() {
        return DataUtil.getDataComHoraAtual(itemEntradaMaterial.getEntradaMaterial().getDataEstorno());
    }

    @Override
    public BigDecimal getValorTotal() {
        return itemEntradaMaterial.getValorTotal();
    }

    @Override
    public BigDecimal getQuantidade() {
        return itemEntradaMaterial.getQuantidade();
    }

    @Override
    public BigDecimal getValorUnitario() {
        return itemEntradaMaterial.getValorUnitario();
    }

    @Override
    public LocalEstoqueOrcamentario getLocalEstoqueOrcamentario() {
        return itemEntradaMaterial.getLocalEstoqueOrcamentario();
    }

    @Override
    public LoteMaterial getLoteMaterial() {
        return itemEntradaMaterial.getLoteMaterial();
    }

    @Override
    public Material getMaterial() {
        return itemEntradaMaterial.getMaterial();
    }

    @Override
    public TipoOperacao getTipoOperacao() {
        return TipoOperacao.DEBITO;
    }

    @Override
    public TipoEstoque getTipoEstoque() {
        return itemEntradaMaterial.getLocalEstoque().getTipoEstoque();
    }

    @Override
    public String getDescricaoDaOperacao() {
        return "Estorno de Entrada por Compra N°: " + itemEntradaMaterial.getEntradaMaterial().getNumero();
    }

    @Override
    public Boolean ehEstorno() {
        return true;
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return itemEntradaMaterial.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria();
    }

    @Override
    public TipoBaixaBens getTipoBaixaBens() {
        return null;
    }

    @Override
    public TipoIngresso getTipoIngressoBens() {
        return null;
    }
}
