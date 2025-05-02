package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.dte.entidades.ConfiguracaoDte;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagEmailSolicitacaoAlteracaoSenhaDte extends TrocaTagNfse {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagEmailSolicitacaoAlteracaoSenhaDte.class);
    private UsuarioWeb user;
    private Cidade municipio;

    public TrocaTagEmailSolicitacaoAlteracaoSenhaDte(UsuarioWeb user, ConfiguracaoDte configuracaoDte) {
        super(configuracaoDte);
        this.user = user;
        this.municipio = configuracaoDte.getCidade();
        adicionarFields();
    }

    public void adicionarFields() {
        addicionarField(new Field(TagEmailSolicitacaoAlteracaoSenha.MUNICIPIO, municipio.getNome()));
        addicionarField(new Field(TagEmailSolicitacaoAlteracaoSenha.USUARIO, user.getPessoa().getNome()));
        addicionarField(new Field(TagEmailSolicitacaoAlteracaoSenha.CPFCNPJ, user.getPessoa().getCpf_Cnpj()));
        addicionarField(new Field(TagEmailSolicitante.LINK, configuracaoDte.getUrlDte() +
            "/#/reset/finish?key=" + user.getResetKey()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
