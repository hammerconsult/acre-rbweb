package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Audited
@Table(name = "ITEMAPROVACAOTRANSFBEM")
@Etiqueta(value = "Item Aprovação Transferência de Bem", genero = "F")
public class ItemAprovacaoTransferenciaBem extends EventoBem {

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Aprovação")
    @ManyToOne
    private AprovacaoTransferenciaBem aprovacao;

    public ItemAprovacaoTransferenciaBem() {
        super(TipoEventoBem.APROVACAO_TRANSFERENCIA_BEM, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemAprovacaoTransferenciaBem(Bem bem, AprovacaoTransferenciaBem aprovacao, EstadoBem estadoInicial,EstadoBem estadoResultante) {
        super(TipoEventoBem.APROVACAO_TRANSFERENCIA_BEM, TipoOperacao.NENHUMA_OPERACAO);
        this.setBem(bem);
        this.aprovacao = aprovacao;
        this.setEstadoInicial(estadoInicial);
        this.setEstadoResultante(estadoResultante);
        this.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        this.setValorDoLancamento(BigDecimal.ZERO);
    }

    public AprovacaoTransferenciaBem getAprovacao() {
        return aprovacao;
    }

    public void setAprovacao(AprovacaoTransferenciaBem aprovacao) {
        this.aprovacao = aprovacao;
    }
}
