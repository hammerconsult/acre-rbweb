package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 04/11/14
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class LoteAvaliacaoAlienacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @Obrigatorio
    private Long numero;

    @Invisivel
    @OneToMany(mappedBy = "loteAvaliacaoAlienacao")
    private List<ItemSolicitacaoAlienacao> itensSolicitacaoAlienacao;

    @Invisivel
    @OneToMany(mappedBy = "loteAvaliacaoAlienacao")
    private List<ItemAvaliacaoAlienacao> itensAvaliacaoAlienacao;

    @Invisivel
    @Transient
    private List<VOItemSolicitacaoAlienacao> itensSolicitacao;

    @Transient
    private BigDecimal valorTotalLote;

    @ManyToOne
    private AvaliacaoAlienacao avaliacaoAlienacao;

    public LoteAvaliacaoAlienacao() {
        this.itensSolicitacaoAlienacao = Lists.newArrayList();
        this.itensAvaliacaoAlienacao = Lists.newArrayList();
        this.itensSolicitacao = Lists.newArrayList();
    }

    public List<ItemAvaliacaoAlienacao> getItensAvaliacaoAlienacao() {
        return itensAvaliacaoAlienacao;
    }

    public void setItensAvaliacaoAlienacao(List<ItemAvaliacaoAlienacao> itensAvaliacaoAlienacao) {
        this.itensAvaliacaoAlienacao = itensAvaliacaoAlienacao;
    }

    public List<VOItemSolicitacaoAlienacao> getItensSolicitacao() {
        return itensSolicitacao;
    }

    public void setItensSolicitacao(List<VOItemSolicitacaoAlienacao> itensSolicitacao) {
        this.itensSolicitacao = itensSolicitacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public List<ItemSolicitacaoAlienacao> getItensSolicitacaoAlienacao() {
        return itensSolicitacaoAlienacao;
    }

    public void setItensSolicitacaoAlienacao(List<ItemSolicitacaoAlienacao> itensSolicitacaoAlienacao) {
        this.itensSolicitacaoAlienacao = itensSolicitacaoAlienacao;
    }

    public BigDecimal getValorTotalLote() {
        return valorTotalLote;
    }

    public void setValorTotalLote(BigDecimal valorTotalLote) {
        this.valorTotalLote = valorTotalLote;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public AvaliacaoAlienacao getAvaliacaoAlienacao() {
        return avaliacaoAlienacao;
    }

    public void setAvaliacaoAlienacao(AvaliacaoAlienacao avaliacaoAlienacao) {
        this.avaliacaoAlienacao = avaliacaoAlienacao;
    }

    public BigDecimal getValorTotalArrematadoLote() {
        valorTotalLote = BigDecimal.ZERO;
        if (itensSolicitacaoAlienacao != null) {
            for (ItemSolicitacaoAlienacao item : itensSolicitacaoAlienacao) {
                valorTotalLote = valorTotalLote.add(item.getValorAvaliado());
            }
        }
        return valorTotalLote;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (numero != null) {
            retorno = numero.toString();
        }
        if (descricao != null) {
            retorno = retorno + " - " + descricao;
        }
        return retorno;
    }
}
