package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class GuiaDistribuicaoRequisicao extends SuperEntidade implements Comparable<GuiaDistribuicaoRequisicao> {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @Etiqueta("Cardápio Requisição de Compra")
    private CardapioRequisicaoCompra cardapioRequisicaoCompra;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Requisicão Material")
    private RequisicaoMaterial requisicaoMaterial;

    @OneToMany(mappedBy = "guiaDistribuicaoRequisicao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuiaDistribuicaoReqItem> itens;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequisicaoMaterial getRequisicaoMaterial() {
        return requisicaoMaterial;
    }

    public void setRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        this.requisicaoMaterial = requisicaoMaterial;
    }

    public CardapioRequisicaoCompra getCardapioRequisicaoCompra() {
        return cardapioRequisicaoCompra;
    }

    public void setCardapioRequisicaoCompra(CardapioRequisicaoCompra cardapioRequisicaoCompra) {
        this.cardapioRequisicaoCompra = cardapioRequisicaoCompra;
    }

    public List<GuiaDistribuicaoReqItem> getItens() {
        return itens;
    }

    public void setItens(List<GuiaDistribuicaoReqItem> itens) {
        this.itens = itens;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(requisicaoMaterial.getDataRequisicao()) + " / " + requisicaoMaterial.getLocalEstoqueDestino().getCodigoDescricao();
    }

    @Override
    public int compareTo(GuiaDistribuicaoRequisicao o) {
        try {
            return ComparisonChain.start().compare(getRequisicaoMaterial().getLocalEstoqueDestino().getCodigo(),
                o.getRequisicaoMaterial().getLocalEstoqueDestino().getCodigo()).result();
        } catch (Exception e) {
            return 0;
        }
    }
}
