package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.PontoComercial;
import br.com.webpublico.util.StringUtil;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 21/07/14
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public class OrdenaPontosComerciais implements Comparator<PontoComercial> {
    @Override
    public int compare(PontoComercial ponto1, PontoComercial ponto2) {
        Integer numeroBox1;
        Integer numeroBox2;
        if (ponto1.getNumeroBox() != null) {
            numeroBox1 = Integer.parseInt(StringUtil.retornaApenasNumeros(ponto1.getNumeroBox()));
        } else {
            numeroBox1 = 0;
        }
        if (ponto2.getNumeroBox() != null) {
            numeroBox2 = Integer.parseInt(StringUtil.retornaApenasNumeros(ponto2.getNumeroBox()));
        } else {
            numeroBox2 = 0;
        }
        return numeroBox1.compareTo(numeroBox2);
    }
}
