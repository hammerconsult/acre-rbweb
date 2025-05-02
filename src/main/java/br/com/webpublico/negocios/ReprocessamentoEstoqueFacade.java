package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamentoEstoque;
import br.com.webpublico.entidadesauxiliares.ItemConversaoUnidadeMedidaVO;
import br.com.webpublico.entidadesauxiliares.ItemReprocessamentoEstoque;
import br.com.webpublico.enums.SituacaoEntradaMaterial;
import br.com.webpublico.enums.SituacaoMovimentoMaterial;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.negocios.administrativo.dao.JdbcReprocessamentoEstoque;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ReprocessamentoEstoqueFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MovimentoEstoqueFacade movimentoEstoqueFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;


    private JdbcReprocessamentoEstoque jdbcReprocessamentoEstoque;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcReprocessamentoEstoque = (JdbcReprocessamentoEstoque) ap.getBean("jdbcReprocessamentoEstoque");
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public Future<AssistenteReprocessamentoEstoque> reprocessar(AssistenteReprocessamentoEstoque assistente, AssistenteBarraProgresso assistenteBarra, List<ItemReprocessamentoEstoque> itens) throws OperacaoEstoqueException {
        excluirMovimentacaoEstoque(assistente, assistenteBarra);
        assistenteBarra.zerarContadoresProcesso();
        assistenteBarra.setTotal(assistente.getItens().size());

        for (ItemReprocessamentoEstoque item : itens) {
            assistenteBarra.setDescricaoProcesso("Gerando estoque na data " + DataUtil.getDataFormatada(item.getDataMovimento()));
            if (item.getTipoMovimento().isEntrada()) {
                ItemEntradaMaterial itemEntrada = item.getItemEntradaMaterial();
                MovimentoEstoque movimentoEstoque = movimentoEstoqueFacade.criarMovimentoEstoque(itemEntrada, true, itemEntrada.getDataMovimento());
                jdbcReprocessamentoEstoque.updateItemEntrada(itemEntrada.getId(), movimentoEstoque.getId());

            } else if (item.getTipoMovimento().isEstornoEntrada()) {
                ItemEntradaMaterialEstorno itemEntEst = item.getItemEntradaMaterialEstorno();
                MovimentoEstoque movimentoEstoque = movimentoEstoqueFacade.criarMovimentoEstoque(itemEntEst, true, itemEntEst.getDataMovimento());
                jdbcReprocessamentoEstoque.updateItemEntradaEstorno(itemEntEst.getId(), movimentoEstoque.getId());

            } else if (item.getTipoMovimento().isSaida()) {
                ItemSaidaMaterial itemSaida = item.getItemSaidaMaterial();
                MovimentoEstoque movimentoEstoque = movimentoEstoqueFacade.criarMovimentoEstoque(itemSaida, true, itemSaida.getDataMovimento());
                jdbcReprocessamentoEstoque.updateItemSaida(itemSaida.getId(), movimentoEstoque.getId());

            } else if (item.getTipoMovimento().isConversaoEntrada()) {
                ItemConversaoUnidadeMedidaMaterial itemConversao = item.getItemConversao();
                Date dataMovimento = DataUtil.getDataComHoraAtual(itemConversao.getConversaoUnidadeMedida().getDataMovimento());

                LocalEstoqueOrcamentario localEstoqueOrcamentario = localEstoqueFacade.recuperarLocalEstoqueOrcamentario(
                    itemConversao.getConversaoUnidadeMedida().getLocalEstoque(),
                    itemConversao.getConversaoUnidadeMedida().getUnidadeOrcamentaria(),
                    item.getDataMovimento());

                ItemConversaoUnidadeMedidaVO itemSaida = new ItemConversaoUnidadeMedidaVO(itemConversao, TipoOperacao.DEBITO, localEstoqueOrcamentario);
                MovimentoEstoque movEstoqueSaida = movimentoEstoqueFacade.criarMovimentoEstoque(itemSaida, true, dataMovimento);

                ItemConversaoUnidadeMedidaVO itemEntrada = new ItemConversaoUnidadeMedidaVO(itemConversao, TipoOperacao.CREDITO, localEstoqueOrcamentario);
                MovimentoEstoque movEstoqueEntrada = movimentoEstoqueFacade.criarMovimentoEstoque(itemEntrada, true, dataMovimento);
                jdbcReprocessamentoEstoque.updateItemConversao(itemConversao.getId(), movEstoqueEntrada.getId(), movEstoqueSaida.getId());
            }
            assistenteBarra.conta();
        }
        assistenteBarra.zerarContadoresProcesso();
        assistenteBarra.setDescricaoProcesso("Finalizando reprocessamento...");
        return new AsyncResult<>(assistente);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteReprocessamentoEstoque> buscarMovimentos(AssistenteReprocessamentoEstoque assistente, AssistenteBarraProgresso assistenteBarra) {
        AsyncResult<AssistenteReprocessamentoEstoque> result = new AsyncResult<>(assistente);
        assistenteBarra.zerarContadoresProcesso();
        assistenteBarra.setDescricaoProcesso("Recuperando movimentos do estoque...");

        List<ItemReprocessamentoEstoque> itens = buscarItensEntradaSaidaMaterial(assistente, assistenteBarra);
        assistente.setItens(itens);
        assistente.setReprocessamento(Boolean.FALSE);
        return result;
    }

    public List<ItemReprocessamentoEstoque> buscarItensEntradaSaidaMaterial(AssistenteReprocessamentoEstoque assistente, AssistenteBarraProgresso assistenteBarra) {
        String sql = " " +
            "   select * from ( " +
            "      select 'ENTRADA'         as tipo_movimento, " +
            "              item.id          as idItem,  " +
            "              le.codigo || ' - ' || le.descricao  as local_estoque, " +
            "              ent.dataconclusao  as data_mov " +
            "      from itementradamaterial item " +
            "               inner join entradamaterial ent on ent.id = item.entradamaterial_id " +
            "               inner join material mat on mat.id = item.material_id " +
            "               inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "               inner join localestoque le on le.id = leo.localestoque_id " +
            "               inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "               inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "      where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "        and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "        and trunc(ent.dataEntrada) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() +
            "        and (ent.situacao in (:situacoesEntrada) or (ent.situacao = :situacaoAguardandoLiquidacao and ent.numero is not null))" +
            "      union all " +
            "      select 'SAIDA'         as tipo_movimento, " +
            "             item.id         as idItem, " +
            "              le.codigo || ' - ' || le.descricao  as local_estoque, " +
            "             saida.dataconclusao as data_mov " +
            "      from itemsaidamaterial item " +
            "               inner join saidamaterial saida on saida.id = item.saidamaterial_id " +
            "               inner join material mat on mat.id = item.material_id " +
            "               inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "               inner join localestoque le on le.id = leo.localestoque_id " +
            "               inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "               inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "      where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "        and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "        and trunc(saida.datasaida) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() +
            "        and saida.situacao = :situacaoSaida " +
            "      union all " +
            "      select 'ESTORNO_ENTRADA'                    as tipo_movimento, " +
            "              itemEst.id                          as idItem,  " +
            "              le.codigo || ' - ' || le.descricao  as local_estoque, " +
            "              ent.dataestorno                     as data_mov " +
            "      from itementradamaterialestorno itemEst " +
            "               inner join itementradamaterial item on item.id = itemEst.itementradamaterial_id " +
            "               inner join entradamaterial ent on ent.id = item.entradamaterial_id " +
            "               inner join movimentoestoque mov on mov.id = itemEst.movimentoestoque_id " +
            "               inner join material mat on mat.id = item.material_id " +
            "               inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "               inner join localestoque le on le.id = leo.localestoque_id " +
            "               inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "               inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "      where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "        and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "        and trunc(ent.dataEntrada) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() +
            "      union all " +
            "      select distinct 'CONVERSAO' as tipo_movimento, " +
            "              item.id             as idItem, " +
            "              le.codigo || ' - ' || le.descricao  as local_estoque, " +
            "              conv.datamovimento  as data_mov " +
            "      from itemconversaounidademedmat item " +
            "               inner join conversaounidademedidamat conv on conv.id = item.conversaounidademedida_id " +
            "               inner join material mat on mat.id = coalesce(item.materialentrada_id, item.materialsaida_id) " +
            "               inner join localestoque le on le.id = conv.localestoque_id " +
            "               inner join localestoqueorcamentario leo on leo.localestoque_id = le.id " +
            "               inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "               inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "      where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "        and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "        and trunc(conv.datamovimento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() +
            " ) order by data_mov ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("situacoesEntrada", Util.getListOfEnumName(Lists.newArrayList(SituacaoEntradaMaterial.ATESTO_DEFINITIVO_LIQUIDADO, SituacaoEntradaMaterial.ATESTO_DEFINITIVO_ESTORNADO, SituacaoEntradaMaterial.CONCLUIDA)));
        q.setParameter("situacaoAguardandoLiquidacao", SituacaoEntradaMaterial.ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO.name());
        q.setParameter("situacaoSaida", SituacaoMovimentoMaterial.CONCLUIDA.name());

        List<Object[]> list = (List<Object[]>) q.getResultList();
        List<ItemReprocessamentoEstoque> retorno = Lists.newArrayList();
        if (assistenteBarra != null) {
            assistenteBarra.zerarContadoresProcesso();
            assistenteBarra.setTotal(list.size());
        }

        for (Object[] obj : list) {
            ItemReprocessamentoEstoque.TipoMovimento tipoMovimento = ItemReprocessamentoEstoque.TipoMovimento.valueOf((String) obj[0]);
            long idItem = ((BigDecimal) obj[1]).longValue();
            String localEstoque = (String) obj[2];
            if (tipoMovimento.isEntrada()) {
                ItemEntradaMaterial itemEntrada = em.find(ItemEntradaMaterial.class, idItem);
                if (itemEntrada != null) {
                    ItemReprocessamentoEstoque item = new ItemReprocessamentoEstoque(itemEntrada);
                    item.setLocalEstoque(localEstoque);
                    retorno.add(item);
                }
            } else if (tipoMovimento.isEstornoEntrada()) {
                ItemEntradaMaterialEstorno itemEst = em.find(ItemEntradaMaterialEstorno.class, idItem);
                if (itemEst != null) {
                    ItemReprocessamentoEstoque item = new ItemReprocessamentoEstoque(itemEst);
                    item.setLocalEstoque(localEstoque);
                    retorno.add(item);
                }
            } else if (tipoMovimento.isSaida()) {
                ItemSaidaMaterial itemSaida = em.find(ItemSaidaMaterial.class, idItem);
                if (itemSaida != null) {
                    ItemReprocessamentoEstoque item = new ItemReprocessamentoEstoque(itemSaida);
                    item.setLocalEstoque(localEstoque);
                    retorno.add(item);
                }
            } else if (tipoMovimento.isConversao()) {
                ItemConversaoUnidadeMedidaMaterial itemConversao = em.find(ItemConversaoUnidadeMedidaMaterial.class, idItem);
                if (itemConversao != null) {
                    ItemReprocessamentoEstoque itenSaida = new ItemReprocessamentoEstoque(ItemReprocessamentoEstoque.TipoMovimento.CONVERSAO_SAIDA, itemConversao);
                    itenSaida.setLocalEstoque(localEstoque);
                    retorno.add(itenSaida);

                    ItemReprocessamentoEstoque itemEntrada = new ItemReprocessamentoEstoque(ItemReprocessamentoEstoque.TipoMovimento.CONVERSAO_ENTRADA, itemConversao);
                    itemEntrada.setLocalEstoque(localEstoque);
                    retorno.add(itemEntrada);
                }
            }
            if (assistenteBarra != null) {
                assistenteBarra.conta();
            }
        }
        return retorno;
    }

    public void excluirMovimentacaoEstoque(AssistenteReprocessamentoEstoque assistente, AssistenteBarraProgresso assistenteBarraProgresso) {
        assistenteBarraProgresso.zerarContadoresProcesso();
        assistenteBarraProgresso.setDescricaoProcesso("Excluindo movimentação de estoque...");
        removerMovimentoEstoqueItemEntrada(assistente);
        removerMovimentoEstoqueItemEntradaEstorno(assistente);
        removerMovimentoEstoqueItemSaida(assistente);
        removerMovimentoEstoqueItemConversao(assistente);
        deletarMovimentoEstoque(assistente);
        deleteEstoqueLoteMaterial(assistente);
        deletarEstoque(assistente);
    }

    public void deletarEstoque(AssistenteReprocessamentoEstoque assistente) {
        String sql = " delete from estoque " +
            "          where id in (select est.id from estoque est " +
            "                       inner join material mat on mat.id = est.material_id " +
            "                       inner join localestoqueorcamentario leo on leo.id = est.localestoqueorcamentario_id " +
            "                       inner join localestoque le on le.id = leo.localestoque_id " +
            "                       inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                       inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                        where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                        and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                        and trunc(est.dataestoque) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.executeUpdate();
    }

    public void deleteEstoqueLoteMaterial(AssistenteReprocessamentoEstoque assistente) {
        String sql = " delete from estoquelotematerial lote " +
            "          where lote.estoque_id in (select est.id " +
            "                                   from estoque est " +
            "                                   inner join material mat on mat.id = est.material_id " +
            "                                   inner join localestoqueorcamentario leo on leo.id = est.localestoqueorcamentario_id " +
            "                                   inner join localestoque le on le.id = leo.localestoque_id " +
            "                                   inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                                   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                                     where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                                     and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                                     and trunc(est.dataestoque) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.executeUpdate();
    }

    public void deletarMovimentoEstoque(AssistenteReprocessamentoEstoque assistente) {
        String sql = " delete from movimentoestoque " +
            "          where id in (select mov.id from movimentoestoque mov " +
            "                       inner join material mat on mat.id = mov.material_id " +
            "                       inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "                       inner join localestoque le on le.id = leo.localestoque_id " +
            "                       inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                       inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                         where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                         and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                         and trunc(datamovimento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.executeUpdate();
    }

    public void removerMovimentoEstoqueItemConversao(AssistenteReprocessamentoEstoque assistente) {
        String sql = " update itemconversaounidademedmat set movimentoestoquesaida_id = null, movimentoestoqueentrada_id = null " +
            "           where id in (select distinct item.id from itemconversaounidademedmat item " +
            "                         inner join conversaounidademedidamat conv on conv.id = item.conversaounidademedida_id " +
            "                         inner join material mat on mat.id = coalesce(item.materialentrada_id, item.materialsaida_id) " +
            "                         inner join localestoquematerial lem on lem.material_id = mat.id " +
            "                         inner join localestoque le on le.id = lem.localestoque_id " +
            "                         inner join localestoqueorcamentario leo on leo.localestoque_id = le.id " +
            "                         inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                         inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                           where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                           and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                           and trunc(conv.datamovimento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.executeUpdate();
    }

    public void removerMovimentoEstoqueItemSaida(AssistenteReprocessamentoEstoque assistente) {
        String sql = " update itemsaidamaterial set movimentoestoque_id = null  " +
            "           where id in (select item.id from itemsaidamaterial item " +
            "                        inner join saidamaterial saida on saida.id = item.saidamaterial_id " +
            "                        inner join material mat on mat.id = item.material_id " +
            "                        inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "                        inner join localestoque le on le.id = leo.localestoque_id " +
            "                        inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                        inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                           where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                           and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                           and trunc(saida.datasaida) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.executeUpdate();
    }

    public void removerMovimentoEstoqueItemEntrada(AssistenteReprocessamentoEstoque assistente) {
        String sql = " update itementradamaterial set movimentoestoque_id = null " +
            "           where id in (select item.id from itementradamaterial item " +
            "                        inner join entradamaterial ent on ent.id = item.entradamaterial_id " +
            "                        inner join material mat on mat.id = item.material_id " +
            "                        inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "                        inner join localestoque le on le.id = leo.localestoque_id " +
            "                        inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                        inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                          where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                          and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                          and trunc(ent.dataentrada) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.executeUpdate();
    }

    public void removerMovimentoEstoqueItemEntradaEstorno(AssistenteReprocessamentoEstoque assistente) {
        String sql = " update itementradamaterialestorno set movimentoestoque_id = null " +
            "           where id in (select itemEst.id from itementradamaterialestorno itemEst" +
            "                        inner join itementradamaterial item on item.id = itemEst.itementradamaterial_id " +
            "                        inner join entradamaterial ent on ent.id = item.entradamaterial_id " +
            "                        inner join material mat on mat.id = item.material_id " +
            "                        inner join localestoqueorcamentario leo on leo.id = item.localestoqueorcamentario_id " +
            "                        inner join localestoque le on le.id = leo.localestoque_id " +
            "                        inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = le.unidadeorganizacional_id " +
            "                        inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = leo.unidadeorcamentaria_id " +
            "                          where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(vwadm.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                          and to_date(:dataInicial, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(vworc.fimvigencia, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            "                          and trunc(ent.dataentrada) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') " + assistente.getCondicaoSql() + ")";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.executeUpdate();
    }

    public EstoqueFacade getEstoqueFacade() {
        return estoqueFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
