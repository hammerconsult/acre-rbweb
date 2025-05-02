package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mateus on 19/09/17.
 */
public class ProgramacaoFeriasComAssinaturaPeriodoAquisitivo implements Serializable {
    private Date dataInicial;
    private Date dataFinal;
    private Date dataLimite;

    public ProgramacaoFeriasComAssinaturaPeriodoAquisitivo() {
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

    public Date getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(Date dataLimite) {
        this.dataLimite = dataLimite;
    }
}


