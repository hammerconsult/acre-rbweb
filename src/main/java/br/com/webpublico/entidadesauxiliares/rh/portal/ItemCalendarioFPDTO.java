package br.com.webpublico.entidadesauxiliares.rh.portal;

import br.com.webpublico.enums.MesCalendarioPagamento;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class ItemCalendarioFPDTO implements Serializable {

    private MesCalendarioPagamento mesCalendarioPagamento;
    private Integer diaEntregaDocumentos;
    private Integer diaCorteConsignacoes;
    private Integer ultimoDiaProcessamento;
    private Integer diaPagamento;
    private Date dataDiaPagamento;
    private Boolean pagamentoEfetuado;
    private Boolean decimoTerceiro;


    public ItemCalendarioFPDTO(MesCalendarioPagamento mesCalendarioPagamento, Integer diaEntregaDocumentos, Integer diaCorteConsignacoes, Integer ultimoDiaProcessamento, Integer diaPagamento, Boolean pagamentoEfetuado, Boolean decimoTerceiro) {
        this.mesCalendarioPagamento = mesCalendarioPagamento;
        this.diaEntregaDocumentos = diaEntregaDocumentos;
        this.diaCorteConsignacoes = diaCorteConsignacoes;
        this.ultimoDiaProcessamento = ultimoDiaProcessamento;
        this.diaPagamento = diaPagamento;
        this.pagamentoEfetuado = pagamentoEfetuado;
        this.decimoTerceiro = decimoTerceiro;

        init(mesCalendarioPagamento, diaPagamento);
    }

    private void init(MesCalendarioPagamento mesCalendarioPagamento, Integer diaPagamento) {
        if (mesCalendarioPagamento != null) {
            DateTime dateTime = new DateTime();
            MesCalendarioPagamento mes = mesCalendarioPagamento;
            if (mesCalendarioPagamento.DECIMO.equals(mesCalendarioPagamento)) {
                mes = MesCalendarioPagamento.DEZEMBRO;
            }
            dateTime = dateTime.withMonthOfYear(mes.getNumeroDoMes());
            dateTime = dateTime.withDayOfMonth(diaPagamento);
            dataDiaPagamento = dateTime.toDate();
        }
    }

    public MesCalendarioPagamento getMesCalendarioPagamento() {
        return mesCalendarioPagamento;
    }

    public void setMesCalendarioPagamento(MesCalendarioPagamento mesCalendarioPagamento) {
        this.mesCalendarioPagamento = mesCalendarioPagamento;
    }

    public Integer getDiaEntregaDocumentos() {
        return diaEntregaDocumentos;
    }

    public void setDiaEntregaDocumentos(Integer diaEntregaDocumentos) {
        this.diaEntregaDocumentos = diaEntregaDocumentos;
    }

    public Integer getDiaCorteConsignacoes() {
        return diaCorteConsignacoes;
    }

    public void setDiaCorteConsignacoes(Integer diaCorteConsignacoes) {
        this.diaCorteConsignacoes = diaCorteConsignacoes;
    }

    public Integer getUltimoDiaProcessamento() {
        return ultimoDiaProcessamento;
    }

    public void setUltimoDiaProcessamento(Integer ultimoDiaProcessamento) {
        this.ultimoDiaProcessamento = ultimoDiaProcessamento;
    }

    public Integer getDiaPagamento() {
        return diaPagamento;
    }

    public void setDiaPagamento(Integer diaPagamento) {
        this.diaPagamento = diaPagamento;
    }

    public Date getDataDiaPagamento() {
        return dataDiaPagamento;
    }

    public void setDataDiaPagamento(Date dataDiaPagamento) {
        this.dataDiaPagamento = dataDiaPagamento;
    }

    public Boolean getPagamentoEfetuado() {
        return pagamentoEfetuado;
    }

    public void setPagamentoEfetuado(Boolean pagamentoEfetuado) {
        this.pagamentoEfetuado = pagamentoEfetuado;
    }

    public Boolean getDecimoTerceiro() {
        return decimoTerceiro;
    }

    public void setDecimoTerceiro(Boolean decimoTerceiro) {
        this.decimoTerceiro = decimoTerceiro;
    }
}
