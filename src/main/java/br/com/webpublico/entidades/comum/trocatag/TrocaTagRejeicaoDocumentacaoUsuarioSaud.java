package br.com.webpublico.entidades.comum.trocatag;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.UsuarioSaud;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagRejeicaoDocumentacaoUsuarioSaud extends TrocaTagEmail {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagRejeicaoDocumentacaoUsuarioSaud.class);

    public TrocaTagRejeicaoDocumentacaoUsuarioSaud(ConfiguracaoTributario configuracaoTributario,
                                                   UsuarioSaud usuarioSaud) {
        addicionarField(new Field(TagComum.LINK, configuracaoTributario.getUrlPortalContribuinte() +
            "usuario-saud/rejeicao-documentacao/" + usuarioSaud.getId()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
