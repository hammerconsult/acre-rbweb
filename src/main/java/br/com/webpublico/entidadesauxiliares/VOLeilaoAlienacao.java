package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Julio on 22-09-2017.
 */
public class VOLeilaoAlienacao {
    private String codigo;
    private String descricao;
    private String responsavel;
    private Date data;
    private String situacao;
    private String tipoBem;
    private String avaliacao;
    private String undOrc;
    private String codigoBem;
    private String registroAnterior;

    private String codigoLote;
    private String descricaoLote;
    private BigDecimal valorTotalLote;

    private String bem;
    private String grupoPatrimonial;
    private BigDecimal valorOriginal;
    private BigDecimal valorLiquido;
    private BigDecimal valorAvaliado;
    private BigDecimal valorArrematado;
    private String arrematado;

    private Date dataDocumento;
    private String numeroDocumento;
    private String serieDocumento;
    private String ufDocumento;
    private String tipoDocumento;
    private BigDecimal valorDocumento;
    private List<VOLeilaoAlienacao> documentosFiscais;

    public VOLeilaoAlienacao() {
        documentosFiscais = Lists.newArrayList();
        valorTotalLote = BigDecimal.ZERO;
        valorOriginal = BigDecimal.ZERO;
        valorLiquido = BigDecimal.ZERO;
        valorAvaliado = BigDecimal.ZERO;
        valorArrematado = BigDecimal.ZERO;
        valorDocumento = BigDecimal.ZERO;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public String getDescricaoLote() {
        return descricaoLote;
    }

    public void setDescricaoLote(String descricaoLote) {
        this.descricaoLote = descricaoLote;
    }

    public BigDecimal getValorTotalLote() {
        return valorTotalLote;
    }

    public void setValorTotalLote(BigDecimal valorTotalLote) {
        this.valorTotalLote = valorTotalLote;
    }

    public String getBem() {
        return bem;
    }

    public void setBem(String bem) {
        this.bem = bem;
    }

    public String getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(String grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public BigDecimal getValorAvaliado() {
        return valorAvaliado;
    }

    public void setValorAvaliado(BigDecimal valorAvaliado) {
        this.valorAvaliado = valorAvaliado;
    }

    public String getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(String tipoBem) {
        this.tipoBem = tipoBem;
    }

    public BigDecimal getValorArrematado() {
        return valorArrematado;
    }

    public void setValorArrematado(BigDecimal valorArrematado) {
        this.valorArrematado = valorArrematado;
    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getUndOrc() {
        return undOrc;
    }

    public void setUndOrc(String undOrc) {
        this.undOrc = undOrc;
    }

    public String getArrematado() {
        return arrematado;
    }

    public void setArrematado(String arrematado) {
        this.arrematado = arrematado;
    }

    public String getCodigoBem() {
        return codigoBem;
    }

    public void setCodigoBem(String codigoBem) {
        this.codigoBem = codigoBem;
    }

    public Date getDataDocumento() {
        return dataDocumento;
    }

    public void setDataDocumento(Date dataDocumento) {
        this.dataDocumento = dataDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getSerieDocumento() {
        return serieDocumento;
    }

    public void setSerieDocumento(String serieDocumento) {
        this.serieDocumento = serieDocumento;
    }

    public String getUfDocumento() {
        return ufDocumento;
    }

    public void setUfDocumento(String ufDocumento) {
        this.ufDocumento = ufDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public BigDecimal getValorDocumento() {
        return valorDocumento;
    }

    public void setValorDocumento(BigDecimal valorDocumento) {
        this.valorDocumento = valorDocumento;
    }

    public List<VOLeilaoAlienacao> getDocumentosFiscais() {
        return documentosFiscais;
    }

    public void setDocumentosFiscais(List<VOLeilaoAlienacao> documentosFiscais) {
        this.documentosFiscais = documentosFiscais;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getRegistroAnterior() {
        return registroAnterior;
    }

    public void setRegistroAnterior(String registroAnterior) {
        this.registroAnterior = registroAnterior;
    }
}
