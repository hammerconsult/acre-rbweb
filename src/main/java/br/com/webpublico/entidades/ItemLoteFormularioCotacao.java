package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ItemFormularioCompraVO;
import br.com.webpublico.enums.TipoBeneficio;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Positivo;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 17/04/15
 * Time: 08:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemLoteFormularioCotacao extends SuperEntidade implements Comparable<ItemLoteFormularioCotacao> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private LoteFormularioCotacao loteFormularioCotacao;

    @Positivo(permiteZero = false)
    @Obrigatorio
    @Etiqueta("Nº do Item")
    private Integer numero;

    @Obrigatorio
    @Etiqueta("Objeto de Compra")
    @ManyToOne
    private ObjetoCompra objetoCompra;

    @Lob
    private String descricaoComplementar;

    @Positivo(permiteZero = false)
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Valor Item (R$)")
    private BigDecimal valor;

    @ManyToOne
    @Obrigatorio
    @Etiqueta(value = "Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @Etiqueta("Tipo de Controle")
    @Enumerated(EnumType.STRING)
    private TipoControleLicitacao tipoControle;

    @Obrigatorio
    @Etiqueta("Tipo de Benefício")
    @Enumerated(EnumType.STRING)
    private TipoBeneficio tipoBeneficio;

    @OneToOne
    private ItemLoteFormularioCotacao itemReferencialCotaReserv;

    public ItemLoteFormularioCotacao() {
        super();
        quantidade = BigDecimal.ZERO;
        valor = BigDecimal.ZERO;
    }

    public ItemLoteFormularioCotacao(LoteFormularioCotacao loteFormularioCotacao, Integer numero, ObjetoCompra objetoCompra, String descricaoComplementar, BigDecimal quantidade) {
        super();
        this.loteFormularioCotacao = loteFormularioCotacao;
        this.numero = numero;
        this.objetoCompra = objetoCompra;
        this.descricaoComplementar = descricaoComplementar;
        this.quantidade = quantidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoteFormularioCotacao getLoteFormularioCotacao() {
        return loteFormularioCotacao;
    }

    public void setLoteFormularioCotacao(LoteFormularioCotacao loteFormularioCotacao) {
        this.loteFormularioCotacao = loteFormularioCotacao;
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

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public String getDescricao() {
        if (objetoCompra != null) {
            return objetoCompra.toString();
        }
        return "";
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public int compareTo(ItemLoteFormularioCotacao o) {
        return this.getNumero().compareTo(o.getNumero());
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    public TipoBeneficio getTipoBeneficio() {
        return tipoBeneficio;
    }

    public void setTipoBeneficio(TipoBeneficio tipoBeneficio) {
        this.tipoBeneficio = tipoBeneficio;
    }

    public ItemLoteFormularioCotacao getItemReferencialCotaReserv() {
        return itemReferencialCotaReserv;
    }

    public void setItemReferencialCotaReserv(ItemLoteFormularioCotacao itemReferencialCotaReserv) {
        this.itemReferencialCotaReserv = itemReferencialCotaReserv;
    }

    public static ItemLoteFormularioCotacao fromVO(ItemFormularioCompraVO itemVO, LoteFormularioCotacao lote){
        ItemLoteFormularioCotacao item = itemVO.getItemFormulario() != null ? itemVO.getItemFormulario() : new ItemLoteFormularioCotacao();
        item.setLoteFormularioCotacao(lote);
        item.setNumero(itemVO.getNumero());
        item.setDescricaoComplementar(itemVO.getDescricaoComplementar());
        item.setObjetoCompra(itemVO.getObjetoCompra());
        item.setTipoControle(itemVO.getTipoControle());
        item.setTipoBeneficio(itemVO.getTipoBeneficio());
        item.setUnidadeMedida(itemVO.getUnidadeMedida());
        item.setQuantidade(itemVO.getQuantidade());
        item.setValor(itemVO.getValorTotal());
        return item;
    }
}
