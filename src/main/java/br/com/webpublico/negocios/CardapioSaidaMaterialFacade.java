package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.CardapioSaidaMaterialItemVO;
import br.com.webpublico.entidadesauxiliares.CardapioSaidaMaterialVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Stateless
public class CardapioSaidaMaterialFacade extends AbstractFacade<CardapioSaidaMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CardapioFacade cardapioFacade;
    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;

    public CardapioSaidaMaterialFacade() {
        super(CardapioSaidaMaterial.class);
    }

    @Override
    public CardapioSaidaMaterial recuperar(Object id) {
        CardapioSaidaMaterial entity = super.recuperar(id);
        Hibernate.initialize(entity.getSaidaMaterial().getListaDeItemSaidaMaterial());
        return entity;
    }

    public CardapioSaidaMaterial salvarSaidaMaterial(CardapioSaidaMaterial entity, List<CardapioSaidaMaterialVO> locaisEstoques) {
        Map<CardapioSaidaMaterialItemVO, BigDecimal> mapMaterial = criarMapMaterialQuantidade(locaisEstoques);

        try {
            SaidaDireta saida = new SaidaDireta();
            saida.setDataSaida(sistemaFacade.getDataOperacao());
            saida.setDataConclusao(sistemaFacade.getDataOperacao());
            saida.setRetiradoPor(sistemaFacade.getUsuarioCorrente().getNome());
            saida.setUsuario(sistemaFacade.getUsuarioCorrente());
            saida.setSituacao(SituacaoMovimentoMaterial.CONCLUIDA);
            saida.setTipoBaixaBens(tipoBaixaBensFacade.recuperarTipoIngressoBaixa(TipoBem.ESTOQUE, TipoIngressoBaixa.CONSUMO));
            saida.setListaDeItemSaidaMaterial(Lists.newArrayList());

            for (Map.Entry<CardapioSaidaMaterialItemVO, BigDecimal> entry : mapMaterial.entrySet()) {
                CardapioSaidaMaterialItemVO itemVo = entry.getKey();
                BigDecimal qtdeTotal = entry.getValue();

                ItemSaidaMaterial itemSaida = new ItemSaidaMaterial();
                itemSaida.setSaidaMaterial(saida);
                itemSaida.setNumeroItem(saida.getListaDeItemSaidaMaterial().size() + 1);
                itemSaida.setLocalEstoqueOrcamentario(itemVo.getLocalEstoqueOrcamentario());
                itemSaida.setMaterial(itemVo.getMaterial());
                itemSaida.setQuantidade(qtdeTotal);
                itemSaida.setValorUnitario(itemVo.getValorUnitario());
                itemSaida.calcularValorTotal();
                itemSaida.setItemSaidaDireta(new ItemSaidaDireta(itemVo.getMaterial(), itemSaida));
                Util.adicionarObjetoEmLista(saida.getListaDeItemSaidaMaterial(), itemSaida);
            }
            saida = (SaidaDireta) saidaMaterialFacade.salvarSaidaDireta(saida);
            entity.setSaidaMaterial(saida);
            return em.merge(entity);
        } catch (ParseException | OperacaoEstoqueException e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private Map<CardapioSaidaMaterialItemVO, BigDecimal> criarMapMaterialQuantidade(List<CardapioSaidaMaterialVO> locaisEstoques) {
        Map<CardapioSaidaMaterialItemVO, BigDecimal> mapMaterial = Maps.newHashMap();
        for (CardapioSaidaMaterialVO saidaVo : locaisEstoques) {
            for (CardapioSaidaMaterialItemVO itemVo : saidaVo.getItens()) {
                if (itemVo.hasQuantidadeSaida()) {
                    if (!mapMaterial.containsKey(itemVo)) {
                        mapMaterial.put(itemVo, itemVo.getQuantidadeSaida());
                    } else {
                        BigDecimal qtdeMap = mapMaterial.get(itemVo).add(itemVo.getQuantidadeSaida());
                        mapMaterial.put(itemVo, qtdeMap);
                    }
                }
            }
        }
        return mapMaterial;
    }

    public List<CardapioSaidaMaterialVO> buscarLocaisEstoque(String condicao, Cardapio cardapio) {
        String sql;
        sql = " select distinct le.* " +
            "   from guiadistribuicaorequisicao guia " +
            "         inner join requisicaomaterial req on req.id = guia.requisicaomaterial_id " +
            "         inner join localestoque le on le.id = req.localestoquedestino_id  " +
            "         inner join gestorlocalestoque gle on gle.localestoque_id = le.id " +
            "         inner join saidarequisicaomaterial saida on saida.requisicaomaterial_id = req.id " +
            "         inner join entradatransfmaterial ent on ent.saidarequisicaomaterial_id = saida.id " +
            "         inner join cardapiorequisicaocompra crc on crc.id = guia.cardapiorequisicaocompra_id " +
            "         inner join cardapio card on card.id = crc.cardapio_id " +
            "   where req.tiposituacaorequisicao = :req_transf_concluida " +
            "         and gle.usuariosistema_id = :idUsuario " +
            "         and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(gle.iniciovigencia) and coalesce(trunc(gle.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) "
            + condicao +
            "  order by le.codigo ";
        Query q = em.createNativeQuery(sql, LocalEstoque.class);
        q.setParameter("req_transf_concluida", SituacaoRequisicaoMaterial.TRANSFERENCIA_TOTAL_CONCLUIDA.name());
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<LocalEstoque> list = q.getResultList();
        List<CardapioSaidaMaterialVO> materiais = new ArrayList<>();
        for (LocalEstoque le : list) {
            CardapioSaidaMaterialVO mat = new CardapioSaidaMaterialVO();
            mat.setLocalEstoque(le);
            mat.setItens(buscarMateriais(le, cardapio, condicao));
            materiais.add(mat);
        }
        return materiais;
    }

    public List<CardapioSaidaMaterialItemVO> buscarMateriais(LocalEstoque localEstoque, Cardapio cardapio, String condicao) {
        String sql;
        sql = " select " +
            "       mat.id         as id_material, " +
            "       leo.id         as id_local_orc, " +
            "       sum(irm.quantidade) as qtde_guia, " +
            "       est.fisico     as qtde_estoque, " +
            "       est.financeiro as vl_estoque " +
            "  from cardapio card " +
            "         inner join cardapiorequisicaocompra crc on crc.cardapio_id = card.id " +
            "         inner join guiadistribuicaorequisicao guia on guia.cardapiorequisicaocompra_id = crc.id " +
            "         inner join requisicaomaterial req on req.id = guia.requisicaomaterial_id " +
            "         inner join itemrequisicaomaterial irm on irm.requisicaomaterial_id = req.id " +
            "         inner join material mat on mat.id = irm.materialrequisitado_id " +
            "         inner join estoque est on est.material_id = mat.id " +
            "         inner join localestoqueorcamentario leo on leo.id = est.localestoqueorcamentario_id " +
            "  where req.tiposituacaorequisicao = :req_transf_concluida " +
            "   and est.dataestoque = (select max(estMax.dataestoque) " +
            "                         from estoque estMax " +
            "                         where estMax.material_id = est.material_id " +
            "                           and estmax.localestoqueorcamentario_id = est.localestoqueorcamentario_id " +
            "                           and leo.localestoque_id = :idLocalEstoque " +
            "                           and trunc(estmax.dataestoque) <= to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and req.localestoquedestino_id = :idLocalEstoque " + condicao +
            "  group by mat.id, leo.id, est.fisico, est.financeiro " +
            "  order by mat.id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("req_transf_concluida", SituacaoRequisicaoMaterial.TRANSFERENCIA_TOTAL_CONCLUIDA.name());
        q.setParameter("idLocalEstoque", localEstoque.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<Object[]> list = q.getResultList();
        List<CardapioSaidaMaterialItemVO> materiais = new ArrayList<>();
        for (Object[] obj : list) {
            CardapioSaidaMaterialItemVO mat = new CardapioSaidaMaterialItemVO();
            long idMaterial = ((BigDecimal) obj[0]).longValue();
            mat.setMaterial(em.find(Material.class, idMaterial));
            long idLocalEst = ((BigDecimal) obj[1]).longValue();
            mat.setLocalEstoqueOrcamentario(em.find(LocalEstoqueOrcamentario.class, idLocalEst));
            mat.setQuantidadeGuia((BigDecimal) obj[2]);
            mat.setQuantidadeEstoque((BigDecimal) obj[3]);
            mat.setValorEstoque((BigDecimal) obj[4]);
            mat.setQuantidadeOutraSaida(getQuantidadeOutraSaidaCardapio(cardapio, mat.getMaterial(), localEstoque));
            materiais.add(mat);
        }
        return materiais;
    }

    public BigDecimal getQuantidadeOutraSaidaCardapio(Cardapio cardapio, Material material, LocalEstoque localEstoque) {
        String sql = " select coalesce(sum(ism.quantidade), 0) as quantidade " +
            "          from cardapiosaidamaterial csm " +
            "           inner join saidamaterial sm on sm.id = csm.saidamaterial_id " +
            "           inner join saidadireta sd on sd.id = sm.id " +
            "           inner join itemsaidamaterial ism on ism.saidamaterial_id = sm.id " +
            "           inner join itemsaidadireta isd on isd.itemsaidamaterial_id = ism.id " +
            "           inner join localestoqueorcamentario leo on leo.id = ism.localestoqueorcamentario_id " +
            "          where csm.cardapio_id = :idCardapio " +
            "           and isd.material_id = :idMaterial " +
            "           and leo.localestoque_id = :idLocalEstoque ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCardapio", cardapio.getId());
        q.setParameter("idMaterial", material.getId());
        q.setParameter("idLocalEstoque", localEstoque.getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CardapioFacade getCardapioFacade() {
        return cardapioFacade;
    }
}
