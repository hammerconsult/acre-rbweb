/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import br.com.webpublico.enums.StatusLancePregao;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author renato
 */
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Entity

public class ItemProcessoDeCompra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    private Integer numero;

    @OneToOne
    @Tabelavel
    @Etiqueta("Item Solicitação ")
    @Pesquisavel
    private ItemSolicitacao itemSolicitacaoMaterial;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Lote")
    @Pesquisavel
    private LoteProcessoDeCompra loteProcessoDeCompra;

    private BigDecimal quantidade;

    @OneToMany(mappedBy = "itemProcessoDeCompra")
    private List<ItemPropostaFornecedor> itensPropostaFornecedor;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação do Item de Compra")
    private SituacaoItemProcessoDeCompra situacaoItemProcessoDeCompra;

    @Etiqueta("Sequencial criado pelo PNCP ao realizar o envio do resultado do item de uma contratação")
    private String sequencialPncp;

    @Transient
    @Invisivel
    private SituacaoItemCertame situacaoItemCertame;
    @Transient
    @Invisivel
    private StatusLancePregao statusLancePregao;

    public ItemProcessoDeCompra() {
        super();
        this.quantidade = BigDecimal.ZERO;
    }

    public StatusLancePregao getStatusLancePregao() {
        return statusLancePregao;
    }

    public void setStatusLancePregao(StatusLancePregao statusLancePregao) {
        this.statusLancePregao = statusLancePregao;
    }

    public SituacaoItemCertame getSituacaoItemCertame() {
        return situacaoItemCertame;
    }

    public void setSituacaoItemCertame(SituacaoItemCertame situacaoItemCertame) {
        this.situacaoItemCertame = situacaoItemCertame;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemPropostaFornecedor> getItensPropostaFornecedor() {
        Comparator<ItemPropostaFornecedor> comparator = new Comparator<ItemPropostaFornecedor>() {
            @Override
            public int compare(ItemPropostaFornecedor o1, ItemPropostaFornecedor o2) {
                return o1.getFornecedor().getNome().compareTo(o2.getFornecedor().getNome());
            }
        };
        Collections.sort(itensPropostaFornecedor, comparator);
        return itensPropostaFornecedor;
    }

    public void setItensPropostaFornecedor(List<ItemPropostaFornecedor> itensPropostaFornecedor) {
        this.itensPropostaFornecedor = itensPropostaFornecedor;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public ItemSolicitacao getItemSolicitacaoMaterial() {
        return itemSolicitacaoMaterial;
    }

    public void setItemSolicitacaoMaterial(ItemSolicitacao itemSolicitacaoMaterial) {
        this.itemSolicitacaoMaterial = itemSolicitacaoMaterial;
    }

    public LoteProcessoDeCompra getLoteProcessoDeCompra() {
        return loteProcessoDeCompra;
    }

    public void setLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        this.loteProcessoDeCompra = loteProcessoDeCompra;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return itemSolicitacaoMaterial.getDescricao();
    }

    public ObjetoCompra getObjetoCompra() {
        return this.getItemSolicitacaoMaterial().getObjetoCompra();
    }

    public UnidadeMedida getUnidadeMedida() {
        return getItemSolicitacaoMaterial().getUnidadeMedida();
    }

    public String getDescricao() {
        return this.getItemSolicitacaoMaterial().getDescricao();
    }

    public String getDescricaoComplementar() {
        return this.getItemSolicitacaoMaterial().getDescricaoComplementar();
    }

    public boolean isMaterial() {
        return this.getItemSolicitacaoMaterial().isMaterial();
    }

    public boolean isServico() {
        return this.getItemSolicitacaoMaterial().isServico();
    }

    public ServicoCompravel getServicoCompravel() {
        return this.getItemSolicitacaoMaterial().getServicoCompravel();
    }

    public SituacaoItemProcessoDeCompra getSituacaoItemProcessoDeCompra() {
        return situacaoItemProcessoDeCompra;
    }

    public void setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra situacaoItemProcessoDeCompra) {
        this.situacaoItemProcessoDeCompra = situacaoItemProcessoDeCompra;
    }

    public Boolean isAguardando() {
        return SituacaoItemProcessoDeCompra.AGUARDANDO.equals(this.situacaoItemProcessoDeCompra);
    }

    public String getSequencialPncp() {
        return sequencialPncp;
    }

    public void setSequencialPncp(String sequencialPncp) {
        this.sequencialPncp = sequencialPncp;
    }

    public Integer getNumeroLote() {
        return this.getLoteProcessoDeCompra().getNumero();
    }

    public String getNumeroDescricao() {
        return getNumero() + " - " + getDescricao();
    }

    public TipoControleLicitacao getTipoControle() {
        try {
            return getItemSolicitacaoMaterial().getItemCotacao().getTipoControle();
        } catch (NullPointerException e) {
            return TipoControleLicitacao.QUANTIDADE;
        }
    }

}
