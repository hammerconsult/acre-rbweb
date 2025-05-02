/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoItemCertame;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Mapa Comparativo")
public class Certame extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data")
    private Date dataRealizado;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Licitação")
    private Licitacao licitacao;
    @OneToMany(mappedBy = "certame", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Item(ns) do Certame")
    @Tabelavel(campoSelecionado = false)
    private List<ItemCertame> listaItemCertame;

    public Certame() {
        this.listaItemCertame = new ArrayList<>();
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

    public Date getDataRealizado() {
        return dataRealizado;
    }

    public void setDataRealizado(Date dataRealizado) {
        this.dataRealizado = dataRealizado;
    }

    public List<ItemCertame> getListaItemCertame() {
        if (listaItemCertame != null) {
            if (isTipoApuracaoPrecoItem()) {
                ordenarItensCertamePorItem();
            } else if (isTipoApuracaoPrecoLote()) {
                ordenarItensCertamePorLote();
            }
        }
        return listaItemCertame;
    }

    private void ordenarItensCertamePorLote() {
        Collections.sort(listaItemCertame, new Comparator<ItemCertame>() {
            @Override
            public int compare(ItemCertame o1, ItemCertame o2) {
                return o1.getItemCertameLoteProcesso().getLoteProcessoDeCompra().getNumero()
                    .compareTo(o2.getItemCertameLoteProcesso().getLoteProcessoDeCompra().getNumero());
            }
        });
    }

    private void ordenarItensCertamePorItem() {
        Collections.sort(listaItemCertame, new Comparator<ItemCertame>() {
            @Override
            public int compare(ItemCertame o1, ItemCertame o2) {
                int i = o1.getItemCertameItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero()
                    .compareTo(o2.getItemCertameItemProcesso().getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero());
                if (i == 0) {
                    i = o1.getItemCertameItemProcesso().getItemProcessoDeCompra().getNumero()
                        .compareTo(o2.getItemCertameItemProcesso().getItemProcessoDeCompra().getNumero());
                }
                return i;
            }
        });
    }

    public void setListaItemCertame(List<ItemCertame> listaItemCertame) {
        this.listaItemCertame = listaItemCertame;
    }

    @Override
    public String toString() {
        try {
            return this.licitacao.getNumeroLicitacao() + " " + this.licitacao.getResumoDoObjeto().substring(0, 10);
        } catch (Exception e) {
            return "Mapa comparativo Ref. a Licitação Nº " + this.licitacao.getNumeroLicitacao();
        }
    }

    public boolean isApurado() {
        if (this.getId() != null) {
            return true;
        }

        return false;
    }

    public boolean isTipoApuracaoPrecoItem() {
        try {
            return this.getLicitacao().isTipoApuracaoPrecoItem();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isTipoApuracaoPrecoLote() {
        try {
            return this.getLicitacao().isTipoApuracaoPrecoLote();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isMaiorDesconto() {
        try {
            return this.getLicitacao().getTipoAvaliacao().isMaiorDesconto();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public void processarSituacaoDosItens() {
        if (this.listaItemCertame != null) {
            for (ItemCertame itemCertameSendoVerificado : this.listaItemCertame) {
                List<ItemCertame> itensCertameDoItemProcessoDeCompra = getTodosItensCertameComMesmoItemProcessoDeCompra(itemCertameSendoVerificado);
                ordenarItensPorPrecoAscOrMaiorDescontoDesc(itensCertameDoItemProcessoDeCompra);

                ItemCertame itemCertameComMenorPrecoDoMesmoObjetoCompra = itensCertameDoItemProcessoDeCompra.get(0);

                BigDecimal valorOrDescontoItem = isMaiorDesconto() ? itemCertameSendoVerificado.getPercentualDesconto() : itemCertameSendoVerificado.getPrecoItem();
                BigDecimal valorOrDescontoVencedor = isMaiorDesconto() ? itemCertameComMenorPrecoDoMesmoObjetoCompra.getPercentualDesconto() : itemCertameComMenorPrecoDoMesmoObjetoCompra.getPrecoItem();

                if (itemCertameSendoVerificado.equals(itemCertameComMenorPrecoDoMesmoObjetoCompra)) {
                    itemCertameSendoVerificado.setSituacaoItemCertame(SituacaoItemCertame.VENCEDOR);
                }
                if (valorOrDescontoItem.compareTo(BigDecimal.ZERO) <= 0) {
                    itemCertameSendoVerificado.setSituacaoItemCertame(SituacaoItemCertame.NAO_COTADO);
                }
                if (!itemCertameSendoVerificado.equals(itemCertameComMenorPrecoDoMesmoObjetoCompra)
                    && valorOrDescontoItem.equals(valorOrDescontoVencedor)) {
                    itemCertameSendoVerificado.setSituacaoItemCertame(SituacaoItemCertame.EMPATE);
                    itemCertameComMenorPrecoDoMesmoObjetoCompra.setSituacaoItemCertame(SituacaoItemCertame.EMPATE);
                }
            }
        }
    }

    public void classificarItensEmpatadosComMenorPreco(ItemCertame itemManipuladoNaTela) {
        List<ItemCertame> itensEmpatados = getItensEmpatadosNoPrecoDoMesmoItemProposta(itemManipuladoNaTela);
        if (!itensEmpatados.isEmpty()) {
            for (ItemCertame item : itensEmpatados) {
                BigDecimal precoOrDescontoItem = isMaiorDesconto() ? item.getPercentualDesconto() : item.getPrecoItem();
                BigDecimal precoOrDescontoItemManipuladoTela = isMaiorDesconto() ? itemManipuladoNaTela.getPercentualDesconto() : itemManipuladoNaTela.getPrecoItem();
                if (precoOrDescontoItem.compareTo(precoOrDescontoItemManipuladoTela) == 0) {
                    item.setSituacaoItemCertame(SituacaoItemCertame.CLASSIFICADO);
                }
            }
        }
    }

    public List<ItemCertame> getItensEmpatadosNoPrecoDoMesmoItemProposta(ItemCertame itemManipuladoNaTela) {
        List<ItemCertame> itensEmpatadosNoPrecoDoMesmoItemProposta = new ArrayList<ItemCertame>();
        for (ItemCertame item : this.listaItemCertame) {
            if (item.isTipoApuracaoPrecoItem()) {
                if (item.getNumeroItem().equals(itemManipuladoNaTela.getNumeroItem())) {
                    if (!item.equals(itemManipuladoNaTela) && item.isEmpate()) {
                        itensEmpatadosNoPrecoDoMesmoItemProposta.add(item);
                    }
                }
            }
            if (item.isTipoApuracaoPrecoLote()) {
                if (item.getNumeroLote().equals(itemManipuladoNaTela.getNumeroLote())) {
                    if (!item.equals(itemManipuladoNaTela) && item.isEmpate()) {
                        itensEmpatadosNoPrecoDoMesmoItemProposta.add(item);
                    }
                }
            }
        }
        return itensEmpatadosNoPrecoDoMesmoItemProposta;
    }

    private void ordenarItensPorPrecoAscOrMaiorDescontoDesc(List<ItemCertame> itensDoMesmoItemProposta) {
        Collections.sort(itensDoMesmoItemProposta, new Comparator<ItemCertame>() {
            @Override
            public int compare(ItemCertame o1, ItemCertame o2) {
                if (isMaiorDesconto()) {
                    return o2.getPercentualDesconto().compareTo(o1.getPercentualDesconto());
                }
                return o1.getPrecoItem().compareTo(o2.getPrecoItem());
            }
        });
    }

    public List<ItemCertame> getLotesProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        List<ItemCertame> lotesPdc = new ArrayList<>();
        for (ItemCertame item : this.listaItemCertame) {
            if (item.getItemCertameLoteProcesso().getLoteProcessoDeCompra().equals(loteProcessoDeCompra)) {
                lotesPdc.add(item);
            }
        }
        return lotesPdc;
    }

    private List<ItemCertame> getTodosItensCertameComMesmoItemProcessoDeCompra(ItemCertame itemManipuladoNaTela) {
        List<ItemCertame> itensDoMesmoItemProcessoDeCompra = new ArrayList<>();
        for (ItemCertame itemCertame : this.listaItemCertame) {
            if (itemManipuladoNaTela.isTipoApuracaoPrecoItem()) {
                if (itemCertame.getItemPropostaFornecedorVencedor().getItemProcessoDeCompra().equals(itemManipuladoNaTela.getItemPropostaFornecedorVencedor().getItemProcessoDeCompra())
                    && !SituacaoItemCertame.NAO_COTADO.equals(itemCertame.getSituacaoItemCertame())) {
                    itensDoMesmoItemProcessoDeCompra.add(itemCertame);
                }
            }
            if (itemManipuladoNaTela.isTipoApuracaoPrecoLote()) {
                if (itemCertame.getLotePropostaFornecedorVencedor().getLoteProcessoDeCompra().equals(itemManipuladoNaTela.getLotePropostaFornecedorVencedor().getLoteProcessoDeCompra())
                    && !SituacaoItemCertame.NAO_COTADO.equals(itemCertame.getSituacaoItemCertame())) {
                    itensDoMesmoItemProcessoDeCompra.add(itemCertame);
                }
            }
        }
        return itensDoMesmoItemProcessoDeCompra;
    }

    public void validarItemEmpatado() {
        for (ItemCertame item : listaItemCertame) {
            if (item.isEmpate()) {
                throw new ValidacaoException("Item empatado: <b>" + item.toString() + "</b>");
            }
        }
    }

    public void validarItemComApenasUmVencedor() {
        for (ItemCertame item : listaItemCertame) {
            ItemCertame itemAnterior = null;
            for (ItemCertame itemCertame : getItemProcessoDeCompra(item.getDescricaoItem())) {
                if (itemCertame.isVencedor()) {
                    if (itemAnterior == null) {
                        itemAnterior = itemCertame;
                    } else {
                        itemAnterior = validarEspecificacoes(itemAnterior, itemCertame);
                    }
                }
            }
        }
    }

    private ItemCertame validarEspecificacoes(ItemCertame itemAnterior, ItemCertame itemCertame) {
        if (itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar() == null &&
            itemAnterior.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar() == null) {
            throw new ValidacaoException("Item com as mesmas especificações possui mais de um vencedor: <b>" + itemCertame.toString() + "</b>");
        } else if (itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar() == null &&
            itemAnterior.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar() != null) {
            itemAnterior = itemCertame;
        } else if (itemCertame.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar().equals(
            itemAnterior.getItemCertameItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar())) {
            throw new ValidacaoException("Item com as mesmas especificações possui mais de um vencedor: <b>" + itemCertame.toString() + "</b>");
        } else {
            itemAnterior = itemCertame;
        }
        return itemAnterior;
    }

    public void validarVencedorQuandoTiverAlgumClassificado(ItemCertame itemClassificado) {
        for (ItemCertame item : listaItemCertame) {
            if (item.getDescricaoItem().equals(itemClassificado.getDescricaoItem())) {
                if (item.isVencedor()) {
                    return;
                }
            }
        }
        String classificado = SituacaoItemCertame.CLASSIFICADO.getDescricao();
        String vencedor = SituacaoItemCertame.VENCEDOR.getDescricao();
        throw new ValidacaoException("O item <b>" + itemClassificado.toString() + "</b></br>Tem fornecedor <b>" + classificado + "</b> mas não tem um <b>" + vencedor + "</b>. Informe um " + vencedor + " para salvar!");
    }

    private List<ItemCertame> getItemProcessoDeCompra(String descricaoItem) {
        List<ItemCertame> itens = Lists.newArrayList();
        for (ItemCertame item : listaItemCertame) {
            if (item.getDescricaoItem().equals(descricaoItem)) {
                itens.add(item);
            }
        }
        return itens;
    }

    public List<ItemCertame> getItensClassificados() {
        List<ItemCertame> classificados = Lists.newArrayList();
        for (ItemCertame item : listaItemCertame) {
            if (item.isClassificado()) {
                classificados.add(item);
            }
        }
        return classificados;
    }

}
