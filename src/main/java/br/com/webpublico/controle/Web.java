/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.negocios.RecuperadorFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.anotacoes.SessaoManual;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.persistence.Entity;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


@ManagedBean
@ViewScoped
public class Web implements Serializable {

    public static final String SESSION_KEY_CONVERSATION = "objectConversation";
    public static final String SESSION_KEY_CONVERSATION_WITH_KEY = "objectConversationWithKey";
    public static final String SESSION_KEY_CAMINHO = "objectCaminho";
    public static final String ESPERARETORNO = "esperaRetorno";
    public static final String CAMPORETORNO = "campoRetorno";
    @EJB
    protected RecuperadorFacade recuperadorFacade;

    public Web() {
    }

    public static Map<String, Object> getSessionMap() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    private static Map<Class, Object> getMapConversation() {
        return (Map<Class, Object>) getSessionMap().get(SESSION_KEY_CONVERSATION);
    }

    private static Map<String, Object> getMapConversationWithKey() {
        return (Map<String, Object>) getSessionMap().get(SESSION_KEY_CONVERSATION_WITH_KEY);
    }

    private static Map<String, Object> getMapConversationWithKey(String key) {
        return (Map<String, Object>) getSessionMap().get(key);
    }


    private static Stack getStackCaminhos() {
        return (Stack) getSessionMap().get(SESSION_KEY_CAMINHO);
    }

    public static void limpaNavegacao() {
        if (getStackCaminhos() != null) {
            getStackCaminhos().clear();
        }
    }

    public static void poeNaSessao(Object object) {
        if (object != null) {
            if (getMapConversation() == null) {
                getSessionMap().put(SESSION_KEY_CONVERSATION, new HashMap<Class, Object>());
            }
            getMapConversation().put(object.getClass(), object);
        }
    }

    public static void poeTodosFieldsNaSessao(Object object) {
        if (object != null) {
            percorreFieldsDaClasseColocandoNaSessao(object, object.getClass());
            if (!object.getClass().getSuperclass().equals(Object.class)) {
                percorreFieldsDaClasseColocandoNaSessao(object, object.getClass().getSuperclass());
            }
        }
    }

    private static void percorreFieldsDaClasseColocandoNaSessao(Object object, Class classe) {
        for (Field f : classe.getDeclaredFields()) {
            poeFieldNaSessao(object, f);
        }
    }

    private static void poeFieldNaSessao(Object object, Field f) {
        try {
            f.setAccessible(true);
            if (!f.isAnnotationPresent(EJB.class) && !f.isAnnotationPresent(SessaoManual.class)) {
                poeNaSessao(object.getClass().getSimpleName(), f.getName(), f.get(object));
            }
        } catch (Exception e) {

        }
    }

    public static void pegaTodosFieldsNaSessao(Object object) throws IllegalAccessException {
        if (object != null) {
            percorreFieldsDaClassePegandoDaSessao(object, object.getClass());
            if (!object.getClass().getSuperclass().equals(Object.class)) {
                percorreFieldsDaClassePegandoDaSessao(object, object.getClass().getSuperclass());
            }
        }

    }

    private static void percorreFieldsDaClassePegandoDaSessao(Object object, Class classe) {
        for (Field f : classe.getDeclaredFields()) {
            pegaFieldDaSessao(object, f);
        }
    }

    private static void pegaFieldDaSessao(Object object, Field f) {
        try {
            f.setAccessible(true);
            if (!f.isAnnotationPresent(EJB.class) && !f.isAnnotationPresent(SessaoManual.class)) {
                Object ob = Web.pegaDaSessao(object.getClass().getSimpleName(), f.getName());
                f.set(object, ob);
            }
        } catch (Exception e) {

        }
    }

    public static void poeNaSessao(String key, Object object) {
        if (object != null) {
            if (getMapConversationWithKey() == null) {
                getSessionMap().put(SESSION_KEY_CONVERSATION_WITH_KEY, new HashMap<String, Object>());
            }
            getMapConversationWithKey().put(key, object);
        }
    }

    public static void poeNaSessao(String keyMap, String keyObejct, Object object) {
        if (object != null) {
            if (getMapConversationWithKey(keyMap) == null) {
                getSessionMap().put(keyMap, new HashMap<String, Object>());
            }
            getMapConversationWithKey(keyMap).put(keyObejct, object);
        }
    }

    public static Object pegaDaSessao(String key) {
        try {
            if (getMapConversationWithKey() != null) {
                Object object = getMapConversationWithKey().get(key);
                if (object != null) {
                    getMapConversationWithKey().remove(key);
                    preencheAtributos(object);
                    return object;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }
        return null;
    }

    public static Object pegaDaSessao(String keyMap, String keyObejct) {
        try {
            if (getMapConversationWithKey(keyMap) != null) {
                Object object = getMapConversationWithKey(keyMap).get(keyObejct);
                if (object != null) {
                    getMapConversationWithKey(keyMap).remove(keyObejct);
                    preencheAtributos(object);
                    return object;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }
        return null;
    }

    public static Object pegaDaSessao(Class classe) {
        try {
            if (getMapConversation() != null) {
                Object object = getMapConversation().get(classe);
                if (object != null) {
                    getMapConversation().remove(classe);
                    preencheAtributos(object);
                    return object;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErrorPadrao(e);
        }
        return null;
    }

    public static boolean isTodosFieldsNaSessao(Object object) throws IllegalAccessException {
        boolean retorno = false;
        if (object != null) {
            retorno = verificaTodosCamposDaSessao(object, object.getClass());
            if(retorno) return true;
            if (!object.getClass().getSuperclass().equals(Object.class)) {
                retorno = verificaTodosCamposDaSessao(object, object.getClass().getSuperclass());
            }
        }
        return retorno;
    }

    private static boolean verificaTodosCamposDaSessao(Object object, Class classe) {
        boolean retorno = false;
        for (Field f : classe.getDeclaredFields()) {
            if(isFieldNaSessao(object, f)){
                return true;
            }
        }
        return retorno;
    }


    private static boolean isFieldNaSessao(Object object, Field f) {
        try {
            f.setAccessible(true);
            if (!f.isAnnotationPresent(EJB.class)) {
                Object ob = Web.pegaDaSessao(object.getClass().getSimpleName(), f.getName());
                if(ob != null) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }


    private static void preencheAtributos(Object object) throws InstantiationException, IllegalArgumentException, IllegalAccessException {
        trataSeTiverCampoParaPreenchimentoNoRetorno(object);
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getType().isAnnotationPresent(Entity.class) && field.get(object) == null) {
                Object o = pegaDaSessao(field.getType());
                if (o != null) {
                    Object id = Persistencia.getId(o);
                    if (id != null) {
                        field.set(object, o);
                    }
                }
            }
        }
    }

    public static String getCaminhoOrigem() {
        Stack<String> origens = getStackCaminhos();
        if (origens != null && !origens.isEmpty()) {
            String origem = origens.peek();
            origens.remove(origem);
            return origem;
        }
        return "";
    }

    public static void setCaminhoOrigem(String caminho) {
        if (getStackCaminhos() == null) {
            getSessionMap().put(SESSION_KEY_CAMINHO, new Stack<>());
        }
        getStackCaminhos().add(caminho);
    }

    public static boolean getEsperaRetorno() {
        Boolean retorno = (Boolean) Web.getSessionMap().get(ESPERARETORNO);
        getSessionMap().put(ESPERARETORNO, false);
        return retorno != null ? retorno : false;
    }

    public static void navegacao(String origem, String destino, Object... objetos) {
        for (Object obj : objetos) {
            poeNaSessao(obj);
        }

        origem = origem.contains("?") ? origem.replace("?", "?sessao=true&") : origem + "?sessao=true";
        destino = destino.contains("?") ? destino.replace("?", "?sessao=true&") : destino + "?sessao=true";

        setCaminhoOrigem(origem);
        FacesUtil.redirecionamentoInterno(destino);
        getSessionMap().put(ESPERARETORNO, true);
    }

    public static void navegacao(String origem, String destino, String key, Object... objetos) {
        for (Object obj : objetos) {
            poeNaSessao(key, obj);
        }

        origem = origem.contains("?") ? origem.replace("?", "?sessao=true&") : origem + "?sessao=true";
        destino = destino.contains("?") ? destino.replace("?", "?sessao=true&") : destino + "?sessao=true";

        setCaminhoOrigem(origem);
        FacesUtil.redirecionamentoInterno(destino);
        getSessionMap().put(ESPERARETORNO, true);
    }

    private static void trataSeTiverCampoParaPreenchimentoNoRetorno(Object object) throws IllegalAccessException {
        String campo = getCampoRetorno();
        if (campo != null && !campo.trim().isEmpty()) {
            if (campo.contains(".")) {
                String[] split = campo.split("\\.");
                String ultimo = split[split.length - 1];
                Object vouPorAqui = getValorCampo(object, retornaArrayRetirandoUmaPosicao(split, split.length - 1));
                for (Field field : vouPorAqui.getClass().getDeclaredFields()) {
                    if (field.getName().equals(ultimo)) {
                        Object daSessao = pegaDaSessao(field.getType());
                        field.set(vouPorAqui, daSessao);
                    }
                }
            }
        }
    }

    private static Object getValorCampo(Object obj, String atributo) throws IllegalAccessException {
        String[] composto;
        if (atributo.contains(".")) {
            composto = atributo.split("\\.");
        } else {
            composto = new String[]{atributo};
        }
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equals(composto[0])) {
                obj = field.get(obj) == null ? "" : field.get(obj);
                if (composto.length > 1) {
                    obj = getValorCampo(obj, retornaArrayRetirandoUmaPosicao(composto, 0));
                }
            }
        }
        return obj;
    }

    private static String retornaArrayRetirandoUmaPosicao(String[] array, int indexRetirar) {
        String primeiro = array[indexRetirar];
        String retorno = "";
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != primeiro) {
                retorno += array[i] + ".";
                ++j;
            }
        }
        return retorno.substring(0, retorno.length() - 1);
    }

    private static String getCampoRetorno() {
        return (String) getSessionMap().remove(CAMPORETORNO);
    }

    public static void navegacao(Object obj, String origem, String destino, String campoRetorno) {
        origem = origem.contains("?") ? origem.replace("?", "?sessao=true&") : origem + "?sessao=true";
        destino = destino.contains("?") ? destino.replace("?", "?sessao=true&") : destino + "?sessao=true";
        poeNaSessao(obj);
        setCaminhoOrigem(origem);
        FacesUtil.redirecionamentoInterno(destino);
        getSessionMap().put(ESPERARETORNO, true);
        getSessionMap().put(CAMPORETORNO, campoRetorno);
    }

    private boolean possuiAnotacao(Class classe, Class anotacao) {
        return classe.isAnnotationPresent(anotacao);
    }

    private boolean isEntidade(Class c) {
        return possuiAnotacao(c, Entity.class);
    }

    private boolean isAtributoDeEntidade(Field atributo) {
        if (isEntidade(atributo.getType())) {
            return true;
        }

        if (atributo.getType().equals(List.class)) {
            ParameterizedType pt = (ParameterizedType) atributo.getGenericType();
            for (Type t : pt.getActualTypeArguments()) {
                if (isEntidade((Class) t)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Object preencherCampo(Object o, AtributoRelatorioGenerico atributoRelatorioGenerico) {
        return recuperadorFacade.preencherCampo(o, atributoRelatorioGenerico);
    }

    public Object obter(Object e, String atributo) throws IllegalArgumentException, IllegalAccessException {
        Field f = null;
        Object o = null;

        o = recuperadorFacade.obterCampo(e, atributo);

        return o;
    }

    public void navegacao(Object obj, String origem, String destino) {
        origem = origem.contains("?") ? origem.replace("?", "?sessao=true&") : origem + "?sessao=true";
        destino = destino.contains("?") ? destino.replace("?", "?sessao=true&") : destino + "?sessao=true";
        poeNaSessao(obj);
        setCaminhoOrigem(origem);
        FacesUtil.redirecionamentoInterno(destino);
        getSessionMap().put(ESPERARETORNO, true);
    }

    public void navegacao(Object selecionado, Object parente, String origem, String destino) {
        origem = origem.contains("?") ? origem.replace("?", "?sessao=true&") : origem + "?sessao=true";
        destino = destino.contains("?") ? destino.replace("?", "?sessao=true&") : destino + "?sessao=true";
        poeNaSessao(selecionado);
        if (parente != null) {
            poeNaSessao(parente);
        }
        setCaminhoOrigem(origem);
        FacesUtil.redirecionamentoInterno(destino);
        getSessionMap().put(ESPERARETORNO, true);
    }

    public void navegacaoPadrao(String origem, String padrao, String destino) {
        setCaminhoOrigem(origem);
        FacesUtil.redirecionamentoInterno(padrao + destino);
    }

    public void enganar(String s) {
    }
}
