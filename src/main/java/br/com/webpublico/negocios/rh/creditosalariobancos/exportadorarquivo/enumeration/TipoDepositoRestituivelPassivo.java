package br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration;

/**
 * @author Daniel Franco
 * @since 17/05/2016 08:53
 */
public enum TipoDepositoRestituivelPassivo {
    INSCRICAO_ORCAMENTARIA(1),
    INSCRICAO_NAO_ORCAMENTARIA(2),
    INSCRICAO_CISAO_FUSAO_EXTINCAO(3),
    BAIXA_CAIXA(4),
    BAIXA_BANCO(5),
    BAIXA_CISAO_FUSAO_EXTINCAO(6),
    CANCELAMENTO_RESTITUIVEIS(7)
    ;

    int codigo;

    TipoDepositoRestituivelPassivo(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static TipoDepositoRestituivelPassivo withCodigo(int codigo) {
        for (TipoDepositoRestituivelPassivo tipo : TipoDepositoRestituivelPassivo.values()) {
            if (tipo.getCodigo() == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Nenhum TipoDepositoRestituivelPassivo encontrado com Código [" + codigo + "]");
    }
}
