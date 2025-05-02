package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 04/11/14
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemSolicitacaoAlienacao extends EventoBem {

    @ManyToOne
    private LoteAvaliacaoAlienacao loteAvaliacaoAlienacao;

    @ManyToOne
    private SolicitacaoAlienacao solicitacaoAlienacao;

    private BigDecimal valorAvaliado;

    public ItemSolicitacaoAlienacao(Long id, BigDecimal valorAvaliado) {
        super(null, null);
        setId(id);
        this.valorAvaliado = valorAvaliado;
    }

    public ItemSolicitacaoAlienacao() {
        super(TipoEventoBem.ITEMLOTESOLICITACAOALIENACAO, TipoOperacao.NENHUMA_OPERACAO);
        this.setValorAvaliado(BigDecimal.ZERO);
    }

    public LoteAvaliacaoAlienacao getLoteAvaliacaoAlienacao() {
        return loteAvaliacaoAlienacao;
    }

    public void setLoteAvaliacaoAlienacao(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        this.loteAvaliacaoAlienacao = loteAvaliacaoAlienacao;
    }

    public BigDecimal getValorAvaliado() {
        return valorAvaliado;
    }

    public void setValorAvaliado(BigDecimal valorAvaliado) {
        this.valorAvaliado = valorAvaliado;
    }

    public SolicitacaoAlienacao getSolicitacaoAlienacao() {
        return solicitacaoAlienacao;
    }

    public void setSolicitacaoAlienacao(SolicitacaoAlienacao solicitacaoAlienacao) {
        this.solicitacaoAlienacao = solicitacaoAlienacao;
    }

    @Override
    public int compareTo(EventoBem o) {
        return this.getBem().getIdentificacao().compareTo(o.getBem().getIdentificacao());
    }
}
