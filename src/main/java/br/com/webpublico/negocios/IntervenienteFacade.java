/*
 * Codigo gerado automaticamente em Thu Apr 05 14:41:35 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Interveniente;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless

public class IntervenienteFacade extends AbstractFacade<Interveniente> {

    @EJB
    private TipoIntervenienteFacade tipoIntervenienteFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public IntervenienteFacade() {
        super(Interveniente.class);
    }

    public List<Interveniente> listaPorPessoa(String parte) {
        String sql = "SELECT i.* FROM interveniente i "
                + "INNER JOIN pessoafisica pf ON pf.id = i.pessoa_id "
                + "AND ((lower(pf.nome) LIKE :parte) OR (pf.cpf LIKE :parte)) "
                + "UNION ALL "
                + "SELECT i.* FROM interveniente i "
                + "INNER JOIN pessoajuridica pj ON pj.id = i.pessoa_id "
                + "AND ((lower(pj.nomefantasia) LIKE :parte) OR (lower(pj.nomereduzido) LIKE :parte) OR (pj.cnpj LIKE :parte))";
        Query q = em.createNativeQuery(sql, Interveniente.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        List<Interveniente> intervenientes = q.getResultList();
        if (intervenientes.isEmpty()) {
            return new ArrayList<Interveniente>();
        } else {
            return intervenientes;
        }
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public TipoIntervenienteFacade getTipoIntervenienteFacade() {
        return tipoIntervenienteFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }
}
