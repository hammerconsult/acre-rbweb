package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by william on 10/08/17.
 */
public class ValidadorDatasEMesesOutorga implements Serializable {
    private Date dataInicial;
    private Date dataInicialRegistroAdicionado;
    private Date dataFinal;
    private Date dataFinalRegistroAdicionado;
    private Mes mes;

    public ValidadorDatasEMesesOutorga(Date dataInicial, Date dataFinal, Mes mes) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.mes = mes;
    }

    public ValidadorDatasEMesesOutorga(Date dataInicial, Date dataFinal, Date dataInicialRegistroAdicionado, Date dataFinalRegistroAdicionado) {
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.dataInicialRegistroAdicionado = dataInicialRegistroAdicionado;
        this.dataFinalRegistroAdicionado = dataFinalRegistroAdicionado;
    }

    public void validarDatasAndMes() {
        ValidacaoException ve = new ValidacaoException();
        if (getDataInicial() != null && getDataFinal() != null) {
            Calendar dataInicial = Calendar.getInstance();
            Calendar dataFinal = Calendar.getInstance();
            dataInicial.setTime(getDataInicial());
            dataFinal.setTime(getDataFinal());

            if (getDataInicial().after(getDataFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser menor ou igual a Data Final.");
            }
            if (dataInicial.get(Calendar.MONTH) + 1 != getMes().getNumeroMes()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve pertencer ao mês de " + getMes());

            }
            if (dataFinal.get(Calendar.MONTH) + 1 != getMes().getNumeroMes()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final deve pertencer ao mês de " + getMes());
            }
        }
        ve.lancarException();
    }

    public void validarPeriodosVigentes() {
        ValidacaoException ve = new ValidacaoException();
        if (getDataInicial().compareTo(getDataInicialRegistroAdicionado()) >= 0 && getDataInicial().compareTo(getDataFinalRegistroAdicionado()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data " + DataUtil.getDataFormatada(getDataInicial()) + " já se encontra em um período vigente.");
        }
        if (getDataFinal().compareTo(getDataInicialRegistroAdicionado()) >= 0 && getDataFinal().compareTo(getDataFinalRegistroAdicionado()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data " + DataUtil.getDataFormatada(getDataFinal()) + " já se encontra em um período vigente.");
        }
        if (getDataInicial().before(getDataInicialRegistroAdicionado()) && getDataFinal().after(getDataFinalRegistroAdicionado())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe registro vigênte no intervalo informado.");
        }
        ve.lancarException();
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

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Date getDataInicialRegistroAdicionado() {
        return dataInicialRegistroAdicionado;
    }

    public void setDataInicialRegistroAdicionado(Date dataInicialRegistroAdicionado) {
        this.dataInicialRegistroAdicionado = dataInicialRegistroAdicionado;
    }

    public Date getDataFinalRegistroAdicionado() {
        return dataFinalRegistroAdicionado;
    }

    public void setDataFinalRegistroAdicionado(Date dataFinalRegistroAdicionado) {
        this.dataFinalRegistroAdicionado = dataFinalRegistroAdicionado;
    }
}
