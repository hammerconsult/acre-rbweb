package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class MaterialPermanenteFacade extends AbstractFacade<MaterialPermanente> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MaterialPermanenteFacade() {
        super(MaterialPermanente.class);
    }

    public MaterialPermanente buscaMaterialPermanentePorCodigo(String codigo) {
        String hql = "from MaterialPermanente m where m.codigo = :filtro";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", codigo);
        try {
            return (MaterialPermanente) q.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<MaterialPermanente> recuperarMateriaisPermanentesPorObjetoDeCompraDescricaoMarcaModelo(ObjetoCompra objetoCompraFiltro, String parte) {
        String hql = " from MaterialPermanente m "
                + "   where m.objetoCompra = :objeto"
                + "     and (lower(m.objetoCompra.descricao) like :filtro"
                + "          or lower(m.descricao) like :filtro"
                + "          or lower(m.marca.descricao) like :filtro"
                + "          or lower(m.modelo.descricao) like :filtro)";

        Query q = em.createQuery(hql);
        q.setParameter("objeto", objetoCompraFiltro);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public GrupoBem recuperarAssociacaoComGrupoBem(GrupoObjetoCompra grupoObjetoCompra) throws ExcecaoNegocioGenerica{
        try {
            String sql = " Select gb.* " +
                    "     from GRUPOOBJCOMPRAGRUPOBEM associacao" +
                    "     inner join grupobem gb on gb.id = associacao.grupoBem_id" +
                    "     where associacao.grupoObjetoCompra_id = :id_grupoObjetoCompra ";
            Query q = em.createNativeQuery(sql, GrupoBem.class);
            q.setParameter("id_grupoObjetoCompra", grupoObjetoCompra.getId());
            return (GrupoBem) q.getSingleResult();
        } catch (NoResultException ex) {
            throw  new ExcecaoNegocioGenerica("Não foi encontrada nenhuma associação do grupo objeto de compra "+grupoObjetoCompra.toString()+" com grupo bem.");
        }
    }
}
