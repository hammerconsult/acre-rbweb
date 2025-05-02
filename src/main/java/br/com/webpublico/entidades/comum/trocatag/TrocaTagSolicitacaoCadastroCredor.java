package br.com.webpublico.entidades.comum.trocatag;

import br.com.webpublico.entidades.ConfiguracaoTributario;
import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagSolicitacaoCadastroCredor extends TrocaTagEmail {
    private static final Logger logger = LoggerFactory.getLogger(TrocaTagSolicitacaoCadastroCredor.class);
    private ConfiguracaoTributario configuracaoTributario;
    private SolicitacaoCadastroPessoa solicitacaoCadastroPessoa;

    public TrocaTagSolicitacaoCadastroCredor(ConfiguracaoTributario configuracaoTributario, SolicitacaoCadastroPessoa solicitacaoCadastroPessoa) {
        this.configuracaoTributario = configuracaoTributario;
        this.solicitacaoCadastroPessoa = solicitacaoCadastroPessoa;

        addicionarField(new Field(TagComum.LINK, configuracaoTributario.getUrlPortalContribuinte() +
            "/credor/solicitacao-cadastro/" + solicitacaoCadastroPessoa.getKey()));
        addicionarField(new Field(TagComum.LINK_REJEICAO, configuracaoTributario.getUrlPortalContribuinte() +
            "/credor/correcao-solicitacao-cadastro/" + solicitacaoCadastroPessoa.getKey()));
        addicionarField(new Field(TagComum.DATA, DataUtil.getDataFormatada(solicitacaoCadastroPessoa.getDataRejeicao())));
        addicionarField(new Field(TagComum.MOTIVO, solicitacaoCadastroPessoa.getMotivoRejeicao()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
