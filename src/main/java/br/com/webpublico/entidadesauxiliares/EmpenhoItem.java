package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 11/06/14
 * Time: 09:05
 * To change this template use File | Settings | File Templates.
 */
public class EmpenhoItem {

    private String numero;
    private Date dataEmpenho;
    private String codigo;
    private String Empenho;
    private String fornecedor;
    private String tipoEmpenho;
    private String contaCodigo;
    private String contaDescricao;
    private String projetoAtividade;
    private String fonte;
    private String orgaoDescricao;
    private String unidadeDescricao;
    private String unidadeCodigo;
    private String unidadeGestora;
    private String complemento;
    private BigDecimal valorEmpenho;
    private BigDecimal valorLiquidado;
    private BigDecimal valorALiquidar;
    private BigDecimal valorPago;
    private BigDecimal valorAPagar;

    public EmpenhoItem() {
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getEmpenho() {
        return Empenho;
    }

    public void setEmpenho(String empenho) {
        Empenho = empenho;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getTipoEmpenho() {
        return tipoEmpenho;
    }

    public void setTipoEmpenho(String tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }

    public String getContaCodigo() {
        return contaCodigo;
    }

    public void setContaCodigo(String contaCodigo) {
        this.contaCodigo = contaCodigo;
    }

    public String getContaDescricao() {
        return contaDescricao;
    }

    public void setContaDescricao(String contaDescricao) {
        this.contaDescricao = contaDescricao;
    }

    public String getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(String projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getOrgaoDescricao() {
        return orgaoDescricao;
    }

    public void setOrgaoDescricao(String orgaoDescricao) {
        this.orgaoDescricao = orgaoDescricao;
    }

    public String getUnidadeDescricao() {
        return unidadeDescricao;
    }

    public void setUnidadeDescricao(String unidadeDescricao) {
        this.unidadeDescricao = unidadeDescricao;
    }

    public String getUnidadeCodigo() {
        return unidadeCodigo;
    }

    public void setUnidadeCodigo(String unidadeCodigo) {
        this.unidadeCodigo = unidadeCodigo;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public BigDecimal getValorEmpenho() {
        return valorEmpenho;
    }

    public void setValorEmpenho(BigDecimal valorEmpenho) {
        this.valorEmpenho = valorEmpenho;
    }

    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    public BigDecimal getValorALiquidar() {
        return valorALiquidar;
    }

    public void setValorALiquidar(BigDecimal valorALiquidar) {
        this.valorALiquidar = valorALiquidar;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getValorAPagar() {
        return valorAPagar;
    }

    public void setValorAPagar(BigDecimal valorAPagar) {
        this.valorAPagar = valorAPagar;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
}
