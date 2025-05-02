package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 12/11/14
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class LeilaoAlienacaoLoteBem extends EventoBem {

    @ManyToOne
    private LeilaoAlienacaoLote leilaoAlienacaoLote;

    @ManyToOne
    private ItemSolicitacaoAlienacao itemSolicitacaoAlienacao;

    private BigDecimal valorProporcionalArrematado;

    public LeilaoAlienacaoLoteBem() {
        super(TipoEventoBem.LEILAOALIENACAOLOTEBEM, TipoOperacao.NENHUMA_OPERACAO);
        valorProporcionalArrematado = BigDecimal.ZERO;
    }

    public LeilaoAlienacaoLoteBem(Long id) {
        super(null, null);
        setId(id);
    }

    public LeilaoAlienacaoLoteBem(EstadoBem estadoBem,  EstadoBem estadoResultante, ItemSolicitacaoAlienacao itemSolicitacao) {
        super(TipoEventoBem.LEILAOALIENACAOLOTEBEM, TipoOperacao.NENHUMA_OPERACAO);
        this.setValorProporcionalArrematado(BigDecimal.ZERO);
        this.setBem(itemSolicitacao.getBem());
        this.setEstadoInicial(estadoBem);
        this.setEstadoResultante(estadoResultante);
        this.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
        this.setItemSolicitacaoAlienacao(itemSolicitacao);
    }

    public LeilaoAlienacaoLote getLeilaoAlienacaoLote() {
        return leilaoAlienacaoLote;
    }

    public void setLeilaoAlienacaoLote(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        this.leilaoAlienacaoLote = leilaoAlienacaoLote;
    }

    public ItemSolicitacaoAlienacao getItemSolicitacaoAlienacao() {
        return itemSolicitacaoAlienacao;
    }

    public void setItemSolicitacaoAlienacao(ItemSolicitacaoAlienacao itemSolicitacaoAlienacao) {
        this.itemSolicitacaoAlienacao = itemSolicitacaoAlienacao;
    }

    public BigDecimal getValorProporcionalArrematado() {
        return valorProporcionalArrematado;
    }

    public void setValorProporcionalArrematado(BigDecimal valorProporcionalArrematado) {
        this.valorProporcionalArrematado = valorProporcionalArrematado;
    }
}
