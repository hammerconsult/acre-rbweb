package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.SubvencaoParcela;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 04/03/15
 * Time: 11:59
 * To change this template use File | Settings | File Templates.
 */
public class OrdenarParcelasSubvencaoPorVencimento implements Comparator<SubvencaoParcela> {
    @Override
    public int compare(SubvencaoParcela o1, SubvencaoParcela o2) {
        if (o1 != null && o2 != null) {
            return o1.getVencimento().compareTo(o2.getVencimento());  //To change body of implemented methods use File | Settings | File Templates.
        } else {
            return 0;
        }

    }
}
