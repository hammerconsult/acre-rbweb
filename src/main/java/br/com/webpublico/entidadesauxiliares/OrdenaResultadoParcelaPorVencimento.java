package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.util.Comparator;


public class OrdenaResultadoParcelaPorVencimento implements Comparator<ResultadoParcela> {
    @Override
    public int compare(ResultadoParcela r1, ResultadoParcela r2) {
        int i = r1.getVencimento().compareTo(r2.getVencimento());
        if (i == 0) {
            i = r1.getSequenciaParcela().compareTo(r2.getSequenciaParcela());
        }
        if (i == 0) {
            i = r1.getDivida().compareTo(r2.getDivida());
        }
        if (i == 0) {
            i = r1.getReferencia().compareTo(r2.getReferencia());
        }
        return i;
    }
}
