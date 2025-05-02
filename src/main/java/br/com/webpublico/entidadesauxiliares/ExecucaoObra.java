package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 19/05/2017.
 */
public class ExecucaoObra {

    private Long idObra;
    private String tipoObra;
    private String descricaoObra;
    private String descricaoMedicao;
    private String numeroContrato;
    private String itemObra;
    private BigDecimal valorContrato;
    private BigDecimal valorMedicao;
    private BigDecimal percentualExecucao;
    private BigDecimal valorContrapartida;
    private BigDecimal valorPagoOGU;
    private BigDecimal totalPago;
    private BigDecimal valorPendente;
    private String dataPagamento;
    private Date inicioMedicao;
    private Date fimMedicao;
    private String dataMedicao;
    private List<ExecucaoObra> execucoesObra;

    public ExecucaoObra() {
        execucoesObra = Lists.newArrayList();
        zerarValores();
    }

    public String getDataMedicao() {
        return dataMedicao;
    }

    public void setDataMedicao(String dataMedicao) {
        this.dataMedicao = dataMedicao;
    }

    public String getItemObra() {
        return itemObra;
    }

    public void setItemObra(String itemObra) {
        this.itemObra = itemObra;
    }

    public Long getIdObra() {
        return idObra;
    }

    public void setIdObra(Long idObra) {
        this.idObra = idObra;
    }

    public String getTipoObra() {
        return tipoObra;
    }

    public void setTipoObra(String tipoObra) {
        this.tipoObra = tipoObra;
    }

    public String getDescricaoObra() {
        return descricaoObra;
    }

    public void setDescricaoObra(String descricaoObra) {
        this.descricaoObra = descricaoObra;
    }

    public String getDescricaoMedicao() {
        return descricaoMedicao;
    }

    public void setDescricaoMedicao(String descricaoMedicao) {
        this.descricaoMedicao = descricaoMedicao;
    }

    public BigDecimal getValorContrato() {
        return valorContrato;
    }

    public void setValorContrato(BigDecimal valorContrato) {
        this.valorContrato = valorContrato;
    }

    public BigDecimal getValorMedicao() {
        return valorMedicao;
    }

    public void setValorMedicao(BigDecimal valorMedicao) {
        this.valorMedicao = valorMedicao;
    }

    public BigDecimal getPercentualExecucao() {
        return percentualExecucao;
    }

    public void setPercentualExecucao(BigDecimal percentualExecucao) {
        this.percentualExecucao = percentualExecucao;
    }

    public BigDecimal getValorContrapartida() {
        return valorContrapartida;
    }

    public void setValorContrapartida(BigDecimal valorContrapartida) {
        this.valorContrapartida = valorContrapartida;
    }

    public BigDecimal getValorPagoOGU() {
        return valorPagoOGU;
    }

    public void setValorPagoOGU(BigDecimal valorPagoOGU) {
        this.valorPagoOGU = valorPagoOGU;
    }

    public BigDecimal getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(BigDecimal totalPago) {
        this.totalPago = totalPago;
    }

    public BigDecimal getValorPendente() {
        return valorPendente;
    }

    public void setValorPendente(BigDecimal valorPendente) {
        this.valorPendente = valorPendente;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public Date getInicioMedicao() {
        return inicioMedicao;
    }

    public void setInicioMedicao(Date inicioMedicao) {
        this.inicioMedicao = inicioMedicao;
    }

    public Date getFimMedicao() {
        return fimMedicao;
    }

    public void setFimMedicao(Date fimMedicao) {
        this.fimMedicao = fimMedicao;
    }

    public List<ExecucaoObra> getExecucoesObra() {
        return execucoesObra;
    }

    public void setExecucoesObra(List<ExecucaoObra> execucoesObra) {
        this.execucoesObra = execucoesObra;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public void zerarValores() {
        valorContrato = BigDecimal.ZERO;
        valorMedicao = BigDecimal.ZERO;
        percentualExecucao = BigDecimal.ZERO;
        valorContrapartida = BigDecimal.ZERO;
        valorPagoOGU = BigDecimal.ZERO;
        totalPago = BigDecimal.ZERO;
        valorPendente = BigDecimal.ZERO;
    }
}
