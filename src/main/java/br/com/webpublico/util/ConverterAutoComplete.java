/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * @author terminal4
 */
public class ConverterAutoComplete implements Converter, Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ConverterAutoComplete.class);
    private AbstractFacade facade;
    private Class classe;
    private Map<String, Object> mapaDeCache;

    public ConverterAutoComplete(Class classe, AbstractFacade f) {
        this.facade = f;
        this.classe = classe;
        this.mapaDeCache = Maps.newHashMap();
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return getObject(value);
        } catch (NumberFormatException e) {
        } catch (Exception ex) {
            //logger.trace("Erro: ", ex);
            logger.error("Erro ao chave(id) [{}]", value);
            logger.error("Erro ao converter [{}]", ex.getMessage());
            logger.error("Caminho da view [{}]", context.getExternalContext().getRequestPathInfo());
            logger.error("ID Componente: [{}]", component.getClientId());
        }
        return null;
    }

    public Object getObject(String chave) {
        if (mapaDeCache.containsKey(chave)) {
            return mapaDeCache.get(chave);
        } else {
            mapaDeCache.put(chave, facade.recuperar(classe, Long.valueOf(chave)));
            return mapaDeCache.get(chave);
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Long) {
            return String.valueOf(value);
        } else if (value instanceof SuperEntidade && ((SuperEntidade) value).getId() != null) {
            return ((SuperEntidade) value).getId().toString();
        } else {
            try {
                return Objects.requireNonNull(Persistencia.getAttributeValue(value, Persistencia.getFieldId(classe).getName())).toString();
            } catch (Exception e) {
                return String.valueOf(value);
            }
        }

    }
}
