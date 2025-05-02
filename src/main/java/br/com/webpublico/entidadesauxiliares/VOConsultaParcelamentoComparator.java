package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/07/15
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class VOConsultaParcelamentoComparator implements Comparator<VOConsultaParcelamento> {

    @Override
    public int compare(VOConsultaParcelamento o1, VOConsultaParcelamento o2) {
        int i = o1.getResultadoParcela().getCadastro().compareTo(o2.getResultadoParcela().getCadastro());
        if (i != 0) {
            return i;
        }
        i = o1.getResultadoParcela().getDivida().compareTo(o2.getResultadoParcela().getDivida());
        if (i != 0) {
            return i;
        }
        i = o1.getResultadoParcela().getExercicio().compareTo(o2.getResultadoParcela().getExercicio());
        if (i != 0) {
            return i;
        }
        i = o1.getNumeroParcelamento().compareTo(o2.getNumeroParcelamento());
        if (i != 0) {
            return i;
        }
        i = o1.getNumeroReparcelamento().compareTo(o2.getNumeroReparcelamento());
        if (i != 0) {
            return i;
        }
        i = o1.getResultadoParcela().getSd().compareTo(o2.getResultadoParcela().getSd());
        if (i != 0) {
            return i;
        }
        i = (o2.getResultadoParcela().getCotaUnica() ? BigDecimal.ONE : BigDecimal.ZERO).compareTo(o1.getResultadoParcela().getCotaUnica() ? BigDecimal.ONE : BigDecimal.ZERO);
        if (i != 0) {
            return i;
        }
        i = o1.getResultadoParcela().getVencimento().compareTo(o2.getResultadoParcela().getVencimento());
        if (i != 0) {
            return i;
        }
        i = o1.getResultadoParcela().getIdValorDivida().compareTo(o2.getResultadoParcela().getIdValorDivida());
        if (i != 0) {
            return i;
        }
        i = o1.getResultadoParcela().getParcela().compareTo(o2.getResultadoParcela().getParcela());
        if (i != 0) {
            return i;
        }
        i = o1.getResultadoParcela().getSituacao().compareTo(o2.getResultadoParcela().getSituacao());
        if (i != 0) {
            return i;
        }
        i = o1.getResultadoParcela().getReferencia().compareTo(o2.getResultadoParcela().getReferencia());
        if (i != 0) {
            return i;
        }
        return 0;
    }
}
