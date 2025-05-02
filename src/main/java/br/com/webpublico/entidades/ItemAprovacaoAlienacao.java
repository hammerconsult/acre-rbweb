package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 12/01/15
 * Time: 09:12
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
public class ItemAprovacaoAlienacao extends EventoBem {

    @OneToOne
    private ItemSolicitacaoAlienacao itemSolicitacaoAlienacao;

    @ManyToOne
    private AprovacaoAlienacao aprovacaoAlienacao;

    public ItemAprovacaoAlienacao() {
        super(TipoEventoBem.ITEMAPROVACAOALIENACAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemSolicitacaoAlienacao getItemSolicitacaoAlienacao() {
        return itemSolicitacaoAlienacao;
    }

    public void setItemSolicitacaoAlienacao(ItemSolicitacaoAlienacao itemSolicitacaoAlienacao) {
        this.itemSolicitacaoAlienacao = itemSolicitacaoAlienacao;
    }

    public AprovacaoAlienacao getAprovacaoAlienacao() {
        return aprovacaoAlienacao;
    }

    public void setAprovacaoAlienacao(AprovacaoAlienacao aprovacaoAlienacao) {
        this.aprovacaoAlienacao = aprovacaoAlienacao;
    }
}
