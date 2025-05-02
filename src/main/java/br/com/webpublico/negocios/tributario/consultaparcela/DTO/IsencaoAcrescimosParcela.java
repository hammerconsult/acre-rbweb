package br.com.webpublico.negocios.tributario.consultaparcela.DTO;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Date;

public class IsencaoAcrescimosParcela {
    private Boolean isentaJuros;
    private Boolean isentaMulta;
    private Boolean isentaCorrecao;
    private DateTime dataIsencao;


    public IsencaoAcrescimosParcela(Object[] obj) {
        this.isentaJuros = ((BigDecimal) obj[0]).compareTo(BigDecimal.ZERO) > 0;
        this.isentaMulta = ((BigDecimal) obj[1]).compareTo(BigDecimal.ZERO) > 0;
        this.isentaCorrecao = ((BigDecimal) obj[2]).compareTo(BigDecimal.ZERO) > 0;
        this.dataIsencao = new DateTime((Date) obj[3]);
    }

    public Boolean getIsentaJuros() {
        return isentaJuros;
    }

    public void setIsentaJuros(Boolean isentaJuros) {
        this.isentaJuros = isentaJuros;
    }

    public Boolean getIsentaMulta() {
        return isentaMulta;
    }

    public void setIsentaMulta(Boolean isentaMulta) {
        this.isentaMulta = isentaMulta;
    }

    public Boolean getIsentaCorrecao() {
        return isentaCorrecao;
    }

    public void setIsentaCorrecao(Boolean isentaCorrecao) {
        this.isentaCorrecao = isentaCorrecao;
    }

    public DateTime getDataIsencao() {
        return dataIsencao;
    }

    public void setDataIsencao(DateTime dataIsencao) {
        this.dataIsencao = dataIsencao;
    }
}
