package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CalculoITBI;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 11/03/14
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
public class OrdenaITBI  implements Comparator<CalculoITBI> {
    @Override
    public int compare(CalculoITBI o1, CalculoITBI o2) {
    return o2.getSequencia().compareTo(o1.getSequencia());
    }
}
