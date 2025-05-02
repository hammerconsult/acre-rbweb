package br.com.webpublico.nfse.domain.template;


import br.com.webpublico.dte.entidades.ConfiguracaoDte;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.dtos.DadosPessoaisNfseDTO;
import br.com.webpublico.util.trocatag.Field;
import br.com.webpublico.util.trocatag.TrocaTag;

public abstract class TrocaTagNfse extends TrocaTag {

    protected ConfiguracaoNfse configuracaoNfse;
    protected ConfiguracaoDte configuracaoDte;

    TrocaTagNfse(ConfiguracaoNfse configuracaoNfse) {
        this.configuracaoNfse = configuracaoNfse;
    }

    TrocaTagNfse(ConfiguracaoDte configuracaoDte) {
        this.configuracaoDte = configuracaoDte;
    }

    public void addFieldsUser(UsuarioWeb user) {
        addicionarField(new Field(TagUsuario.CPF_CNPJ_USUARIO, user.getPessoa().getCpf_Cnpj()));
        addicionarField(new Field(TagUsuario.NOME_RAZAOSOCIAL_USUARIO, user.getPessoa().getNome()));
    }

    public void addFieldsPrestador(CadastroEconomico cadastroEconomico) {
        addFieldsPrestador(cadastroEconomico.getPessoa().toNfseDto().getDadosPessoais());
    }

    public void addFieldsPrestador(DadosPessoaisNfseDTO prestador) {
        if (prestador != null) {
            addicionarField(new Field(TagPrestadorServicos.NOME_FANTASIA_PRESTADOR, prestador.getNomeFantasia()));
            addicionarField(new Field(TagPrestadorServicos.RAZAO_SOCIAL_PRESTADOR, prestador.getNomeRazaoSocial()));
            addicionarField(new Field(TagPrestadorServicos.CPF_CNPJ_PRESTADOR, prestador.getCpfCnpj()));
            addicionarField(new Field(TagPrestadorServicos.EMAIL_PRESTADOR, prestador.getEmail()));
            addicionarField(new Field(TagPrestadorServicos.LOGRADOURO_PRESTADOR, prestador.getLogradouro()));
            addicionarField(new Field(TagPrestadorServicos.BAIRRO_PRESTADOR, prestador.getBairro()));
            addicionarField(new Field(TagPrestadorServicos.CEP_PRESTADOR, prestador.getCep()));
            addicionarField(new Field(TagPrestadorServicos.COMPLEMENTO_PRESTADOR, prestador.getComplemento()));
            if (prestador.getMunicipio() != null) {
                addicionarField(new Field(TagPrestadorServicos.MUNICIPIO_PRESTADOR, prestador.getMunicipio().getNome()));
            }
        }
    }
}
