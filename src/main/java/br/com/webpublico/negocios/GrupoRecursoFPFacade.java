/*
 * Codigo gerado automaticamente em Fri Feb 11 09:06:37 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Stateless
public class GrupoRecursoFPFacade extends AbstractFacade<GrupoRecursoFP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;

    public GrupoRecursoFPFacade() {
        super(GrupoRecursoFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public GrupoRecursoFP recuperar(Object id) {
        GrupoRecursoFP grupoRecursoFP = em.find(GrupoRecursoFP.class, id);
        grupoRecursoFP.getAgrupamentoRecursoFP().size();
        return grupoRecursoFP;
    }

    public List<GrupoRecursoFP> buscarGruposRecursoFPPorOrgao(HierarquiaOrganizacional hierarquiaOrganizacional) {
        return buscarGruposRecursoFPPorCodigoOrgao(hierarquiaOrganizacional.getCodigo());
    }


    public List<GrupoRecursoFP> buscarGruposRecursoFPPorCodigoOrgao(String codigo) {
        String hql = "from GrupoRecursoFP where hierarquiaOrganizacional.codigo = :hierarquiaOrganizacional";
        Query q = em.createQuery(hql);
        q.setParameter("hierarquiaOrganizacional", codigo);
        return q.getResultList();
    }

    public List<GrupoRecursoFP> buscarGruposRecursoFPPorNome(String nome) {
        String hql = "from GrupoRecursoFP where nome = :nome";
        Query q = em.createQuery(hql);
        q.setParameter("nome", nome);
        return q.getResultList();
    }

    public List<GrupoRecursoFP> buscarGrupoRecursoFPsPorVinculoFPs(List<Long> vinculoFPIds) {
        String sql = "  select grupo.* " +
            "      from GrupoRecursoFP grupo " +
            "inner join AgrupamentoRecursoFP agr on agr.grupoRecursofp_ID = grupo.id " +
            "inner join RecursoFP rec on rec.id = agr.recursofp_ID " +
            "inner join RecursoDoVinculoFP recv on recv.recursofp_id = rec.id " +
            "inner join VinculoFP v on recv.vinculofp_id = v.id " +
            "     where recv.inicioVigencia = (select max(rev.inicioVigencia) from RecursoDoVinculoFP rev where rev.vinculoFP_id = v.id)" +
            "     and v.id in :vinculoFPIds ";
        Query q = em.createNativeQuery(sql, GrupoRecursoFP.class);
        q.setParameter("vinculoFPIds", vinculoFPIds);
        List retorno = q.getResultList();
        if (retorno == null) {
            retorno = Lists.newArrayList();
        }
        return retorno;
    }

    public List<GrupoRecursoFP> buscarGruposRecursoFPPorOrgaoAndDataOperacao(HierarquiaOrganizacional hierarquiaOrganizacional, Date dataOperacao) {
        return buscarGruposRecursoFPPorCodigoOrgaoAndDataOperacao(hierarquiaOrganizacional.getCodigo(), dataOperacao);
    }

    public List<GrupoRecursoFP> buscarGruposRecursoFPPorOrgaoAndDataOperacao(HierarquiaOrganizacional hierarquiaOrganizacional) {
        return buscarGruposRecursoFPPorCodigoOrgaoAndDataOperacao(hierarquiaOrganizacional.getCodigo(), sistemaFacade.getDataOperacao());
    }


    public List<GrupoRecursoFP> buscarGruposRecursoFPPorCodigoOrgaoAndDataOperacao(String codigo, Date dataOperacao) {
        String hql = "SELECT g.*" +
            " FROM " +
            "   (SELECT DISTINCT g.*," +
            "                    rec.ordem," +
            "                    rec.codigogrupo" +
            "    FROM gruporecursofp g" +
            "    INNER JOIN agrupamentorecursofp agrup ON agrup.gruporecursofp_id = g.id" +
            "    INNER JOIN recursofp rec ON rec.id = agrup.recursofp_id" +
            "    INNER JOIN hierarquiaOrganizacional ho ON ho.id = g.hierarquiaOrganizacional_id " +
            "    WHERE :dataOperacao BETWEEN trunc(rec.inicioVigencia) AND COALESCE(trunc(rec.finalVigencia), :dataOperacao) and ho.codigo like :codigo ) grupo" +
            " INNER JOIN gruporecursofp g ON g.id = grupo.id" +
            " ORDER BY grupo.ordem," +
            "         grupo.codigogrupo";
        Query q = em.createNativeQuery(hql, GrupoRecursoFP.class);
        q.setParameter("dataOperacao", dataOperacao, TemporalType.DATE);
        q.setParameter("codigo", codigo);
        List<GrupoRecursoFP> grupos = q.getResultList();
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setHierarquiaVigente(DataUtil.isVigente(grupo.getHierarquiaOrganizacional().getInicioVigencia(), grupo.getHierarquiaOrganizacional().getFimVigencia(), UtilRH.getDataOperacao()));
        }
        return grupos;
    }

    public List<GrupoRecursoFP> buscarGruposRecursoFPPorDataOperacao(Date data) {
        String hql = "   SELECT DISTINCT g.*," +
            "                    rec.ordem," +
            "                    rec.codigogrupo" +
            "    FROM gruporecursofp g" +
            "    INNER JOIN agrupamentorecursofp agrup ON agrup.gruporecursofp_id = g.id" +
            "    INNER JOIN recursofp rec ON rec.id = agrup.recursofp_id" +
            "    INNER JOIN hierarquiaOrganizacional ho ON ho.id = g.hierarquiaOrganizacional_id " +
            "    WHERE    rec.id in(select ficha.recursofp_id from FichaFinanceiraFP ficha  " +
            "              inner join FolhaDePagamento folha on folha.ID = ficha.folhaDePagamento_ID  " +
            "              inner join VinculoFP vin on vin.ID = ficha.vinculoFP_ID  " +
            "              inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID  " +
            " where folha.ano = :ano " +
            " and folha.mes = :mes ) " +
            " ORDER BY rec.ordem," +
            "         rec.codigogrupo ";
        DateTime operacao = new DateTime(data);
        Query q = em.createNativeQuery(hql, GrupoRecursoFP.class);
        q.setParameter("mes", Mes.getMesToInt(operacao.getMonthOfYear()).getNumeroMesIniciandoEmZero());
        q.setParameter("ano", operacao.getYear());
        List<GrupoRecursoFP> grupos = q.getResultList();
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setHierarquiaVigente(DataUtil.isVigente(grupo.getHierarquiaOrganizacional().getInicioVigencia(), grupo.getHierarquiaOrganizacional().getFimVigencia(), UtilRH.getDataOperacao()));
        }
        return grupos;
    }


    @Override
    public List<GrupoRecursoFP> lista() {
        String hql = "SELECT g.*" +
            " FROM\n" +
            "   (SELECT DISTINCT g.*," +
            "                    rec.ordem," +
            "                    rec.codigogrupo" +
            "    FROM gruporecursofp g" +
            "    INNER JOIN agrupamentorecursofp agrup ON agrup.gruporecursofp_id = g.id" +
            "    INNER JOIN recursofp rec ON rec.id = agrup.recursofp_id" +
            "    WHERE :dataOperacao BETWEEN rec.inicioVigencia AND COALESCE(rec.finalVigencia, :dataOperacao)) grupo" +
            " INNER JOIN gruporecursofp g ON g.id = grupo.id" +
            " ORDER BY grupo.ordem," +
            "         grupo.codigogrupo";
        Query q = em.createNativeQuery(hql, GrupoRecursoFP.class);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao(), TemporalType.DATE);
        List<GrupoRecursoFP> grupos = q.getResultList();
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setHierarquiaVigente(DataUtil.isVigente(grupo.getHierarquiaOrganizacional().getInicioVigencia(), grupo.getHierarquiaOrganizacional().getFimVigencia(), UtilRH.getDataOperacao()));
        }
        return grupos;
    }

    public boolean existeEventoFPParaGrupoAndMesAndAnoAndTipoFolha(GrupoRecursoFP grupoRecursoFP, Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento) {
        String sql = "  select distinct grupo.* " +
            "      from GrupoRecursoFP grupo " +
            "inner join AgrupamentoRecursoFP agr on agr.grupoRecursofp_ID = grupo.id " +
            "inner join RecursoFP rec on rec.id = agr.recursofp_ID " +
            "inner join FichaFinanceiraFP ficha on ficha.recursofp_id = rec.id " +
            "inner join FolhaDePagamento folha on folha.id = ficha.folhaDePagamento_ID " +
            "inner join ItemFichaFinanceiraFP item on item.fichaFinanceirafp_ID = ficha.id " +
            "inner join EventoFP ev on ev.id = item.eventofp_ID " +
            "     where grupo.id = :idGrupo " +
            "       and folha.mes = :mes " +
            "       and folha.ano = :ano " +
            "       and folha.tipoFolhaDePagamento = :tipo ";
        Query q = em.createNativeQuery(sql, GrupoRecursoFP.class);
        q.setParameter("idGrupo", grupoRecursoFP.getId());
        q.setParameter("mes", mes - 1);
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipoFolhaDePagamento.name());
        return !q.getResultList().isEmpty();
    }

    public boolean existeAgrupamentoParaRecursoFP(RecursoFP recursoFP, Mes mes, Integer ano) {
        String sql = "  select distinct grupo.* " +
            "      from GrupoRecursoFP grupo " +
            "inner join AgrupamentoRecursoFP agr on agr.grupoRecursofp_ID = grupo.id " +
            "inner join RecursoFP rec on rec.id = agr.recursofp_ID " +
            "inner join RecursoDoVinculoFP recv on recv.recursofp_id = rec.id " +
            "inner join VinculoFP v on recv.vinculofp_id = v.id " +
            "inner join FichaFinanceiraFP ficha on ficha.vinculofp_id = v.id " +
            "inner join FolhaDePagamento folha on folha.id = ficha.folhaDePagamento_id " +
            "     where rec.id = :idRec " +
            " and folha.mes = :mes " +
            " and folha.ano = :ano ";
        Query q = em.createNativeQuery(sql, GrupoRecursoFP.class);
        q.setParameter("idRec", recursoFP.getId());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        return !CollectionUtils.isEmpty(q.getResultList());
    }

    public boolean vinculoFPEstaNoGrupo(VinculoFP v, GrupoRecursoFP grupoRecursoFP) {
        String sql = "  select v.id " +
            "      from GrupoRecursoFP grupo " +
            "inner join AgrupamentoRecursoFP agr on agr.grupoRecursofp_ID = grupo.id " +
            "inner join RecursoFP rec on rec.id = agr.recursofp_ID " +
            "inner join RecursoDoVinculoFP recv on recv.recursofp_id = rec.id " +
            "inner join VinculoFP v on recv.vinculofp_id = v.id " +
            "     where recv.inicioVigencia = (select max(rev.inicioVigencia) from RecursoDoVinculoFP rev where rev.vinculoFP_id = v.id)" +
            "     and grupo.id = :idRec " +
            "     and v.id = :vinculo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRec", grupoRecursoFP.getId());
        q.setParameter("vinculo", v.getId());
        return !CollectionUtils.isEmpty(q.getResultList());
    }

    public List<GrupoRecursoFP> buscarGruposRecursoFPsPorEntidadeAndCategoriaPrestacaoContas(Entidade entidade, CategoriaDeclaracaoPrestacaoContas categoria) {
        String sql = " SELECT grupo.* " +
                "FROM ENTIDADEDPCONTAS entidadeDp " +
                "  INNER JOIN ITEMENTIDADEDPCONTAS item ON entidadeDp.ID = item.ENTIDADEDPCONTAS_ID " +
                "  INNER JOIN ENTIDADE entidade ON item.ENTIDADE_ID = entidade.ID " +
                "  INNER JOIN ITEMENTIDADEUNIDADEORG itemOrgao ON item.ID = itemOrgao.ITEMENTIDADEDPCONTAS_ID " +
                "  INNER JOIN HIERARQUIAORGANIZACIONAL ho ON itemOrgao.HIERARQUIAORGANIZACIONAL_ID = ho.ID " +
                "  INNER JOIN GRUPORECURSOFP grupo ON ho.ID = grupo.HIERARQUIAORGANIZACIONAL_ID " +
                "  INNER JOIN AGRUPAMENTORECURSOFP agrupamento ON grupo.ID = agrupamento.GRUPORECURSOFP_ID " +
                "  INNER JOIN RECURSOFP recurso ON agrupamento.RECURSOFP_ID = recurso.ID " +
                "  INNER JOIN DECLARACAOPRESTACAOCONTAS declaracao ON entidadeDp.DECLARACAOPRESTACAOCONTAS_ID = declaracao.ID " +
                " WHERE :dataAtual BETWEEN ho.INICIOVIGENCIA AND coalesce(ho.FIMVIGENCIA, :dataAtual) AND " +
                "       :dataAtual BETWEEN recurso.INICIOVIGENCIA AND coalesce(recurso.FINALVIGENCIA, :dataAtual) AND " +
                "       :dataAtual BETWEEN entidadeDp.INICIOVIGENCIA AND coalesce(entidadeDp.FINALVIGENCIA, :dataAtual) " +
                "       AND entidade.ID = :entidadeId" +
                "       AND declaracao.CATEGORIADECLARACAO = :categoria " +
                "       AND entidade.ativa = :ativa ";

        Query q = em.createNativeQuery(sql, GrupoRecursoFP.class);
        q.setParameter("entidadeId", entidade.getId());
        q.setParameter("categoria", categoria.name());
        q.setParameter("dataAtual", UtilRH.getDataOperacao());
        q.setParameter("ativa", Boolean.TRUE);
        try {
            List<GrupoRecursoFP> grupos = q.getResultList();
            for (GrupoRecursoFP grupo : grupos) {
                grupo.setHierarquiaVigente(DataUtil.isVigente(grupo.getHierarquiaOrganizacional().getInicioVigencia(), grupo.getHierarquiaOrganizacional().getFimVigencia(), UtilRH.getDataOperacao()));
            }
            return grupos;
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public List<GrupoRecursoFP> buscarGruposRecursoFPPorHierarquiaAndMesAndAnoAndVersaoAndTipoFolha(HierarquiaOrganizacional ho, Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, Integer versao) {
        String sql = " SELECT DISTINCT " +
            "    grupo.* " +
            "FROM " +
            "    gruporecursofp grupo " +
            "    INNER JOIN agrupamentorecursofp agr ON agr.gruporecursofp_id = grupo.id " +
            "    INNER JOIN recursofp rec ON rec.id = agr.recursofp_id " +
            "    INNER JOIN recursodovinculofp rv ON rv.recursofp_id = rec.id " +
            "    INNER JOIN vinculofp vin ON vin.id = rv.vinculofp_id " +
            "    INNER JOIN fichafinanceirafp ficha ON ficha.vinculofp_id = vin.id " +
            "    INNER JOIN folhadepagamento folha ON folha.id = ficha.folhadepagamento_id " +
            "    INNER JOIN itemfichafinanceirafp item ON item.fichafinanceirafp_id = ficha.id " +
            "    INNER JOIN eventofp ev ON ev.id = item.eventofp_id " +
            "    INNER JOIN hierarquiaorganizacional ho ON ho.id = grupo.hierarquiaorganizacional_id " +
            "WHERE " +
            "        folha.mes = :mes" +
            "    AND " +
            "        folha.ano = :ano " +
            "    AND " +
            "        folha.tipofolhadepagamento = :tipo " +
            "    AND " +
            "        folha.calculadaem BETWEEN rv.iniciovigencia AND coalesce(rv.finalvigencia, folha.calculadaem) " +
            "    AND " +
            "        folha.calculadaem BETWEEN rec.iniciovigencia AND coalesce(rec.finalvigencia, folha.calculadaem) " +
            "    AND " +
            "        ho.codigo LIKE :ho ";
        if (versao != null) {
            sql += " AND folha.versao = :versao ";
        }

        Query q = em.createNativeQuery(sql, GrupoRecursoFP.class);
        q.setParameter("mes", mes - 1);
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipoFolhaDePagamento.name());
        q.setParameter("ho", ho.getCodigoSemZerosFinais() + "%");
        if (versao != null) {
            q.setParameter("versao", versao);
        }
        List<GrupoRecursoFP> grupos = q.getResultList();
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setHierarquiaVigente(DataUtil.isVigente(grupo.getHierarquiaOrganizacional().getInicioVigencia(), grupo.getHierarquiaOrganizacional().getFimVigencia(), UtilRH.getDataOperacao()));
        }
        return grupos;
    }

    public List<GrupoRecursoFP> buscarGruposRecursoFPPorFolhaPagamento(Long idFolhaPagamento, HierarquiaOrganizacional ho) {
        String hql = "   SELECT DISTINCT g.*," +
            "                    rec.ordem," +
            "                    rec.codigogrupo" +
            "    FROM gruporecursofp g" +
            "    INNER JOIN agrupamentorecursofp agrup ON agrup.gruporecursofp_id = g.id" +
            "    INNER JOIN recursofp rec ON rec.id = agrup.recursofp_id" +
            "    INNER JOIN hierarquiaOrganizacional ho ON ho.id = g.hierarquiaOrganizacional_id " +
            "    WHERE    rec.id in(select ficha.recursofp_id from FichaFinanceiraFP ficha  " +
            "              inner join FolhaDePagamento folha on folha.ID = ficha.folhaDePagamento_ID  " +
            "              inner join VinculoFP vin on vin.ID = ficha.vinculoFP_ID  " +
            "              inner join RecursoFP rec on rec.ID = ficha.recursoFP_ID  " +
            " where folha.id = :idFolha " +
            " and ficha.id in(select item.fichaFinanceiraFP_id from ITEMFICHAFINANCEIRAFP item where item.FICHAFINANCEIRAFP_ID = ficha.id) ";
        if (ho != null) {
            hql += " and ho.codigo like :ho ";
        }
            hql += " ) " +
            " ORDER BY rec.ordem," +
            "         rec.codigogrupo ";
        Query q = em.createNativeQuery(hql, GrupoRecursoFP.class);
        q.setParameter("idFolha",idFolhaPagamento);
        if (ho != null) {
            q.setParameter("ho", ho.getCodigoSemZerosFinais() + "%");
        }
        List<GrupoRecursoFP> grupos = q.getResultList();
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setHierarquiaVigente(DataUtil.isVigente(grupo.getHierarquiaOrganizacional().getInicioVigencia(), grupo.getHierarquiaOrganizacional().getFimVigencia(), UtilRH.getDataOperacao()));
        }
        return grupos;
    }

    public GrupoRecursoFP buscarGrupoRecursoVigentePorRecursoAndDataOperacao(RecursoFP recursoFP) {
        Query q = em.createQuery("select distinct grupo from GrupoRecursoFP grupo " +
            " inner join grupo.agrupamentoRecursoFP agrupamento " +
            " inner join grupo.hierarquiaOrganizacional ho " +
            " where agrupamento.recursoFP = :recursoFP " +
            " and :dataOperacao between ho.inicioVigencia and coalesce(ho.fimVigencia, :dataOperacao)");
        q.setParameter("recursoFP", recursoFP);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        if (!q.getResultList().isEmpty()) {
            return (GrupoRecursoFP) q.getSingleResult();
        }
        return null;
    }
}
