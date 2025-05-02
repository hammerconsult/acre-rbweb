package br.com.webpublico.entidades.administrativo.licitacao;

import br.com.webpublico.entidades.ItemPregao;
import br.com.webpublico.entidades.PropostaFornecedor;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Próximo Vencedor da Licitação - Item")
@Table(name = "PROXIMOVENCEDORLICITEM")
public class ProximoVencedorLicitacaoItem extends SuperEntidade implements Comparable<ProximoVencedorLicitacaoItem> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Item Pregão")
    private ItemPregao itemPregao;

    @ManyToOne
    @Etiqueta("Próximo Vencedor Licitação")
    private ProximoVencedorLicitacao proximoVencedorLicitacao;

    @ManyToOne
    @Etiqueta("Próximo Vencedor")
    private PropostaFornecedor proximoVencedor;

    @Obrigatorio
    @Etiqueta("Valor Lance Atual (R$)")
    private BigDecimal valorLanceAtual;

    @Obrigatorio
    @Etiqueta("Valor Próximo Lance (R$)")
    private BigDecimal valorProximoLance;

    public ProximoVencedorLicitacaoItem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemPregao getItemPregao() {
        return itemPregao;
    }

    public void setItemPregao(ItemPregao itemPregao) {
        this.itemPregao = itemPregao;
    }

    public ProximoVencedorLicitacao getProximoVencedorLicitacao() {
        return proximoVencedorLicitacao;
    }

    public void setProximoVencedorLicitacao(ProximoVencedorLicitacao proximoVencedorLicitacao) {
        this.proximoVencedorLicitacao = proximoVencedorLicitacao;
    }

    public PropostaFornecedor getProximoVencedor() {
        return proximoVencedor;
    }

    public void setProximoVencedor(PropostaFornecedor proximoVencedor) {
        this.proximoVencedor = proximoVencedor;
    }

    public BigDecimal getValorLanceAtual() {
        return valorLanceAtual;
    }

    public void setValorLanceAtual(BigDecimal valorLanceAtual) {
        this.valorLanceAtual = valorLanceAtual;
    }

    public BigDecimal getValorProximoLance() {
        return valorProximoLance;
    }

    public void setValorProximoLance(BigDecimal valorProximoLance) {
        this.valorProximoLance = valorProximoLance;
    }

    @Override
    public int compareTo(ProximoVencedorLicitacaoItem o) {
        int comparacao = 0;
        if (getProximoVencedorLicitacao().getLicitacao().isTipoApuracaoPrecoLote() && getItemPregao().getItemPregaoLoteProcesso() != null) {
            comparacao = getItemPregao().getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getNumero().compareTo(o.getItemPregao().getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getNumero());
        }
        if (comparacao == 0) {
            if (getProximoVencedorLicitacao().getLicitacao().isTipoApuracaoPrecoItem() && getItemPregao().getItemPregaoItemProcesso() != null) {
                comparacao = ComparisonChain.start().compare(getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero(), o.getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero())
                    .compare(getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra().getNumero(), o.getItemPregao().getItemPregaoItemProcesso().getItemProcessoDeCompra().getNumero()).result();
            }
        }
        return comparacao;
    }
}
