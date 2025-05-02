package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteExclusaoMateriais;
import br.com.webpublico.entidadesauxiliares.ExclusaoMateriaisItensVO;
import br.com.webpublico.entidadesauxiliares.ExclusaoMateriaisVO;
import br.com.webpublico.enums.SituacaoRequisicaoCompra;
import br.com.webpublico.enums.SituacaoRequisicaoMaterial;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ExclusaoMateriaisFacade extends AbstractFacade<ExclusaoMateriais> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private AprovacaoMaterialFacade aprovacaoMaterialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;

    public ExclusaoMateriaisFacade() {
        super(ExclusaoMateriais.class);
    }

    public ExclusaoMateriais salvarRetornando(AssistenteExclusaoMateriais assistente) {
        ExclusaoMateriais entity = assistente.getSelecionado();
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ExclusaoMateriais.class, "numero"));
        }
        excluirMovimentos(assistente);
        return super.salvarRetornando(entity);
    }

    public void excluirMovimentos(AssistenteExclusaoMateriais assistente) {
        ExclusaoMateriais entity = assistente.getSelecionado();
        for (ExclusaoMateriaisVO rel : assistente.getMovimentos()) {
            if (entity.getTipo().isEntradaCompra()) {
                if (rel.getSelecionado()) {
                    excluirEntrada(rel);
                }
            }
            if (entity.getTipo().isSaidaConsumo()
                || entity.getTipo().isRequisicaoConsumo()
                || entity.getTipo().isAvaliacaoMaterial()) {
                if (rel.getSelecionado()) {
                    excluirMovimentosSaida(rel);
                }
            }
        }
    }

    private void excluirMovimentosSaida(ExclusaoMateriaisVO rel) {
        if (rel.getTipoRelacionamento().isBensEstoqueSaida()) {
            BensEstoque bensEstoque = em.find(BensEstoque.class, rel.getSaidaMaterialContabil().getBensEstoque().getId());
            em.remove(bensEstoque);

            SaidaMaterialContabil saidaMaterialContabil = em.find(SaidaMaterialContabil.class, rel.getSaidaMaterialContabil().getId());
            em.remove(saidaMaterialContabil);
        }
        if (rel.getTipoRelacionamento().isSaidaMaterial()) {
            SaidaMaterial saida = saidaMaterialFacade.recuperar(rel.getSaidaMaterial().getId());
            for (ItemSaidaMaterial item : saida.getListaDeItemSaidaMaterial()) {
                ItemRequisicaoMaterial itemRequisicao = item.getItemRequisicaoSaidaMaterial().getItemRequisicaoMaterial();
                if (itemRequisicao.getQuantidadeAtendida() != null) {
                    itemRequisicao.setQuantidadeAtendida(itemRequisicao.getQuantidadeAtendida().subtract(item.getQuantidade()));
                    em.merge(itemRequisicao);
                }
            }
            SaidaRequisicaoMaterial saidaRequisicao = (SaidaRequisicaoMaterial) saida;
            RequisicaoMaterial requisicaoMaterial = saidaRequisicao.getRequisicaoMaterial();
            requisicaoMaterial.setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.ATENDIDA_PARCIALMENTE);
            em.merge(requisicaoMaterial);

            saidaMaterialFacade.remover(saida);
        }
        if (rel.getTipoRelacionamento().isAprovacaoMaterial()) {
            AprovacaoMaterial aprovacao = aprovacaoMaterialFacade.recuperar(rel.getAprovacaoMaterial().getId());

            List<ReservaEstoque> reservas = Lists.newArrayList();
            for (ItemAprovacaoMaterial iam : aprovacao.getItensAprovados()) {
                ReservaEstoque o = aprovacaoMaterialFacade.recuperarReservaEstoque(iam);
                reservas.add(o);
            }
            for (ReservaEstoque reserva : reservas) {
                if (reserva != null) {
                    em.remove(reserva);
                }
            }
            RequisicaoMaterial requisicaoMaterial = aprovacao.getRequisicaoMaterial();
            aprovacao.setRequisicaoMaterial(requisicaoMaterialFacade.recuperar(requisicaoMaterial.getId()));
            aprovacao.zerarQuantidadeAprovadaDosItensRequisicaoMaterial();
            requisicaoMaterial.setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.NAO_AVALIADA);
            em.merge(requisicaoMaterial);

            em.remove(aprovacao);
        }
        em.flush();
        if (rel.getTipoRelacionamento().isRequisicaoMaterial()) {
            requisicaoMaterialFacade.remover(requisicaoMaterialFacade.recuperar(rel.getRequisicaoMaterial().getId()));
        }
    }

    private void excluirEntrada(ExclusaoMateriaisVO rel) {
        EntradaMaterial entrada = entradaMaterialFacade.recuperar(rel.getEntradaMaterial().getId());
        EntradaCompraMaterial entradaCompra = (EntradaCompraMaterial) entrada;

        if (rel.getTipoRelacionamento().isBensEstoqueEntrada()) {
            BensEstoque bensEstoque = em.find(BensEstoque.class, rel.getEntradaMaterialContabil().getBensEstoque().getId());
            em.remove(bensEstoque);

            EntradaMaterialContabil entradaMaterialContabil = em.find(EntradaMaterialContabil.class, rel.getEntradaMaterialContabil().getId());
            em.remove(entradaMaterialContabil);
        }
        requisicaoDeCompraFacade.movimentarSituacaoRequisicaoCompra(entradaCompra.getRequisicaoDeCompra(), SituacaoRequisicaoCompra.EM_ELABORACAO);
        em.remove(entradaCompra);
    }

    public List<ExclusaoMateriaisVO> buscarMovimentos(ExclusaoMateriais entity) {
        String sql = "";
        switch (entity.getTipo()) {
            case SAIDA_CONSUMO:
                sql = " " +
                    " select saida.id                 as id_mov, " +
                    "       saida.datasaida          as data_mov, " +
                    "       'SAIDA_MATERIAL_CONSUMO' as tipo_mov " +
                    " from saidamaterial saida " +
                    "         inner join saidarequisicaomaterial srm on srm.id = saida.id " +
                    " where saida.id = :idMovimento " +
                    " union all " +
                    " select ap.id                as id_mov, " +
                    "       ap.datadaaprovacao   as data_mov, " +
                    "       'APROVACAO_MATERIAL' as tipo_mov " +
                    " from saidamaterial saida " +
                    "         inner join saidarequisicaomaterial srm on srm.id = saida.id " +
                    "         inner join requisicaomaterial req on req.id = srm.requisicaomaterial_id " +
                    "         inner join aprovacaomaterial ap on ap.requisicaomaterial_id = req.id " +
                    " where saida.id = :idMovimento " +
                    " union all " +
                    " select req.id                as id_mov, " +
                    "       req.datarequisicao    as data_mov, " +
                    "       'REQUISICAO_MATERIAL' as tipo_mov " +
                    " from saidamaterial saida " +
                    "         inner join saidarequisicaomaterial srm on srm.id = saida.id " +
                    "         inner join requisicaomaterial req on req.id = srm.requisicaomaterial_id " +
                    " where saida.id = :idMovimento " +
                    " union all " +
                    " select smc.id          as id_mov, " +
                    "        be.databem     as data_mov, " +
                    "       'BENS_ESTOQUE_SAIDA'  as tipo_mov " +
                    " from saidamaterial saida " +
                    "         inner join saidarequisicaomaterial srm on srm.id = saida.id " +
                    "         inner join saidamaterialcontabil smc on smc.saidamaterial_id = saida.id " +
                    "         inner join bensestoque be on be.id = smc.bensestoque_id " +
                    " where saida.id = :idMovimento ";
                break;
            case ENTRADA_COMPRA:
                sql = " " +
                    " select " +
                    "    ecm.id as id_mov," +
                    "    ent.dataentrada as data_mov," +
                    "    'ENTRADA_MATERIAL_COMPRA' as tipo_mov " +
                    "  from entradacompramaterial ecm " +
                    "    inner join entradamaterial ent on ent.id = ecm.id " +
                    "  where ecm.id = :idMovimento " +
                    " union all " +
                    " select " +
                    "    liq.id as id_mov," +
                    "    liq.dataliquidacao as data_mov," +
                    "    'LIQUIDACAO' as tipo_mov " +
                    "  from entradacompramaterial ecm " +
                    "    inner join doctofiscalentradacompra det on ecm.id = det.entradacompramaterial_id " +
                    "    inner join LIQUIDACAODOCTOFISCAL ldf on ldf.DOCTOFISCALLIQUIDACAO_ID = det.DOCTOFISCALLIQUIDACAO_ID " +
                    "    inner join liquidacao liq on liq.id = ldf.LIQUIDACAO_ID " +
                    "  where ecm.id = :idMovimento " +
                    " union all " +
                    " select emc.id          as id_mov, " +
                    "        be.databem     as data_mov, " +
                    "       'BENS_ESTOQUE_ENTRADA'  as tipo_mov " +
                    " from entradacompramaterial ecm " +
                    "    inner join entradamaterial ent on ent.id = ecm.id " +
                    "    inner join entradamaterialcontabil emc on emc.entradamaterial_id = ent.id " +
                    "    inner join bensestoque be on be.id = emc.bensestoque_id " +
                    " where ent.id = :idMovimento ";
                break;
            case REQUISICAO_MATERIAL_CONSUMO:
                sql = " " +
                    " select req.id                as id_mov, " +
                    "        req.datarequisicao    as data_mov, " +
                    "        'REQUISICAO_MATERIAL' as tipo_mov " +
                    " from requisicaomaterial req  " +
                    " where req.id = :idMovimento " +
                    " union all " +
                    " select ap.id                as id_mov, " +
                    "        ap.datadaaprovacao   as data_mov, " +
                    "        'APROVACAO_MATERIAL' as tipo_mov " +
                    " from requisicaomaterial req " +
                    "         inner join aprovacaomaterial ap on ap.requisicaomaterial_id = req.id " +
                    " where req.id = :idMovimento " +
                    " union all " +
                    " select saida.id                as id_mov, " +
                    "        saida.datasaida    as data_mov, " +
                    "        'SAIDA_MATERIAL_CONSUMO' as tipo_mov " +
                    " from saidamaterial saida " +
                    "         inner join saidarequisicaomaterial srm on srm.id = saida.id " +
                    "         inner join requisicaomaterial req on req.id = srm.requisicaomaterial_id " +
                    " where req.id = :idMovimento " +
                    " union all " +
                    " select smc.id          as id_mov, " +
                    "        be.databem     as data_mov, " +
                    "       'BENS_ESTOQUE_SAIDA'  as tipo_mov " +
                    " from saidamaterial saida " +
                    "         inner join saidarequisicaomaterial srm on srm.id = saida.id " +
                    "         inner join saidamaterialcontabil smc on smc.saidamaterial_id = saida.id " +
                    "         inner join bensestoque be on be.id = smc.bensestoque_id " +
                    "         inner join requisicaomaterial req on req.id = srm.requisicaomaterial_id " +
                    " where req.id = :idMovimento ";
                break;
            case AVALIACAO_MATERIAL:
                sql = " " +
                    " select ap.id                as id_mov, " +
                    "        ap.datadaaprovacao   as data_mov, " +
                    "        'APROVACAO_MATERIAL' as tipo_mov " +
                    " from aprovacaomaterial ap  " +
                    " where ap.id = :idMovimento " +
                    " union all " +
                    " select req.id               as id_mov, " +
                    "       req.datarequisicao    as data_mov, " +
                    "       'REQUISICAO_MATERIAL' as tipo_mov " +
                    " from aprovacaomaterial ap" +
                    "         inner join requisicaomaterial req on req.id = ap.requisicaomaterial_id " +
                    " where ap.id = :idMovimento " +
                    " union all " +
                    " select saida.id                 as id_mov, " +
                    "        saida.datasaida          as data_mov, " +
                    "        'SAIDA_MATERIAL_CONSUMO' as tipo_mov " +
                    " from aprovacaomaterial ap " +
                    "         inner join requisicaomaterial req on req.id = ap.requisicaomaterial_id " +
                    "         inner join saidarequisicaomaterial srm on srm.requisicaomaterial_id = req.id " +
                    "         inner join saidamaterial saida on srm.id = saida.id " +
                    " where ap.id = :idMovimento " +
                    " union all " +
                    " select smc.id          as id_mov, " +
                    "        be.databem     as data_mov, " +
                    "       'BENS_ESTOQUE_SAIDA'  as tipo_mov " +
                    " from aprovacaomaterial ap " +
                    "         inner join requisicaomaterial req on req.id = ap.requisicaomaterial_id " +
                    "         inner join saidarequisicaomaterial srm on srm.requisicaomaterial_id = req.id " +
                    "         inner join saidamaterial saida on srm.id = saida.id " +
                    "         inner join saidamaterialcontabil smc on smc.saidamaterial_id = saida.id " +
                    "         inner join bensestoque be on be.id = smc.bensestoque_id " +
                    " where ap.id = :idMovimento ";
                break;
            default:
                break;
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMovimento", entity.getIdMovimento());
        List resultList = q.getResultList();
        List<ExclusaoMateriaisVO> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) resultList) {
            ExclusaoMateriaisVO rel = new ExclusaoMateriaisVO();
            rel.setId(((BigDecimal) obj[0]).longValue());
            rel.setData((Date) obj[1]);
            rel.setTipoRelacionamento(ExclusaoMateriaisVO.TipoRelacionamento.valueOf((String) obj[2]));

            if (rel.getTipoRelacionamento().isEntradaMaterial()) {
                rel.setEntradaMaterial(em.find(EntradaCompraMaterial.class, rel.getId()));
                rel.setMovimento(rel.getEntradaMaterial().toString());
            }
            if (rel.getTipoRelacionamento().isLiquidacao()) {
                rel.setLiquidacao(em.find(Liquidacao.class, rel.getId()));
                rel.setMovimento(rel.getLiquidacao().toString());
            }
            if (rel.getTipoRelacionamento().isBensEstoqueEntrada()) {
                rel.setEntradaMaterialContabil(em.find(EntradaMaterialContabil.class, rel.getId()));
                rel.setMovimento(rel.getEntradaMaterialContabil().toString());
            }
            if (rel.getTipoRelacionamento().isSaidaMaterial()) {
                rel.setSaidaMaterial(em.find(SaidaRequisicaoMaterial.class, rel.getId()));
                rel.setMovimento(rel.getSaidaMaterial().toString());
            }
            if (rel.getTipoRelacionamento().isAprovacaoMaterial()) {
                rel.setAprovacaoMaterial(em.find(AprovacaoMaterial.class, rel.getId()));
                rel.setMovimento(rel.getAprovacaoMaterial().toString());
            }
            if (rel.getTipoRelacionamento().isRequisicaoMaterial()) {
                rel.setRequisicaoMaterial(em.find(RequisicaoMaterial.class, rel.getId()));
                rel.setMovimento(rel.getRequisicaoMaterial().toString());
            }
            if (rel.getTipoRelacionamento().isBensEstoqueSaida()) {
                rel.setSaidaMaterialContabil(em.find(SaidaMaterialContabil.class, rel.getId()));
                rel.setMovimento(rel.getSaidaMaterialContabil().toString());
            }
            toReturn.add(rel);
        }
        return toReturn;
    }

    public List<ExclusaoMateriaisItensVO> buscarItensEntrada(Long idEntrada) {
        String sql = " " +
            "  select item.numeroitem                            as numero_item, " +
            "         mat.id                                     as id_mat, " +
            "         mat.codigo || ' - ' || mat.descricao       as material, " +
            "         item.quantidade                            as qtde_item, " +
            "         item.valorunitario                         as vl_unit_item, " +
            "         item.valortotal                            as vl_total, " +
            "         coalesce(sum(trunc(est.fisico, 4)), 0)     as qtde_est_atual, " +
            "         coalesce(sum(trunc(est.financeiro, 4)), 0) as vl_est_atual " +
            "from itementradamaterial item " +
            "         inner join movimentoestoque mov on mov.id = item.movimentoestoque_id " +
            "         inner join material mat on mat.id = mov.material_id " +
            "         inner join estoque est on est.material_id = mat.id " +
            "         inner join localestoqueorcamentario leo on leo.id = est.localestoqueorcamentario_id " +
            "         inner join localestoque le on le.id = leo.localestoque_id " +
            "where item.entradamaterial_id = :idEntrada " +
            "  and est.dataestoque = (select max(e2.dataestoque) " +
            "                         from estoque e2 " +
            "                         where e2.localestoqueorcamentario_id = est.localestoqueorcamentario_id " +
            "                           and e2.localestoqueorcamentario_id = mov.localestoqueorcamentario_id  " +
            "                           and e2.material_id = mat.id " +
            "                           and trunc(e2.dataestoque) <= trunc(to_date(:dataOperacao, 'dd/mm/yyyy'))) " +
            "group by item.numeroitem, mat.id, mat.codigo, mat.descricao, item.quantidade, item.valorunitario, item.valortotal " +
            "order by item.numeroitem";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEntrada",idEntrada);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List resultList = q.getResultList();
        List<ExclusaoMateriaisItensVO> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) resultList) {
            ExclusaoMateriaisItensVO item = new ExclusaoMateriaisItensVO();
            item.setNumero(((BigDecimal) obj[0]).longValue());
            item.setIdMaterial(((BigDecimal) obj[1]).longValue());
            item.setMaterial((String) obj[2]);
            item.setQuantidade((BigDecimal) obj[3]);
            item.setValorUnitario((BigDecimal) obj[4]);
            item.setValorTotal((BigDecimal) obj[5]);
            item.setQuantidadeEstoqueAtual((BigDecimal) obj[6]);
            item.setValorEstoqueAtual((BigDecimal) obj[7]);
            toReturn.add(item);
        }
        return toReturn;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SaidaMaterialFacade getSaidaMaterialFacade() {
        return saidaMaterialFacade;
    }

    public EntradaMaterialFacade getEntradaMaterialFacade() {
        return entradaMaterialFacade;
    }

    public RequisicaoMaterialFacade getRequisicaoMaterialFacade() {
        return requisicaoMaterialFacade;
    }

    public AprovacaoMaterialFacade getAprovacaoMaterialFacade() {
        return aprovacaoMaterialFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }
}
