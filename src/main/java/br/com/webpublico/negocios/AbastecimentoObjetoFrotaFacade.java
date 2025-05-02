/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class AbastecimentoObjetoFrotaFacade extends AbstractFacade<AbastecimentoObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CotaAbastecimentoFacade cotaAbastecimentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private VeiculoFacade veiculoFacade;
    @EJB
    private EquipamentoFacade equipamentoFacade;

    public AbastecimentoObjetoFrotaFacade() {
        super(AbastecimentoObjetoFrota.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public AbastecimentoObjetoFrota recuperar(Object id) {
        AbastecimentoObjetoFrota abastecimentoVeiculo = super.recuperar(id);
        abastecimentoVeiculo.setCotaAbastecimento(cotaAbastecimentoFacade.recuperar(abastecimentoVeiculo.getCotaAbastecimento().getId()));
        abastecimentoVeiculo.getItensAbastecimentoObjetoFrota().size();
        if (abastecimentoVeiculo.getDetentorArquivoComposicao() != null) {
            abastecimentoVeiculo.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return abastecimentoVeiculo;
    }


    public List<AbastecimentoObjetoFrota> listarAbastecimento(Veiculo veiculo, String parte) {
        if (veiculo == null || veiculo.getId() == null) {
            return new ArrayList();
        }
        String sql = " select * from abastecimentoObjetoFrota abastecimento " +
            " where abastecimento.objetofrota_id = " + veiculo.getId() +
            "   and to_char(abastecimento.id) like '%" + parte.trim() + "%' ";
        Query q = em.createNativeQuery(sql, AbastecimentoObjetoFrota.class);
        return q.getResultList();
    }

    public BigDecimal quantidadeItemCotaEmOutrosAbastecimentos(AbastecimentoObjetoFrota abastecimentoObjetoFrota,
                                                               ItemCotaAbastecimento itemCotaAbastecimento) {
        String sql = " select  sum(itemAbastec.quantidade) quantidade\n" +
            "  from itemAbastecObjetoFrota itemAbastec\n" +
            "where itemAbastec.itemcotaabastecimento_id = :itemCotaAbastecimento_id";
        if (abastecimentoObjetoFrota.getId() != null) {
            sql += " and itemAbastec.abastecimentoObjetoFrota_id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("itemCotaAbastecimento_id", itemCotaAbastecimento.getId());
        if (abastecimentoObjetoFrota.getId() != null) {
            q.setParameter("id", abastecimentoObjetoFrota.getId());
        }

        return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
    }

    public Integer recuperarProximoNumeroAbastecimento(Integer anoAbastecimento) {
        String sql = " select max(numero) + 1 from abastecimentoObjetoFrota where ano = :ano ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", anoAbastecimento);
        return q.getResultList() != null && q.getResultList().get(0) != null ? ((BigDecimal) q.getResultList().get(0)).intValue() : 1;
    }

    public AbastecimentoObjetoFrota salvarRetornando(AbastecimentoObjetoFrota selecionado) {
        selecionado = em.merge(selecionado);
        if (selecionado.getTipoObjetoFrota().isVeiculo()) {
            veiculoFacade.criarLancamentoKmPercorrido((Veiculo) selecionado.getObjetoFrota(), selecionado.getKm());
        } else {
            equipamentoFacade.criarLancamentoHoraPercorrido((Equipamento) selecionado.getObjetoFrota(), selecionado.getHorasUso());
        }
        return recuperar(((AbastecimentoObjetoFrota) em.merge(selecionado)).getId());
    }

    public BigDecimal buscarKmInicialNoPeriodoDeAbastecimento(ObjetoFrota objetoFrota, Date abastecimentoInicial, Date abastecimentoFinal) {
        String sql = "select coalesce(abastec.km, 0) kminicial \n" +
            "   from abastecimentoobjetofrota abastec \n" +
            " where abastec.id = (select id \n" +
            "                       from (select s_abastecimento.id \n" +
            "                                from abastecimentoobjetofrota s_abastecimento \n" +
            "                             where s_abastecimento.objetofrota_id = :id_objeto \n" +
            "                               and s_abastecimento.dataabastecimento between :abastecimento_inicial and :abastecimento_final " +
            "                             order by s_abastecimento.dataabastecimento asc, s_abastecimento.id asc) \n" +
            "                    where rownum = 1) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_objeto", objetoFrota.getId());
        q.setParameter("abastecimento_inicial", abastecimentoInicial);
        q.setParameter("abastecimento_final", abastecimentoFinal);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarKmFinalNoPeriodoDeAbastecimento(ObjetoFrota objetoFrota, Date abastecimentoInicial, Date abastecimentoFinal) {
        String sql = "select coalesce(abastec.km, 0) kmfinal \n" +
            "   from abastecimentoobjetofrota abastec \n" +
            " where abastec.id = (select id \n" +
            "                       from (select s_abastecimento.id \n" +
            "                                from abastecimentoobjetofrota s_abastecimento \n" +
            "                             where s_abastecimento.objetofrota_id = :id_objeto \n" +
            "                               and s_abastecimento.dataabastecimento between :abastecimento_inicial and :abastecimento_final " +
            "                             order by s_abastecimento.dataabastecimento desc, s_abastecimento.id desc) \n" +
            "                    where rownum = 1) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_objeto", objetoFrota.getId());
        q.setParameter("abastecimento_inicial", abastecimentoInicial);
        q.setParameter("abastecimento_final", abastecimentoFinal);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarHorasUsoInicialNoPeriodoDeAbastecimento(ObjetoFrota objetoFrota, Date abastecimentoInicial, Date abastecimentoFinal) {
        String sql = "select coalesce(abastec.horasuso, 0) kminicial \n" +
            "   from abastecimentoobjetofrota abastec \n" +
            " where abastec.id = (select id \n" +
            "                       from (select s_abastecimento.id \n" +
            "                                from abastecimentoobjetofrota s_abastecimento \n" +
            "                             where s_abastecimento.objetofrota_id = :id_objeto \n" +
            "                               and s_abastecimento.dataabastecimento between :abastecimento_inicial and :abastecimento_final " +
            "                             order by s_abastecimento.dataabastecimento asc, s_abastecimento.id asc) \n" +
            "                    where rownum = 1) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_objeto", objetoFrota.getId());
        q.setParameter("abastecimento_inicial", abastecimentoInicial);
        q.setParameter("abastecimento_final", abastecimentoFinal);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarHorasUsoFinalNoPeriodoDeAbastecimento(ObjetoFrota objetoFrota, Date abastecimentoInicial, Date abastecimentoFinal) {
        String sql = "select coalesce(abastec.horasuso, 0) kmfinal \n" +
            "   from abastecimentoobjetofrota abastec \n" +
            " where abastec.id = (select id \n" +
            "                       from (select s_abastecimento.id \n" +
            "                                from abastecimentoobjetofrota s_abastecimento \n" +
            "                             where s_abastecimento.objetofrota_id = :id_objeto \n" +
            "                               and s_abastecimento.dataabastecimento between :abastecimento_inicial and :abastecimento_final " +
            "                             order by s_abastecimento.dataabastecimento desc, s_abastecimento.id desc) \n" +
            "                    where rownum = 1) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_objeto", objetoFrota.getId());
        q.setParameter("abastecimento_inicial", abastecimentoInicial);
        q.setParameter("abastecimento_final", abastecimentoFinal);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarQtdLitrosNoPeriodoDeAbastecimento(ObjetoFrota objetoFrota, Date abastecimentoInicial, Date abastecimentoFinal) {
        String sql = "select sum(item.quantidade) litros\n" +
            "   from abastecimentoobjetofrota abastec\n" +
            "  inner join itemabastecobjetofrota item on item.abastecimentoobjetofrota_id = abastec.id\n" +
            "where abastec.objetofrota_id = :id_objeto\n" +
            "  and abastec.dataabastecimento between :abastecimento_inicial and :abastecimento_final " +
            "  and abastec.id <> (select id \n" +
            "                        from (select s_abastecimento.id \n" +
            "                                 from abastecimentoobjetofrota s_abastecimento \n" +
            "                               where s_abastecimento.objetofrota_id = :id_objeto \n" +
            "                                 and s_abastecimento.dataabastecimento between :abastecimento_inicial and :abastecimento_final \n" +
            "                               order by s_abastecimento.dataabastecimento desc, s_abastecimento.id desc) \n" +
            "                     where rownum = 1) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_objeto", objetoFrota.getId());
        q.setParameter("abastecimento_inicial", abastecimentoInicial);
        q.setParameter("abastecimento_final", abastecimentoFinal);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList().get(0) != null ? (BigDecimal) q.getResultList().get(0) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    public AbastecimentoObjetoFrota buscarUltimoAbastecimento(ObjetoFrota objetoFrota, AbastecimentoObjetoFrota desconsiderar) {
        String sql = " select abast.* " +
            "   from abastecimentoobjetofrota abast " +
            " where abast.objetofrota_id = :id_objetofrota ";
        if (desconsiderar != null && desconsiderar.getId() != null) {
            sql += " and abast.id <> :id_abastecimento ";
        }
        sql += " order by abast.ano, abast.numero desc ";
        Query q = em.createNativeQuery(sql, AbastecimentoObjetoFrota.class);
        q.setParameter("id_objetofrota", objetoFrota.getId());
        if (desconsiderar != null && desconsiderar.getId() != null) {
            q.setParameter("id_abastecimento", desconsiderar.getId());
        }
        return q.getResultList().isEmpty() ? null : (AbastecimentoObjetoFrota) q.getResultList().get(0);
    }
}
