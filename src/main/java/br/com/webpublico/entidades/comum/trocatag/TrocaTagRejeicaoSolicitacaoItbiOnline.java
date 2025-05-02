package br.com.webpublico.entidades.comum.trocatag;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.SolicitacaoItbiOnline;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagRejeicaoSolicitacaoItbiOnline extends TrocaTagEmail {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagRejeicaoSolicitacaoItbiOnline.class);

    public TrocaTagRejeicaoSolicitacaoItbiOnline(ConfiguracaoTributario configuracaoTributario,
                                                 SolicitacaoItbiOnline solicitacaoItbiOnline,
                                                 String motivoRejeicao) {
        addicionarField(new Field(TagComum.LINK, configuracaoTributario.getUrlPortalContribuinte() +
            "solicitacao-itbi-online/editar/" + solicitacaoItbiOnline.getId() + "/"));
        addicionarField(new Field(TagComum.MOTIVO, motivoRejeicao));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
