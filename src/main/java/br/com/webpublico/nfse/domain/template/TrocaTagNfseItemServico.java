package br.com.webpublico.nfse.domain.template;

import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.ItemDeclaracaoServico;
import br.com.webpublico.nfse.domain.NotaFiscal;
import br.com.webpublico.nfse.enums.TipoTemplateNfse;
import br.com.webpublico.util.trocatag.Field;
import com.google.common.collect.Lists;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

/**
 * Created by rodolfo on 14/02/18.
 */
public class TrocaTagNfseItemServico extends TrocaTagNfse {

    private static final Logger logger = LoggerFactory.getLogger(TrocaTagNfseItemServico.class);
    private NotaFiscal notaFiscal;

    public TrocaTagNfseItemServico(NotaFiscal notaFiscal, ConfiguracaoNfse configuracaoTributario) {
        super(configuracaoTributario);
        this.notaFiscal = notaFiscal;
    }

    @Override
    public String trocarTags(String conteudo) {
        try {
            Properties p = new Properties();

            p.setProperty("resource.loader", "string");
            p.setProperty("string.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
            VelocityEngine ve = new VelocityEngine();
            ve.init(p);


            VelocityContext context = new VelocityContext();
            StringWriter writer = new StringWriter();
            for (ItemDeclaracaoServico item : notaFiscal.getDeclaracaoPrestacaoServico().getItens()) {
                for (Field field : getFieldsLocais(item)) {
                    if (field.getValue() != null) {
                        context.put(field.getTag().name(), field.getValue());
                    }
                }
                ve.evaluate(context, writer, "str", conteudo);
            }
            return writer.toString();
        } catch (Exception e) {
            return conteudo;
        }
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }

    private List<Field> getFieldsLocais(ItemDeclaracaoServico item) {
        List<Field> fields = Lists.newArrayList();
        fields.add(new Field(TagItemServico.NOME, item.getNomeServico()));
        fields.add(new Field(TagItemServico.VALOR, decimalFormat.format(item.getValorServico())));
        fields.add(new Field(TagItemServico.QUANTIDADE, item.getQuantidade().toString()));
        fields.add(new Field(TagItemServico.TOTAL_SERVICO, decimalFormat.format(item.getValorTotal())));
        fields.add(new Field(TagItemServico.BASE_CALCULO, decimalFormat.format(item.getBaseCalculo())));
        fields.add(new Field(TagItemServico.ISS, decimalFormat.format(item.getIss() != null ? item.getIss() : BigDecimal.ZERO)));

        return fields;
    }
}
