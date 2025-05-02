package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.ItemFormularioCompraVO;
import br.com.webpublico.enums.TipoBeneficio;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Length;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Positivo;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
@Etiqueta("Item Intenção de Registro de Preço")
public class ItemIntencaoRegistroPreco extends SuperEntidade implements Comparable<ItemIntencaoRegistroPreco> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Positivo(permiteZero = false)
    @Obrigatorio
    @Etiqueta("Nº do Item")
    private Integer numero;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;

    @Length(maximo = 3000)
    @Etiqueta("Descrição Complementar")
    private String descricaoComplementar;

    @ManyToOne
    @Obrigatorio
    @Etiqueta(value = "Unidade de Medida")
    private UnidadeMedida unidadeMedida;

    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Etiqueta("Valor Item (R$)")
    private BigDecimal valor;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Controle")
    private TipoControleLicitacao tipoControle;

    @Obrigatorio
    @Etiqueta("Tipo de Benefício")
    @Enumerated(EnumType.STRING)
    private TipoBeneficio tipoBeneficio;

    @OneToOne
    private ItemIntencaoRegistroPreco itemReferencialCotaReserv;

    @ManyToOne
    private LoteIntencaoRegistroPreco loteIntencaoRegistroPreco;

    public ItemIntencaoRegistroPreco() {
        quantidade = BigDecimal.ZERO;
        valor = BigDecimal.ZERO;
    }

    @Override
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

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoControleLicitacao getTipoControle() {
        return tipoControle;
    }

    public void setTipoControle(TipoControleLicitacao tipoControle) {
        this.tipoControle = tipoControle;
    }

    @Override
    public int compareTo(ItemIntencaoRegistroPreco o) {
        if (o.getNumero() != null && getNumero() != null) {
            return (getNumero().compareTo(o.getNumero()));
        }
        return 0;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    public boolean isTipoControlePorQuantidade() {
        return tipoControle != null && tipoControle.isTipoControlePorQuantidade();
    }

    public boolean isTipoControlePorValor() {
        return tipoControle != null && tipoControle.isTipoControlePorValor();
    }

    public LoteIntencaoRegistroPreco getLoteIntencaoRegistroPreco() {
        return loteIntencaoRegistroPreco;
    }

    public void setLoteIntencaoRegistroPreco(LoteIntencaoRegistroPreco loteIntencaoRegistroPreco) {
        this.loteIntencaoRegistroPreco = loteIntencaoRegistroPreco;
    }

    public BigDecimal getValorColuna() {
        return isTipoControlePorQuantidade() ? quantidade : valor;
    }

    public String getDescricao() {
        if (objetoCompra != null) {
            return objetoCompra.toString();
        }
        return "";
    }

    public TipoBeneficio getTipoBeneficio() {
        return tipoBeneficio;
    }

    public void setTipoBeneficio(TipoBeneficio tipoBeneficio) {
        this.tipoBeneficio = tipoBeneficio;
    }

    public ItemIntencaoRegistroPreco getItemReferencialCotaReserv() {
        return itemReferencialCotaReserv;
    }

    public void setItemReferencialCotaReserv(ItemIntencaoRegistroPreco itemReferencialCotaRes) {
        this.itemReferencialCotaReserv = itemReferencialCotaRes;
    }

    public static ItemIntencaoRegistroPreco fromVO(ItemFormularioCompraVO itemVO, LoteIntencaoRegistroPreco lote) {
        ItemIntencaoRegistroPreco itemIrp = itemVO.getItemIRP() != null ? itemVO.getItemIRP() : new ItemIntencaoRegistroPreco();
        itemIrp.setLoteIntencaoRegistroPreco(lote);
        itemIrp.setNumero(itemVO.getNumero());
        itemIrp.setDescricaoComplementar(itemVO.getDescricaoComplementar());
        itemIrp.setObjetoCompra(itemVO.getObjetoCompra());
        itemIrp.setTipoControle(itemVO.getTipoControle());
        itemIrp.setTipoBeneficio(itemVO.getTipoBeneficio());
        itemIrp.setUnidadeMedida(itemVO.getUnidadeMedida());
        itemIrp.setQuantidade(itemVO.getQuantidade());
        itemIrp.setValor(itemVO.getValorTotal());
        return itemIrp;
    }
}
