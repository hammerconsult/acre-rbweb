/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.util.Persistencia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class RecuperadorFacade {

    private static final Logger logger = LoggerFactory.getLogger(RecuperadorFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ComponentePesquisaGenericoFacade componentePesquisaGenericoFacade;

    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean isCampoTipoLista(Field f) {
        return f.getType().equals(List.class);
    }


    private String getNomeEntidade(Object objeto) {
        String tabela = objeto.getClass().getSimpleName();
        if (objeto.getClass().isAnnotationPresent(Entity.class)) {
            Entity annotation = (Entity) objeto.getClass().getAnnotation(Entity.class);
            if (annotation.name() != null && !annotation.name().trim().isEmpty()) {
                tabela = annotation.name();
            }

        }
        return tabela;
    }

    public Object obterCampo(Object pai, Field campo) {
        String hql = "select f from " + pai.getClass().getSimpleName() + " p inner join p." + campo.getName() + " f where p.id = :id";
        Query q = em.createQuery(hql);
        q.setParameter("id", Persistencia.getId(pai));

        Object o = isCampoTipoLista(campo) ? q.getResultList() : q.getSingleResult();

        try {
            campo.set(pai, o);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            logger.error("Erro:", ex);
        }
        return o;
    }

    public Object obterCampo(Object pai, String campo) {
        String hql = "";

        hql = "select p." + campo + " from " + getNomeEntidade(pai) + " p where p.id = :id";

        Query q = em.createQuery(hql);
        q.setParameter("id", Persistencia.getId(pai));
        List l = q.getResultList();

        if (!l.isEmpty()) {
            if (l.size() == 1) {
                return l.get(0);
            } else {
                return l;
            }
        }
        return null;
    }

    public Object obterCampoLista(Object pai, String campo, String campoLista) {
        String hql = null;
        if (campo.equals(campoLista)) {
            hql = "select distinct p from " + pai.getClass().getSimpleName() + " p left join fetch p." + campoLista + " s where p.id = :id";
        } else {
            hql = "select distinct f from " + pai.getClass().getSimpleName() + " p left join p." + campo + " f left join fetch  f." + campoLista + " s where p.id = :id";
        }
        Query q = em.createQuery(hql);
        q.setParameter("id", Persistencia.getId(pai));
        List l = q.getResultList();

        if (!l.isEmpty()) {
            if (l.size() == 1) {
                return l.get(0);
            } else {
                return l;
            }
        }
        return null;
    }

    public String preencherCampo(Object objeto, AtributoRelatorioGenerico atributo) {
        try {
            if (!atributo.isTransiente()) {
                if (!atributo.getCondicao().contains(".")) {
                    if (objeto != null) {
                        Object o = getValorDoAtributo(atributo, objeto);
                        if (o != null) {
                            return o.toString();
                        }
                    }
                } else {
                    String retorno = getValorDoAtributo(atributo, objeto);
                    return retorno;
                }
            } else {
                return Persistencia.getAsStringAtributoValue(objeto, atributo.getField());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //apenas continua
        }
        return "";
    }

    public String getValorDoAtributo(AtributoRelatorioGenerico atributo, Object objeto) {
        if (atributo.getAtributoDeEntidade() && !atributo.getAtributoDeLista()) {
            Object objetoDestino = obterCampo(objeto, atributo.getCondicao());
            if (objetoDestino != null) {
                return objetoDestino.toString();
            }
        } else if (!atributo.getAtributoDeEntidade() && atributo.getAtributoDeLista()) {
            String[] split = atributo.getCondicao().split("\\.");
            String condicao = atributo.getCondicao().replace("." + split[split.length - 1], "");

            Object objetoDestino = obterCampoLista(objeto, condicao, split[split.length - 1]);
            if (objetoDestino != null) {
                Object atributoDaCondicao = getAtributoDaCondicao(objetoDestino, split[split.length - 1]);
                if (atributoDaCondicao != null) {
                    return atributoDaCondicao.toString();
                }
            }
        } else {
            Object atributoDaCondicao = getAtributoDaCondicao(objeto, atributo.getCondicao());
            if (atributoDaCondicao != null) {
                return atributoDaCondicao.toString();
            } else {
                String[] split = atributo.getCondicao().split("\\.");
                String condicao = atributo.getCondicao().replace("." + split[split.length - 1], "");
                Object objetoDestino = obterCampo(objeto, condicao);
                if (objetoDestino != null) {
                    Object atributoDaCondicaoRecuperado = getAtributoDaCondicao(objetoDestino, split[split.length - 1]);
                    if (atributoDaCondicaoRecuperado != null) {
                        return atributoDaCondicaoRecuperado.toString();
                    }
                }
            }
        }
        return "";
    }

    public Object getAtributoDaCondicao(Object o, String condicao) {
        if (!condicao.contains(".")) {
            Object retorno = getAtributo(o, condicao);
            try {
                Field f = null;
                for (Field field : Persistencia.getAtributos(o.getClass())) {
                    field.setAccessible(true);
                    if (field.getName().equals(condicao)) {
                        f = field;
                        break;
                    }
                }
                retorno = Persistencia.getAsStringAtributoValue(o, f);
            } catch (Exception e) {
            }

            return retorno;
        }
        String[] split = condicao.split("\\.");
        o = getAtributo(o, split[0]);
        condicao = condicao.replace(split[0] + ".", "");
        return getAtributoDaCondicao(o, condicao);
    }

    public Object getAtributo(Object o, String nomeCampo) {
        try {
            String methodName = "get" + nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1);
            Object retorno = o.getClass().getMethod(methodName, new Class[]{}).invoke(o, new Object[]{});
            if (retorno == null) {
                retorno = obterCampo(o, nomeCampo);
            }
            return retorno;
        } catch (Exception e) {
            try {
                return o;
            } catch (Exception e1) {
                return "";
            }
        }
    }

    public boolean possuiAnotacao(Class classe, Class anotacao) {
        return classe.isAnnotationPresent(anotacao);
    }

    public boolean isEntidade(Class c) {
        return possuiAnotacao(c, Entity.class);
    }

    public boolean isAtributoDeEntidade(Field atributo) {
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

    public Long getCountDe(String toSelect, String from, String where, Map<String, Object> parametros) {
//        toSelect = toSelect.replace("select ", "");
        String hql = toSelect;
        hql += from;
        hql += where;

        Query q = em.createQuery(hql);
        for (String param : parametros.keySet()) {
            q.setParameter(param, parametros.get(param));
        }

        return (Long) q.getSingleResult();
    }

    public Map<String, Object> getResultadoDe(String selectCount, String toSelect, String from, String where, String orderBy, Map<String, Object> parametros, int inicio, int maximo) {
        String hql = toSelect + from + where + orderBy;
        Query q = getEntityManager().createQuery(hql);

        for (String param : parametros.keySet()) {
            q.setParameter(param, parametros.get(param));
        }
        q.setFirstResult(inicio);
        q.setMaxResults(maximo);

        Map<String, Object> resultado = new HashMap<String, Object>();
        resultado.put("DADOS", q.getResultList());
        resultado.put("COUNT", getCountDe(selectCount, from, where, parametros));

        return resultado;
    }

    public static class FiltroCampoOperador {
        private String campo;
        private Operador operador;
        private String complementoQuery;

        public FiltroCampoOperador(String campo, Operador operador) {
            this.campo = campo;
            this.operador = operador;
        }

        public FiltroCampoOperador(String campo, Operador operador, String complementoQuery) {
            this.campo = campo;
            this.operador = operador;
            this.complementoQuery = complementoQuery;
        }

        public String getCampo() {
            return campo;
        }

        public Operador getOperador() {
            return operador;
        }

        public String getComplementoQuery() {
            return complementoQuery;
        }

        public Boolean isCampoEhComplemento() {
            return complementoQuery != null;
        }
    }
}
