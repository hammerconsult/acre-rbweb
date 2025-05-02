/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import javax.swing.text.MaskFormatter;
import java.math.BigDecimal;


/**
 * @author reidocrime
 */
public class FormataValoresUtil {

    public static String adicionaPontoNoValor(String texto, int casasDecimais) {
        try {
            MaskFormatter mask = new MaskFormatter("########0.00");
            mask.setValueContainsLiteralCharacters(false);

            return mask.valueToString(texto);

        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao formatar valores");
        }
    }

    public static String valorConvertidoR$(String texto) {
        try {
            MaskFormatter mask = new MaskFormatter("###,###,##0.00");

//            mask.setValueContainsLiteralCharacters(false);
//            return mask.valueToString(texto);


            StringBuilder stringBuilder = new StringBuilder(texto);
            stringBuilder.insert(texto.length() - 2, '.');



            return Util.formataValor(BigDecimal.valueOf(new Double(stringBuilder.toString())));
        } catch (Exception ex) {
//            throw new ExcecaoNegocioGenerica("Erro ao formatar valores");
            return texto;
        }

    }
}
