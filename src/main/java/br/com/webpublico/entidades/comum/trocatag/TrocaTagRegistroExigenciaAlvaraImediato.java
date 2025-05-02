package br.com.webpublico.entidades.comum.trocatag;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.ExigenciaAlvaraImediato;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagRegistroExigenciaAlvaraImediato extends TrocaTagEmail {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagRegistroExigenciaAlvaraImediato.class);
    private ConfiguracaoTributario configuracaoTributario;
    private ExigenciaAlvaraImediato exigenciaAlvaraImediato;

    public TrocaTagRegistroExigenciaAlvaraImediato(ConfiguracaoTributario configuracaoTributario, ExigenciaAlvaraImediato exigenciaAlvaraImediato) {
        this.configuracaoTributario = configuracaoTributario;
        this.exigenciaAlvaraImediato = exigenciaAlvaraImediato;

        addicionarField(new Field(TagComum.LINK, configuracaoTributario.getUrlPortalContribuinte() +
                "servicos-externos/exigencia-alvara-imediato/" + exigenciaAlvaraImediato.getId()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
