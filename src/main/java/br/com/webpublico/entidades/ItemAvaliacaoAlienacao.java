package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "Patrimonio")
public class ItemAvaliacaoAlienacao extends EventoBem {

    @ManyToOne
    private LoteAvaliacaoAlienacao loteAvaliacaoAlienacao;

    /**
     * Adicionado para mostrar valor avaliado e os valores atuais do bem no relatório.
     * O restante do fluxo utiliza o valor avaliado do item da solicitação,
     * pois o mesmo está relacionado com o restante do fluxo.
     */
    private BigDecimal valorAvaliado;

    public ItemAvaliacaoAlienacao() {
        super(TipoEventoBem.AVALIACAO_ALIENACAO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public LoteAvaliacaoAlienacao getLoteAvaliacaoAlienacao() {
        return loteAvaliacaoAlienacao;
    }

    public void setLoteAvaliacaoAlienacao(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        this.loteAvaliacaoAlienacao = loteAvaliacaoAlienacao;
    }

    public BigDecimal getValorAvaliado() {
        return valorAvaliado == null ? BigDecimal.ZERO : valorAvaliado;
    }

    public void setValorAvaliado(BigDecimal valorAvaliado) {
        this.valorAvaliado = valorAvaliado;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
