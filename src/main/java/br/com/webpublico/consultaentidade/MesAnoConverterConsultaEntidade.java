package br.com.webpublico.consultaentidade;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.slf4j.Logger;


@FacesConverter(value = "mesAnoConverterConsultaEntidade")
public class MesAnoConverterConsultaEntidade implements Serializable, Converter {

    private final Logger logger = LoggerFactory.getLogger(MesAnoConverterConsultaEntidade.class);

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String mesAno) {
        try {
            if (mesAno != null && !StringUtils.isBlank(mesAno)) {

                SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
                Date data = sdf.parse(mesAno);
                Calendar primeiroDia = new GregorianCalendar();
                primeiroDia.setTime(data);
                primeiroDia.set(Calendar.DAY_OF_MONTH, 1);
                return primeiroDia.getTime();
            }

        } catch (Exception ex) {
            logger.error("Erro de conversão de data, Exception {}", ex.getMessage());
            logger.debug("Detalhes do Erro: " + ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro de conversão de data, favor insira um valor aceitável"));
            return null;
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object mesAno) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dataFormatada = sdf.format(mesAno);
        StringBuilder builder = new StringBuilder(dataFormatada);
        builder.replace(0, 3, "");
        return builder.toString();
    }
}


