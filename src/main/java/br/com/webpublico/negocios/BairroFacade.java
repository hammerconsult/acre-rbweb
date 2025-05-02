/*
 * Codigo gerado automaticamente em Fri Feb 11 08:09:57 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Testada;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class BairroFacade extends AbstractFacade<Bairro> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LogradouroFacade logradouroFacade;

    public BairroFacade() {
        super(Bairro.class);
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SingletonWPEntities getSingletonWPEntities() {
        return SingletonWPEntities.getINSTANCE(getEntityManager().getMetamodel().getEntities());
    }

    @Override
    public Bairro recuperar(Object id) {
        Bairro bairro = super.recuperar(id);
        bairro.getLogradouros().size();
        return bairro;
    }

    public List<Bairro> listarPorCidade(Cidade cidade, String parte) {
        if (cidade == null || cidade.getId() == null) {
            return listaFiltrando(parte, "descricao");
        } else {
            String hql = "from Bairro b where lower(b.descricao) like :bairoParametro and b.cidade = :cidadeParametro";
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("bairoParametro", "%" + parte.toLowerCase() + "%");
            q.setParameter("cidadeParametro", cidade);
            return q.getResultList();
        }
    }

    public List<Bairro> listaPorCodigo(String parte) {
        String hql = " select b from " + Bairro.class.getSimpleName() + " as b "
            + " where to_char(b.codigo) like :parte ";

        Query query = em.createQuery(hql);
        query.setMaxResults(50);
        query.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public List<Bairro> consultaBairros(String localidade, String parte) {
        Query q = em.createQuery("select distinct bairro from Bairro bairro, Cidade cidade where lower(cidade.nome) like :localidade and lower(bairro.descricao) like :parte and bairro.ativo = 1");
        q.setParameter("localidade", "%" + localidade.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public Bairro mergeEntity(Bairro bairro) {
        return em.merge(bairro);
    }

    public Long ultimoNumeroMaisUm() {
        Query q = em.createNativeQuery("select coalesce(max(codigo), 0) + 1 as numero from Bairro");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    @Override
    public List<Bairro> listaFiltrando(String s, String... atributos) {
        String hql = "from Bairro obj where (";
        for (String atributo : atributos) {
            hql += "lower(obj." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        hql += ") and obj.ativo = :ativo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("ativo", true);
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();
    }

    public boolean existeBairroPorCodigo(Long id, Long codigo) {
        StringBuilder sql = new StringBuilder("select * from bairro where codigo = :codigo");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("codigo", codigo);
        if (id != null) {
            q.setParameter("id", id);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean existeBairroPorNome(Long id, String nome) {
        StringBuilder sql = new StringBuilder("select * from bairro where upper(replace(descricao,' ', '')) = :nome and ativo = 1");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("nome", nome.trim().replaceAll(" ", "").toUpperCase());
        if (id != null) {
            q.setParameter("id", id);
        }
        return !q.getResultList().isEmpty();
    }

    public Bairro bairroPorNome(String nome) {
        StringBuilder sql = new StringBuilder("select * from bairro where upper(replace(descricao,' ', '')) = :nome and ativo = 1");
        Query q = em.createNativeQuery(sql.toString(), Bairro.class);
        q.setParameter("nome", nome.trim().replaceAll(" ", "").toUpperCase());
        if (!q.getResultList().isEmpty()) {
            return (Bairro) q.getResultList().get(0);
        }
        return null;
    }

    public Bairro bairroPorCodigo(Long codigo) {
        StringBuilder sql = new StringBuilder("select * from bairro where codigo = :codigo and ativo = 1");
        Query q = em.createNativeQuery(sql.toString(), Bairro.class);
        q.setParameter("codigo", codigo);
        if (!q.getResultList().isEmpty()) {
            return (Bairro) q.getResultList().get(0);
        }
        return null;
    }

    public List<Bairro> completaBairro(String parte) {
        StringBuilder sql = new StringBuilder("select * from bairro");
        sql.append(" where codigo like :parte");
        sql.append(" or trim(upper(descricao)) like :parte");
        sql.append(" and ativo = 1");

        Query q = em.createNativeQuery(sql.toString(), Bairro.class);
        q.setParameter("parte", "%" + parte.toUpperCase().trim() + "%");
        q.setMaxResults(10);

        return q.getResultList();
    }

    public Bairro recuperaBairroPorTestada(Testada testada) {
        StringBuilder sql = new StringBuilder("select * from bairro b");
        sql.append(" inner join logradouroBairro lb on lb.bairro_id = b.id");
        sql.append(" inner join face f on f.logradourobairro_id = lb.id");
        sql.append(" inner join testada t on t.face_id = f.id");
        sql.append(" where t.id = :testada");
        Query q = em.createNativeQuery(sql.toString(), Bairro.class);
        q.setParameter("testada", testada.getId());
        return (Bairro) q.getSingleResult();
    }

    public List<Object[]> buscarBairros() {
        String sql = " select distinct bai.codigo as codigoBairro, bai.descricao as descricaoBairro, coalesce(setor.codigo, '0') from bairro bai " +
            " left join LOGRADOUROBAIRRO lb on lb.BAIRRO_ID = bai.id " +
            " left join FACE face on face.LOGRADOUROBAIRRO_ID = lb.id " +
            " left join TESTADA tes on tes.FACE_ID = face.id " +
            " left join lote lote on lote.id = tes.LOTE_ID " +
            " left join setor setor on setor.id = lote.SETOR_ID " +
            " where bai.ativo = :true " +
            " order by bai.descricao, coalesce(setor.codigo, '0') ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("true", Boolean.TRUE);
        return q.getResultList();
    }

}
