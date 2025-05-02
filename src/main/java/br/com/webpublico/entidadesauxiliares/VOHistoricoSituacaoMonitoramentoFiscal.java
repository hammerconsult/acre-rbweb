package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.HistoricoSituacaoMonitoramentoFiscal;

import java.util.Date;

public class VOHistoricoSituacaoMonitoramentoFiscal {

    private String situacao;
    private Date data;

    public VOHistoricoSituacaoMonitoramentoFiscal(HistoricoSituacaoMonitoramentoFiscal historico) {
        data = historico.getData();
        situacao = historico.getSituacaoMonitoramentoFiscal().getDescricao();
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
}
