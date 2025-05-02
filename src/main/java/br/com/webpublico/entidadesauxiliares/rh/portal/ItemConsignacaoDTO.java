package br.com.webpublico.entidadesauxiliares.rh.portal;

import br.com.webpublico.enums.TipoDeConsignacao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peixe on 26/10/17.
 */
public class ItemConsignacaoDTO implements Serializable {
    private String descricao;
    private Date inicioVigencia;
    private Date finalVigencia;
    private BigDecimal quantificacao;
    private TipoDeConsignacao tipoDeConsignacao;
    private Date dataCadastroEconsig;
    private BigDecimal valorDescontado;
    private String motivoRejeicao;

    public ItemConsignacaoDTO() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public BigDecimal getQuantificacao() {
        return quantificacao;
    }

    public void setQuantificacao(BigDecimal quantificacao) {
        this.quantificacao = quantificacao;
    }

    public TipoDeConsignacao getTipoDeConsignacao() {
        return tipoDeConsignacao;
    }

    public void setTipoDeConsignacao(TipoDeConsignacao tipoDeConsignacao) {
        this.tipoDeConsignacao = tipoDeConsignacao;
    }

    public Date getDataCadastroEconsig() {
        return dataCadastroEconsig;
    }

    public void setDataCadastroEconsig(Date dataCadastroEconsig) {
        this.dataCadastroEconsig = dataCadastroEconsig;
    }

    public BigDecimal getValorDescontado() {
        return valorDescontado;
    }

    public void setValorDescontado(BigDecimal valorDescontado) {
        this.valorDescontado = valorDescontado;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }
}
