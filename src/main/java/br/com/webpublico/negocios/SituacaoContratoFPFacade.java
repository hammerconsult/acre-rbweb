/*
 * Codigo gerado automaticamente em Mon Nov 14 16:00:21 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SituacaoContratoFP;
import br.com.webpublico.entidades.SituacaoFuncional;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class SituacaoContratoFPFacade extends AbstractFacade<SituacaoContratoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;

    public SituacaoContratoFPFacade() {
        super(SituacaoContratoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(SituacaoContratoFP entity) {
        entity = getSituacaoContratoFPComHistorico(entity);
        super.salvar(entity);
    }

    public SituacaoContratoFP getSituacaoContratoFPComHistorico(SituacaoContratoFP entity) {
        SituacaoContratoFP situacaoContratoFPAtualmentePersistido = getSituacaoContratoFPAtualmentePersistido(entity);
        entity.criarOrAtualizarAndAssociarHistorico(situacaoContratoFPAtualmentePersistido);
        return entity;
    }

    public SituacaoContratoFP getSituacaoContratoFPAtualmentePersistido(SituacaoContratoFP entity) {
        if (entity.getId() != null) {
            return recuperar(entity.getId());
        }
        return entity;
    }

    public SituacaoContratoFP recuperarSituacaoFuncionalVigenteEm(ContratoFP c, Date dataOperacao) {
        Query q = em.createQuery(" from SituacaoContratoFP situacaoContrato "
            + " where situacaoContrato.contratoFP = :contratoFP  "
            + "   and :dataOperacao between situacaoContrato.inicioVigencia and coalesce(situacaoContrato.finalVigencia, :dataOperacao)");
        q.setMaxResults(1);
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("contratoFP", c);
        try {
            return (SituacaoContratoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public SituacaoContratoFP recuperaSituacaoContratoFPVigente(ContratoFP contratoFP) {
        Query q = em.createQuery(" select situacaoContratoFP from SituacaoContratoFP situacaoContratoFP "
            + " where situacaoContratoFP.contratoFP = :contratoFP "
            + " and :dataVigencia >= situacaoContratoFP.inicioVigencia and "
            + " :dataVigencia <= coalesce(situacaoContratoFP.finalVigencia,:dataVigencia) ");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (SituacaoContratoFP) q.getSingleResult();
        }
    }

    public List<SituacaoContratoFP> recuperarSituacoesContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery(" select situacaoContratoFP from SituacaoContratoFP situacaoContratoFP "
            + " where situacaoContratoFP.contratoFP = :contratoFP ");
        q.setParameter("contratoFP", contratoFP);
        List<SituacaoContratoFP> toReturn = new ArrayList<>();
        if (!q.getResultList().isEmpty()) {
            toReturn = (List<SituacaoContratoFP>) q.getResultList();
        }
        return toReturn;
    }

    public SituacaoContratoFP ultimaSituacaoContratoFP(ContratoFP contratoFP) {
        String hql = "select situacaoContratoFP from SituacaoContratoFP situacaoContratoFP " +
            " where situacaoContratoFP.contratoFP = :contratoFP " +
            " order by situacaoContratoFP.inicioVigencia desc";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);

        List<SituacaoContratoFP> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return new SituacaoContratoFP();
    }

    public void reverterSituacaoFuncional(ContratoFP contratoFP) {
        //exclui a situacao do contratofp e volta a situacão anterior para vigente
        SituacaoContratoFP situacao = ultimaSituacaoContratoFP(contratoFP);
        remover(situacao);
        situacao = ultimaSituacaoContratoFP(contratoFP);
        situacao.setFinalVigencia(null);
        salvar(situacao);
    }

    public SituacaoContratoFP buscarSituacaoContratoFPPorContratoFPAndCodigoSituacaoFuncional(ContratoFP contratoFP, Long codigoSituacaoFuncional) {
        try {
            String sql = "select s.* from situacaocontratofp s inner join situacaofuncional sf on sf.id = s.situacaofuncional_id where s.contratofp_id = :idContrato and sf.codigo = :cod order by s.INICIOVIGENCIA desc";
            Query q = em.createNativeQuery(sql, SituacaoContratoFP.class);
            q.setParameter("idContrato", contratoFP.getId());
            q.setParameter("cod", codigoSituacaoFuncional);
            q.setMaxResults(1);
            return (SituacaoContratoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public SituacaoContratoFP adicionarSituacaoFuncionalParaContrato(Date inicioVigencia, Date finalVigencia, ContratoFP contratoFP, Long codigoSituacaoFuncional) {
        SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
        situacaoContratoFP.setInicioVigencia(inicioVigencia);
        situacaoContratoFP.setFimVigencia(finalVigencia);
        situacaoContratoFP.setContratoFP(contratoFP);
        situacaoContratoFP.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(codigoSituacaoFuncional));
        em.persist(situacaoContratoFP);
        contratoFP.getSituacaoFuncionals().add(situacaoContratoFP);
        return situacaoContratoFP;
    }

    public SituacaoFuncional buscarSituacaoFuncionalPorCodigo(Integer codigoSituacaoFuncional) {
        try {

            String sql = "select s.* from situacaofuncional s " +
                " where s.codigo = :cod";
            Query q = em.createNativeQuery(sql, SituacaoFuncional.class);
            q.setParameter("cod", codigoSituacaoFuncional);
            q.setMaxResults(1);
            return (SituacaoFuncional) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
