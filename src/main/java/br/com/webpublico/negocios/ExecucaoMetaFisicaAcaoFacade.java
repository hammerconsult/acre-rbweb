/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ExecucaoMetaFisicaAcao;
import br.com.webpublico.entidades.SubAcaoPPA;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Edi
 */
@Stateless
public class ExecucaoMetaFisicaAcaoFacade extends AbstractFacade<ExecucaoMetaFisicaAcao> {

    @EJB
    private LDOFacade lDOFacade;
    @EJB
    private ProdutoPPAFacade subAcaoPPAFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExecucaoMetaFisicaAcaoFacade() {
        super(ExecucaoMetaFisicaAcao.class);
    }

    public List<SubAcaoPPA> listaFiltrandoSubacao(String parte) {
        String sql = " SELECT SUB.* FROM "
                + " SUBACAOPPA SUB "
                + " WHERE ((SUB.CODIGO LIKE :parte) OR (LOWER(SUB.DESCRICAO) LIKE :parte))"
                + " ORDER BY SUB.CODIGO ";
        Query consulta = em.createNativeQuery(sql, SubAcaoPPA.class);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setMaxResults(10);
        return consulta.getResultList();
    }

    public LDOFacade getlDOFacade() {
        return lDOFacade;
    }

    public void setlDOFacade(LDOFacade lDOFacade) {
        this.lDOFacade = lDOFacade;
    }

    public ProdutoPPAFacade getSubAcaoPPAFacade() {
        return subAcaoPPAFacade;
    }

    public void setSubAcaoPPAFacade(ProdutoPPAFacade subAcaoPPAFacade) {
        this.subAcaoPPAFacade = subAcaoPPAFacade;
    }
}
