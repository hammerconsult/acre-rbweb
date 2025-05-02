package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.nfse.facades.TemplateNfseFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.trocatag.Field;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * Created by rodolfo on 14/02/18.
 */
public class TrocaTagNfseNotaFiscal extends TrocaTagNfse {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagNfseNotaFiscal.class);
    private final NotaFiscal notaFiscal;
    private final TemplateNfseFacade templateService;
    private final HttpServletRequest request;

    public TrocaTagNfseNotaFiscal(NotaFiscal notaFiscal, TemplateNfseFacade templateService, ConfiguracaoNfse configuracaoTributario, HttpServletRequest request) {
        super(configuracaoTributario);
        this.templateService = templateService;
        this.notaFiscal = notaFiscal;
        this.request = request;
        addicionarFields();
    }

    public void addicionarFields() {
        addTagsHeader();
        addFieldsPrestador(notaFiscal.getDeclaracaoPrestacaoServico().getDadosPessoaisPrestador().toNfseDto());
        addTomador();
        addItensServico();
        addTagsRentencoesFederais();
        addTagsValores();
    }

    private void addItensServico() {
        TemplateNfse templateItem = templateService.buscarPorTipo(TipoTemplateNfse.ITEM_SERVICO);
        String itens = new TrocaTagNfseItemServico(notaFiscal, configuracaoNfse).trocarTags(templateItem.getConteudo());
        addicionarField(new Field(TagNotaFiscal.TABELA_ITENS, itens));
    }

    private void addTomador() {
        DadosPessoaisNfse tomador = notaFiscal.getDeclaracaoPrestacaoServico().getDadosPessoaisTomador();
        if (tomador != null) {
            addicionarField(new Field(TagNotaFiscal.NOME_FANTASIA_TOMADOR, tomador.getNomeFantasia()));
            addicionarField(new Field(TagNotaFiscal.RAZAO_SOCIAL_TOMADOR, tomador.getNomeRazaoSocial()));
            addicionarField(new Field(TagNotaFiscal.CPF_CNPJ_TOMADOR, tomador.getCpfCnpj()));
            addicionarField(new Field(TagNotaFiscal.EMAIL_TOMADOR, tomador.getEmail()));
            addicionarField(new Field(TagNotaFiscal.LOGRADOURO_TOMADOR, tomador.getLogradouro()));
            addicionarField(new Field(TagNotaFiscal.BAIRRO_TOMADOR, tomador.getBairro()));
            addicionarField(new Field(TagNotaFiscal.CEP_TOMADOR, tomador.getCep()));
            addicionarField(new Field(TagNotaFiscal.COMPLEMENTO_TOMADOR, tomador.getComplemento()));
            if (tomador.getMunicipio() != null) {
                addicionarField(new Field(TagNotaFiscal.MUNICIPIO_TOMADOR, tomador.getMunicipio()));
            }

        }
    }

    private void addTagsHeader() {
        addicionarField(new Field(TagNotaFiscal.DATA_FATO, dateFormat.print(DateTime.now())));
        addicionarField(new Field(TagNotaFiscal.EMISSAO, dateFormat.print(notaFiscal.getEmissao().getTime())));
        addicionarField(new Field(TagNotaFiscal.NUMERO_NOTA, notaFiscal.getNumero().toString()));
        addicionarField(new Field(TagNotaFiscal.CODIGO_AUTENTICIDADE, notaFiscal.getCodigoVerificacao()));
        addicionarField(new Field(TagNotaFiscal.NUMERO_RPS, "0"));
        addicionarField(new Field(TagNotaFiscal.LOGO_EMPRESA, " "));
        if (notaFiscal.getPrestador().getPessoa().getArquivo() != null) {
            addicionarField(new Field(TagNotaFiscal.LOGO_EMPRESA,
                "<img src='" + FacesUtil.getBaseUrl(request) + "/nfse/arquivo/" + notaFiscal.getPrestador().getPessoa().getArquivo().getId() + "' style='width: 100%'/>"));
        }
    }


    private void addTagsRentencoesFederais() {
        TributosFederais tributosFederais = notaFiscal.getDeclaracaoPrestacaoServico().getTributosFederais();
        if (tributosFederais != null) {
            addicionarField(new Field(TagNotaFiscal.PIS, decimalFormat.format(tributosFederais.getPis())));
            addicionarField(new Field(TagNotaFiscal.COFINS, decimalFormat.format(tributosFederais.getCofins())));
            addicionarField(new Field(TagNotaFiscal.INSS, decimalFormat.format(tributosFederais.getInss())));
            addicionarField(new Field(TagNotaFiscal.IR, decimalFormat.format(tributosFederais.getIrrf())));
            addicionarField(new Field(TagNotaFiscal.CSLL, decimalFormat.format(tributosFederais.getCsll())));
            addicionarField(new Field(TagNotaFiscal.OUTRAS_RETENCOES, decimalFormat.format(tributosFederais.getOutrasRetencoes())));
        } else {
            addicionarField(new Field(TagNotaFiscal.PIS, decimalFormat.format(BigDecimal.ZERO)));
            addicionarField(new Field(TagNotaFiscal.COFINS, decimalFormat.format(BigDecimal.ZERO)));
            addicionarField(new Field(TagNotaFiscal.INSS, decimalFormat.format(BigDecimal.ZERO)));
            addicionarField(new Field(TagNotaFiscal.IR, decimalFormat.format(BigDecimal.ZERO)));
            addicionarField(new Field(TagNotaFiscal.CSLL, decimalFormat.format(BigDecimal.ZERO)));
            addicionarField(new Field(TagNotaFiscal.OUTRAS_RETENCOES, decimalFormat.format(BigDecimal.ZERO)));
        }
    }

    private void addTagsValores() {
        addicionarField(new Field(TagNotaFiscal.TOTAL, decimalFormat.format(notaFiscal.getDeclaracaoPrestacaoServico().getTotalServicos())));
        addicionarField(new Field(TagNotaFiscal.VALOR_LIQUIDO, decimalFormat.format(notaFiscal.getDeclaracaoPrestacaoServico().getTotalServicos())));
        addicionarField(new Field(TagNotaFiscal.DESCONTO_CONDICIONADO, decimalFormat.format(BigDecimal.ZERO)));
        addicionarField(new Field(TagNotaFiscal.DESCONTO_INCONDICIONADO, decimalFormat.format(BigDecimal.ZERO)));
        addicionarField(new Field(TagNotaFiscal.DEDUCOES, decimalFormat.format(BigDecimal.ZERO)));
        addicionarField(new Field(TagNotaFiscal.BASE_CALCULO, decimalFormat.format(notaFiscal.getDeclaracaoPrestacaoServico().getBaseCalculo())));
        addicionarField(new Field(TagNotaFiscal.VALOR_ISS, decimalFormat.format(notaFiscal.getDeclaracaoPrestacaoServico().getIssCalculado() != null ? notaFiscal.getDeclaracaoPrestacaoServico().getIssCalculado() : BigDecimal.ZERO)));

    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
