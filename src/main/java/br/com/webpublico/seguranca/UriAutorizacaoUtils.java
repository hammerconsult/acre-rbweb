package br.com.webpublico.seguranca;

import br.com.webpublico.enums.Direito;

public class UriAutorizacaoUtils {

    private UriAutorizacaoUtils() {
    }

    public static Direito obterDireitoNecessarioParaAcessar(String uri) {
        if (uri.matches(".*/(lista|visualizar)\\.(xhtml|jsf)")) {
            return Direito.LEITURA;
        } else if (uri.matches(".*/edita\\.(xhtml|jsf)")) {
            return Direito.ESCRITA;
        }
        return Direito.LEITURA;
    }
}
