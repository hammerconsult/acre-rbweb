package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 16/10/14
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
@Etiqueta("Item Aquisição")
public class ItemAquisicao extends EventoBem {

    @ManyToOne
    @Etiqueta("Aquisição")
    @Obrigatorio
    private Aquisicao aquisicao;

    @OneToOne
    @Etiqueta("Item Solicitação Aquisição")
    private ItemSolicitacaoAquisicao itemSolicitacaoAquisicao;

    public ItemAquisicao() {
        super(TipoEventoBem.ITEMAQUISICAO, TipoOperacao.DEBITO);
    }

    public Aquisicao getAquisicao() {
        return aquisicao;
    }

    public void setAquisicao(Aquisicao aquisicao) {
        this.aquisicao = aquisicao;
    }

    public ItemSolicitacaoAquisicao getItemSolicitacaoAquisicao() {
        return itemSolicitacaoAquisicao;
    }

    public void setItemSolicitacaoAquisicao(ItemSolicitacaoAquisicao itemSolicitacaoAquisicao) {
        this.itemSolicitacaoAquisicao = itemSolicitacaoAquisicao;
    }

    public String getDescricaoProduto() {
        return itemSolicitacaoAquisicao.getDescricaoDoBem();
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return itemSolicitacaoAquisicao.getUnidadeAdministrativa();
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return itemSolicitacaoAquisicao.getUnidadeOrcamentaria();
    }
}
