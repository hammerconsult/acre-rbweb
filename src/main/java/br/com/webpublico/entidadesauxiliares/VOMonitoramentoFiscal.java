package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.FiscalMonitoramento;
import br.com.webpublico.entidades.HistoricoSituacaoMonitoramentoFiscal;
import br.com.webpublico.entidades.MonitoramentoFiscal;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class VOMonitoramentoFiscal {

    private String cadastroEconomico;
    private Date dataInicial;
    private Date dataFinal;
    private String situacao;
    private Date dataFinalLevantamento;
    private String fiscais;
    private List<VOHistoricoSituacaoMonitoramentoFiscal> historicos;

    public VOMonitoramentoFiscal(MonitoramentoFiscal monitoramentoFiscal) {
        historicos = Lists.newArrayList();
        cadastroEconomico = monitoramentoFiscal.getCadastroEconomico().toString();
        dataInicial = monitoramentoFiscal.getDataInicialMonitoramento();
        dataFinal = monitoramentoFiscal.getDataFinalMonitoramento();
        situacao = monitoramentoFiscal.getSituacaoMF().getDescricao();
        dataFinalLevantamento = monitoramentoFiscal.getDataLevantamentoFinal();
        fiscais = "";
        for (FiscalMonitoramento fiscalMonitoramento : monitoramentoFiscal.getFiscaisMonitoramento()) {
            fiscais += fiscalMonitoramento.getAuditorFiscal().getLogin() + ", ";
        }
        if (fiscais.endsWith(", ")) {
            fiscais = fiscais.substring(0, fiscais.length() - 2);
        }
        for (HistoricoSituacaoMonitoramentoFiscal historico : monitoramentoFiscal.getHistoricoSituacoesMonitoramentoFiscal()) {
            historicos.add(new VOHistoricoSituacaoMonitoramentoFiscal(historico));
        }
        Collections.sort(historicos, new Comparator<VOHistoricoSituacaoMonitoramentoFiscal>() {
            @Override
            public int compare(VOHistoricoSituacaoMonitoramentoFiscal o1, VOHistoricoSituacaoMonitoramentoFiscal o2) {
                return o2.getData().compareTo(o1.getData());
            }
        });
    }

    public String getFiscais() {
        return fiscais;
    }

    public void setFiscais(String fiscais) {
        this.fiscais = fiscais;
    }

    public String getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(String cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
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

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Date getDataFinalLevantamento() {
        return dataFinalLevantamento;
    }

    public void setDataFinalLevantamento(Date dataFinalLevantamento) {
        this.dataFinalLevantamento = dataFinalLevantamento;
    }

    public List<VOHistoricoSituacaoMonitoramentoFiscal> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<VOHistoricoSituacaoMonitoramentoFiscal> historicos) {
        this.historicos = historicos;
    }
}
