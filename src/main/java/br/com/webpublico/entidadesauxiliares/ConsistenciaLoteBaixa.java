/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoParcela;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author gustavo
 */
public class ConsistenciaLoteBaixa implements Serializable {

    private String dam;
    private String codigoBarras;
    private String tipo;
    private Date dataPagamento;
    private String boletim;
    private Inconsistencia descricao;
    private BigDecimal valorEmitido;
    private BigDecimal juros;
    private BigDecimal multa;
    private BigDecimal correcao;
    private BigDecimal valorPago;
    private BigDecimal valorPagar;
    private BigDecimal diferenca;
    private Boolean consistente;
    private SituacaoParcela situacaoParcela;

    public ConsistenciaLoteBaixa() {
        multa = BigDecimal.ZERO;
        juros = BigDecimal.ZERO;
        correcao = BigDecimal.ZERO;
        valorEmitido = BigDecimal.ZERO;
        valorPago = BigDecimal.ZERO;
        valorPagar = BigDecimal.ZERO;
        diferenca = BigDecimal.ZERO;
    }

    public Boolean getConsistente() {
        return consistente;
    }

    public void setConsistente(Boolean consistente) {
        this.consistente = consistente;
    }

    public String getDam() {
        return dam;
    }

    public void setDam(String dam) {
        this.dam = dam;
    }

    public Inconsistencia getDescricao() {
        return descricao;
    }

    public String getDescricaoString() {
        return descricao.name();
    }

    public void setDescricao(Inconsistencia descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(BigDecimal diferenca) {
        this.diferenca = diferenca;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getValorEmitido() {
        return valorEmitido;
    }

    public void setValorEmitido(BigDecimal valorEmitido) {
        this.valorEmitido = valorEmitido;
    }

    public BigDecimal getValorPagar() {
        return valorPagar;
    }

    public void setValorPagar(BigDecimal valorPagar) {
        this.valorPagar = valorPagar;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public String getBoletim() {
        return boletim;
    }

    public void setBoletim(String boletim) {
        this.boletim = boletim;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public BigDecimal getCorrecao() {
        return correcao;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getJuros() {
        return juros;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public enum Inconsistencia {
        ARQUIVO_INCONSISTENTE,
        DAM_NAO_ENCONTRATO,
        DEBITO_PAGO_EM_DUPLICIDADE_NO_MESMO_ARQUIVO,
        NAO_EXISTE_DATA_DE_VENCIMENTO_PARA_O_DAM,
        DIFERENCA_NO_TOTAL_DO_LOTE,
        DIFERENCA_ENTRE_VALOR_DEVIDO_E_VALOR_PAGO,
        DEBITO_COM_SITUAÇAO_INCONSISTENTE;
    }
}
