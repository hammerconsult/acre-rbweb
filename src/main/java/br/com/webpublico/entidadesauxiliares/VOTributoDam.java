package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Tributo;

import java.math.BigDecimal;

/**
 * Created by Fabio on 10/04/2018.
 */
public class VOTributoDam implements Comparable<VOTributoDam> {

    private Long id;
    private Long idItemDam;
    private BigDecimal valorOriginal;
    private BigDecimal desconto;
    private Long idTributo;
    private Long codigoTributo;
    private String descricaoTributo;
    private Tributo.TipoTributo tipoTributo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdItemDam() {
        return idItemDam;
    }

    public void setIdItemDam(Long idItemDam) {
        this.idItemDam = idItemDam;
    }

    public Long getCodigoTributo() {
        return codigoTributo;
    }

    public void setCodigoTributo(Long codigoTributo) {
        this.codigoTributo = codigoTributo;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal != null ? valorOriginal : BigDecimal.ZERO;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getDesconto() {
        return desconto != null ? desconto : BigDecimal.ZERO;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public Long getIdTributo() {
        return idTributo;
    }

    public void setIdTributo(Long idTributo) {
        this.idTributo = idTributo;
    }

    public String getDescricaoTributo() {
        return descricaoTributo;
    }

    public void setDescricaoTributo(String descricaoTributo) {
        this.descricaoTributo = descricaoTributo;
    }

    public Tributo.TipoTributo getTipoTributo() {
        return tipoTributo;
    }

    public void setTipoTributo(Tributo.TipoTributo tipoTributo) {
        this.tipoTributo = tipoTributo;
    }

    public BigDecimal getValorComDesconto() {
        return getValorOriginal().subtract(getDesconto());
    }

    @Override
    public int compareTo(VOTributoDam o) {
        return this.getId().compareTo(o.getId());
    }
}
