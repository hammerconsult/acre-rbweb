package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteConsultaMovimentacaoBemContabil;
import br.com.webpublico.entidadesauxiliares.MovimentacaoBemContabilGrupo;
import br.com.webpublico.entidadesauxiliares.MovimentacaoBemContabilMovimento;
import br.com.webpublico.entidadesauxiliares.MovimentacaoBemContabilUnidade;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class ConsultaMovimentacaoBemContabilFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private MovimentoEstoqueFacade movimentoEstoqueFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemMovelFacade;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public List<HierarquiaOrganizacional> getHierarquiasOrcamentariasConsulta(AssistenteConsultaMovimentacaoBemContabil assistente) {
        List<HierarquiaOrganizacional> hosOrc = Lists.newArrayList();
        if (assistente.getHierarquiaOrcamentaria() != null) {
            hosOrc.add(assistente.getHierarquiaOrcamentaria());

        } else if (assistente.getHierarquiaAdministrativa() != null) {
            hosOrc.addAll(hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                assistente.getHierarquiaAdministrativa().getSubordinada(),
                assistente.getDataFinal()));

        } else {
            hosOrc.addAll(hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipoData(
                "",
                assistente.getUsuarioSistema(),
                assistente.getDataFinal(),
                TipoHierarquiaOrganizacional.ORCAMENTARIA));
        }
        return hosOrc;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteConsultaMovimentacaoBemContabil buscarMovimentacaoUnidade(AssistenteConsultaMovimentacaoBemContabil assistente) {
        List<HierarquiaOrganizacional> hosOrc = getHierarquiasOrcamentariasConsulta(assistente);

        assistente.setTotal(hosOrc.size());
        for (HierarquiaOrganizacional ho : hosOrc) {
            assistente.setDescricaoProcesso("Buscando movimentação da unidade " + ho.getCodigo() + " ...");
            Long idUnidade = ho.getSubordinada().getId();
            MovimentacaoBemContabilUnidade mov = new MovimentacaoBemContabilUnidade();
            mov.setHierarquiaOrcamentaria(ho);
            if (assistente.getTipoConsulta().isContabil() || assistente.getTipoConsulta().isDetalhado()) {
                mov.setValorOriginalContabil(buscarSaldoGrupo(assistente.getGrupoBem(), NaturezaTipoGrupoBem.ORIGINAL, ho.getSubordinada(), assistente.getDataFinal()));
                mov.setValorAjusteContabil(buscarSaldoGrupo(assistente.getGrupoBem(), NaturezaTipoGrupoBem.DEPRECIACAO, ho.getSubordinada(), assistente.getDataFinal()));

                Exercicio exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(assistente.getDataFinal()));
                ContaContabil contaContabilOriginal = contaFacade.buscarContaContabilPorCodigoAndExercicio(AssistenteConsultaMovimentacaoBemContabil.CONTA_CONTABIL_ORIGINAL, exercicio);
                Date dataInicialExercicio = DataUtil.montaData(1, 0, exercicio.getAno()).getTime();
                if (contaContabilOriginal != null) {
                    mov.setValorOriginalBalancete(buscarSaldoContabil(ho.getSubordinada(), contaContabilOriginal, dataInicialExercicio, assistente.getDataFinal()));
                }
                ContaContabil contaContabilAjuste = contaFacade.buscarContaContabilPorCodigoAndExercicio(AssistenteConsultaMovimentacaoBemContabil.CONTA_CONTABIL_AJUSTE, exercicio);
                if (contaContabilAjuste != null) {
                    mov.setValorAjusteBalancete(buscarSaldoContabil(ho.getSubordinada(), contaContabilAjuste, dataInicialExercicio, assistente.getDataFinal()));
                }
            }
            if (assistente.getTipoConsulta().isAdministrativo() || assistente.getTipoConsulta().isDetalhado()) {
                mov.setValorOriginalAdm(buscarValorOriginalBens(assistente, idUnidade, assistente.getGrupoBem()));
                mov.setValorAjusteAdm(buscarValorAjusteBens(assistente, idUnidade, assistente.getGrupoBem()));
            }
            assistente.getMovimentacoes().add(mov);
            assistente.conta();
        }
        return assistente;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteConsultaMovimentacaoBemContabil buscarMovimentacaoGrupo(AssistenteConsultaMovimentacaoBemContabil assistente, MovimentacaoBemContabilUnidade movUnidade) {
        String sql = " " +
            " select distinct grupo.* from grupobem grupo " +
            "   inner join estadobem est on est.grupobem_id = grupo.id " +
            "   inner join eventobem ev on ev.estadoresultante_id = est.id " +
            " where est.detentoraorcamentaria_id = :idUnidade " +
            "   and trunc(ev.datalancamento) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
        sql += assistente.getGrupoBem() != null ? " and grupo.id = :idGrupo " : " ";
        sql += " order by grupo.codigo ";
        Query q = em.createNativeQuery(sql, GrupoBem.class);
        q.setParameter("idUnidade", movUnidade.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        if (assistente.getGrupoBem() != null) {
            q.setParameter("idGrupo", assistente.getGrupoBem().getId());
        }
        List<GrupoBem> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            assistente.setTotal(resultList.size());
            Long idUnidade = movUnidade.getHierarquiaOrcamentaria().getSubordinada().getId();
            for (GrupoBem grupo : resultList) {
                assistente.setDescricaoProcesso("Buscanco movimentaçãdo do grupo " + grupo + "...");
                MovimentacaoBemContabilGrupo movGrupo = new MovimentacaoBemContabilGrupo();
                movGrupo.setGrupoBem(grupo);
                GrupoBem grupoBem = movGrupo.getGrupoBem();
                assistente.setGrupoBem(grupo);
                if (assistente.getTipoConsulta().isContabil() || assistente.getTipoConsulta().isDetalhado()) {
                    movGrupo.setValorOriginalContabil(buscarSaldoGrupo(grupo, NaturezaTipoGrupoBem.ORIGINAL, movUnidade.getHierarquiaOrcamentaria().getSubordinada(), assistente.getDataFinal()));
                    movGrupo.setValorAjusteContabil(buscarSaldoGrupo(grupo, NaturezaTipoGrupoBem.DEPRECIACAO, movUnidade.getHierarquiaOrcamentaria().getSubordinada(), assistente.getDataFinal()));
                }

                if (assistente.getTipoConsulta().isAdministrativo() || assistente.getTipoConsulta().isDetalhado()) {
                    movGrupo.setValorOriginalAdm(buscarValorOriginalBens(assistente, idUnidade, grupoBem));
                    movGrupo.setValorAjusteAdm(buscarValorAjusteBens(assistente, idUnidade, grupoBem));
                }
                movUnidade.getGrupos().add(movGrupo);
                assistente.conta();
            }
        }
        return assistente;
    }

    public BigDecimal buscarValorOriginalContabil(AssistenteConsultaMovimentacaoBemContabil assistente, Long idUnidade, GrupoBem grupoBem) {
        String sql = " " +
            " select " +
            "    coalesce(sum(credito) - sum(debito),0) from (" +
            "   select case " +
            "      when bens.TIPOLANCAMENTO = :tipoLancamento " +
            "       then coalesce(bens.valor, 0) " +
            "      else coalesce(bens.valor, 0) * -1 end as credito, " +
            "      0                                     as debito " +
            "  from bensmoveis bens " +
            "   where trunc(bens.databensmoveis) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "  and bens.tipooperacaobemestoque in (:operacaoBensCredito) " +
            "  and bens.unidadeorganizacional_id = :idUnidade ";
        sql += grupoBem != null ? " and bens.grupobem_id = :idGrupo " : "";
        sql += " union all " +
            "   select " +
            "    0                                     as credito, " +
            "    case " +
            "     when bens.tipolancamento = :tipoLancamento " +
            "      then coalesce(bens.valor, 0) " +
            "    else coalesce(bens.valor, 0) * -1 end as debito " +
            " from bensmoveis bens " +
            "  where trunc(bens.databensmoveis) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "  and bens.tipooperacaobemestoque in (:operacaoBensDebito) " +
            "  and bens.unidadeorganizacional_id = :idUnidade ";
        sql += grupoBem != null ? " and bens.grupobem_id = :idGrupo " : "";
        sql += " union all " +
            "  select case " +
            "    when transf.tipolancamento = :tipoLancamento " +
            "    then coalesce(transf.valor, 0) " +
            "    else coalesce(transf.valor, 0) * -1 end as credito, " +
            "    0                                       as debito " +
            "  from transfbensmoveis transf " +
            "  where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "   and transf.operacaoorigem in (:operacaoTransfCredito) " +
            "   and transf.unidadeorigem_id = :idUnidade ";
        sql += grupoBem != null ? " and transf.grupobemorigem_id = :idGrupo " : "";
        sql += " union all " +
            "   select 0                                  as credito, " +
            "    case " +
            "     when transf.tipolancamento = :tipoLancamento " +
            "     then coalesce(transf.valor, 0) " +
            "     else coalesce(transf.valor, 0) * -1 end as debito " +
            "   from transfbensmoveis transf " +
            "   where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "     and transf.operacaodestino in (:operacaoTransfDebito) " +
            "     and transf.unidadedestino_id = :idUnidade ";
        sql += grupoBem != null ? " and transf.grupobemdestino_id = :idGrupo " : "";
        sql += " union all " +
            "  select coalesce(est.valor, 0) as credito, " +
            "         0                      as debito " +
            "   from liquidacaoestorno est " +
            "     inner join liquidacao liq on liq.id = est.liquidacao_id " +
            "     inner join empenho emp on emp.id = liq.empenho_id " +
            "     inner join desdobramento desd on desd.liquidacao_id = liq.id " +
            "     inner join conta cliq on cliq.id = desd.conta_id " +
            "     inner join configdespesabens cgb on cgb.contadespesa_id = cliq.id " +
            "     inner join grupobem grupo on grupo.id = cgb.grupobem_id " +
            "   where trunc(liq.dataliquidacao) between trunc(cgb.iniciovigencia) and coalesce(trunc(cgb.fimvigencia), liq.dataliquidacao) " +
            "     and emp.tipocontadespesa = :operacaoLiquidacao " +
            "     and trunc(est.dataestorno) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "     and liq.unidadeorganizacional_id = :idUnidade ";
        sql += grupoBem != null ? " and grupo.id = :idGrupo " : "";
        sql += " union all " +
            "  select  0                      as credito, " +
            "          coalesce(liq.valor, 0) as debito " +
            "  from liquidacao liq " +
            "    inner join empenho emp on emp.id = liq.empenho_id " +
            "    inner join desdobramento desd on desd.liquidacao_id = liq.id " +
            "    inner join conta cliq on cliq.id = desd.conta_id " +
            "    inner join configdespesabens cgb on cgb.contadespesa_id = cliq.id " +
            "    inner join grupobem grupo on grupo.id = cgb.grupobem_id " +
            "  where emp.tipocontadespesa = :operacaoLiquidacao " +
            "    and trunc(liq.dataliquidacao) between trunc(cgb.iniciovigencia) and coalesce(trunc(cgb.fimvigencia), liq.dataliquidacao) " +
            "    and trunc(liq.dataliquidacao) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')  " +
            "    and liq.unidadeorganizacional_id = :idUnidade ";
        sql += grupoBem != null ? " and grupo.id = :idGrupo " : "";
        sql += " ) dados ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("operacaoBensCredito", Util.getListOfEnumName(TipoOperacaoBensMoveis.getOperacoesBensOriginalCredito()));
        q.setParameter("operacaoBensDebito", Util.getListOfEnumName(TipoOperacaoBensMoveis.getOperacoesBensOriginalDebito()));
        q.setParameter("operacaoTransfCredito", Util.getListOfEnumName(Lists.newArrayList(TipoOperacaoBensMoveis.getOperacoesTransfOriginalCredito())));
        q.setParameter("operacaoTransfDebito", Util.getListOfEnumName(Lists.newArrayList(TipoOperacaoBensMoveis.getOperacoesTransfOriginalDebito())));
        q.setParameter("operacaoLiquidacao", TipoContaDespesa.BEM_MOVEL.name());
        q.setParameter("tipoLancamento", TipoLancamento.NORMAL.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("idUnidade", idUnidade);
        if (grupoBem != null) {
            q.setParameter("idGrupo", grupoBem.getId());
        }
        try {
            return ((BigDecimal) q.getSingleResult()).abs();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarValorAjusteContabil(AssistenteConsultaMovimentacaoBemContabil assistente, Long idUnidade, GrupoBem grupoBem) {
        String sql = "" +
            " select coalesce(sum(credito) - sum(debito),0) from ( " +
            "  select " +
            "    case when bens.tipolancamento = :tipoLancamento " +
            "         then coalesce(bens.valor, 0) " +
            "        else coalesce(bens.valor, 0) * -1 end as credito, " +
            "      0                                       as debito " +
            "      from bensmoveis bens " +
            "      where trunc(bens.databensmoveis) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and bens.tipooperacaobemestoque in (:operacaoBensCredito) " +
            "        and bens.unidadeorganizacional_id = :idUnidade ";
        sql += grupoBem != null ? " and bens.grupobem_id = :idGrupo " : "";
        sql += " union all " +
            "      select " +
            "             0                                         as credito, " +
            "             case " +
            "                 when bens.tipolancamento = :tipoLancamento " +
            "                     then coalesce(bens.valor, 0) " +
            "                 else coalesce(bens.valor, 0) * -1 end as debito " +
            "      from bensmoveis bens " +
            "      where trunc(bens.databensmoveis) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and bens.tipooperacaobemestoque in (:operacaoBensDebito) " +
            "        and bens.unidadeorganizacional_id = :idUnidade ";
        sql += grupoBem != null ? " and bens.grupobem_id = :idGrupo " : "";
        sql += " union all " +
            "      select " +
            "             case " +
            "                 when transf.tipolancamento = :tipoLancamento " +
            "                     then coalesce(transf.valor, 0) " +
            "                 else coalesce(transf.valor, 0) * -1 end as credito, " +
            "             0                                           as debito " +
            "      from transfbensmoveis transf " +
            "      where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and transf.operacaodestino in (:operacaoTransfCredito) " +
            "        and transf.unidadedestino_id = :idUnidade ";
        sql += grupoBem != null ? " and transf.grupobemdestino_id  = :idGrupo " : "";
        sql += " union all " +
            "      select  " +
            "             0                                           as credito, " +
            "             case " +
            "                 when transf.tipolancamento = :tipoLancamento " +
            "                     then coalesce(transf.valor, 0) " +
            "                 else coalesce(transf.valor, 0) * -1 end as debito " +
            "      from transfbensmoveis transf " +
            "      where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and transf.operacaoorigem in (:operacaoTransfDebito)" +
            "        and transf.unidadeorigem_id = :idUnidade ";
        sql += grupoBem != null ? " and transf.grupobemorigem_id  = :idGrupo " : "";
        sql += " ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("operacaoBensCredito", Util.getListOfEnumName(TipoOperacaoBensMoveis.getOperacoesBensAjusteCredito()));
        q.setParameter("operacaoBensDebito", Util.getListOfEnumName(TipoOperacaoBensMoveis.getOperacoesBensAjusteDebito()));
        q.setParameter("operacaoTransfCredito", Util.getListOfEnumName(Lists.newArrayList(TipoOperacaoBensMoveis.getOperacoesTransfAjusteCredito())));
        q.setParameter("operacaoTransfDebito", Util.getListOfEnumName(Lists.newArrayList(TipoOperacaoBensMoveis.getOperacoesTransfAjusteDebito())));
        q.setParameter("tipoLancamento", TipoLancamento.NORMAL.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("idUnidade", idUnidade);
        if (grupoBem != null) {
            q.setParameter("idGrupo", grupoBem.getId());
        }
        try {
            return ((BigDecimal) q.getSingleResult()).abs();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarValorOriginalBens(AssistenteConsultaMovimentacaoBemContabil assistente, Long idUnidade, GrupoBem grupoBem) {
        String sql = " " +
            " select coalesce(sum(est.valororiginal), 0) as vl_original " +
            " from eventobem ev " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            " where ev.dataoperacao = (select max(ev2.dataoperacao) " +
            "                         from eventobem ev2 " +
            "                         where ev2.bem_id = ev.bem_id " +
            "                         and trunc(ev2.datalancamento) <= to_date(:dataReferencia, 'dd/MM/yyyy')) " +
            "  and est.detentoraorcamentaria_id = :idUnidade " +
            "  and est.estadobem <> :baixado " +
            "  and ev.situacaoeventobem <> :bloqueado ";
        sql += grupoBem != null ? " and est.grupobem_id = :idGrupo " : " ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", idUnidade);
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("bloqueado", SituacaoEventoBem.BLOQUEADO.name());
        if (grupoBem != null) {
            q.setParameter("idGrupo", grupoBem.getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal buscarValorAjusteBens(AssistenteConsultaMovimentacaoBemContabil assistente, Long idUnidade, GrupoBem grupoBem) {
        String sql = " " +
            " select coalesce(sum(est.valoracumuladodadepreciacao), 0) as vl_ajuste " +
            " from eventobem ev " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            " where ev.dataoperacao = (select max(ev2.dataoperacao) " +
            "                         from eventobem ev2 " +
            "                         where ev2.bem_id = ev.bem_id " +
            "                         and trunc(ev2.datalancamento) <= to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "  and est.detentoraorcamentaria_id = :idUnidade " +
            "  and est.estadobem <> :baixado " +
            "  and ev.situacaoeventobem <> :bloqueado ";
        sql += grupoBem != null ? " and est.grupobem_id = :idGrupo " : " ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", idUnidade);
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("bloqueado", SituacaoEventoBem.BLOQUEADO.name());
        if (grupoBem != null) {
            q.setParameter("idGrupo", grupoBem.getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public List<MovimentacaoBemContabilMovimento> buscarMovimentosGrupoOriginalAdm(AssistenteConsultaMovimentacaoBemContabil assistente, UnidadeOrganizacional unidadeOrc) {
        String sql = " " +
            " select * " +
            "   from (" +
            " select coalesce(aq.id, reav.id, transf.id, ajust.id)              as id_mov, " +
            "             coalesce(aq.numero, reav.codigo, transf.codigo, ajust.codigo) as numero_mov, " +
            "             trunc(ev.datalancamento)                                      as data_mov, " +
            "             ev.tipoeventobem                                              as tipo_mov, " +
            "             coalesce(sum(ev.valordolancamento), 0)                        as vl_credito, " +
            "             0                                                             as vl_debito" +
            "      from eventobem ev " +
            "               inner join estadobem est on est.id = ev.estadoresultante_id " +
            "               left join itemaquisicaoestorno itemaq on itemaq.id = ev.id " +
            "               left join aquisicaoestorno aq on aq.id = itemaq.aquisicaoestorno_id " +
            "               left join efetivacaoreavaliacaobem itemreav on itemreav.id = ev.id " +
            "               left join loteefetivacaoreavaliacao reav on reav.id = itemreav.lote_id " +
            "               left join transferenciabemconcedida itemtransf on itemtransf.id = ev.id " +
            "               left join loteefettransfbem transf on itemtransf.efetivacaotransferencia_id = transf.id " +
            "               left join itemefetivacaoajustemovel itemajust on itemajust.id = ev.id " +
            "               left join efetivacaoajustebemmovel ajust on ajust.id = itemajust.efetivacaoajustebemmovel_id " +
            "      where trunc(ev.datalancamento) <= to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "        and est.detentoraorcamentaria_id = :idUnidade " +
            "        and est.grupobem_id = :idGrupo " +
            "        and ev.valordolancamento > 0 " +
            "        and ev.tipooperacao = :operacaoCredito " +
            "        and ev.tipoeventobem in (:tiposEventosCredito) " +
            "        and not exists(select 1 " +
            "                       from eventobem ev1 " +
            "                                inner join estadobem est1 on est1.id = ev1.estadoresultante_id " +
            "                       where ev1.tipoeventobem = :tipoEvBaixa " +
            "                         and ev1.bem_id = ev.bem_id) " +
            "      group by coalesce(aq.id, reav.id, transf.id, ajust.id), " +
            "               coalesce(aq.numero, reav.codigo, transf.codigo, ajust.codigo), " +
            "               trunc(ev.datalancamento), ev.tipoeventobem, 0 " +
            "      union all " +
            "      select coalesce(aq.id, lev.id, incorp.id, transf.id, reav.id, ajust.id)      as id_mov, " +
            "             coalesce(aq.numero, lev.codigo, incorp.codigo, transf.codigo, " +
            "                      reav.codigo, ajust.codigo)                                   as numero_mov, " +
            "             trunc(ev.datalancamento)                                              as data_mov, " +
            "             ev.tipoeventobem                                                      as tipo_mov, " +
            "             0                                                                     as vl_credito, " +
            "             coalesce(sum(ev.valordolancamento), 0)                                as vl_debito " +
            "      from eventobem ev " +
            "               inner join estadobem est on est.id = ev.estadoresultante_id " +
            "               left join itemaquisicao itemaq on itemaq.id = ev.id " +
            "               left join aquisicao aq on aq.id = itemaq.aquisicao_id " +
            "               left join efetivacaolevantamentobem itemlev on itemlev.id = ev.id " +
            "               left join loteefetlevantbem lev on itemlev.lote_id = lev.id " +
            "               left join itemefetivacaoincorpmovel iteminc on iteminc.id = ev.id " +
            "               left join efetsoliciincorpomovel incorp on iteminc.efetivacao_id = incorp.id " +
            "               left join efetivacaotransferenciabem itemtransf on itemtransf.ID = ev.id " +
            "               left join loteefettransfbem transf on itemtransf.lote_id = transf.id " +
            "               left join efetivacaoreavaliacaobem itemreav on itemreav.id = ev.id " +
            "               left join loteefetivacaoreavaliacao reav on itemreav.lote_id = reav.id " +
            "               left join itemefetivacaoajustemovel itemajust on itemajust.id = ev.id " +
            "               left join efetivacaoajustebemmovel ajust on ajust.id = itemajust.efetivacaoajustebemmovel_id " +
            "      where trunc(ev.datalancamento) <= to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "        and est.detentoraorcamentaria_id = :idUnidade " +
            "        and est.grupobem_id = :idGrupo " +
            "        and ev.valordolancamento > 0 " +
            "        and ev.tipooperacao = :operacaoDebito " +
            "        and ev.tipoeventobem in (:tiposEventosDebito) " +
            "        and not exists(select 1 " +
            "                       from eventobem ev1 " +
            "                                inner join estadobem est1 on est1.id = ev1.estadoresultante_id " +
            "                       where ev1.tipoeventobem = :tipoEvBaixa " +
            "                         and ev1.bem_id = ev.bem_id) " +
            "      group by coalesce(aq.id, lev.id, incorp.id, transf.id, reav.id, ajust.id), " +
            "               coalesce(aq.numero, lev.codigo, incorp.codigo, transf.codigo, reav.codigo, ajust.codigo), " +
            "               trunc(ev.datalancamento), ev.tipoeventobem, 0) " +
            " order by id_mov ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", unidadeOrc.getId());
        q.setParameter("idGrupo", assistente.getMovGrupoSelecionado().getGrupoBem().getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("operacaoCredito", TipoOperacao.CREDITO.name());
        q.setParameter("operacaoDebito", TipoOperacao.DEBITO.name());
        q.setParameter("tipoEvBaixa", TipoEventoBem.ITEMEFETIVACAOBAIXAPATRIMONIAL.name());
        q.setParameter("tiposEventosCredito", Util.getListOfEnumName(TipoEventoBem.getTiposEventosCreditoOriginal()));
        q.setParameter("tiposEventosDebito", Util.getListOfEnumName(TipoEventoBem.getTiposEventosDebitoOriginal()));
        List<Object[]> resultado = q.getResultList();

        List<MovimentacaoBemContabilMovimento> movimentos = Lists.newArrayList();
        for (Object[] obj : resultado) {
            MovimentacaoBemContabilMovimento mov = new MovimentacaoBemContabilMovimento();
            mov.setId(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
            mov.setNumero(obj[1] != null ? ((BigDecimal) obj[1]).toString() : null);
            mov.setData((Date) obj[2]);
            mov.setTipoMovimento(MovimentacaoBemContabilMovimento.TipoMovimentoBemContabil.valueOf((String) obj[3]));
            mov.setCredito(((BigDecimal) obj[4]).abs());
            mov.setDebito(((BigDecimal) obj[5]).abs());
            movimentos.add(mov);
        }
        return movimentos;
    }

    public List<MovimentacaoBemContabilMovimento> buscarMovimentosGrupoAjusteAdm(AssistenteConsultaMovimentacaoBemContabil assistente, UnidadeOrganizacional unidadeOrc) {
        String sql = " " +
            " select * " +
            "  from (select coalesce(lev.id, red.id, transf.id, ajust.id)             as id_mov, " +
            "             coalesce(lev.codigo, red.codigo, transf.codigo, ajust.codigo)  as numero_mov, " +
            "             trunc(ev.datalancamento)                                    as data_mov, " +
            "             ev.tipoeventobem                                            as tipo_mov, " +
            "             coalesce(sum(ev.valordolancamento), 0)                      as vl_credito, " +
            "             0                                                           as vl_debito " +
            "      from eventobem ev " +
            "               inner join estadobem est on est.id = ev.estadoresultante_id " +
            "               left join efetivacaodepreciacaobem itemlev on itemlev.id = ev.id " +
            "               left join efetivacaolevantamentobem efetlev on efetlev.id = itemlev.efetivacaolevantamentobem_id " +
            "               left join loteefetlevantbem lev on lev.id = efetlev.lote_id " +
            "               left join reducaovalorbem itemred on itemred.id = ev.id " +
            "               left join lotereducaovalorbem red on red.id = itemred.lotereducaovalorbem_id " +
            "               left join transferenciadepreciacarec itemtransfrec on itemtransfrec.id = ev.id " +
            "               left join loteefettransfbem transf on itemtransfrec.efetivacaotransferencia_id = transf.id " +
            "               left join itemefetivacaoajustemovel itemajust on itemajust.id = ev.id  " +
            "               left join efetivacaoajustebemmovel ajust on ajust.id = itemajust.efetivacaoajustebemmovel_id " +
            "      where trunc(ev.datalancamento) <= to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "        and est.detentoraorcamentaria_id = :idUnidade " +
            "        and est.grupobem_id = :idGrupo " +
            "        and ev.valordolancamento > 0 " +
            "        and ev.tipooperacao = :operacaoCredito " +
            "        and ev.tipoeventobem in (:tiposEventosCredito) " +
            "        and not exists(select 1 " +
            "                       from eventobem ev1 " +
            "                                inner join estadobem est1 on est1.id = ev1.estadoresultante_id " +
            "                       where ev1.tipoeventobem = :tipoEvBaixa " +
            "                         and ev1.bem_id = ev.bem_id) " +
            "      group by coalesce(lev.id, red.id, transf.id, ajust.id), " +
            "               coalesce(lev.codigo, red.codigo, transf.codigo, ajust.codigo), " +
            "               trunc(ev.datalancamento), ev.tipoeventobem, 0 " +
            "      union all " +
            "      select coalesce(red.id, transf.id, ajust.id)             as id_mov, " +
            "             coalesce(red.codigo, transf.codigo, ajust.codigo) as numero_mov, " +
            "             trunc(ev.datalancamento)                          as data_mov, " +
            "             ev.tipoeventobem                                  as tipo_mov, " +
            "             0                                                 as vl_credito, " +
            "             coalesce(sum(ev.valordolancamento), 0)            as vl_debito " +
            "      from eventobem ev " +
            "               inner join estadobem est on est.id = ev.estadoresultante_id " +
            "               left join estornoreducaovalorbem itemest on itemest.id = ev.id " +
            "               left join loteestornoreducaovalorbem est on itemest.loteestornoreducaovalorbem_id = est.id " +
            "               left join lotereducaovalorbem red on red.id = est.lotereducaovalorbem_id " +
            "               left join transferenciadeprconcedida itemtransfconc on itemtransfconc.id = ev.id " +
            "               left join loteefettransfbem transf on itemtransfconc.efetivacaotransferencia_id = transf.id " +
            "               left join itemefetivacaoajustemovel itemajust on itemajust.id = ev.id  " +
            "               left join efetivacaoajustebemmovel ajust on ajust.id = itemajust.efetivacaoajustebemmovel_id " +
            "      where trunc(ev.datalancamento) <= to_date(:dataReferencia, 'dd/MM/yyyy') " +
            "        and est.detentoraorcamentaria_id = :idUnidade " +
            "        and est.grupobem_id = :idGrupo " +
            "        and ev.valordolancamento > 0 " +
            "        and ev.tipooperacao = :operacaoDebito " +
            "        and ev.tipoeventobem in (:tiposEventosDebito) " +
            "        and not exists(select 1 " +
            "                       from eventobem ev1 " +
            "                                inner join estadobem est1 on est1.id = ev1.estadoresultante_id " +
            "                       where ev1.tipoeventobem = :tipoEvBaixa " +
            "                         and ev1.bem_id = ev.bem_id) " +
            "      group by coalesce(red.id, transf.id, ajust.id), coalesce(red.codigo, transf.codigo, ajust.codigo), " +
            "              trunc(ev.datalancamento), ev.tipoeventobem, 0" +
            ") " +
            " order by data_mov ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", unidadeOrc.getId());
        q.setParameter("idGrupo", assistente.getMovGrupoSelecionado().getGrupoBem().getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("tipoEvBaixa", TipoEventoBem.ITEMEFETIVACAOBAIXAPATRIMONIAL.name());
        q.setParameter("operacaoCredito", TipoOperacao.CREDITO.name());
        q.setParameter("operacaoDebito", TipoOperacao.DEBITO.name());
        q.setParameter("tiposEventosCredito", Util.getListOfEnumName(TipoEventoBem.getTiposEventosCreditoAjuste()));
        q.setParameter("tiposEventosDebito", Util.getListOfEnumName(TipoEventoBem.getTiposEventosDebitoAjuste()));
        List<Object[]> resultado = q.getResultList();

        List<MovimentacaoBemContabilMovimento> movimentos = Lists.newArrayList();
        for (Object[] obj : resultado) {
            MovimentacaoBemContabilMovimento mov = new MovimentacaoBemContabilMovimento();
            mov.setId(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
            mov.setNumero(obj[0] != null ? ((BigDecimal) obj[1]).toString() : null);
            mov.setData((Date) obj[2]);
            mov.setTipoMovimento(MovimentacaoBemContabilMovimento.TipoMovimentoBemContabil.valueOf((String) obj[3]));
            mov.setCredito(((BigDecimal) obj[4]).abs());
            mov.setDebito(((BigDecimal) obj[5]).abs());
            movimentos.add(mov);
        }
        return movimentos;
    }

    public List<MovimentacaoBemContabilMovimento> buscarMovimentosGrupoOriginalContabil(AssistenteConsultaMovimentacaoBemContabil assistente, UnidadeOrganizacional unidadeOrc) {
        String sql = " " +
            " select " +
            "   id_mov," +
            "   numero_mov, " +
            "   data_mov, " +
            "   tipo_mov, " +
            "   categoria_orc, " +
            "   tipo_lanc, " +
            "   operacao, " +
            "   coalesce(sum(credito),0) as credito, " +
            "   coalesce(sum(debito),0) as debito " +
            " from ( " +
            "  select " +
            "   bens.id                                   as id_mov, " +
            "   bens.numero                               as numero_mov, " +
            "   bens.databensmoveis                       as data_mov, " +
            "   'BENS_MOVEIS'                             as tipo_mov," +
            "   'NORMAL'                                  as categoria_orc," +
            "   bens.tipolancamento                       as tipo_lanc," +
            "   bens.tipooperacaobemestoque               as operacao, " +
            "    case " +
            "      when bens.tipolancamento = :lancNormal " +
            "       then coalesce(bens.valor, 0) " +
            "      else 0 end                            as credito, " +
            "    case " +
            "      when bens.tipolancamento = :lancEstorno " +
            "       then coalesce(bens.valor, 0) " +
            "      else 0 end                            as debito " +
            "  from bensmoveis bens " +
            "   where trunc(bens.databensmoveis) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "    and bens.tipooperacaobemestoque in (:operacaoBensCredito) " +
            "    and bens.unidadeorganizacional_id = :idUnidade " +
            "    and bens.grupobem_id = :idGrupo " +
            " union all " +
            " select " +
            "   bens.id                                   as id_mov, " +
            "   bens.numero                               as numero_mov, " +
            "   bens.databensmoveis                       as data_mov, " +
            "   'BENS_MOVEIS'                             as tipo_mov, " +
            "   'NORMAL'                                  as categoria_orc," +
            "   bens.tipolancamento                       as tipo_lanc," +
            "   bens.tipooperacaobemestoque               as operacao, " +
            "    case " +
            "     when bens.tipolancamento = :lancEstorno " +
            "      then coalesce(bens.valor, 0) " +
            "    else 0 end                               as credito, " +
            "    case " +
            "     when bens.tipolancamento = :lancNormal " +
            "      then coalesce(bens.valor, 0) " +
            "    else 0 end                               as debito " +
            " from bensmoveis bens " +
            "  where trunc(bens.databensmoveis) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "   and bens.tipooperacaobemestoque in (:operacaoBensDebito) " +
            "   and bens.unidadeorganizacional_id = :idUnidade " +
            "   and bens.grupobem_id = :idGrupo " +
            " union all " +
            "  select " +
            "   transf.id                                 as id_mov, " +
            "   transf.numero                             as numero_mov, " +
            "   transf.datatransferencia                  as data_mov, " +
            "  'TRANSF_BENS_MOVEIS'                       as tipo_mov," +
            "   'NORMAL'                                  as categoria_orc," +
            "   transf.tipolancamento                     as tipo_lanc," +
            "   transf.operacaoorigem                     as operacao, " +
            "   case " +
            "    when transf.tipolancamento = :lancNormal " +
            "    then coalesce(transf.valor, 0) " +
            "    else 0 end                               as credito, " +
            "   case " +
            "    when transf.tipolancamento = :lancEstorno " +
            "    then coalesce(transf.valor, 0) " +
            "    else 0 end                               as debito " +
            "  from transfbensmoveis transf " +
            "  where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "   and transf.operacaoorigem in (:operacaoTransfCredito) " +
            "   and transf.unidadeorigem_id = :idUnidade " +
            "   and transf.grupobemorigem_id = :idGrupo " +
            " union all " +
            "  select " +
            "   transf.id                                  as id_mov, " +
            "   transf.numero                              as numero_mov, " +
            "   transf.datatransferencia                   as data_mov, " +
            "   'TRANSF_BENS_MOVEIS'                       as tipo_mov," +
            "   'NORMAL'                                   as categoria_orc," +
            "   transf.tipolancamento                      as tipo_lanc," +
            "   transf.operacaodestino                     as operacao, " +
            "    case " +
            "     when transf.tipolancamento = :lancEstorno " +
            "     then coalesce(transf.valor, 0) " +
            "     else 0 end                               as credito, " +
            "    case " +
            "     when transf.tipolancamento = :lancNormal " +
            "     then coalesce(transf.valor, 0) " +
            "     else 0 end                                as debito " +
            "   from transfbensmoveis transf " +
            "   where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "     and transf.operacaodestino in (:operacaoTransfDebito) " +
            "     and transf.unidadedestino_id = :idUnidade " +
            "     and transf.grupobemdestino_id = :idGrupo " +
            " union all " +
            "  select distinct " +
            "    est.id                   as id_mov, " +
            "    est.numero               as numero_mov, " +
            "    est.dataestorno          as data_mov, " +
            "   'LIQUIDACAO_ESTORNO'      as tipo_mov," +
            "   liq.categoriaorcamentaria as categoria_orc," +
            "   'NORMAL'                  as tipo_lanc," +
            "   'AQUISICAO_BENS_MOVEIS'   as operacao, " +
            "    coalesce(est.valor, 0)   as credito, " +
            "    0                        as debito " +
            "   from liquidacaoestorno est " +
            "     inner join liquidacao liq on liq.id = est.liquidacao_id " +
            "     inner join empenho emp on emp.id = liq.empenho_id " +
            "     inner join desdobramento desd on desd.liquidacao_id = liq.id " +
            "     inner join conta cliq on cliq.id = desd.conta_id " +
            "     inner join configdespesabens cgb on cgb.contadespesa_id = cliq.id " +
            "     inner join grupobem grupo on grupo.id = cgb.grupobem_id " +
            "   where trunc(liq.dataliquidacao) between trunc(cgb.iniciovigencia) and coalesce(trunc(cgb.fimvigencia), liq.dataliquidacao) " +
            "     and emp.tipocontadespesa = :operacaoLiquidacao " +
            "     and trunc(est.dataestorno) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "     and liq.unidadeorganizacional_id = :idUnidade " +
            "     and grupo.id = :idGrupo " +
            " union all " +
            "  select distinct " +
            "   liq.id                      as id_mov, " +
            "   liq.numero                  as numero_mov, " +
            "   liq.dataliquidacao          as data_mov, " +
            "   'LIQUIDACAO'                as tipo_mov,  " +
            "   liq.categoriaorcamentaria   as categoria_orc," +
            "   'ESTORNO'                   as tipo_lanc," +
            "   'AQUISICAO_BENS_MOVEIS'     as operacao, " +
            "   0                           as credito, " +
            "   coalesce(liq.valor, 0)      as debito " +
            "  from liquidacao liq " +
            "    inner join empenho emp on emp.id = liq.empenho_id " +
            "    inner join desdobramento desd on desd.liquidacao_id = liq.id " +
            "    inner join conta cliq on cliq.id = desd.conta_id " +
            "    inner join configdespesabens cgb on cgb.contadespesa_id = cliq.id " +
            "    inner join grupobem grupo on grupo.id = cgb.grupobem_id " +
            "  where emp.tipocontadespesa = :operacaoLiquidacao " +
            "    and trunc(liq.dataliquidacao) between trunc(cgb.iniciovigencia) and coalesce(trunc(cgb.fimvigencia), liq.dataliquidacao) " +
            "    and trunc(liq.dataliquidacao) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy')  " +
            "    and liq.unidadeorganizacional_id = :idUnidade " +
            "    and grupo.id = :idGrupo " +
            " ) group by id_mov, numero_mov, data_mov, tipo_mov, categoria_orc, tipo_lanc, operacao " +
            "   order by id_mov ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("operacaoBensCredito", Util.getListOfEnumName(TipoOperacaoBensMoveis.getOperacoesBensOriginalCredito()));
        q.setParameter("operacaoBensDebito", Util.getListOfEnumName(TipoOperacaoBensMoveis.getOperacoesBensOriginalDebito()));
        q.setParameter("operacaoTransfCredito", Util.getListOfEnumName(Lists.newArrayList(TipoOperacaoBensMoveis.getOperacoesTransfOriginalCredito())));
        q.setParameter("operacaoTransfDebito", Util.getListOfEnumName(Lists.newArrayList(TipoOperacaoBensMoveis.getOperacoesTransfOriginalDebito())));
        q.setParameter("operacaoLiquidacao", TipoContaDespesa.BEM_MOVEL.name());
        q.setParameter("lancNormal", TipoLancamento.NORMAL.name());
        q.setParameter("lancEstorno", TipoLancamento.ESTORNO.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("idUnidade", unidadeOrc.getId());
        q.setParameter("idGrupo", assistente.getMovGrupoSelecionado().getGrupoBem().getId());
        List<Object[]> resultado = q.getResultList();

        List<MovimentacaoBemContabilMovimento> movimentos = Lists.newArrayList();
        for (Object[] obj : resultado) {
            MovimentacaoBemContabilMovimento mov = new MovimentacaoBemContabilMovimento();
            mov.setId(((BigDecimal) obj[0]).longValue());
            mov.setNumero((String) obj[1]);
            mov.setData((Date) obj[2]);
            mov.setTipoMovimento(MovimentacaoBemContabilMovimento.TipoMovimentoBemContabil.valueOf((String) obj[3]));
            mov.setCategoriaOrcamentaria(CategoriaOrcamentaria.valueOf((String) obj[4]));
            mov.setTipoLancamento(TipoLancamento.valueOf((String) obj[5]));
            mov.setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis.valueOf((String) obj[6]));
            mov.setCredito(((BigDecimal) obj[7]).abs());
            mov.setDebito(((BigDecimal) obj[8]).abs());
            movimentos.add(mov);
        }
        return movimentos;
    }

    public List<MovimentacaoBemContabilMovimento> buscarMovimentosGrupoAjusteContabil(AssistenteConsultaMovimentacaoBemContabil assistente, UnidadeOrganizacional unidadeOrc) {
        String sql = " " +
            " select " +
            "   id_mov, " +
            "   numero_mov, " +
            "   data_mov, " +
            "   tipo_mov, " +
            "   tipo_lanc, " +
            "   operacao, " +
            "   coalesce(sum(credito),0), " +
            "   coalesce(sum(debito),0) " +
            " from ( " +
            "  select " +
            "    bens.id                                   as id_mov, " +
            "    bens.numero                               as numero_mov, " +
            "    bens.databensmoveis                       as data_mov, " +
            "    'BENS_MOVEIS'                             as tipo_mov, " +
            "    bens.tipolancamento                       as tipo_lanc, " +
            "    bens.tipooperacaobemestoque               as operacao, " +
            "    case when bens.tipolancamento = :lancNormal " +
            "        then coalesce(bens.valor, 0) " +
            "        else 0 end                            as credito, " +
            "    case when bens.tipolancamento = :lancEstorno " +
            "        then coalesce(bens.valor, 0) " +
            "        else 0 end                            as debito " +
            "      from bensmoveis bens " +
            "      where trunc(bens.databensmoveis) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and bens.tipooperacaobemestoque in (:operacaoBensCredito) " +
            "        and bens.unidadeorganizacional_id = :idUnidade " +
            "        and bens.grupobem_id = :idGrupo " +
            "      union all " +
            "      select bens.id                                   as id_mov, " +
            "             bens.numero                               as numero_mov, " +
            "             bens.databensmoveis                       as data_mov, " +
            "             'BENS_MOVEIS'                             as tipo_mov, " +
            "             bens.tipolancamento                       as tipo_lanc, " +
            "             bens.tipooperacaobemestoque               as operacao, " +
            "             case " +
            "                 when bens.tipolancamento = :lancEstorno " +
            "                     then coalesce(bens.valor, 0) " +
            "                 else 0 end                            as credito, " +
            "             case " +
            "                 when bens.tipolancamento = :lancNormal " +
            "                     then coalesce(bens.valor, 0) " +
            "                 else 0 end                            as debito " +
            "      from bensmoveis bens " +
            "      where trunc(bens.databensmoveis) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and bens.tipooperacaobemestoque in (:operacaoBensDebito) " +
            "        and bens.unidadeorganizacional_id = :idUnidade " +
            "        and bens.grupobem_id = :idGrupo " +
            "      union all " +
            "      select transf.id                                   as id_mov, " +
            "             transf.numero                               as numero_mov, " +
            "             transf.datatransferencia                    as data_mov, " +
            "             'TRANSF_BENS_MOVEIS'                        as tipo_mov, " +
            "             transf.tipolancamento                       as tipo_lanc, " +
            "             transf.operacaodestino                      as operacao, " +
            "             case " +
            "                 when transf.tipolancamento = :lancNormal " +
            "                     then coalesce(transf.valor, 0) " +
            "                 else 0 end                              as credito, " +
            "             case " +
            "                 when transf.tipolancamento = :lancEstorno " +
            "                     then coalesce(transf.valor, 0) " +
            "                 else 0 end                              as debito " +
            "      from transfbensmoveis transf " +
            "      where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and transf.operacaodestino in (:operacaoTransfCredito) " +
            "        and transf.unidadedestino_id = :idUnidade " +
            "        and transf.grupobemdestino_id = :idGrupo " +
            "      union all " +
            "      select transf.id                                   as id_mov, " +
            "             transf.numero                               as numero_mov, " +
            "             transf.datatransferencia                    as data_mov, " +
            "             'TRANSF_BENS_MOVEIS'                        as tipo_mov, " +
            "             transf.tipolancamento                       as tipo_lanc, " +
            "             transf.operacaoorigem                       as operacao, " +
            "             case " +
            "                 when transf.tipolancamento = :lancEstorno " +
            "                     then coalesce(transf.valor, 0) " +
            "                 else 0 end                               as debito, " +
            "             case " +
            "                 when transf.tipolancamento = :lancNormal " +
            "                     then coalesce(transf.valor, 0) " +
            "                 else 0 end                               as debito " +
            "      from transfbensmoveis transf " +
            "      where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and transf.unidadeorigem_id = :idUnidade " +
            "        and transf.grupobemorigem_id = :idGrupo " +
            "        and transf.operacaoorigem in (:operacaoTransfDebito) " +
            " ) group by id_mov, numero_mov, data_mov, tipo_mov, tipo_lanc, operacao " +
            "   order by id_mov ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("operacaoBensCredito", Util.getListOfEnumName(TipoOperacaoBensMoveis.getOperacoesBensAjusteCredito()));
        q.setParameter("operacaoBensDebito", Util.getListOfEnumName(TipoOperacaoBensMoveis.getOperacoesBensAjusteDebito()));
        q.setParameter("operacaoTransfCredito", Util.getListOfEnumName(Lists.newArrayList(TipoOperacaoBensMoveis.getOperacoesTransfAjusteCredito())));
        q.setParameter("operacaoTransfDebito", Util.getListOfEnumName(Lists.newArrayList(TipoOperacaoBensMoveis.getOperacoesTransfAjusteDebito())));
        q.setParameter("lancNormal", TipoLancamento.NORMAL.name());
        q.setParameter("lancEstorno", TipoLancamento.ESTORNO.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("idUnidade", unidadeOrc.getId());
        q.setParameter("idGrupo", assistente.getMovGrupoSelecionado().getGrupoBem().getId());
        List<Object[]> resultado = q.getResultList();

        List<MovimentacaoBemContabilMovimento> movimentos = Lists.newArrayList();
        for (Object[] obj : resultado) {
            MovimentacaoBemContabilMovimento mov = new MovimentacaoBemContabilMovimento();
            mov.setId(((BigDecimal) obj[0]).longValue());
            mov.setNumero((String) obj[1]);
            mov.setData((Date) obj[2]);
            mov.setTipoMovimento(MovimentacaoBemContabilMovimento.TipoMovimentoBemContabil.valueOf((String) obj[3]));
            mov.setTipoLancamento(TipoLancamento.valueOf((String) obj[4]));
            mov.setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis.valueOf((String) obj[5]));
            mov.setCredito(((BigDecimal) obj[6]).abs());
            mov.setDebito(((BigDecimal) obj[7]).abs());
            movimentos.add(mov);
        }
        return movimentos;
    }

    public BigDecimal buscarSaldoGrupo(GrupoBem grupoBem, NaturezaTipoGrupoBem natureza, UnidadeOrganizacional unidadeOrc, Date dataReferencia) {
        return saldoGrupoBemMovelFacade.recuperarUltimoSaldoGrupoBem(grupoBem, unidadeOrc, dataReferencia, natureza).abs();
    }

    public BigDecimal buscarSaldoContabil(UnidadeOrganizacional unidadeOrc, Conta contaContabil, Date dataInicial, Date dataFinal) {
        return saldoContaContabilFacade.buscarSaldoAtualContaAnalitica(dataInicial, dataFinal, contaContabil, unidadeOrc).abs();

    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SaldoGrupoBemMovelFacade getSaldoGrupoBemMovelFacade() {
        return saldoGrupoBemMovelFacade;
    }
}
