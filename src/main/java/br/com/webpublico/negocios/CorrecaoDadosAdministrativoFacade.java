package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AlteracaoContratual;
import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.ItemRequisicaoCompraExecucao;
import br.com.webpublico.entidades.MovimentoAlteracaoContratual;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.singletons.SingletonConcorrenciaMaterial;
import br.com.webpublico.singletons.SingletonContrato;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class CorrecaoDadosAdministrativoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LoteEfetivacaoTransferenciaBemFacade loteEfetivacaoTransferenciaBemFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private SingletonContrato singletonContrato;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;
    @EJB
    private SingletonConcorrenciaMaterial singletonConcorrenciaMaterial;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;

    @Asynchronous
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public Future<CorrecaoDadosAdministrativoVO> reprocessarContrato(CorrecaoDadosAdministrativoVO selecionado) {

        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        selecionado.setAssistenteBarraProgresso(assistente);
        assistente.setDescricaoProcesso("Buscando Contratos...");

        List<Contrato> contratos = buscarTodosContratos();

        assistente.setDescricaoProcesso("Reprocessando Contratos...  " + contratos.size() + " de " + countContratos());
        assistente.setTotal(contratos.size());

        reprocessamentoSaldoContratoFacade.gerarSaldoAndMovimentoItemContrato(assistente, selecionado.getDataOperacao(), contratos);
        assistente.setDescricaoProcesso("Finalizando Processo...");
        assistente.zerarContadoresProcesso();
        return new AsyncResult<>(selecionado);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<CorrecaoDadosAdministrativoVO> buscarRequisicaoCompra(CorrecaoDadosAdministrativoVO selecionado) {
        AssistenteBarraProgresso assistente = selecionado.getAssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Recuperando requisições de compra...");

        String sql = " select req.id, req.numero, req.tipoobjetocompra " +
            "           from requisicaodecompra req " +
            "          where req.tiporequisicao = :tipoReq " +
            "          and req.reprocessada = 0 ";
        sql += selecionado.getRequisicaoDeCompra() != null ? " and req.id = :idReq " : " ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoReq", selecionado.getTipoRequisicaoCompra().name());
        if (selecionado.getRequisicaoDeCompra() != null) {
            q.setParameter("idReq", selecionado.getRequisicaoDeCompra().getId());
        }
        q.setMaxResults(3000);
        List<Object[]> resultado = q.getResultList();

        assistente.setTotal(resultado.size());
        for (Object[] obj : resultado) {
            RequisicaoCompraCorrecaoDados novaReqVO = new RequisicaoCompraCorrecaoDados();
            novaReqVO.setIdRequisicao(((BigDecimal) obj[0]).longValue());
            novaReqVO.setNumeroRequisicao(((BigDecimal) obj[1]).longValue());
            novaReqVO.setTipoObjetoCompra(TipoObjetoCompra.valueOf((String) obj[2]));
            novaReqVO.setExecucoes(buscarRequisicaoExecucaoSemEmpenho(novaReqVO, selecionado));
            selecionado.getRequisicoesCompra().add(novaReqVO);
            assistente.conta();
        }
        return new AsyncResult<CorrecaoDadosAdministrativoVO>(selecionado);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<CorrecaoDadosAdministrativoVO> atribuirEmpenhoRequisicaoCompraComUnicoEmpenho(CorrecaoDadosAdministrativoVO selecionado) {

        AssistenteBarraProgresso assistente = selecionado.getAssistenteBarraProgresso();
        assistente.zerarContadoresProcesso();
        assistente.setTotal(selecionado.getRequisicoesCompra().size());
        assistente.setDescricaoProcesso("Atribuíndo empenho na requisição de compra...");

        for (RequisicaoCompraCorrecaoDados req : selecionado.getRequisicoesCompra()) {
            for (RequisicaoCompraExecucaoCorrecaoDados reqExec : req.getExecucoes()) {
                if (CorrecaoDadosAdministrativoVO.TipoAlteracaoDados.INSERT.equals(reqExec.getTipoAlteracaoDados())) {
                    for (RequisicaoCompraExecucaoItemCorrecaoDados itemVO : reqExec.getItens()) {
                        executarUpdateItemRequisicaoCompraExecucao(itemVO.getIdEmpenho(), itemVO, selecionado.getTipoRequisicaoCompra());
                    }
                }
                executarUpdateRequisicaoCompraExecucao(reqExec.getIdRequisicaoCompraExecucao(), reqExec.getIdExecucaoEmpenho(),
                    req.getIdRequisicao(), selecionado.getTipoRequisicaoCompra(), reqExec.getTipoAlteracaoDados());
            }
            executarUpdateRequisicaoCompra(req.getIdRequisicao());
            assistente.conta();
        }
        return new AsyncResult<CorrecaoDadosAdministrativoVO>(selecionado);
    }

    public List<RequisicaoCompraExecucaoCorrecaoDados> buscarRequisicaoExecucaoSemEmpenho(RequisicaoCompraCorrecaoDados reqVO, CorrecaoDadosAdministrativoVO selecionado) {
        String sql = " ";
        if (selecionado.getTipoRequisicaoCompra().isContrato()) {
            sql += " select rce.id as id_rce, " +
                "           ex.id as id_exec," +
                "           ex.numero as numero_exec " +
                "    from requisicaocompraexecucao rce " +
                "     inner join execucaocontrato ex on ex.id = rce.execucaocontrato_id " +
                "     where rce.requisicaocompra_id = :idRequisicao ";
        } else if (selecionado.getTipoRequisicaoCompra().isExecucaoProcesso()) {
            sql += " select rce.id as id_rce, " +
                "           ex.id as id_exec," +
                "           ex.numero as numero_exec " +
                "     from requisicaocompraexecucao rce  " +
                "       inner join execucaoprocesso ex on ex.id = rce.execucaoprocesso_id " +
                "   where rce.requisicaocompra_id = :idRequisicao ";
        } else if (selecionado.getTipoRequisicaoCompra().isReconhecimentoDivida()) {
            sql += " select rd.id as id_rce, " +
                "           rd.id as id_exec," +
                "           to_number(rd.numero) as numero_exec " +
                "    from reconhecimentodivida rd " +
                "      inner join requisicaodecompra req on req.reconhecimentodivida_id = rd.id " +
                "   where req.id = :idRequisicao ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", reqVO.getIdRequisicao());
        if (Util.isListNullOrEmpty(q.getResultList())) {
            return Lists.newArrayList();
        }

        List<RequisicaoCompraExecucaoCorrecaoDados> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            RequisicaoCompraExecucaoCorrecaoDados reqExecVO = new RequisicaoCompraExecucaoCorrecaoDados();
            reqExecVO.setIdRequisicao(reqVO.getIdRequisicao());
            reqExecVO.setTipoObjetoCompra(reqVO.getTipoObjetoCompra());
            reqExecVO.setIdRequisicaoCompraExecucao(((BigDecimal) obj[0]).longValue());
            reqExecVO.setIdExecucao(((BigDecimal) obj[1]).longValue());
            reqExecVO.setNumeroExecucao(((BigDecimal) obj[2]).longValue());
            reqExecVO.setTipoAlteracaoDados(CorrecaoDadosAdministrativoVO.TipoAlteracaoDados.DEPRECATE);
            reqExecVO.setEmpenhos(buscarRequisicaoExecucaoEmpenho(reqExecVO.getIdRequisicaoCompraExecucao(), reqVO.getTipoObjetoCompra(), selecionado.getTipoRequisicaoCompra()));

            Set<Long> idsDistintosGrupoItem = Sets.newHashSet();
            reqExecVO.setItens(criarRequisicaoCompraExecucaoItemCorrecaoDados(reqExecVO, reqExecVO.getEmpenhos(), selecionado.getTipoRequisicaoCompra()));
            reqExecVO.getItens().forEach(i -> idsDistintosGrupoItem.add(i.getIdGrupo()));
            toReturn.add(reqExecVO);

            List<RequisicaoCompraExecucaoCorrecaoDados> reqExecInsert = Lists.newArrayList();
            for (RequisicaoCompraEmpenhoCorrecaoDados exeEmpVO : reqExecVO.getEmpenhos()) {
                if (reqExecVO.getTipoObjetoCompra().isPermanenteOrConsumo()) {
                    if (idsDistintosGrupoItem.contains(exeEmpVO.getIdGrupoEmpenho())) {

                        RequisicaoCompraExecucaoCorrecaoDados cloneReqExecVO = (RequisicaoCompraExecucaoCorrecaoDados) Util.clonarObjeto(reqExecVO);
                        cloneReqExecVO.setIdRequisicaoCompraExecucao(null);
                        cloneReqExecVO.setTipoAlteracaoDados(CorrecaoDadosAdministrativoVO.TipoAlteracaoDados.INSERT);
                        cloneReqExecVO.setIdExecucaoEmpenho(exeEmpVO.getIdExecucaoEmpenho());
                        reqExecInsert.add(cloneReqExecVO);
                        toReturn.add(cloneReqExecVO);
                    }
                }
            }
            if (Util.isListNullOrEmpty(reqExecInsert)) {
                for (RequisicaoCompraEmpenhoCorrecaoDados empVO : reqExecVO.getEmpenhos()) {
                    RequisicaoCompraExecucaoCorrecaoDados cloneReqExecVO = (RequisicaoCompraExecucaoCorrecaoDados) Util.clonarEmNiveis(reqExecVO, 1);
                    cloneReqExecVO.setIdRequisicaoCompraExecucao(null);
                    cloneReqExecVO.setTipoAlteracaoDados(CorrecaoDadosAdministrativoVO.TipoAlteracaoDados.INSERT);
                    cloneReqExecVO.setIdExecucaoEmpenho(empVO.getIdExecucaoEmpenho());

                    for (RequisicaoCompraExecucaoItemCorrecaoDados i : cloneReqExecVO.getItens()) {
                        if (i.getIdEmpenho() == null) {
                            i.setIdEmpenho(empVO.getIdEmpenho());
                            i.setNumeroEmpenho(empVO.getNumeroEmpenho());
                        }
                    }
                    toReturn.add(cloneReqExecVO);
                }
            }
        }
        return toReturn;
    }


    public void atribuirEmpenhoAoItemRequisicaoExecucao(RequisicaoCompraExecucaoItemCorrecaoDados itemReqExecVO, List<RequisicaoCompraEmpenhoCorrecaoDados> empenhosVO) {
        for (RequisicaoCompraEmpenhoCorrecaoDados empVO : empenhosVO) {
            List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> gruposEmp = buscarGrupoContaEmpenho(empVO.getIdEmpenho(), itemReqExecVO.getIdGrupo(), true);

            if (Util.isListNullOrEmpty(gruposEmp)) {
                gruposEmp = buscarGrupoContaEmpenho(empVO.getIdEmpenho(), itemReqExecVO.getIdGrupo(), false);
            }
            if (Util.isListNullOrEmpty(gruposEmp)) {
                gruposEmp = buscarGrupoContaLiquidacao(empVO.getIdEmpenho(), itemReqExecVO.getIdGrupo(), true);
            }
            if (!Util.isListNullOrEmpty(gruposEmp)) {
                RequisicaoCompraEmpenhoGrupoCorrecaoDados grupoEmp = gruposEmp.get(0);
                empVO.setIdGrupoEmpenho(grupoEmp.getIdGrupo());
                empVO.setCodigoGrupo(grupoEmp.getCodigoGrupo());
                empVO.setCodigoConta(grupoEmp.getCodigoConta());
            }
            if (empVO.getIdGrupoEmpenho() != null
                && itemReqExecVO.getIdGrupo() != null
                && empVO.getIdGrupoEmpenho().equals(itemReqExecVO.getIdGrupo())) {
                itemReqExecVO.setIdEmpenho(empVO.getIdEmpenho());
                itemReqExecVO.setCodigoConta(empVO.getCodigoConta());
                itemReqExecVO.setNumeroEmpenho(empVO.getNumeroEmpenho());
            }
        }
    }

    public List<RequisicaoCompraEmpenhoCorrecaoDados> buscarRequisicaoExecucaoEmpenho(Long idRequisicaoCompraExecucao, TipoObjetoCompra tipoObjetoCompra, TipoRequisicaoCompra tipoRequisicao) {
        String sql = "";
        if (tipoRequisicao.isContrato()) {
            sql = " " +
                "   select distinct exemp.id as id_exec_emp," +
                "                   emp.id as id_empenho, " +
                "                   emp.numero || '/' || ex.ano as numero_emp," +
                "                   emp.valor as valor  " +
                "  from requisicaocompraexecucao rce " +
                "     inner join execucaocontrato ex on ex.id= rce.execucaocontrato_id " +
                "     inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
                "     inner join empenho emp on emp.id = exemp.empenho_id " +
                "     inner join exercicio ex on ex.id = emp.exercicio_id  " +
                "    where rce.id = :idReqCompExec ";
        } else if (tipoRequisicao.isExecucaoProcesso()) {
            sql = " " +
                "   select distinct exemp.id as id_exec_emp," +
                "                   emp.id as id_empenho, " +
                "                   emp.numero || '/' || ex.ano as numero_emp,  " +
                "                   emp.valor as valor  " +
                "  from requisicaocompraexecucao rce " +
                "     inner join execucaoprocesso ex on ex.id= rce.execucaoprocesso_id " +
                "     inner join execucaoprocessoempenho exemp on exemp.execucaoprocesso_id = ex.id " +
                "     inner join empenho emp on emp.id = exemp.empenho_id " +
                "     inner join exercicio ex on ex.id = emp.exercicio_id  " +
                "    where rce.id = :idReqCompExec ";
        } else if (tipoRequisicao.isReconhecimentoDivida()) {
            sql = " " +
                "   select distinct solrc.id as id_exec_emp," +
                "                   emp.id as id_empenho, " +
                "                   emp.numero || '/' || ex.ano as numero_emp,  " +
                "                   emp.valor as valor  " +
                "  from reconhecimentodivida rd " +
                "     inner join solicitacaoempenhorecdiv solrc on rd.id = solrc.reconhecimentodivida_id " +
                "     inner join solicitacaoempenho sol on sol.id = solrc.solicitacaoempenho_id " +
                "     inner join empenho emp on emp.id = sol.empenho_id " +
                "     inner join exercicio ex on ex.id = emp.exercicio_id  " +
                "    where rd.id = :idReqCompExec ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idReqCompExec", idRequisicaoCompraExecucao);

        List<RequisicaoCompraEmpenhoCorrecaoDados> empenhos = Lists.newArrayList();
        for (Object[] objEmp : (List<Object[]>) q.getResultList()) {
            RequisicaoCompraEmpenhoCorrecaoDados novoReqExecEmp = new RequisicaoCompraEmpenhoCorrecaoDados();
            novoReqExecEmp.setIdExecucaoEmpenho(((BigDecimal) objEmp[0]).longValue());
            novoReqExecEmp.setIdEmpenho(((BigDecimal) objEmp[1]).longValue());
            novoReqExecEmp.setNumeroEmpenho((String) objEmp[2]);
            novoReqExecEmp.setValorEmpenho((BigDecimal) objEmp[3]);

            if (tipoObjetoCompra.isPermanenteOrConsumo()) {
                List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> gruposEmp = buscarGrupoContaEmpenho(novoReqExecEmp.getIdEmpenho(), null, true);
                if (Util.isListNullOrEmpty(gruposEmp)) {
                    gruposEmp = buscarGrupoContaEmpenho(novoReqExecEmp.getIdEmpenho(), null, false);
                }
                if (Util.isListNullOrEmpty(gruposEmp)) {
                    gruposEmp = buscarGrupoContaLiquidacao(novoReqExecEmp.getIdEmpenho(), null, true);
                }
                novoReqExecEmp.setGruposEmpenho(gruposEmp);
            }
            empenhos.add(novoReqExecEmp);
        }
        return empenhos;
    }

    public List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> buscarGrupoContaEmpenho(Long idEmpenho, Long idGrupo, boolean verifciaVigenciaComDataEmpenho) {
        try {
            String sql = " " +
                "  select distinct g.id      as id_grupo," +
                "         g.codigo  as cod_grupo," +
                "         c.codigo  as cod_conta " +
                "   from desdobramentoempenho desd " +
                "    inner join empenho emp on emp.id = desd.empenho_id  " +
                "    inner join configdespesabens config on config.contadespesa_id = desd.conta_id " +
                "    inner join grupobem g on g.id = config.grupobem_id" +
                "    inner join conta c on c.id = desd.conta_id " +
                "   where desd.empenho_id = :idEmpenho ";
            sql += idGrupo != null ? " and g.id = :idGrupo " : "";
            sql += verifciaVigenciaComDataEmpenho
                ? "  and trunc(emp.dataempenho) between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), trunc(emp.dataempenho))"
                : "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))";
            sql += " union  all " +
                "  select distinct g.id      as id_grupo," +
                "         g.codigo  as cod_grupo," +
                "         c.codigo  as cod_conta " +
                "  from desdobramentoempenho desd " +
                "   inner join empenho emp on emp.id = desd.empenho_id  " +
                "   inner join configgrupomaterial config on config.contadespesa_id = desd.conta_id " +
                "   inner join grupomaterial g on g.id = config.grupomaterial_id" +
                "   inner join conta c on c.id = desd.conta_id " +
                "  where desd.empenho_id = :idEmpenho ";
            sql += idGrupo != null ? " and g.id = :idGrupo " : "";
            sql += verifciaVigenciaComDataEmpenho
                ? "  and trunc(emp.dataempenho) between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), trunc(emp.dataempenho))"
                : "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idEmpenho", idEmpenho);
            if (idGrupo != null) {
                q.setParameter("idGrupo", idGrupo);
            }
            if (!verifciaVigenciaComDataEmpenho) {
                q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
            }
            List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> grupos = Lists.newArrayList();
            for (Object[] o : (List<Object[]>) q.getResultList()) {
                RequisicaoCompraEmpenhoGrupoCorrecaoDados grupo = new RequisicaoCompraEmpenhoGrupoCorrecaoDados();
                grupo.setIdGrupo(((BigDecimal) o[0]).longValue());
                grupo.setCodigoGrupo((String) o[1]);
                grupo.setCodigoConta((String) o[2]);
                grupos.add(grupo);

            }
            return grupos;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> buscarGrupoContaLiquidacao(Long idEmpenho, Long idGrupo, boolean verifciaVigenciaComDataEmpenho) {
        try {

            String sql = " " +
                "  select distinct g.id      as id_grupo," +
                "         g.codigo  as cod_grupo," +
                "         c.codigo  as cod_conta " +
                "   from desdobramento desd " +
                "    inner join liquidacao liq on liq.id = desd.liquidacao_id " +
                "         inner join empenho emp on emp.id = liq.empenho_id " +
                "         inner join configdespesabens config on config.contadespesa_id = desd.conta_id " +
                "         inner join grupobem g on g.id = config.grupobem_id " +
                "         inner join conta c on c.id = desd.conta_id " +
                "   where emp.id = :idEmpenho ";
            sql += idGrupo != null ? " and g.id = :idGrupo " : "";
            sql += verifciaVigenciaComDataEmpenho
                ? "  and trunc(liq.dataliquidacao) between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), trunc(liq.dataliquidacao))"
                : "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))";
            sql += " union  all " +
                "  select distinct g.id      as id_grupo," +
                "         g.codigo  as cod_grupo," +
                "         c.codigo  as cod_conta " +
                "   from desdobramento desd " +
                "    inner join liquidacao liq on liq.id = desd.liquidacao_id " +
                "    inner join empenho emp on emp.id = liq.empenho_id  " +
                "   inner join configgrupomaterial config on config.contadespesa_id = desd.conta_id " +
                "   inner join grupomaterial g on g.id = config.grupomaterial_id" +
                "   inner join conta c on c.id = desd.conta_id " +
                "  where emp.id = :idEmpenho ";
            sql += idGrupo != null ? " and g.id = :idGrupo " : "";
            sql += verifciaVigenciaComDataEmpenho
                ? "  and trunc(liq.dataliquidacao) between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), trunc(liq.dataliquidacao))"
                : "  and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(config.iniciovigencia) and coalesce(trunc(config.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idEmpenho", idEmpenho);
            if (idGrupo != null) {
                q.setParameter("idGrupo", idGrupo);
            }
            if (!verifciaVigenciaComDataEmpenho) {
                q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
            }
            List<RequisicaoCompraEmpenhoGrupoCorrecaoDados> grupos = Lists.newArrayList();
            for (Object[] o : (List<Object[]>) q.getResultList()) {
                RequisicaoCompraEmpenhoGrupoCorrecaoDados grupo = new RequisicaoCompraEmpenhoGrupoCorrecaoDados();
                grupo.setIdGrupo(((BigDecimal) o[0]).longValue());
                grupo.setCodigoGrupo((String) o[1]);
                grupo.setCodigoConta((String) o[2]);
                grupos.add(grupo);

            }
            return grupos;
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<RequisicaoCompraExecucaoItemCorrecaoDados> criarRequisicaoCompraExecucaoItemCorrecaoDados(RequisicaoCompraExecucaoCorrecaoDados reqExecVO,
                                                                                                          List<RequisicaoCompraEmpenhoCorrecaoDados> empenhosVO,
                                                                                                          TipoRequisicaoCompra tipoRequisicao) {
        String sql = "";
        if (tipoRequisicao.isContrato()) {
            sql = " select distinct " +
                "          item.id as id_item_req, " +
                "          irce.id as id_item_req_exec, " +
                "          item.quantidade," +
                "          item.valorunitario," +
                "          item.valortotal, " +
                "           coalesce(gm.id, gb.id) as id_grupo, " +
                "           coalesce(gm.codigo, gb.codigo) as cod_grupo," +
                "          oc.id as id_objeto_compra " +
                "      from itemrequisicaocompraexec irce " +
                "        inner join execucaocontratoitem exitem on exitem.id = irce.execucaocontratoitem_id " +
                "        inner join itemrequisicaodecompra item on item.id = irce.itemrequisicaocompra_id " +
                "        inner join requisicaodecompra req on req.id = item.requisicaodecompra_id " +
                "        inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
                "        left join itemcontratovigente icv on icv.itemcontrato_id = ic.id " +
                "        left join itemcotacao icot on icot.id = icv.itemcotacao_id " +
                "        left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                "        left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                "        left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                "        left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                "        left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                "        left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
                "        left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
                "        left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                "        left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
                "        left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
                "        inner join objetocompra oc on oc.id = coalesce(ic.objetocompracontrato_id, itemsol.objetocompra_id, ise.objetocompra_id, icot.objetocompra_id) " +
                "        inner join grupoobjetocompra goc on goc.id = oc.grupoobjetocompra_id " +
                "        left join associacaogruobjcomgrumat gocm on goc.id = gocm.grupoobjetocompra_id " +
                "        left join grupomaterial gm on gm.id = gocm.grupomaterial_id " +
                "            and trunc(req.datarequisicao) between trunc(gocm.iniciovigencia) and coalesce(trunc(gocm.finalvigencia), trunc(req.datarequisicao))" +
                "        left join grupoobjcompragrupobem gocb on goc.id = gocb.grupoobjetocompra_id " +
                "           and trunc(req.datarequisicao) between trunc(gocb.iniciovigencia) and coalesce(trunc(gocb.fimvigencia), trunc(req.datarequisicao)) " +
                "       left join grupobem gb on gb.id = gocb.grupobem_id " +
                "       where item.requisicaodecompra_id = :idRequisicao " +
                "           and exitem.execucaocontrato_id = :idExecucao ";
        } else if (tipoRequisicao.isExecucaoProcesso()) {
            sql = " select distinct " +
                "          item.id as id_item_req," +
                "          exitem.id as id_item_exec," +
                "          item.quantidade," +
                "          item.valorunitario," +
                "          item.valortotal, " +
                "           coalesce(gm.id, gb.id) as id_grupo, " +
                "           coalesce(gm.codigo, gb.codigo) as cod_grupo, " +
                "          oc.id as id_objeto_compra " +
                " from itemrequisicaodecompra item " +
                "        inner join execucaoprocessoitem exitem on exitem.id = item.execucaoprocessoitem_id " +
                "        inner join execucaoprocesso ex on ex.id = exitem.execucaoprocesso_id " +
                "        inner join itemprocessodecompra ipc on ipc.id = exitem.itemprocessocompra_id " +
                "        inner join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
                "        inner join objetocompra oc on oc.id = itemsol.objetocompra_id" +
                "        inner join grupoobjetocompra goc on goc.id = oc.grupoobjetocompra_id " +
                "        left join associacaogruobjcomgrumat gocm on goc.id = gocm.grupoobjetocompra_id " +
                "            and trunc(ex.datalancamento) between trunc(gocm.iniciovigencia) and coalesce(trunc(gocm.finalvigencia), trunc(ex.datalancamento))" +
                "        left join grupomaterial gm on gm.id = gocm.grupomaterial_id " +
                "        left join grupoobjcompragrupobem gocb on goc.id = gocb.grupoobjetocompra_id " +
                "           and trunc(ex.datalancamento) between trunc(gocb.iniciovigencia) and coalesce(trunc(gocb.fimvigencia), trunc(ex.datalancamento))" +
                "        left join grupobem gb on gb.id = gocb.grupobem_id " +
                "       where item.requisicaodecompra_id = :idRequisicao " +
                "           and exitem.execucaoprocesso_id = :idExecucao ";
        } else if (tipoRequisicao.isReconhecimentoDivida()) {
            sql = " select item.id as id_item_req," +
                "          ird.id as id_item_exec," +
                "          item.quantidade," +
                "          item.valorunitario," +
                "          item.valortotal, " +
                "          coalesce(gm.id, gb.id) as id_grupo, " +
                "          coalesce(gm.codigo, gb.codigo) as cod_grupo, " +
                "          oc.id as id_objeto_compra " +
                " from itemrequisicaodecompra item " +
                "        inner join itemreconhecimentodivida ird on ird.id = item.itemreconhecimentodivida_id " +
                "        inner join reconhecimentodivida rd on rd.id = ird.reconhecimentodivida_id " +
                "        inner join objetocompra oc on oc.id = ird.objetocompra_id" +
                "        inner join grupoobjetocompra goc on goc.id = oc.grupoobjetocompra_id " +
                "        left join associacaogruobjcomgrumat gocm on goc.id = gocm.grupoobjetocompra_id " +
                "            and trunc(rd.datareconhecimento) between trunc(gocm.iniciovigencia) and coalesce(trunc(gocm.finalvigencia), trunc(rd.datareconhecimento))" +
                "        left join grupomaterial gm on gm.id = gocm.grupomaterial_id " +
                "        left join grupoobjcompragrupobem gocb on goc.id = gocb.grupoobjetocompra_id " +
                "           and trunc(rd.datareconhecimento) between trunc(gocb.iniciovigencia) and coalesce(trunc(gocb.fimvigencia), trunc(rd.datareconhecimento))" +
                "        left join grupobem gb on gb.id = gocb.grupobem_id " +
                "          where item.requisicaodecompra_id = :idRequisicao ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", reqExecVO.getIdRequisicao());
        if (!tipoRequisicao.isReconhecimentoDivida()) {
            q.setParameter("idExecucao", reqExecVO.getIdExecucao());
        }

        List<RequisicaoCompraExecucaoItemCorrecaoDados> itens = Lists.newArrayList();
        Map<String, List<RequisicaoCompraExecucaoItemCorrecaoDados>> maps = Maps.newHashMap();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            RequisicaoCompraExecucaoItemCorrecaoDados itemVO = new RequisicaoCompraExecucaoItemCorrecaoDados();
            itemVO.setIdItemRequisicao(((BigDecimal) obj[0]).longValue());
            itemVO.setIdItemExecucao(((BigDecimal) obj[1]).longValue());
            itemVO.setQuantidade((BigDecimal) obj[2]);
            itemVO.setValorUnitario((BigDecimal) obj[3]);
            itemVO.setValorTotal((BigDecimal) obj[4]);
            itemVO.setIdGrupo(obj[5] != null ? ((BigDecimal) obj[5]).longValue() : null);
            itemVO.setCodigoGrupo(obj[6] != null ? (String) obj[6] : null);
            itemVO.setTipoAlteracaoDados(tipoRequisicao.isContrato() ? CorrecaoDadosAdministrativoVO.TipoAlteracaoDados.UPDATE : CorrecaoDadosAdministrativoVO.TipoAlteracaoDados.INSERT);
            if (reqExecVO.getTipoObjetoCompra().isPermanenteOrConsumo()) {
                if (itemVO.getIdGrupo() == null) {
                    long idObjetoCompra = ((BigDecimal) obj[7]).longValue();
                    Object[] objects = buscarGrupoObjetoCompra(idObjetoCompra);
                    itemVO.setIdGrupo(objects[0] != null ? ((BigDecimal) objects[0]).longValue() : null);
                    itemVO.setCodigoGrupo(objects[0] != null ? (String) objects[1] : "???");
                }
                atribuirEmpenhoAoItemRequisicaoExecucao(itemVO, empenhosVO);
                preencherMapItensExecucaoPorGrupo(maps, itemVO);
            }
            itens.add(itemVO);
        }

        criarRequisicaoCompraExecucaoItemCorrecaoDados(reqExecVO, maps);
        return itens;
    }

    public Object[] buscarGrupoObjetoCompra(Long idObjetoCompra) {
        try {
            String sql = " select distinct " +
                "          coalesce(gm.id, gb.id) as id_grupo, " +
                "          coalesce(gm.codigo, gb.codigo) as cod_grupo " +
                "       from objetocompra oc " +
                "        inner join grupoobjetocompra goc on goc.id = oc.grupoobjetocompra_id " +
                "        left join associacaogruobjcomgrumat gocm on goc.id = gocm.grupoobjetocompra_id " +
                "            and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(gocm.iniciovigencia) and coalesce(trunc(gocm.finalvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "        left join grupomaterial gm on gm.id = gocm.grupomaterial_id " +
                "        left join grupoobjcompragrupobem gocb on goc.id = gocb.grupoobjetocompra_id " +
                "           and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(gocb.iniciovigencia) and coalesce(trunc(gocb.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "        left join grupobem gb on gb.id = gocb.grupobem_id " +
                "       where oc.id = :idObjetoCompra ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idObjetoCompra", idObjetoCompra);
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
            return ((Object[]) q.getSingleResult());
        } catch (NoResultException e) {
            return null;
        }
    }

    private static void criarRequisicaoCompraExecucaoItemCorrecaoDados(RequisicaoCompraExecucaoCorrecaoDados reqExecVO, Map<String, List<RequisicaoCompraExecucaoItemCorrecaoDados>> maps) {
        for (Map.Entry<String, List<RequisicaoCompraExecucaoItemCorrecaoDados>> entry : maps.entrySet()) {
            RequisicaoCompraExecucaoItemGrupoCorrecaoDados itemGrupo = new RequisicaoCompraExecucaoItemGrupoCorrecaoDados();
            itemGrupo.setCodigoGrupo(entry.getKey());
            itemGrupo.setItens(entry.getValue());

            reqExecVO.getItensGrupo().add(itemGrupo);
        }
    }

    private static void preencherMapItensExecucaoPorGrupo(Map<String, List<RequisicaoCompraExecucaoItemCorrecaoDados>> maps, RequisicaoCompraExecucaoItemCorrecaoDados novoItem) {
        if (!maps.containsKey(novoItem.getCodigoGrupo())) {
            maps.put(novoItem.getCodigoGrupo(), Lists.newArrayList());
        }
        List<RequisicaoCompraExecucaoItemCorrecaoDados> itensMap = maps.get(novoItem.getCodigoGrupo());
        itensMap.add(novoItem);
        maps.put(novoItem.getCodigoGrupo(), itensMap);
    }

    private void executarUpdateRequisicaoCompraExecucao(Long idReqCompExec, Long idExecucaoEmpenho, Long idRequisicao,
                                                        TipoRequisicaoCompra tipoRequisicao, CorrecaoDadosAdministrativoVO.TipoAlteracaoDados tipoAlteracao) {
        String colunaExecucaoEmpenho;
        if (tipoRequisicao.isContrato()) {
            colunaExecucaoEmpenho = "execucaocontratoempenho_id ";
        } else if (tipoRequisicao.isExecucaoProcesso()) {
            colunaExecucaoEmpenho = "execucaoprocessoempenho_id ";
        } else {
            colunaExecucaoEmpenho = "execucaoreconhecimentodiv_id ";
        }
        switch (tipoAlteracao) {
            case INSERT:
                String sqlInsert = " insert into requisicaocompraexecucao (ID, REQUISICAOCOMPRA_ID, " + colunaExecucaoEmpenho + ") " +
                    "                values (HIBERNATE_SEQUENCE.nextval, :idRequisicao, :idExecucaoEmpenho)";
                Query qInsert = em.createNativeQuery(sqlInsert);
                qInsert.setParameter("idRequisicao", idRequisicao);
                qInsert.setParameter("idExecucaoEmpenho", idExecucaoEmpenho);
                qInsert.executeUpdate();
                break;
            case DEPRECATE:
                String sqlDelete = " update requisicaocompraexecucao set deprecado = 1 where id = :idReqCompExec ";
                Query qDelete = em.createNativeQuery(sqlDelete);
                qDelete.setParameter("idReqCompExec", idReqCompExec);
                qDelete.executeUpdate();
                break;
        }
    }

    private void executarUpdateRequisicaoCompra(Long idRequisicao) {
        String sql = " update requisicaodecompra set reprocessada = 1 where id = :idReq ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idReq", idRequisicao);
        q.executeUpdate();
    }

    private void executarUpdateItemRequisicaoCompraExecucao(Long idEmpenho,
                                                            RequisicaoCompraExecucaoItemCorrecaoDados itemVO,
                                                            TipoRequisicaoCompra tipoRequisicao) {

        if (CorrecaoDadosAdministrativoVO.TipoAlteracaoDados.INSERT.equals(itemVO.getTipoAlteracaoDados())) {
            String colunaItem;
            if (tipoRequisicao.isContrato()) {
                colunaItem = "execucaocontratoitem_id ";
            } else if (tipoRequisicao.isExecucaoProcesso()) {
                colunaItem = "execucaoprocessoitem_id ";
            } else {
                colunaItem = "itemreconhecimentodivida_id ";
            }
            String sql = " insert into itemrequisicaocompraexec (id, itemrequisicaocompra_id, " + colunaItem + ", quantidade, valorunitario, valortotal, empenho_id) " +
                "          values(hibernate_sequence.nextval, :idItemReq, :idItemExecucao, :quantidade, :valorUnitario, :valorTotal, :empenhoId)";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idItemReq", itemVO.getIdItemRequisicao());
            q.setParameter("idItemExecucao", itemVO.getIdItemExecucao());
            q.setParameter("quantidade", itemVO.getQuantidade());
            q.setParameter("valorUnitario", itemVO.getValorUnitario());
            q.setParameter("valorTotal", itemVO.getValorTotal());
            q.setParameter("empenhoId", idEmpenho);
            q.executeUpdate();
        } else {
            String sql = " update itemrequisicaocompraexec set empenho_id = " + idEmpenho + " where id = " + itemVO.getIdItemExecucao();
            Query q = em.createNativeQuery(sql);
            q.executeUpdate();
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<CorrecaoDadosAdministrativoVO> reprocessarAlteracaoContratual(CorrecaoDadosAdministrativoVO
                                                                                    selecionado) {
        AssistenteBarraProgresso assistente = selecionado.getAssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Buscando movimentos para reprocessar...");

        List<AlteracaoContratual> alteracoes = buscarAlteracoesNaoReprocessadas("");
        selecionado.setAlteracoesContratuais(alteracoes);
        assistente.setTotal(selecionado.getAlteracoesContratuais().size());

        assistente.setDescricaoProcesso("Reprocessando movimentos alteração contratual...");
        for (AlteracaoContratual alt : selecionado.getAlteracoesContratuais()) {
            AlteracaoContratual altCont = alteracaoContratualFacade.recuperarComDependenciasMovimentosAndItens(alt.getId());
            unificarMovimentosAlteracaoContratual(altCont);
            assistente.conta();
        }
        return new AsyncResult<CorrecaoDadosAdministrativoVO>(selecionado);
    }

    public List<Contrato> buscarTodosContratos() {
        String sql = " select c.* from contrato c " +
            "           where c.reprocessado = :reprocessado " +
            "           and c.situacaocontrato in (:aprovado, :deferido)";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("reprocessado", Boolean.FALSE);
        q.setParameter("aprovado", SituacaoContrato.APROVADO.name());
        q.setParameter("deferido", SituacaoContrato.DEFERIDO.name());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public Long countContratos() {
        String sql = " select count(c.id) from contrato c " +
            "           where c.reprocessado = :reprocessado " +
            "           and c.situacaocontrato in (:aprovado, :deferido) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("reprocessado", Boolean.FALSE);
        q.setParameter("aprovado", SituacaoContrato.APROVADO.name());
        q.setParameter("deferido", SituacaoContrato.DEFERIDO.name());
        return ((BigDecimal) q.getSingleResult()).longValue();
    }

    public List<AlteracaoContratual> buscarAlteracoesNaoReprocessadas(String parte) {
        String sql = "" +
            "   select ad.* from alteracaocontratual ad " +
            "     inner join exercicio e on ad.exercicio_id = e.id " +
            "     inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ad.id   " +
            "     inner join contrato c on c.id = acc.contrato_id " +
            "     inner join exercicio exCont on c.exerciciocontrato_id = exCont.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "   where (ad.numerotermo like :parte " +
            "           or ad.numero like :parte " +
            "           or lower(" + Util.getTranslate("ad.justificativa") + ") like " + Util.getTranslate(":parte") +
            "           or lower(" + Util.getTranslate("c.objeto") + ") like " + Util.getTranslate(":parte") +
            "           or e.ano like :parte " +
            "           or to_char(ad.numerotermo) || '/' || to_char(e.ano) like :parte " +
            "           or to_char(ad.numero) || '/' || to_char(extract(year from ad.datalancamento)) like :parte " +
            "           or lower(" + Util.getTranslate("pf.nome") + ") like " + Util.getTranslate(":parte") +
            "           or lower(" + Util.getTranslate("pj.razaoSocial") + ") like " + Util.getTranslate(":parte") +
            "           or pf.cpf like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pj.cnpj like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or to_char(c.numerotermo) || '/' || to_char(exCont.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte " +
            "          )" +
            "     and ad.situacao <> :em_elaboracao " +
            "     and ad.tipotermo in (:valor, :prazoValor, :prazo) " +
            "   order by ad.numerotermo desc ";
        Query q = em.createNativeQuery(sql, AlteracaoContratual.class);
        q.setParameter("em_elaboracao", SituacaoContrato.EM_ELABORACAO.name());
        q.setParameter("valor", TipoTermoAlteracaoContratual.VALOR.name());
        q.setParameter("prazo", TipoTermoAlteracaoContratual.PRAZO.name());
        q.setParameter("prazoValor", TipoTermoAlteracaoContratual.PRAZO_VALOR.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public void unificarMovimentosAlteracaoContratual(AlteracaoContratual alt) {
        Map<OperacaoMovimentoAlteracaoContratual, List<MovimentoAlteracaoContratual>> mapValor = Maps.newHashMap();
        List<MovimentoAlteracaoContratual> movimentosPrazo = Lists.newArrayList();

        for (MovimentoAlteracaoContratual movAlt : alt.getMovimentos()) {
            OperacaoMovimentoAlteracaoContratual operacao = movAlt.getFinalidade().isAcrescimo() ? OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO : OperacaoMovimentoAlteracaoContratual.REDUCAO_PRAZO;
            if (movAlt.getInicioVigencia() != null) {
                movAlt.setOperacao(operacao);
                movimentosPrazo.add(movAlt);
            }
            if (movAlt.getInicioExecucao() != null) {
                movAlt.setOperacao(operacao);
                movimentosPrazo.add(movAlt);
            }
            if (movAlt.isOperacoesValor()) {
                if (!mapValor.containsKey(movAlt.getOperacao())) {
                    mapValor.put(movAlt.getOperacao(), Lists.newArrayList(movAlt));
                } else {
                    List<MovimentoAlteracaoContratual> movimentosMap = mapValor.get(movAlt.getOperacao());
                    movimentosMap.add(movAlt);
                    mapValor.put(movAlt.getOperacao(), movimentosMap);
                }
            }
        }
        if (movimentosPrazo.size() > 1) {
            MovimentoAlteracaoContratual primeiroMovPrazo = movimentosPrazo.get(0);
            MovimentoAlteracaoContratual ultimoMovPrazo = movimentosPrazo.get(movimentosPrazo.size() - 1);

            if (primeiroMovPrazo.getInicioVigencia() == null) {
                primeiroMovPrazo.setInicioVigencia(ultimoMovPrazo.getInicioVigencia());
            }
            if (primeiroMovPrazo.getTerminoVigencia() == null) {
                primeiroMovPrazo.setTerminoVigencia(ultimoMovPrazo.getTerminoVigencia());
            }

            if (primeiroMovPrazo.getInicioExecucao() == null) {
                primeiroMovPrazo.setInicioExecucao(ultimoMovPrazo.getInicioExecucao());
            }
            if (primeiroMovPrazo.getTerminoExecucao() == null) {
                primeiroMovPrazo.setTerminoExecucao(ultimoMovPrazo.getTerminoExecucao());
            }
            alt.getMovimentos().remove(ultimoMovPrazo);
        }

        for (Map.Entry<OperacaoMovimentoAlteracaoContratual, List<MovimentoAlteracaoContratual>> entry : mapValor.entrySet()) {
            if (entry.getValue().size() > 1) {
                MovimentoAlteracaoContratual primeiroMovValor = entry.getValue().get(0);
                MovimentoAlteracaoContratual ultimoMovValor = entry.getValue().get(entry.getValue().size() - 1);

                if (!primeiroMovValor.getValorVariacaoContrato()) {
                    alt.getMovimentos().remove(primeiroMovValor);
                }
                if (!ultimoMovValor.getValorVariacaoContrato()) {
                    alt.getMovimentos().remove(ultimoMovValor);
                }
            }
        }
        em.merge(alt);
    }

    public LoteEfetivacaoTransferenciaBemFacade getLoteEfetivacaoTransferenciaBemFacade() {
        return loteEfetivacaoTransferenciaBemFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SingletonGeradorCodigoPatrimonio getSingletonGeradorCodigoPatrimonio() {
        return singletonGeradorCodigoPatrimonio;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }


    public SingletonContrato getSingletonContrato() {
        return singletonContrato;
    }

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }

    public SingletonConcorrenciaMaterial getSingletonConcorrenciaMaterial() {
        return singletonConcorrenciaMaterial;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }
}
