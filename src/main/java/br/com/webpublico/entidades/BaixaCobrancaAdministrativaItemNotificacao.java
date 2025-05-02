package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;


@Entity
@Audited
@Table(name = "BAIXANOTIFICACOBADMITEM")
public class BaixaCobrancaAdministrativaItemNotificacao extends SuperEntidade{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Baixa de Notificação Administrativa")
    private BaixaNotificacaoCobrancaAdministrativa baixaNotificacao;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Itens Notificação")
    private ItemNotificacao itemNotificacao;

    public BaixaCobrancaAdministrativaItemNotificacao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BaixaNotificacaoCobrancaAdministrativa getBaixaNotificacao() {
        return baixaNotificacao;
    }

    public void setBaixaNotificacao(BaixaNotificacaoCobrancaAdministrativa baixaNotificacao) {
        this.baixaNotificacao = baixaNotificacao;
    }

    public ItemNotificacao getItemNotificacao() {
        return itemNotificacao;
    }

    public void setItemNotificacao(ItemNotificacao itemNotificacao) {
        this.itemNotificacao = itemNotificacao;
    }
}
