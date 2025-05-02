package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagNfseEmailSolicitacaoAcessoEmpresa extends TrocaTagNfse {
    private static final Logger logger = LoggerFactory.getLogger(TrocaTagNfseEmailSolicitacaoAcessoEmpresa.class);
    private CadastroEconomico prestadorServicos;
    private Cidade municipio;
    private UsuarioWeb user;

    public TrocaTagNfseEmailSolicitacaoAcessoEmpresa(CadastroEconomico prestadorServicos, UsuarioWeb user, ConfiguracaoNfse configuracaoTributario) {
        super(configuracaoTributario);
        this.prestadorServicos = prestadorServicos;
        this.user = user;
        this.municipio = configuracaoTributario.getCidade();
        adicionarFields();
    }

    public void adicionarFields() {
        addFieldsUser(user);
        addFieldsPrestador(prestadorServicos);
        addicionarField(new Field(TagComum.MUNICIPIO, municipio.getNome()));
        addicionarField(new Field(TagComum.ESTADO, municipio.getUf().getNome()));

    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
