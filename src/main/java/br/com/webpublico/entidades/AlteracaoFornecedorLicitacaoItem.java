package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "ALTERACAOFORNECEDORLICITEM")
@Etiqueta("Item Alteração Fornecedor Licitação")
public class AlteracaoFornecedorLicitacaoItem extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Alteração Fornecedor")
    private AlteracaoFornecedorLicitacao alteracaoFornecedorLicit;

    @ManyToOne
    @Etiqueta("Item Proposta Fornecedor")
    private ItemPropostaFornecedor itemPropostaFornecedor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlteracaoFornecedorLicitacao getAlteracaoFornecedorLicit() {
        return alteracaoFornecedorLicit;
    }

    public void setAlteracaoFornecedorLicit(AlteracaoFornecedorLicitacao alteracaoFornecedorLicitacao) {
        this.alteracaoFornecedorLicit = alteracaoFornecedorLicitacao;
    }

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        return itemPropostaFornecedor;
    }

    public void setItemPropostaFornecedor(ItemPropostaFornecedor itemPropostaFornecedor) {
        this.itemPropostaFornecedor = itemPropostaFornecedor;
    }
}
