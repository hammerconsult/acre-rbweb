/*
 * Codigo gerado automaticamente em Fri Mar 04 11:02:44 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Assunto;
import br.com.webpublico.entidades.SubAssunto;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;

@Stateless
public class SubAssuntoFacade extends AbstractFacade<SubAssunto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private AssuntoFacade assuntoFacade;
    @EJB
    private DocumentoFacade documentoFacade;
    @EJB
    private SingletonHierarquiaOrganizacional singletonHO;
    @EJB
    private SistemaFacade sistemaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubAssuntoFacade() {
        super(SubAssunto.class);
    }

    public List<SubAssunto> listaSubAssuntoPorAssunto(Assunto assun) {
        List<SubAssunto> listaCombo = new ArrayList<SubAssunto>();
        String hql = "select sb from SubAssunto sb where sb.assunto=:paramAssunto";
        Query q;
        if (assun != null) {
            q = em.createQuery(hql);
            q.setParameter("paramAssunto", assun);
            listaCombo = q.getResultList();
            //System.out.println(hql);
            //System.out.println(assun + "----- 3");

        }
        //System.out.println(assun + "-----");

        return listaCombo;
    }

    @Override
    public SubAssunto recuperar(Object id) {
        SubAssunto sub = em.find(SubAssunto.class, id);
        sub.getItensRota().size();
        sub.getSubAssuntoDocumentos().size();
        return sub;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public AssuntoFacade getAssuntoFacade() {
        return assuntoFacade;
    }

    public DocumentoFacade getDocumentoFacade() {
        return documentoFacade;
    }

    public SingletonHierarquiaOrganizacional getSingletonHO() {
        return singletonHO;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }


}
