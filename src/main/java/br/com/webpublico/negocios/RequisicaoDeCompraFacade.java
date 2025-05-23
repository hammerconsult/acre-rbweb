/*
 * Codigo gerado automaticamente em Wed Feb 22 16:41:13 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.entidadesauxiliares.contabil.LiquidacaoVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.administrativo.ExecucaoProcessoEstornoFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class RequisicaoDeCompraFacade extends AbstractFacade<RequisicaoDeCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private ExecucaoContratoEstornoFacade execucaoContratoEstornoFacade;
    @EJB
    private ExecucaoProcessoEstornoFacade execucaoProcessoEstornoFacade;
    @EJB
    private RequisicaoCompraEstornoFacade requisicaoCompraEstornoFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associcaoGrupoMaterialFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade associcaoGrupoBemFacade;
    @EJB
    private ItemPregaoFacade itemPregaoFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private ExecucaoProcessoFacade execucaoProcessoFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private SolicitacaoMaterialExternoFacade solicitacaoMaterialExternoFacade;
    @EJB
    private ReconhecimentoDividaFacade reconhecimentoDividaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private DerivacaoObjetoCompraComponenteFacade derivacaoObjetoCompraComponenteFacade;
    @EJB
    private SolicitacaoAquisicaoFacade solicitacaoAquisicaoFacade;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @EJB
    private ConfigDespesaBensFacade configGrupoBemFacade;

    public RequisicaoDeCompraFacade() {
        super(RequisicaoDeCompra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public RequisicaoDeCompra recuperar(Object id) {
        RequisicaoDeCompra entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        if (entity.getExecucoes() != null) {
            Hibernate.initialize(entity.getExecucoes());
        }
        if (entity.getItens() != null) {
            for (ItemRequisicaoDeCompra item : entity.getItens()) {
                Hibernate.initialize(item.getItensRequisicaoExecucao());
            }
        }
        return entity;
    }

    public RequisicaoDeCompra recuperarComDependenciasItens(Object id) {
        RequisicaoDeCompra entity = super.recuperar(id);
        if (entity.getItens() != null) {
            Hibernate.initialize(entity.getItens());
            for (ItemRequisicaoDeCompra item : entity.getItens()) {
                Hibernate.initialize(item.getItensRequisicaoExecucao());
            }
        }
        return entity;
    }

    public RequisicaoDeCompra recuperarComDependenciasRequisicaoExecucao(Object id) {
        RequisicaoDeCompra entity = super.recuperar(id);
        Hibernate.initialize(entity.getExecucoes());
        return entity;
    }

    public RequisicaoDeCompra recuperarSemDependencias(Object id) {
        return super.recuperar(id);
    }

    public RequisicaoDeCompra salvarRequisicao(RequisicaoDeCompra entity) {
        if (entity.getId() == null && entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(RequisicaoDeCompra.class, "numero"));
        }
        entity = movimentarSituacaoRequisicaoCompra(entity, SituacaoRequisicaoCompra.EM_ELABORACAO);
        return entity;
    }

    public RequisicaoDeCompra movimentarSituacaoRequisicaoCompra(RequisicaoDeCompra requisicaoCompra, SituacaoRequisicaoCompra situacao) {
        requisicaoCompra.setSituacaoRequisicaoCompra(situacao);
        requisicaoCompra = em.merge(requisicaoCompra);
        return requisicaoCompra;
    }

    public List<RequisicaoDeCompra> recuperarFiltrando(String parte) {
        String sql = " select rc.* from requisicaodecompra rc " +
            "  where (rc.numero like :parte or lower(" + Util.getTranslate("rc.descricao") + ") like " + Util.getTranslate(":parte") + ")" +
            "  order by numero desc ";
        Query q = em.createNativeQuery(sql, RequisicaoDeCompra.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<RequisicaoDeCompra> buscarRequisicoesPorTipo(String parte, TipoRequisicaoCompra tipoRequisicao) {
        String sql = " select rc.* from requisicaodecompra rc " +
            "  where (rc.numero like :parte or lower(" + Util.getTranslate("rc.descricao") + ") like " + Util.getTranslate(":parte") + ")" +
            "  and rc.tiporequisicao = :tipoReq " +
            "  order by numero ";
        Query q = em.createNativeQuery(sql, RequisicaoDeCompra.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoReq", tipoRequisicao.name());
        q.setMaxResults(300);
        return q.getResultList();
    }

    public List<RequisicaoDeCompra> buscarRequisicoesEntradaPorCompra(String trim) {
        String sql = " " +
            " select rc.* from requisicaodecompra rc " +
            "  where (to_char(rc.numero) like :parte or lower(rc.descricao) like :parte) " +
            "   and rc.tipoObjetoCompra = :tipoObjeto " +
            "   and rc.tipoContaDespesa = :tipoContaDespesa " +
            "   and rc.situacaoRequisicaoCompra in (:situacoesMovimentacoes) " +
            "  order by rc.numero desc ";
        Query q = em.createNativeQuery(sql, RequisicaoDeCompra.class);
        q.setParameter("parte", "%" + trim.toLowerCase() + "%");
        q.setParameter("tipoObjeto", TipoObjetoCompra.CONSUMO.name());
        q.setParameter("tipoContaDespesa", TipoContaDespesa.BEM_ESTOQUE.name());
        q.setParameter("situacoesMovimentacoes", Util.getListOfEnumName(SituacaoRequisicaoCompra.getSituacoesPermiteMovimentacoes()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<ItemRequisicaoDeCompra> buscarItensRequisicaoAndItensExecucao(RequisicaoDeCompra requisicaoCompra) {
        String sql = " select item.* from itemrequisicaodecompra item " +
            "          where item.requisicaodecompra_id = :idRequisicao " +
            "          order by item.numero ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoDeCompra.class);
        q.setParameter("idRequisicao", requisicaoCompra.getId());
        List<ItemRequisicaoDeCompra> itens = q.getResultList();
        if (!Util.isListNullOrEmpty(itens)) {
            itens.stream().map(ItemRequisicaoDeCompra::getItensRequisicaoExecucao).forEach(Hibernate::initialize);
            return itens;
        }
        return Lists.newArrayList();
    }

    public List<RequisicaoCompraExecucao> buscarRequisicaoExecucao(Long idRequisicao) {
        String sql = " select rce.* from requisicaocompraexecucao rce " +
            "          where rce.requisicaocompra_id = :idRequisicao ";
        Query q = em.createNativeQuery(sql, RequisicaoCompraExecucao.class);
        q.setParameter("idRequisicao", idRequisicao);
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return Lists.newArrayList();
        }
        return resultList;
    }

    public List<RequisicaoCompraExecucaoVO> buscarRequisicaoExecucaoComponente(Long idRequisicao) {
        String sql = " " +
            "   select distinct " +
            "        coalesce(excont.id, exproc.id, rc.id)                                      as id_execucao, " +
            "        coalesce(to_char(excont.numero), to_char(exproc.numero), rc.numero)           as numero, " +
            "        coalesce(excont.datalancamento, exproc.datalancamento, rc.datareconhecimento) as data_lancamento, " +
            "        coalesce(excont.valor, exproc.valor, " +
            "                         (select sum(coalesce(item.valortotal,0)) " +
            "                          from itemreconhecimentodivida item " +
            "                          where item.reconhecimentodivida_id = rc.id))                as valor, " +
            "        req.tiporequisicao                                                            as tipo_req, " +
            "        req.tipoobjetocompra                                                          as tipo_objeto," +
            "        vw.codigo || ' - ' || vw.descricao                                            as unidade_orc," +
            "        rce.id                                                                        as id_req_exec " +
            "    from requisicaocompraexecucao rce " +
            "         inner join requisicaodecompra req on req.id = rce.requisicaocompra_id " +
            "         left join execucaocontratoempenho ece on ece.id = rce.execucaocontratoempenho_id " +
            "         left join execucaocontrato excont on excont.id = ece.execucaocontrato_id " +
            "         left join solicitacaoempenhorecdiv solrd on solrd.id = rce.execucaoreconhecimentodiv_id " +
            "         left join solicitacaoempenho se on se.id = solrd.solicitacaoempenho_id " +
            "         left join reconhecimentodivida rc on rc.id = solrd.reconhecimentodivida_id " +
            "         left join execucaoprocessoempenho epe on epe.id = rce.execucaoprocessoempenho_id " +
            "         left join execucaoprocesso exproc on exproc.id = epe.execucaoprocesso_id " +
            "         inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = coalesce(excont.unidadeorcamentaria_id, rc.unidadeorcamentaria_id, exproc.unidadeorcamentaria_id) " +
            "               and trunc(req.datarequisicao) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(req.datarequisicao))" +
            "  where req.id = :idRequisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", idRequisicao);
        List<RequisicaoCompraExecucaoVO> list = Lists.newArrayList();
        try {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RequisicaoCompraExecucaoVO reqExecVO = new RequisicaoCompraExecucaoVO();
                reqExecVO.setId(((BigDecimal) obj[0]).longValue());
                reqExecVO.setNumero((String) obj[1]);
                reqExecVO.setDataLancamento((Date) obj[2]);
                reqExecVO.setValor((BigDecimal) obj[3]);
                reqExecVO.setTipoProcesso(TipoRequisicaoCompra.valueOf((String) obj[4]));
                reqExecVO.setTipoObjetoCompra(TipoObjetoCompra.valueOf((String) obj[5]));
                reqExecVO.setUnidadeOrcamentaria((String) obj[6]);

                long idReqExec = ((BigDecimal) obj[7]).longValue();
                reqExecVO.setEmpenhosVO(buscarEmpenhoRequisicaoExecucao(idReqExec, reqExecVO.getTipoObjetoCompra()));
                list.add(reqExecVO);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public List<RequisicaoCompraEmpenhoVO> buscarEmpenhoRequisicaoExecucao(Long idReqExec, TipoObjetoCompra tipoObjetoCompra) {
        String sql = " " +
            "   select emp.* " +
            "    from requisicaocompraexecucao rce " +
            "         left join execucaocontratoempenho ece on ece.id = rce.execucaocontratoempenho_id " +
            "         left join execucaoprocessoempenho epe on epe.id = rce.execucaoprocessoempenho_id " +
            "         left join solicitacaoempenhorecdiv solrd on solrd.id = rce.execucaoreconhecimentodiv_id " +
            "         left join solicitacaoempenho se on se.id = solrd.solicitacaoempenho_id " +
            "         inner join empenho emp on emp.id = coalesce(ece.empenho_id, epe.empenho_id, se.empenho_id) " +
            "  where rce.id = :idReqExec ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idReqExec", idReqExec);
        List<RequisicaoCompraEmpenhoVO> list = Lists.newArrayList();
        try {
            List<Empenho> empenhos = q.getResultList();
            for (Empenho emp : empenhos) {
                RequisicaoCompraEmpenhoVO empVO = novoEmpenhoVO(emp, tipoObjetoCompra);
                list.add(empVO);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public List<RequisicaoCompraExecucaoVO> buscarExecucoesEmpenhadas(Long idProcesso, TipoObjetoCompra tipoObjetoCompra) {
        String sql;
        sql = " " +
            " select id, " +
            "        numero, " +
            "        data_lancamento," +
            "        valor, " +
            "        tipo," +
            "        id_unidade " +
            "  from ( " +
            "       select distinct" +
            "           ex.id as id, " +
            "           to_char(ex.numero) as numero, " +
            "           ex.datalancamento as data_lancamento," +
            "           ex.valor as valor, " +
            "           'CONTRATO' as tipo, " +
            "          ex.unidadeorcamentaria_id as id_unidade " +
            "          from execucaocontrato ex " +
            "           inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id  " +
            "           inner join empenho emp on emp.id = exemp.empenho_id  " +
            "          where ex.contrato_id = :idProcesso " +
            "           and ex.operacaoorigem = :operacaoPreExecucao " +
            "          union all " +
            "         select distinct " +
            "           ex.id as id, " +
            "           to_char(ex.numero) as numero, " +
            "           ex.datalancamento as data_lancamento," +
            "           ex.valor as valor, " +
            "           'ATA_REGISTRO_PRECO' as tipo, " +
            "          ex.unidadeorcamentaria_id as id_unidade " +
            "          from execucaoprocesso ex " +
            "           inner join execucaoprocessoempenho exemp on exemp.execucaoprocesso_id = ex.id " +
            "           inner join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "           inner join empenho emp on emp.id = exemp.empenho_id  " +
            "          where exata.ataregistropreco_id = :idProcesso" +
            "          union all " +
            "         select distinct " +
            "           ex.id as id, " +
            "           to_char(ex.numero) as numero, " +
            "           ex.datalancamento as data_lancamento," +
            "           ex.valor as valor, " +
            "           'DISPENSA_LICITACAO_INEXIGIBILIDADE' as tipo, " +
            "          ex.unidadeorcamentaria_id as id_unidade " +
            "          from execucaoprocesso ex " +
            "           inner join execucaoprocessoempenho exemp on exemp.execucaoprocesso_id = ex.id " +
            "           inner join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "           inner join empenho emp on emp.id = exemp.empenho_id  " +
            "          where exdisp.dispensalicitacao_id = :idProcesso " +
            "      union all " +
            "         select distinct " +
            "           rd.id as id, " +
            "           to_char(rd.numero)  as numero, " +
            "           rd.datareconhecimento as data_lancamento, " +
            "           coalesce((select sum(item.valortotal) from itemreconhecimentodivida item " +
            "             where item.reconhecimentodivida_id = rd.id), 0) as valor, " +
            "           'RECONHECIMENTO_DIVIDA' as tipo," +
            "          rd.unidadeorcamentaria_id as id_unidade " +
            "          from reconhecimentodivida rd " +
            "          where rd.id = :idProcesso " +
            " ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", idProcesso);
        q.setParameter("operacaoPreExecucao", OperacaoSaldoItemContrato.PRE_EXECUCAO.name());
        List<RequisicaoCompraExecucaoVO> list = Lists.newArrayList();
        try {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RequisicaoCompraExecucaoVO reqExecVO = new RequisicaoCompraExecucaoVO();
                reqExecVO.setId(((BigDecimal) obj[0]).longValue());
                reqExecVO.setNumero((String) obj[1]);
                reqExecVO.setDataLancamento((Date) obj[2]);
                reqExecVO.setValor((BigDecimal) obj[3]);
                reqExecVO.setTipoProcesso(TipoRequisicaoCompra.valueOf((String) obj[4]));
                reqExecVO.setTipoObjetoCompra(tipoObjetoCompra);

                Long idUnidade = ((BigDecimal) obj[5]).longValue();
                HierarquiaOrganizacional hoOrc = hierarquiaOrganizacionalFacade.buscarHierarquiaPorTipoIdUnidadeEData(TipoHierarquiaOrganizacional.ORCAMENTARIA, idUnidade, sistemaFacade.getDataOperacao());
                if (hoOrc != null) {
                    reqExecVO.setUnidadeOrcamentaria(hoOrc.toString());
                }
                buscarEmpenhoExecucao(reqExecVO, tipoObjetoCompra);
                list.add(reqExecVO);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public void buscarEmpenhoExecucao(RequisicaoCompraExecucaoVO reqExecVO, TipoObjetoCompra tipoObjetoCompra) {
        if (reqExecVO.getTipoProcesso().isContrato()) {
            List<ExecucaoContratoEmpenho> empenhosExec = execucaoContratoFacade.buscarExecucaoContratoEmpenhoPorExecucao(reqExecVO.getId());
            for (ExecucaoContratoEmpenho exEmp : empenhosExec) {
                if (TipoContaDespesa.getTiposContaDespesaPorTipoObjetoCompra(tipoObjetoCompra).contains(exEmp.getEmpenho().getTipoContaDespesa())) {
                    RequisicaoCompraEmpenhoVO novoEmp = novoEmpenhoVO(exEmp.getEmpenho(), tipoObjetoCompra);
                    novoEmp.setExecucaoContratoEmpenho(exEmp);
                    reqExecVO.getEmpenhosVO().add(novoEmp);
                }
            }
        } else if (reqExecVO.getTipoProcesso().isExecucaoProcesso()) {
            List<ExecucaoProcessoEmpenho> empenhosExec = execucaoProcessoFacade.buscarExecucaoProcessoEmpenhoPorExecucao(reqExecVO.getId());
            empenhosExec.forEach(exEmp -> {
                if (TipoContaDespesa.getTiposContaDespesaPorTipoObjetoCompra(tipoObjetoCompra).contains(exEmp.getEmpenho().getTipoContaDespesa())) {
                    RequisicaoCompraEmpenhoVO novoEmp = novoEmpenhoVO(exEmp.getEmpenho(), tipoObjetoCompra);
                    novoEmp.setExecucaoProcessoEmpenho(exEmp);
                    reqExecVO.getEmpenhosVO().add(novoEmp);
                }
            });
        } else {
            List<SolicitacaoEmpenhoReconhecimentoDivida> empenhosDivida = reconhecimentoDividaFacade.buscarSolicitacaoReconhecimentoDivida(reqExecVO.getId());
            empenhosDivida.forEach(exEmp -> {
                if (TipoContaDespesa.getTiposContaDespesaPorTipoObjetoCompra(tipoObjetoCompra).contains(exEmp.getSolicitacaoEmpenho().getEmpenho().getTipoContaDespesa())) {
                    RequisicaoCompraEmpenhoVO novoEmp = novoEmpenhoVO(exEmp.getSolicitacaoEmpenho().getEmpenho(), tipoObjetoCompra);
                    novoEmp.setExecucaoReconhecimentoDivida(exEmp);
                    reqExecVO.getEmpenhosVO().add(novoEmp);
                }
            });
        }
        Collections.sort(reqExecVO.getEmpenhosVO());
    }

    public RequisicaoCompraEmpenhoVO novoEmpenhoVO(Empenho emp, TipoObjetoCompra tipoObjetoCompra) {
        RequisicaoCompraEmpenhoVO novoEmp = new RequisicaoCompraEmpenhoVO();
        novoEmp.setEmpenho(emp);
        if (emp != null) {
            novoEmp.setDesdobramentoEmpenho(empenhoFacade.buscarDesdobramento(emp));
        }

        if (novoEmp.getDesdobramentoEmpenho() != null) {
            if (tipoObjetoCompra.isMaterialConsumo()) {
                GrupoMaterial grupoMaterial = configGrupoMaterialFacade.buscarGrupoPorConta(novoEmp.getDesdobramentoEmpenho().getConta());
                if (grupoMaterial != null) {
                    novoEmp.setIdGrupo(grupoMaterial.getId());
                    novoEmp.setCodigoGrupo(grupoMaterial.getCodigo());
                }
            } else if (tipoObjetoCompra.isMaterialPermanente()) {
                GrupoBem grupoBem = configGrupoBemFacade.buscarGrupoBemPorContaDespesa(sistemaFacade.getDataOperacao(), novoEmp.getDesdobramentoEmpenho().getConta());
                if (grupoBem != null) {
                    novoEmp.setIdGrupo(grupoBem.getId());
                    novoEmp.setCodigoGrupo(grupoBem.getCodigo());
                }
            }
        }
        return novoEmp;
    }

    public List<ItemRequisicaoProcessoLicitatorio> buscarItensProcessoLicitatorio(Long idExecucao) {
        String sql = " " +
            " select item.id                                          as id_item, " +
            "       ic.tipocontrole                                   as tipo_controle, " +
            "       'CONTRATO'                                        as tipo_processo, " +
            "       (select distinct case " +
            "                            when cd.id is not null " +
            "                                then cd.tipocontadespesa " +
            "                            else 'NAO_APLICAVEL' end " +
            "        from execucaocontratoitemdot itemdot " +
            "                 left join conta c on c.id = itemdot.contadespesa_id " +
            "                 left join contadespesa cd on cd.id = c.id " +
            "        where itemdot.execucaocontratoitem_id = item.id) as tipo_conta_desp " +
            " from execucaocontratoitem item " +
            "         inner join itemcontrato ic on ic.id = item.itemcontrato_id " +
            " where item.execucaocontrato_id = :idExecucao " +
            " union all " +
            " select item.id              as id_item, " +
            "       itemcot.tipocontrole as tipo_controle, " +
            "       'ATA_REGISTRO_PRECO' as tipo_processo, " +
            "       (select distinct case " +
            "                            when cd.id is not null " +
            "                                then cd.tipocontadespesa " +
            "                            else 'NAO_APLICAVEL' end " +
            "        from execucaoprocessofonteitem itemdot " +
            "                 left join conta c on c.id = itemdot.contadespesa_id " +
            "                 left join contadespesa cd on cd.id = c.id " +
            "        where itemdot.execucaoprocessoitem_id = item.id) " +
            "                            as tipo_conta_desp " +
            " from execucaoprocessoitem item " +
            "         inner join execucaoprocesso exproc on exproc.id = item.execucaoprocesso_id " +
            "         inner join execucaoprocessoata exata on exata.execucaoprocesso_id = exproc.id " +
            "         inner join itemprocessodecompra ipc on ipc.id = item.itemprocessocompra_id " +
            "         inner join itemsolicitacao ism on ism.id = ipc.itemsolicitacaomaterial_id " +
            "         inner join itemcotacao itemcot on itemcot.id = ism.itemcotacao_id " +
            " where exproc.id = :idExecucao " +
            " union all " +
            " select item.id                             as id_item, " +
            "       itemcot.tipocontrole                 as tipo_controle, " +
            "       'DISPENSA_LICITACAO_INEXIGIBILIDADE' as tipo_processo, " +
            "       (select distinct case " +
            "                            when cd.id is not null " +
            "                                then cd.tipocontadespesa " +
            "                            else 'NAO_APLICAVEL' end " +
            "        from execucaoprocessofonteitem itemdot " +
            "                 left join conta c on c.id = itemdot.contadespesa_id " +
            "                 left join contadespesa cd on cd.id = c.id " +
            "        where itemdot.execucaoprocessoitem_id = item.id) " +
            "                                            as tipo_conta_desp " +
            " from execucaoprocessoitem item " +
            "         inner join execucaoprocesso exproc on exproc.id = item.execucaoprocesso_id " +
            "         inner join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exproc.id " +
            "         inner join itemprocessodecompra ipc on ipc.id = item.itemprocessocompra_id " +
            "         inner join itemsolicitacao ism on ism.id = ipc.itemsolicitacaomaterial_id " +
            "         inner join itemcotacao itemcot on itemcot.id = ism.itemcotacao_id " +
            " where item.execucaoprocesso_id = :idExecucao " +
            " union all " +
            " select item.id                      as id_item, " +
            "        'QUANTIDADE'                 as tipo_controle, " +
            "        'RECONHECIMENTO_DIVIDA'      as tipo_processo, " +
            "        case " +
            "           when emp.id is not null " +
            "               then emp.tipocontadespesa " +
            "           else 'NAO_APLICAVEL' end as tipo_conta_desp " +
            " from itemreconhecimentodivida item " +
            "         inner join reconhecimentodivida rd on rd.id = item.reconhecimentodivida_id " +
            "         left join empenho emp on emp.reconhecimentodivida_id = rd.id " +
            " where rd.id = :idExecucao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucao", idExecucao);
        List<ItemRequisicaoProcessoLicitatorio> list = Lists.newArrayList();

        try {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ItemRequisicaoProcessoLicitatorio item = new ItemRequisicaoProcessoLicitatorio();
                item.setIdItem(((BigDecimal) obj[0]).longValue());
                item.setTipoControle(TipoControleLicitacao.valueOf((String) obj[1]));
                item.setTipoProcesso(TipoRequisicaoCompra.valueOf((String) obj[2]));
                item.setTipoContaDespesa(obj[3] != null ? TipoContaDespesa.valueOf((String) obj[3]) : TipoContaDespesa.NAO_APLICAVEL);
                if (item.getTipoProcesso().isContrato()) {
                    item.setItemExecucaoContrato(em.find(ExecucaoContratoItem.class, item.getIdItem()));
                    item.setObjetoCompra(item.getItemExecucaoContrato().getObjetoCompra());
                } else if (item.getTipoProcesso().isExecucaoProcesso()) {
                    item.setItemExecucaoProcesso(em.find(ExecucaoProcessoItem.class, item.getIdItem()));
                    item.setObjetoCompra(item.getItemExecucaoProcesso().getObjetoCompra());
                } else {
                    item.setItemReconhecimentoDivida(em.find(ItemReconhecimentoDivida.class, item.getIdItem()));
                    item.setObjetoCompra(item.getItemReconhecimentoDivida().getObjetoCompra());
                }
                recuperarGrupoContaDespesa(item.getObjetoCompra());
                list.add(item);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public void recuperarGrupoContaDespesa(ObjetoCompra objetoCompra) {
        objetoCompra.setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra()));
    }

    public Boolean isRequisicaoCompraCardapio(RequisicaoDeCompra requisicao) {
        String sql = " select req.* from requisicaodecompra req  " +
            "         inner join cardapiorequisicaocompra crc on crc.requisicaocompra_id = req.id " +
            "          where req.id = :idRequisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", requisicao.getId());
        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public Boolean isRequisicaoCompraEntradaPorCompra(RequisicaoDeCompra requisicao) {
        String sql = " select req.* from requisicaodecompra req  " +
            "         inner join entradacompramaterial ecm on ecm.requisicaodecompra_id = req.id " +
            "          where req.id = :idRequisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", requisicao.getId());
        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public BigDecimal getQuantidadeUtilizadaItemExecucaoContrato(ExecucaoContratoItem execItem, FonteDespesaORC fonteDespesaORC, ItemRequisicaoCompraExecucao itemReqEx) {
        String sql = " " +
            "           select distinct coalesce(sum(ircp.quantidade),0) as quantidadeUtilizada  " +
            "           from requisicaodecompra rdc                                                       " +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = rdc.id " +
            "           inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = irc.id " +
            "           inner join execucaocontratoitem exitem on exitem.id = ircp.execucaocontratoitem_id " +
            "           inner join empenho emp on emp.id = ircp.empenho_id " +
            "           where exitem.id = :idItemExecucao  " +
            "           and emp.fontedespesaorc_id = :idFonteDespOrc " +
            "           and rdc.situacaorequisicaocompra not in (:estornada, :estornadaParcial)  ";
        if (itemReqEx != null && itemReqEx.getId() != null) {
            sql += "    and ircp.id <> :idItemReqProc ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("estornadaParcial", SituacaoRequisicaoCompra.ESTORNADA_PARCIAL.name());
        q.setParameter("idItemExecucao", execItem.getId());
        q.setParameter("idFonteDespOrc", fonteDespesaORC.getId());
        if (itemReqEx != null && itemReqEx.getId() != null) {
            q.setParameter("idItemReqProc", itemReqEx.getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getQuantidadeUtilizadaItemExecucaoProcesso(ExecucaoProcessoItem execItem, FonteDespesaORC fonteDespesaORC, ItemRequisicaoCompraExecucao itemReqEx) {
        String sql = " " +
            "           select distinct coalesce(sum(ircp.quantidade),0) as quantidadeUtilizada  " +
            "           from requisicaodecompra rdc                                                       " +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = rdc.id " +
            "           inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = irc.id " +
            "           inner join execucaoprocessoitem exitem on exitem.id = ircp.execucaoprocessoitem_id " +
            "           inner join empenho emp on emp.id = ircp.empenho_id " +
            "           where exitem.id = :idItemExecucao  " +
            "           and emp.fontedespesaorc_id = :idFonteDespOrc " +
            "           and rdc.situacaorequisicaocompra <> :estornada  ";
        if (itemReqEx != null && itemReqEx.getId() != null) {
            sql += "    and ircp.id <> :idItemReqProc ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemExecucao", execItem.getId());
        q.setParameter("idFonteDespOrc", fonteDespesaORC.getId());
        if (itemReqEx != null && itemReqEx.getId() != null) {
            q.setParameter("idItemReqProc", itemReqEx.getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getQuantidadeUtilizadaItemReconhecimentoDivida(ItemReconhecimentoDivida execItem, FonteDespesaORC fonteDespesaORC, ItemRequisicaoCompraExecucao itemReqEx) {
        String sql = " " +
            "           select distinct coalesce(sum(ircp.quantidade),0) as quantidadeUtilizada  " +
            "           from requisicaodecompra rdc                                                       " +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = rdc.id " +
            "           inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = irc.id " +
            "           inner join empenho emp on emp.id = ircp.empenho_id " +
            "           where ird.id = :idItemProcesso  " +
            "           and emp.fontedespesaorc_id = :idFonteDespOrc " +
            "           and rdc.situacaorequisicaocompra <> :estornada  ";
        if (itemReqEx != null && itemReqEx.getId() != null) {
            sql += "    and ircp.id <> :idItemReqProc ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemProcesso", execItem.getId());
        q.setParameter("idFonteDespOrc", fonteDespesaORC.getId());
        if (itemReqEx != null && itemReqEx.getId() != null) {
            q.setParameter("idItemReqProc", itemReqEx.getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorUtilizadoItemExecucaoContrato(ExecucaoContratoItem itemExecCont, FonteDespesaORC fonteDespesaORC, List<Long> idsItemReqDesdobrados) {
        String sql = " " +
            "           select coalesce(sum(ircp.valortotal),0) as quantidadeUtilizada      " +
            "           from requisicaodecompra rdc                                                       " +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = rdc.id     " +
            "           inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = irc.id  " +
            "           inner join execucaocontratoitem exitem on exitem.id = ircp.execucaocontratoitem_id" +
            "           inner join execucaocontratoitemdot exdot on exitem.id = exdot.execucaocontratoitem_id" +
            "           inner join execucaocontratotipofonte exfonte on exfonte.id = exdot.execucaocontratotipofonte_id " +
            "           where ircp.execucaocontratoitem_id = :idItemExecucao  " +
            "           and rdc.situacaorequisicaocompra <> :estornada ";
        sql += fonteDespesaORC != null ? " and exfonte.fontedespesaorc_id = :idFonteDespOrc " : " ";
        sql += !Util.isListNullOrEmpty(idsItemReqDesdobrados) ? " and irc.id not in (:idsItemReqDesdobrados) " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemExecucao", itemExecCont.getId());
        if (fonteDespesaORC != null) {
            q.setParameter("idFonteDespOrc", fonteDespesaORC.getId());
        }
        if (!Util.isListNullOrEmpty(idsItemReqDesdobrados)) {
            q.setParameter("idsItemReqDesdobrados", idsItemReqDesdobrados);
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorUtilizadoItemExecucaoProcesso(ExecucaoProcessoItem itemExecCont, FonteDespesaORC fonteDespesaORC, List<Long> idsItemReqDesdobrados) {
        String sql = " " +
            "           select coalesce(sum(ircp.valortotal),0) as quantidadeUtilizada      " +
            "           from requisicaodecompra rdc                                                       " +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = rdc.id     " +
            "           inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = irc.id  " +
            "           inner join execucaoprocessoitem exitem on exitem.id = ircp.execucaoprocessoitem_id" +
            "           inner join execucaoprocessofonteitem exdot on exitem.id = exdot.execucaoprocessoitem_id" +
            "           inner join execucaoprocessofonte exfonte on exfonte.id = exdot.execucaoprocessofonte_id " +
            "           where exitem.id = :idItemExecucao  " +
            "           and rdc.situacaorequisicaocompra <> :estornada ";
        sql += fonteDespesaORC != null ? " and exfonte.fontedespesaorc_id = :idFonteDespOrc " : " ";
        sql += !Util.isListNullOrEmpty(idsItemReqDesdobrados) ? " and irc.id not in (:idsItemReqDesdobrados) " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemExecucao", itemExecCont.getId());
        if (fonteDespesaORC != null) {
            q.setParameter("idFonteDespOrc", fonteDespesaORC.getId());
        }
        if (!Util.isListNullOrEmpty(idsItemReqDesdobrados)) {
            q.setParameter("idsItemReqDesdobrados", idsItemReqDesdobrados);
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public Pessoa getFornecedorRequisicao(RequisicaoDeCompra requisicaoCompra) {
        String hql = " select coalesce(pCont, pRd, pExProc) from RequisicaoDeCompra req " +
            "           inner join req.execucoes rce " +
            "           left join rce.execucaoContratoEmpenho excontemp " +
            "           left join excontemp.execucaoContrato excont " +
            "           left join excont.contrato c " +
            "           left join c.contratado pCont  " +
            "           left join rce.execucaoReconhecimentoDiv exdp " +
            "           left join exdp.reconhecimentoDivida rc " +
            "           left join rc.fornecedor pRd  " +
            "           left join rce.execucaoProcessoEmpenho exprocemp " +
            "           left join exprocemp.execucaoProcesso exproc " +
            "           left join exproc.fornecedor pExProc  " +
            "        where req.id = :idRequisicao ";
        Query q = em.createQuery(hql);
        q.setParameter("idRequisicao", requisicaoCompra.getId());
        try {
            return (Pessoa) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível recuperar o fornecedor da requisição de compra.");
        }
    }

    public UnidadeOrganizacional getUnidadeAdministrativa(RequisicaoDeCompra requisicaoCompra, Date dataOperacao) {
        String sql = " select distinct unid.* from requisicaodecompra req " +
            "         left join requisicaocompraexecucao rce on rce.requisicaocompra_id = req.id" +
            "         left join execucaocontratoempenho ece on ece.id = rce.execucaocontratoempenho_id " +
            "         left join execucaocontrato excont on excont.id = ece.execucaocontrato_id " +
            "         left join contrato c on c.id = excont.contrato_id " +
            "         left join unidadecontrato uc on uc.contrato_id = c.id " +
            "              and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "         left join solicitacaoempenhorecdiv solrd on solrd.id = rce.execucaoreconhecimentodiv_id " +
            "         left join reconhecimentodivida rc on rc.id = solrd.reconhecimentodivida_id " +
            "         left join execucaoprocessoempenho epe on epe.id = rce.execucaoprocessoempenho_id " +
            "         left join execucaoprocesso exproc on exproc.id = epe.execucaoprocesso_id " +
            "         left join execucaoprocessoata exata on exata.execucaoprocesso_id = exproc.id " +
            "         left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "         left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exproc.id " +
            "         left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "         left join processodecompra pc on pc.id = disp.processodecompra_id " +
            "         inner join unidadeorganizacional unid on unid.id = coalesce(uc.unidadeadministrativa_id, rc.unidadeadministrativa_id, ata.unidadeorganizacional_id, pc.unidadeorganizacional_id) " +
            "         where req.id = :idRequisicao ";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("idRequisicao", requisicaoCompra.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            throw new ExcecaoNegocioGenerica("Unidade Administrativa não encontrada para a requisição de compra de " + requisicaoCompra.getTipoRequisicao().getDescricao() + ".");
        }
        return (UnidadeOrganizacional) resultList.get(0);
    }

    public List<RequisicaoDeCompra> buscarRequisicaoCompraComSaldoPorSituacoes(String filtro, List<SituacaoRequisicaoCompra> situacoes) {
        String sql = " select distinct req.* from requisicaodecompra req " +
            "       where (req.numero like :filtro or lower(req.descricao) like :filtro ) " +
            "       and req.situacaorequisicaocompra in (:situacoesMovimentacoes) " +
            "       and exists (select distinct 1 from ITEMREQUISICAODECOMPRA item " +
            "             left join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = item.id" +
            "             left join ITEMREQUISICAOCOMPRAEST est on est.itemrequisicaocompraexec_id = ircp.id " +
            "             where item.QUANTIDADE > 0 " +
            "             and item.REQUISICAODECOMPRA_ID = req.id " +
            "             group by item.id, item.QUANTIDADE " +
            "             having item.QUANTIDADE - sum(coalesce(est.quantidade, 0)) <> 0) " +
            "       order by req.numero desc ";
        Query q = em.createNativeQuery(sql, RequisicaoDeCompra.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("situacoesMovimentacoes", SituacaoRequisicaoCompra.getSituacoesAsString(situacoes));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<RequisicaoDeCompra> buscarFiltrandoRequisicaoCompraSemMovimentacao(String filtro, TipoBem tipoBem) {
        String sql = " select req.* from requisicaodecompra req " +
            "  where (req.numero like :filtro or lower(req.descricao) like :filtro ) " +
            "   and req.tipoobjetocompra = :tipoObjetoCompra " +
            "   and req.situacaorequisicaocompra in (:situacoesMovimentacoes) " +
            " order by req.numero desc ";
        Query q = em.createNativeQuery(sql, RequisicaoDeCompra.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("situacoesMovimentacoes", SituacaoRequisicaoCompra.getSituacoesAsString(SituacaoRequisicaoCompra.getSituacoesPermiteMovimentacoes()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        switch (tipoBem) {
            case MOVEIS: {
                q.setParameter("tipoObjetoCompra", TipoObjetoCompra.PERMANENTE_MOVEL.name());
                break;
            }
            case IMOVEIS: {
                q.setParameter("tipoObjetoCompra", TipoObjetoCompra.PERMANENTE_IMOVEL.name());
                break;
            }
        }
        return q.getResultList();
    }

    public BigDecimal valorTotalEmpenhadoDaObraPorContrato(Contrato contrato) {
        String sql = " SELECT sum(emp.VALOR) " +
            "      FROM obra " +
            "      INNER JOIN contrato ON contrato.id = obra.CONTRATO_ID " +
            "      INNER JOIN obraMedicao medicao on medicao.OBRA_ID = obra.id " +
            "      inner join obramedicaoexeccontrato omExec on omExec.obramedicao_id = medicao.id " +
            "      inner join execucaocontrato exec on exec.id = omExec.execucaocontrato_id " +
            "      inner join execucaocontratoempenho execEmp on execEmp.execucaocontrato_id = exec.id " +
            "      inner join empenho emp on emp.id = execEmp.empenho_id " +
            "      WHERE contrato.id = :contrato_id " +
            "      group by contrato.ID ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato_id", contrato.getId());
        return q.getResultList().isEmpty() ? BigDecimal.ZERO : (BigDecimal) q.getSingleResult();
    }

    public BigDecimal buscarQuantidadeRestante(ItemRequisicaoDeCompra itemReq) {
        String sql = " " +
            " select sum(quantidade) from ( " +
            "  select item.quantidade as quantidade " +
            "   from itemrequisicaodecompra item " +
            "  where item.id = :idItemReq " +
            " union all " +
            " select coalesce(est.quantidade, 0) * - 1 as quantidade " +
            "   from itemrequisicaocompraest est " +
            "   inner join itemrequisicaocompraexec irce on irce.id = est.itemrequisicaocompraexec_id " +
            " where irce.itemrequisicaocompra_id = :idItemReq) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemReq", itemReq.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return resultado.get(0);
    }

    public BigDecimal buscarQuantidadeTotal(RequisicaoDeCompra requisicaoDeCompra) {
        String sql = " select coalesce(sum(ircp.quantidade),0) as quantidade_total " +
            "           from itemrequisicaocompraexec ircp " +
            "           inner join itemrequisicaodecompra irc on irc.id = ircp.itemrequisicaocompra_id " +
            "          where irc.requisicaodecompra_id = :idRequisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", requisicaoDeCompra.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public List<ItemRequisicaoDeCompra> buscarItemRequisicaoDerivacaoComponente(ItemContrato itemContrato, RequisicaoDeCompra requisicao, DerivacaoObjetoCompra derivacaoObjetoCompra) {
        String sql = " select distinct irc.id, irc.numero from itemrequisicaodecompra irc " +
            "           inner join derivacaoobjcompracomp comp on comp.id = irc.derivacaocomponente_id " +
            "           inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = irc.id " +
            "           inner join execucaocontratoitem exitem on exitem.id = ircp.execucaocontratoitem_id " +
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "           and exitem.itemcontrato_id = :idItemContrato " +
            "           and comp.derivacaoobjetocompra_id = :idDerivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemContrato", itemContrato.getId());
        q.setParameter("idDerivacao", derivacaoObjetoCompra.getId());
        List resultado = q.getResultList();

        List<ItemRequisicaoDeCompra> itensReq = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) resultado) {
            ItemRequisicaoDeCompra itemReq = em.find(ItemRequisicaoDeCompra.class, ((BigDecimal) obj[0]).longValue());
            Hibernate.initialize(itemReq.getItensRequisicaoExecucao());
            itensReq.add(itemReq);
        }
        return itensReq;
    }

    public List<ItemRequisicaoDeCompra> buscarItensDesdobrado(ItemContrato itemContrato, RequisicaoDeCompra requisicao, Integer numeroItemProcesso) {
        String sql = " select irc.* from itemrequisicaodecompra irc " +
            "           inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = irc.id " +
            "           inner join execucaocontratoitem exitem on exitem.id = ircp.execucaocontratoitem_id " +
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "           and exitem.itemcontrato_id = :idItemContrato " +
            "           and irc.numeroitemprocesso = :numeroItemProcesso ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoDeCompra.class);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemContrato", itemContrato.getId());
        q.setParameter("numeroItemProcesso", numeroItemProcesso);
        List<ItemRequisicaoDeCompra> itensReq = q.getResultList();
        for (ItemRequisicaoDeCompra itemReq : itensReq) {
            Hibernate.initialize(itemReq.getItensRequisicaoExecucao());
        }
        return itensReq;
    }

    public DerivacaoObjetoCompra buscarDerivacaoObjetoCompraItemRequisicao(ItemContrato itemContrato, RequisicaoDeCompra requisicao) {
        String sql = " select distinct doc.* from derivacaoobjetocompra doc" +
            "           inner join derivacaoobjcompracomp comp on comp.derivacaoobjetocompra_id = doc.id " +
            "           inner join itemrequisicaodecompra irc on irc.derivacaocomponente_id = comp.id " +
            "           inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = irc.id " +
            "           inner join execucaocontratoitem exitem on exitem.id = ircp.execucaocontratoitem_id " +
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "          and exitem.itemcontrato_id = :idItemContrato ";
        Query q = em.createNativeQuery(sql, DerivacaoObjetoCompra.class);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemContrato", itemContrato.getId());
        try {
            return (DerivacaoObjetoCompra) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public ItemRequisicaoCompraExecucao getItemRequisicaoExecucaoPorItemExecucao(Long idItemExec, Long idFonteDespesaOrc, RequisicaoDeCompra requisicao) {
        if (requisicao.getId() == null) {
            return null;
        }
        String sql = " select irce.* from itemrequisicaocompraexec irce " +
            "           inner join itemrequisicaodecompra irc on irc.id = irce.itemrequisicaocompra_id " +
            "           inner join empenho emp on emp.id = irce.empenho_id " +
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "          and coalesce(irce.execucaocontratoitem_id, irce.execucaoprocessoitem_id, irce.itemreconhecimentodivida_id)  = :idItemExecucao " +
            "          and emp.fontedespesaorc_id = :idFonte ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoCompraExecucao.class);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemExecucao", idItemExec);
        q.setParameter("idFonte", idFonteDespesaOrc);
        try {
            return (ItemRequisicaoCompraExecucao) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public BigDecimal getValorEmRequisicao(ExecucaoContratoItem item) {
        String sql = " " +
            " select coalesce(sum(ircp.valortotal),0) as valortotal " +
            " from ItemRequisicaoDeCompra item " +
            "   inner join itemrequisicaocompraexec ircp on ircp.itemrequisicaocompra_id = item.id " +
            " where ircp.execucaocontratoitem_id = :idItemExecucao  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemExecucao", item.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public List<LiquidacaoVO> buscarLiquidacoesVOPorDocumentoFiscal(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        String sql = " select distinct " +
            "    liq.id as idLiq, " +
            "    'N° ' || liq.NUMERO || ' - ' || coalesce(pf.NOME, pj.RAZAOSOCIAL) || ' - ' || to_char(liq.DATALIQUIDACAO, 'dd/MM/yyyy')  || ' - ' || replace(to_char(liq.valor, 'FML999G999G990D00'), 'R$', 'R$ ') as liquidacao, " +
            "    le.id as idEstorno, " +
            "    le.NUMERO as estorno, " +
            "    liq.categoriaorcamentaria as categoriaOrcLiq " +
            "from liquidacao liq " +
            "    inner join EMPENHO emp on emp.ID = liq.EMPENHO_ID " +
            "    left join PESSOAFISICA pf on pf.ID = emp.FORNECEDOR_ID " +
            "    left join PESSOAJURIDICA pj on pj.ID = emp.FORNECEDOR_ID " +
            "    inner join LiquidacaoDoctoFiscal ldf on ldf.LIQUIDACAO_ID = liq.ID " +
            "    left join LIQUIDACAOESTORNO le on le.LIQUIDACAO_ID = liq.ID " +
            "where ldf.DOCTOFISCALLIQUIDACAO_ID = :idDoctoFiscalLiquidacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDoctoFiscalLiquidacao", doctoFiscalLiquidacao.getId());
        List<Object[]> resultado = q.getResultList();
        List<LiquidacaoVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            resultado.forEach(obj -> {
                retorno.add(new LiquidacaoVO(
                    ((BigDecimal) obj[0]).longValue(),
                    (String) obj[1],
                    obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null,
                    (String) obj[3],
                    CategoriaOrcamentaria.valueOf((String) obj[4])
                ));
            });
        }
        return retorno;
    }

    public List<EmpenhoDocumentoFiscal> buscarEmpenhosDocumentoFiscal(FiltroEmpenhoDocumentoFiscal filtro) {
        String sql = "" +
            "  select " +
            "      emp.id                                             as id_empenho, " +
            "      coalesce(iem.numeroitem, irc.numero)               as numero_item, " +
            "      case when mat.id is not null " +
            "           then mat.codigo || ' - ' || mat.descricao " +
            "           else oc.descricao end                          as descricao_item, " +
            "      gm.codigo || ' - ' || gm.descricao                  as grupo_item, " +
            "      irce.quantidade                                     as qtde, " +
            "      irce.valorunitario                                  as vl_unitario, " +
            "      irce.valortotal                                     as vl_total," +
            "      c.codigo || ' - ' || c.descricao                    as desdobramento_emp  " +
            " from itemrequisicaodecompra irc " +
            "         inner join requisicaodecompra req on req.id = irc.requisicaodecompra_id " +
            "         inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id " +
            "         inner join objetocompra oc on oc.id = irc.objetocompra_id " +
            "         inner join empenho emp on emp.id = irce.empenho_id " +
            "         inner join desdobramentoempenho desd on desd.empenho_id = emp.id " +
            "         inner join conta c on c.id = desd.conta_id " +
            "         inner join configgrupomaterial cgm on cgm.contadespesa_id = c.id " +
            "         inner join grupomaterial gm on gm.id = cgm.grupomaterial_id " +
            "         left join itemcompramaterial icm on icm.itemrequisicaodecompra_id = irc.id " +
            "         left join itementradamaterial iem on iem.id = icm.itementradamaterial_id " +
            "         left join material mat on mat.id = iem.material_id " +
            "         left join itemdoctoitementrada idie on idie.itementradamaterial_id = iem.id " +
            "         left join doctofiscalentradacompra dfec on dfec.id = idie.doctofiscalentradacompra_id " +
            "    where trunc(emp.dataempenho) between trunc(cgm.iniciovigencia) and coalesce(trunc(cgm.fimvigencia), trunc(emp.dataempenho)) ";
        sql += filtro.getCondicaoSql();
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();

        List<EmpenhoDocumentoFiscal> retorno = new ArrayList<>();
        Map<Long, List<EmpenhoDocumentoFiscalItem>> groupedItens = new HashMap<>();

        for (Object[] obj : resultado) {
            EmpenhoDocumentoFiscalItem item = new EmpenhoDocumentoFiscalItem();
            item.setIdEmpenho(((BigDecimal) obj[0]).longValue());
            item.setNumeroItem(((BigDecimal) obj[1]).intValue());
            item.setDescricaoItem((String) obj[2]);
            item.setGrupoItem((String) obj[3]);
            item.setQuantidade((BigDecimal) obj[4]);
            item.setValorUnitario((BigDecimal) obj[5]);
            item.setValorTotal((BigDecimal) obj[6]);

            groupedItens.computeIfAbsent(item.getIdEmpenho(), k -> new ArrayList<>()).add(item);
        }

        for (Map.Entry<Long, List<EmpenhoDocumentoFiscalItem>> entry : groupedItens.entrySet()) {
            Long idEmpenho = entry.getKey();
            List<EmpenhoDocumentoFiscalItem> itens = entry.getValue();

            EmpenhoDocumentoFiscal emp = new EmpenhoDocumentoFiscal();
            Empenho empenho = em.find(Empenho.class, idEmpenho);
            emp.setId(idEmpenho);
            emp.setEmpenho(empenho);
            emp.setItens(itens);

            resultado.stream()
                .filter(r -> ((BigDecimal) r[0]).longValue() == idEmpenho)
                .findFirst()
                .ifPresent(firstResult -> {
                    emp.setDesdobramentoEmpenho((String) firstResult[7]);
                });

            retorno.add(emp);
        }
        return retorno;
    }

    public BigDecimal getTotalizadorItensEmpenho(EmpenhoDocumentoFiscal empDoc) {
        BigDecimal total = BigDecimal.ZERO;
        if (empDoc != null) {
            for (EmpenhoDocumentoFiscalItem item : empDoc.getItens()) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }

    public ExecucaoContratoEstornoFacade getExecucaoContratoEstornoFacade() {
        return execucaoContratoEstornoFacade;
    }

    public RequisicaoCompraEstornoFacade getRequisicaoCompraEstornoFacade() {
        return requisicaoCompraEstornoFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterialFacade getAssocicaoGrupoMaterialFacade() {
        return associcaoGrupoMaterialFacade;
    }

    public GrupoObjetoCompraGrupoBemFacade getAssocicaoGrupoBemFacade() {
        return associcaoGrupoBemFacade;
    }

    public ItemPregaoFacade getItemPregaoFacade() {
        return itemPregaoFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public EntradaMaterialFacade getEntradaMaterialFacade() {
        return entradaMaterialFacade;
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }

    public SolicitacaoMaterialExternoFacade getSolicitacaoMaterialExternoFacade() {
        return solicitacaoMaterialExternoFacade;
    }

    public ReconhecimentoDividaFacade getReconhecimentoDividaFacade() {
        return reconhecimentoDividaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public ExecucaoProcessoEstornoFacade getExecucaoProcessoEstornoFacade() {
        return execucaoProcessoEstornoFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public ExecucaoProcessoFacade getExecucaoProcessoFacade() {
        return execucaoProcessoFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }

    public DerivacaoObjetoCompraComponenteFacade getDerivacaoObjetoCompraComponenteFacade() {
        return derivacaoObjetoCompraComponenteFacade;
    }

    public SolicitacaoAquisicaoFacade getSolicitacaoAquisicaoFacade() {
        return solicitacaoAquisicaoFacade;
    }
}
