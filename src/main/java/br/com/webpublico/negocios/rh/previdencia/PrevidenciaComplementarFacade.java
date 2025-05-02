package br.com.webpublico.negocios.rh.previdencia;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.previdencia.ItemPrevidenciaComplementar;
import br.com.webpublico.entidades.rh.previdencia.PrevidenciaComplementar;
import br.com.webpublico.negocios.*;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Date;

@Stateless
public class PrevidenciaComplementarFacade extends AbstractFacade<PrevidenciaComplementar> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    public PrevidenciaComplementarFacade() {
        super(PrevidenciaComplementar.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @Override
    public PrevidenciaComplementar recuperar(Object id) {
        PrevidenciaComplementar p = em.find(PrevidenciaComplementar.class, id);
        Hibernate.initialize(p.getItens());
        return p;
    }

    public PrevidenciaComplementar buscarPrevidenciaComplementarPorContrato(ContratoFP contratoFP) {
        String hql = " from PrevidenciaComplementar prev " +
            " where prev.contratoFP = :contratoFP ";
        Query q = em.createQuery(hql);
        q.setParameter("contratoFP", contratoFP);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (PrevidenciaComplementar) resultList.get(0);
        }
        return null;
    }

    public List<ItemPrevidenciaComplementar> buscarItemPrevidenciaComplementarPorContrato(ContratoFP contratoFP, Date dataOperacao) {
        String sql = " select item.* from ITEMPREVCOMPLEMENTAR item " +
                "                     inner join PREVIDENCIACOMPLEMENTAR prev on item.PREVIDENCIACOMPLEMENTAR_ID = prev.ID " +
                " where prev.CONTRATOFP_ID = :contratoFP " +
                "  and to_date(to_char(:dataOperacao,'mm/yyyy'),'mm/yyyy') between to_date(to_char(item.inicioVigencia,'mm/yyyy'),'mm/yyyy')  and to_date(to_char(coalesce(item.finalVigencia, :dataOperacao),'mm/yyyy'),'mm/yyyy') " +
                " order by item.INICIOVIGENCIA desc ";
        Query q = em.createNativeQuery(sql, ItemPrevidenciaComplementar.class);
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataOperacao", dataOperacao);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (List<ItemPrevidenciaComplementar>) resultList;
        }
        return null;
    }

    public ItemPrevidenciaComplementar buscarItemPrevidenciaComplementarPorContratoSemFinalVigencia(ContratoFP contratoFP, Date dataOperacao) {
        String sql = " select item.* from itemprevcomplementar item  " +
            "                     inner join previdenciacomplementar prev on item.previdenciacomplementar_id = prev.id  " +
            " where prev.contratofp_id = :contratoFP  " +
            "  and trunc(item.iniciovigencia) <= trunc(:dataOperacao) " +
            "  and item.finalvigencia is null " +
            " order by item.iniciovigencia desc";
        Query q = em.createNativeQuery(sql, ItemPrevidenciaComplementar.class);
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataOperacao", dataOperacao);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (ItemPrevidenciaComplementar) resultList.get(0);
        }
        return null;
    }

    public ItemPrevidenciaComplementar buscarItemPrevidenciaComplementarPorContratoComFinalVigenciaIgualDataReferencia(ContratoFP contratoFP, Date dataReferencia) {
        String sql = " select item.* from itemprevcomplementar item  " +
            "                     inner join previdenciacomplementar prev on item.previdenciacomplementar_id = prev.id  " +
            " where prev.contratofp_id = :contratoFP  " +
            "  and trunc(item.finalvigencia) = trunc(:dataReferencia) " +
            " order by item.iniciovigencia desc";
        Query q = em.createNativeQuery(sql, ItemPrevidenciaComplementar.class);
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataReferencia", dataReferencia);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (ItemPrevidenciaComplementar) resultList.get(0);
        }
        return null;
    }
}
