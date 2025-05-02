package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/09/14
 * Time: 08:56
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "MAPACOMTECPREC")
@Etiqueta("Mapa Comparativo Técnica e Preço")
public class MapaComparativoTecnicaPreco extends SuperEntidade implements ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date data;

    @Etiqueta("Licitação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private Licitacao licitacao;

    @Etiqueta("Itens Mapa Comparativo Tecnica e Preço")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "mapaComTecnicaPreco")
    private List<ItemMapaComparativoTecnicaPreco> itens = Lists.newArrayList();

    public MapaComparativoTecnicaPreco() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public List<ItemMapaComparativoTecnicaPreco> getItens() {
        if (itens != null) {
            Collections.sort(itens, new Comparator<ItemMapaComparativoTecnicaPreco>() {
                @Override
                public int compare(ItemMapaComparativoTecnicaPreco o1, ItemMapaComparativoTecnicaPreco o2) {
                    return o1.getNumeroItem().compareTo(o2.getNumeroItem());
                }
            });
        }
        return itens;
    }

    public void setItens(List<ItemMapaComparativoTecnicaPreco> itens) {
        this.itens = itens;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public void limparItens() {
        itens.clear();
    }

    public void ordenarItensPorClassificacaoFinalDesc(List<ItemMapaComparativoTecnicaPreco> itensParaOrdenacao) {
        Comparator<ItemMapaComparativoTecnicaPreco> comparator = new Comparator<ItemMapaComparativoTecnicaPreco>() {
            @Override
            public int compare(ItemMapaComparativoTecnicaPreco o1, ItemMapaComparativoTecnicaPreco o2) {
                if (o1.hasItemMapaItemProcesso() && o2.hasItemMapaItemProcesso()) {
                    return o2.getItemPropostaFornecedor().getNotaClassificacaoFinal().compareTo(o1.getItemPropostaFornecedor().getNotaClassificacaoFinal());
                } else {
                    return o2.getLotePropostaFornecedor().getNotaClassificacaoFinal().compareTo(o1.getLotePropostaFornecedor().getNotaClassificacaoFinal());
                }
            }
        };
        Collections.sort(itensParaOrdenacao, comparator);
    }

    public List<ItemMapaComparativoTecnicaPreco> getItensMapaPorItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        List<ItemMapaComparativoTecnicaPreco> itensMapaPorItemProcessoDeCompra = Lists.newArrayList();
        for (ItemMapaComparativoTecnicaPreco item : this.itens) {
            if (item.hasItemMapaItemProcesso() && item.getItemMapaComparativoTecnicaPrecoItemProcesso().getItemProcessoDeCompra().equals(itemProcessoDeCompra)) {
                itensMapaPorItemProcessoDeCompra.add(item);
            }
        }
        return itensMapaPorItemProcessoDeCompra;
    }

    public List<ItemMapaComparativoTecnicaPreco> getItensMapaPorLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        List<ItemMapaComparativoTecnicaPreco> itensMapaPorLoteProcessoDeCompra = Lists.newArrayList();
        for (ItemMapaComparativoTecnicaPreco itemMapa : this.itens) {
            if (itemMapa.hasItemMapaLoteProcesso() && itemMapa.getItemMapaComparativoTecnicaPrecoLoteProcesso().getLoteProcessoDeCompra().equals(loteProcessoDeCompra)) {
                itensMapaPorLoteProcessoDeCompra.add(itemMapa);
            }
        }
        return itensMapaPorLoteProcessoDeCompra;
    }

    public ItemMapaComparativoTecnicaPreco getItemComMaiorClassificacaoFinalPorItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        List<ItemMapaComparativoTecnicaPreco> itensMapa = getItensMapaPorItemProcessoDeCompra(itemProcessoDeCompra);
        ordenarItensPorClassificacaoFinalDesc(itensMapa);
        return itensMapa.get(0);
    }

    public ItemMapaComparativoTecnicaPreco getItemComMaiorClassificacaoFinalPorLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        List<ItemMapaComparativoTecnicaPreco> itensMapa = getItensMapaPorLoteProcessoDeCompra(loteProcessoDeCompra);
        ordenarItensPorClassificacaoFinalDesc(itensMapa);
        return itensMapa.get(0);
    }

    public List<ItemProcessoDeCompra> getItensProcessoDeCompra() {
        List<ItemProcessoDeCompra> itensProcessoDeCompra = Lists.newArrayList();
        for (ItemMapaComparativoTecnicaPreco item : itens) {
            if (!itensProcessoDeCompra.contains(item.getItemProcessoDeCompra())) {
                itensProcessoDeCompra.add(item.getItemProcessoDeCompra());
            }
        }
        return itensProcessoDeCompra;
    }

    public List<LoteProcessoDeCompra> getLotesProcessoDeCompra() {
        List<LoteProcessoDeCompra> lotesProcessoDeCompra = Lists.newArrayList();
        for (ItemMapaComparativoTecnicaPreco itemMapa : itens) {
            if (!lotesProcessoDeCompra.contains(itemMapa.getLoteProcessoDeCompra())) {
                lotesProcessoDeCompra.add(itemMapa.getLoteProcessoDeCompra());
            }
        }
        return lotesProcessoDeCompra;
    }

    public List<PropostaFornecedor> getPropostas() {
        List<PropostaFornecedor> propostas = Lists.newArrayList();
        for (ItemMapaComparativoTecnicaPreco item : itens) {
            if (licitacao.isTipoApuracaoPrecoItem() && !propostas.contains(item.getItemPropostaFornecedor().getPropostaFornecedor())) {
                propostas.add(item.getItemPropostaFornecedor().getPropostaFornecedor());
            }
            if (licitacao.isTipoApuracaoPrecoLote() && !propostas.contains(item.getLotePropostaFornecedor().getPropostaFornecedor())) {
                propostas.add(item.getLotePropostaFornecedor().getPropostaFornecedor());
            }
        }
        return propostas;
    }

    public void criarItemPorItemProposta(ItemPropostaFornecedor itemProposta, BigDecimal indiceTecnico) {
        ItemMapaComparativoTecnicaPreco itemMapa = new ItemMapaComparativoTecnicaPreco();
        itemMapa.setMapaComTecnicaPreco(this);
        ItemMapaComparativoTecnicaPrecoItemProcesso itemMapaItemProcesso = new ItemMapaComparativoTecnicaPrecoItemProcesso();
        itemMapaItemProcesso.setItemMapaComTecnicaPreco(itemMapa);
        itemMapaItemProcesso.setItemPropostaVencedor(itemProposta);
        itemMapaItemProcesso.setItemProcessoDeCompra(itemProposta.getItemProcessoDeCompra());
        itemMapa.setItemMapaComparativoTecnicaPrecoItemProcesso(itemMapaItemProcesso);

        itemMapa.definirNotaPrecoPorItem();
        itemMapa.definirNotaClassificacaoFinalPorItemProposta(itemProposta, indiceTecnico);
        adicionarItem(itemMapa);
    }

    public void criarItemPorLoteProposta(LotePropostaFornecedor loteProposta, BigDecimal indiceTecnico) {
        ItemMapaComparativoTecnicaPreco itemMapa = new ItemMapaComparativoTecnicaPreco();
        itemMapa.setMapaComTecnicaPreco(this);
        ItemMapaComparativoTecnicaPrecoLoteProcesso itemMapaLoteProcesso = new ItemMapaComparativoTecnicaPrecoLoteProcesso();
        itemMapaLoteProcesso.setItemMapaComTecnicaPreco(itemMapa);
        itemMapaLoteProcesso.setLotePropostaVencedor(loteProposta);
        itemMapaLoteProcesso.setLoteProcessoDeCompra(loteProposta.getLoteProcessoDeCompra());
        itemMapa.setItemMapaComparativoTecnicaPrecoLoteProcesso(itemMapaLoteProcesso);

        itemMapa.definirNotaPrecoPorLote();
        itemMapa.definirNotaClassificacaoFinalPorLoteProposta(indiceTecnico);
        adicionarItem(itemMapa);
    }

    private void adicionarItem(ItemMapaComparativoTecnicaPreco itemMapa) {
        itens = (Util.adicionarObjetoEmLista(itens, itemMapa));
    }

    public void hasMaisDeUmVencedorPorItem(List<ItemMapaComparativoTecnicaPreco> itensMapa) throws ValidacaoException {
        ItemMapaComparativoTecnicaPreco itemAnterior = null;
        for (ItemMapaComparativoTecnicaPreco itemMapa : itensMapa) {
            if (itemMapa.isSituacao(SituacaoItemCertame.VENCEDOR)) {
                if (itemAnterior == null) {
                    itemAnterior = itemMapa;
                } else {
                    itemAnterior = validarEspecificacoes(itemAnterior, itemMapa);
                }
            }
        }
    }

    private ItemMapaComparativoTecnicaPreco validarEspecificacoes(ItemMapaComparativoTecnicaPreco itemAnterior, ItemMapaComparativoTecnicaPreco itemMapa) throws ValidacaoException {
        if (itemMapa.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar() == null &&
            itemAnterior.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar() == null) {
            throw new ValidacaoException("Item ou lote com as mesmas especificações possui mais de um vencedor: <b>" + itemMapa.getItemProcessoDeCompra().getDescricao() + "</b>");
        } else if (itemMapa.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar() == null &&
            itemAnterior.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar() != null) {
            itemAnterior = itemMapa;
        } else if (itemMapa.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar().equals(
            itemAnterior.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar())) {
            throw new ValidacaoException("Item ou lote com as mesmas especificações possui mais de um vencedor: <b>" + itemMapa.getItemProcessoDeCompra().getDescricao() + "</b>");
        } else {
            return itemMapa;
        }
        return itemAnterior;
    }
}
