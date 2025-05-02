package br.com.webpublico.util;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/**
 * Classe utilitária com alguns recursos para construção de métodos genéricos.
 * Ao implementar novos métodos, que busquem novas informações, verificar a
 * possibilidade de armazenar tais informações na classe CacheEntidade
 *
 * @author Munif
 */
public class Persistencia {

    private static final Logger logger = LoggerFactory.getLogger(Persistencia.class);

    /**
     * Obtem o valor da chave primária de uma entidade. O método procura o
     * atributo com a anotação ID na classe e nas superclasses
     *
     * @param entidade entidade que deseja-se a chave
     * @return valor da chave
     */
    public static Object getId(Object entidade) {
        if (entidade == null) {
            return null;
        }
        try {
            Field f = getFieldId(entidade.getClass());
            if (f != null) {
                f.setAccessible(true);
                return f.get(entidade);
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar a chave primária de " + entidade, ex);
        }
        return null;
    }

    /**
     * Obtém o valor do atributo a partir de seu nome e do objeto que o contém
     *
     * @param entidade
     * @param attrName
     * @return
     */
    public static Object getAttributeValue(Object entidade, String attrName) {
        try {
            //Field f = entidade.getClass().getField(attrName);
            String methodName = "get" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
            return entidade.getClass().getMethod(methodName, new Class[]{}).invoke(entidade, new Object[]{});
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar o atributo " + attrName + " na classe " + entidade.getClass().getName(), ex);
        }
        return null;
    }

    /**
     * Altera o valor da chave primária de um objeto, procura pelo
     *
     * @param entidade a entidade a alterar o ID
     * @param valor    novo valor
     * @Id
     */
    public static void setId(Object entidade, Object valor) {
        try {
            Field f = getFieldId(entidade.getClass());
            f.setAccessible(true);
            f.set(entidade, valor);
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar a chave primária de " + entidade, ex);
        }
    }

    public static void setVersaoAnterior(Object entidade, Object valor) {
        try {
            Field f = getAtributoVersaoAnterior(entidade.getClass());
            f.setAccessible(true);
            f.set(entidade, valor);
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar o atributo de versão anterior " + entidade, ex);
        }

    }

    /**
     * Obtem o atributo que é a versão anterior desta entidade. O método procura
     * o atributo com o nome versaoAnterior na classe e nas superclasses
     *
     * @return Field da chave
     */
    public static Field getAtributoVersaoAnterior(Class classe) {
        try {
            for (Field f : getAtributos(classe)) {
                if (f.getName().equals("versaoAnterior")) {
                    return f;
                }
            }
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar o atributo versão anterior na " + classe, ex);
        }
        return null;
    }

    /**
     * Obtem o atributo que é chave primária de uma entidade. O método procura o
     * atributo com a anotação ID na classe e nas superclasses
     *
     * @return Field da chave
     */
    public static Field getFieldId(Class classe) {
        if (classe == null) {
            return null;
        }
        try {
            CacheEntidade cacheEntidade = SingletonCacheEntidade.buscaDoCache(classe);
            if (cacheEntidade != null) {
                Field retorno = SingletonCacheEntidade.buscaDoCache(classe).getAtributoId();
                if (retorno != null) {
                    return retorno;
                }
            }
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

    /**
     * Método recursivo para descobrir todos os atributos da entidade, incluindo
     * os das superclasses, se existirem
     *
     * @param classe classe da entidade
     * @return um ArrayList com os atributos (Fields) da entidade
     */
    public static List<Field> getAtributos(Class classe) {
        return getAtributos(classe, true);
    }

    public static List<Field> getAtributos(Class classe, boolean buscarAtributosSuperClasse) {
        CacheEntidade cacheEntidade = SingletonCacheEntidade.buscaDoCache(classe);
        if (cacheEntidade != null) {
            List<Field> retorno = SingletonCacheEntidade.buscaDoCache(classe).getAtributos();
            if (retorno != null) {
                return retorno;
            }
        }
        List<Field> lista = new ArrayList<>();
        if (!classe.getSuperclass().equals(Object.class) && buscarAtributosSuperClasse) {
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

    /**
     * Método que retorna o primeiro atributo da classe de entidade. Não retorna
     * atributos estáticos. Evita retorna atributos com a anotação
     * GeneratedValue, isto é, caso exista algum atributo não estático sem esta
     * anotação, este será retornado. Este método é destinado a construção de
     * consultas genéricas.
     *
     * @param classe Classe da entidade.
     * @return
     */
    public static Field primeiroAtributo(Class classe) {
        Field field = SingletonCacheEntidade.buscaDoCache(classe).getPrimeiroAtributo();
        if (field != null) {
            return field;
        }
        Field f = null;
        for (Field atributo : getAtributos(classe)) {
            if (Modifier.isStatic(atributo.getModifiers())) {
                continue;
            }
            if (f == null) {
                f = atributo;
            }
            if (!atributo.isAnnotationPresent(GeneratedValue.class)) {
                return atributo;
            } else if (f == null) {
                f = atributo;
            }
        }
        return f;
    }

    public static void setDataRegistro(Object entidade) {
        Class classe = entidade.getClass();
        try {
            Field dataRegistro = SingletonCacheEntidade.buscaDoCache(classe).getAtributoDataRegistro();
            if (dataRegistro == null) {
                for (Field f : getAtributos(classe)) {
                    if (f.getName().equals("dataRegistro")) {
                        f.setAccessible(true);
                        f.set(entidade, new Date());
                        return;
                    }
                }
            } else {
                dataRegistro.setAccessible(true);
                dataRegistro.set(entidade, new Date());
            }
            return;
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar o atributo dataRegistro na classe " + classe, ex);
        }
    }

    public static void setAtivo(Object entidade, boolean valor) {
        Class classe = entidade.getClass();
        try {
            Field ativo = SingletonCacheEntidade.buscaDoCache(classe).getAtributoAtivo();
            if (ativo == null) {
                for (Field f : getAtributos(classe)) {
                    if (f.getName().equals("ativo")) {
                        f.setAccessible(true);
                        f.set(entidade, valor);
                        return;
                    }
                }
            } else {
                ativo.setAccessible(true);
                ativo.set(entidade, valor);
            }
            return;
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar o atributo ativo na classe " + classe, ex);
        }
    }

    public static void duplicaColecoes(Object destino, Object origem) {
        try {
            for (Field f : getAtributos(origem.getClass())) {
                if (f.isAnnotationPresent(OneToMany.class) || (f.isAnnotationPresent(ManyToMany.class))) {
                    logger.debug("Colecao " + f);
                    f.setAccessible(true);
                    Collection colecaoDestino = null;
                    Collection colecaoOrigem = (Collection) f.get(origem);
                    if (f.getType().equals(Set.class)) {
                        colecaoDestino = new HashSet();
                    }
                    if (f.getType().equals(List.class)) {
                        colecaoDestino = new ArrayList();
                    }
                    for (Object obj : colecaoOrigem) {
                        colecaoDestino.add(obj);
                    }
                    f.set(destino, colecaoDestino);
                }
            }
        } catch (Exception ex) {
            logger.error("Problema ao duplicar colecoes", ex);
        }
    }

    public static String getNomeDoCampo(Field f) {
        String retorno = SingletonCacheEntidade.buscaDoCache(f.getDeclaringClass()).getEtiquetas().get(f);
        if (retorno != null) {
            return retorno;
        }
        if (f.isAnnotationPresent(Etiqueta.class)) {
            return f.getAnnotation(Etiqueta.class).value();
        }
        return f.getName().substring(0, 1) + f.getName().substring(1, f.getName().length());
    }

    public static String getAsStringAtributoValue(Object objeto, Field atributo) {
        if (objeto == null) {
            return "";
        }
        atributo.setAccessible(true);
        try {
            Object valor = atributo.get(objeto);
            if (valor == null) {
                return "";
            }
            if (atributo.isAnnotationPresent(br.com.webpublico.util.anotacoes.Monetario.class)) {
                NumberFormat nf = NumberFormat.getCurrencyInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(3);
                return nf.format(new BigDecimal(valor.toString()));
            }
            if (atributo.isAnnotationPresent(br.com.webpublico.util.anotacoes.UFM.class)) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(4);
                return nf.format(new BigDecimal(valor.toString()));
            }
            if (atributo.isAnnotationPresent(br.com.webpublico.util.anotacoes.Porcentagem.class)) {
                NumberFormat nf = NumberFormat.getPercentInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(4);
                return nf.format(new BigDecimal(valor.toString()).divide(new BigDecimal("100")));
            }
            if (atributo.isAnnotationPresent(br.com.webpublico.util.anotacoes.Numerico.class)) {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(3);
                return nf.format(new BigDecimal(valor.toString()));
            }
            if (atributo.getType().equals(Date.class)) {
                Temporal t = atributo.getAnnotation(Temporal.class);
                if (t.value() == TemporalType.TIME) {
                    return Util.formatterHoraMinuto.format(valor);
                }
                if (t.value() == TemporalType.DATE) {
                    return Util.formatterDiaMesAno.format(valor);
                } else {
                    return Util.formatterDataHora.format(valor);
                }

            }
            if (atributo.getType().equals(Boolean.class)) {
                if (valor.equals(true)) {
                    return "Sim";
                } else {
                    return "Não";
                }
            }
            if ((atributo.getType().equals(List.class))) {
                String lista = valor.toString().replaceAll("\\[", "<table><tr><td>");
                lista = lista.replaceAll(",", "</td></tr><tr><td>");
                lista = lista.replaceAll("]", "</td></tr></table>");
                return lista;
            }
            if ((atributo.getType().equals(Map.class))) {
                String lista = valor.toString().replaceAll("\\{", "<table><tr><td>");
                lista = lista.replaceAll(" ,", "</td></tr><tr><td>");
                lista = lista.replaceAll("}", "</td></tr></table>");
                return lista;
            }
            if (atributo.isAnnotationPresent(Enumerated.class)) {
                String nomeDaClasse = atributo.getType().toString();
                nomeDaClasse = nomeDaClasse.replace("class ", "");
                Class<?> classe = null;
                try {
                    classe = Class.forName(nomeDaClasse);
                } catch (ClassNotFoundException ex) {
                }


                for (Field field : classe.getDeclaredFields()) {
                    if (field.isEnumConstant()) {
                        try {
                            Enum<?> valorEnum = Enum.valueOf((Class<? extends Enum>) classe, field.getName());
                            if (valor.equals(valorEnum)) {
                                String nomeCampo = "descricao";
                                String methodName = "get" + nomeCampo.substring(0, 1).toUpperCase() + nomeCampo.substring(1);
                                Object valorRecuperado = valorEnum.getClass().getMethod(methodName, new Class[]{}).invoke(valorEnum, new Object[]{});
                                return valorRecuperado.toString();
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
            return valor.toString();
        } catch (Exception ex) {
            logger.error("Problemas na conversão de atributos", ex);
        }
        return "?" + objeto.toString();
    }

    public static void verificaExistenciaObjtoNalista(List lista, Object o) throws Exception {
        if (lista == null) {
            throw new NullPointerException("A lista verificada esta nula!");
        }

        if (o == null) {
            throw new NullPointerException("O objeto a ser adiconado na lista está nulo!");
        }

        if (lista.contains(o)) {
            throw new ExcecaoNegocioGenerica("O " + o.toString() + " já existe na Lista!");
        }
    }

    public static String getNomeDaClasse(Class classe) {
        String retorno = SingletonCacheEntidade.buscaDoCache(classe).getNomeClasse();
        if (retorno != null) {
            return retorno;
        }
        if (classe.isAnnotationPresent(br.com.webpublico.util.anotacoes.Etiqueta.class)) {
            br.com.webpublico.util.anotacoes.Etiqueta e = (Etiqueta) classe.getAnnotation(br.com.webpublico.util.anotacoes.Etiqueta.class);
            return e.value();
        }
        return classe.getSimpleName();
    }


    public static boolean possuiAnotacao(Class classe, Class anotacao) {
        return classe.isAnnotationPresent(anotacao);
    }

    public static boolean isEntidade(Class c) {
        return possuiAnotacao(c, Entity.class);
    }

    public static boolean isAtributoDeEntidade(Field atributo) {
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

    public static Class getGenericTypeFromCollection(Field field, Class classe) {
        ParameterizedType pt = (ParameterizedType) field.getGenericType();
        for (Type t : pt.getActualTypeArguments()) {
            if (Persistencia.isEntidade((Class) t)) {
                classe = (Class) t;
            }
        }
        return classe;
    }
    public static Field getField(Class classe, String atributo) {
        try {
            for (Field f : getAtributos(classe)) {
                if (f.getName().equals(atributo)) {
                    return f;
                }
            }
        } catch (Exception ex) {
            logger.error("Não foi possível encontrar o atributo versão anterior na " + classe, ex);
        }
        return null;
    }


    private static int pegarQuantiaAtualDeExecucoesRecursivas(String method) {
        int qtd = 0;
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : elements) {
            if (element.getMethodName().equals(method)) {
                qtd++;
            }
        }
        return qtd - 1;
    }

    public static boolean possuiAnotacaoNaImplementacaoLazy(Object object, Class anotacao) {
        if (object instanceof HibernateProxy) {
            return possuiAnotacaoNasClassesPai(((HibernateProxy) object).getHibernateLazyInitializer().getImplementation().getClass(), anotacao);
        }
        return false;
    }

    public static boolean possuiAnotacaoNasClassesPai(Class classe, Class anotacao) {
        return SingletonCacheAnotacoes.buscaDoCache(classe, anotacao);
    }

    /**
     * @param nivel           tente sempre usar o menor número possível no nivel de profundidade da inicialização generica,
     *                        um numero muito alto vai causar o carregamento recursivo de todos os dados do banco
     * @param camposIgnorados utilize o nome do campo junto com o pacote e a classe
     * @throws IllegalAccessException
     */
    public static void inicializarObjetoGenerico(Object obj, int nivel, String... camposIgnorados) throws IllegalAccessException { //TODO limitar o tempo de execução maxíma para prevenir de carregar muitos dados ao utilizar um nivel muito alto
        inicializarObjetoGenerico(obj, null, Lists.newArrayList(), nivel, camposIgnorados);
    }

    private static void inicializarObjetoGenerico(Object obj, Object objetoPai, List<Object> jaInicializados, int nivelMaximo, String[] camposIgnorados) throws IllegalAccessException {
        if (pegarQuantiaAtualDeExecucoesRecursivas("inicializarObjetoGenerico") >= nivelMaximo) {
            return;
        }
        if (obj == null || jaInicializados.contains(obj)) {
            return;
        }
        if (!Hibernate.isInitialized(obj)) {
            Hibernate.initialize(obj);
        }
        jaInicializados.add(obj);
        Field[] fields = Persistencia.getAtributos(obj.getClass()).toArray(new Field[0]);
        for (Field field : fields) {
            if (camposIgnorados != null && camposIgnorados.length > 0) {
                boolean flagIgnorarField = false;
                for (String nomeCampoIgnorado : camposIgnorados) {
                    if ((field.getDeclaringClass().getName() + "." + field.getName()).equals(nomeCampoIgnorado)) {
                        flagIgnorarField = true;
                        break;
                    }
                }
                if (flagIgnorarField) {
                    continue;
                }
            }
            try {
                boolean flagAcessible = false;
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                    flagAcessible = true;
                }
                if (!Hibernate.isInitialized(field.get(obj))) {
                    Hibernate.initialize(field.get(obj));
                }
                if (field.get(obj) != null) {
                    if (objetoPai != null && obj.equals(objetoPai)) {
                        return;
                    }
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        Collection collection = (Collection) field.get(obj);
                        Iterator iterator = collection.iterator();
                        while (iterator.hasNext()) {
                            try {
                                inicializarObjetoGenerico(iterator.next(), obj, jaInicializados, nivelMaximo, camposIgnorados);
                            } catch (EntityNotFoundException en) {
                                throw en;
                            } catch (Exception e) {
                                logger.error("Erro ao inicializar: ", e);
                            }
                        }
                        List<Object> objects = Lists.newArrayList();
                        objects.addAll(collection);
                        field.set(obj, objects);
                    } else if (Map.class.isAssignableFrom(field.getType())) {
                        Map map = (Map) field.get(obj);
                        for (Object object : map.values()) {
                            try {
                                inicializarObjetoGenerico(object, obj, jaInicializados, nivelMaximo, camposIgnorados);
                            } catch (EntityNotFoundException en) {
                                throw en;
                            } catch (Exception e) {
                                logger.error("Erro ao inicializar: ", e);
                            }
                        }
                    } else if (field.get(obj) instanceof SuperEntidade || possuiAnotacaoNasClassesPai(field.get(obj).getClass(), Entity.class) || possuiAnotacaoNaImplementacaoLazy(field.get(obj), Entity.class)) {
                        if (field.get(obj) instanceof HibernateProxy) {
                            inicializarObjetoGenerico(((HibernateProxy) field.get(obj)).getHibernateLazyInitializer().getImplementation(), obj, jaInicializados, nivelMaximo, camposIgnorados);
                            field.set(obj, ((HibernateProxy) field.get(obj)).getHibernateLazyInitializer().getImplementation());
                        } else {
                            inicializarObjetoGenerico(field.get(obj), obj, jaInicializados, nivelMaximo, camposIgnorados);
                        }
                    }
                }
                if (flagAcessible) {
                    field.setAccessible(false);
                }
            } catch (EntityNotFoundException en) {
                throw en;
            } catch (Exception e) {
                logger.error("Erro ao inicializar: ", e);
            }
        }
    }
}
