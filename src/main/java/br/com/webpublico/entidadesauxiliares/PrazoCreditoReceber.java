package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.util.StringUtil;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;

/**
 * Created by rodolfo on 01/09/17.
 */
public class PrazoCreditoReceber {

    LocalDate inicio, fim;

    public PrazoCreditoReceber(LocalDate inicio, LocalDate fim) {
        this.inicio = inicio;
        this.fim = fim;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrazoCreditoReceber prazo = (PrazoCreditoReceber) o;

        if (inicio != null ? !inicio.equals(prazo.inicio) : prazo.inicio != null) return false;
        return fim != null ? fim.equals(prazo.fim) : prazo.fim == null;
    }

    @Override
    public int hashCode() {
        int result = inicio != null ? inicio.hashCode() : 0;
        result = 31 * result + (fim != null ? fim.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String tipoPrazo = " - LP";
        if (Days.daysBetween(inicio, fim).getDays() < 364) {
            tipoPrazo = " - CP";
        }
        if (Years.yearsBetween(inicio, fim).getYears() > 1) {
            return "Exercícios anteriores" + tipoPrazo;

        } else {
            return "de " + StringUtil.cortarOuCompletarEsquerda(inicio.getDayOfMonth() + "", 2, "0") + " de " + getNomeDoMes(inicio.getMonthOfYear())
                + " a " + StringUtil.cortarOuCompletarEsquerda(fim.getDayOfMonth() + "", 2, "0") + " de " + getNomeDoMes(fim.getMonthOfYear()) + " de " + fim.getYear()
                + tipoPrazo;
        }
    }

    public String getNomeDoMes(Integer mes) {
        return Mes.getMesToInt(mes).getDescricao().substring(0, 3).toLowerCase();
    }
}
