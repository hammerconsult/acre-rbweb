package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoMovimentoTCE;
import br.com.webpublico.util.Util;

import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 04/10/17.
 */
public class PesquisaLCP {

    private String codigoEvento;
    private String descricaoEvento;
    private String codigoClp;
    private String descricaoClp;
    private String codigoTag;
    private String descricaoTag;
    private String codigoContaContabil;
    private String descricaoContaContabil;
    private String codigoContaAuxiliar;
    private String descricaoContaAuxiliar;
    @Enumerated(EnumType.STRING)
    private TipoMovimentoTCE tipoMovimentoTCE;
    private String codigoContaAuxiliarSiconfi;
    private String descricaoContaAuxiliarSiconfi;
    private Integer itemLcp;
    private String codigoLcp;
    private Date clpInicioVigencia;
    @Enumerated(EnumType.STRING)
    private DebitoCredito debitoCredito;

    public PesquisaLCP() {
    }

    public String getCodigoEvento() {
        return codigoEvento;
    }

    public void setCodigoEvento(String codigoEvento) {
        this.codigoEvento = codigoEvento;
    }

    public String getDescricaoEvento() {
        return descricaoEvento;
    }

    public void setDescricaoEvento(String descricaoEvento) {
        this.descricaoEvento = descricaoEvento;
    }

    public String getCodigoClp() {
        return codigoClp;
    }

    public void setCodigoClp(String codigoClp) {
        this.codigoClp = codigoClp;
    }

    public String getDescricaoClp() {
        return descricaoClp;
    }

    public void setDescricaoClp(String descricaoClp) {
        this.descricaoClp = descricaoClp;
    }

    public String getCodigoTag() {
        return codigoTag;
    }

    public void setCodigoTag(String codigoTag) {
        this.codigoTag = codigoTag;
    }

    public String getDescricaoTag() {
        return descricaoTag;
    }

    public void setDescricaoTag(String descricaoTag) {
        this.descricaoTag = descricaoTag;
    }

    public String getCodigoContaContabil() {
        return codigoContaContabil;
    }

    public void setCodigoContaContabil(String codigoContaContabil) {
        this.codigoContaContabil = codigoContaContabil;
    }

    public String getDescricaoContaContabil() {
        return descricaoContaContabil;
    }

    public void setDescricaoContaContabil(String descricaoContaContabil) {
        this.descricaoContaContabil = descricaoContaContabil;
    }

    public String getCodigoContaAuxiliar() {
        return codigoContaAuxiliar;
    }

    public void setCodigoContaAuxiliar(String codigoContaAuxiliar) {
        this.codigoContaAuxiliar = codigoContaAuxiliar;
    }

    public String getDescricaoContaAuxiliar() {
        return descricaoContaAuxiliar;
    }

    public void setDescricaoContaAuxiliar(String descricaoContaAuxiliar) {
        this.descricaoContaAuxiliar = descricaoContaAuxiliar;
    }

    public TipoMovimentoTCE getTipoMovimentoTCE() {
        return tipoMovimentoTCE;
    }

    public void setTipoMovimentoTCE(TipoMovimentoTCE tipoMovimentoTCE) {
        this.tipoMovimentoTCE = tipoMovimentoTCE;
    }

    public Integer getItemLcp() {
        return itemLcp;
    }

    public void setItemLcp(Integer itemLcp) {
        this.itemLcp = itemLcp;
    }

    public String getCodigoLcp() {
        return codigoLcp;
    }

    public void setCodigoLcp(String codigoLcp) {
        this.codigoLcp = codigoLcp;
    }

    public Date getClpInicioVigencia() {
        return clpInicioVigencia;
    }

    public void setClpInicioVigencia(Date clpInicioVigencia) {
        this.clpInicioVigencia = clpInicioVigencia;
    }

    public DebitoCredito getDebitoCredito() {
        return debitoCredito;
    }

    public void setDebitoCredito(DebitoCredito debitoCredito) {
        this.debitoCredito = debitoCredito;
    }

    public String getCodigoContaAuxiliarSiconfi() {
        return codigoContaAuxiliarSiconfi;
    }

    public void setCodigoContaAuxiliarSiconfi(String codigoContaAuxiliarSiconfi) {
        this.codigoContaAuxiliarSiconfi = codigoContaAuxiliarSiconfi;
    }

    public String getDescricaoContaAuxiliarSiconfi() {
        return descricaoContaAuxiliarSiconfi;
    }

    public void setDescricaoContaAuxiliarSiconfi(String descricaoContaAuxiliarSiconfi) {
        this.descricaoContaAuxiliarSiconfi = descricaoContaAuxiliarSiconfi;
    }

    public List<SelectItem> getDebitosCreditos() {
        return Util.getListSelectItem(DebitoCredito.values());
    }

    public List<SelectItem> getTiposMovimentoTCE() {
        return Util.getListSelectItem(TipoMovimentoTCE.values());
    }

    public enum DebitoCredito {
        DEBITO("Débito"),
        CREDITO("Crédito");

        private String descricao;

        DebitoCredito(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
