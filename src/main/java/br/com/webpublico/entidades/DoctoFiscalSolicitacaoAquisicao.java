package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.contabil.LiquidacaoVO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@Audited
@Etiqueta("Documento Fiscal Solicitação de Aquisição")
@Table(name = "DOCTOFISCALSOLICAQUISICAO")
public class DoctoFiscalSolicitacaoAquisicao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Solicitação de Aquisição")
    private SolicitacaoAquisicao solicitacaoAquisicao;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Documento Fiscal")
    private DoctoFiscalLiquidacao documentoFiscal;

    @Invisivel
    @OneToMany(mappedBy = "doctoFiscalSolicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemDoctoItemAquisicao> itens;

    //Utilizado na tela de requisição de compra, necessário para saber 'situação' (Liquidado, Aguardando Liquidação)
    @Transient
    private List<LiquidacaoVO> liquidacoesVOs;

    public DoctoFiscalSolicitacaoAquisicao() {
        itens = Lists.newArrayList();
        documentoFiscal = new DoctoFiscalLiquidacao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoAquisicao getSolicitacaoAquisicao() {
        return solicitacaoAquisicao;
    }

    public void setSolicitacaoAquisicao(SolicitacaoAquisicao solicitacaoAquisicao) {
        this.solicitacaoAquisicao = solicitacaoAquisicao;
    }

    public DoctoFiscalLiquidacao getDocumentoFiscal() {
        return documentoFiscal;
    }

    public void setDocumentoFiscal(DoctoFiscalLiquidacao documentoFiscal) {
        this.documentoFiscal = documentoFiscal;
    }

    public List<ItemDoctoItemAquisicao> getItens() {
        return itens;
    }

    public void setItens(List<ItemDoctoItemAquisicao> itens) {
        this.itens = itens;
    }

    public List<LiquidacaoVO> getLiquidacoesVOs() {
        return liquidacoesVOs;
    }

    public void setLiquidacoesVOs(List<LiquidacaoVO> liquidacoesVOs) {
        this.liquidacoesVOs = liquidacoesVOs;
    }

    public BigDecimal getQuantidadeTotal() {
        BigDecimal qtde = BigDecimal.ZERO;
        for (ItemDoctoItemAquisicao item : getItens()) {
            qtde = qtde.add(item.getItemDoctoFiscalLiquidacao().getQuantidade());
        }
        return qtde;
    }

    public BigDecimal getValorTotal() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemDoctoItemAquisicao item : itens) {
            if (item.getItemDoctoFiscalLiquidacao().hasQuantidade() && item.getItemDoctoFiscalLiquidacao().hasValorUnitario()) {
                valorTotal = valorTotal.add(item.getItemDoctoFiscalLiquidacao().getQuantidade().multiply(item.getItemDoctoFiscalLiquidacao().getValorUnitario()));
            }
        }
        return valorTotal.setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal getValorTotalItensSelecionados() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemDoctoItemAquisicao item : itens) {
            if (item.getSelecionado()
                && item.getItemDoctoFiscalLiquidacao().hasQuantidade()
                && item.getItemDoctoFiscalLiquidacao().hasValorUnitario()) {
                valorTotal = valorTotal.add(item.getItemDoctoFiscalLiquidacao().getQuantidade().multiply(item.getItemDoctoFiscalLiquidacao().getValorUnitario()));
            }
        }
        return valorTotal.setScale(2, RoundingMode.HALF_EVEN);
    }

    @Override
    public String toString() {
        try {
            return documentoFiscal.toString();
        } catch (NullPointerException ne) {
            return "";
        }
    }
}
