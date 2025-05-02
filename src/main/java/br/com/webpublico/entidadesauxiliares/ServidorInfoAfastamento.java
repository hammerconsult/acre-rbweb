package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by mga on 02/06/2017.
 */
public class ServidorInfoAfastamento {

    private Integer item;
    private Date inicio;
    private Date fim;
    private String tipoAfastamento;
    private String retornoInformado;
    private Date dataCadastro;
    private String descontarDiaTrabalhando;
    private String descontarTempoServico;
    private String descontarDescancoSemanal;
    private String descontarTempoAposentadoria;
    private String pagoEntidadePrevidenciaria;
    private String calcularValeTransporte;
    private String processaFolhaNormal;
    private String permiteAfastamentoCC;
    private String naoPermiteProgressao;

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(String tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }

    public String getRetornoInformado() {
        return retornoInformado;
    }

    public void setRetornoInformado(String retornoInformado) {
        this.retornoInformado = retornoInformado;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public String getDescontarDiaTrabalhando() {
        return descontarDiaTrabalhando;
    }

    public void setDescontarDiaTrabalhando(String descontarDiaTrabalhando) {
        this.descontarDiaTrabalhando = descontarDiaTrabalhando;
    }

    public String getDescontarTempoServico() {
        return descontarTempoServico;
    }

    public void setDescontarTempoServico(String descontarTempoServico) {
        this.descontarTempoServico = descontarTempoServico;
    }

    public String getDescontarDescancoSemanal() {
        return descontarDescancoSemanal;
    }

    public void setDescontarDescancoSemanal(String descontarDescancoSemanal) {
        this.descontarDescancoSemanal = descontarDescancoSemanal;
    }

    public String getDescontarTempoAposentadoria() {
        return descontarTempoAposentadoria;
    }

    public void setDescontarTempoAposentadoria(String descontarTempoAposentadoria) {
        this.descontarTempoAposentadoria = descontarTempoAposentadoria;
    }

    public String getPagoEntidadePrevidenciaria() {
        return pagoEntidadePrevidenciaria;
    }

    public void setPagoEntidadePrevidenciaria(String pagoEntidadePrevidenciaria) {
        this.pagoEntidadePrevidenciaria = pagoEntidadePrevidenciaria;
    }

    public String getCalcularValeTransporte() {
        return calcularValeTransporte;
    }

    public void setCalcularValeTransporte(String calcularValeTransporte) {
        this.calcularValeTransporte = calcularValeTransporte;
    }

    public String getProcessaFolhaNormal() {
        return processaFolhaNormal;
    }

    public void setProcessaFolhaNormal(String processaFolhaNormal) {
        this.processaFolhaNormal = processaFolhaNormal;
    }

    public String getPermiteAfastamentoCC() {
        return permiteAfastamentoCC;
    }

    public void setPermiteAfastamentoCC(String permiteAfastamentoCC) {
        this.permiteAfastamentoCC = permiteAfastamentoCC;
    }

    public String getNaoPermiteProgressao() {
        return naoPermiteProgressao;
    }

    public void setNaoPermiteProgressao(String naoPermiteProgressao) {
        this.naoPermiteProgressao = naoPermiteProgressao;
    }
}
