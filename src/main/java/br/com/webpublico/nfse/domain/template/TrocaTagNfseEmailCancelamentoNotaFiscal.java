package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.nfse.domain.TomadorServicoNfse;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.trocatag.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by rodolfo on 14/02/18.
 */
public class TrocaTagNfseEmailCancelamentoNotaFiscal extends TrocaTagNfse {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagNfseEmailCancelamentoNotaFiscal.class);

    private NotaFiscal notaFiscal;

    public TrocaTagNfseEmailCancelamentoNotaFiscal(NotaFiscal notaFiscal, ConfiguracaoNfse configuracaoTributario) {
        super(configuracaoTributario);
        this.notaFiscal = notaFiscal;
        addicionarFields();
    }
    public void addicionarFields() {
        addicionarField(new Field(TagEmailNotaFiscal.NUMERO, notaFiscal.getNumero().toString()));
        addicionarField(new Field(TagEmailNotaFiscal.EMISSAO, Util.formatterDiaMesAno.format(notaFiscal.getEmissao())));
        addicionarField(new Field(TagEmailNotaFiscal.VALOR, Util.formataValor(notaFiscal.getDeclaracaoPrestacaoServico().getValorLiquido())));
        addicionarField(new Field(TagEmailNotaFiscal.CODIGO, notaFiscal.getCodigoVerificacao()));
        addicionarField(new Field(TagEmailNotaFiscal.LINK_NOTA, configuracaoNfse.getUrlNfse() + "/impressao/" + notaFiscal.getId()));
        addicionarField(new Field(TagEmailNotaFiscal.LINK_SISTEMA, configuracaoNfse.getUrlNfse()));
        Pessoa prestador = notaFiscal.getPrestador().getPessoa();
        if (prestador != null) {
            addFieldsPrestador(prestador.toNfseDto().getDadosPessoais());
        }
        TomadorServicoNfse tomador = notaFiscal.getTomador();
        if (tomador != null) {
            addicionarField(new Field(TagEmailNotaFiscal.CPF_CNPJ_TOMADOR, tomador.getDadosPessoaisNfse().getCpfCnpj()));
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
