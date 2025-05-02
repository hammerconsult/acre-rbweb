package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/09/14
 * Time: 09:14
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "ITEMMAPACOMTECPRE")
@Etiqueta("Item Mapa Comparativo Técnica e Preço")
public class ItemMapaComparativoTecnicaPreco extends SuperEntidade implements ValidadorEntidade, Comparable<ItemMapaComparativoTecnicaPreco> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Mapa Comparativo Técnica e Preço")
    @ManyToOne
    private MapaComparativoTecnicaPreco mapaComTecnicaPreco;

    @Etiqueta("Item Mapa Comparativo Técnica e Preço Item Processo")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemMapaComTecnicaPreco")
    private ItemMapaComparativoTecnicaPrecoItemProcesso itemMapaComparativoTecnicaPrecoItemProcesso;

    @Etiqueta("Item Mapa Comparativo Técnica e Preço Lote Processo")
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "itemMapaComTecnicaPreco")
    private ItemMapaComparativoTecnicaPrecoLoteProcesso itemMapaComparativoTecnicaPrecoLoteProcesso;

    @Etiqueta("Situação Item Mapa Comparativo Técnica e Preço")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private SituacaoItemCertame situacaoItem;

    @Etiqueta("Justificativa")
    private String justificativa;

    public ItemMapaComparativoTecnicaPreco() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MapaComparativoTecnicaPreco getMapaComTecnicaPreco() {
        return mapaComTecnicaPreco;
    }

    public void setMapaComTecnicaPreco(MapaComparativoTecnicaPreco mapaComTecnicaPreco) {
        this.mapaComTecnicaPreco = mapaComTecnicaPreco;
    }

    public ItemMapaComparativoTecnicaPrecoItemProcesso getItemMapaComparativoTecnicaPrecoItemProcesso() {
        return itemMapaComparativoTecnicaPrecoItemProcesso;
    }

    public void setItemMapaComparativoTecnicaPrecoItemProcesso(ItemMapaComparativoTecnicaPrecoItemProcesso itemMapaComparativoTecnicaPrecoItemProcesso) {
        this.itemMapaComparativoTecnicaPrecoItemProcesso = itemMapaComparativoTecnicaPrecoItemProcesso;
    }

    public ItemMapaComparativoTecnicaPrecoLoteProcesso getItemMapaComparativoTecnicaPrecoLoteProcesso() {
        return itemMapaComparativoTecnicaPrecoLoteProcesso;
    }

    public void setItemMapaComparativoTecnicaPrecoLoteProcesso(ItemMapaComparativoTecnicaPrecoLoteProcesso itemMapaComparativoTecnicaPrecoLoteProcesso) {
        this.itemMapaComparativoTecnicaPrecoLoteProcesso = itemMapaComparativoTecnicaPrecoLoteProcesso;
    }

    public SituacaoItemCertame getSituacaoItem() {
        return situacaoItem;
    }

    public void setSituacaoItem(SituacaoItemCertame situacaoItem) {
        this.situacaoItem = situacaoItem;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public Integer getNumeroLote() {
        if (hasItemMapaItemProcesso()) {
            return getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero();
        }
        return getLoteProcessoDeCompra().getNumero();
    }

    public Integer getNumeroItem() {
        if (hasItemMapaItemProcesso()) {
            return getItemProcessoDeCompra().getNumero();
        }
        return getLoteProcessoDeCompra().getNumero();
    }

    public String getDescricao() {
        if (hasItemMapaItemProcesso()) {
            return getItemProcessoDeCompra().getDescricao();
        }
        return getLoteProcessoDeCompra().getDescricao();
    }

    public String getMarcaItemPropostaFornecedor() {
        try {
            return itemMapaComparativoTecnicaPrecoItemProcesso.getItemPropostaVencedor().getMarca();
        } catch (Exception ex) {
            return "";
        }
    }

    public BigDecimal getQuantidadeItemProcessoDeCompra() {
        try {
            return getItemProcessoDeCompra().getQuantidade();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValor() {
        if (hasItemMapaItemProcesso()) {
            return getItemPropostaFornecedor().getPreco();
        }
        return getLotePropostaFornecedor().getValor();
    }

    public ItemPropostaFornecedor getItemPropostaFornecedor() {
        try {
            return itemMapaComparativoTecnicaPrecoItemProcesso.getItemPropostaVencedor();
        } catch (Exception ex) {
            return null;
        }
    }

    public LotePropostaFornecedor getLotePropostaFornecedor() {
        return itemMapaComparativoTecnicaPrecoLoteProcesso.getLotePropostaVencedor();
    }

    public boolean hasItemMapaItemProcesso() {
        return itemMapaComparativoTecnicaPrecoItemProcesso != null;
    }

    public boolean hasItemMapaLoteProcesso() {
        return itemMapaComparativoTecnicaPrecoLoteProcesso != null;
    }

    public boolean hasJustificativa() {
        return justificativa != null && !justificativa.trim().isEmpty();
    }

    @Override
    public String toString() {
        return itemMapaComparativoTecnicaPrecoItemProcesso.getItemProcessoDeCompra().getDescricao();
    }

    @Override
    public int compareTo(ItemMapaComparativoTecnicaPreco o) {
        int comparacao = 0;
        if (mapaComTecnicaPreco.getLicitacao().isTipoApuracaoPrecoItem()) {
            comparacao = itemMapaComparativoTecnicaPrecoItemProcesso.getItemPropostaVencedor().getNumeroLote().compareTo(o.getItemMapaComparativoTecnicaPrecoItemProcesso().getItemPropostaVencedor().getNumeroLote());

            if (comparacao == 0) {
                comparacao = itemMapaComparativoTecnicaPrecoItemProcesso.getItemPropostaVencedor().getNumeroItem().compareTo(o.getItemMapaComparativoTecnicaPrecoItemProcesso().getItemPropostaVencedor().getNumeroItem());
            }
        }
        return comparacao;
    }

    public void definirNotaPrecoPorItem() {
        PropostaFornecedor propostaDoItem = getItemPropostaFornecedor().getPropostaFornecedor();

        ItemPropostaFornecedor itemDeMenorPropostaPorItemProcesso = propostaDoItem.getItemDeMenorPropostaPorItemProcessoDeCompra(getItemProcessoDeCompra());
        if (itemDeMenorPropostaPorItemProcesso != null) {
            itemDeMenorPropostaPorItemProcesso.setNotaPreco(getNotaMaximaInformadaNaSolicitacao());

            if (getItemPropostaFornecedor().hasPreco()) {
                BigDecimal precoDaMenorPropostaDivididoPeloPrecoDoItemEmExame = itemDeMenorPropostaPorItemProcesso.getPreco().divide(getItemPropostaFornecedor().getPreco(), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal notaPreco = precoDaMenorPropostaDivididoPeloPrecoDoItemEmExame.multiply(getNotaMaximaInformadaNaSolicitacao());
                getItemPropostaFornecedor().setNotaPreco(notaPreco);
            }
        } else {
            getItemPropostaFornecedor().setNotaPreco(BigDecimal.ZERO);
        }
    }

    public void definirNotaPrecoPorLote() {
        getLotePropostaFornecedor().setNotaPreco(BigDecimal.ZERO);
        PropostaFornecedor propostaDoLote = getLotePropostaFornecedor().getPropostaFornecedor();
        LotePropostaFornecedor loteDeMenorPropostaPorLoteProcesso = propostaDoLote.getLoteDeMenorPropostaPorLoteProcessoDeCompra(getLoteProcessoDeCompra());
        if (loteDeMenorPropostaPorLoteProcesso != null) {
            loteDeMenorPropostaPorLoteProcesso.setNotaPreco(getNotaMaximaInformadaNaSolicitacao());

            if (getLotePropostaFornecedor().equals(loteDeMenorPropostaPorLoteProcesso)) {
                getLotePropostaFornecedor().setNotaPreco(getNotaMaximaInformadaNaSolicitacao());
            } else {
                if (getLotePropostaFornecedor().hasValor()) {
                    BigDecimal valorDaMenorPropostaDivididoPeloValorDoLoteEmExame = loteDeMenorPropostaPorLoteProcesso.getValor().divide(getLotePropostaFornecedor().getValor(), 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal notaPreco = valorDaMenorPropostaDivididoPeloValorDoLoteEmExame.multiply(getNotaMaximaInformadaNaSolicitacao());
                    getLotePropostaFornecedor().setNotaPreco(notaPreco);
                }
            }
        }
    }

    public void definirNotaClassificacaoFinalPorItemProposta(ItemPropostaFornecedor itemProposta, BigDecimal indiceTecnico) {
        BigDecimal valorFinalTecnica = indiceTecnico.multiply(getPesoTecnica());

        BigDecimal valorFinalPreco = itemProposta.getNotaPreco().multiply(getPesoPreco());

        itemProposta.setNotaClassificacaoFinal(valorFinalTecnica.add(valorFinalPreco));
    }

    public void definirNotaClassificacaoFinalPorLoteProposta(BigDecimal indiceTecnico) {
        BigDecimal pesoTecnica = getPesoTecnica();

        BigDecimal notaPreco = getLotePropostaFornecedor().getNotaPreco();
        BigDecimal pesoPreco = getPesoPreco();

        BigDecimal indiceTecnicoMultiplicadoPeloPesoTecnica = indiceTecnico.multiply(pesoTecnica);
        BigDecimal notaPrecoMultiplicadoPeloPesoPreco = notaPreco.multiply(pesoPreco);

        BigDecimal notaClassificacaoFinal = indiceTecnicoMultiplicadoPeloPesoTecnica.add(notaPrecoMultiplicadoPeloPesoPreco);
        getLotePropostaFornecedor().setNotaClassificacaoFinal(notaClassificacaoFinal);
    }

    public BigDecimal getNotaMaximaInformadaNaSolicitacao() {
        BigDecimal notaMaxima = BigDecimal.ZERO;
        try {
            for (LoteProcessoDeCompra loteProcesso : mapaComTecnicaPreco.getLicitacao().getProcessoDeCompra().getLotesProcessoDeCompra()) {
                for (ItemProcessoDeCompra itemProcesso : loteProcesso.getItensProcessoDeCompra()) {
                    notaMaxima = itemProcesso.getItemSolicitacaoMaterial().getLoteSolicitacaoMaterial().getSolicitacaoMaterial().getCriterioPrecoSolicitacao().getNota();
                    break;
                }
            }
            return notaMaxima;
        } catch (Exception ex) {
            return notaMaxima;
        }
    }

    public BigDecimal getPesoTecnica() {
        for (LoteProcessoDeCompra loteProcessoDeCompra : mapaComTecnicaPreco.getLicitacao().getProcessoDeCompra().getLotesProcessoDeCompra()) {
            for (ItemProcessoDeCompra itemProcessoDeCompra : loteProcessoDeCompra.getItensProcessoDeCompra()) {
                return itemProcessoDeCompra.getItemSolicitacaoMaterial().getLoteSolicitacaoMaterial().getSolicitacaoMaterial().getClassificacaoFinalSolicitacao().getPesoTecnica();
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getPesoPreco() {
        for (LoteProcessoDeCompra loteProcessoDeCompra : mapaComTecnicaPreco.getLicitacao().getProcessoDeCompra().getLotesProcessoDeCompra()) {
            for (ItemProcessoDeCompra itemProcessoDeCompra : loteProcessoDeCompra.getItensProcessoDeCompra()) {
                return itemProcessoDeCompra.getItemSolicitacaoMaterial().getLoteSolicitacaoMaterial().getSolicitacaoMaterial().getClassificacaoFinalSolicitacao().getPesoPreco();
            }
        }
        return BigDecimal.ZERO;
    }

    public ItemProcessoDeCompra getItemProcessoDeCompra() {
        return itemMapaComparativoTecnicaPrecoItemProcesso.getItemProcessoDeCompra();
    }

    public LoteProcessoDeCompra getLoteProcessoDeCompra() {
        return itemMapaComparativoTecnicaPrecoLoteProcesso.getLoteProcessoDeCompra();
    }

    public boolean hasPrecoPropostoMaiorQueReservado() {
        try {
            if (mapaComTecnicaPreco.getLicitacao().isTipoApuracaoPrecoItem()) {
                ItemProcessoDeCompra itemProcesso = getItemProcessoDeCompra();
                return getItemPropostaFornecedor().getPreco().compareTo(itemProcesso.getItemSolicitacaoMaterial().getPreco()) > 0;
            }
            if (mapaComTecnicaPreco.getLicitacao().isTipoApuracaoPrecoLote()) {
                LoteProcessoDeCompra loteProcesso = getLoteProcessoDeCompra();
                return getLotePropostaFornecedor().getValor().compareTo(loteProcesso.getValor()) > 0;
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean hasPreco() {
        if (mapaComTecnicaPreco.getLicitacao().isTipoApuracaoPrecoItem()) {
            return itemMapaComparativoTecnicaPrecoItemProcesso.getItemPropostaVencedor().hasPreco();
        }
        if (mapaComTecnicaPreco.getLicitacao().isTipoApuracaoPrecoLote()) {
            return itemMapaComparativoTecnicaPrecoLoteProcesso.getLotePropostaVencedor().hasValor();
        }
        return false;
    }

    public boolean isSituacao(SituacaoItemCertame situacao) {
        return situacao.equals(this.situacaoItem);
    }

    public BigDecimal getNotaPreco() {
        if (hasItemMapaItemProcesso()) {
            return itemMapaComparativoTecnicaPrecoItemProcesso.getItemPropostaVencedor().getNotaPreco();
        }
        return itemMapaComparativoTecnicaPrecoLoteProcesso.getLotePropostaVencedor().getNotaPreco();
    }

    public BigDecimal getNotaClassificacaoFinal() {
        if (hasItemMapaItemProcesso()) {
            return itemMapaComparativoTecnicaPrecoItemProcesso.getItemPropostaVencedor().getNotaClassificacaoFinal();
        }
        return itemMapaComparativoTecnicaPrecoLoteProcesso.getLotePropostaVencedor().getNotaClassificacaoFinal();
    }

    public BigDecimal getPrecoItem() {
        if (hasItemMapaItemProcesso()) {
            return getItemPropostaFornecedor().getPreco();
        }
        return itemMapaComparativoTecnicaPrecoLoteProcesso.getLotePropostaVencedor().getValor();
    }

    public BigDecimal getPrecoReservado() {
        if (hasItemMapaItemProcesso()) {
            return getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco();
        }
        return getLoteProcessoDeCompra().getValor();
    }

    public PropostaFornecedor getPropostaFornecedor() {
        if (hasItemMapaItemProcesso()) {
            return getItemPropostaFornecedor().getPropostaFornecedor();
        }
        return getLotePropostaFornecedor().getPropostaFornecedor();
    }
}
