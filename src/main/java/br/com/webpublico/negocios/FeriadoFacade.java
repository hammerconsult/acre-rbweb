/*
 * Codigo gerado automaticamente em Mon Feb 14 10:35:18 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.Feriado;
import br.com.webpublico.entidades.UnidadeContrato;
import br.com.webpublico.entidadesauxiliares.rh.portal.FeriadoDTO;
import br.com.webpublico.singletons.CacheRH;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Stateless
public class FeriadoFacade extends AbstractFacade<Feriado> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CacheRH cacheRH;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvar(Feriado entity) {
        super.salvar(entity);
        initializeCache();
    }

    @Override
    public void salvarNovo(Feriado entity) {
        super.salvarNovo(entity);
        initializeCache();
    }

    private void initializeCache() {
        cacheRH.setFeriadosPorAno(new HashMap<Integer, List<Feriado>>());
    }

    public FeriadoFacade() {
        super(Feriado.class);
    }

    public List<Feriado> feriadosNoDia(Date data) {
        Query q = em.createQuery("From Feriado f where trunc(f.dataFeriado) = :data");
        q.setParameter("data", DataUtil.dataSemHorario(data));
        return q.getResultList();
    }

    public List<Feriado> buscarFeriadosPorAno(Integer ano) {
        Query q = em.createQuery("from Feriado f where extract(year from f.dataFeriado) = :ano order by f.dataFeriado");
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    public List<FeriadoDTO> buscarFeriadosPorAnoPortal(Integer ano) {
        if (!cacheRH.getFeriadosPorAno().containsKey(ano)) {
            List<Feriado> feriados = buscarFeriadosPorAno(ano);
            cacheRH.getFeriadosPorAno().put(ano, feriados);
        }
        List<FeriadoDTO> feriados = Lists.newLinkedList();
        for (Feriado feriado : cacheRH.getFeriadosPorAno().get(ano)) {
            feriados.add(new FeriadoDTO(feriado.getDescricao(), feriado.getDataFeriado()));
        }
        return feriados;
    }

    public boolean isFeriado(Date data) {
        Query query = em.createNativeQuery("select * from feriado where trunc(dataferiado) = :data ")
            .setParameter("data", DateUtils.dataSemHorario(data))
            .setMaxResults(1);
        List resultList = query.getResultList();
        return resultList != null && !resultList.isEmpty();
    }

    public Date proximoDiaUtil(Date data) {
        if (DateUtils.isSabadoOrDomingo(data) || isFeriado(data)) {
            return proximoDiaUtil(DateUtils.adicionarDias(data, 1));
        }
        return data;
    }

    public Feriado buscarFeriadoPorAno(Integer ano, Date dataOperacao) {
        String sql = " select f.* from feriado f " +
            "          where extract(year from f.dataFeriado) = :ano " +
            "           and trunc(f.dataFeriado) = to_date(:dataOperacao, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql, Feriado.class);
        q.setParameter("ano", ano);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        try {
            return (Feriado) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException ex) {
            throw new ExcecaoNegocioGenerica("Retornou mais de um feriado para a data: " + DataUtil.getDataFormatada(dataOperacao) + ".");
        }
    }
}
