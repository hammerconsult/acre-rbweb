/*
 * Codigo gerado automaticamente em Wed Apr 04 11:08:47 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MovimentoSEFIP;
import br.com.webpublico.enums.TipoMovimentoSEFIP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class MovimentoSEFIPFacade extends AbstractFacade<MovimentoSEFIP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MovimentoSEFIPFacade() {
        super(MovimentoSEFIP.class);
    }

    public List<MovimentoSEFIP> listaMovimentosSEFIP(TipoMovimentoSEFIP tipo) {
        Query q = em.createQuery("from MovimentoSEFIP movimento where movimento.tipoMovimentoSEFIP = :tipo");
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<MovimentoSEFIP> completaMovimentoSEFIP(TipoMovimentoSEFIP tipo, String parte){
        Query q = em.createQuery("select m from MovimentoSEFIP m" +
                                "  where m.tipoMovimentoSEFIP = :tipo" +
                                "    and (lower(m.descricao) like :parte or lower(to_char(m.codigo)) like :parte)");
        q.setParameter("tipo", tipo);
        q.setParameter("parte", "%"+parte.toLowerCase()+"%");
        q.setMaxResults(10);
        return q.getResultList();
    }
}
