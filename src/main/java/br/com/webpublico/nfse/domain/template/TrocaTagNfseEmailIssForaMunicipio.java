package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.dtos.IssqnFmLancamentoNfseDTO;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrocaTagNfseEmailIssForaMunicipio extends TrocaTagNfse {
    private static final Logger logger = LoggerFactory.getLogger(TrocaTagNfseEmailIssForaMunicipio.class);
    private IssqnFmLancamentoNfseDTO lancamento;

    public TrocaTagNfseEmailIssForaMunicipio(IssqnFmLancamentoNfseDTO lancamento, ConfiguracaoNfse configuracaoTributario) {
        super(configuracaoTributario);
        this.lancamento = lancamento;
        addicionarFields();
    }

    public void addicionarFields() {
        addicionarField(new Field(TagEmailIssForaMunicipio.CPF_CNPJ, lancamento.getPessoa().getDadosPessoais().getCpfCnpj()));
        addicionarField(new Field(TagEmailIssForaMunicipio.NOME_RAZAO_SOCIAL, lancamento.getPessoa().getDadosPessoais().getNomeRazaoSocial()));
        addicionarField(new Field(TagEmailIssForaMunicipio.INSCRICAO_MUNICIPAL, lancamento.getPessoa().getDadosPessoais().getInscricaoMunicipal()));
        addicionarField(new Field(TagEmailIssForaMunicipio.LOGRADOURO, lancamento.getPessoa().getDadosPessoais().getLogradouro()));
        addicionarField(new Field(TagEmailIssForaMunicipio.NUMERO, lancamento.getPessoa().getDadosPessoais().getNumero()));
        addicionarField(new Field(TagEmailIssForaMunicipio.COMPLEMENTO, lancamento.getPessoa().getDadosPessoais().getComplemento()));
        addicionarField(new Field(TagEmailIssForaMunicipio.BAIRRO, lancamento.getPessoa().getDadosPessoais().getBairro()));
        addicionarField(new Field(TagEmailIssForaMunicipio.MUNICIPIO, lancamento.getPessoa().getDadosPessoais().getMunicipio().getNome()));
        addicionarField(new Field(TagEmailIssForaMunicipio.UF, lancamento.getPessoa().getDadosPessoais().getMunicipio().getEstado()));
        addicionarField(new Field(TagEmailIssForaMunicipio.CEP, lancamento.getPessoa().getDadosPessoais().getCep()));
        addicionarField(new Field(TagEmailIssForaMunicipio.TELEFONE, lancamento.getPessoa().getDadosPessoais().getTelefone()));
        addicionarField(new Field(TagEmailIssForaMunicipio.EMAIL, lancamento.getPessoa().getDadosPessoais().getEmail()));
        addicionarField(new Field(TagEmailIssForaMunicipio.TIPO_LANCAMENTO, lancamento.getTipoPessoa().toString()));
        addicionarField(new Field(TagEmailIssForaMunicipio.EXERCICIO, lancamento.getExercicio().toString()));
        addicionarField(new Field(TagEmailIssForaMunicipio.MES, lancamento.getMes().getDescricao()));
        addicionarField(new Field(TagEmailIssForaMunicipio.VALOR, Util.formataValor(lancamento.getValor())));
        addicionarField(new Field(TagEmailIssForaMunicipio.OBSERVACAO, lancamento.getObservacao()));
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
