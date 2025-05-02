package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

@Entity
@Audited
public class CardapioRequisicaoCompra extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @Etiqueta("Cardápio")
    private Cardapio cardapio;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Requisição de Compra")
    private RequisicaoDeCompra requisicaoCompra;

    @OneToMany(mappedBy = "cardapioRequisicaoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuiaDistribuicaoRequisicao> guiasDistribuicao;

    public CardapioRequisicaoCompra() {
        guiasDistribuicao = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cardapio getCardapio() {
        return cardapio;
    }

    public void setCardapio(Cardapio cardapio) {
        this.cardapio = cardapio;
    }

    public RequisicaoDeCompra getRequisicaoCompra() {
        return requisicaoCompra;
    }

    public void setRequisicaoCompra(RequisicaoDeCompra requisicaoCompra) {
        this.requisicaoCompra = requisicaoCompra;
    }

    public List<GuiaDistribuicaoRequisicao> getGuiasDistribuicao() {
        return guiasDistribuicao;
    }

    public void setGuiasDistribuicao(List<GuiaDistribuicaoRequisicao> guiasDistribuicao) {
        this.guiasDistribuicao = guiasDistribuicao;
    }
}
