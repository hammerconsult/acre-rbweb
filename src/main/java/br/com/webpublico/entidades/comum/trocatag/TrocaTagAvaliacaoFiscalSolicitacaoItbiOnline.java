package br.com.webpublico.entidades.comum.trocatag;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.SolicitacaoItbiOnline;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagAvaliacaoFiscalSolicitacaoItbiOnline extends TrocaTagEmail {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagAvaliacaoFiscalSolicitacaoItbiOnline.class);

    public TrocaTagAvaliacaoFiscalSolicitacaoItbiOnline(ConfiguracaoTributario configuracaoTributario,
                                                        SolicitacaoItbiOnline solicitacaoItbiOnline,
                                                        String observacao) {
        addicionarField(new Field(TagComum.LINK, configuracaoTributario.getUrlPortalContribuinte() +
            "solicitacao-itbi-online/avaliar/" + solicitacaoItbiOnline.getId() + "/"));
        addicionarField(new Field(TagComum.MOTIVO, observacao));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
