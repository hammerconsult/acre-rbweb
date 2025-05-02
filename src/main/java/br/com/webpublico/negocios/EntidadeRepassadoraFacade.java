/*
 * Codigo gerado automaticamente em Wed Apr 04 15:31:02 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EntidadeRepassadora;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class EntidadeRepassadoraFacade extends AbstractFacade<EntidadeRepassadora> {

    @EJB
    private PessoaFacade pessoaJuridicaFacade;
    @EJB
    private EsferaGovernoFacade esferaGovernoFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EntidadeRepassadoraFacade() {
        super(EntidadeRepassadora.class);
    }

    public boolean validaMesmaPessoaParaEntidadeRepassadora(EntidadeRepassadora selecionado) {
        String sql = " SELECT * FROM ENTIDADEREPASSADORA ER "
                + " WHERE ER.PESSOAJURIDICA_ID = :pessoa_id ";
        if (selecionado.getId() != null) {
            sql += " and er.id <> :idEntidadeRepassadora";
        }
        Query consulta = getEntityManager().createNativeQuery(sql);
        consulta.setParameter("pessoa_id", selecionado.getPessoaJuridica().getId());
        if (selecionado.getId() != null) {
            consulta.setParameter("idEntidadeRepassadora", selecionado.getId());
        }
        if (!consulta.getResultList().isEmpty()) {
            return false;
        }
        return true;
    }

    public PessoaFacade getPessoaJuridicaFacade() {
        return pessoaJuridicaFacade;
    }

    public EsferaGovernoFacade getEsferaGovernoFacade() {
        return esferaGovernoFacade;
    }

    public List<EntidadeRepassadora> completaEntidadeRepassadora(String parte) {
        String sql = "select ent.* from entidaderepassadora ent " +
                "inner join pessoajuridica pj on ent.pessoajuridica_id = pj.id " +
                "where lower(pj.razaosocial) like :parte or replace(pj.cnpj,'.','') like :parte";
        Query consulta = em.createNativeQuery(sql, EntidadeRepassadora.class);
        consulta.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        consulta.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return consulta.getResultList();
    }
}
