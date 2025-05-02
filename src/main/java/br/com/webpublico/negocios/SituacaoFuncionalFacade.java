/*
 * Codigo gerado automaticamente em Mon Nov 14 16:00:21 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.SituacaoContratoFP;
import br.com.webpublico.entidades.SituacaoFuncional;
import br.com.webpublico.entidades.VinculoFP;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class SituacaoFuncionalFacade extends AbstractFacade<SituacaoFuncional> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public SituacaoFuncionalFacade() {
        super(SituacaoFuncional.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    //    @Override
//    public SituacaoFuncional recuperar(Object id) {
//        SituacaoFuncional sit = em.find(SituacaoFuncional.class, id);
//        sit.getContratoFPs().size();
//        return sit;
//    }
    public SituacaoContratoFP recuperarSituacao(Object id) {
        SituacaoContratoFP sit = em.find(SituacaoContratoFP.class, id);

        return sit;
    }

    public SituacaoFuncional recuperaCodigo(Long id) {
        Query createQuery = em.createQuery("from SituacaoFuncional s where s.codigo = :codigo");
        createQuery.setParameter("codigo", id);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return (SituacaoFuncional) createQuery.getSingleResult();
    }

    public List<SituacaoContratoFP> recuperaSituacaoFuncionalProvimento(ContratoFP contratoFP, Date dataProvimento) {
        Query q = em.createQuery(" from SituacaoContratoFP situacaoContrato "
            + " where situacaoContrato.contratoFP = :contratoFP  "
            + " and :dataProvimento >= situacaoContrato.inicioVigencia");
        q.setMaxResults(1);
        q.setParameter("dataProvimento", dataProvimento);
        q.setParameter("contratoFP", contratoFP);
        return q.getResultList();
    }

    public SituacaoContratoFP recuperaSituacaoFuncionalVigente(VinculoFP contratoFP) {
        Query q = em.createQuery(" from SituacaoContratoFP situacaoContrato "
            + " where situacaoContrato.contratoFP = :contratoFP  "
            + " and :dataProvimento >= situacaoContrato.inicioVigencia "
            + " and :dataProvimento <= coalesce(situacaoContrato.finalVigencia, :dataProvimento) ");
        q.setMaxResults(1);
        q.setParameter("dataProvimento", new Date());
        q.setParameter("contratoFP", contratoFP);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (SituacaoContratoFP) q.getResultList().get(0);
    }

    public SituacaoContratoFP recuperaUltimaSituacaoFuncional(VinculoFP contratoFP) {
        Query q = em.createQuery(" from SituacaoContratoFP situacaoContrato "
            + " where situacaoContrato.contratoFP = :contratoFP  "
            + " and situacaoContrato.inicioVigencia = (select max(s.inicioVigencia) from SituacaoContratoFP  s where s.contratoFP = :contratoFP)");
        q.setMaxResults(1);
        q.setParameter("contratoFP", contratoFP);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (SituacaoContratoFP) q.getResultList().get(0);
    }

    public List<SituacaoFuncional> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createQuery(" from SituacaoFuncional s "
            + " where cast(s.codigo as string) like :filtro "
            + " or lower(s.descricao) like :filtro order by s.codigo");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<SituacaoFuncional> buscarSituacaoFuncional() {
        Query q = em.createQuery(" from SituacaoFuncional s order by s.codigo ");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<SituacaoFuncional> buscarSituacaoFuncionalExcluindoCodigos(String s, Long... codigos) {
        Query q = em.createQuery(" from SituacaoFuncional s "
            + " where s.codigo not in(:codigos) and (cast(s.codigo as string) like :filtro "
            + " or lower(s.descricao) like :filtro) order by s.codigo");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setParameter("codigos", Arrays.asList(codigos));
        q.setMaxResults(50);
        return q.getResultList();
    }

    public boolean isUltimaSituacaoFuncionalContrato(Long idContratoFP, Long codigoSituacaoFuncional) {
        String sql = "     select sf.codigo" +
            "       from situacaocontratofp sc" +
            " inner join situacaofuncional  sf on sf.id = sc.situacaofuncional_id" +
            "      where contratofp_id = :idContratoFP" +
            "   order by iniciovigencia desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContratoFP", idContratoFP);
        q.setMaxResults(1);
        try {
            return Long.parseLong("" + q.getSingleResult()) == codigoSituacaoFuncional;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public SituacaoContratoFP buscarSituacaoFuncionalPorServidorVigenteEm(ContratoFP cfp, Date dataVigenciaSituacaoFuncional) {
        String hql = "select sf from SituacaoContratoFP sf where " +
            " :dataEmQuestao between sf.inicioVigencia and coalesce(sf.finalVigencia, :dataEmQuestao) and" +
            " sf.contratoFP = :contratoFP";
        Query q = em.createQuery(hql);
        q.setParameter("dataEmQuestao", dataVigenciaSituacaoFuncional);
        q.setParameter("contratoFP", cfp);
        q.setMaxResults(1);
        try {
            return (SituacaoContratoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public SituacaoContratoFP buscarSituacaoFuncionalExataPorServidorVigenteEm(ContratoFP cfp, Date dataVigenciaSituacaoFuncional) {
        String hql = "select sf from SituacaoContratoFP sf where " +
            " sf.inicioVigencia = :dataEmQuestao " +
            " and sf.contratoFP = :contratoFP ";
        Query q = em.createQuery(hql);
        q.setParameter("dataEmQuestao", dataVigenciaSituacaoFuncional);
        q.setParameter("contratoFP", cfp);
        q.setMaxResults(1);
        try {
            return (SituacaoContratoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public SituacaoContratoFP buscarSituacaoFuncionalPorServidorAndLimite(ContratoFP cfp, Date dataVigencia, AntesDepoisParametroFerias parametro) {
        String hql = "select sf from SituacaoContratoFP sf where " +
            " sf.inicioVigencia " + parametro.getSinal() + " :dataEmQuestao and sf.contratoFP = :contratoFP order by sf.inicioVigencia " + parametro.getOrderBy();
        Query q = em.createQuery(hql);
        q.setParameter("dataEmQuestao", dataVigencia);
        q.setParameter("contratoFP", cfp);
        q.setMaxResults(1);
        try {
            return (SituacaoContratoFP) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public enum AntesDepoisParametroFerias {
        ANTES("<", "desc"),
        DEPOIS(">", "asc");
        private String sinal;
        private String orderBy;

        AntesDepoisParametroFerias(String sinal, String orderBy) {
            this.sinal = sinal;
            this.orderBy = orderBy;
        }

        public String getSinal() {
            return sinal;
        }

        public void setSinal(String sinal) {
            this.sinal = sinal;
        }

        public String getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(String orderBy) {
            this.orderBy = orderBy;
        }
    }

}
