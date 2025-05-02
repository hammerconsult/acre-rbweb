package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class GuiaDistribuicaoReqItem extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @Etiqueta("Guia Distribuição Requisição")
    private GuiaDistribuicaoRequisicao guiaDistribuicaoRequisicao;

    @ManyToOne
    @Etiqueta("Item Requisição de Compra")
    private ItemRequisicaoDeCompra itemRequisicaoCompra;

    @ManyToOne
    @Etiqueta("Item Requisição Material")
    private ItemRequisicaoMaterial itemRequisicaoMaterial;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GuiaDistribuicaoRequisicao getGuiaDistribuicaoRequisicao() {
        return guiaDistribuicaoRequisicao;
    }

    public void setGuiaDistribuicaoRequisicao(GuiaDistribuicaoRequisicao guiaDistribuicaoRequisicao) {
        this.guiaDistribuicaoRequisicao = guiaDistribuicaoRequisicao;
    }

    public ItemRequisicaoDeCompra getItemRequisicaoCompra() {
        return itemRequisicaoCompra;
    }

    public void setItemRequisicaoCompra(ItemRequisicaoDeCompra itemRequisicaoCompra) {
        this.itemRequisicaoCompra = itemRequisicaoCompra;
    }

    public ItemRequisicaoMaterial getItemRequisicaoMaterial() {
        return itemRequisicaoMaterial;
    }

    public void setItemRequisicaoMaterial(ItemRequisicaoMaterial itemRequisicaoMaterial) {
        this.itemRequisicaoMaterial = itemRequisicaoMaterial;
    }
}
