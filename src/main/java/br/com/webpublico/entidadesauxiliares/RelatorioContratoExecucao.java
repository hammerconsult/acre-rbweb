package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.webreportdto.dto.administrativo.RelatorioContratoExecucaoDTO;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioContratoExecucao {

    private Long idExecucao;
    private String fonteDespesa;
    private BigDecimal valor;
    private String numero;
    private BigDecimal valorEmpenhado;
    private BigDecimal estornoEmpenho;
    private BigDecimal cancelamentoRestosAPagar;
    private BigDecimal valorAEmpenhar;
    private BigDecimal valorTotal;

    public RelatorioContratoExecucao() {
        valorEmpenhado = BigDecimal.ZERO;
        estornoEmpenho = BigDecimal.ZERO;
        cancelamentoRestosAPagar = BigDecimal.ZERO;
        valorAEmpenhar = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
    }

    public static List<RelatorioContratoExecucaoDTO> execucoesToDto (List<RelatorioContratoExecucao> execucoes) {
        List<RelatorioContratoExecucaoDTO> retorno = Lists.newArrayList();
        for (RelatorioContratoExecucao execucao : execucoes) {
            retorno.add(execucaoToDto(execucao));
        }
        return retorno;
    }

    public static RelatorioContratoExecucaoDTO execucaoToDto (RelatorioContratoExecucao execucao) {
        RelatorioContratoExecucaoDTO execucaoDto = new RelatorioContratoExecucaoDTO();
        execucaoDto.setIdExecucao(execucao.getIdExecucao());
        execucaoDto.setFonteDespesa(execucao.getFonteDespesa());
        execucaoDto.setValor(execucao.getValor());
        execucaoDto.setNumero(execucao.getNumero());
        execucaoDto.setValorEmpenhado(execucao.getValorEmpenhado());
        execucaoDto.setEstornoEmpenho(execucao.getEstornoEmpenho());
        execucaoDto.setCancelamentoRestosAPagar(execucao.getCancelamentoRestosAPagar());
        execucaoDto.setValorAEmpenhar(execucao.getValorAEmpenhar());
        execucaoDto.setValorTotal(execucao.getValorTotal());
        return execucaoDto;
    }

    public Long getIdExecucao() {
        return idExecucao;
    }

    public void setIdExecucao(Long idExecucao) {
        this.idExecucao = idExecucao;
    }

    public String getFonteDespesa() {
        return fonteDespesa;
    }

    public void setFonteDespesa(String fonteDespesa) {
        this.fonteDespesa = fonteDespesa;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getValorEmpenhado() {
        return valorEmpenhado;
    }

    public void setValorEmpenhado(BigDecimal valorEmpenhado) {
        this.valorEmpenhado = valorEmpenhado;
    }

    public BigDecimal getEstornoEmpenho() {
        return estornoEmpenho;
    }

    public void setEstornoEmpenho(BigDecimal estornoEmpenho) {
        this.estornoEmpenho = estornoEmpenho;
    }

    public BigDecimal getCancelamentoRestosAPagar() {
        return cancelamentoRestosAPagar;
    }

    public void setCancelamentoRestosAPagar(BigDecimal cancelamentoRestosAPagar) {
        this.cancelamentoRestosAPagar = cancelamentoRestosAPagar;
    }

    public BigDecimal getValorAEmpenhar() {
        return valorAEmpenhar;
    }

    public void setValorAEmpenhar(BigDecimal valorAEmpenhar) {
        this.valorAEmpenhar = valorAEmpenhar;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
