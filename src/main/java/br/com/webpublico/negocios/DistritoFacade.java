/*
 * Codigo gerado automaticamente em Fri Feb 11 13:59:38 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Bairro;
import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.Distrito;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class DistritoFacade extends AbstractFacade<Distrito> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private DistritoFacade distritoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DistritoFacade() {
        super(Distrito.class);
    }

    @Override
    public void salvarNovo(Distrito entity) {
        super.salvarNovo(entity);
    }

    @Override
    public void remover(Distrito entity) {
        try {
            super.remover(entity);
        } catch (Exception e) {
            //System.out.println("Erro ao remover " + entity.getNome());
        }
    }

    public List<Distrito> listaDistritosSemMunicipio(String nome) {
        String hql = "from Distrito d where d.municipio is null and lower(d.cidade.nome) like :nome";
        Query q = em.createQuery(hql).setParameter("nome", "%" + nome.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    @Override
    public List<Distrito> lista() {
        String sql = "select * from DISTRITO order by CODIGO";
        Query q = em.createNativeQuery(sql, Distrito.class);
        return q.getResultList();
    }

    public Distrito recuperaDistritoPorCodigo(Integer codigo) {
        Query q = em.createQuery("from Distrito d where d.codigo = :codigo ");

        q.setParameter("codigo", codigo != null ? codigo.toString() : "0");
        if (q.getResultList().size() > 0) {
            return (Distrito) q.getResultList().get(0);
        } else {
            return null;
        }
    }

    public boolean existeDistritoComMesmoCodigo(Long id, String codigo) {
        StringBuilder sql = new StringBuilder("select * from distrito where codigo = :codigo");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("codigo", codigo);
        if (id != null) {
            q.setParameter("id", id);
        }

        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean existeDistritoComMesmoNome(Long id, String nome) {
        StringBuilder sql = new StringBuilder("select * from distrito where upper(replace(nome, ' ', '')) = :nome ");
        if (id != null) {
            sql.append(" and id <> :id");
        }
        Query q = em.createNativeQuery(sql.toString());
        if (id != null) {
            q.setParameter("id", id);
        }
        q.setParameter("nome", nome.replaceAll(" ", "").toUpperCase());
        if (q.getResultList().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Distrito recuperaDistritoPorNome(String nome) {
        StringBuilder hql = new StringBuilder("from Distrito d where upper(replace(d.nome, ' ', '')) = :nome ");
        Query q = em.createQuery(hql.toString());
        q.setParameter("nome", nome.replaceAll(" ", "").toUpperCase());
        if (!q.getResultList().isEmpty()) {
            return (Distrito) q.getResultList().get(0);
        } else {
            return null;
        }
    }

    public List<Distrito> recuperaDistrito() {
        Query q = em.createQuery("from Distrito ");
        return q.getResultList();
    }

    public DistritoFacade getDistritoFacade() {
        return distritoFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }
}
