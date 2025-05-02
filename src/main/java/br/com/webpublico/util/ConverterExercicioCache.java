/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.singletons.CacheTributario;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;

/**
 * @author Gustavo
 */
public class ConverterExercicioCache implements Converter, Serializable {

    private CacheTributario cache;

    public ConverterExercicioCache(CacheTributario cache) {
        this.cache = cache;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value.trim().isEmpty()) {
            return null;
        }
        if (value.length() != 4) {
            FacesUtil.addOperacaoNaoPermitida("O campo exercício deve conter quatro caracteres.");
            return null;
        }
        try {
            int chave = Integer.parseInt(value);
            return cache.getExercicioPorAno(chave);
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addOperacaoNaoPermitida("Não existe exercício cadastrado para o ano informado: " + value);
            return null;
        }

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        try {
            if (value != null) {
                return String.valueOf(((Exercicio) value).getAno());
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar", "Erro ao converter: " + value));
            return null;
        }
    }
}
