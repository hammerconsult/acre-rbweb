package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class ItemContratoEmVigencia implements Comparable<ItemContratoEmVigencia> {

    private Long idObjetoCompra;
    private Number idGrupoObjetoCompra;
    private Long numeroItem;
    private String objetoCompra;
    private String grupoObjetoCompra;
    private BigDecimal quantidadeLicitada;
    private BigDecimal quantidadeContrato;
    private Boolean pesquisado;

    public ItemContratoEmVigencia() {
        quantidadeLicitada = BigDecimal.ZERO;
        quantidadeContrato = BigDecimal.ZERO;
        pesquisado = Boolean.TRUE;
    }

    public Long getIdObjetoCompra() {
        return idObjetoCompra;
    }

    public void setIdObjetoCompra(Long idObjetoCompra) {
        this.idObjetoCompra = idObjetoCompra;
    }

    public Number getIdGrupoObjetoCompra() {
        return idGrupoObjetoCompra;
    }

    public void setIdGrupoObjetoCompra(Number idGrupoObjetoCompra) {
        this.idGrupoObjetoCompra = idGrupoObjetoCompra;
    }

    public String getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(String objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public BigDecimal getQuantidadeLicitada() {
        return quantidadeLicitada;
    }

    public void setQuantidadeLicitada(BigDecimal quantidadeLicitada) {
        this.quantidadeLicitada = quantidadeLicitada;
    }

    public BigDecimal getQuantidadeContrato() {
        return quantidadeContrato;
    }

    public void setQuantidadeContrato(BigDecimal quantidadeContrato) {
        this.quantidadeContrato = quantidadeContrato;
    }

    public String getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(String grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public Long getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(Long numeroItem) {
        this.numeroItem = numeroItem;
    }

    public Boolean getPesquisado() {
        return pesquisado;
    }

    public void setPesquisado(Boolean pesquisado) {
        this.pesquisado = pesquisado;
    }

    @Override
    public int compareTo(ItemContratoEmVigencia o) {
        if (o.getNumeroItem() != null && getNumeroItem() != null) {
            return getNumeroItem().compareTo(o.getNumeroItem());
        }
        return 0;
    }
}
