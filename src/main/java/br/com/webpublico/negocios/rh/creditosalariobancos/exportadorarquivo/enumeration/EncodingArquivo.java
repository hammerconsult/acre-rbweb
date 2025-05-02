package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration;

import java.nio.charset.Charset;

/**
 * @author Daniel Franco
 * @since 24/05/2016 11:33
 */
public enum EncodingArquivo {

    WINDOWS_1252("Cp1252"),
    UNICODE("UTF-8"),
        ;
    String texto;

    EncodingArquivo(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    public Charset getCharset() {
        return Charset.forName(getTexto());
    }
}
