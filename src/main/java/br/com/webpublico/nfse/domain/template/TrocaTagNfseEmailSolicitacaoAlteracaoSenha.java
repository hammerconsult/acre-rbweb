package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagNfseEmailSolicitacaoAlteracaoSenha extends TrocaTagNfse {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagNfseEmailSolicitacaoAlteracaoSenha.class);
    private UsuarioWeb user;
    private Cidade municipio;

    public TrocaTagNfseEmailSolicitacaoAlteracaoSenha(UsuarioWeb user, ConfiguracaoNfse configuracaoTributario) {
        super(configuracaoTributario);
        this.user = user;
        this.municipio = configuracaoTributario.getCidade();
        adicionarFields();
    }

    public void adicionarFields() {
        addicionarField(new Field(TagEmailSolicitacaoAlteracaoSenha.MUNICIPIO, municipio.getNome()));
        addicionarField(new Field(TagEmailSolicitacaoAlteracaoSenha.USUARIO, user.getPessoa().getNome()));
        addicionarField(new Field(TagEmailSolicitacaoAlteracaoSenha.CPFCNPJ, user.getPessoa().getCpf_Cnpj()));
        addicionarField(new Field(TagEmailSolicitante.LINK, configuracaoNfse.getUrlNfse() + "/#/reset/finish?key=" + user.getResetKey()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
