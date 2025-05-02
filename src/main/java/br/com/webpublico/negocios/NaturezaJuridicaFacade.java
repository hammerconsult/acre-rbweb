/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.NaturezaJuridica;
import br.com.webpublico.enums.SituacaoNaturezaJuridica;
import br.com.webpublico.enums.TipoNaturezaJuridica;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wellington
 */
@Stateless
public class NaturezaJuridicaFacade extends AbstractFacade<NaturezaJuridica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public NaturezaJuridicaFacade() {
        super(NaturezaJuridica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Integer buscarProximoCodigo() {
        Integer codigo = new Integer(1);

        String sql = " select max(codigo) from NaturezaJuridica ";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() != null) {
            codigo = new Integer(((BigDecimal) q.getSingleResult()).intValue() + 1);
        }

        return codigo;
    }

    public NaturezaJuridica buscarNaturezaPorCodigo(Integer codigo) {
        String hql = "select np from NaturezaJuridica np where np.codigo =:codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", codigo);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            return (NaturezaJuridica) q.getSingleResult();
        }
        return null;
    }

    public List<NaturezaJuridica> listaNaturezaJuridicaPorTipo(TipoNaturezaJuridica tipoNaturezaJuridica) {
        List<NaturezaJuridica> retorno = new ArrayList<>();
        String hql = "";
        if (tipoNaturezaJuridica == null) {
            hql = " select np from NaturezaJuridica np where np.situacao = :situacao order by np.codigo ";
        } else {
            hql = " select np from NaturezaJuridica np where np.situacao = :situacao and np.tipoNaturezaJuridica = :tipo order by np.codigo ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("situacao", SituacaoNaturezaJuridica.ATIVO);
        if (tipoNaturezaJuridica != null) {
            q.setParameter("tipo", tipoNaturezaJuridica);
        }
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }

        return retorno;
    }

    public List<NaturezaJuridica> listaNaturezaJuridicaAutonomo() {
        List<NaturezaJuridica> retorno = new ArrayList<>();
        String hql = "select np from NaturezaJuridica np where np.situacao = :situacao and np.autonomo is true order by np.codigo ";
        Query q = em.createQuery(hql);
        q.setParameter("situacao", SituacaoNaturezaJuridica.ATIVO);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = q.getResultList();
        }
        return retorno;
    }

    public List<NaturezaJuridica> buscarNaturezasJuridicasAtivas() {
        String hql = "select np from NaturezaJuridica np where np.situacao = :situacao order by np.descricao ";
        Query q = em.createQuery(hql);
        q.setParameter("situacao", SituacaoNaturezaJuridica.ATIVO);
        List<NaturezaJuridica> retorno = q.getResultList();
        if (!retorno.isEmpty()) {
            return retorno;
        }
        return Lists.newArrayList();
    }

    public boolean existeVinculoComCMC(NaturezaJuridica naturezaJuridica) {
        boolean retorno = false;
        String hql = " select ce from CadastroEconomico ce where ce.naturezaJuridica =:naturezaJuridica ";
        Query q = em.createQuery(hql);
        q.setParameter("naturezaJuridica", naturezaJuridica);
        if (q.getResultList() != null && q.getResultList().size() > 0) {
            retorno = true;
        }

        return retorno;
    }
}
