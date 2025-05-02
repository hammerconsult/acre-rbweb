package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Edi on 15/04/2015.
 */
@Stateless
public class ReprocessamentoCotaOrcamentariaFacade {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoOrcamentarioFacade grupoOrcamentarioFacade;
    @EJB
    private CotaOrcamentariaFacade cotaOrcamentariaFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;

    public EntityManager getEm() {
        return em;
    }

    public BigDecimal recuperarValorUtilizadoNaCota(CotaOrcamentaria cotaOrcamentaria, Exercicio exercicio) {
        if (cotaOrcamentaria.getId() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal valorEmpenhado = BigDecimal.ZERO;
        BigDecimal valorEstornado = BigDecimal.ZERO;

        String sqlEmpenho = " select " +
            " coalesce(sum(emp.valor), 0) as valor " +
            " from empenho emp " +
            " inner join fontedespesaorc fonte on emp.fontedespesaorc_id = fonte.id " +
            " inner join grupoorcamentario go on fonte.grupoorcamentario_id = go.id " +
            " inner join grupocotaorc goorc on go.id = goorc.grupoorcamentario_id " +
            " inner join cotaorcamentaria cota on goorc.id = cota.grupocotaorc_id and cota.indice = extract(month from emp.dataempenho)" +
            " where cota.id = :idCota " +
            " and extract(month from emp.dataempenho) = :mes" +
            " and extract(year from emp.dataempenho) = :ano" +
            " and emp.categoriaorcamentaria = :normal ";

        Query consultaEmpenho = em.createNativeQuery(sqlEmpenho);
        consultaEmpenho.setParameter("mes", cotaOrcamentaria.getMes().getNumeroMesString());
        consultaEmpenho.setParameter("ano", exercicio.getAno());
        consultaEmpenho.setParameter("idCota", cotaOrcamentaria.getId());
        consultaEmpenho.setParameter("normal", CategoriaOrcamentaria.NORMAL.name());
        if (consultaEmpenho.getResultList() != null
            || !consultaEmpenho.getResultList().isEmpty()) {
            valorEmpenhado = (BigDecimal) consultaEmpenho.getSingleResult();
        }

        String sqlEstorno = " select " +
            " coalesce(sum(est.valor), 0) as valor " +
            " from empenhoestorno est " +
            " inner join empenho emp on emp.id = est.empenho_id " +
            " inner join fontedespesaorc fonte on emp.fontedespesaorc_id = fonte.id " +
            " inner join grupoorcamentario go on fonte.grupoorcamentario_id = go.id " +
            " inner join grupocotaorc goorc on go.id = goorc.grupoorcamentario_id " +
            " inner join cotaorcamentaria cota on goorc.id = cota.grupocotaorc_id and cota.indice = extract(month from est.dataestorno) " +
            " where cota.id = :idCota " +
            " and extract(month from est.dataestorno) = :mes" +
            " and extract(year from est.dataestorno) = :ano" +
            " and est.categoriaorcamentaria = :normal ";

        Query consultaEstorno = em.createNativeQuery(sqlEstorno);
        consultaEstorno.setParameter("mes", cotaOrcamentaria.getMes().getNumeroMesString());
        consultaEstorno.setParameter("ano", exercicio.getAno());
        consultaEstorno.setParameter("idCota", cotaOrcamentaria.getId());
        consultaEstorno.setParameter("normal", CategoriaOrcamentaria.NORMAL.name());
        if (consultaEstorno.getResultList() != null
            || !consultaEstorno.getResultList().isEmpty()) {
            valorEstornado = (BigDecimal) consultaEstorno.getSingleResult();
        }
        return valorEmpenhado.subtract(valorEstornado);
    }

    public List<SuplementacaoORC> recuperarSuplementacoes(CotaOrcamentaria cotaOrcamentaria) {

        String sql = " select sup.* " +
            " from suplementacaoorc sup " +
            " inner join alteracaoorc alt on alt.id = sup.alteracaoorc_id " +
            " inner join fontedespesaorc f on f.id = sup.fontedespesaorc_id " +
            " inner join grupoorcamentario go on go.id = f.grupoorcamentario_id " +
            " inner join grupocotaorc gc on gc.grupoorcamentario_id = go.id " +
            " inner join cotaorcamentaria cota on cota.grupocotaorc_id = gc.id " +
            "       and cota.indice = sup.mes " +
            " where cota.id = :idCota and alt.status = 'DEFERIDO'";

        Query q = em.createNativeQuery(sql, SuplementacaoORC.class);
        q.setParameter("idCota", cotaOrcamentaria.getId());
        if (q.getResultList() != null
            || !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<AnulacaoORC> recuperarValorAnulacao(CotaOrcamentaria cotaOrcamentaria) {


        String sql = " select anul.* " +
            " from anulacaoorc anul " +
            " inner join alteracaoorc alt on alt.id = anul.alteracaoorc_id " +
            " inner join fontedespesaorc f on f.id = anul.fontedespesaorc_id " +
            " inner join grupoorcamentario go on go.id = f.grupoorcamentario_id " +
            " inner join grupocotaorc gc on gc.grupoorcamentario_id = go.id " +
            " inner join cotaorcamentaria cota on cota.grupocotaorc_id = gc.id " +
            "           and cota.indice = anul.mes " +
            " where cota.id = :idCota  and alt.status = 'DEFERIDO'";

        Query q = em.createNativeQuery(sql, AnulacaoORC.class);
        q.setParameter("idCota", cotaOrcamentaria.getId());
        if (q.getResultList() != null
            || !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


    public BigDecimal recuperarValorOrcamentoInicial(GrupoOrcamentario grupoOrcamentario) {

        BigDecimal valorOrcamento = BigDecimal.ZERO;

        String sql = " select coalesce(sum(ppf.valor), 0)                                 " +
            "      from provisaoppafonte ppf                                          " +
            "       inner join fontedespesaorc f on f.provisaoppafonte_id = ppf.id    " +
            "       inner join grupoorcamentario go on go.id = f.grupoorcamentario_id " +
            "       inner join grupocotaorc gc on gc.grupoorcamentario_id = go.id     " +
            "       where go.id = :idGrupoOrc                                         ";

        Query query = em.createNativeQuery(sql);
        query.setParameter("idGrupoOrc", grupoOrcamentario.getId());
        if (query.getResultList() != null
            || !query.getResultList().isEmpty()) {
            valorOrcamento = (BigDecimal) query.getSingleResult();
        }
        return valorOrcamento;
    }


    public GrupoOrcamentarioFacade getGrupoOrcamentarioFacade() {
        return grupoOrcamentarioFacade;
    }

    public CotaOrcamentariaFacade getCotaOrcamentariaFacade() {
        return cotaOrcamentariaFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }
}
