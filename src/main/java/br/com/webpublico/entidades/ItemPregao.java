/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.administrativo.licitacao.ItemPregaoLanceVencedor;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Pregão")
public class ItemPregao extends SuperEntidade implements Comparable<ItemPregao> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Pregão")
    private Pregao pregao;

    @Invisivel
    @OneToMany(mappedBy = "itemPregao", cascade = CascadeType.REMOVE)
    private List<RodadaPregao> listaDeRodadaPregao;

    @OneToOne(mappedBy = "itemPregao", cascade = CascadeType.REMOVE)
    private ItemPregaoItemProcesso itemPregaoItemProcesso;

    @OneToOne(mappedBy = "itemPregao", cascade = CascadeType.REMOVE)
    private ItemPregaoLoteProcesso itemPregaoLoteProcesso;

    @Etiqueta("Status do Fornecedor")
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoClassificacaoFornecedor statusFornecedorVencedor;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Status do Item")
    private TipoStatusItemPregao tipoStatusItemPregao;

    @OneToMany(mappedBy = "itemPregao", cascade = CascadeType.REMOVE)
    private List<ItemPregaoLanceVencedor> lancesVencedores;

    @ManyToOne
    @Etiqueta("Lance Vencedor")
    private ItemPregaoLanceVencedor itemPregaoLanceVencedor;

    @Transient
    private boolean selecionado;

    public ItemPregao() {
        this.listaDeRodadaPregao = Lists.newArrayList();
        this.statusFornecedorVencedor = TipoClassificacaoFornecedor.INABILITADO;
        this.itemPregaoItemProcesso = null;
        this.itemPregaoLoteProcesso = null;
    }

    public ItemPregao(ItemProcessoDeCompra itemProcessoDeCompra, Pregao pg) {
        this.itemPregaoItemProcesso = new ItemPregaoItemProcesso(this, itemProcessoDeCompra);
        this.listaDeRodadaPregao = Lists.newArrayList();
        this.pregao = pg;
    }

    public ItemPregao(LoteProcessoDeCompra loteProcessoDeCompra, Pregao pg) {
        this.itemPregaoLoteProcesso = new ItemPregaoLoteProcesso(this, loteProcessoDeCompra);
        this.listaDeRodadaPregao = Lists.newArrayList();
        this.pregao = pg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pregao getPregao() {
        return pregao;
    }

    public void setPregao(Pregao pregao) {
        this.pregao = pregao;
    }

    public List<RodadaPregao> getListaDeRodadaPregao() {
        return listaDeRodadaPregao;
    }

    public void setListaDeRodadaPregao(List<RodadaPregao> listaDeRodadaPregao) {
        this.listaDeRodadaPregao = listaDeRodadaPregao;
    }

    public TipoClassificacaoFornecedor getStatusFornecedorVencedor() {
        return statusFornecedorVencedor;
    }

    public void setStatusFornecedorVencedor(TipoClassificacaoFornecedor statusFornecedorVencedor) {
        this.statusFornecedorVencedor = statusFornecedorVencedor;
    }

    public BigDecimal recuperarMenorValorDoItem(ItemPregao item) {
        if (item.temRodadaAdicionada()) {
            for (LancePregao lancePregao : item.getUltimaRodada().getListaDeLancePregao()) {
                if (lancePregao.equals(item.getItemPregaoLanceVencedor().getLancePregao())) {
                    return lancePregao.getValor();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private RodadaPregao getUltimaRodada() {
        return this.getListaDeRodadaPregao().get(this.getListaDeRodadaPregao().size() - 1);
    }

    public boolean temRodadaAdicionada() {
        return this.getListaDeRodadaPregao() != null && !this.getListaDeRodadaPregao().isEmpty();
    }

    public boolean hasMaisDeUmLanceVencedor() {
        return lancesVencedores != null && lancesVencedores.size() > 1;
    }

    public String recuperarVencedor() {
        if (this.listaDeRodadaPregao == null || this.listaDeRodadaPregao.isEmpty()) {
            return "Nenhum";
        } else {
            for (LancePregao lancePregao : listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1).getListaDeLancePregao()) {
                if (lancePregao.getStatusLancePregao().isVencedor()) {
                    return lancePregao.getPropostaFornecedor().getFornecedor().getNome();
                }
            }
        }
        return "Nenhum";
    }

    @Override
    public String toString() {
        return this.getDescricao() != null ? this.getDescricao() : "";
    }

    public ItemPregaoItemProcesso getItemPregaoItemProcesso() {
        return itemPregaoItemProcesso;
    }

    public void setItemPregaoItemProcesso(ItemPregaoItemProcesso itemPregaoItemProcesso) {
        this.itemPregaoItemProcesso = itemPregaoItemProcesso;
    }

    public ItemPregaoLoteProcesso getItemPregaoLoteProcesso() {
        return itemPregaoLoteProcesso;
    }

    public void setItemPregaoLoteProcesso(ItemPregaoLoteProcesso itemPregaoLoteProcesso) {
        this.itemPregaoLoteProcesso = itemPregaoLoteProcesso;
    }

    public Integer getNumeroLote() {
        if (itemPregaoItemProcesso != null) {
            return itemPregaoItemProcesso.getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero();
        } else if (itemPregaoLoteProcesso != null) {
            return itemPregaoLoteProcesso.getLoteProcessoDeCompra().getNumero();
        }
        return 0;
    }

    public Integer getNumeroItem() {
        if (itemPregaoItemProcesso != null) {
            return itemPregaoItemProcesso.getItemProcessoDeCompra().getNumero();
        } else if (itemPregaoLoteProcesso != null) {
            return itemPregaoLoteProcesso.getLoteProcessoDeCompra().getNumero();
        }
        return 0;
    }

    public String getDescricao() {
        if (itemPregaoItemProcesso != null) {
            if (this.getPregao().getLicitacao().getProcessoDeCompra().getTipoSolicitacao().equals(TipoSolicitacao.COMPRA_SERVICO)) {
                return this.getItemPregaoItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao();
            }
            return this.getItemPregaoItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao();
        }
        if (itemPregaoLoteProcesso != null) {
            return this.getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getDescricao();
        }
        return "";
    }

    public String getDescricaoComplementar() {
        try {
            if (itemPregaoItemProcesso != null) {
                return this.getItemPregaoItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar();
            }
            return this.getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getDescricao();
        } catch (NullPointerException npe) {
            return "";
        }
    }

    public TipoStatusItemPregao getTipoStatusItemPregao() {
        return this.tipoStatusItemPregao;
    }

    public TipoStatusItemPregao getApenasTipoStatusItemPregao() {
        return this.tipoStatusItemPregao;
    }


    public void setTipoStatusItemPregao(TipoStatusItemPregao tipoStatusItemPregao) {
        this.tipoStatusItemPregao = tipoStatusItemPregao;
    }

    public TipoStatusItemPregao definirStatus() {

        if (isPrejudicado()) {
            return TipoStatusItemPregao.PREJUDICADO;
        }

        if (isItemNaoIniciado()) {
            return TipoStatusItemPregao.DESERTO;
        }

        if (isTodosOsLancesCancelado()) {
            return TipoStatusItemPregao.CANCELADO;
        }

        if (isTodosOsLancesDeclinados()) {
            return TipoStatusItemPregao.DECLINADO;
        }

        if (isTodosOsLancesInexequivel()) {
            return TipoStatusItemPregao.INEXEQUIVEL;
        }

        if (isNenhumLanceStatusAtivoOuVencedor()) {
            return TipoStatusItemPregao.CANCELADO;
        }

        if (isLanceVencedor()) {
            return TipoStatusItemPregao.FINALIZADO;
        }

        return TipoStatusItemPregao.EM_ANDAMENTO;
    }

    private boolean isItemNaoIniciado() {
        if (this.tipoStatusItemPregao == null) {
            return true;
        }

        if (this.getListaDeRodadaPregao().isEmpty()) {
            return true;
        }

        if (this.getListaDeRodadaPregao().size() == 1
            && isLancesDaRodadaSemPreco(this.listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1))
            && isLancesDaRodadaComStatusAtivo(this.listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1))) {
            return true;
        }

        return false;
    }

    private boolean isLancesDaRodadaSemPreco(RodadaPregao rodada) {
        for (LancePregao lancePregao : rodada.getListaDeLancePregao()) {
            if (lancePregao.getValor().compareTo(BigDecimal.ZERO) == 1) {
                return false;
            }
        }

        return true;
    }

    private boolean isLancesDaRodadaComStatusAtivo(RodadaPregao rodada) {
        for (LancePregao lancePregao : rodada.getListaDeLancePregao()) {
            if (!lancePregao.getStatusLancePregao().equals(StatusLancePregao.ATIVO)) {
                return false;
            }
        }

        return true;
    }

    private boolean isTodosOsLancesCancelado() {
        //um item é cancelado quando todos seus lances estiverem com status cancelados
        for (LancePregao lancePregao : listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1).getListaDeLancePregao()) {
            if (!lancePregao.getStatusLancePregao().equals(StatusLancePregao.CANCELADO)) {
                return false;
            }
        }

        return true;
    }

    private boolean isTodosOsLancesDeclinados() {
        //um item é cancelado quando todos seus lances estiverem com status declinado
        for (LancePregao lancePregao : listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1).getListaDeLancePregao()) {
            if (!lancePregao.getStatusLancePregao().equals(StatusLancePregao.DECLINADO)) {
                return false;
            }
        }

        return true;
    }

    private boolean isTodosOsLancesInexequivel() {
        //um item é cancelado quando todos seus lances estiverem com status cancelados
        for (LancePregao lancePregao : listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1).getListaDeLancePregao()) {
            if (!lancePregao.getStatusLancePregao().equals(StatusLancePregao.INEXEQUIVEL)) {
                return false;
            }
        }

        return true;
    }

    private boolean isItemEmAndamento() {
        //um item esta em andamento quando pelo menos um de seus lances sofreram alteracoes
        if (!listaDeRodadaPregao.isEmpty()) {
            for (LancePregao lancePregao : listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1).getListaDeLancePregao()) {
                if (lancePregao.getValor().compareTo(BigDecimal.ZERO) == 1
                    || !lancePregao.getStatusLancePregao().equals(StatusLancePregao.ATIVO)
                    || lancePregao.getStatusLancePregao().equals(StatusLancePregao.ATIVO)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isNenhumLanceStatusAtivoOuVencedor() {
        for (LancePregao lancePregao : listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1).getListaDeLancePregao()) {
            if (lancePregao.getStatusLancePregao().equals(StatusLancePregao.ATIVO)
                || lancePregao.getStatusLancePregao().equals(StatusLancePregao.VENCEDOR)) {
                return false;
            }
        }

        return true;
    }

    public boolean isLanceVencedor() {
        for (LancePregao lancePregao : listaDeRodadaPregao.get(listaDeRodadaPregao.size() - 1).getListaDeLancePregao()) {
            if (StatusLancePregao.VENCEDOR.equals(lancePregao.getStatusLancePregao())) {
                return true;
            }
        }
        return false;
    }

    public boolean isPrejudicado() {
        if (TipoStatusItemPregao.PREJUDICADO.equals(this.tipoStatusItemPregao)) {
            return true;
        }
        return false;
    }

    public BigDecimal getQuantidade() {
        try {
            return this.getItemPregaoItemProcesso().getQuantidade();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalItem() {
        try {
            return this.getItemPregaoItemProcesso().getValorTotal();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValor() {
        try {
            if (getPregao().getLicitacao().getTipoAvaliacao().isMaiorDesconto()) {
                if (this.getPregao().getLicitacao().isTipoApuracaoPrecoItem()) {
                    return getItemPregaoItemProcesso().getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco();
                }
            }
            if (this.getPregao().getLicitacao().isTipoApuracaoPrecoItem()) {
                return this.getItemPregaoLanceVencedor().getValor();
            }
            if (this.getPregao().getLicitacao().isTipoApuracaoPrecoLote()) {
                return this.getItemPregaoLoteProcesso().getValor();
            }
            return BigDecimal.ZERO;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public boolean isMaterial() {
        try {
            return this.getItemPregaoItemProcesso().isMaterial();
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isServico() {
        try {
            return this.getItemPregaoItemProcesso().isServico();
        } catch (Exception ex) {
            return false;
        }
    }

    public ObjetoCompra getObjetoCompra() {
        try {
            return this.getItemPregaoItemProcesso().getObjetoCompra();
        } catch (Exception ex) {
            return null;
        }
    }

    public ServicoCompravel getServicoCompravel() {
        try {
            return this.getItemPregaoItemProcesso().getServicoCompravel();
        } catch (Exception ex) {
            return null;
        }
    }

    public Pessoa getFornecedorDoLanceVencedor() {
        if (this.getItemPregaoLanceVencedor() != null) {
            return this.getItemPregaoLanceVencedor().getLancePregao().getFornecedorDoLanceVencedor();
        }
        return null;
    }

    public RodadaPregao getRodadaAnteriorAEsta(RodadaPregao rodadaPregaoSelecionado) {
        if (!this.getListaDeRodadaPregao().isEmpty()) {
            if (this.getListaDeRodadaPregao().size() > 1) {
                int indexRodadaAnterior = this.getListaDeRodadaPregao().indexOf(rodadaPregaoSelecionado) - 1;
                if (indexRodadaAnterior >= 0) {
                    return this.getListaDeRodadaPregao().get(indexRodadaAnterior);
                }
            }
        }
        return null;
    }

    public void validarExclusaoRodada() {
        ValidacaoException ve = new ValidacaoException();
        if (getListaDeRodadaPregao().size() == 1) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Somente é permitido excluir a rodada de itens com mais de uma rodada.");
        }
        ve.lancarException();
    }

    @Override
    public int compareTo(ItemPregao o) {
        int comparacao = 0;
        if (this.itemPregaoLoteProcesso != null && o.getItemPregaoLoteProcesso() != null) {
            comparacao = this.itemPregaoLoteProcesso.getLoteProcessoDeCompra().getNumero().compareTo(o.getItemPregaoLoteProcesso().getLoteProcessoDeCompra().getNumero());
        }
        if (comparacao == 0) {
            if (this.itemPregaoItemProcesso != null && o.getItemPregaoItemProcesso() != null) {
                comparacao = this.itemPregaoItemProcesso.getItemProcessoDeCompra().getNumero().compareTo(o.getItemPregaoItemProcesso().getItemProcessoDeCompra().getNumero());
            }
        }
        return comparacao;
    }

    public boolean isFinalizado() {
        return TipoStatusItemPregao.FINALIZADO.equals(this.tipoStatusItemPregao);
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public BigDecimal getPrecoItemSolicitacaoMaterial() {
        try {
            return itemPregaoItemProcesso.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getPreco();
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public TipoMascaraUnidadeMedida getMascaraValorUnitario() {
        try {
            return itemPregaoItemProcesso.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraValorUnitario();
        } catch (Exception ex) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL;
        }
    }

    public List<ItemPregaoLanceVencedor> getLancesVencedores() {
        return lancesVencedores;
    }

    public void setLancesVencedores(List<ItemPregaoLanceVencedor> lancesVencedores) {
        this.lancesVencedores = lancesVencedores;
    }

    public ItemPregaoLanceVencedor getItemPregaoLanceVencedor() {
        return itemPregaoLanceVencedor;
    }

    public void setItemPregaoLanceVencedor(ItemPregaoLanceVencedor itemPregaoLanceVencedor) {
        this.itemPregaoLanceVencedor = itemPregaoLanceVencedor;
    }
}
