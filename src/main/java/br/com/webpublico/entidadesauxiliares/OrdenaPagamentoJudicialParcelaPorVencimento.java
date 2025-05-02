package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.PagamentoJudicialParcela;

import java.util.Comparator;


public class OrdenaPagamentoJudicialParcelaPorVencimento implements Comparator<PagamentoJudicialParcela> {
    @Override
    public int compare(PagamentoJudicialParcela r1, PagamentoJudicialParcela r2) {
        return r1.getVencimento().compareTo(r2.getVencimento());  //To change body of implemented methods use File | Settings | File Templates.
    }
}
