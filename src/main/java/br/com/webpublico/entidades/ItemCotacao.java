/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ItemFormularioCompraVO;
import br.com.webpublico.enums.TipoBeneficio;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author lucas
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Item Cotação/Planilha Orçamentária")
public class ItemCotacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Número")
    private Integer numero;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Lote Cotação/Planilha Orçamentária")
    @ManyToOne()
    private LoteCotacao loteCotacao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Objeto Compra")
    @ManyToOne
    private ObjetoCompra objetoCompra;

    @Etiqueta("Tipo de Controle")
    @Enumerated(EnumType.STRING)
    private TipoControleLicitacao tipoControle;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Serviço Comprável")
    @ManyToOne
    private ServicoCompravel servicoCompravel;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição Complementar")
    private String descricaoComplementar;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    //Utilizado quando o tipo é obra/serviço de engenharia ou tipo de cotação 'VALOR_REFERENCIA'
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    @ManyToOne
    @Etiqueta(value = "Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @Obrigatorio
    @Etiqueta("Tipo de Benefício")
    @Enumerated(EnumType.STRING)
    private TipoBeneficio tipoBeneficio;

    @OneToOne
    private ItemCotacao itemReferencialCotaReserv;

    @Invisivel
    @OneToMany(mappedBy = "itemCotacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ValorCotacao> valoresCotacao;

    public ItemCotacao() {
        this.quantidade = BigDecimal.ZERO;
        this.valorUnitario = BigDecimal.ZERO;
        this.valorTotal = BigDecimal.ZERO;
        this.valoresCotacao = Lists.newArrayList();
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

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public ServicoCompravel getServicoCompravel() {
        return servicoCompravel;
    }

    public void setServicoCompravel(ServicoCompravel servicoCompravel) {
        this.servicoCompravel = servicoCompravel;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public LoteCotacao getLoteCotacao() {
        return loteCotacao;
    }

    public void setLoteCotacao(LoteCotacao loteCotacao) {
        this.loteCotacao = loteCotacao;
    }

    public List<ValorCotacao> getValoresCotacao() {
        return valoresCotacao;
    }

    public void setValoresCotacao(List<ValorCotacao> listaDeValorCotacao) {
        this.valoresCotacao = listaDeValorCotacao;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public void calcularValorTotal() {
        if (quantidade != null && valorUnitario != null) {
            setValorTotal(quantidade.multiply(valorUnitario).setScale(2, RoundingMode.HALF_EVEN));
        }
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    @Override
    public String toString() {
        return numero + " - " + objetoCompra.getDescricao();
    }


    public boolean isFornecedorCotou(Pessoa fornecedor) {
        for (ValorCotacao valorCotacao : valoresCotacao) {
            if (valorCotacao.getFornecedor().equals(fornecedor)) {
                return true;
            }
        }
        return false;
    }

    public ValorCotacao getValorCotacaoDoFornecedor(Pessoa fornecedor) {
        for (ValorCotacao valor : valoresCotacao) {
            if (valor.getFornecedor().equals(fornecedor)) {
                return valor;
            }
        }
        return null;
    }

    public boolean isPossuiCotacao() {
        if (valoresCotacao != null) {
            for (ValorCotacao valorCotacao : valoresCotacao) {
                if (valorCotacao.getPreco() != null && valorCotacao.getPreco().compareTo(BigDecimal.ZERO) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTipoControlePorValor() {
        return tipoControle != null && tipoControle.isTipoControlePorValor();
    }

    public boolean isTipoControlePorQuantidade() {
        return tipoControle != null && tipoControle.isTipoControlePorQuantidade();
    }

    public BigDecimal getPrecoTotalItem(ItemCotacao itemCotacao, BigDecimal valorUnitario) {
        if (itemCotacao.getObjetoCompra().isObjetoValorReferencia()) {
            return itemCotacao.getValorTotal();
        }
        return (itemCotacao.getQuantidade() != null && itemCotacao.getQuantidade().compareTo(BigDecimal.ZERO) > 0)
            ? itemCotacao.getQuantidade().multiply(valorUnitario).setScale(2, RoundingMode.HALF_EVEN)
            : valorUnitario;
    }

    public BigDecimal getPrecoItem(ItemCotacao itemCotacao, BigDecimal valorPorCriterio) {
        if (itemCotacao.getLoteCotacao().getCotacao().getFormularioCotacao().getTipoSolicitacao().isCompraServico()) {
            if (itemCotacao.getObjetoCompra().getGrupoObjetoCompra().getTipoCotacao().isFornecedor()) {
                return valorPorCriterio;
            }
        } else if (itemCotacao.getLoteCotacao().getCotacao().getFormularioCotacao().getTipoSolicitacao().isObraServicoEngenharia()) {
            return itemCotacao.getValorUnitario();
        }
        return BigDecimal.ZERO;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public TipoBeneficio getTipoBeneficio() {
        return tipoBeneficio;
    }

    public void setTipoBeneficio(TipoBeneficio tipoBeneficio) {
        this.tipoBeneficio = tipoBeneficio;
    }

    public ItemCotacao getItemReferencialCotaReserv() {
        return itemReferencialCotaReserv;
    }

    public void setItemReferencialCotaReserv(ItemCotacao itemReferencialCotaReserv) {
        this.itemReferencialCotaReserv = itemReferencialCotaReserv;
    }

    public static ItemCotacao fromVO(ItemFormularioCompraVO itemVO, LoteCotacao lote){
        ItemCotacao item = itemVO.getItemCotacao() !=null ? itemVO.getItemCotacao() : new ItemCotacao();
        item.setLoteCotacao(lote);
        item.setNumero(itemVO.getNumero());
        item.setDescricaoComplementar(itemVO.getDescricaoComplementar());
        item.setObjetoCompra(itemVO.getObjetoCompra());
        item.setTipoControle(itemVO.getTipoControle());
        item.setTipoBeneficio(itemVO.getTipoBeneficio());
        item.setUnidadeMedida(itemVO.getUnidadeMedida());
        item.setQuantidade(itemVO.getQuantidade());
        item.setValorTotal(itemVO.getValorTotal());
        return item;
    }
}
