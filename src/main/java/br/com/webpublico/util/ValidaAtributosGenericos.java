/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.Atributo;
import br.com.webpublico.enums.TipoAtributo;
import br.com.webpublico.entidades.ValorAtributo;

import java.util.Map;

/**
 * @author gustavo
 */
public class ValidaAtributosGenericos {

    public static boolean validaAtributosdGenericos(Map<Atributo, ValorAtributo> atributos) {
        boolean retorno = true;
        for (Atributo atributo : atributos.keySet()) {
            if (atributo.getObrigatorio() && valorCorreto(atributos.get(atributo), atributo) == null) {
                retorno = false;
                FacesUtil.addError("Campo Obrigatório.", "O atributo " + atributo.getNome() + " de " + atributo.getClasseDoAtributo().getDescricao() + " deve ser preenchido");
            }
        }
        return retorno;
    }

    private static Object valorCorreto(ValorAtributo valor, Atributo atributo) {
        if (atributo.getTipoAtributo() == TipoAtributo.DATE) {
            return valor.getValorDate();
        }
        if (atributo.getTipoAtributo() == TipoAtributo.DECIMAL) {
            return valor.getValorDecimal();
        }
        if (atributo.getTipoAtributo() == TipoAtributo.DISCRETO) {
            return valor.getValorDiscreto();
        }
        if (atributo.getTipoAtributo() == TipoAtributo.INTEIRO) {
            return valor.getValorInteiro();
        }
        if (atributo.getTipoAtributo() == TipoAtributo.STRING) {
            if (valor.getValorString().trim().isEmpty()) {
                return null;
            } else {
                return valor.getValorString();
            }

        } else {
            return null;
        }
    }
}
