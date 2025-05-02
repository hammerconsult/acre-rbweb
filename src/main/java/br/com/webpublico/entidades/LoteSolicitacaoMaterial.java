/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Lote Solicitação Material")
public class LoteSolicitacaoMaterial extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Tabelavel
    @Obrigatorio
    private Integer numero;

    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;

    @Tabelavel
    private BigDecimal valor;

    @Etiqueta("Solicitação Material")
    @ManyToOne
    private SolicitacaoMaterial solicitacaoMaterial;

    @OneToMany(mappedBy = "loteSolicitacaoMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSolicitacao> itensSolicitacao;

    @OneToOne
    private LoteCotacao loteCotacao;

    private BigDecimal percentualDescontoMinimo;


    public LoteSolicitacaoMaterial() {
        super();
        this.itensSolicitacao = Lists.newArrayList();
        this.percentualDescontoMinimo = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public SolicitacaoMaterial getSolicitacaoMaterial() {
        return solicitacaoMaterial;
    }

    public void setSolicitacaoMaterial(SolicitacaoMaterial solicitacaoMaterial) {
        this.solicitacaoMaterial = solicitacaoMaterial;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ItemSolicitacao> getItensSolicitacao() {
        if (itensSolicitacao != null) {
            Collections.sort(itensSolicitacao, new Comparator<ItemSolicitacao>() {
                @Override
                public int compare(ItemSolicitacao o1, ItemSolicitacao o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
        }
        return itensSolicitacao;
    }

    public void ordenarItensSolicitacao() {
        Integer numero = null;
        if (solicitacaoMaterial.getCotacao().getFormularioCotacao().getTipoApuracaoLicitacao().isPorItem()) {
            for (LoteSolicitacaoMaterial lote : solicitacaoMaterial.getLoteSolicitacaoMaterials()) {
                for (ItemSolicitacao item : lote.getItensSolicitacao()) {
                    if (numero == null) {
                        numero = lote.getItensSolicitacao().indexOf(item);
                    }
                    numero++;
                    item.setNumero(numero);
                    Util.adicionarObjetoEmLista(lote.getItensSolicitacao(), item);
                }
            }
        } else {
            Iterator<ItemSolicitacao> itens = itensSolicitacao.iterator();
            while (itens.hasNext()) {
                ItemSolicitacao proximo = itens.next();
                int i = itensSolicitacao.indexOf(proximo);
                proximo.setNumero(i + 1);
                Util.adicionarObjetoEmLista(itensSolicitacao, proximo);
            }
        }
    }

    public void setItensSolicitacao(List<ItemSolicitacao> itensSolicitacao) {
        this.itensSolicitacao = itensSolicitacao;
    }

    public void somarValorTotal() {
        BigDecimal soma = BigDecimal.ZERO;
        if (getItensSolicitacao() != null) {
            for (ItemSolicitacao itemSolicitacao : getItensSolicitacao()) {
                soma = soma.add(itemSolicitacao.getPreco().multiply(itemSolicitacao.getQuantidade()));
            }
        }
        setValor(soma);
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasItens()) {
            for (ItemSolicitacao item : getItensSolicitacao()) {
                total = total.add(item.getPrecoTotal());
            }
        }
        return total;
    }

    @Override
    public String toString() {
        return "Lote Nº " + numero + " - " + descricao;
    }

    public boolean hasItens() {
        return this.getItensSolicitacao() != null && !this.getItensSolicitacao().isEmpty();
    }

    public LoteCotacao getLoteCotacao() {
        return loteCotacao;
    }

    public void setLoteCotacao(LoteCotacao loteCotacao) {
        this.loteCotacao = loteCotacao;
    }

    public BigDecimal getPercentualDescontoMinimo() {
        return percentualDescontoMinimo;
    }

    public void setPercentualDescontoMinimo(BigDecimal percentualDescontoMinimo) {
        this.percentualDescontoMinimo = percentualDescontoMinimo;
    }
}
