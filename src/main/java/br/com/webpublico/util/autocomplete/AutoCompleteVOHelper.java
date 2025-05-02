/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.autocomplete;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arthur
 */
public class AutoCompleteVOHelper<T> {

    public static List<AutoCompleteVO> createAutoCompleteVOList(List<Object[]> results) {
        List<AutoCompleteVO> lista = new ArrayList<>();
        try {
            for (Object[] o : results) {
                AutoCompleteVO ac = new AutoCompleteVO(Long.parseLong(o[0].toString()), (String) o[1]);
                lista.add(ac);
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Falha ao popular a lista de AutoCompleteVOs", e);
        }
        return lista;
    }

    private AutoCompleteVOHelper() {
    }
}
