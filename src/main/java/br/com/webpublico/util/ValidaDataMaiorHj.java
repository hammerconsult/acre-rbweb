/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import java.util.Date;

/**
 * @author Jaime
 */
public class ValidaDataMaiorHj implements Validator {


    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        //HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
        //Boolean expira = Boolean.valueOf(req.getParameter("ParamExpira"));

        //if (expira == true){
        Date campo = new Date();
        if (value != null) {
            campo = (Date) value;
        }
        Date hj = new Date();
        if (campo.before(hj)) {
            throw new ValidatorException(
                    new FacesMessage("* A data de expiração deve estar no futuro!"));
        }
        //}
    }
}
