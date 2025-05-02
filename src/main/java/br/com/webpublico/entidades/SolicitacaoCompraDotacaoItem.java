package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Table(name = "SOLICITACAOCOMPRADOTITEM")
@Etiqueta("Solicitação de Compra Dotação - Itens")
public class SolicitacaoCompraDotacaoItem extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Solicitação de Compra Dotação")
    private SolicitacaoCompraDotacao solicitacaoCompraDotacao;

    @ManyToOne
    @Etiqueta("Item Solicitação")
    private ItemSolicitacao itemSolicitacao;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    public SolicitacaoCompraDotacaoItem() {
        quantidade = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoCompraDotacao getSolicitacaoCompraDotacao() {
        return solicitacaoCompraDotacao;
    }

    public void setSolicitacaoCompraDotacao(SolicitacaoCompraDotacao solicitacaoCompraDotacao) {
        this.solicitacaoCompraDotacao = solicitacaoCompraDotacao;
    }

    public ItemSolicitacao getItemSolicitacao() {
        return itemSolicitacao;
    }

    public void setItemSolicitacao(ItemSolicitacao itemSolicitacao) {
        this.itemSolicitacao = itemSolicitacao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }


    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorTotal() {
        try {
            if (hasQuantidade()) {
                BigDecimal total = quantidade.multiply(itemSolicitacao.getPreco());
                return total.setScale(2, RoundingMode.HALF_EVEN);
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    public String toString() {
        return itemSolicitacao.getNumero() + " - " + itemSolicitacao.getDescricao();
    }
}
