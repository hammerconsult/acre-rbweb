package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by mga on 06/03/2018.
 */

@Entity
@Audited
@Etiqueta("Item Solicitação Ajuste de Bens Móveis")
@Table(name = "ITEMSOLICITACAOAJUSTEMOVEL")
public class ItemSolicitacaoAjusteBemMovel extends EventoBem {

    @ManyToOne
    @Etiqueta("Solicitação de Ajuste de Bens Móveis")
    private SolicitacaoAjusteBemMovel solicitacaoAjusteBemMovel;

    @Monetario
    @Etiqueta("Valor do Ajuste")
    private BigDecimal valorAjuste;

    public ItemSolicitacaoAjusteBemMovel() {
        super(TipoEventoBem.SOLICITACAO_AJUSTE_BEM_MOVEL_ORIGINAL, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemSolicitacaoAjusteBemMovel(TipoEventoBem tipoEventoBem) {
        super(tipoEventoBem, TipoOperacao.NENHUMA_OPERACAO);
        setValorAjuste(BigDecimal.ZERO);
    }

    public SolicitacaoAjusteBemMovel getSolicitacaoAjusteBemMovel() {
        return solicitacaoAjusteBemMovel;
    }

    public void setSolicitacaoAjusteBemMovel(SolicitacaoAjusteBemMovel solicitacaoAjusteBemMovel) {
        this.solicitacaoAjusteBemMovel = solicitacaoAjusteBemMovel;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }


    @Override
    public int compareTo(EventoBem o) {
        return this.getBem().getIdentificacao().compareTo(o.getBem().getIdentificacao());
    }
}
