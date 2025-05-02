/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.singletons.SingletonEntidade;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * Classe abstrata para construção de operações genéricas com entidades.
 *
 * @author Jaime e Munif
 */
public abstract class AbstractFacade<T> implements Serializable {

    public static final int MAX_RESULTADOS_NA_CONSULTA = 50;
    public static final int MAX_RESULTADOS_NO_AUTOCOMPLATE = 10;
    /**
     * Instancia do logador
     */
    protected static final Logger logger = LoggerFactory.getLogger(AbstractFacade.class);
    /**
     * Atributo que representa a classe da entidade
     */
    private Class<T> classe;

    @EJB
    private SingletonEntidade singletonEntidade;

    /**
     * Deve ser executado no construtor da subclasse para indicar qual é a
     * classe da entidade
     *
     * @param classe Classe da entidade
     */
    public AbstractFacade(Class<T> classe) {
        this.classe = classe;
    }

    public static <T> T initializeAndUnproxy(T var) {
        if (var == null) {
            return null;
        }
        Hibernate.initialize(var);
        if (var instanceof HibernateProxy) {
            var = (T) ((HibernateProxy) var).getHibernateLazyInitializer().getImplementation();
        }
        return var;
    }

    public Class<T> getClasse() {
        return classe;
    }

    /**
     * Método abstrato que deve ser implementado para retornar a instância do
     * EntityManager atual.
     *
     * @return EntityManager atual
     */
    protected abstract EntityManager getEntityManager();

    /**
     * Salva a entidade através do método persist
     *
     * @param entity Entidade a ser salva
     */
    public void salvarNovo(T entity) {
        preSave(entity);

        getEntityManager().persist(entity);
    }

    public void preSave(T entidade) {
        //AQUI TAMBÉM PODEM SER ADICIONADAS VALIDAÇÕES QUE A ENTIDADE NÃO CONSEGUE FAZER SOZINHA, COMO TER QUE ACESSAR ALGUM DADO NO BANCO POR EXEMPLO.
    }

    /**
     * Salva a entidade através do método merge
     *
     * @param entity Entidade a ser salva
     */
    public void salvar(T entity) {
        preSave(entity);

        try {
            getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    /**
     * Salva a entidade através do método merge, retornando o objeto salvo
     *
     * @param entity Entidade a ser salva
     */
    public T salvarRetornando(T entity) {
        preSave(entity);

        try {
            return getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
        return null;
    }

    /**
     * Salva a entidade em uma transação separada, e torna o objeto salvo fora de uma transação
     * chamar utilizando sessionContext.getBusinessObject
     *
     * @param entity Entidade a ser salva
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public T salvarRetornandoEmNovaTransacao(T entity) {
        try {
            return getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
        return null;
    }

    /**
     * Remove a entidade. Este método recupera a entidade com o método recuperar
     * e em seguida exclui a entidade. Isto é necessário pois a entidade deve
     * ser recuperada e excluída dentro da mesma trasação. Caso não seja
     * possível recuperar a entidade, por exemplo devido a uma exclusão recente,
     * o método não faz nada.
     *
     * @param entity Entidade a ser salva
     */
    public void remover(T entity) {
        try {
            Object chave = Persistencia.getId(entity);
            Object obj = recuperar(chave);
            if (obj != null) {
                getEntityManager().remove(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public T recarregar(T entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Object obj = recuperar(chave);
        return (T) obj;
    }

    /**
     * Recarrega a entidade em uma nova transação, prevenindo commits não intencionais
     * chamar utilizando sessionContext.getBusinessObject
     *
     * @param entity Entidade a ser salva
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public T recarregarEmNovaTransacao(T entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Object obj = recuperar(chave);
        return (T) obj;
    }

    /**
     * Retorna a entidade do facade com uma determinada chave
     *
     * @param id chave da entidade
     * @return a entidade recuperada ou null caso não exista entidade com tal
     * chave.
     */
    public T recuperar(Object id) {
        return getEntityManager().find(classe, id);
    }

    /**
     * Recupera uma entidade específica a partir da chave. Se não encontrar
     * retorna null
     *
     * @param entidade classe da entidade
     * @param id       valor da chave
     * @return entidade recuperada ou null se não encontrar
     */
    public Object recuperar(Class entidade, Object id) {
        return getEntityManager().find(entidade, id);
    }

    /**
     * Lista todos as entidades da entidade do facade ordenado pelo primeiro
     * atributo
     *
     * @return List contendo as entidades
     */
    public List<T> lista() {
        /*
         * TODO Mudar forma de lista. ordenar por id decrescente (Ultimo salvo)
         */


        String hql = "from " + classe.getSimpleName() + " obj order by obj." + Persistencia.primeiroAtributo(classe).getName();
        Query q = getEntityManager().createQuery(hql);

        return q.getResultList();
    }

    public List<T> buscarTodosOrdenandoPorParametro(String parametro) {
        String hql = "from " + classe.getSimpleName() + " order by " + parametro;
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    public List<T> listaDecrescente() {
        /*
         * TODO Mudar forma de lista. ordenar por id decrescente (Ultimo salvo)
         */


        String hql = "from " + classe.getSimpleName() + " obj order by obj.id desc ";
        Query q = getEntityManager().createQuery(hql);

        return q.getResultList();
    }

    public List<T> listaId(Long id, String campo) {
        String hql = "from " + classe.getSimpleName() + " obj where obj." + campo + " = " + id;
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    public List<T> listaFiltrando(String s, String... atributos) {
        return listaFiltrando(s, MAX_RESULTADOS_NO_AUTOCOMPLATE, atributos);
    }

    public List<T> listaFiltrando(String s, Integer qtdeRegistros, String... atributos) {
        String hql = "from " + classe.getSimpleName() + " obj where ";
        for (String atributo : atributos) {
            hql += "lower(to_char(obj." + atributo + ")) like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        if (qtdeRegistros != null) {
            q.setMaxResults(qtdeRegistros);
        }
        return q.getResultList();
    }

    public List<T> listaFiltrandoOrdenada(String s, Integer qtdeRegistros, String orderByObj, String... atributos) {
        String hql = "from " + classe.getSimpleName() + " obj where ";
        for (String atributo : atributos) {
            hql += "lower(to_char(obj." + atributo + ")) like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        hql += " order by " + orderByObj;
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        if (qtdeRegistros != null) {
            q.setMaxResults(qtdeRegistros);
        }
        return q.getResultList();
    }

    public List<T> listaFiltrandoX(String s, int inicio, int max, Field... atributos) {
        Long longo = 0l;
        boolean eNumero = false;
        try {
            longo = Long.parseLong(s);
            eNumero = true;
        } catch (NumberFormatException ex) {
            eNumero = false;
        }
        boolean temParametro = false;
        String hql = "from " + classe.getSimpleName() + " obj ";
        String junct = " where ";
        for (Field atributo : atributos) {
            if (atributo.getType().equals(String.class)) {
                hql += junct + "lower(obj." + atributo.getName() + ") like :filtro ";
                junct = " OR ";
                temParametro = true;
            } else if (eNumero) {
                hql += junct + "obj." + atributo.getName() + " = :filtroNumero";
                junct = " OR ";
            }
        }
        Query q = getEntityManager().createQuery(hql);
        if (temParametro) {
            q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        }
        if (eNumero) {
            q.setParameter("filtroNumero", longo);
        }
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    /*
     * Metodo para autoComplete em que se passam campos numericos para busca.
     * Caso o metodo @contidoNaLista retornar uma string não vazia, essa listra
     * servira como filtro onde os valores retornados por contidoNaLista não são
     * apresentos no autoComplete.
     */
    public List<T> listaFiltrandoInteiro(String s, String... atributos) {
        String hql = "from " + classe.getSimpleName() + " obj where ";
        for (String atributo : atributos) {
            hql += "obj." + atributo + " = :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", new Integer(s));
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();
    }

    public List<T> listaFiltrandoFk(String campo, Long fk, String s, String... atributos) {
        String hql = "from " + classe.getSimpleName() + " obj where obj." + campo + "=" + fk + " and ";
        for (String atributo : atributos) {
            hql += "lower(obj." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();
    }

    /**
     * Retorna o número de entidades armazenadas no banco
     *
     * @return número de entidades
     */
    public long count() {
        Query query = getEntityManager().createQuery("SELECT COUNT(obj." + Persistencia.getFieldId(classe).getName() + ") FROM " + classe.getSimpleName() + " obj");
        Long quantidade = (Long) query.getSingleResult();
        return quantidade;
    }

    public List executaConsultaGenerica(String consulta) {
        return getEntityManager().createQuery(consulta).getResultList();
    }

    public Object criaAssociacoesAleatorias(Object aRetornar) {
        Random rand = new Random();
        try {
            for (Field f : Persistencia.getAtributos(aRetornar.getClass())) {
                f.setAccessible(true);
                Class classeDoAtributo = f.getType();
                if (classeDoAtributo.equals(List.class) || classeDoAtributo.equals(List.class)) {
                    ParameterizedType type = (ParameterizedType) f.getGenericType();
                    Type[] typeArguments = type.getActualTypeArguments();
                    Class tipoGenerico = (Class) typeArguments[f.getType().equals(Map.class) ? 1 : 0];
                    Collection listaNoObjeto = classeDoAtributo.equals(List.class) ? new ArrayList() : new HashSet();
                    List listaDeValoresPossiveis = this.executaConsultaGenerica("from " + tipoGenerico.getSimpleName());
                    if (listaDeValoresPossiveis.size() > 0) {
                        listaNoObjeto.add(listaDeValoresPossiveis.get(rand.nextInt(listaDeValoresPossiveis.size())));
                        f.set(aRetornar, listaNoObjeto);
                    }
                }

                if (classeDoAtributo.isAnnotationPresent(Entity.class)) {
                    if (f.get(aRetornar) == null) {
                        List lista = this.executaConsultaGenerica("from " + classeDoAtributo.getSimpleName());
                        if (lista.size() > 0) {
                            f.set(aRetornar, lista.get(rand.nextInt(lista.size())));
                        }
                    }
                }
            }
            return aRetornar;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /*
     * Método para retornar o último código do tipo long já incrementado,
     * utilizado nos cadastros do rh onde o código é único e incremental
     *
     */
    public Long retornarUltimoValorSequencialLong(String atributoSeguencial) {
        Long num;
        String sql = " SELECT max(coalesce(cast(obj." + atributoSeguencial.trim() + " as integer),0)) FROM " + classe.getSimpleName() + " obj ";
        Query query = getEntityManager().createNativeQuery(sql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            BigDecimal b = (BigDecimal) query.getSingleResult();

            if (b != null) {
                b = b.add(BigDecimal.ONE);
            } else {
                b = BigDecimal.ONE;
            }
            num = b.setScale(0, BigDecimal.ROUND_UP).longValueExact();
        } else {
            return 1l;
        }
        return num;
    }

    public Long retornaUltimoCodigoLong() {
        return retornarUltimoValorSequencialLong("codigo");
    }

    public String getNomeDependencia(String message) throws ClassNotFoundException {
        return SingletonWPEntities.getINSTANCE(getEntityManager().getMetamodel().getEntities()).getByTableName(getTableName(getFkName(message))).etiquetaValue;
    }

    private String getFkName(String message) {
        int index_begin = message.indexOf("FK");
        int index_end = message.indexOf(")");
        return message.substring(index_begin, index_end);
    }

    private String getTableName(String fkName) {
        String sql = "select u.table_name\n" +
            "       from dba_constraints d\n" +
            " inner join user_cons_columns u on u.constraint_name = d.constraint_name\n" +
            "      where d.constraint_name = :fkName\n" +
            "        and rownum = 1";

        Query q = getEntityManager().createNativeQuery(sql).setParameter("fkName", fkName);

        return (String) q.getSingleResult();
    }

    /**
     * recupera as revisões de auditoria simples, sem verificar relacionamentos do tipo List.
     *
     * @param objeto
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public List<Object[]> recuperarHistoricoAlteracoes(Object objeto) throws IllegalArgumentException, IllegalAccessException {
        AuditReader reader = AuditReaderFactory.get(getEntityManager());
        Long id = (Long) Persistencia.getId(objeto);
        Class<?> classe = objeto.getClass();
        List<Object[]> lista = reader.createQuery().forRevisionsOfEntity(classe, false, true).add(AuditEntity.id().eq(id)).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        return lista;
    }

    /**
     * Método utilizado para registros que vem de migracao e não tem registro na tabela de auditoria.
     *
     * @param object
     */
    public void forcarRegistroInicialNaAuditoria(Object object) {
        try {
            List<Object[]> objetos = recuperarHistoricoAlteracoes(object);
            if (objetos == null || (objetos != null && objetos.isEmpty())) {
                Session s = getEntityManager().unwrap(Session.class);
                s.evict(object);
                s.saveOrUpdate(object);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public List<String> buscarCasosDeUsoComRegistroVinculado(T registro) {
        List<String> toReturn = Lists.newArrayList();
        Query consultaVinculo = null;
        String sqlConsultaVinculo = "";
        Query query = getEntityManager().createNativeQuery(" select fk.table_name, c_fk.column_name " +
            "   from user_constraints fk " +
            "  inner join user_cons_columns c_fk on c_fk.constraint_name = fk.constraint_name " +
            "  inner join user_constraints pk on pk.constraint_name = fk.r_constraint_name " +
            "where fk.constraint_type = 'R' and pk.table_name in (:table_name)" +
            "  and fk.table_name not in (:table_name)" +
            "  and exists (select 1 from USER_TAB_COLS tabela where tabela.TABLE_NAME = fk.table_name and tabela.column_name = 'ID')");

        List<String> tabelas = Lists.newArrayList();
        tabelas.add(getNomeTabela(registro.getClass()).toUpperCase());
        if (registro.getClass().getSuperclass().isAnnotationPresent(Entity.class)) {
            tabelas.add(getNomeTabela(registro.getClass().getSuperclass()).toUpperCase());
        }
        query.setParameter("table_name", tabelas);


        if (!query.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) query.getResultList()) {
                try {
                    Class<?> entidade = singletonEntidade.buscarEntidadePeloNomeTabela((String) obj[0]);
                    if (isEntidadePresenteFieldListDoRegistro(registro, entidade)) {
                        continue;
                    }
                    sqlConsultaVinculo = "select id from " + obj[0] + " where " + obj[1] + " = " + Persistencia.getId(registro);
                    consultaVinculo = getEntityManager().createNativeQuery(sqlConsultaVinculo);
                    if (!consultaVinculo.getResultList().isEmpty() &&
                        consultaVinculo.getResultList().get(0) != null) {
                        long idVinculo = ((BigDecimal) consultaVinculo.getResultList().get(0)).longValue();
                        String e = singletonEntidade.buscarNomeDoCasoDeUsoPeloNomeDaTabela((String) obj[0]);
                        Map<Field, String> stringStringMap = listarAtributosBasicos((String) obj[0], idVinculo);
                        if (stringStringMap != null && !stringStringMap.isEmpty()) {
                            e += " [";
                            int i = 1;
                            for (Field campo : stringStringMap.keySet()) {
                                e += getConteudoDoCampo(stringStringMap.get(campo), campo);
                                i++;
                                if (i <= stringStringMap.keySet().size()) {
                                    e += ", ";
                                }
                            }
                            e += "]";
                        }
                        toReturn.add(e);
                    }
                } catch (Exception ex) {
                    logger.error("Não foi possível verificar dados para o sql " + sqlConsultaVinculo + " {}", ex);
                }
            }
        }
        return toReturn;
    }

    private String getConteudoDoCampo(String valor, Field campo) {
        if (campo.getType().isAnnotationPresent(Entity.class)) {
            String e = getNomeCampo(campo) + ": [ <i>";
            int i = 1;
            if (valor != null && !valor.isEmpty()) {
                Map<Field, String> fieldStringMap = listarAtributosBasicos(getNomeTabela(campo.getType()), Long.valueOf(valor));
                for (Field campoRelacao : fieldStringMap.keySet()) {
                    e += getNomeCampo(campoRelacao) + ": <strong>" + fieldStringMap.get(campoRelacao) + "</strong>";
                    i++;
                    if (i <= fieldStringMap.keySet().size()) {
                        e += ", ";
                    }
                }
            }
            e += "</i>]";
            return e;

        }
        return getNomeCampo(campo) + ": <strong>" + valor + "</strong>";
    }

    public Map<Field, String> listarAtributosBasicos(String tabela, Long id) {
        try {
            String select = "select tabela.id as idPrincipal";
            Class<?> classe = singletonEntidade.buscarEntidadePeloNomeTabela(tabela);
            LinkedList<Field> campos = Lists.newLinkedList();
            for (Field field : Persistencia.getAtributos(classe)) {
                if (field.isAnnotationPresent(Tabelavel.class)
                    && !field.isAnnotationPresent(Transient.class)
                    && !Collection.class.isAssignableFrom(field.getType())) {
                    String alias = field.getDeclaringClass().equals(classe) ? "tabela." : "pai.";
                    if (field.isAnnotationPresent(Column.class) && !Strings.isNullOrEmpty(field.getAnnotation(Column.class).name())) {
                        select += ", " + alias + field.getAnnotation(Column.class).name();
                    } else if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                        select += ", " + alias + field.getName().toLowerCase() + "_id ";
                    } else {
                        select += ", " + alias + field.getName();
                    }
                    campos.add(field);
                }
            }
            String sql = " from " + tabela.toUpperCase() + " tabela ";
            if (classe.getSuperclass() != null && classe.getSuperclass().isAnnotationPresent(Entity.class)) {
                String pai = getNomeTabela(classe.getSuperclass());
                sql += " inner join " + pai + " pai on pai.id = tabela.id";
            }
            sql += " where tabela.id = " + id;
            Map<Field, String> atributos = Maps.newHashMap();
            List<Object[]> resultList = getEntityManager().createNativeQuery(select + sql).getResultList();
            if (!resultList.isEmpty()) {
                for (Field campo : campos) {
                    Object valor = resultList.get(0)[campos.indexOf(campo) + 1];
                    atributos.put(campo, "" + (valor != null ? valor : ""));
                }
            }
            return atributos;
        } catch (Exception ex) {
            logger.error("Não foi possível listar os atributos basicos: {}", ex);
            return Maps.newHashMap();
        }
    }

    private String getNomeCampo(Field field) {
        if (field.isAnnotationPresent(Etiqueta.class)) {
            return field.getAnnotation(Etiqueta.class).value();
        } else {
            return StringUtils.capitalize(field.getName()).replaceAll("([a-z]+)([A-Z])", "$1 $2");
        }
    }

    private String getNomeTabela(Class classe) {
        return classe.isAnnotationPresent(Table.class)
            && !Strings.isNullOrEmpty(((Table) classe.getAnnotation(Table.class)).name())
            ? ((Table) classe.getAnnotation(Table.class)).name() : classe.getSimpleName();
    }


    private boolean isEntidadePresenteFieldListDoRegistro(T registro, Class<?> entidade) {
        List<Field> fields = Persistencia.getAtributos(registro.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                if (field.getType().equals(List.class)) {
                    Class classeLista = Persistencia.getGenericTypeFromCollection(field, registro.getClass());
                    if (classeLista.equals(entidade)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
