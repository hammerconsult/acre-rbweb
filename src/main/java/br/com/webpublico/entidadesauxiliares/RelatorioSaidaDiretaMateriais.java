package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by mga on 06/04/2018.
 */
public class RelatorioSaidaDiretaMateriais {

    private Long numero;
    private Date dataSaida;
    private String usuario;
    private String retiradoPor;
    private String tipoBaixa;

    private String material;
    private String localEstoque;
    private String unidadeAdm;
    private String unidadeOrc;
    private String lote;
    private String unidadeMedida;
    private BigDecimal quantidade;

    public RelatorioSaidaDiretaMateriais() {
        quantidade = BigDecimal.ZERO;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(Date dataSaida) {
        this.dataSaida = dataSaida;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getRetiradoPor() {
        return retiradoPor;
    }

    public void setRetiradoPor(String retiradoPor) {
        this.retiradoPor = retiradoPor;
    }

    public String getTipoBaixa() {
        return tipoBaixa;
    }

    public void setTipoBaixa(String tipoBaixa) {
        this.tipoBaixa = tipoBaixa;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getLocalEstoque() {
        return localEstoque;
    }

    public void setLocalEstoque(String localEstoque) {
        this.localEstoque = localEstoque;
    }

    public String getUnidadeAdm() {
        return unidadeAdm;
    }

    public void setUnidadeAdm(String unidadeAdm) {
        this.unidadeAdm = unidadeAdm;
    }

    public String getUnidadeOrc() {
        return unidadeOrc;
    }

    public void setUnidadeOrc(String unidadeOrc) {
        this.unidadeOrc = unidadeOrc;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(String unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
}
