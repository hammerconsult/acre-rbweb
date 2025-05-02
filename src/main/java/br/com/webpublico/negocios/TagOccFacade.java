/*
 * Codigo gerado automaticamente em Wed Jun 20 15:59:49 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.enums.EntidadeOCC;
import br.com.webpublico.entidades.TagOCC;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class TagOccFacade extends AbstractFacade<TagOCC> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TagOccFacade() {
        super(TagOCC.class);
    }

    public String retornaUltimoNumeroTag() {
        String sql = "SELECT TAG.CODIGO FROM TAGOCC TAG ORDER BY TO_NUMBER(TAG.CODIGO) DESC";
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return "0";
        } else {
            String retorno = (String) q.getSingleResult();
            if (retorno == null) {
                return "0";
            } else {
                return retorno;
            }
        }
    }

    public List<TagOCC> listaPorEntidadeOCC(String parte, EntidadeOCC ent) {
        List<TagOCC> tags = new ArrayList<TagOCC>();
        String sql = "SELECT tag.* FROM tagocc tag WHERE tag.entidadeocc = :ent AND (tag.codigo LIKE :parte OR lower(tag.descricao) LIKE :parte)";
        Query q = getEntityManager().createNativeQuery(sql, TagOCC.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("ent", ent.toString());
        tags = q.getResultList();
        return tags;
    }
}
