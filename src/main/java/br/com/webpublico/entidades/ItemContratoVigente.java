package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.interfaces.ItemLicitatorioContrato;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by mga on 01/03/2018.
 */
@Entity
@Audited
@Etiqueta("Item Contrato Vigente")
@Deprecated
public class ItemContratoVigente extends SuperEntidade implements ItemLicitatorioContrato {

    public static final int CEM = 100;
    public static final int ZERO = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @Etiqueta("Item Contrato")
    private ItemContrato itemContrato;

    @ManyToOne
    @Etiqueta("Item Cotação")
    private ItemCotacao itemCotacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public ItemCotacao getItemCotacao() {
        return itemCotacao;
    }

    public void setItemCotacao(ItemCotacao itemCotacao) {
        this.itemCotacao = itemCotacao;
    }

    @Override
    public BigDecimal getQuantidadeLicitada() {
        return itemCotacao.getQuantidade();
    }

    @Override
    public Integer getNumeroLote() {
        return getItemCotacao().getLoteCotacao().getNumero();
    }

    @Override
    public Integer getNumeroItem() {
        return getItemCotacao().getNumero();
    }

    @Override
    public String getDescricao() {
        return getObjetoCompra().getDescricao();
    }

    @Override
    public String getDescricaoComplementar() {
        if (!Strings.isNullOrEmpty(itemContrato.getDescricaoComplementar())) {
            return itemContrato.getDescricaoComplementar();
        } else if (!Strings.isNullOrEmpty(getItemCotacao().getDescricaoComplementar())) {
            return getItemCotacao().getDescricaoComplementar();
        }
        return getItemCotacao().getDescricaoComplementar();
    }

    public String getDescricaoCurta() {
        if (getObjetoCompra().getDescricao().length() > CEM) {
            return getObjetoCompra().getDescricao().substring(ZERO, CEM).concat("...");
        }
        return getObjetoCompra().getDescricao();
    }

    @Override
    public ItemProcessoDeCompra getItemProcessoCompra() {
        return null;
    }

    @Override
    public ObjetoCompra getObjetoCompra() {
        if (itemContrato.getObjetoCompraContrato() !=null){
            return itemContrato.getObjetoCompraContrato();
        }
        return getItemCotacao().getObjetoCompra();
    }

    @Override
    public String getModelo() {
        return "";
    }

    @Override
    public String getMarca() {
        return "";
    }

    @Override
    public TipoControleLicitacao getTipoControle() {
        return getItemCotacao().getTipoControle();
    }

    @Override
    public UnidadeMedida getUnidadeMedida() {
        return getItemCotacao().getUnidadeMedida();
    }
}
