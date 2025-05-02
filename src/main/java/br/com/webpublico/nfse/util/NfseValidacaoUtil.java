package br.com.webpublico.nfse.util;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.util.AtributoMetadata;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.anotacoes.Obrigatorio;

import java.util.Iterator;

public class NfseValidacaoUtil {

    public static void validarCampos(Object selecionado) throws ValidacaoException {
        EntidadeMetaData metadata = new EntidadeMetaData(selecionado.getClass());

        NfseValidacaoException exception = new NfseValidacaoException();

        for (Iterator<AtributoMetadata> it = metadata.getAtributos().iterator(); it.hasNext(); ) {
            AtributoMetadata object = it.next();
            if (object.getAtributo().isAnnotationPresent(Obrigatorio.class)) {
                if (object.getString(selecionado).trim().equals("") || ((AtributoMetadata) object).getString(selecionado).trim() == null) {
                    exception.adicionarMensagem("O campo " + object.getEtiqueta() + " deve ser informado.");
                    continue;
                }
            }

            if (object.getAtributo().isAnnotationPresent(br.com.webpublico.util.anotacoes.Length.class)) {
                int max = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).maximo();
                int min = (object.getAtributo().getAnnotation(br.com.webpublico.util.anotacoes.Length.class)).minimo();
                if (object.getString(selecionado).length() > max) {
                    exception.adicionarMensagem("O campo " + object.getEtiqueta() + " deve ter menos que " + max + " caracteres.");
                }
                if (object.getString(selecionado).length() < min) {
                    exception.adicionarMensagem("O campo " + object.getEtiqueta() + " deve ter mais que " + min + " caracteres.");
                }
            }
        }

        exception.lancarException();
    }
}
