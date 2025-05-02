package br.com.webpublico.util;

import br.com.webpublico.negocios.QuadraFacade;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 05/02/14
 * Time: 10:42
 */
public class ConverterQuadra implements Converter, Serializable {
    private QuadraFacade facade;

    public ConverterQuadra(QuadraFacade facade) {
        this.facade = facade;
    }

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s.trim().isEmpty()) {
            return null;
        }
        try {
            int chave = Integer.parseInt(s);
            return facade.getQuadraPorCodigo(chave);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação Inválida", "Não existe quadra cadastrada para o código : " + s));
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
