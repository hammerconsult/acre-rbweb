/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Lote Proposta Fornecedor")
@Table(name = "LOTEPROPFORNEC")
public class LotePropostaFornecedor extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LoteProcessoDeCompra loteProcessoDeCompra;

    @ManyToOne
    private PropostaFornecedor propostaFornecedor;

    private BigDecimal valor;
    @OneToMany(mappedBy = "lotePropostaFornecedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ItemPropostaFornecedor> itens;

    @Etiqueta("Nota Preço")
    private BigDecimal notaPreco;
    @Etiqueta("Nota Classificação Final")
    private BigDecimal notaClassificacaoFinal;
    @Transient
    @Invisivel
    private Boolean selecionado;
    @Etiqueta("Percentual Desconto (%)")
    private BigDecimal percentualDesconto;

    public LotePropostaFornecedor() {
        super();
        percentualDesconto = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteProcessoDeCompra getLoteProcessoDeCompra() {
        return loteProcessoDeCompra;
    }

    public void setLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        this.loteProcessoDeCompra = loteProcessoDeCompra;
    }

    public PropostaFornecedor getPropostaFornecedor() {
        return propostaFornecedor;
    }

    public void setPropostaFornecedor(PropostaFornecedor propostaFornecedor) {
        this.propostaFornecedor = propostaFornecedor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ItemPropostaFornecedor> getItens() {
        if (itens != null) {
            Collections.sort(itens, new Comparator<ItemPropostaFornecedor>() {
                @Override
                public int compare(ItemPropostaFornecedor o1, ItemPropostaFornecedor o2) {
                    return o1.getNumeroLote().compareTo(o2.getNumeroLote());
                }
            });
        }
        return itens;
    }

    public void setItens(List<ItemPropostaFornecedor> itens) {
        this.itens = itens;
    }

    public BigDecimal getNotaPreco() {
        return notaPreco;
    }

    public void setNotaPreco(BigDecimal notaPreco) {
        this.notaPreco = notaPreco;
    }

    public BigDecimal getNotaClassificacaoFinal() {
        return notaClassificacaoFinal;
    }

    public void setNotaClassificacaoFinal(BigDecimal notaClassificacaoFinal) {
        this.notaClassificacaoFinal = notaClassificacaoFinal;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public Integer getNumeroLote() {
        return this.getLoteProcessoDeCompra().getNumero();
    }

    public String getDescricaoLote() {
        return this.getLoteProcessoDeCompra().getDescricao();
    }

    public Pessoa getFornecedor() {
        return this.getPropostaFornecedor().getFornecedor();
    }

    public Boolean getSelecionado() {
        return selecionado == null ? Boolean.FALSE : selecionado;
    }

    public void setSelecionado(Boolean selecionado) {
        this.selecionado = selecionado;
    }

    public void anularNotas() {
        for (ItemPropostaFornecedor item : itens) {
            item.setNotaPreco(null);
            item.setNotaClassificacaoFinal(null);
        }
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasItens()) {
            for (ItemPropostaFornecedor item : itens) {
                total = total.add(item.getPrecoTotal());
            }
        }
        return total.setScale(2, RoundingMode.HALF_EVEN);
    }

    public boolean hasValor() {
        return valor != null && valor.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasItens() {
        return itens != null && !itens.isEmpty();
    }

    @Override
    public String toString() {
        return loteProcessoDeCompra.toString();
    }
}
