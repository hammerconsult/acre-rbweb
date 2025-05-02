package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TipoPontoComercial;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author daniel
 */
@Stateless
public class TipoPontoComercialFacade extends AbstractFacade<TipoPontoComercial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public TipoPontoComercialFacade() {
        super(TipoPontoComercial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public boolean emUso(TipoPontoComercial tipoPontoComercial) {
        return true;
    }

    public List<TipoPontoComercial> retornaTipoPontoComercial() {
        String hql = "from TipoPontoComercial";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public boolean jaExisteCodigo(TipoPontoComercial selecionado) {
        if (selecionado == null || selecionado.getCodigo() == null) {
            return false;
        }
        String hql = "from TipoPontoComercial tpc where tpc.codigo = :codigo";
        if (selecionado.getId() != null) {
            hql += " and tpc <> :tipoPontoComercial";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", selecionado.getCodigo());
        if (selecionado.getId() != null) {
            q.setParameter("tipoPontoComercial", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean jaExisteDescricao(TipoPontoComercial selecionado) {
        if (selecionado == null || selecionado.getDescricao() == null || selecionado.getDescricao().isEmpty()) {
            return false;
        }
        String hql = "from TipoPontoComercial tpc where lower(tpc.descricao) = :descricao";
        if (selecionado.getId() != null) {
            hql += " and tpc <> :tipoPontoComercial";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", selecionado.getDescricao().toLowerCase());
        if (selecionado.getId() != null) {
            q.setParameter("tipoPontoComercial", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public List<TipoPontoComercial> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createQuery(" from TipoPontoComercial t where lower(cast(t.codigo as string)) like :filtro"
                + " or lower(t.descricao) like :filtro");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<TipoPontoComercial> listaOrdemAlfabetica() {
        String hql = "from TipoPontoComercial obj order by obj.descricao asc ";
        Query q = em.createQuery(hql);
        return q.getResultList();
    }
}
