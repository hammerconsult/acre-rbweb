/*
 * Codigo gerado automaticamente em Tue Feb 07 11:11:42 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.VinculoDeContratoFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class VinculoDeContratoFPFacade extends AbstractFacade<VinculoDeContratoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public VinculoDeContratoFPFacade() {
        super(VinculoDeContratoFP.class);
    }

    public boolean existeCodigo(VinculoDeContratoFP vinculoDeContratoFP) {
        String hql = " from VinculoDeContratoFP tipo where tipo.codigo = :parametroCodigo ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("parametroCodigo", vinculoDeContratoFP.getCodigo());
        if (((List<VinculoDeContratoFP>) q.getResultList()).contains(vinculoDeContratoFP)) {
            return false;
        } else {
            return (!q.getResultList().isEmpty());
        }
    }

    public List<VinculoDeContratoFP> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createQuery(" from VinculoDeContratoFP v "
                + " where lower(cast(v.codigo as string)) like :filtro "
                + " or lower(v.descricao) like :filtro ");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public VinculoDeContratoFP recuperaVinculoDeContratoVigente(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select vinc from ContratoVinculoDeContrato contratovinc");
        hql.append(" inner join contratovinc.vinculoDeContratoFP vinc");
        hql.append(" where contratovinc.contratoFP = :contrato");
        hql.append(" and contratovinc.inicioVigencia >= :data");
        hql.append(" and coalesce(to_char(contratovinc.finalVigencia, 'dd/MM/yyyy'), :data) <= :data");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("data", dataProvimento);

        List<VinculoDeContratoFP> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new VinculoDeContratoFP();
        }
        return lista.get(0);
    }
}
