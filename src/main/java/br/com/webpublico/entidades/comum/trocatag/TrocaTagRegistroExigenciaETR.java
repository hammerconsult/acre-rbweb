package br.com.webpublico.entidades.comum.trocatag;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.ExigenciaETR;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagRegistroExigenciaETR extends TrocaTagEmail {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagRegistroExigenciaETR.class);
    private ConfiguracaoTributario configuracaoTributario;
    private ExigenciaETR exigenciaETR;

    public TrocaTagRegistroExigenciaETR(ConfiguracaoTributario configuracaoTributario, ExigenciaETR exigenciaETR) {
        this.configuracaoTributario = configuracaoTributario;
        this.exigenciaETR = exigenciaETR;

        addicionarField(new Field(TagComum.LINK, configuracaoTributario.getUrlPortalContribuinte() +
                "exigencia-etr/" + exigenciaETR.getId()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
