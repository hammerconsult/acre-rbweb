package br.com.webpublico.util.trocatag;

import br.com.webpublico.nfse.domain.template.TrocaTagNfse;
import com.google.common.collect.Lists;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;

public abstract class TrocaTag {
    protected final static DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd/MM/yyyy");
    protected final static DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private List<Field> fields = Lists.newArrayList();

    protected void addicionarField(Field field) {
        fields.add(field);
    }

    public String trocarTags(String conteudo) {
        try {
            Properties p = new Properties();
            p.setProperty("resource.loader", "string");
            p.setProperty("string.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
            VelocityEngine ve = new VelocityEngine();
            ve.init(p);

            VelocityContext context = new VelocityContext();
            for (Field field : fields) {
                if (field.getValue() != null) {
                    context.put(field.getTag().name(), field.getValue());
                } else {
                    context.put(field.getTag().name(), "");
                }
            }
            StringWriter writer = new StringWriter();
            ve.evaluate(context, writer, "str", conteudo);
            return writer.toString();
        } catch (Exception e) {
            getLogger().debug("Exceção ao trocar as tags.", e);
            return conteudo;
        }
    }

    protected abstract Logger getLogger();
}
