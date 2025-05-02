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
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private MaterialFacade materialFacade;
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
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associcaoGrupoMaterial;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade associcaoGrupoBem;
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
            for (RequisicaoCompraExecucao execucao : entity.getExecucoes()) {
                if (execucao.getExecucaoContrato() != null) {
                    execucao.setExecucaoContrato(execucaoContratoFacade.recuperarComDependenciasExecucaoEmpenho(execucao.getExecucaoContrato().getId()));
                }
            }
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

    public List<RequisicaoDeCompra> recuperarFiltrando(String trim) {
        String sql = " select rc.* from requisicaodecompra rc " +
            "  where (to_char(rc.numero) like :numero or rc.descricao like :descricao)" +
            "  order by numero desc ";
        Query q = em.createNativeQuery(sql, RequisicaoDeCompra.class);
        q.setParameter("numero", "%" + trim + "%");
        q.setParameter("descricao", "%" + trim.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<RequisicaoDeCompra> buscarRequisicaoCompraCardapio(Cardapio cardapio) {
        String sql;
        sql = " select req.* from requisicaodecompra req " +
            "       inner join cardapiorequisicaocompra crc on crc.requisicaocompra_id = req.id " +
            "   where crc.cardapio_id = :idCardapio ";
        Query q = em.createNativeQuery(sql, RequisicaoDeCompra.class);
        q.setParameter("idCardapio", cardapio.getId());
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

    public List<ItemRequisicaoDeCompra> buscarItensRequisicao(RequisicaoDeCompra requisicaoCompra) {
        String sql = " select item.* from itemrequisicaodecompra item " +
            "          where item.requisicaodecompra_id = :idRequisicao " +
            "          order by item.numero ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoDeCompra.class);
        q.setParameter("idRequisicao", requisicaoCompra.getId());
        List<ItemRequisicaoDeCompra> itens = q.getResultList();
        if (itens == null || itens.isEmpty()) {
            return Lists.newArrayList();
        }
        return itens;
    }

    public List<ItemRequisicaoCompraExecucao> buscarItensRequisicaoExecucao(ItemRequisicaoDeCompra itemRequisicao) {
        String sql = " select item.* from itemrequisicaocompraexec item " +
            "          where item.itemrequisicaocompra_id = :idItem ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoCompraExecucao.class);
        q.setParameter("idItem", itemRequisicao.getId());
        List itens = q.getResultList();
        if (Util.isListNullOrEmpty(itens)) {
            return Lists.newArrayList();
        }
        return itens;
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

    public List<RequisicaoExecucaoExecucaoVO> buscarExecucoesEmpenhadas(Long idProcesso) {
        String sql;
        sql = " " +
            " select id, " +
            "        numero, " +
            "        data_lancamento," +
            "        valor, " +
            "        tipo " +
            "  from (" +
            "       select distinct" +
            "           ex.id as id, " +
            "           ex.numero as numero, " +
            "           ex.datalancamento as data_lancamento," +
            "           ex.valor as valor, " +
            "           'CONTRATO' as tipo " +
            "          from execucaocontrato ex " +
            "           inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id  " +
            "           inner join empenho emp on emp.id = exemp.empenho_id  " +
            "          where ex.contrato_id = :idProcesso " +
            "           and ex.operacaoorigem = :operacaoPreExecucao " +
            "          union all " +
            "         select distinct " +
            "           ex.id as id, " +
            "           ex.numero as numero, " +
            "           ex.datalancamento as data_lancamento," +
            "           ex.valor as valor, " +
            "           'ATA_REGISTRO_PRECO' as tipo " +
            "          from execucaoprocesso ex " +
            "           inner join execucaoprocessoempenho exemp on exemp.execucaoprocesso_id = ex.id " +
            "           inner join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "           inner join empenho emp on emp.id = exemp.empenho_id  " +
            "          where exata.ataregistropreco_id = :idProcesso" +
            "          union all " +
            "         select distinct " +
            "           ex.id as id, " +
            "           ex.numero as numero, " +
            "           ex.datalancamento as data_lancamento," +
            "           ex.valor as valor, " +
            "           'DISPENSA_LICITACAO_INEXIGIBILIDADE' as tipo " +
            "          from execucaoprocesso ex " +
            "           inner join execucaoprocessoempenho exemp on exemp.execucaoprocesso_id = ex.id " +
            "           inner join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "           inner join empenho emp on emp.id = exemp.empenho_id  " +
            "          where exdisp.dispensalicitacao_id = :idProcesso" +
            ") order by numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", idProcesso);
        q.setParameter("operacaoPreExecucao", OperacaoSaldoItemContrato.PRE_EXECUCAO.name());
        List<RequisicaoExecucaoExecucaoVO> list = Lists.newArrayList();
        try {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RequisicaoExecucaoExecucaoVO item = new RequisicaoExecucaoExecucaoVO();
                item.setId(((BigDecimal) obj[0]).longValue());
                item.setNumero(((BigDecimal) obj[1]).intValue());
                item.setDataLancamento((Date) obj[2]);
                item.setValorExecucao((BigDecimal) obj[3]);
                item.setTipoExecucao(TipoRequisicaoCompra.valueOf((String) obj[4]));
                if (item.getTipoExecucao().isContrato()) {
                    item.setExecucaoContrato(em.find(ExecucaoContrato.class, item.getId()));
                } else {
                    item.setExecucaoProcesso(em.find(ExecucaoProcesso.class, item.getId()));
                }
                list.add(item);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public List<ItemProcessoLicitacao> buscarItensProcesso(Long idProcesso) {
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
            " where item.execucaocontrato_id = :idProcesso " +
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
            " where exproc.id = :idProcesso " +
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
            " where item.execucaoprocesso_id = :idProcesso " +
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
            " where rd.id = :idProcesso ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", idProcesso);
        List<ItemProcessoLicitacao> list = Lists.newArrayList();
        try {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ItemProcessoLicitacao item = new ItemProcessoLicitacao();
                item.setIdItem(((BigDecimal) obj[0]).longValue());
                item.setTipoControle(TipoControleLicitacao.valueOf((String) obj[1]));
                item.setTipoProcesso(TipoRequisicaoCompra.valueOf((String) obj[2]));
                item.setTipoContaDespesa(obj[3] != null ? TipoContaDespesa.valueOf((String) obj[3]) : TipoContaDespesa.NAO_APLICAVEL);
                if (item.getTipoProcesso().isContrato()) {
                    item.setItemExecucaoContrato(em.find(ExecucaoContratoItem.class, item.getIdItem()));
                } else if (item.getTipoProcesso().isExecucaoProcesso()) {
                    item.setItemExecucaoProcesso(em.find(ExecucaoProcessoItem.class, item.getIdItem()));
                } else {
                    item.setItemReconhecimentoDivida(em.find(ItemReconhecimentoDivida.class, item.getIdItem()));
                }
                list.add(item);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
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

    public BigDecimal getQuantidadeUtilizadaItemExecucaoContrato(ExecucaoContratoItem itemExecucaoContrato, ItemRequisicaoCompraExecucao itemRequisicaoExecucao) {
        String sql = " " +
            "           select coalesce(sum(irce.quantidade),0) as quantidadeUtilizada      " +
            "           from requisicaodecompra rdc                                                       " +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = rdc.id     " +
            "           inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id     " +
            "           where irce.execucaocontratoitem_id = :idItemExecucao  " +
            "           and rdc.situacaorequisicaocompra <> :estornada  ";
        if (itemRequisicaoExecucao != null && itemRequisicaoExecucao.getId() != null) {
            sql += "    and irce.id <> :idItemReqExecucao  ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemExecucao", itemExecucaoContrato.getId());
        if (itemRequisicaoExecucao != null && itemRequisicaoExecucao.getId() != null) {
            q.setParameter("idItemReqExecucao", itemRequisicaoExecucao.getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getQuantidadeEmRequisicaoItemExecucaoProcesso(ExecucaoProcessoItem itemExecucaoProcesso, ItemRequisicaoDeCompra itemRequisicao) {
        String sql = " " +
            "           select coalesce(sum(irc.quantidade),0) as quantidade      " +
            "           from requisicaodecompra req" +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = req.id                                                       " +
            "           where irc.execucaoprocessoitem_id = :idItemAta  " +
            "           and req.situacaorequisicaocompra <> :estornada  ";
        if (itemRequisicao != null && itemRequisicao.getId() != null) {
            sql += "    and irc.id <> :itemRequisicao  ";
        }
        if (itemRequisicao != null && itemRequisicao.getRequisicaoDeCompra().getId() != null) {
            sql += "    and req.id <> :idRequisicao  ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemAta", itemExecucaoProcesso.getId());
        if (itemRequisicao != null && itemRequisicao.getId() != null) {
            q.setParameter("itemRequisicao", itemRequisicao.getId());
        }
        if (itemRequisicao != null && itemRequisicao.getRequisicaoDeCompra().getId() != null) {
            q.setParameter("idRequisicao", itemRequisicao.getRequisicaoDeCompra().getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getQuantidadeUtilizadaItemReconhecimentoDivida(ItemReconhecimentoDivida itemReconhecimento, ItemRequisicaoDeCompra itemRequisicao) {
        String sql = " " +
            "           select coalesce(sum(irc.quantidade),0) as quantidadeUtilizada      " +
            "           from requisicaodecompra rdc                                                       " +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = rdc.id     " +
            "           where irc.itemreconhecimentodivida_id = :idItemReconhecimento  " +
            "           and rdc.situacaorequisicaocompra <> :estornada  ";
        if (itemRequisicao != null && itemRequisicao.getId() != null) {
            sql += "    and irc.id <> :itemRequisicao  ";
        }
        if (itemRequisicao != null && itemRequisicao.getRequisicaoDeCompra().getId() != null) {
            sql += "    and rdc.id <> :idRequisicao  ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemReconhecimento", itemReconhecimento.getId());
        if (itemRequisicao != null && itemRequisicao.getId() != null) {
            q.setParameter("itemRequisicao", itemRequisicao.getId());
        }
        if (itemRequisicao != null && itemRequisicao.getRequisicaoDeCompra().getId() != null) {
            q.setParameter("idRequisicao", itemRequisicao.getRequisicaoDeCompra().getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorUtilizadoItemExecucaoContrato(ExecucaoContratoItem itemExecucaoContrato, List<Long> idsItemReqDesdobrados) {
        String sql = " " +
            "           select coalesce(sum(irce.valortotal),0) as quantidadeUtilizada      " +
            "           from requisicaodecompra rdc                                                       " +
            "           inner join itemrequisicaodecompra irc on irc.requisicaodecompra_id = rdc.id     " +
            "           inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id     " +
            "           where irce.execucaocontratoitem_id = :idItemExecucao  " +
            "           and rdc.situacaorequisicaocompra <> :estornada ";
        sql += !Util.isListNullOrEmpty(idsItemReqDesdobrados) ? " and irc.id not in (:idsItemReqDesdobrados) " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemExecucao", itemExecucaoContrato.getId());
        if (!Util.isListNullOrEmpty(idsItemReqDesdobrados)){
            q.setParameter("idsItemReqDesdobrados", idsItemReqDesdobrados);
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorUtilizadoItemExecucaoProcesso(ExecucaoProcessoItem itemExecucaoProcesso, List<Long> idsItemReqDesdobrados) {
        String sql = " " +
            "           select coalesce(sum(item.valortotal),0) as valor_utilizado      " +
            "           from requisicaodecompra rdc " +
            "           inner join itemrequisicaodecompra item on item.requisicaodecompra_id = rdc.id                                                       " +
            "           where item.execucaoprocessoitem_id = :idItemAta  " +
            "           and rdc.situacaorequisicaocompra <> :estornada  ";
        sql += !Util.isListNullOrEmpty(idsItemReqDesdobrados) ? " and item.id not in (:idsItemReqDesdobrados) " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("estornada", SituacaoRequisicaoCompra.ESTORNADA.name());
        q.setParameter("idItemAta", itemExecucaoProcesso.getId());
        if (!Util.isListNullOrEmpty(idsItemReqDesdobrados)) {
            q.setParameter("idsItemReqDesdobrados", idsItemReqDesdobrados);
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public RequisicaoDeCompra salvarRequisicao(RequisicaoDeCompra entity) {
        if (entity.getId() == null && entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(RequisicaoDeCompra.class, "numero"));
        }
        entity = movimentarSituacaoRequisicaoCompra(entity, SituacaoRequisicaoCompra.EM_ELABORACAO);
        return entity;
    }

    public Pessoa getFornecedorRequisicao(RequisicaoDeCompra requisicaoCompra) {
        String hql = " select coalesce(pCont, pRd, pExProc) from RequisicaoDeCompra req " +
            "           inner join req.execucoes rce " +
            "           left join req.contrato c" +
            "           left join req.reconhecimentoDivida rc " +
            "           left join rce.execucaoProcesso exproc " +
            "           left join c.contratado pCont  " +
            "           left join rc.fornecedor pRd  " +
            "           left join exproc.fornecedor pExProc  " +
            "        where req.id = :idRequisicao ";
        Query q = em.createQuery(hql);
        q.setParameter("idRequisicao", requisicaoCompra.getId());
        try {
            return (Pessoa) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public UnidadeOrganizacional getUnidadeAdministrativa(RequisicaoDeCompra requisicaoCompra, Date dataOperacao) {
        String sql = " select unid.* from requisicaodecompra req " +
            "         left join contrato c on c.id = req.contrato_id " +
            "         left join unidadecontrato uc on uc.contrato_id = c.id " +
            "              and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "         left join reconhecimentodivida rc on rc.id = req.reconhecimentodivida_id " +
            "         left join requisicaocompraexecucao rce on rce.requisicaocompra_id = req.id " +
            "         left join execucaoprocesso exproc on exproc.id = rce.execucaoprocesso_id " +
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
            "             left join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = item.id" +
            "             left join ITEMREQUISICAOCOMPRAEST est on est.itemrequisicaocompraexec_id = irce.id " +
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

    public BigDecimal buscarQuantidadeRestante(ItemRequisicaoDeCompra item) {
        String sql = " select sum(quantidade) from (" +
            " select item.quantidade as quantidade from ItemRequisicaoDeCompra item " +
            " where item.id = :itemId " +
            " union all " +
            " select coalesce(est.quantidade, 0) * - 1 as quantidade " +
            "   from ITEMREQUISICAOCOMPRAEST est " +
            "   inner join itemrequisicaocompraexec irce on irce.id = est.itemrequisicaocompraexec_id" +
            " where irce.itemRequisicaoCompra_id = :itemId ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("itemId", item.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public BigDecimal buscarQuantidadeTotal(RequisicaoDeCompra requisicaoDeCompra) {
        String sql = " select coalesce(sum(irce.quantidade),0) as quantidade_total " +
            "           from itemrequisicaocompraexec irce " +
            "           inner join itemrequisicaodecompra irc on irc.id = irce.itemrequisicaocompra_id " +
            "          where irc.requisicaodecompra_id = :idRequisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idRequisicao", requisicaoDeCompra.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public ItemRequisicaoDeCompra getItemRequisicaoPorItemContrato(ItemContrato itemContrato, RequisicaoDeCompra requisicao) {
        String sql = " select irc.* from itemrequisicaodecompra irc " +
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "          and irc.itemcontrato_id = :idItemContrato ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoDeCompra.class);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemContrato", itemContrato.getId());
        try {
            return (ItemRequisicaoDeCompra) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public List<ItemRequisicaoDeCompra> buscarItemRequisicaoDerivacaoComponente(ItemContrato itemContrato, RequisicaoDeCompra requisicao, DerivacaoObjetoCompra derivacaoObjetoCompra) {
        String sql = " select irc.* from itemrequisicaodecompra irc " +
            "           inner join derivacaoobjcompracomp comp on comp.id = irc.derivacaocomponente_id " +
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "          and irc.itemcontrato_id = :idItemContrato " +
            "          and comp.derivacaoobjetocompra_id = :idDerivacao ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoDeCompra.class);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemContrato", itemContrato.getId());
        q.setParameter("idDerivacao", derivacaoObjetoCompra.getId());
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
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "          and irc.itemcontrato_id = :idItemContrato ";
        Query q = em.createNativeQuery(sql, DerivacaoObjetoCompra.class);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemContrato", itemContrato.getId());
        try {
            return (DerivacaoObjetoCompra) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public ItemRequisicaoDeCompra getItemRequisicaoPorItemExecucaoProcesso(ExecucaoProcessoItem execucaoProcessoItem, RequisicaoDeCompra requisicao) {
        String sql = " select irc.* from itemrequisicaodecompra irc " +
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "          and irc.execucaoprocessoitem_id = :idItemExecProc ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoDeCompra.class);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemExecProc", execucaoProcessoItem.getId());
        try {
            return (ItemRequisicaoDeCompra) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public ItemRequisicaoCompraExecucao getItemRequisicaoExecucaoPorItemExecucao(ExecucaoContratoItem itemExecucao, RequisicaoDeCompra requisicao) {
        String sql = " select irce.* from itemrequisicaocompraexec irce " +
            "           inner join itemrequisicaodecompra irc on irc.id = irce.itemrequisicaocompra_id " +
            "          where irc.requisicaodecompra_id = :idRequisicao " +
            "          and irce.execucaocontratoitem_id = :idItemExecucao ";
        Query q = em.createNativeQuery(sql, ItemRequisicaoCompraExecucao.class);
        q.setParameter("idRequisicao", requisicao.getId());
        q.setParameter("idItemExecucao", itemExecucao.getId());
        try {
            return (ItemRequisicaoCompraExecucao) q.getSingleResult();
        } catch (NoResultException | NonUniqueResultException e) {
            return null;
        }
    }

    public BigDecimal getValorEmRequisicao(ExecucaoContratoItem item) {
        String sql = " " +
            " select coalesce(sum(irce.valortotal),0) as valortotal " +
            " from ItemRequisicaoDeCompra item " +
            "   inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = item.id " +
            " where irce.execucaocontratoitem_id = :idItemExecucao  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemExecucao", item.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public BigDecimal buscarQuantidadeRestantePorItemExecucao(ExecucaoContratoItem item) {
        String sql = " select coalesce(sum(quantidade),0) from (" +
            " select irce.quantidade as quantidade from ItemRequisicaoDeCompra item " +
            " inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = item.id " +
            " where irce.execucaocontratoitem_id = :idItemExecucao  " +
            " union all " +
            " select coalesce(est.quantidade, 0) * - 1 as quantidade from ITEMREQUISICAOCOMPRAEST est " +
            " inner join itemrequisicaocompraexec irce on irce.id = est.itemrequisicaocompraexec_id     " +
            " where irce.execucaocontratoitem_id = :idItemExecucao)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemExecucao", item.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return resultado.get(0);
    }

    public BigDecimal buscarValorEmRequisicaoCompraItemExecucaoProcesso(ExecucaoProcessoItem item) {
        String sql = " " +
            " select coalesce(sum(valor_total),0) " +
            " from (" +
            "  select item.valortotal as valor_total " +
            "  from ItemRequisicaoDeCompra item " +
            "  where item.execucaoprocessoitem_id = :idItemExecucao  " +
            "  union all " +
            "  select (coalesce(est.quantidade, 0) * (item.valorunitario)) * - 1 as valor_total " +
            "  from ITEMREQUISICAOCOMPRAEST est " +
            "   inner join ItemRequisicaoDeCompra item on item.id = est.itemrequisicaocompra_id     " +
            "  where item.execucaoprocessoitem_id = :idItemExecucao" +
            " ) ";
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

    public RequisicaoDeCompra movimentarSituacaoRequisicaoCompra(RequisicaoDeCompra requisicaoCompra, SituacaoRequisicaoCompra situacao) {
        requisicaoCompra.setSituacaoRequisicaoCompra(situacao);
        requisicaoCompra = em.merge(requisicaoCompra);
        return requisicaoCompra;
    }

    public List<EmpenhoDocumentoFiscal> buscarEmpenhosDocumentoFiscal(FiltroEmpenhoDocumentoFiscal filtro) {
        String sql = "select " +
            "       emp.id                                                       as id_empenho, " +
            "       coalesce(iem.numeroitem, irc.numero)                         as numero_item, " +
            "       coalesce(mat.codigo || ' - ' || mat.descricao, oc.descricao) as descricao_item, " +
            "       gmitem.codigo || ' - ' || gmitem.descricao                   as grupo_item, " +
            "       case " +
            "           when req.tiporequisicao = 'CONTRATO' then coalesce(irce.quantidade, 1) " +
            "           else irc.quantidade end                                  as qtde, " +
            "       case " +
            "           when req.tiporequisicao = 'CONTRATO' then irce.valorunitario " +
            "           else irc.valorunitario end                               as vl_unitario, " +
            "       case " +
            "           when req.tiporequisicao = 'CONTRATO' then irce.valortotal " +
            "           else irc.valortotal end                                  as vl_total," +
            "       cliq.codigo || ' - ' || cliq.DESCRICAO                       as desdobramento_emp  " +
            "from itemrequisicaodecompra irc " +
            "         inner join requisicaodecompra req on req.id = irc.requisicaodecompra_id " +
            "         left join itemcompramaterial icm on icm.itemrequisicaodecompra_id = irc.id " +
            "         left join itementradamaterial iem on iem.id = icm.itementradamaterial_id " +
            "         left join material mat on mat.id = iem.material_id " +
            "         left join itemdoctoitementrada idie on idie.itementradamaterial_id = iem.id " +
            "         left join doctofiscalentradacompra dfec on dfec.id = idie.doctofiscalentradacompra_id ";

        if (filtro.getTipoRequisicaoCompra().isReconhecimentoDivida()) {
            sql += "   inner join itemreconhecimentodivida itemrd on itemrd.id = irc.itemreconhecimentodivida_id " +
                "      inner join objetocompra oc on oc.id = itemrd.objetocompra_id " +
                "      inner join reconhecimentodivida rd on rd.id = req.reconhecimentodivida_id " +
                "      inner join solicitacaoempenho sol on sol.reconhecimentodivida_id = rd.id " +
                "      inner join empenho emp on emp.id = sol.empenho_id " +
                "      left join itemrequisicaocompraexec irce on irc.id = irce.itemrequisicaocompra_id " ;
        } else if (filtro.getTipoRequisicaoCompra().isContrato()) {
            sql += "   inner join itemrequisicaocompraexec irce on irce.itemrequisicaocompra_id = irc.id " +
                "      inner join execucaocontratoitem eci on eci.id = irce.execucaocontratoitem_id " +
                "      inner join itemcontrato ic on ic.id = eci.itemcontrato_id " +
                "      left join itemcontratovigente icv on icv.itemcontrato_id = ic.id " +
                "      left join itemcotacao icot on icot.id = icv.itemcotacao_id " +
                "      left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                "      left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                "      left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                "      left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                "      left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                "      left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
                "      left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, " +
                "                                               iata.itempropostafornecedor_id) = ipf.id " +
                "      left join itemprocessodecompra ipc " +
                "                on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                "      left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
                "      left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
                "      inner join objetocompra oc on oc.id = coalesce(ic.objetocompracontrato_id, itemsol.objetocompra_id, " +
                "                                                     ise.objetocompra_id, icot.objetocompra_id) " +
                "      inner join execucaocontrato ex on ex.id = eci.execucaocontrato_id " +
                "      inner join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
                "      inner join empenho emp on emp.id = exemp.empenho_id ";
        } else {
            sql += "   inner join execucaoprocessoitem itemexproc on itemexproc.id = irc.execucaoprocessoitem_id " +
                "      inner join execucaoprocesso exproc on exproc.id = itemexproc.execucaoprocesso_id " +
                "      inner join execucaoprocessoempenho exprocemp on exprocemp.execucaoprocesso_id = exproc.id " +
                "      inner join empenho emp on emp.id = exprocemp.empenho_id " +
                "      inner join objetocompra oc on coalesce(irc.objetocompra_id, mat.objetocompra_id) = oc.id " +
                "      left join itemrequisicaocompraexec irce on irc.id = irce.itemrequisicaocompra_id ";
        }
        sql += "  inner join exercicio ex on emp.exercicio_id = ex.id " +
            "     inner join desdobramentoempenho desd on desd.empenho_id = emp.id " +
            "     inner join conta cliq on cliq.id = desd.conta_id " +
            "     inner join configgrupomaterial cgm on cgm.contadespesa_id = cliq.id " +
            "     inner join grupomaterial gmemp on gmemp.id = cgm.grupomaterial_id " +
            "     inner join associacaogruobjcomgrumat agm on agm.grupoobjetocompra_id = oc.grupoobjetocompra_id " +
            "     inner join grupomaterial gmitem on gmitem.id = agm.grupomaterial_id " +
            "where trunc(emp.dataempenho) between trunc(agm.iniciovigencia) and coalesce(trunc(agm.finalvigencia), " +
            "                                                                                         trunc(emp.dataempenho)) " +
            "  and trunc(emp.dataempenho) between trunc(cgm.iniciovigencia) and coalesce(trunc(cgm.fimvigencia), " +
            "                                                                                         trunc(emp.dataempenho)) " +
            "  and gmemp.id = gmitem.id " ;
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

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterialFacade getAssocicaoGrupoMaterial() {
        return associcaoGrupoMaterial;
    }

    public GrupoObjetoCompraGrupoBemFacade getAssocicaoGrupoBem() {
        return associcaoGrupoBem;
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

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
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
}
