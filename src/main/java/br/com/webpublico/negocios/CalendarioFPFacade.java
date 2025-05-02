/*
 * Codigo gerado automaticamente em Fri Jan 06 09:41:47 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CalendarioFP;
import br.com.webpublico.entidades.CompetenciaFP;
import br.com.webpublico.entidades.ItemCalendarioFP;
import br.com.webpublico.entidadesauxiliares.rh.portal.ItemCalendarioFPDTO;
import br.com.webpublico.enums.MesCalendarioPagamento;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.interfaces.FolhaCalculavel;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Stateless
public class CalendarioFPFacade extends AbstractFacade<CalendarioFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CreditoSalarioFacade creditoSalarioFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CalendarioFPFacade() {
        super(CalendarioFP.class);
    }

    @Override
    public CalendarioFP recuperar(Object id) {
        CalendarioFP c = em.find(CalendarioFP.class, id);
        c.getItemCalendarioFPs().size();
        return c;
    }

    public CalendarioFP recuperarByAno(Integer ano) {
        CalendarioFP c = recuperarCalendarioFP(ano);
        if (c != null) {
            c.getItemCalendarioFPs().size();
        }
        return c;
    }

    public CalendarioFP recuperarCalendarioFP(Integer ano) {
        try {
            Query q = em.createQuery(" from CalendarioFP calendario where calendario.ano = :parametro ");
            q.setParameter("parametro", ano);
            q.setMaxResults(1);
            return (CalendarioFP) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ItemCalendarioFPDTO> buscarCalendarioDTO(Integer ano) {
        List<ItemCalendarioFPDTO> retorno = Lists.newLinkedList();
        Query q = em.createNativeQuery("select calendario.* from CalendarioFP calendario where calendario.ano = :parametro ", CalendarioFP.class);
        q.setParameter("parametro", ano);
        for (CalendarioFP calendarioFP : (List<CalendarioFP>) q.getResultList()) {
            List<ItemCalendarioFP> itemCalendarioFPs = calendarioFP.getItemCalendarioFPs();
            ordernarCalendarioPorMes(itemCalendarioFPs);
            for (ItemCalendarioFP item : itemCalendarioFPs) {
                MesCalendarioPagamento mesCalendarioPagamento = item.getMesCalendarioPagamento();
                if (MesCalendarioPagamento.DECIMO.equals(mesCalendarioPagamento)) {
                    mesCalendarioPagamento = MesCalendarioPagamento.DEZEMBRO;
                }
                Date date = creditoSalarioFacade.buscarDataPagamentoAtravesDoCreditoSalario(mesCalendarioPagamento.getNumeroDoMes(), item.getCalendarioFP().getAno(), TipoFolhaDePagamento.NORMAL);
                retorno.add(new ItemCalendarioFPDTO(mesCalendarioPagamento,
                    item.getDiaEntregaDocumentos(), item.getDiaCorteConsignacoes(), item.getUltimoDiaProcessamento(), item.getDiaPagamento(), date != null, MesCalendarioPagamento.DECIMO.equals(item.getMesCalendarioPagamento())));
            }

        }
        return retorno;
    }

    private void ordernarCalendarioPorMes(List<ItemCalendarioFP> itemCalendarioFPs) {
        Collections.sort(itemCalendarioFPs, new Comparator<ItemCalendarioFP>() {
            @Override
            public int compare(ItemCalendarioFP o1, ItemCalendarioFP o2) {
                return o1.getMesCalendarioPagamento().getNumeroDoMes().compareTo(o2.getMesCalendarioPagamento().getNumeroDoMes());
            }
        });
    }

    public ItemCalendarioFP buscarDataCalculoCalendarioFPPorFolha(FolhaCalculavel fp) {
        if (fp != null) {
            if (fp.getCompetenciaFP() != null) {
                return buscarDataCalculoCalendarioFPPorCompetenciaFP(fp.getCompetenciaFP(), false);
            }
        }
        return null;
    }

    public ItemCalendarioFP buscarDataCalculoCalendarioFPPorCompetenciaFP(CompetenciaFP cp, boolean permiteRetornarNulo) {
        if (cp != null) {
            if (cp.getTipoFolhaDePagamento() != null && cp.getMes() != null && cp.getExercicio().getAno() != null) {
                return buscarDataCalculoCalendarioPorTipo(cp.getExercicio().getAno(), cp.getTipoFolhaDePagamento() == TipoFolhaDePagamento.SALARIO_13 ? MesCalendarioPagamento.DECIMO : MesCalendarioPagamento.getMesToInt(cp.getMes().getNumeroMes()), permiteRetornarNulo);
            }
        }
        return null;
    }

    private ItemCalendarioFP buscarDataCalculoCalendarioPorTipo(Integer ano, MesCalendarioPagamento mes, boolean permiteRetornarNulo) {
        Query q = em.createQuery("select item from CalendarioFP calendario" +
            " inner join calendario.itemCalendarioFPs item" +
            "      where calendario.ano = :ano" +
            "        and item.mesCalendarioPagamento = :mes ");


        q.setParameter("ano", ano);
        q.setParameter("mes", MesCalendarioPagamento.getMesToInt(mes.getNumeroDoMes()));
        q.setMaxResults(1);
        try {
            if (permiteRetornarNulo) {
                List resultList = q.getResultList();
                if (resultList != null && !resultList.isEmpty()) {
                    return (ItemCalendarioFP) resultList.get(0);
                }
                return null;
            }
            return (ItemCalendarioFP) q.getSingleResult();
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao tentar recuperar a data de Calendário!", e);
        }
    }

}
