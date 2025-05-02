package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by Desenvolvimento on 02/02/2016.
 */
@Entity
@Audited
@Table(name = "ITEMEFETIVACAOINCORPMOVEL")
public class ItemEfetivacaoIncorporacaoMovel extends EventoBem {

    @ManyToOne
    private EfetivacaoSolicitacaoIncorporacaoMovel efetivacao;

    @ManyToOne
    private ItemSolicitacaoIncorporacaoMovel itemSolicitacao;

    public ItemEfetivacaoIncorporacaoMovel() {
        super(TipoEventoBem.INCORPORACAOBEM, TipoOperacao.DEBITO);
    }

    public ItemEfetivacaoIncorporacaoMovel(Bem bem, EstadoBem inicial, EstadoBem resultante, EfetivacaoSolicitacaoIncorporacaoMovel efetivacao, ItemSolicitacaoIncorporacaoMovel itemSolicitacao) {
        super(TipoEventoBem.INCORPORACAOBEM, TipoOperacao.DEBITO);
        this.setEstadoInicial(inicial);
        this.setEstadoResultante(resultante);
        this.setBem(bem);
        this.itemSolicitacao = itemSolicitacao;
        this.setValorDoLancamento(itemSolicitacao.getValorBem());
        this.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        this.efetivacao = efetivacao;
    }

    @Override
    public BigDecimal getValorDoLancamento() {
        return itemSolicitacao.getValorBem();
    }

    public EfetivacaoSolicitacaoIncorporacaoMovel getEfetivacao() {
        return efetivacao;
    }

    public void setEfetivacao(EfetivacaoSolicitacaoIncorporacaoMovel efetivacao) {
        this.efetivacao = efetivacao;
    }

    public ItemSolicitacaoIncorporacaoMovel getItemSolicitacao() {
        return itemSolicitacao;
    }

    public void setItemSolicitacao(ItemSolicitacaoIncorporacaoMovel itemSolicitacao) {
        this.itemSolicitacao = itemSolicitacao;
    }
}
