/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.MemoriaCalculoRH;
import br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha;
import br.com.webpublico.entidadesauxiliares.ValorFinanceiroRH;
import br.com.webpublico.entidadesauxiliares.rh.ContraChequeVO;
import br.com.webpublico.entidadesauxiliares.rh.ResumoFichaFinanceira;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistenteContraCheque;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistenteRelatorioFichaFinanceira;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.dirf.DirfReg316;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.ws.model.WSFichaFinanceira;
import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author andre
 */
@Stateless
public class FichaFinanceiraFPFacade extends AbstractFacade<FichaFinanceiraFP> {

    private static final Logger logger = LoggerFactory.getLogger(FichaFinanceiraFPFacade.class);
    private static final String NOME_IMAGEM_BRASAO_PREFEITURA = "Brasao_de_Rio_Branco.gif";
    private static final String CODIGO25 = "25";
    private static final String DESCRICAO_MODULO_EXPORTACAO = "IRRF";


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoExportacaoFacade grupoExportacaoFacade;
    @EJB
    private ModuloExportacaoFacade moduloExportacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContraChequeFacade contraChequeFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;

    public FichaFinanceiraFPFacade() {
        super(FichaFinanceiraFP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BigDecimal recuperaValorLiquidoFichaFinanceiraFP(FichaFinanceiraFP fichaFinanceiraFP) {
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(case when (item.tipoEventoFP = '").append(TipoEventoFP.VANTAGEM.name()).append("') then coalesce(item.valor, 0) else coalesce(-item.valor, 0) end)");
        sql.append("  from itemfichafinanceirafp item");
        sql.append(" inner join fichafinanceirafp ficha on ficha.id = item.fichafinanceirafp_id");
//        sql.append(" inner join eventofp evento on item.eventofp_id = evento.id");
        sql.append(" where ficha.id = :ficha");
        sql.append("   and item.tipoEventoFP in ('").append(TipoEventoFP.VANTAGEM.name()).append("', '").append(TipoEventoFP.DESCONTO.name()).append("')");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ficha", fichaFinanceiraFP.getId());
        q.setMaxResults(1);

        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else if (q.getSingleResult() == null) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }
    }

    public BigDecimal recuperaValorAnualPorVinculoFpETipoEvento(Long idVinculo, Integer ano, TipoEventoFP tipo, TipoFolhaDePagamento... tipos) {
        StringBuilder sql = new StringBuilder();
        sql.append("select sum(item.valor) from FolhaDePagamento folha join folha.fichaFinanceiraFPs ficha join ficha.itemFichaFinanceiraFP item ");
        sql.append(" where ficha.vinculoFP.id = :idVinculo");
        sql.append(" and item.tipoEventoFP = :tipoEvento ");
        sql.append(" and folha.ano = :ano ");
        sql.append(" and folha.tipoFolhaDePagamento in (:tipos) ");

        Query q = em.createQuery(sql.toString());
        q.setParameter("idVinculo", idVinculo);
        q.setParameter("ano", ano);
        q.setParameter("tipoEvento", tipo);
        q.setParameter("tipos", Arrays.asList(tipos));

        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        } else if (q.getSingleResult() == null) {
            return BigDecimal.ZERO;
        } else {
            return (BigDecimal) q.getSingleResult();
        }
    }

    public ValorFinanceiroRH buscarValorPorModuloGrupoVinculoMesAndAno(VinculoFP vinculoFP, Mes mes, Integer ano, Long codigoModulo, Long codigoGrupo, TipoFolhaDePagamento[] tipo) {
        ModuloExportacao modulo = moduloExportacaoFacade.recuperaModuloExportacaoPorCodigo(codigoModulo);
        GrupoExportacao grupo = grupoExportacaoFacade.recuperaGrupoExportacaoPorCodigoEModuloExportacao(codigoGrupo, modulo);
        return recuperarValorPorMesAndAnoPorVinculoFpEModuloExportacao(vinculoFP.getId(), mes, ano, grupo, modulo, tipo);
    }

    public ValorFinanceiroRH recuperaValorAnualPorVinculoFpEModuloExportacao(Long idVinculo, Integer ano, GrupoExportacao grupo, ModuloExportacao modulo, TipoFolhaDePagamento... tipos) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select new br.com.webpublico.entidadesauxiliares.ValorFinanceiroRH(coalesce(sum(case when item.tipoEventoFP = 'VANTAGEM' then item.valor else 0 end),0),   ");
        sql.append("  coalesce(sum(case when item.tipoEventoFP = 'DESCONTO' then item.valor else 0 end),0))  ");
        sql.append("  from FolhaDePagamento folha, ItemGrupoExportacao itemGrupo join folha.fichaFinanceiraFPs ficha join ficha.itemFichaFinanceiraFP item ");
        sql.append("  join itemGrupo.grupoExportacao grupo join grupo.moduloExportacao modulo ");
        sql.append("  where ficha.vinculoFP.id = :idVinculo");
        sql.append("  and folha.ano = :ano ");
        sql.append("  and itemGrupo.eventoFP = item.eventoFP ");
        sql.append("  and folha.tipoFolhaDePagamento in (:tipos) ");
        sql.append("  and item.tipoEventoFP in('VANTAGEM', 'DESCONTO') ");
        sql.append("  and grupo = :grupo ");
        sql.append("  and modulo = :modulo ");

        Query q = em.createQuery(sql.toString());
        q.setParameter("idVinculo", idVinculo);
        q.setParameter("ano", ano);
        q.setParameter("modulo", modulo);
        q.setParameter("grupo", grupo);
        q.setParameter("tipos", Arrays.asList(tipos));
        List<ValorFinanceiroRH> valorFinanceiroRHs = q.getResultList();
        if (valorFinanceiroRHs != null && valorFinanceiroRHs.isEmpty()) {
            return new ValorFinanceiroRH(BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return valorFinanceiroRHs.get(0);
    }

    public ValorFinanceiroRH recuperaValorAnualPorVinculoFpEModuloExportacao(Map.Entry<Entidade, HierarquiaOrganizacional> entidadeHo, Long idVinculo, Integer ano, GrupoExportacao grupo, ModuloExportacao modulo, TipoFolhaDePagamento... tipos) {
        if (entidadeHo.getValue().getCodigo().equalsIgnoreCase(hierarquiaOrganizacionalFacade.getRaizHierarquia(sistemaFacade.getDataOperacao()).getCodigo())) {
            return recuperaValorAnualPorVinculoFpEModuloExportacao(idVinculo, ano, grupo, modulo, tipos);
        }
        StringBuilder sql = new StringBuilder();
        sql.append("  select new br.com.webpublico.entidadesauxiliares.ValorFinanceiroRH(ficha.unidadeOrganizacional, coalesce(sum(case when item.tipoEventoFP = 'VANTAGEM' then item.valor else 0 end),0),   ");
        sql.append("  coalesce(sum(case when item.tipoEventoFP = 'DESCONTO' then item.valor else 0 end),0))  ");
        sql.append("  from FolhaDePagamento folha, ItemGrupoExportacao itemGrupo join folha.fichaFinanceiraFPs ficha join ficha.itemFichaFinanceiraFP item ");
        sql.append("  join itemGrupo.grupoExportacao grupo join grupo.moduloExportacao modulo ");
        sql.append("  where ficha.vinculoFP.id = :idVinculo");
        sql.append("  and folha.ano = :ano ");
        sql.append("  and itemGrupo.eventoFP = item.eventoFP ");
        sql.append("  and folha.tipoFolhaDePagamento in (:tipos) ");
        sql.append("  and item.tipoEventoFP in('VANTAGEM', 'DESCONTO') ");
        sql.append("  and grupo = :grupo ");
        sql.append("  and modulo = :modulo ");
        sql.append("  group by ficha.unidadeOrganizacional ");

        Query q = em.createQuery(sql.toString());
        q.setParameter("idVinculo", idVinculo);
        q.setParameter("ano", ano);
        q.setParameter("modulo", modulo);
        q.setParameter("grupo", grupo);
        q.setParameter("tipos", Arrays.asList(tipos));
        List<ValorFinanceiroRH> valorFinanceiroRHs = q.getResultList();

        for (ValorFinanceiroRH valorFinanceiroRH : valorFinanceiroRHs) {
            if (hierarquiaOrganizacionalFacade.estaNoOrgao(valorFinanceiroRH.getUnidadeOrganizacional(), entidadeHo.getValue())) {
                return valorFinanceiroRH;
            }
        }
        return new ValorFinanceiroRH(BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public BigDecimal buscarValorItemFichaFinanceiraPorVinculoFPAndEventoFPAndMesAndAno(VinculoFP vin, EventoFP evento, Integer ano, Mes mes) {
        String sql = " select  sum(item.valor) from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on folha.ID = ficha.FOLHADEPAGAMENTO_ID " +
            " inner join ITEMFICHAFINANCEIRAFP item on ficha.ID = item.FICHAFINANCEIRAFP_ID " +
            " inner join EVENTOFP evento on item.EVENTOFP_ID = evento.ID " +
            " where ficha.VINCULOFP_ID = :vinculoId " +
            " and evento.ID = :eventoId " +
            " and item.ANO = :ano " +
            " and item.mes = :mes ";


        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculoId", vin.getId());
        q.setParameter("eventoId", evento.getId());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMes());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;


    }

    public BigDecimal buscarValorDecimoTerceiroAnualPorVinculoFPAndEventoFpAndAno(VinculoFP vin, EventoFP evento, Integer ano) {
        String sql = " select  SUM(ITEM.VALOR) from FOLHADEPAGAMENTO folha " +
            " inner join FICHAFINANCEIRAFP ficha on folha.ID = ficha.FOLHADEPAGAMENTO_ID " +
            " inner join ITEMFICHAFINANCEIRAFP item on ficha.ID = item.FICHAFINANCEIRAFP_ID " +
            " inner join EVENTOFP evento on item.EVENTOFP_ID = evento.ID " +
            " where ficha.VINCULOFP_ID = :vinculoId " +
            " and evento.ID = :eventoId " +
            " and item.ANO = :ano " +
            " AND FOLHA.TIPOFOLHADEPAGAMENTO IN ('SALARIO_13','ADIANTAMENTO_13_SALARIO') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculoId", vin.getId());
        q.setParameter("eventoId", evento.getId());
        q.setParameter("ano", ano);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarValorItemFichaFinanceiraPorVinculoFPAndEventoFPAndAno(VinculoFP vin, EventoFP evento, Integer ano) {
        String sql = " select sum(item.VALOR) from FICHAFINANCEIRAFP ficha " +
            " inner join ITEMFICHAFINANCEIRAFP item on ficha.ID = item.FICHAFINANCEIRAFP_ID " +
            " inner join EVENTOFP evento on item.EVENTOFP_ID = evento.ID " +
            " where ficha.VINCULOFP_ID = :vinculoId " +
            " and evento.ID = :eventoId " +
            " and item.ANO = :ano  ";


        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculoId", vin.getId());
        q.setParameter("eventoId", evento.getId());
        q.setParameter("ano", ano);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;

    }

    public BigDecimal getSomaDosEventosMensal(Pessoa p, VinculoFP vinculoFP, Mes mes, Integer ano, DirfReg316 nomeReduzido, List<TipoFolhaDePagamento> tiposFolha, EventoFP verba, Boolean temDeducaoDeValor) {
        String folhas = "and (";
        for (int i = 0; i < tiposFolha.size(); i++) {
            folhas += " fp.tipoFolhaDePagamento = :tipo_folha_" + i + " or";
        }
        folhas = folhas.substring(0, folhas.length() - 2);
        folhas += ")";
        String sql = " SELECT (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                    "
            + "INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                             "
            + "INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                               "
            + "INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                      "
            + "INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                     "
            + "INNER JOIN eventofp      evento ON  evento.id = iff.eventofp_id "
            + "     WHERE fp.ano = :ano                                                                        "
            + "       AND fp.mes = :mes                                                                        "
            + "       AND iff.tipoeventofp = evento.tipoeventofp                                                                        "
            + "       AND m.pessoa_id = :pessoa_id                                                             ";
        if (verba != null) {
            sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
        }
        sql += "       AND v.id = :vinculo_id                                                                   "
            + folhas
            + "       AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige              "
            + "                           INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id      "
            + "                           INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id     "
            + "                                WHERE me.descricao = :descricaoModulo                           "
            + "                                  AND ige.operacaoformula = :adicao                             "
            + "                                  AND ge.nomereduzido = :nomeReduzido)                        ";
        if (!DirfReg316.RTRT.equals(nomeReduzido) || temDeducaoDeValor) {
            sql += ")  -                                                                                              "
                + "    (SELECT coalesce(sum(iff.valor),0) FROM itemfichafinanceirafp iff                           "
                + " INNER JOIN fichafinanceirafp ff ON ff.id = iff.fichafinanceirafp_id                            "
                + " INNER JOIN folhadepagamento  fp ON fp.id = ff.folhadepagamento_id                              "
                + " INNER JOIN vinculofp          v ON  v.id = ff.vinculofp_id                                     "
                + " INNER JOIN matriculafp        m ON  m.id = v.matriculafp_id                                    "
                + "     WHERE fp.ano = :ano                                                                       "
                + "        AND fp.mes = :mes                                                                       "
                + "        AND m.pessoa_id = :pessoa_id                                                            ";
            if (verba != null) {
                sql += " AND iff.eventofp_id = :eventofp_id                                                        ";
            }
            sql += " AND v.id = :vinculo_id                                                                  "
                + folhas
                + "        AND iff.eventofp_id IN (SELECT ige.eventofp_id FROM itemgrupoexportacao ige             "
                + "                            INNER JOIN grupoexportacao ge ON ige.grupoexportacao_id = ge.id     "
                + "                            INNER JOIN moduloexportacao me ON ge.moduloexportacao_id = me.id    "
                + "                                 WHERE me.descricao = :descricaoModulo                          "
                + "                                   AND ige.operacaoformula = :subtracao                         "
                + "                                   AND ge.nomereduzido = :nomeReduzido)";
        }
        sql += " ) AS resultado FROM dual";
        Query q = em.createNativeQuery(sql);

        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("pessoa_id", p.getId());
        q.setParameter("vinculo_id", vinculoFP.getId());
        if (verba != null) {
            q.setParameter("eventofp_id", verba.getId());
        }
        q.setParameter("descricaoModulo", DESCRICAO_MODULO_EXPORTACAO);
        q.setParameter("adicao", OperacaoFormula.ADICAO.name());
        if (!DirfReg316.RTRT.equals(nomeReduzido) || temDeducaoDeValor) {
            q.setParameter("subtracao", OperacaoFormula.SUBTRACAO.name());
        }
        q.setParameter("nomeReduzido", nomeReduzido.toString());
        int i = 0;
        for (TipoFolhaDePagamento tpf : tiposFolha) {
            q.setParameter("tipo_folha_" + i, tpf.name());
            i++;
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public ValorFinanceiroRH recuperarValorPorMesAndAnoPorVinculoFpEModuloExportacao(Long idVinculo, Mes mes, Integer ano, GrupoExportacao grupo, ModuloExportacao modulo, TipoFolhaDePagamento... tipos) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select new br.com.webpublico.entidadesauxiliares.ValorFinanceiroRH(coalesce(sum(case when item.tipoEventoFP = 'VANTAGEM' then item.valor else 0 end),0), coalesce(sum(case when item.tipoEventoFP = 'DESCONTO' then item.valor else 0 end),0), coalesce(sum(item.valorBaseDeCalculo),0)) from FolhaDePagamento folha, ItemGrupoExportacao itemGrupo join folha.fichaFinanceiraFPs ficha join ficha.itemFichaFinanceiraFP item ");
        sql.append("  join itemGrupo.grupoExportacao grupo join grupo.moduloExportacao modulo ");
        sql.append("  where ficha.vinculoFP.id = :idVinculo");
        sql.append("  and folha.ano = :ano ");
        sql.append("  and folha.mes = :mes ");
        sql.append("  and itemGrupo.eventoFP = item.eventoFP ");
        sql.append("  and folha.tipoFolhaDePagamento in (:tipos) ");
        sql.append("  and item.tipoEventoFP in('VANTAGEM', 'DESCONTO') ");
        sql.append("  and grupo = :grupo ");
        sql.append("  and modulo = :modulo ");

        Query q = em.createQuery(sql.toString());
        q.setParameter("idVinculo", idVinculo);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("modulo", modulo);
        q.setParameter("grupo", grupo);
        q.setParameter("tipos", Arrays.asList(tipos));
        List<ValorFinanceiroRH> valorFinanceiroRHs = q.getResultList();
        if (valorFinanceiroRHs != null && valorFinanceiroRHs.isEmpty()) {
            return new ValorFinanceiroRH();
        }
        return valorFinanceiroRHs.get(0);
    }


    /**
     * metodo utilizado para recuperar a ultima ficha financeira do contrato
     *
     * @param vinculoFP
     * @return a ultima ficha financeira do servidor, aposentado.
     */
    public FichaFinanceiraFP recuperaFichaFinanceiraPorContratoFP(VinculoFP vinculoFP) {
        return recuperaFichaFinanceiraPorContratoFP(vinculoFP, null);
    }

    public FichaFinanceiraFP recuperaFichaFinanceiraPorContratoFP(VinculoFP vinculoFP, TipoFolhaDePagamento tipoFolhaDePagamento) {
        String hql = "select ficha from FolhaDePagamento folha "
            + " inner join folha.fichaFinanceiraFPs ficha where ficha.vinculoFP = :vinculo and folha.calculadaEm is not null ";
        if (tipoFolhaDePagamento != null) {
            hql += " and folha.tipoFolhaDePagamento = :tipoFolha";
        }
        hql += " order by folha.calculadaEm desc";
        Query q = em.createQuery(hql);
        q.setParameter("vinculo", vinculoFP);
        if (tipoFolhaDePagamento != null) {
            q.setParameter("tipoFolha", tipoFolhaDePagamento);
        }
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (FichaFinanceiraFP) q.getSingleResult();

    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public FichaFinanceiraFP recuperar(Object id) {
        FichaFinanceiraFP ficha = em.find(FichaFinanceiraFP.class, id);
        Hibernate.initialize(ficha.getItemFichaFinanceiraFP());
        return ficha;
    }


    public FichaFinanceiraFP recuperarFichaSemItemFicha(Object id) {
        FichaFinanceiraFP ficha = em.find(FichaFinanceiraFP.class, id);
        return ficha;
    }

    public List<EventoTotalItemFicha> buscarTotalEventosItemFichaFinanceira(VinculoFP ep, Integer ano, Integer mes, TipoFolhaDePagamento tipo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento,evento.tipoEventoFP, ");
        sql.append("       sum(case when (evento.tipoEventoFP = 'VANTAGEM' AND itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor ");
        sql.append("                when (evento.tipoEventoFP = 'DESCONTO' AND itemFicha.tipoEventoFP = 'DESCONTO') then itemFicha.valor ");
        sql.append("                when (evento.tipoEventoFP = 'INFORMATIVO' AND itemFicha.tipoEventoFP = 'INFORMATIVO') then itemFicha.valor ");
        sql.append("                when (evento.tipoEventoFP = 'VANTAGEM' AND itemFicha.tipoEventoFP = 'INFORMATIVO') then 0 ");
        sql.append("                when (evento.tipoEventoFP = 'DESCONTO' AND itemFicha.tipoEventoFP = 'INFORMATIVO') then 0 ");
        sql.append("                else - itemFicha.valor end), ");
        sql.append("       sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where itemFicha.ano = :ano");
        sql.append("   and itemFicha.mes = :mes");
        sql.append("   and vinculo.id = :ep and ficha.folhaDePagamento.tipoFolhaDePagamento in :tipos ");
        sql.append(" group by evento, evento.tipoEventoFP");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        //** ao calcular uma folha do tipo rescisão, se caso for ativada a retroação irá duplicar os eventos, visto que nunca se tem uma 2 meses calculados de rescisão para o mesmo servidor. */
        q.setParameter("tipos", getTipoCorreto(tipo));
        q.setParameter("ep", ep.getId());
        return q.getResultList();
    }

    public List<EventoTotalItemFicha> buscarTotalEventoItemFichaFinanceira(VinculoFP ep, Integer ano, Integer mes, TipoFolhaDePagamento tipo, EventoFP evento) {
        String hql = "select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento,evento.tipoEventoFP, " +
            "       sum(case when (evento.tipoEventoFP = 'VANTAGEM' AND itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor " +
            "                when (evento.tipoEventoFP = 'DESCONTO' AND itemFicha.tipoEventoFP = 'DESCONTO') then itemFicha.valor " +
            "                when (evento.tipoEventoFP = 'INFORMATIVO' AND itemFicha.tipoEventoFP = 'INFORMATIVO') then itemFicha.valor " +
            "                else - itemFicha.valor end), " +
            "       sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo))" +
            "  from ItemFichaFinanceiraFP itemFicha" +
            "  join itemFicha.eventoFP evento" +
            "  join itemFicha.fichaFinanceiraFP ficha" +
            "  join ficha.vinculoFP vinculo" +
            " where itemFicha.ano = :ano" +
            "   and itemFicha.mes = :mes" +
            "   and vinculo.id = :ep and ficha.folhaDePagamento.tipoFolhaDePagamento in :tipos " +
            "   and evento.id = :evento " +
            "   and ficha.folhaDePagamento.tipoFolhaDePagamento in :tipos " +
            " group by evento, evento.tipoEventoFP";

        Query q = em.createQuery(hql);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("tipos", getTipoCorreto(tipo));
        q.setParameter("ep", ep.getId());
        q.setParameter("evento", evento.getId());
        return q.getResultList();
    }

    public List<EventoTotalItemFicha> buscarTotalEventosItemFichaFinanceira(VinculoFP ep, Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento,itemFicha.tipoEventoFP, sum(itemFicha.valor),sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo), sum(itemFicha.valorIntegral))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where itemFicha.ano = :ano");
        sql.append("   and itemFicha.mes = :mes");
        sql.append(" and ficha.folhaDePagamento.ano = :ano ");
        sql.append("   and ficha.folhaDePagamento.mes = :mesFolha ");
        sql.append("   and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipo");
        sql.append("   and vinculo.id = :ep ");
        sql.append(" group by evento, itemFicha.tipoEventoFP");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMes());
        q.setParameter("mesFolha", Mes.getMesToInt(mes));
        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL);
        q.setParameter("ep", ep.getId());
        return q.getResultList();
    }

    public List<EventoTotalItemFicha> buscarTotalEventosItemFichaFinanceiraParaRetroativo(EntidadePagavelRH ep, Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento,itemFicha.tipoEventoFP, sum(itemFicha.valor),sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and itemFicha.tipoCalculoFP = :tipoCalculo ");
        sql.append("   and vinculo.id = :ep ");
        sql.append(" group by evento, itemFicha.tipoEventoFP");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("tipoCalculo", TipoCalculoFP.NORMAL);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", ep.getId());
        return q.getResultList();
    }

    public List<EventoTotalItemFicha> buscarTotalEventosItemFichaFinanceiraParaRetroativoAndNormal(EntidadePagavelRH ep, Integer ano, Integer mes) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento,itemFicha.tipoEventoFP, sum(itemFicha.valor),sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep ");
        sql.append(" group by evento, itemFicha.tipoEventoFP");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", ep.getId());
        return q.getResultList();
    }

    private List<TipoFolhaDePagamento> getTipoCorreto(TipoFolhaDePagamento tipo) {
        List<TipoFolhaDePagamento> tipos = new ArrayList<>();

        switch (tipo) {
            case NORMAL:
                tipos.add(TipoFolhaDePagamento.NORMAL);
//                tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
                break;
            case COMPLEMENTAR:
                tipos.add(TipoFolhaDePagamento.NORMAL);
                tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
                break;
            case RESCISAO:
                tipos.add(TipoFolhaDePagamento.NORMAL);
                tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
                break;
            case RESCISAO_COMPLEMENTAR:
                tipos.add(TipoFolhaDePagamento.NORMAL);
                tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
                break;
            case SALARIO_13:
                tipos.add(TipoFolhaDePagamento.NORMAL);
                tipos.add(TipoFolhaDePagamento.SALARIO_13);
//                tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
                tipos.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
                break;
            case ADIANTAMENTO_13_SALARIO:
                tipos.add(TipoFolhaDePagamento.NORMAL);
                tipos.add(TipoFolhaDePagamento.SALARIO_13);
//                tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
                tipos.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
                break;
            default:
                tipos.add(tipo);
        }

//        if (tipo.equals(TipoFolhaDePagamento.NORMAL)) {
//            tipos.add(TipoFolhaDePagamento.NORMAL);
//            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
//        }
//        if (tipo.equals(TipoFolhaDePagamento.RESCISAO) || tipo.equals(TipoFolhaDePagamento.RESCISAO_COMPLEMENTAR)) {
//            tipos.add(TipoFolhaDePagamento.NORMAL);
//            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
//        }
//        if (tipo.equals(TipoFolhaDePagamento.SALARIO_13) || tipo.equals(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO)) {
//            tipos.add(TipoFolhaDePagamento.NORMAL);
//            tipos.add(TipoFolhaDePagamento.SALARIO_13);
//            tipos.add(TipoFolhaDePagamento.COMPLEMENTAR);
//            tipos.add(TipoFolhaDePagamento.ADIANTAMENTO_13_SALARIO);
//        }
//        if (tipos.isEmpty()) {
//            tipos.add(tipo);
//        }
        return tipos;
    }

    public List<EventoTotalItemFicha> totalEventosItemFichaFinanceiraPorTipoFolha(Long idVinculo, Integer mes, Integer ano, TipoFolhaDePagamento tipo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento, sum(itemFicha.valor))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep");
        sql.append("   and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipo");
        sql.append(" group by evento");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", idVinculo);
        q.setParameter("tipo", tipo);
        return q.getResultList();
    }

    public List<EventoTotalItemFicha> totalEventosItemFichaFinanceiraPorTipoFolhaEventos(Long idVinculo, Integer mes, Integer ano, TipoFolhaDePagamento tipo, String clausulaWeb) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento,evento.tipoEventoFP, sum(case when itemFicha.tipoEventoFP = 'VANTAGEM' then itemFicha.valor else -itemFicha.valor end))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep");

        sql.append(clausulaWeb);

        sql.append("   and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipo");
        sql.append(" group by evento, evento.tipoEventoFP");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", idVinculo);
        q.setParameter("tipo", tipo);

        return q.getResultList();
    }

    public ItemFichaFinanceiraFP recuperaValorItemFichaFinanceiraFPPorEvento(Long idVinculo, Integer mes, Integer ano, TipoFolhaDePagamento tipo, EventoFP evento) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidades.ItemFichaFinanceiraFP(itemFicha.id,evento, ficha, sum(itemFicha.valor),sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep and itemFicha.eventoFP = :evento");
        sql.append("   and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipo");
        sql.append(" group by itemFicha.id,evento, ficha");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", idVinculo);
        q.setParameter("evento", evento);
        q.setParameter("tipo", tipo);
        if (q.getResultList().isEmpty()) {
            return new ItemFichaFinanceiraFP(null, evento, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return (ItemFichaFinanceiraFP) q.getResultList().get(0);
    }

    public ItemFichaFinanceiraFP recuperaValorItemFichaFinanceiraFPPorEventoETiposDeFolha(Long idVinculo, Integer mes, Integer ano, EventoFP evento, List<TipoFolhaDePagamento> tipos) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidades.ItemFichaFinanceiraFP(itemFicha.id,evento, ficha, sum(itemFicha.valor),sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep and itemFicha.eventoFP = :evento");
        sql.append("   and ficha.folhaDePagamento.tipoFolhaDePagamento in :tipo");
        sql.append(" group by itemFicha.id,evento, ficha");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", idVinculo);
        q.setParameter("evento", evento);
        q.setParameter("tipo", tipos);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (ItemFichaFinanceiraFP) q.getResultList().get(0);
    }

    public ItemFichaFinanceiraFP recuperaValorItemFichaFinanceiraFPPorEventos(Long idVinculo, Integer mes, Integer ano, TipoFolhaDePagamento tipo, List<EventoFP> eventos) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidades.ItemFichaFinanceiraFP(itemFicha.id,evento, ficha, sum(itemFicha.valor),sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep and itemFicha.eventoFP in :evento ");
        sql.append("   and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipo");
        sql.append(" group by itemFicha.id,evento, ficha");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", idVinculo);
        q.setParameter("evento", eventos);
        q.setParameter("tipo", tipo);
        if (q.getResultList().isEmpty()) {
            return new ItemFichaFinanceiraFP(null, null, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        return (ItemFichaFinanceiraFP) q.getResultList().get(0);
    }

    public BigDecimal recuperaValorTotalPorTipoEvento(Long idVinculo, Integer mes, Integer ano, TipoFolhaDePagamento tipo, TipoEventoFP tipoEvento) {
        StringBuilder sql = new StringBuilder();
        sql.append("select coalesce(sum(itemFicha.valor),0)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep and itemFicha.tipoEventoFP = :tipoEvento");
        sql.append("   and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipo");
        sql.append(" group by ficha ");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", idVinculo);
        q.setParameter("tipoEvento", tipoEvento);
        q.setParameter("tipo", tipo);
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getResultList().get(0);
    }

    public BigDecimal buscarValorTotalPorTipoEventoAndTipoCalculo(Long idVinculo, Integer mes, Integer ano, TipoFolhaDePagamento tipo, TipoEventoFP tipoEvento, TipoCalculoFP tipoCalculoFP) {
        StringBuilder sql = new StringBuilder();
        sql.append("select coalesce(sum(itemFicha.valor),0)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep and itemFicha.tipoEventoFP = :tipoEvento");
        sql.append("   and itemFicha.tipoCalculoFP = :tipoCalculo");
        sql.append("   and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipo");
        sql.append(" group by ficha ");

        Query q = em.createQuery(sql.toString());
        q.setParameter("tipoCalculo", tipoCalculoFP);
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", idVinculo);
        q.setParameter("tipoEvento", tipoEvento);
        q.setParameter("tipo", tipo);
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getResultList().get(0);
    }


    public EventoTotalItemFicha totalEventosItemFichaFinanceiraByEvento(EntidadePagavelRH ep, Integer mes, Integer ano, String codigoEvento) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento, sum(itemFicha.valor))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha join ficha.folhaDePagamento folha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        sql.append("   and vinculo.id = :ep and evento.codigo = :codigo");
        sql.append(" group by evento");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("codigo", codigoEvento);
        q.setParameter("ep", ((VinculoFP) ep).getId());
        if (q.getResultList().isEmpty()) return null;
        return (EventoTotalItemFicha) q.getResultList().get(0);
    }

    public List<ItemFichaFinanceiraFP> totalItemFichaFinanceiraFP(EntidadePagavelRH ep, Integer mes, Integer ano, TipoFolhaDePagamento tipo, boolean canRetirarRetroativoDoCalculo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select new br.com.webpublico.entidades.ItemFichaFinanceiraFP(evento, itemFicha.tipoEventoFP,sum(itemFicha.valor),sum(itemFicha.valorReferencia), sum(itemFicha.valorBaseDeCalculo))");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha join ficha.folhaDePagamento folha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where folha.ano = :ano");
        sql.append("   and folha.mes = :mes");
        if (canRetirarRetroativoDoCalculo) {
            sql.append(" and itemficha.tipoCalculoFP = :tipoCalculoFP ");
        }
        sql.append("   and folha.tipoFolhaDePagamento = :tipo");
        sql.append("   and vinculo.id = :ep ");
        sql.append(" group by evento, itemFicha.tipoEventoFP ");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", ep.getId());
        q.setParameter("tipo", tipo);
        if (canRetirarRetroativoDoCalculo) {
            q.setParameter("tipoCalculoFP", TipoCalculoFP.NORMAL);
        }
        return q.getResultList();
    }

    public List<ItemFichaFinanceiraFP> totalItemFichaFinanceiraFPPorEvento(EntidadePagavelRH ep, Integer mes, Integer ano, TipoFolhaDePagamento tipo, EventoFP eventoFP) {
        String hql = "select new br.com.webpublico.entidades.ItemFichaFinanceiraFP(evento, itemFicha.tipoEventoFP,sum(itemFicha.valor),sum(itemFicha.valorReferencia), sum(itemFicha.valorBaseDeCalculo))" +
            "  from ItemFichaFinanceiraFP itemFicha" +
            "  join itemFicha.eventoFP evento" +
            "  join itemFicha.fichaFinanceiraFP ficha join ficha.folhaDePagamento folha" +
            "  join ficha.vinculoFP vinculo" +
            " where folha.ano = :ano" +
            "   and folha.mes = :mes" +
            " and itemficha.tipoCalculoFP = :tipoCalculoFP " +
            "   and folha.tipoFolhaDePagamento = :tipo" +
            "   and vinculo.id = :ep " +
            "   and evento.id = :evento " +
            " group by evento, itemFicha.tipoEventoFP ";

        Query q = em.createQuery(hql);
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", ep.getId());
        q.setParameter("tipo", tipo);
        q.setParameter("tipoCalculoFP", TipoCalculoFP.NORMAL);
        q.setParameter("evento", eventoFP.getId());
        return q.getResultList();
    }

    public List<EventoTotalItemFicha> totalEventosItemFichaFinanceiraPorEvento(EntidadePagavelRH ep, Integer ano, Integer mes, String codigoEvento) {
        StringBuilder hql = new StringBuilder();
        //max(itemFicha.valorReferencia)) quando ocorre retroação de verba de falta a referencia fica o mesmo valor, não se pode somar essa referencia, acaba estornando valores menores
        hql.append("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento, sum(itemFicha.valor), max(itemFicha.valorReferencia))");
        hql.append("  from FichaFinanceiraFP ficha inner join ficha.itemFichaFinanceiraFP itemFicha");
        hql.append("  join itemFicha.eventoFP evento");

        hql.append("  join ficha.vinculoFP vinculo");
        hql.append(" where ");
        hql.append(" itemFicha.ano = :ano ");
        hql.append(" and  itemFicha.mes = :mes");
        hql.append("  and  vinculo.id = :ep ");
        hql.append("  and evento.codigo = :codigo");
        hql.append(" group by evento ");

        Query q = em.createQuery(hql.toString());

        q.setParameter("ano", ano);
        q.setParameter("codigo", codigoEvento);
        q.setParameter("mes", mes);
        q.setParameter("ep", ep.getIdCalculo());
        return q.getResultList();
    }

    public List<ItemFichaFinanceiraFP> recuperafichaFinanceiraFP(Integer mes, Integer ano, VinculoFP vinculoFP, TipoFolhaDePagamento tipoFolhaDePagamento, Integer versao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT item.*, CASE ");
        sql.append(" WHEN item.tipoeventofp  = :eventoInformativo THEN '3' ");
        sql.append(" WHEN item.tipoeventofp  = :eventoDesconto THEN '2' ");
        sql.append(" WHEN item.tipoeventofp  = :eventoVantagem THEN '1' ");
        sql.append(" END AS ordenacao ");
        sql.append(" FROM itemfichafinanceirafp item ");
        sql.append(" INNER JOIN fichafinanceirafp ficha ON ficha.id = item.fichafinanceirafp_id ");
        sql.append(" INNER JOIN folhadepagamento folha ON folha.id = ficha.folhadepagamento_id ");
        sql.append(" INNER JOIN eventofp evento ON evento.id = item.eventofp_id ");
        sql.append(" WHERE ficha.vinculofp_id = :vinculoFP ");
        sql.append(" AND folha.mes = :mes AND folha.ano = :ano ");
        sql.append(" AND folha.tipoFolhaDePagamento = :tipo ");
        if (versao != null) {
            sql.append(" AND folha.VERSAO = :versao ");
        }
        sql.append(" ORDER BY ordenacao, item.mes, item.ano, evento.codigo ");
        Query q = em.createNativeQuery(sql.toString(), ItemFichaFinanceiraFP.class);
        q.setParameter("tipo", tipoFolhaDePagamento.name());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes - 1);
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("eventoInformativo", TipoEventoFP.INFORMATIVO.name());
        q.setParameter("eventoDesconto", TipoEventoFP.DESCONTO.name());
        q.setParameter("eventoVantagem", TipoEventoFP.VANTAGEM.name());
        if (versao != null) {
            q.setParameter("versao", versao);
        }


//        Query q = em.createQuery(" select item from ItemFichaFinanceiraFP item "
//                + " inner join item.fichaFinanceiraFP ficha "
//                + " inner join ficha.folhaDePagamento folha "
//                + " where ficha.vinculoFP.id = :vinculoFP "
//                + " and folha.mes = :mes and folha.ano = :ano "
//                + " and folha.tipoFolhaDePagamento = :tipo "
//                + " order by item.eventoFP.ordenacaoTipoEventoFP, item.eventoFP.codigo");
//        q.setParameter("tipo", tipoFolhaDePagamento);
//        q.setParameter("ano", ano);
//        q.setParameter("mes", Mes.getMesToInt(mes));
//        q.setParameter("vinculoFP", vinculoFP.getId());

        return q.getResultList();
    }


    public List<ItemFichaFinanceiraFP> buscarItemFichaFinanceiraPorMesAnoVinculoTipoFolha(Integer mes, Integer ano, VinculoFP vinculoFP, TipoFolhaDePagamento tipoFolhaDePagamento) {

        Query q = em.createNativeQuery(" SELECT item.* "
            + " FROM itemfichafinanceirafp item "
            + " INNER JOIN fichafinanceirafp ficha ON ficha.id = item.fichafinanceirafp_id "
            + " INNER JOIN folhadepagamento folha ON folha.id = ficha.folhadepagamento_id "
            + " INNER JOIN eventofp evento ON evento.id = item.eventofp_id "
            + " WHERE ficha.vinculofp_id = :vinculoFP "
            + " AND folha.mes = :mes AND folha.ano = :ano "
            + " AND folha.tipoFolhaDePagamento = :tipo ", ItemFichaFinanceiraFP.class);
        q.setParameter("tipo", tipoFolhaDePagamento.name());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes).getNumeroMesIniciandoEmZero());
        q.setParameter("vinculoFP", vinculoFP.getId());
        return q.getResultList();
    }

    public FichaFinanceiraFP recuperaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolha(VinculoFP vinculoFPSelecionado, Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Query q = this.em.createQuery("select ficha from FichaFinanceiraFP ficha "
            + "                         where ficha.vinculoFP = :vinculoFP "
            + "                           and ficha.folhaDePagamento.ano = :ano "
            + "                           and ficha.folhaDePagamento.mes = :mes "
            + "                           and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipofolha");
        q.setParameter("vinculoFP", vinculoFPSelecionado);
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("tipofolha", tipoFolhaDePagamento);
        q.setMaxResults(1);
        try {
            return (FichaFinanceiraFP) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Long recuperaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolhaAtuarial(VinculoFP vinculoFPSelecionado, Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Query q = this.em.createQuery("select ficha.id from FichaFinanceiraFP ficha "
            + "                         where ficha.vinculoFP = :vinculoFP "
            + "                           and ficha.folhaDePagamento.ano = :ano "
            + "                           and ficha.folhaDePagamento.mes = :mes "
            + "                           and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipofolha");
        q.setParameter("vinculoFP", vinculoFPSelecionado);
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("tipofolha", tipoFolhaDePagamento);
        q.setMaxResults(1);
        try {
            return (Long) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public FichaFinanceiraFP recuperaUltimaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolha(VinculoFP vinculoFPSelecionado, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Query q = this.em.createQuery("select ficha from FichaFinanceiraFP ficha "
            + "                         join ficha.itemFichaFinanceiraFP itens "
            + "                         where ficha.vinculoFP = :vinculoFP "
            + "                           and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipofolha order by ficha.folhaDePagamento.calculadaEm desc ");
        q.setParameter("vinculoFP", vinculoFPSelecionado);
        q.setParameter("tipofolha", tipoFolhaDePagamento);
        q.setMaxResults(1);
        try {
            return (FichaFinanceiraFP) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<FichaFinanceiraFP> recuperaFichaFinanceiraFPPorVinculoFPMesesAnos(VinculoFP vinculoFPSelecionado, Integer mesInicial, Integer mesFinal, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Query q = this.em.createQuery("select distinct ficha from FichaFinanceiraFP ficha " +
            "                             join ficha.itemFichaFinanceiraFP "
            + "                         where ficha.vinculoFP = :vinculoFP "
            + "                           and ficha.folhaDePagamento.ano = :ano "
            + "                           and ficha.folhaDePagamento.mes between :mesinicial and :mesfinal"
            + "                           and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipofolha");
        q.setParameter("vinculoFP", vinculoFPSelecionado);
        q.setParameter("ano", ano);
        q.setParameter("mesinicial", Mes.getMesToInt(mesInicial));
        q.setParameter("mesfinal", Mes.getMesToInt(mesFinal));
        q.setParameter("tipofolha", tipoFolhaDePagamento);
        return q.getResultList();
    }

    public List<FichaFinanceiraFP> recuperaFichaFinanceiraFPPorVinculo(Long idVinculo) {
        Query q = this.em.createQuery("select ficha from FichaFinanceiraFP ficha "
            + "                         where ficha.vinculoFP.id = :vinculoFP "
            + "                           order by ficha.folhaDePagamento.ano desc, ficha.folhaDePagamento.mes desc");
        q.setParameter("vinculoFP", idVinculo);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<FichaFinanceiraFP> recuperaFichaFinanceiraFPPorVinculoAndAno(Long idVinculo, Integer ano) {
        Query q = this.em.createQuery("select ficha from FichaFinanceiraFP ficha "
            + "                         where ficha.vinculoFP.matriculaFP.id in (select v.matriculaFP.id from VinculoFP v where v.id = :vinculoFP)  "
            + "                         and ficha.folhaDePagamento.ano = :ano "
            + "                         and ficha.folhaDePagamento.efetivadaEm is not null "
            + "                         and ficha.folhaDePagamento.exibirPortal = true "
            + "                           order by ficha.folhaDePagamento.mes desc");
        q.setParameter("vinculoFP", idVinculo);
        q.setParameter("ano", ano);
        return q.getResultList();
    }

    private AssistenteRelatorioFichaFinanceira preencherAssistenteRelatorioFicha(WSFichaFinanceira wsFicha) {
        AssistenteRelatorioFichaFinanceira assistente = new AssistenteRelatorioFichaFinanceira();
        assistente.setCondicoes(montaIdFichas(wsFicha));
        assistente.setIdFicha(wsFicha.getId());
        assistente.setIdVinculo(wsFicha.getWsVinculoFP().getId());
        assistente.setDataOperacao(sistemaFacade.getDataOperacao());
        assistente.setOperacaoFormula(OperacaoFormula.ADICAO);

        return assistente;
    }

    public ByteArrayOutputStream gerarContrachequeNovo(AssistenteContraCheque assistente) {
        if (assistente == null) {
            return null;
        }
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;

        HashMap parameters = new HashMap();
        parameters.put("IMAGEM", img + NOME_IMAGEM_BRASAO_PREFEITURA);
        parameters.put("USUARIO", "Portal");
        parameters.put("MUNICIPIO", "Prefeitura Municipal de Rio Branco");
        parameters.put("MODULO", "Recursos Humanos");
        parameters.put("SECRETARIA", "DIRETORIA DE GESTÃO DE PESSOA");
        parameters.put("NOMERELATORIO", "SECRETARIA MUNICIPAL DE GESTÃO ADMINISTRATIVA E TECNOLOGIA DA INFORMAÇÃO - SEGATI");
        parameters.put("NOMEDOCUMENTO", "Contracheque");
        parameters.put("SUBREPORT_DIR", subReport);
        parameters.put("URLPORTAL", contraChequeFacade.atribuirUrlPortal() + "/autenticidade-de-documentos/");

        String arquivo = subReport + "ContraChequeNovo.jasper";
        Connection con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();

        try {
            assistente.setCondicoes(montaIdsWsFichas(assistente.getWsFichaFinanceira()));
            ContraChequeVO contraChequeVO = contraChequeFacade.buscarContraCheque(assistente);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(Lists.newArrayList(contraChequeVO));
            return AbstractReport.gerarReport(parameters, arquivo, ds);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            AbstractReport.fecharConnection(con);
        }
        return null;
    }

    public RelatorioDTO montarRelatorioDTO(AssistenteContraCheque assistente, List<FichaFinanceiraFP> fichas) {
        RelatorioDTO dto = new RelatorioDTO();
        List<FichaFinanceiraFP> fichasFinanceiras = Lists.newArrayList();
        if (fichas != null) {
            fichasFinanceiras = fichas;
        } else {
            for (WSFichaFinanceira wsFichaFinanceira : assistente.getWsFichaFinanceira()) {
                VinculoFP vinculo = vinculoFPFacade.buscarVinculoPorId(wsFichaFinanceira.getWsVinculoFP().getId());
                fichasFinanceiras.addAll(recuperaFichaFinanceiraFPPorVinculoFPMesesAnos(vinculo, wsFichaFinanceira.getMes().getNumeroMes(), wsFichaFinanceira.getMes().getNumeroMes(), wsFichaFinanceira.getAno(), wsFichaFinanceira.getTipoFolhaDePagamento()));
            }
        }
        contraChequeFacade.validarChaveAutenticidadeFicha(fichasFinanceiras);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.setNomeRelatorio("Contracheque");
        dto.adicionarParametro("USUARIO", "Portal");
        dto.adicionarParametro("MUNICIPIO", "Prefeitura Municipal de Rio Branco");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DIRETORIA DE GESTÃO DE PESSOA");
        dto.adicionarParametro("NOMERELATORIO", "SECRETARIA MUNICIPAL DE GESTÃO ADMINISTRATIVA E TECNOLOGIA DA INFORMAÇÃO - SEGATI");
        dto.adicionarParametro("NOMEDOCUMENTO", "Contracheque");
        dto.adicionarParametro("URLPORTAL", contraChequeFacade.atribuirUrlPortal() + "/autenticidade-de-documentos/");
        dto.adicionarParametro("FILTRO", montaIdsWsFichas(assistente.getWsFichaFinanceira()));
        dto.adicionarParametro("QRCODE", contraChequeFacade.getQrCodeContraCheque(assistente.getWsFichaFinanceira().get(0).getId()));
        return dto;
    }

    private AssistenteContraCheque preencherAssistenteContraCheque(List<FichaFinanceiraFP> fichasFinanceiras) {
        List<WSFichaFinanceira> wsFichaFinanceiras = WSFichaFinanceira.convertFichaFinanceiraToWSFichaFinanceiraList(fichasFinanceiras);
        AssistenteContraCheque assistente = new AssistenteContraCheque();
        assistente.setWsFichaFinanceira(wsFichaFinanceiras);
        return assistente;
    }

    public ByteArrayOutputStream gerarContracheque(WSFichaFinanceira wsFicha) {
        if (wsFicha == null) {
            return null;
        }
        String separator = System.getProperty("file.separator");
        String subReport = Util.getApplicationPath("/WEB-INF/") + separator + "report" + separator;
        String img = Util.getApplicationPath("/img/") + separator;

        HashMap parameters = new HashMap();
        parameters.put("IMAGEM", img + NOME_IMAGEM_BRASAO_PREFEITURA);
        parameters.put("USUARIO", "Portal");
        parameters.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        parameters.put("MODULO", "Recursos Humanos");
        parameters.put("SECRETARIA", "DEPARTAMENTO DE GESTÃO DE PESSOAS");
        parameters.put("NOMERELATORIO", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        parameters.put("FICHA_ID", montaIdFichas(wsFicha));
        parameters.put("SUBREPORT_DIR", subReport);

        String arquivo = subReport + "ContraCheque.jasper";
        Connection con = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
        try {
            JasperPrint jasperPrint = JasperFillManager.fillReport(arquivo, parameters, con);
            ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
            exporter.exportReport();
            return dadosByte;
        } catch (Exception ex) {
            logger.error("Erro ao gerarContracheque", ex);
        } finally {
            AbstractReport.fecharConnection(con);
        }
        return null;
    }

    private String montaIdsWsFichas(List<WSFichaFinanceira> wsFicha) {
        String retorno = " where ficha.id in (";
        for (WSFichaFinanceira ficha : wsFicha) {
            retorno += ficha.getId() + ",";
        }
        return retorno.substring(0, retorno.length() - 1) + ")";
    }

    private String montaIdFichas(WSFichaFinanceira wsFicha) {
        String retorno = " and ficha.id in (";
//        for (FichaFinanceiraFP financeiraFP : fichaFinanceiraFP) {
        retorno += wsFicha.getId() + " ) ";
//        }
//        return retorno.substring(0, retorno.length() - 1) + ")";
        return retorno;
    }

    public void salvarNovoItemFichaFinanceira(ItemFichaFinanceiraFP item) {
        try {
            getEntityManager().persist(item);
        } catch (Exception ex) {
            logger.error("Problema ao salvar o ItemFichaFinanceira " + item, ex);
            throw ex;
        }
    }

    public void salvarItemFichaFinanceira(ItemFichaFinanceiraFP item) {
        try {
            getEntityManager().merge(item);
        } catch (Exception ex) {
            logger.error("Problema ao alterar o ItemFichaFinanceira " + item, ex);
        }

    }

    public boolean existeFichaComFolhaEfetivadaPorContratoFP(ContratoFP contratoFP, Date data) {
        String sql = "select folha.* from folhadepagamento folha " +
            "               inner join fichafinanceirafp ficha on ficha.FOLHADEPAGAMENTO_ID = folha.id  " +
            "               inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.id " +
            "               where efetivadaem is not null    " +
            "               and (folha.mes >= :mesCedencia and folha.ano >= :anoCedencia)   " +
            "               and ficha.VINCULOFP_ID = :contrato ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("mesCedencia", DataUtil.getMesIniciandoEmZero(data));
        q.setParameter("anoCedencia", DataUtil.getAno(data));


        if (!q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean existeFolhaProcessadaPorVinculoFPAndPeriodoDeVigencia(VinculoFP vinculoFP, Date inicioDeVigencia, Date finalDeVigencia) {
        int mesInicial = inicioDeVigencia.getMonth() + 1;
        int mesFinal = getDezembro();

        int anoInicial = DataUtil.getAno(inicioDeVigencia);
        int anoFinal = DataUtil.getAno(new Date());

        if (finalDeVigencia != null) {
            anoFinal = DataUtil.getAno(finalDeVigencia);
            if (anoFinal == anoInicial) {
                mesInicial = DataUtil.getMes(inicioDeVigencia);
                mesFinal = DataUtil.getMes(finalDeVigencia);
            }
        }

        FichaFinanceiraFP fichaFinanceiraFP = this.buscarFichaFinanceiraFPPorVinculoFPAndPeriodo(vinculoFP, mesInicial, mesFinal, anoInicial, anoFinal);
        if (fichaFinanceiraFP != null) {
            return true;
        }

        return false;
    }

    public boolean hasFichaCalculadaPorData(VinculoFP vinculo, Date data) throws Exception {
        if (vinculo == null || data == null) {
            throw new ValidacaoException("O Vinculo e a data de cadastro devem estar preenchidos!");
        }
        if (vinculo.getId() == null) {
            return true;
        }

        Query query = em.createQuery("from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
            + " where ficha.vinculoFP = :vinculo and "
            + " folha.calculadaEm >= :param");
        query.setParameter("param", data);
        query.setParameter("vinculo", vinculo);
        return !query.getResultList().isEmpty();
    }


    private int getJaneiro() {
        return 1;
    }

    private int getDezembro() {
        return 12;
    }

    public FichaFinanceiraFP buscarFichaFinanceiraFPPorVinculoFPAndEnquadramentoFuncional(ContratoFP contratoFP, EnquadramentoFuncional enquadramentoFuncional) {
        String sql = " SELECT unique ficha.* " +
            " FROM FICHAFINANCEIRAFP ficha " +
            " INNER JOIN FOLHADEPAGAMENTO folha ON folha.id = ficha.FOLHADEPAGAMENTO_ID " +
            " INNER JOIN CONTRATOFP contratoFP ON ficha.VINCULOFP_ID = contratoFP.ID " +
            " INNER JOIN ENQUADRAMENTOFUNCIONAL EF ON EF.CONTRATOSERVIDOR_ID = contratoFP.id " +
            " WHERE contratoFP.id = :contratoFP AND ef.id = :enquadramentoFuncional ";
        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("contratoFP", contratoFP.getId());
        q.setParameter("enquadramentoFuncional", enquadramentoFuncional.getId());
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (FichaFinanceiraFP) q.getSingleResult();
        }
        return null;
    }

    public FichaFinanceiraFP buscarFichaFinanceiraFPPorVinculoFPAndPeriodo(VinculoFP vinculoFP, int mesInicial, int mesFinal, int anoInicial, int anoFinal) {
        Query q = this.em.createQuery(
            "select ficha " +
                "  from FichaFinanceiraFP ficha " +
                " where ficha.vinculoFP = :vinculo " +
                "   and ficha.folhaDePagamento.mes between :mesInicial and :mesFinal " +
                "   and ficha.folhaDePagamento.ano between :anoInicial and :anoFinal " +
                "   and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipoFolha");
        q.setParameter("vinculo", vinculoFP);
        q.setParameter("mesInicial", Mes.getMesToInt(mesInicial));
        q.setParameter("mesFinal", Mes.getMesToInt(mesFinal));
        q.setParameter("anoInicial", anoInicial);
        q.setParameter("anoFinal", anoFinal);
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (FichaFinanceiraFP) q.getSingleResult();
        }
        return null;
    }

    public boolean existeFolhaProcessadaPorVinculoFPAndData(VinculoFP vinculoFP, Date data) {
        int mes = DataUtil.getMes(data);
        int ano = DataUtil.getAno(data);

        FichaFinanceiraFP fichaFinanceiraFP = this.recuperaFichaFinanceiraFPPorVinculoFPMesAno(vinculoFP, ano, mes);
        if (fichaFinanceiraFP != null) {
            return true;
        }

        return false;
    }

    //    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<FichaFinanceiraFP> recuperarFichaFinanceiraFPPorAnoMes(Integer ano, Integer mes) {
        String sql = " select distinct ficha.* from FICHAFINANCEIRAFP ficha " +
            " inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            " inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = ficha.id " +
            " where folha.ano = :ano and folha.mes = :mes and tipoFolhaDePagamento = :tipoFolha ";
        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes - 1);
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }


    public FichaFinanceiraFP recuperaFichaFinanceiraFPPorVinculoFPMesAno(VinculoFP vinculoFP, Integer ano, Integer mes) {
        Query q = this.em.createQuery("select ficha from FichaFinanceiraFP ficha " +
            " where ficha.vinculoFP = :vinculo " +
            " and ficha.folhaDePagamento.ano = :ano " +
            " and ficha.folhaDePagamento.mes = :mes " +
            " and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipoFolha");
        q.setParameter("vinculo", vinculoFP);
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (FichaFinanceiraFP) q.getSingleResult();
        }
        return null;
    }

    public Long recuperaIdFichaFinanceiraFPPorVinculoFPMesAno(VinculoFP vinculoFP, String identificador) {
        String sql = " select ficha.id from FichaFinanceiraFP ficha " +
            "            inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.id" +
            "            where ficha.VINCULOFP_ID = :vinculo and ficha.identificador = :identificador ";
        Query q = this.em.createNativeQuery(sql);
        q.setParameter("vinculo", vinculoFP.getId());
        q.setParameter("identificador", identificador);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return Long.parseLong("" + q.getSingleResult());
        }
        return null;
    }

    public ItemFichaFinanceiraFP recuperaItemFichaFinanceiraFPPorVinculoFPMesAnoEvento(VinculoFP vinculoFP, Integer ano, Integer mes, EventoFP evento) {
        Query q = this.em.createQuery("select item from ItemFichaFinanceiraFP item " +
            " inner join item.fichaFinanceiraFP ficha " +
            " where ficha.folhaDePagamento.ano = :ano " +
            " and ficha.folhaDePagamento.mes = :mes " +
            " and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipoFolha" +
            " and ficha.vinculoFP = :vinculo" +
            " and item.eventoFP = :evento ");

        q.setParameter("vinculo", vinculoFP);
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL);
        q.setParameter("evento", evento);
        if (!q.getResultList().isEmpty()) {
            try {
                return (ItemFichaFinanceiraFP) q.getSingleResult();
            } catch (NonUniqueResultException noUnique) {
                logger.error("Retornou mais do que um resultado na consulta... retornando o primeiro resultado!", noUnique);
                return (ItemFichaFinanceiraFP) q.getResultList().get(0);
            }

        }
        return null;
    }

    public BigDecimal buscarItemFichaFinanceiraFPPorVinculoFPEventoAndFolha(VinculoFP vinculoFP, EventoFP evento, FolhaDePagamento folha) {
        Query q = this.em.createQuery("select coalesce(sum(item.valor),0) from ItemFichaFinanceiraFP item " +
            " inner join item.fichaFinanceiraFP ficha " +
            " where ficha.vinculoFP = :vinculo" +
            " and ficha.folhaDePagamento.id = :folha" +
            " and item.eventoFP = :evento ");

        q.setParameter("vinculo", vinculoFP);
        q.setParameter("folha", folha.getId());
        q.setParameter("evento", evento);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (BigDecimal) resultList.get(0);
        }
        return null;
    }

    public EventoTotalItemFicha recuperarTotalItemFichaPorVinculoFPAnoEvento(VinculoFP vinculoFP, Integer ano, EventoFP evento) {
        Query q = this.em.createQuery("select new br.com.webpublico.entidadesauxiliares.EventoTotalItemFicha(evento,evento.tipoEventoFP, sum(case when (evento.tipoEventoFP = 'VANTAGEM' AND itemFicha.tipoEventoFP = 'VANTAGEM') then itemFicha.valor when (evento.tipoEventoFP = 'DESCONTO' AND itemFicha.tipoEventoFP = 'DESCONTO') then itemFicha.valor else - itemFicha.valor end ),sum(itemFicha.valorReferencia),sum(itemFicha.valorBaseDeCalculo)) from ItemFichaFinanceiraFP itemFicha " +
            " inner join itemFicha.fichaFinanceiraFP ficha " +
            " inner join itemFicha.fichaFinanceiraFP.folhaDePagamento folha " +
            " inner join itemFicha.eventoFP evento " +
            " where folha.ano = :ano " +
            " and ficha.vinculoFP = :vinculo" +
            " and itemFicha.eventoFP = :evento " +
            " and folha.tipoFolhaDePagamento in :tiposFolha" +
            " group by evento, evento.tipoEventoFP ");
        q.setParameter("vinculo", vinculoFP);
        q.setParameter("ano", ano);
        q.setParameter("evento", evento);
        q.setParameter("tiposFolha", TipoFolhaDePagamento.getFolhasDePagamentoSemDecimoTerceiro());
        if (!q.getResultList().isEmpty()) {
            return (EventoTotalItemFicha) q.getSingleResult();
        }
        return null;
    }

    public List<ItemFichaFinanceiraFP> recuperarItensDaFichaFinanceira(FichaFinanceiraFP ff) {
        Query q = this.em.createQuery("select item from FichaFinanceiraFP ficha join ficha.itemFichaFinanceiraFP item where ficha = :ficha order by item.eventoFP.codigo");
        q.setParameter("ficha", ff);
        return q.getResultList();
    }

    public boolean verificaExistenciaFichaCalculadaPorDataEStatus(VinculoFP vinculoFP, Date dataInicial, Date dataFinal, StatusCompetencia status) {
        Query q = this.em.createQuery("from FichaFinanceiraFP  ficha " +
            "                   where ficha.folhaDePagamento.competenciaFP.statusCompetencia = :status " +
            "                     and ficha.vinculoFP = :vinculo " +
            "                     and ficha.folhaDePagamento.calculadaEm between :datainicial and :datafinal ");

        q.setParameter("status", status);
        q.setParameter("vinculo", vinculoFP);
        q.setParameter("datainicial", dataInicial, TemporalType.DATE);
        q.setParameter("datafinal", dataFinal, TemporalType.DATE);
        return !q.getResultList().isEmpty();
    }

    public List<FichaFinanceiraFP> recuperaListaFichaFinanceiraPorVinculoFPMesAnoTipoFolha(VinculoFP vinculoFPSelecionado, Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Query q = this.em.createQuery("select ficha from FichaFinanceiraFP ficha "
            + "                         where ficha.vinculoFP = :vinculoFP "
            + "                           and ficha.folhaDePagamento.ano = :ano "
            + "                           and ficha.folhaDePagamento.mes = :mes "
            + "                           and ficha.folhaDePagamento.tipoFolhaDePagamento = :tipofolha");
        q.setParameter("vinculoFP", vinculoFPSelecionado);
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("tipofolha", tipoFolhaDePagamento);
        return q.getResultList();
    }

    public BigDecimal recuperarValorTotalPorVinculo(Long idVinculo, Integer mes, Integer ano) {
        StringBuilder sql = new StringBuilder();
        sql.append("select coalesce(sum(itemFicha.valor),0)");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" where ficha.folhaDePagamento.ano = :ano");
        sql.append("   and ficha.folhaDePagamento.mes = :mes");
        sql.append("   and vinculo.id = :ep ");
        sql.append(" group by ficha ");

        Query q = em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ep", idVinculo);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<BigDecimal> buscarFichasSemRecursoFP() {
        Query q = em.createNativeQuery("select ficha.id from FichaFinanceiraFP ficha where ficha.recursoFP_id is null");
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<BigDecimal> buscarFichasSemLotacao() {
        Query q = em.createNativeQuery("select ficha.id from FichaFinanceiraFP ficha where ficha.unidadeOrganizacional_id is null");
        return q.getResultList();
    }

    public List<FichaFinanceiraFP> buscarFichaPorCpf(String cpf, Integer year) {
        String hql = "select ficha from FichaFinanceiraFP ficha "
            + "                           where replace(replace(replace(ficha.vinculoFP.matriculaFP.pessoa.cpf,'.',''),'-',''),'/','') = :numeroCpf "
            + "                           and ficha.folhaDePagamento.ano = :ano "
            + "                           and ficha.folhaDePagamento.efetivadaEm is not null "
            + "                           and ficha.folhaDePagamento.exibirPortal = true "
            + "                           order by ficha.folhaDePagamento.ano desc, ficha.folhaDePagamento.mes desc, ficha.folhaDePagamento.versao asc";
        Query q = this.em.createQuery(hql);
        q.setParameter("ano", year);
        q.setParameter("numeroCpf", StringUtil.retornaApenasNumeros(cpf.trim()));
        return q.getResultList();
    }

    public List<RecursoFP> buscarRecursosFPPorServidor(VinculoFP vinculoFP, Mes mes, Exercicio exercicio) {
        String hql = "select ficha.recursoFP from FichaFinanceiraFP ficha "
            + "                         where ficha.vinculoFP = :vinculo "
            + "                           and ficha.folhaDePagamento.ano = :ano "
            + "                           and ficha.folhaDePagamento.mes = :mes ";
        Query q = this.em.createQuery(hql);
        q.setParameter("vinculo", vinculoFP);
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("mes", mes);
        return q.getResultList();
    }

    public List<RecursoFP> buscarRecursosFPPorServidorPorTiposFolha(VinculoFP vinculoFP, List<TipoFolhaDePagamento> tipos, Exercicio exercicio) {
        String hql = "select ficha.recursoFP from FichaFinanceiraFP ficha "
            + "                         where ficha.vinculoFP = :vinculo "
            + "                           and ficha.folhaDePagamento.ano = :ano "
            + "                           and ficha.folhaDePagamento.tipoFolhaDePagamento in :tipos ";
        Query q = this.em.createQuery(hql);
        q.setParameter("vinculo", vinculoFP);
        q.setParameter("ano", exercicio.getAno());
        q.setParameter("tipos", tipos);
        return q.getResultList();
    }


    public List<ResumoFichaFinanceira> buscarResumoFichaFinanceira(Long idVinculo, Mes mes, Integer ano, TipoFolhaDePagamento tipo) {
        String sql = "select " +
            "                id as idVinculo,   " +
            "               coalesce(sum(vencimentoBase),0) as vencimentoBase," +
            "               coalesce(sum(valorBruto),0) as valorBruto,  " +
            "               coalesce(sum(valorDesconto),0) as valorDesconto, " +
            "               coalesce(sum(valorBruto) -sum(vencimentoBase),0) as outrasVerbas,  " +
            "               sum(coalesce(valorBruto,0)) - sum(coalesce(valorDesconto,0)) as totalLiquido " +
            "               from ( " +
            "               select  v.id," +
            "               (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "                 inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "                 inner join itembasefp itemBase on itemBase.eventofp_id = item.eventofp_id " +
            "                 inner join basefp b on b.id = itemBase.BASEFP_ID " +
            "               where ficha.vinculofp_id = v.id and itemFicha.id = item.id and item.tipoeventofp = 'VANTAGEM' and b.codigo = 1080) as vencimentoBase,  " +
            "                " +
            "               (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "                 inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "               where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'VANTAGEM') as valorBruto,  " +
            "              " +
            "               (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "                 inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "               where ficha.vinculofp_id = v.id and  item.id = itemFicha.id  and item.tipoeventofp = 'DESCONTO') as valorDesconto " +
            "               " +
            "             from vinculofp v  " +
            "             inner join matriculafp mat on mat.id = v.matriculafp_id  " +
            "               inner join pessoafisica pf on pf.id = mat.pessoa_id " +
            "               inner join FichaFinanceiraFP ff on ff.vinculofp_id = v.id " +
            "               inner join folhadepagamento folha on folha.id  = ff.folhadepagamento_id " +
            "               inner join itemFichaFinanceiraFP itemFicha on itemFicha.FICHAFINANCEIRAFP_ID = ff.id " +
            "               where folha.mes = :mes  and folha.ano = :ano and folha.TIPOFOLHADEPAGAMENTO = :tipo " +
            "               and v.id = :vinculo " +
            "                  )" +
            "                  dados group by id ";
        Query consulta = em.createNativeQuery(sql);


        consulta.setParameter("ano", ano);
        consulta.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        consulta.setParameter("tipo", tipo.name());
        consulta.setParameter("vinculo", idVinculo);
        List resultList = consulta.getResultList();
        List<ResumoFichaFinanceira> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            BigDecimal vinculo = (BigDecimal) objeto[0];
            BigDecimal vencimentoBase = (BigDecimal) objeto[1];
            BigDecimal valorBruto = (BigDecimal) objeto[2];
            BigDecimal valorDesconto = (BigDecimal) objeto[3];
            BigDecimal outrasVerbas = (BigDecimal) objeto[4];
            BigDecimal liquido = (BigDecimal) objeto[5];
            retorno.add(new ResumoFichaFinanceira(vinculo, vencimentoBase, valorBruto, valorDesconto, outrasVerbas, liquido, BigDecimal.ZERO));
        }
        return retorno;
    }

    /**
     * @param idVinculo
     * @param mes
     * @param ano
     * @param tipo
     * @param codigoBase
     * @return Valor bruto será carregado o valor da base pasasdo por parametro
     */
    public List<ResumoFichaFinanceira> buscarResumoFichaFinanceiraPorBaseMargemConsiganvel(Long idVinculo, Mes mes, Integer ano, TipoFolhaDePagamento tipo, Integer codigoBase, boolean calculaRetroativo) {
        String sql = "select " +
            "                id as idVinculo,   " +
            "               coalesce(sum(vencimentoBase),0) as vencimentoBase," +
            "               coalesce(sum(valorBruto),0) as valorBruto,  " +
            "               coalesce(sum(valorDesconto),0) as valorDesconto, " +
            "               coalesce(sum(valorBruto) -sum(vencimentoBase),0) as outrasVerbas,  " +
            "               sum(coalesce(valorBruto,0)) - sum(coalesce(valorDesconto,0)) as totalLiquido, " +
            "               sum(coalesce(vencimentoBase,0)) - sum(coalesce(descontoMargem,0)) as margem " +
            "               from ( " +
            "               select  v.id," +
            "               (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "                 inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "                 inner join itembasefp itemBase on itemBase.eventofp_id = item.eventofp_id " +
            "                 inner join basefp b on b.id = itemBase.BASEFP_ID " +
            "               where ficha.vinculofp_id = v.id and itemFicha.id = item.id and item.tipoeventofp = 'VANTAGEM' and b.codigo = :codigoBase";
        if (!calculaRetroativo) {
            sql += " and item.TIPOCALCULOFP = :calculoFP ";
        }
        sql += ") as vencimentoBase,  " +

            "                " +
            "               (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "                 inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "                 inner join itembasefp itembase on itembase.eventofp_id = item.eventofp_id  " +
            "                 inner join basefp base on base.id = itembase.basefp_id " +
            "                 inner join configuracaofp conf on conf.baseMargemConsignavel_id = base.id " +
            "               where ficha.vinculofp_id = v.id and item.id = itemFicha.id and item.tipoeventofp = 'VANTAGEM') as valorBruto,  " +
            "              " +
            "               (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id  " +
            "                 inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id  " +
            "               where ficha.vinculofp_id = v.id and  item.id = itemFicha.id  and item.tipoeventofp = 'DESCONTO') as valorDesconto, " +

            "       (select sum(item.valor) from folhadepagamento folha inner join FichaFinanceiraFP ficha on ficha.folhadepagamento_id = folha.id " +
            "           inner join itemfichafinanceirafp item on item.fichafinanceirafp_id = ficha.id " +
            "           inner join itembasefp itembase on itembase.eventofp_id = item.eventofp_id " +
            "           inner join basefp base on base.id = itembase.basefp_id" +
            "           inner join configuracaofp conf on conf.baseMargemConsignavel_id = base.id " +
            "         where ficha.vinculofp_id = v.id and itemFicha.id = item.id and item.tipoeventofp = 'DESCONTO' and base.codigo = :codigoBase ";
        if (!calculaRetroativo) {
            sql += " and item.TIPOCALCULOFP = :calculoFP ";
        }
        sql += " ) as descontoMargem " +
            "             from vinculofp v  " +
            "             inner join matriculafp mat on mat.id = v.matriculafp_id  " +
            "               inner join pessoafisica pf on pf.id = mat.pessoa_id " +
            "               inner join FichaFinanceiraFP ff on ff.vinculofp_id = v.id " +
            "               inner join folhadepagamento folha on folha.id  = ff.folhadepagamento_id " +
            "               inner join itemFichaFinanceiraFP itemFicha on itemFicha.FICHAFINANCEIRAFP_ID = ff.id " +
            "               where folha.mes = :mes  and folha.ano = :ano and folha.TIPOFOLHADEPAGAMENTO = :tipo " +
            "               and v.id = :vinculo " +
            "                  )" +
            "                  dados group by id ";
        Query consulta = em.createNativeQuery(sql);

        consulta.setParameter("ano", ano);
        consulta.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        consulta.setParameter("tipo", tipo.name());
        consulta.setParameter("vinculo", idVinculo);
        consulta.setParameter("codigoBase", codigoBase);
        if (!calculaRetroativo) {
            consulta.setParameter("calculoFP", TipoCalculoFP.NORMAL.name());
        }
        List resultList = consulta.getResultList();
        List<ResumoFichaFinanceira> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            BigDecimal vinculo = (BigDecimal) objeto[0];
            BigDecimal vencimentoBase = (BigDecimal) objeto[1];
            BigDecimal valorBruto = (BigDecimal) objeto[2];
            BigDecimal valorDesconto = (BigDecimal) objeto[3];
            BigDecimal outrasVerbas = (BigDecimal) objeto[4];
            BigDecimal liquido = (BigDecimal) objeto[5];
            BigDecimal margem = (BigDecimal) objeto[6];
            retorno.add(new ResumoFichaFinanceira(vinculo, vencimentoBase, valorBruto, valorDesconto, outrasVerbas, liquido, margem));
        }
        return retorno;
    }

    public ResumoFichaFinanceira buscarValorPorMesAndAnoAndTipoFolha(Long idVinculo, Mes mes, Integer ano, TipoFolhaDePagamento tipo, Integer codigoBase, boolean calculaRetroativo) {
        List<ResumoFichaFinanceira> fichaMes = buscarResumoFichaFinanceiraPorBaseMargemConsiganvel(idVinculo, mes, ano, tipo, codigoBase, calculaRetroativo);
        if (!fichaMes.isEmpty()) {
            if (fichaMes.size() > 1) {
                logger.error("A ficha possui mais de uma ficha por mês/ano e vinculoFP, retornando o primeiro item encontrado." + idVinculo + "-" + mes + "-" + ano + "-" + tipo);
            }
            return fichaMes.get(0);
        }
        return null;
    }

    public BigDecimal buscarValorDeducaoAposentadoAndPensionista65Anos() {
        String hql = " select coalesce(valor,0) from ValorReferenciaFP item " +
            " inner join referenciafp referencia on item.REFERENCIAFP_ID = referencia.id" +
            " where codigo = :codigo AND CURRENT_DATE BETWEEN item.INICIOVIGENCIA and coalesce(item.FINALVIGENCIA, current_date) ";
        Query q = this.em.createNativeQuery(hql);
        q.setParameter("codigo", CODIGO25);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarValorParcelaIsentaAno(Integer exercicio, List<VinculoFP> vinculoFPs) {
        String hql = " select COALESCE(sum(ficha.valorIsento),0) as valor from FICHAFINANCEIRAFP ficha " +
            " inner join FOLHADEPAGAMENTO folha on folha.id = ficha.FOLHADEPAGAMENTO_ID " +
            " where  ficha.VINCULOFP_ID in :vinculoIDs and folha.ano = :anoFolha ";
        Query q = this.em.createNativeQuery(hql);
        q.setParameter("vinculoIDs", getIdsVinculos(vinculoFPs));
        q.setParameter("anoFolha", exercicio);
        return (BigDecimal) q.getResultList().get(0);
    }

    public List<Long> getIdsVinculos(List<VinculoFP> vinculoFPs) {
        List<Long> ids = Lists.newArrayList();
        for (VinculoFP vinculoFP : vinculoFPs) {
            ids.add(vinculoFP.getId());
        }
        return ids;
    }


    public BigDecimal buscarTotalFichaPorFolhaEfetivada(VinculoFP vinculo, Exercicio ano) {
        String sql = " SELECT COUNT(FOLHA.MES) FROM FICHAFINANCEIRAFP FICHA " +
            "INNER JOIN VINCULOFP VINCULO ON VINCULO.ID = FICHA.VINCULOFP_ID " +
            "INNER JOIN FOLHADEPAGAMENTO FOLHA ON FOLHA.ID = FICHA.FOLHADEPAGAMENTO_ID " +
            "WHERE VINCULO.ID = :vinculo " +
            "AND FOLHA.EFETIVADAEM IS NOT NULL " +
            "AND FOLHA.ANO = :ano ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculo", vinculo.getId());
        q.setParameter("ano", ano.getAno());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal buscarValorBaseSalarial(ContratoFP contratoFP, Date dataOperacao) {
        String sql = "select " +
            " sum(distinct iff.valor) " +
            " from fichafinanceirafp ficha    " +
            " inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join ITEMFICHAFINANCEIRAFP iff on iff.FICHAFINANCEIRAFP_ID = ficha.id   " +
            " inner join eventofp evento on iff.EVENTOFP_ID = evento.id   " +
            " inner join itembasefp itembase on itembase.EVENTOFP_ID = evento.id  " +
            " inner join basefp on itembase.basefp_id = basefp.id       " +
            " where ficha.vinculofp_id = :contrato and folha.TIPOFOLHADEPAGAMENTO = :tipoFolha   " +
            " and basefp.codigo = :verbaVencimentoBase " +
            " and folha.EFETIVADAEM =      " +
            "           (select max(subfolha.efetivadaem) from folhadepagamento subfolha  " +
            "           inner join fichafinanceirafp subficha on subficha.FOLHADEPAGAMENTO_ID = subfolha.id  " +
            "           inner join itemfichafinanceirafp iff on iff.FICHAFINANCEIRAFP_ID = subficha.id  " +
            "           where subficha.VINCULOFP_ID = ficha.VINCULOFP_ID and subfolha.TIPOFOLHADEPAGAMENTO = folha.TIPOFOLHADEPAGAMENTO)";

        Query q = em.createNativeQuery(sql);
        q.setParameter("verbaVencimentoBase", configuracaoRHFacade.recuperarConfiguracaoRHVigente(dataOperacao).getVencimentoBasePortal());
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("tipoFolha", TipoFolhaDePagamento.NORMAL.name());
        return (BigDecimal) q.getResultList().get(0);
    }

    public BigDecimal buscarValorEnquadramentoPCSVigente(ContratoFP contratoFP, Date dataOperacao) {
        String sql = "select enqpcs.vencimentobase from enquadramentofuncional funcional  +" +
            "             inner join categoriapcs pcs on funcional.categoriapcs_id = pcs.id  +" +
            "             inner join enquadramentopcs enqpcs on pcs.id = enqpcs.categoriapcs_id  +" +
            "             where funcional.contratoservidor_id = :contrato  +" +
            "             and trunc(:dataoperacao) between trunc(enqpcs.iniciovigencia)  +" +
            "             and coalesce(trunc(enqpcs.finalvigencia), trunc(:dataoperacao))";

        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("dataoperacao", DataUtil.getDataHoraMinutoSegundoZerado(dataOperacao));
        return (BigDecimal) q.getResultList().get(0);
    }


    public BigDecimal buscarValorMensalDoServidorPorBaseFP(String codigoBase, Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, VinculoFP vinculo) {
        String sql = " SELECT " +
            "     coalesce(sum(case when(item_base.operacaoFormula = 'ADICAO' and item.TIPOEVENTOFP = 'VANTAGEM') then item.valor else -item.valor end), 0) as valorBase " +
            " FROM " +
            "     vinculofp v " +
            "     INNER JOIN matriculafp m ON m.id = v.matriculafp_id " +
            "     INNER JOIN fichafinanceirafp ficha ON ficha.vinculofp_id = v.id " +
            "     INNER JOIN folhadepagamento folha ON folha.id = ficha.folhadepagamento_id " +
            "     INNER JOIN itemfichafinanceirafp item ON item.fichafinanceirafp_id = ficha.id " +
            "     INNER JOIN eventofp evento ON evento.id = item.eventofp_id " +
            "     INNER JOIN itembasefp item_base ON item_base.eventofp_id = evento.id " +
            "     INNER JOIN basefp base ON base.id = item_base.basefp_id " +
            " WHERE " +
            "     base.codigo = :codigoBase" +
            "     AND folha.mes = :mes " +
            "     AND folha.ano = :ano " +
            "     AND folha.tipofolhadepagamento = :tipoFolhaDePagamento " +
            "     AND v.id = :vinculo ";
        Query q = this.em.createNativeQuery(sql);
        q.setParameter("codigoBase", codigoBase);
        q.setParameter("mes", mes - 1);
        q.setParameter("ano", ano);
        q.setParameter("tipoFolhaDePagamento", tipoFolhaDePagamento.name());
        q.setParameter("vinculo", vinculo.getId());
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public BigDecimal buscarValorBaseDeCalculoMensalDoServidor(String codigoBase, Integer mes, Integer ano, TipoFolhaDePagamento tipoFolhaDePagamento, VinculoFP vinculo) {
        String sql = " SELECT " +
            "     coalesce(sum(item.valor), 0) " +
            " FROM " +
            "     vinculofp v " +
            "     INNER JOIN matriculafp m ON m.id = v.matriculafp_id " +
            "     INNER JOIN fichafinanceirafp ficha ON ficha.vinculofp_id = v.id " +
            "     INNER JOIN folhadepagamento folha ON folha.id = ficha.folhadepagamento_id " +
            "     INNER JOIN itemfichafinanceirafp item ON item.fichafinanceirafp_id = ficha.id " +
            "     INNER JOIN eventofp evento ON evento.id = item.eventofp_id " +
            "     INNER JOIN itembasefp item_base ON item_base.eventofp_id = evento.id " +
            "     INNER JOIN basefp base ON base.id = item_base.basefp_id " +
            " WHERE " +
            "     base.codigo = :codigoBase" +
            "     AND folha.mes = :mes " +
            "     AND folha.ano = :ano " +
            "     AND folha.tipofolhadepagamento = :tipoFolhaDePagamento " +
            "     AND m.matricula = :vinculo ";
        Query q = this.em.createNativeQuery(sql);
        q.setParameter("codigoBase", codigoBase);
        q.setParameter("mes", mes - 1);
        q.setParameter("ano", ano);
        q.setParameter("tipoFolhaDePagamento", tipoFolhaDePagamento.name());
        q.setParameter("vinculo", vinculo.getMatriculaFP().getMatricula());
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    public List<EventoFP> buscarVerbasComLancamentosAnuais(VinculoFP vinculo, Integer ano) {

        StringBuilder sql = new StringBuilder();
        sql.append("select evento ");
        sql.append("  from ItemFichaFinanceiraFP itemFicha");
        sql.append("  join itemFicha.eventoFP evento");
        sql.append("  join itemFicha.fichaFinanceiraFP ficha");
        sql.append("  join ficha.vinculoFP vinculo");
        sql.append(" WHERE ficha.folhaDePagamento.ano = :ano ");
        sql.append("     AND ficha.folhaDePagamento.tipoFolhaDePagamento = :tipoFolhaDePagamento ");
        sql.append("     AND evento.automatico = :automatico ");
        sql.append("     AND evento.tipoEventoFP = :tipoEvento ");
        sql.append("     AND evento.tipoCalculo13 <> :tipo13Salario ");
        sql.append("     AND ficha.vinculoFP.id = :vinculo ");
        Query q = this.em.createQuery(sql.toString());
        q.setParameter("ano", ano);
        q.setParameter("automatico", false);
        q.setParameter("tipoEvento", TipoEventoFP.VANTAGEM);
        q.setParameter("tipo13Salario", TipoCalculo13.NAO);
        q.setParameter("tipoFolhaDePagamento", TipoFolhaDePagamento.NORMAL);
        q.setParameter("vinculo", vinculo.getId());

        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }

        return Lists.newArrayList();
    }

    public FichaFinanceiraFP buscarFichaFinanceiraPorId(Long idFicha) {
        String sql = " select ficha.*" +
            "    from fichafinanceirafp ficha " +
            "    where ficha.id = :idFicha ";
        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("idFicha", idFicha);

        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (FichaFinanceiraFP) resultList.get(0);
        }
        return null;
    }


    public List<FichaFinanceiraFP> buscarFichasFinanceirasPorCompetenciaOrServidorOrFolha(String filtros) {
        String sql = " select distinct ficha.* from fichafinanceirafp ficha " +
            " inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.id " +
            filtros;
        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        return q.getResultList();
    }

    public List<FichaFinanceiraFP> buscarFichaPorVinculoAndMesAndAno(Long idVinculo, Integer mes, Integer ano) {
        String sql = " select ficha.* from folhadepagamento folha " +
            " inner join fichafinanceirafp ficha on folha.id = ficha.folhadepagamento_id " +
            " inner join itemfichafinanceirafp item on ficha.id = item.fichafinanceirafp_id " +
            " where ficha.vinculofp_id = :idVinculo " +
            " and item.ano = :ano " +
            " and item.mes = :mes ";

        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("idVinculo", idVinculo);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);

        List<FichaFinanceiraFP> resultado = q.getResultList();
        return (resultado != null && !resultado.isEmpty()) ? resultado : Lists.<FichaFinanceiraFP>newArrayList();
    }

    public List<MemoriaCalculoRH> buscarMemoriasCalculoPorEvento(ItemFichaFinanceiraFP item) {
        String sql = " select memoria.* from MemoriaCalculoRH memoria " +
            " where memoria.ITEMFICHAFINANCEIRAFP_ID = :idItem ";
        Query q = em.createNativeQuery(sql, MemoriaCalculoRH.class);
        q.setParameter("idItem", item.getId());
        return q.getResultList();
    }

    public void apagarMemoriaCalculo(FichaFinanceiraFP ficha) {
        String sqlRemover = "delete from MemoriaCalculoRH where itemFichaFinanceiraFP_id in(select id from itemFichaFinanceiraFP where fichaFinanceiraFP_id = :id) ";
        Query consulta = em.createNativeQuery(sqlRemover).setParameter("id", ficha.getId());
        consulta.executeUpdate();
    }


    public List<FichaFinanceiraFP> buscarFichasFinanceirasFPPorAno(Integer ano, VinculoFP vinculoFP, TipoFolhaDePagamento tipoFolhaDePagamento) {
        Query q = em.createNativeQuery(" SELECT item.*, CASE "
            + " WHEN evento.tipoeventofp  = 'INFORMATIVO' THEN '3' "
            + " WHEN evento.tipoeventofp  = 'DESCONTO' THEN '2' "
            + " WHEN evento.tipoeventofp  = 'VANTAGEM' THEN '1' "
            + " END AS ordenacao "
            + " FROM itemfichafinanceirafp item "
            + " INNER JOIN fichafinanceirafp ficha ON ficha.id = item.fichafinanceirafp_id "
            + " INNER JOIN folhadepagamento folha ON folha.id = ficha.folhadepagamento_id "
            + " INNER JOIN eventofp evento ON evento.id = item.eventofp_id "
            + " WHERE ficha.vinculofp_id = :vinculoFP "
            + " AND folha.ano = :ano "
            + " AND folha.tipoFolhaDePagamento = :tipo "
            + " ORDER BY ordenacao, evento.codigo, item.ano ", ItemFichaFinanceiraFP.class);
        q.setParameter("tipo", tipoFolhaDePagamento.name());
        q.setParameter("ano", ano);
        q.setParameter("vinculoFP", vinculoFP.getId());

        return q.getResultList();
    }

    public List<VinculoFP> buscarServidoresDuplicados(Mes mes, Integer ano) {
        logger.info("Buscando servidores potencialmente duplicados");
        List<VinculoFP> vinculos = Lists.newArrayList();
        Query q = em.createNativeQuery("select distinct dados.id, dados.MATRICULA, dados.NUMERO, dados.nome  " +
            " from (  " +
            "         select v.id,  " +
            "                mat.matricula,  " +
            "                v.NUMERO,  " +
            "                pf.nome  " +
            "         from FICHAFINANCEIRAFP ficha  " +
            "                  inner join FOLHADEPAGAMENTO folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID  " +
            "                  inner join vinculofp v on ficha.VINCULOFP_ID = v.ID  " +
            "                  inner join matriculafp mat on v.MATRICULAFP_ID = mat.ID  " +
            "                  inner join PESSOAfisica pf on mat.PESSOA_ID = pf.ID  " +
            "                  inner join ITEMFICHAFINANCEIRAFP item on ficha.ID = item.FICHAFINANCEIRAFP_ID  " +
            "                  inner join eventofp e on item.EVENTOFP_ID = e.ID  " +
            "         where folha.mes = :mes  " +
            "           AND FOLHA.ano = :ano  " +
            "           and item.TIPOCALCULOFP = :tipoCalculo  " +
            "           and folha.TIPOFOLHADEPAGAMENTO = :tipo  " +
            "         group by v.id, mat.matricula,  " +
            "                  v.NUMERO,  " +
            "                  pf.nome,  " +
            "                  e.codigo,  " +
            "                  e.DESCRICAO,  " +
            "                  item.VALOR,  " +
            "                  item.mes,  " +
            "                  item.ano  " +
            "         having count(*)  " +
            "                    > 1) dados  ");
        q.setParameter("tipo", TipoFolhaDePagamento.NORMAL.name());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes.getNumeroMesIniciandoEmZero());
        q.setParameter("tipoCalculo", TipoCalculoFP.NORMAL.name());
        List<Object[]> resultados = q.getResultList();
        if (!resultados.isEmpty()) {
            logger.info("Encontrado {} duplicados", resultados.size());
        }
        for (Object[] resultado : resultados) {
            VinculoFP vinculoFP = em.find(VinculoFP.class, ((BigDecimal) resultado[0]).longValue());
            vinculos.add(vinculoFP);
        }

        return vinculos;
    }

    public List<Long> buscarIdsFichaFinanceiraPorVinculo(VinculoFP vinculoFP) {
        String sql = " select ficha.id from fichafinanceirafp ficha " +
            " where ficha.vinculofp_id = :vinculo " +
            " order by ficha.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("vinculo", vinculoFP.getId());
        List<BigDecimal> resultList = q.getResultList();
        List<Long> retorno = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (BigDecimal resultado : resultList) {
                retorno.add(resultado.longValue());
            }
        }
        return retorno;
    }
    public List<FichaFinanceiraFP> buscarFichasFinanceirasPorVinculoFPMesAnoTiposFolha(VinculoFP vinculoFP, Integer mes, Integer ano, String... tiposFolhaDePagamento) {
        String sql = "select ficha.* from FichaFinanceiraFP ficha " +
            "               inner join folhadepagamento folha on ficha.FOLHADEPAGAMENTO_ID = folha.ID " +
            " where ficha.VINCULOFP_ID = :vinculoFP " +
            "  and folha.ano = :ano " +
            "  and folha.mes = :mes " +
            "  and folha.tipoFolhaDePagamento in (:tiposfolha)";
        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setParameter("tiposfolha", Arrays.asList(tiposFolhaDePagamento));

        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public BigDecimal buscarValorReferenciaPorMesAnoTipoFolhaEventoVinculo(Integer mesItem, Integer anoItem, String tipoFolhaDePagamento, Long idVinculo, String codigoEvento, Integer mesFolha, Integer anoFolha) {
        Query q = em.createNativeQuery("select iff.valorReferencia from ItemFichaFinanceiraFP iff " +
            " inner join fichafinanceirafp ff on iff.FICHAFINANCEIRAFP_ID = ff.id " +
            " inner join folhadepagamento folha on ff.FOLHADEPAGAMENTO_ID = folha.id " +
            " inner join eventofp evento on iff.EVENTOFP_ID = evento.id " +
            " where ff.VINCULOFP_ID = :idVinculo " +
            " and ((folha.mes < :mesFolha and folha.ano = :anoFolha) or folha.ano < :anoFolha)" +
            " and folha.TIPOFOLHADEPAGAMENTO = :tipo " +
            " and evento.codigo = :evento " +
            " and iff.MES = :mesItem " +
            " and iff.ANO = :anoItem " +
            " order by folha.ano desc, folha.mes desc ");
        q.setParameter("evento", codigoEvento);
        q.setParameter("idVinculo", idVinculo);
        q.setParameter("tipo", tipoFolhaDePagamento);
        q.setParameter("anoItem", anoItem);
        q.setParameter("mesItem", mesItem);
        q.setParameter("mesFolha", mesFolha);
        q.setParameter("anoFolha", anoFolha);
        q.setMaxResults(1);
        List resultado = q.getResultList();

        if (resultado != null && !resultado.isEmpty()) {
            return (BigDecimal) resultado.get(0);
        }
        return null;
    }
}
