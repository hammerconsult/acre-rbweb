package br.com.webpublico.negocios.padrao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.negocios.SingletonWPEntities;
import br.com.webpublico.util.Persistencia;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

@Transactional
public abstract class AbstractJPARepository<T extends SuperEntidade> implements WebPublicoRepository<T>, Serializable {


    public static final int MAX_RESULTADOS_NA_CONSULTA = 50;
    public static final int MAX_RESULTADOS_NO_AUTOCOMPLETE = 10;

    protected abstract EntityManager getEntityManager();

    /**
     * Instancia do logador
     */
    protected static final Logger logger = LoggerFactory.getLogger(AbstractJPARepository.class);
    /**
     * Atributo que representa a classe da entidade
     */
    private Class<T> classe;

    /**
     * Deve ser executado no construtor da subclasse para indicar qual é a
     * classe da entidade
     *
     * @param classe Classe da entidade
     */
    public AbstractJPARepository(Class<T> classe) {
        this.classe = classe;
    }

    public Class<T> getClasse() {
        return classe;
    }


    /**
     * Salva a entidade através do método persist
     *
     * @param entity Entidade a ser salva
     */
    @Override
    public void salvarNovo(T entity) {
        if (entity == null) {
            return;
        }
        getEntityManager().persist(entity);
    }

    /**
     * Salva a entidade através do método merge
     *
     * @param entity Entidade a ser salva
     */
    @Override
    public T salvar(T entity) throws OptimisticLockException {
        if (entity == null) {
            return null;
        }
        return getEntityManager().merge(entity);
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
    @Override
    public void remover(T entity) {
        if (entity == null) {
            return;
        }
        Object chave = Persistencia.getId(entity);
        Object obj = buscar(chave);

        if (obj != null) {
            getEntityManager().remove(obj);
        }
    }

    /**
     * Retorna a entidade do facade com uma determinada chave
     *
     * @param id chave da entidade
     * @return a entidade recuperada ou null caso não exista entidade com tal
     * chave.
     */
    public T buscar(Object id) {
        return getEntityManager().find(classe, id);
    }

    /**
     * Retorna a entidade do facade com uma determinada chave
     * Deve ser utilizado para recuperar somente a entidade em questão sem considerar
     * as dependencias da mesma. Caso queira-se recuperar até as dependencias, utilizar/sobrescrever o 'recuperar'
     *
     * @param id chave da entidade
     * @return a entidade recuperada ou null caso não exista entidade com tal
     * chave.
     */
    @Override
    public T recuperar(Long id) {
        if (id == null) {
            return null;
        }
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
     * Lista todos os registros da entidade em questão ordenada ascendentemente pelo ID
     *
     * @return List contendo as entidades
     */
    public List<T> buscarTodos() {
        String hql = "from " + classe.getSimpleName() + " obj order by obj." + Persistencia.getFieldId(classe).getName() + " asc";
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    /**
     * Lista todos os registros da entidade em questão ordenada descendentemente pelo ID
     *
     * @return List contendo as entidades
     */
    public List<T> buscarTodosDecrescente() {
        String hql = "from " + classe.getSimpleName() + " obj order by obj." + Persistencia.getFieldId(classe).getName() + " desc";
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    public List<T> buscarFiltrando(String s, String... atributos) {

        String hql = "from " + classe.getSimpleName() + " obj where ";
        for (String atributo : atributos) {
            Field field = Persistencia.getField(classe, atributo);
            field.setAccessible(true);
            if (field.getType().equals(Long.class)
                || field.getType().equals(Integer.class)) {
                hql += " lower(to_char(obj." + atributo + ")) like :filtro OR ";
            } else {
                hql += "lower(obj." + atributo + ") like :filtro OR ";
            }
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLETE);
        return q.getResultList();
    }

    /**
     * Retorna o número de entidades armazenadas no banco
     *
     * @return número de entidades
     */
    public long count() {
        Query query = getEntityManager().
            createQuery("SELECT COUNT(obj." + Persistencia.getFieldId(classe).getName() + ") FROM " + classe.getSimpleName() + " obj");
        Long quantidade = (Long) query.getSingleResult();
        return quantidade;
    }

    public Long retornarUltimoCodigo() {
        Long num;
        String sql = " SELECT max(coalesce(cast(obj.codigo as NUMERIC ),0)) FROM " + classe.getSimpleName() + " obj ";
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
            return 1L;
        }
        return num;
    }

    @Override
    public String getNomeDependencia(String message) throws ClassNotFoundException {
        try {
            return SingletonWPEntities.getINSTANCE(getEntityManager().getMetamodel().getEntities()).getByTableName(getTableName(getFkName(message))).etiquetaValue;
        } catch (Exception ex) {
            logger.debug("Erro recuperando Nome da Dependência [" + message + "]", ex);
            return "";
        }
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

    public List<T> listar() {
        String hql = "from " + classe.getSimpleName() + " obj order by obj." + Persistencia.primeiroAtributo(classe).getName();
        Query q = getEntityManager().createQuery(hql);

        return q.getResultList();
    }

    private List<T> listarFiltrando(String s, String... atributos) {

        String hql = "from " + classe.getSimpleName() + " obj where ";
        for (String atributo : atributos) {
            Field field = Persistencia.getField(classe, atributo);
            field.setAccessible(true);
            if (field.getType().equals(Long.class)
                || field.getType().equals(Integer.class)) {
                hql += " lower(to_char(obj." + atributo + ")) like :filtro OR ";
            } else {
                hql += "lower(obj." + atributo + ") like :filtro OR ";
            }
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLETE);
        return q.getResultList();
    }


    @Override
    public List<T> listar(String atributo, String... valores) {
        if (atributo == null || "".equals(atributo) || valores.length == 0) {
            return listar();
        }
        return listarFiltrando(atributo, valores);
    }

    @Override
    public long contar() {
        return count();
    }

    @Override
    public T initializeAndUnproxy(T proxiedEntity) {
        if (proxiedEntity == null) {
            return null;
        }
        T retorno = null;
        Hibernate.initialize(proxiedEntity);
        if (proxiedEntity instanceof HibernateProxy) {
            retorno = (T) ((HibernateProxy) proxiedEntity).getHibernateLazyInitializer().getImplementation();
        }
        return retorno;
    }

    @Override
    public List<Object[]> recuperarHistoricoAlteracoes(T entity) {
        AuditReader reader = AuditReaderFactory.get(getEntityManager());
        Object id = Persistencia.getId(entity);
        Class<?> classe = entity.getClass();
        List<Object[]> lista = reader.createQuery().forRevisionsOfEntity(classe, false, true).add(AuditEntity.id().eq(id)).addOrder(AuditEntity.revisionNumber().desc()).getResultList();
        return lista;
    }

    @Override
    public void forcarRegistroInicialNaAuditoria(T entity) {
        List<Object[]> objetos = recuperarHistoricoAlteracoes(entity);
        if (objetos == null || objetos.isEmpty()) {
            Session s = getEntityManager().unwrap(Session.class);
            s.evict(entity);
            s.saveOrUpdate(entity);
        }
    }

}
