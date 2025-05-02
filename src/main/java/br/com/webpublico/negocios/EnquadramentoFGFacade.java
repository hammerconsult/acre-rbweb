/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.EnquadramentoFG;
import br.com.webpublico.util.Util;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author Usuario
 */
@Stateless
public class EnquadramentoFGFacade extends AbstractFacade<EnquadramentoFG> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnquadramentoFGFacade() {
        super(EnquadramentoFG.class);
    }

    public EnquadramentoFG recuperaFGVigente(ContratoFP contratoFP, Date dataProvimento) {
        //System.out.println("data provimento " + dataProvimento);
        String hql = "select distinct efg from EnquadramentoFG efg "
                + "inner join efg.funcaoGratificada fg "
                + "where fg.contratoFP = :contratoFP "
                + "and fg.inicioVigencia <= :dataVigencia "
                + "and coalesce(fg.finalVigencia, :dataVigencia) >= :dataVigencia";

        String teste2 = "select * from enquadramentofg efg "
                + "inner join funcaogratificada fg on fg.id = efg.funcaogratificada_id "
                + "where fg.contratofp_id = 7694245 "
                + "and fg.iniciovigencia <= '07/12/2012' "
                + "and coalesce(fg.finalvigencia, '07/12/2012') >= '07/12/2012'";

        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataVigencia", dataProvimento);

        new Util().imprimeSQL(hql.toString(), q);

        List<EnquadramentoFG> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new EnquadramentoFG();
        }
        return lista.get(0);
    }

    public EnquadramentoFG recuperaEnquadramentoFGVigente(ContratoFP contratoFP, Date data) {
        String hql = "select distinct efg from EnquadramentoFG efg "
                + "inner join efg.funcaoGratificada fg "
                + "where fg.contratoFP = :contratoFP "
                + "and efg.inicioVigencia <= :dataVigencia "
                + "and coalesce(efg.finalVigencia, :dataVigencia) >= :dataVigencia";

        String teste2 = "select * from enquadramentofg efg "
                + "inner join funcaogratificada fg on fg.id = efg.funcaogratificada_id "
                + "where fg.contratofp_id = 7694245 "
                + "and fg.iniciovigencia <= '07/12/2012' "
                + "and coalesce(fg.finalvigencia, '07/12/2012') >= '07/12/2012'";

        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataVigencia", data);

        new Util().imprimeSQL(hql.toString(), q);

        List<EnquadramentoFG> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new EnquadramentoFG();
        }
        return lista.get(0);
    }

}
