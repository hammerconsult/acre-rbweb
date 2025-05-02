/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
public class LoteProcessoDeCompra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    private Integer numero;

    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;

    @ManyToOne
    private LoteSolicitacaoMaterial loteSolicitacaoMaterial;
    @Pesquisavel
    private BigDecimal valor;

    @Etiqueta("Processo de Compra")
    @ManyToOne
    private ProcessoDeCompra processoDeCompra;

    @OneToMany(mappedBy = "loteProcessoDeCompra", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemProcessoDeCompra> itensProcessoDeCompra;

    @OneToMany(mappedBy = "loteProcessoDeCompra")
    private List<LotePropostaFornecedor> lotesProposta;

    public LoteProcessoDeCompra() {
        super();
        itensProcessoDeCompra = Lists.newArrayList();
        lotesProposta = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ProcessoDeCompra getProcessoDeCompra() {
        return processoDeCompra;
    }

    public void setProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        this.processoDeCompra = processoDeCompra;
    }

    public List<ItemProcessoDeCompra> getItensProcessoDeCompra() {
        if (itensProcessoDeCompra != null && !itensProcessoDeCompra.isEmpty()) {
            Collections.sort(itensProcessoDeCompra, new Comparator<ItemProcessoDeCompra>() {
                @Override
                public int compare(ItemProcessoDeCompra o1, ItemProcessoDeCompra o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
        }
        return itensProcessoDeCompra;
    }

    public void setItensProcessoDeCompra(List<ItemProcessoDeCompra> itensProcessoDeCompra) {
        this.itensProcessoDeCompra = itensProcessoDeCompra;
    }

    public LoteSolicitacaoMaterial getLoteSolicitacaoMaterial() {
        return loteSolicitacaoMaterial;
    }

    public void setLoteSolicitacaoMaterial(LoteSolicitacaoMaterial loteSolicitacaoMaterial) {
        this.loteSolicitacaoMaterial = loteSolicitacaoMaterial;
    }

    public List<LotePropostaFornecedor> getLotesProposta() {
        if (lotesProposta != null) {
            Collections.sort(lotesProposta, new Comparator<LotePropostaFornecedor>() {
                @Override
                public int compare(LotePropostaFornecedor o1, LotePropostaFornecedor o2) {
                    return o1.getNumeroLote().compareTo(o2.getNumeroLote());
                }
            });
        }
        return lotesProposta;
    }

    public void setLotesProposta(List<LotePropostaFornecedor> lotesProposta) {
        this.lotesProposta = lotesProposta;
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (!itensProcessoDeCompra.isEmpty()) {
            for (ItemProcessoDeCompra item : itensProcessoDeCompra) {
                total = total.add(item.getItemSolicitacaoMaterial().getPrecoTotal());
            }
        }
        return total;
    }

    @Override
    public String toString() {
        if (descricao == null) {
            return this.numero + "";
        }
        return this.numero + " - " + descricao;
    }
}
