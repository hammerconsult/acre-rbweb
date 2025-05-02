/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Certame")
public class ItemCertame extends SuperEntidade implements Comparable<ItemCertame> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Certame certame;

    @OneToOne(mappedBy = "itemCertame", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel
    @Etiqueta("Item Certame ")
    private ItemCertameItemProcesso itemCertameItemProcesso;

    @Tabelavel
    @Etiqueta("Lote Certame")
    @OneToOne(mappedBy = "itemCertame", cascade = CascadeType.ALL, orphanRemoval = true)
    private ItemCertameLoteProcesso itemCertameLoteProcesso;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Situação")
    private SituacaoItemCertame situacaoItemCertame;

    @Etiqueta("Motivo")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private String motivo;

    public ItemCertame() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemCertameItemProcesso getItemCertameItemProcesso() {
        return itemCertameItemProcesso;
    }

    public void setItemCertameItemProcesso(ItemCertameItemProcesso itemCertameItemProcesso) {
        this.itemCertameItemProcesso = itemCertameItemProcesso;
    }

    public ItemCertameLoteProcesso getItemCertameLoteProcesso() {
        return itemCertameLoteProcesso;
    }

    public void setItemCertameLoteProcesso(ItemCertameLoteProcesso itemCertameLoteProcesso) {
        this.itemCertameLoteProcesso = itemCertameLoteProcesso;
    }

    public Certame getCertame() {
        return certame;
    }

    public void setCertame(Certame certame) {
        this.certame = certame;
    }

    public SituacaoItemCertame getSituacaoItemCertame() {
        return situacaoItemCertame;
    }

    public void setSituacaoItemCertame(SituacaoItemCertame situacaoItemCertame) {
        this.situacaoItemCertame = situacaoItemCertame;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public String toString() {
        if (itemCertameItemProcesso != null) {
            if (itemCertameItemProcesso.getItemPropostaVencedor() != null) {
                return "Nº : " + itemCertameItemProcesso.getItemPropostaVencedor().getItemProcessoDeCompra().getNumero() + " - " + itemCertameItemProcesso.getItemPropostaVencedor().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao();
            }
            if (itemCertameItemProcesso.getItemProcessoDeCompra() != null) {
                return "DESERTO - Nº :" + itemCertameItemProcesso.getItemProcessoDeCompra().getNumero() + " - " + itemCertameItemProcesso.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao();
            }
        }
        if (itemCertameLoteProcesso != null) {
            if (itemCertameLoteProcesso.getLotePropFornecedorVencedor() != null) {
                return "Nº : " + itemCertameLoteProcesso.getLotePropFornecedorVencedor().getLoteProcessoDeCompra().getNumero() + " - " + itemCertameLoteProcesso.getLotePropFornecedorVencedor().getLoteProcessoDeCompra().getDescricao();
            }
            if (itemCertameItemProcesso.getItemProcessoDeCompra() != null) {
                return "DESERTO - Nº : " + itemCertameLoteProcesso.getLoteProcessoDeCompra().getDescricao() + " - " + itemCertameLoteProcesso.getLoteProcessoDeCompra().getDescricao();
            }
        }
        return new String();
    }

    public Integer getNumeroLote() {
        return this.getItemCertameLoteProcesso().getNumeroLote();
    }

    public Integer getNumeroItem() {
        return this.getItemCertameItemProcesso().getNumeroItem();
    }

    public String getDescricaoItem() {
        try {
            return this.getItemCertameItemProcesso().getDescricaoItem();
        } catch (NullPointerException npe) {
            return this.getItemCertameLoteProcesso().getDescricaoLote();
        }
    }

    public Pessoa getFornecedor() {
        try {
            return this.getItemCertameItemProcesso().getFornecedor();
        } catch (NullPointerException npe) {
            return this.getItemCertameLoteProcesso().getFornecedor();
        }
    }

    public String getMarcaItem() {
        try {
            return this.getItemCertameItemProcesso().getMarcaItem();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public String getDescricaoProdutoItem() {
        try {
            return this.getItemCertameItemProcesso().getDescricaoProdutoItem();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public BigDecimal getQuantidadeItem() {
        try {
            return this.getItemCertameItemProcesso().getQuantidadeItem();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getPrecoItem() {
        try {
            BigDecimal preco = BigDecimal.ZERO;
            if (this.isTipoApuracaoPrecoItem()) {
                preco = this.getItemCertameItemProcesso().getPrecoItem();
            }
            if (this.isTipoApuracaoPrecoLote()) {
                preco = this.getItemCertameLoteProcesso().getValorTotal();
            }
            return preco != null ? preco : BigDecimal.ZERO;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotal() {
        try {
            if (this.isTipoApuracaoPrecoItem()) {
                return this.getItemCertameItemProcesso().getValorTotal();
            }
            if (this.isTipoApuracaoPrecoLote()) {
                return this.getItemCertameLoteProcesso().getValorTotal();
            }
            return BigDecimal.ZERO;
        } catch (NullPointerException npe) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getPercentualDesconto() {
        try {
            BigDecimal desconto = BigDecimal.ZERO;
            if (this.isTipoApuracaoPrecoItem()) {
                desconto = this.getItemCertameItemProcesso().getItemPropostaVencedor().getPercentualDesconto();
            }
            if (this.isTipoApuracaoPrecoLote()) {
                desconto = this.getLotePropostaFornecedorVencedor().getPercentualDesconto();
            }
            return desconto != null ? desconto : BigDecimal.ZERO;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public boolean isMaterial() {
        try {
            return this.getItemCertameItemProcesso().isMaterial();
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isServico() {
        try {
            return this.getItemCertameItemProcesso().isServico();
        } catch (Exception ex) {
            return false;
        }
    }

    public ObjetoCompra getObjetoCompra() {
        try {
            return this.getItemCertameItemProcesso().getObjetoCompra();
        } catch (Exception ex) {
            return null;
        }
    }

    public ServicoCompravel getServicoCompravel() {
        try {
            return this.getItemCertameItemProcesso().getServicoCompravel();
        } catch (Exception ex) {
            return null;
        }
    }

    public void setValorItem(BigDecimal precoProposto) {
        if (this.isTipoApuracaoPrecoItem()) {
            this.getItemCertameItemProcesso().setValorItem(precoProposto);
        }
        if (this.isTipoApuracaoPrecoLote()) {
            this.getItemCertameLoteProcesso().setValorLote(precoProposto);
        }
    }

    public ItemPropostaFornecedor getItemPropostaFornecedorVencedor() {
        return this.getItemCertameItemProcesso().getItemPropostaVencedor();
    }

    public LotePropostaFornecedor getLotePropostaFornecedorVencedor() {
        return this.getItemCertameLoteProcesso().getLotePropFornecedorVencedor();
    }

    public boolean isTipoApuracaoPrecoItem() {
        return this.getItemCertameItemProcesso() != null;
    }

    public boolean isTipoApuracaoPrecoLote() {
        return this.getItemCertameLoteProcesso() != null;
    }

    public boolean isSituacaoAlterada(ItemCertame itemCertame) {
        try {
            return itemCertame.getMotivo() != null;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isDesclassificado() {
        return SituacaoItemCertame.DESCLASSIFICADO.equals(this.getSituacaoItemCertame());
    }

    public boolean isInexequivel() {
        return SituacaoItemCertame.INEXEQUIVEL.equals(this.getSituacaoItemCertame());
    }

    public boolean isVencedor() {
        return SituacaoItemCertame.VENCEDOR.equals(this.getSituacaoItemCertame());
    }

    public boolean isClassificado() {
        return SituacaoItemCertame.CLASSIFICADO.equals(this.getSituacaoItemCertame());
    }

    public boolean isEmpate() {
        return SituacaoItemCertame.EMPATE.equals(this.getSituacaoItemCertame());
    }

    public boolean isFrustrado() {
        return SituacaoItemCertame.FRUSTRADO.equals(this.getSituacaoItemCertame());
    }

    public boolean isNaoCotado() {
        return SituacaoItemCertame.NAO_COTADO.equals(this.getSituacaoItemCertame());
    }

    @Override
    public int compareTo(ItemCertame o) {
        int comparacao = 0;
        if (itemCertameItemProcesso != null && o.getItemCertameItemProcesso() != null) {
            comparacao = itemCertameItemProcesso.getNumeroLote().compareTo(o.getItemCertameItemProcesso().getNumeroLote());

            if (comparacao == 0) {
                comparacao = itemCertameItemProcesso.getNumeroItem().compareTo(o.getItemCertameItemProcesso().getNumeroItem());
            }
        }
        if (itemCertameLoteProcesso != null && o.getItemCertameLoteProcesso() != null) {
            comparacao = itemCertameLoteProcesso.getNumeroLote().compareTo(o.getItemCertameLoteProcesso().getNumeroLote());
        }
        return comparacao;
    }
}
