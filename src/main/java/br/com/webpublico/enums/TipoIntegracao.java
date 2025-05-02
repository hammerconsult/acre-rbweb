package br.com.webpublico.enums;

import java.io.Serializable;

public enum TipoIntegracao implements Serializable {

    INSCRICAO,
    ESTORNO_INSCRICAO,
    BAIXA_INSCRICAO,
    ARRECADACAO,
    ESTORNO_ARRECADACAO,
    DIVIDA_ATIVA,
    CANCELAMENTO_DIVIDA_ATIVA,
    BAIXA_DIVIDA_ATIVA;
}
