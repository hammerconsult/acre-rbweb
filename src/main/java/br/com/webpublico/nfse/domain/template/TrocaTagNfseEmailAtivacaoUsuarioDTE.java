package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.dte.entidades.ConfiguracaoDte;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagNfseEmailAtivacaoUsuarioDTE extends TrocaTagNfse {
    private static final Logger logger = LoggerFactory.getLogger(TrocaTagNfseEmailAtivacaoUsuarioDTE.class);
    private UsuarioWeb user;
    private Cidade municipio;

    public TrocaTagNfseEmailAtivacaoUsuarioDTE(UsuarioWeb user, ConfiguracaoDte configuracaoDte) {
        super(configuracaoDte);
        this.user = user;
        this.municipio = configuracaoDte.getCidade();
        adicionarFields();
    }

    public void adicionarFields() {
        addicionarField(new Field(TagEmailAtivacaoUsuario.MUNICIPIO, municipio.getNome()));
        addicionarField(new Field(TagEmailAtivacaoUsuario.USUARIO, user.getPessoa().getNome()));
        addicionarField(new Field(TagEmailAtivacaoUsuario.LINK, configuracaoDte.getUrlDte() + "/#/activate?key=" + user.getActivationKey()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
