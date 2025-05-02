/*
 * Codigo gerado automaticamente em Sat Jul 02 09:02:36 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.EntidadePCS;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.EntidadePCSFacade;
import br.com.webpublico.negocios.PlanoCargosSalariosFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Persistencia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class EntidadePCSControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(EntidadePCSControlador.class);

    @EJB
    private EntidadePCSFacade entidadePCSFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private ConverterAutoComplete converterEntidade;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;

    public EntidadePCSControlador() {
        metadata = new EntidadeMetaData(EntidadePCS.class);
    }

    public EntidadePCSFacade getFacade() {
        return entidadePCSFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return entidadePCSFacade;
    }

    public List<Entidade> completaEntidade(String parte) {
        return entidadeFacade.listaEntidades(parte.trim());
    }

    public Converter getConverterEntidade() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, entidadeFacade);
        }
        return converterEntidade;
    }

    public List<SelectItem> getPlanoCargosSalarios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem("", ""));
        for (PlanoCargosSalarios object : planoCargosSalariosFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterPlanoCargosSalarios() {
        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                try {
                    Object chave = Persistencia.getFieldId(PlanoCargosSalarios.class).getType().getConstructor(String.class).newInstance(value);
                    return planoCargosSalariosFacade.recuperar(chave);
                } catch (Exception ex) {

                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                if (value != null) {
                    return String.valueOf(getId(value));
                } else {
                    return null;
                }
            }
        };
//        if (converterPlanoCargosSalarios == null) {
//            converterPlanoCargosSalarios = new ConverterGenerico(PlanoCargosSalarios.class, planoCargosSalariosFacade);
//        }
//        return converterPlanoCargosSalarios;
    }

    public static Object getId(Object entidade) {
        try {
            Field f = getFieldId(entidade.getClass());
            if (f != null) {
                f.setAccessible(true);
                return f.get(entidade);
            }

        } catch (Exception ex) {
            logger.error("Não foi possível encontrar a chave primária de " + entidade, ex);
        }
        return null;
    }

    public static Field getFieldId(Class classe) {
        try {
            for (Field f : getAtributos(classe)) {
                if (f.isAnnotationPresent(Id.class)) {
                    return f;
                }
            }
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar a chave primária de " + classe, ex);
        }
        return null;
    }

    public static List<Field> getAtributos(Class classe) {
        List<Field> lista = new ArrayList<Field>();
        if (!classe.getSuperclass().equals(Object.class)) {
            lista.addAll(getAtributos(classe.getSuperclass()));
        }
        for (Field f : classe.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            lista.add(f);
        }
        return lista;
    }
}
