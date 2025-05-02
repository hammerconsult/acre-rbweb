package br.com.webpublico.util;

import org.slf4j.Logger;

public final class LogUtil {

    private static final String MENSAGEM_ERRO = "{}: {}";
    private static final String DETALHES_ERRO = "Detalhes do erro. ";

    private LogUtil() {
    }

    public static void registrarExcecao(Logger logger, String mensagemErro, Exception e) {
        logger.error(MENSAGEM_ERRO, mensagemErro, e.getMessage());
        logger.debug(DETALHES_ERRO, e);
    }

}
