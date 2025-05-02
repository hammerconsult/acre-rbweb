package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.dtos.DadosPessoaisNfseDTO;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagNfseEmailSolicitacaoAcessoSemCadastro extends TrocaTagNfse {
    private static final Logger logger = LoggerFactory.getLogger(TrocaTagNfseEmailSolicitacaoAcessoSemCadastro.class);
    private CadastroEconomico prestadorServicos;
    private Cidade municipio;
    private DadosPessoaisNfseDTO user;

    public TrocaTagNfseEmailSolicitacaoAcessoSemCadastro(CadastroEconomico prestadorServicos, DadosPessoaisNfseDTO user, ConfiguracaoNfse configuracaoTributario) {
        super(configuracaoTributario);
        this.prestadorServicos = prestadorServicos;
        this.user = user;
        this.municipio = configuracaoTributario.getCidade();
        addicionarFields();
    }

    public void addicionarFields() {
        addFieldsPrestador(prestadorServicos);
        addicionarField(new Field(TagComum.MUNICIPIO, municipio.getNome()));
        addicionarField(new Field(TagComum.ESTADO, municipio.getUf().getNome()));
        addicionarField(new Field(TagUsuario.CPF_CNPJ_USUARIO, user.getCpfCnpj()));
        addicionarField(new Field(TagUsuario.NOME_RAZAOSOCIAL_USUARIO, user.getNomeRazaoSocial()));
        addicionarField(new Field(TagEmailSolicitante.LINK, configuracaoNfse.getUrlNfse()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
