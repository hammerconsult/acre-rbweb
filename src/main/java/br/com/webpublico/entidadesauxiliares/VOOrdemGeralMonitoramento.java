package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.MonitoramentoFiscal;
import br.com.webpublico.entidades.OrdemGeralMonitoramento;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class VOOrdemGeralMonitoramento {

    private Long numero;
    private String protocolo;
    private Date dataInicial;
    private Date dataFinal;
    private Date dataCriacao;
    private String situacao;
    private String fiscais;
    private List<VOMonitoramentoFiscal> monitoramentos;

    public VOOrdemGeralMonitoramento(OrdemGeralMonitoramento ordemGeralMonitoramento) {
        monitoramentos = Lists.newArrayList();
        numero = ordemGeralMonitoramento.getNumero();
        if (!Strings.isNullOrEmpty(ordemGeralMonitoramento.getNumeroProtocolo())) {
            protocolo = ordemGeralMonitoramento.getNumeroProtocolo() + "/" + ordemGeralMonitoramento.getAnoProtocolo();
        } else {
            protocolo = "";
        }
        dataInicial = ordemGeralMonitoramento.getDataInicial();
        dataFinal = ordemGeralMonitoramento.getDataFinal();
        dataCriacao = ordemGeralMonitoramento.getDataCriacao();
        situacao = ordemGeralMonitoramento.getSituacaoOGM().getDescricao();
        fiscais = "";
        for (MonitoramentoFiscal monitoramentoFiscal : ordemGeralMonitoramento.getMonitoramentosFiscais()) {
            monitoramentos.add(new VOMonitoramentoFiscal(monitoramentoFiscal));
            fiscais += monitoramentos.get(monitoramentos.size() - 1).getFiscais();
        }
        if (fiscais.endsWith(", ")) {
            fiscais = fiscais.substring(0, fiscais.length() - 2);
        }
    }

    public List<VOMonitoramentoFiscal> getMonitoramentos() {
        return monitoramentos;
    }

    public void setMonitoramentos(List<VOMonitoramentoFiscal> monitoramentos) {
        this.monitoramentos = monitoramentos;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getFiscais() {
        return fiscais;
    }

    public void setFiscais(String fiscais) {
        this.fiscais = fiscais;
    }
}
