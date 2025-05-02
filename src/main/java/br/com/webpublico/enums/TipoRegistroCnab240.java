package br.com.webpublico.enums;

/**
 * @author Alex
 * @since 08/06/2016 09:47
 */
public enum TipoRegistroCnab240 {

    HEADER_ARQUIVO("0"),
    HEADER_LOTE("1"),
    SEGMENTO_A("3A"),
    SEGMENTO_B("3B"),
    SEGMENTO_Z("3Z"),
    TRAILER_LOTE("5"),
    TRAILER_ARQUIVO("9");

    private String codigo;

    TipoRegistroCnab240(String codigo) {
        this.codigo = codigo;
    }

    public static TipoRegistroCnab240 getTipoPorCodigo(String codigo) {
        for (TipoRegistroCnab240 tipoRegistroCnab240 : TipoRegistroCnab240.values()) {
            if (tipoRegistroCnab240.getCodigo().equals(codigo)) {
                return tipoRegistroCnab240;
            }
        }
        return null;
    }

    public String getCodigo() {
        return codigo;
    }
}
