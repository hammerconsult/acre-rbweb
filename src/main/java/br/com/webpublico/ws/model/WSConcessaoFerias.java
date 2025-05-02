package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.ConcessaoFeriasLicenca;
import br.com.webpublico.enums.Mes;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by peixe on 12/08/2015.
 */
public class WSConcessaoFerias implements Serializable {

    private Mes mesPagamento;
    private Integer anoPagamento;
    private Date inicioVigencia;
    private Date finalVigencia;
    private Date diasAbonoPecuniario;


    public WSConcessaoFerias() {
    }

    public WSConcessaoFerias(Mes mesPagamento, Integer anoPagamento, Date inicioVigencia, Date finalVigencia, Date diasAbonoPecuniario) {
        this.mesPagamento = mesPagamento;
        this.anoPagamento = anoPagamento;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.diasAbonoPecuniario = diasAbonoPecuniario;
    }

    public static List<WSConcessaoFerias> convertConcessaoToWSConcessaoFeriasList(List<ConcessaoFeriasLicenca> lista) {
        List<WSConcessaoFerias> wsConcessaoFerias = Lists.newLinkedList();
        for (ConcessaoFeriasLicenca concessao : lista) {
            WSConcessaoFerias ws = new WSConcessaoFerias(Mes.getMesToInt(concessao.getMes()),concessao.getAno(),concessao.getDataInicial(),concessao.getDataFinal(), concessao.getDataInicialAbonoPecuniario());
            wsConcessaoFerias.add(ws);
        }
        return wsConcessaoFerias;
    }

    public Mes getMesPagamento() {
        return mesPagamento;
    }

    public void setMesPagamento(Mes mesPagamento) {
        this.mesPagamento = mesPagamento;
    }

    public Integer getAnoPagamento() {
        return anoPagamento;
    }

    public void setAnoPagamento(Integer anoPagamento) {
        this.anoPagamento = anoPagamento;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getDiasAbonoPecuniario() {
        return diasAbonoPecuniario;
    }

    public void setDiasAbonoPecuniario(Date diasAbonoPecuniario) {
        this.diasAbonoPecuniario = diasAbonoPecuniario;
    }
}
