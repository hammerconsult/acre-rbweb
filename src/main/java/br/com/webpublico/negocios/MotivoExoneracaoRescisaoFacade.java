/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MotivoExoneracaoRescisao;
import br.com.webpublico.entidades.TipoAdmissaoFGTS;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MotivoExoneracaoRescisaoFacade extends AbstractFacade<MotivoExoneracaoRescisao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MotivoExoneracaoRescisaoFacade() {
        super(MotivoExoneracaoRescisao.class);
    }

    public boolean existeCodigo(MotivoExoneracaoRescisao motivoExoneracaoRescisao) {
        String hql = " from MotivoExoneracaoRescisao m where m.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", motivoExoneracaoRescisao.getCodigo());

        List<TipoAdmissaoFGTS> lista = new ArrayList<TipoAdmissaoFGTS>();
        lista = q.getResultList();

        if (lista.contains(motivoExoneracaoRescisao)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public MotivoExoneracaoRescisao recuperaMotivoExoneracaoRescisaoPorCodigo(int codigo) {
        Query q = getEntityManager().createQuery("from MotivoExoneracaoRescisao m where m.codigo = :parametroCodigo");
        q.setParameter("parametroCodigo", codigo);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (MotivoExoneracaoRescisao) q.getSingleResult();
    }

    public List<MotivoExoneracaoRescisao> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createQuery(" from MotivoExoneracaoRescisao m "
                + " where lower(cast(m.codigo as string)) like :parametro "
                + " or lower(m.descricao) like :parametro ");
        q.setParameter("parametro", "%" + s.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }
}
