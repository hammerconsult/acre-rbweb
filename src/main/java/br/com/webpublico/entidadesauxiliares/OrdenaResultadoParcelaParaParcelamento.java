package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.util.Comparator;


public class OrdenaResultadoParcelaParaParcelamento implements Comparator<ResultadoParcela> {
    @Override
    public int compare(ResultadoParcela r1, ResultadoParcela r2) {
        int i = r1.getReferencia().compareTo(r2.getReferencia());
        if (i == 0) {
            i = r1.getVencimento().compareTo(r2.getVencimento());
        }
        return i;
    }
}
