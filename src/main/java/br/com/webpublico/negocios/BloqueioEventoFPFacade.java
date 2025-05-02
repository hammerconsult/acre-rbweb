package br.com.webpublico.negocios;

import br.com.webpublico.entidades.BloqueioEventoFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.*;
import com.google.common.collect.Lists;

@Stateless
public class BloqueioEventoFPFacade extends AbstractFacade<BloqueioEventoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BloqueioEventoFPFacade() {
        super(BloqueioEventoFP.class);
    }

    public boolean isBloqueado(EntidadePagavelRH vinculoFP, EventoFP eventoFP, Date data) {
        Query q = em.createQuery("select bloqueio from BloqueioEventoFP bloqueio where bloqueio.vinculoFP.id = :vinculoID and bloqueio.eventoFP = :evento " +
                "  and :data between bloqueio.dataInicial and coalesce(bloqueio.dataFinal,:data)");
        q.setParameter("vinculoID", vinculoFP.getIdCalculo());
        q.setParameter("evento", eventoFP);
        q.setParameter("data", data, TemporalType.DATE);
        return !q.getResultList().isEmpty();
    }

    public Map<String, List<BloqueioEventoFP>> getVinculosEVerbasBloqueados() {
        Map<String, List<BloqueioEventoFP>> listMap = new LinkedHashMap<>();
        Query query = em.createQuery("from BloqueioEventoFP");
        List<BloqueioEventoFP> locks = query.getResultList();
        for (BloqueioEventoFP lock : locks) {
            String chave = lock.getVinculoFP().getIdCalculo() + "" + lock.getEventoFP().getCodigo();
            if (listMap.containsKey(chave)) {
                listMap.get(chave).add(lock);
            } else {
                List<BloqueioEventoFP> bloqueios = new ArrayList<>();
                bloqueios.add(lock);
                listMap.put(chave, bloqueios);
            }
        }
        return listMap;
    }

    public Map<String, List<BloqueioEventoFP>> getVinculosEVerbasBloqueadosPorVinculo(VinculoFP vinculoFP) {
        Map<String, List<BloqueioEventoFP>> listMap = new LinkedHashMap<>();
        Query q = em.createQuery("select vinculoFP.id, eventoFP.id from BloqueioEventoFP bloqueio where vinculoFP.id = :id group by" +
                " vinculoFP.id, eventoFP.id ");
        q.setParameter("id",vinculoFP.getId());
        List<Long[]> ids = q.getResultList();
        for (Object[] obj : ids) {
            Long idVinculo = (Long) obj[0];
            Long idEvento = (Long) obj[1];
            Query query = em.createQuery("select bloqueio from BloqueioEventoFP  bloqueio where vinculoFP.id = :vinculo and eventoFP.id = :evento ");
            query.setParameter("vinculo", idVinculo);
            query.setParameter("evento", idEvento);
            List<BloqueioEventoFP> locks = query.getResultList();
            if (!locks.isEmpty()) {
                listMap.put(locks.get(0).getVinculoFP().getIdCalculo() + "" + locks.get(0).getEventoFP().getCodigo(), locks);
            }

        }
        return listMap;
    }

    public boolean isBloqueadoNative(EntidadePagavelRH vinculoFP, EventoFP eventoFP, Date data) {
        try {
            Query q = em.createNativeQuery("select bloqueio.id from BloqueioEventoFP bloqueio inner join VinculoFP v on v.id= bloqueio.vinculofp_id " +
                    " inner join eventoFP evento on evento.id= bloqueio.eventofp_id " +
                    " where v.id = :vinculoID and evento.id = :evento " +
                    "  and to_date(:data,'dd/mm/yyyy') between bloqueio.dataInicial and coalesce(bloqueio.dataFinal,to_date(:data,'dd/mm/yyyy'))");
            q.setParameter("vinculoID", vinculoFP.getIdCalculo());
            q.setParameter("evento", eventoFP.getId());
            q.setParameter("data", Util.dateToString(data));
            q.getSingleResult();
            return true;
        } catch (NonUniqueResultException e) {
            return true;
        } catch (NoResultException ex) {
            return false;
        }

    }

    public List<BloqueioEventoFP> buscarBloqueiosEventoFPPorVinculoFP(VinculoFP vinculoFP) {
        String sql = "select bloqueio.* from BloqueioEventoFP bloqueio " +
            " where bloqueio.VINCULOFP_ID = :vinculo";
        Query q = em.createNativeQuery(sql, BloqueioEventoFP.class);
        q.setParameter("vinculo", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }
}
