package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemControleProcesso;
import br.com.webpublico.entidadesauxiliares.ItemControleProcessoMovimento;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.enums.TipoProcesso;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class AlteracaoItemProcessoFacade extends AbstractFacade<AlteracaoItemProcesso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private ParticipanteIRPFacade participanteIRPFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private ItemContratoFacade itemContratoFacade;

    public AlteracaoItemProcessoFacade() {
        super(AlteracaoItemProcesso.class);
    }

    public AlteracaoItemProcesso salvarRetornando(AlteracaoItemProcesso entity, List<ItemControleProcesso> itens) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(AlteracaoItemProcesso.class, "numero"));
        }
        if (entity.getTipoAlteracaoItem().isAlteracaoTipoControle()) {
            salvarItensTipoControle(itens);
        } else {
            salvarItensTipoAlteracaoObjetoCompra(itens);
        }
        return em.merge(entity);
    }

    private void salvarItensTipoControle(List<ItemControleProcesso> itens) {
        for (ItemControleProcesso item : itens) {
            for (ItemControleProcessoMovimento mov : item.getMovimentos()) {
                if (item.getSelecionado()) {
                    switch (mov.getTipoProcesso()) {
                        case CONTRATO:
                            ItemContrato itemContrato = em.find(ItemContrato.class, mov.getIdItem());
                            itemContrato.setTipoControle(mov.getTipoControle());
                            itemContrato.setQuantidadeTotalContrato(mov.getQuantidade());
                            itemContrato.setValorUnitario(mov.getValorUnitario());
                            em.merge(itemContrato);
                            break;
                        case IRP:
                            ItemIntencaoRegistroPreco itemIrp = em.find(ItemIntencaoRegistroPreco.class, mov.getIdItem());
                            itemIrp.setTipoControle(mov.getTipoControle());
                            itemIrp.setQuantidade(mov.getQuantidade());
                            itemIrp.setValor(mov.getValorTotal());
                            em.merge(itemIrp);
                            break;
                        case PARTICIPANTE_IRP:
                            ItemParticipanteIRP itemPartIrp = em.find(ItemParticipanteIRP.class, mov.getIdItem());
                            itemPartIrp.setQuantidade(mov.getQuantidade());
                            itemPartIrp.setValor(mov.getValorTotal());
                            em.merge(itemPartIrp);
                            break;
                        case FORMULARIO_COTACAO:
                            ItemLoteFormularioCotacao itemFc = em.find(ItemLoteFormularioCotacao.class, mov.getIdItem());
                            itemFc.setTipoControle(mov.getTipoControle());
                            itemFc.setQuantidade(mov.getQuantidade());
                            em.merge(itemFc);
                            break;
                        case COTACAO:
                            ItemCotacao itemCot = em.find(ItemCotacao.class, mov.getIdItem());
                            itemCot.setTipoControle(mov.getTipoControle());
                            itemCot.setQuantidade(mov.getQuantidade());
                            em.merge(itemCot);
                            break;
                        case VALOR_COTACAO:
                            ValorCotacao vl = em.find(ValorCotacao.class, mov.getIdItem());
                            vl.setPreco(mov.getValorUnitario());
                            em.merge(vl);
                            break;
                        case SOLICITACAO_COMPRA:
                            ItemSolicitacao itemSol = em.find(ItemSolicitacao.class, mov.getIdItem());
                            itemSol.setQuantidade(mov.getQuantidade());
                            itemSol.setPreco(mov.getValorUnitario());
                            em.merge(itemSol);
                            break;
                        case PROCESSO_COMPRA:
                            ItemProcessoDeCompra itemPc = em.find(ItemProcessoDeCompra.class, mov.getIdItem());
                            itemPc.setQuantidade(mov.getQuantidade());
                            em.merge(itemPc);
                            break;
                        case PROPOSTA_FORNECEDOR:
                            ItemPropostaFornecedor itemProp = em.find(ItemPropostaFornecedor.class, mov.getIdItem());
                            itemProp.setPreco(mov.getValorUnitario());
                            em.merge(itemProp);
                            break;
                        case PREGAO_POR_ITEM:
                            ItemPregao itemPregao = em.find(ItemPregao.class, mov.getIdItem());
                            itemPregao.getItemPregaoLanceVencedor().setValor(mov.getValorUnitario());
                            em.merge(itemPregao.getItemPregaoLanceVencedor());

                            List<LancePregao> lances = pregaoFacade.buscarLancesPregaoPorItemPregao(itemPregao);
                            for (LancePregao lance : lances) {
                                lance.setValor(mov.getValorTotalCalculado(mov.getQuantidadeOriginal(), mov.getValorUnitarioOriginal()));
                                if (lance.getStatusLancePregao().isVencedor()) {
                                    lance.setValorFinal(lance.getValor());
                                }
                                em.merge(lance);
                            }
                            break;
                        case PREGAO_POR_LOTE:
                            ItemPregaoLoteItemProcesso itemPregaoLote = em.find(ItemPregaoLoteItemProcesso.class, mov.getIdItem());
                            itemPregaoLote.setValor(mov.getValorUnitario());
                            em.merge(itemPregaoLote);
                            break;
                        case ADJUDICACAO:
                            ItemStatusFornecedorLicitacao itemAdjudicado = em.find(ItemStatusFornecedorLicitacao.class, mov.getIdItem());
                            itemAdjudicado.setValorUnitario(mov.getValorUnitario());
                            em.merge(itemAdjudicado);
                            break;
                        case HOMOLOGACAO:
                            ItemStatusFornecedorLicitacao itemHomologado = em.find(ItemStatusFornecedorLicitacao.class, mov.getIdItem());
                            itemHomologado.setValorUnitario(mov.getValorUnitario());
                            em.merge(itemHomologado);
                            break;
                    }
                }
            }
        }
    }

    private void salvarItensTipoAlteracaoObjetoCompra(List<ItemControleProcesso> itens) {
        for (ItemControleProcesso item : itens) {
            if (item.getSelecionado()) {
                ItemContrato itemContrato = em.find(ItemContrato.class, item.getIdItem());
                itemContrato.setObjetoCompraContrato(item.getObjetoCompraContrato());
                itemContrato.setDescricaoComplementar(!Strings.isNullOrEmpty(item.getDescricaoComplementarContrato())
                    ? item.getDescricaoComplementarContrato()
                    : item.getDescricaoComplementarProcesso());
                em.merge(itemContrato);
            }
        }
    }

    public List<ItemControleProcesso> buscarItensProcesso(AlteracaoItemProcesso selecionado) {
        String sql = "";
        if (selecionado.getTipoProcesso().isContrato()) {
            sql = " select " +
                "       ic.id                                                                       as id_item, " +
                "       oc.id                                                                       as id_objeto, " +
                "       coalesce(ipc.numero, ise.numero, icot.numero)                               as numero_item, " +
                "       coalesce(lote.numero, lcot.numero, 1)                                       as numero_Lote, " +
                "       'CONTRATO'                                                                  as tipo_processo, " +
                "       ic.tipocontrole                                                             as tipo_controle, " +
                "       um.mascaraquantidade                                                        as mascara_quantidade, " +
                "       um.mascaravalorunitario                                                     as mascara_valor," +
                "       to_char(coalesce(itemsol.descricaocomplementar, ise.descricaocomplementar)) as desc_complementar, " +
                "       ic.quantidadetotalcontrato                                                  as quantidade, " +
                "       ic.valorunitario                                                            as valor_unitario, " +
                "       ic.valortotal                                                               as valor_total, " +
                "       1                                                                           as movimento " +
                " from contrato cont " +
                "         inner join itemcontrato ic on ic.contrato_id = cont.id " +
                "         left join itemcontratovigente icv on icv.itemcontrato_id = ic.id " +
                "         left join itemcotacao icot on icv.itemcotacao_id = icot.id  " +
                "         left join lotecotacao lcot on icot.lotecotacao_id = lcot.id  " +
                "         left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                "         left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                "         left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                "         left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                "         left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                "         left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
                "         left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
                "         left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                "         left join loteprocessodecompra lote on lote.id = ipc.loteprocessodecompra_id " +
                "         left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
                "         left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
                "         inner join objetocompra oc on oc.id = coalesce(itemsol.OBJETOCOMPRA_ID, ise.OBJETOCOMPRA_ID, icot.objetocompra_id) " +
                "         left join unidademedida um on um.id = coalesce(itemsol.unidademedida_id, ise.unidademedida_id, icot.unidademedida_id) " +
                " where cont.id = :idProcesso " +
                " order by coalesce(ipc.numero, ise.numero)";
        } else {
            sql = " select " +
                "       ipc.id                              as id_item, " +
                "       oc.id                               as id_objeto, " +
                "       ipc.numero                          as numero_item, " +
                "       lote.numero                         as numero_Lote, " +
                "       'LICITACAO'                         as tipo_processo, " +
                "       ic.tipocontrole                     as tipo_controle, " +
                "       um.mascaraquantidade                as mascara_quantidade, " +
                "       um.mascaravalorunitario             as mascara_valor," +
                "       to_char(item.descricaocomplementar) as desc_complementar, " +
                "       item.quantidade                      as quantidade, " +
                "       item.preco                          as valor_unitario, " +
                "       item.precototal                     as valor_total, " +
                "       1                                   as movimento " +
                "      from itemprocessodecompra ipc " +
                "               inner join loteprocessodecompra lote on lote.id = ipc.loteprocessodecompra_id " +
                "               inner join processodecompra pc on pc.id = lote.processodecompra_id " +
                "               inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "               left join unidademedida um on um.id = item.unidademedida_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "  order by ipc.numero ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", selecionado.getTipoProcesso().isContrato()
            ? selecionado.getContrato().getId()
            : selecionado.getLicitacao().getId());
        if (selecionado.getTipoProcesso().isLicitacao()) {
            q.setParameter("menorPreco", TipoAvaliacaoLicitacao.MENOR_PRECO.name());
        }
        List<ItemControleProcesso> list = Lists.newArrayList();
        try {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ItemControleProcesso item = new ItemControleProcesso();
                item.setIdItem(((BigDecimal) obj[0]).longValue());

                item.setIdObjetoCompra(((BigDecimal) obj[1]).longValue());
                item.setObjetoCompraProcesso(em.find(ObjetoCompra.class, item.getIdObjetoCompra()));
                atribuirGrupoContaDespesaObjetoCompra(item.getObjetoCompraProcesso());

                item.setNumeroItem(((BigDecimal) obj[2]).longValue());
                item.setNumeroLote(((BigDecimal) obj[3]).longValue());
                item.setTipoProcesso(TipoProcesso.valueOf((String) obj[4]));
                item.setTipoControle(TipoControleLicitacao.valueOf((String) obj[5]));
                item.setTipoControleOriginal(TipoControleLicitacao.valueOf((String) obj[5]));
                item.setMascaraQuantidade(obj[6] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[6]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
                item.setMascaraValor(obj[7] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[7]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
                item.setDescricaoComplementarProcesso((String) obj[8]);
                item.setValorUnitario((BigDecimal) obj[9]);
                item.setValorTotal((BigDecimal) obj[10]);
                if (selecionado.getTipoAlteracaoItem().isAlteracaoTipoControle()) {
                    item.setMovimentos(buscarMovimentos(selecionado, item.getIdObjetoCompra(), item.getNumeroItem()));
                }
                if (selecionado.getTipoAlteracaoItem().isAlteracaoObjetoCompra() && selecionado.getTipoProcesso().isContrato()) {
                    item.setObjetoCompraContrato(itemContratoFacade.getObjetoCompraContrato(item.getIdItem()));
                    if (item.getObjetoCompraContrato() != null) {
                        atribuirGrupoContaDespesaObjetoCompra(item.getObjetoCompraContrato());
                    }
                }
                list.add(item);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public void atribuirGrupoContaDespesaObjetoCompra(ObjetoCompra objetoCompra) {
        objetoCompra.setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra()));
    }

    public List<ItemControleProcessoMovimento> buscarMovimentos(AlteracaoItemProcesso selecionado, Long idObjetoCompra, Long numeroItem) {
        String sql;
        if (selecionado.getTipoProcesso().isContrato()) {
            sql = " select " +
                "       ic.id                                                                       as id_item, " +
                "       cont.id                                                                     as id_mov, " +
                "       'CONTRATO'                                                                  as tipo_processo, " +
                "       ic.quantidadetotalcontrato                                                  as quantidade, " +
                "       ic.valorunitario                                                            as valor_unitario, " +
                "       ic.valortotal                                                               as valor_total, " +
                "       cont.numerocontrato || ' - ' || cont.numerotermo || '/' || ex.ano           as descricao, " +
                "       coalesce(ic.tipocontrole, ise.tipocontrole, icot.tipocontrole)              as tipo_controle, " +
                "       1                                                                           as movimento " +
                " from contrato cont " +
                "         inner join itemcontrato ic on ic.contrato_id = cont.id " +
                "         inner join exercicio ex on ex.id = cont.exerciciocontrato_id " +
                "         left join itemcontratovigente icv on icv.itemcontrato_id = ic.id " +
                "         left join itemcotacao icot on icv.itemcotacao_id = icot.id  " +
                "         left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                "         left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                "         left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                "         left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                "         left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                "         left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
                "         left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
                "         left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                "         left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id " +
                "         left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id " +
                "         inner join objetocompra oc on oc.id = coalesce(itemsol.OBJETOCOMPRA_ID, ise.OBJETOCOMPRA_ID, icot.OBJETOCOMPRA_ID) " +
                "         left join unidademedida um on um.id = coalesce(itemsol.unidademedida_id, ise.unidademedida_id, icot.unidademedida_id) " +
                " where cont.id = :idProcesso " +
                " and oc.id = :idObjetoCompra " +
                " and coalesce(ipc.numero, ise.numero, icot.numero) = :numeroItem " +
                " order by coalesce(ipc.numero, ise.numero, icot.numero)";
        } else {
            sql = " select * from (" +
                "     select item.id                     as id_item, " +
                "            irp.id                      as id_mov, " +
                "            'IRP'                       as tipo_processo, " +
                "            item.quantidade             as quantidade, " +
                "            item.valor                  as valor_unitario, " +
                "            0                           as valor_total, " +
                "            irp.numero || '/' || ex.ano as descricao, " +
                "            item.tipocontrole           as tipo_controle, " +
                "            1                           as movimento " +
                "      from itemintencaoregistropreco item " +
                "               inner join LOTEINTENCAOREGISTROPRECO lote on lote.id = item.LOTEINTENCAOREGISTROPRECO_ID " +
                "               inner join INTENCAOREGISTROPRECO irp on irp.id = lote.INTENCAOREGISTROPRECO_ID " +
                "               inner join formulariocotacao fc on irp.id = fc.INTENCAOREGISTROPRECO_ID " +
                "               inner join exercicio ex on ex.id = fc.exercicio_id " +
                "               inner join cotacao cot on cot.formulariocotacao_id = fc.id " +
                "               inner join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                "               inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and item.numero = :numeroItem " +
                " union all " +
                "     select item.id                      as id_item, " +
                "            part.id                      as id_mov, " +
                "            'PARTICIPANTE_IRP'           as tipo_processo, " +
                "            item.quantidade              as quantidade, " +
                "            item.valor                   as valor_unitario, " +
                "            0                            as valor_total, " +
                "            part.numero || '/' || ex.ano as descricao, " +
                "            itemirp.tipocontrole         as tipo_controle, " +
                "            2                            as movimento " +
                "      from itemparticipanteirp item " +
                "               inner join itemintencaoregistropreco itemirp on itemirp.id = item.itemintencaoregistropreco_id  " +
                "               inner join participanteirp part on part.id = item.participanteirp_id " +
                "               inner join INTENCAOREGISTROPRECO irp on irp.id = part.intencaoregistrodepreco_id " +
                "               inner join formulariocotacao fc on irp.id = fc.INTENCAOREGISTROPRECO_ID " +
                "               inner join exercicio ex on ex.id = fc.exercicio_id " +
                "               inner join cotacao cot on cot.formulariocotacao_id = fc.id " +
                "               inner join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                "               inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "               inner join objetocompra oc on oc.id = itemirp.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and itemirp.numero = :numeroItem " +
                " union all " +
                "   select ifc.id                             as id_item, " +
                "          fc.id                              as id_mov, " +
                "         'FORMULARIO_COTACAO'                as tipo_processo, " +
                "          ifc.quantidade                     as quantidade, " +
                "          ifc.valor                          as valor_unitario, " +
                "          0                                  as valor_total, " +
                "          fc.numero || '/' || ex.ano         as descricao, " +
                "          ifc.tipocontrole                   as tipo_controle, " +
                "          3                                  as movimento " +
                "      from itemloteformulariocotacao ifc " +
                "               inner join loteformulariocotacao lote on lote.id = ifc.loteformulariocotacao_id " +
                "               inner join formulariocotacao fc on fc.id = lote.formulariocotacao_id " +
                "               inner join exercicio ex on ex.id = fc.exercicio_id " +
                "               inner join cotacao cot on cot.formulariocotacao_id = fc.id " +
                "               inner join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                "               inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "               inner join objetocompra oc on oc.id = ifc.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and ifc.numero = :numeroItem" +
                "    union all " +
                "      select ic.id                              as id_item, " +
                "             cot.id                             as id_mov, " +
                "             'COTACAO'                          as tipo_processo, " +
                "             ic.quantidade                      as quantidade, " +
                "             ic.valorunitario                   as valor_unitario, " +
                "             ic.valortotal                      as valor_total, " +
                "             cot.numero || '/' || ex.ano        as descricao, " +
                "             ic.tipocontrole                    as tipo_controle, " +
                "             4                                  as movimento " +
                "      from itemcotacao ic " +
                "               inner join lotecotacao lote on lote.id = ic.lotecotacao_id " +
                "               inner join cotacao cot on cot.id = lote.cotacao_id " +
                "               inner join exercicio ex on ex.id = cot.exercicio_id " +
                "               inner join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                "               inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "               inner join objetocompra oc on oc.id = ic.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and ic.numero = :numeroItem " +
                "      union all " +
                "      select vl.id                              as id_item, " +
                "             cot.id                             as id_mov, " +
                "             'VALOR_COTACAO'                    as tipo_processo, " +
                "             ic.quantidade                      as quantidade, " +
                "             vl.preco                           as valor_unitario, " +
                "             0                                  as valor_total, " +
                "             coalesce(pf.nome, pj.razaosocial)  as descricao, " +
                "             ic.tipocontrole                    as tipo_controle, " +
                "             5                                  as movimento " +
                "      from itemcotacao ic " +
                "               inner join valorcotacao vl on vl.itemcotacao_id = ic.id " +
                "               inner join pessoa p on p.id = vl.fornecedor_id " +
                "               left join pessoafisica pf on pf.id = p.id " +
                "               left join pessoajuridica pj on pj.id = p.id " +
                "               inner join lotecotacao lote on lote.id = ic.lotecotacao_id " +
                "               inner join cotacao cot on cot.id = lote.cotacao_id " +
                "               inner join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                "               inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "               inner join objetocompra oc on oc.id = ic.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and ic.numero = :numeroItem " +
                "        and vl.preco > 0" +
                "      union all " +
                "      select item.id                             as id_item, " +
                "             sol.id                              as id_mov, " +
                "             'SOLICITACAO_COMPRA'                as tipo_processo, " +
                "             item.quantidade                     as quantidade, " +
                "             item.preco                          as valor_unitario, " +
                "             item.precototal                     as valor_total, " +
                "             sol.numero || '/' || ex.ano         as descricao, " +
                "             ic.tipocontrole                    as tipo_controle, " +
                "             6                                   as movimento " +
                "      from itemsolicitacao item " +
                "               inner join lotesolicitacaomaterial lote on lote.id = item.lotesolicitacaomaterial_id " +
                "               inner join solicitacaomaterial sol on sol.id = lote.solicitacaomaterial_id " +
                "               inner join exercicio ex on ex.id = sol.exercicio_id " +
                "               inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                "               inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and item.numero = :numeroItem" +
                "      union all " +
                "      select ipc.id                              as id_item, " +
                "             pc.id                               as id_mov, " +
                "             'PROCESSO_COMPRA'                   as tipo_processo, " +
                "             ipc.quantidade                      as quantidade, " +
                "             item.preco                          as valor_unitario, " +
                "             item.precototal                     as valor_total, " +
                "             pc.numero || '/' || ex.ano          as descricao, " +
                "             ic.tipocontrole                    as tipo_controle, " +
                "             7                                   as movimento " +
                "      from itemprocessodecompra ipc " +
                "               inner join loteprocessodecompra lote on lote.id = ipc.loteprocessodecompra_id " +
                "               inner join processodecompra pc on pc.id = lote.processodecompra_id " +
                "               inner join exercicio ex on ex.id = pc.exercicio_id " +
                "               inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and ipc.numero = :numeroItem" +
                "      union all " +
                "      select ipf.id                              as id_item, " +
                "             pf.id                               as id_mov, " +
                "             'PROPOSTA_FORNECEDOR'               as tipo_processo, " +
                "             ipc.quantidade                      as quantidade, " +
                "             ipf.preco                           as valor_unitario, " +
                "             0                                   as valor_total, " +
                "             coalesce(pf.nome, pj.razaosocial)  as descricao, " +
                "             ic.tipocontrole                    as tipo_controle, " +
                "             8                                  as movimento " +
                "      from propostafornecedor pf " +
                "               inner join licitacao lic on lic.id = pf.licitacao_id " +
                "               inner join pessoa p on p.id = pf.fornecedor_id " +
                "               left join pessoafisica pf on pf.id = p.id " +
                "               left join pessoajuridica pj on pj.id = p.id " +
                "               inner join lotepropfornec lpf on lpf.propostafornecedor_id = pf.id " +
                "               inner join itempropfornec ipf on ipf.lotepropostafornecedor_id = lpf.id " +
                "               inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and ipc.numero = :numeroItem" +
                "      union all " +
                "      select ic.id                               as id_item, " +
                "             p.id                                as id_mov, " +
                "             'PREGAO_POR_ITEM'                   as tipo_processo, " +
                "             ipc.quantidade                      as quantidade, " +
                "             iplv.valor                          as valor_unitario, " +
                "             0                                   as valor_total, " +
                "             lic.numerolicitacao || '/' || ex.ano || ' - ' || coalesce(pf.nome, pj.razaosocial)  as descricao, " +
                "             ic.tipocontrole                     as tipo_controle, " +
                "             9                                   as movimento " +
                "      from pregao p " +
                "               inner join licitacao lic on lic.id = p.licitacao_id " +
                "               inner join exercicio ex on ex.id = lic.exercicio_id " +
                "               inner join itempregao ic on ic.pregao_id = p.id " +
                "               inner join itpreitpro ipip on ipip.itempregao_id = ic.id " +
                "               inner join itemprocessodecompra ipc on ipc.id = ipip.itemprocessodecompra_id " +
                "               inner join itempregaolancevencedor iplv on iplv.id = ic.itempregaolancevencedor_id " +
                "               inner join lancepregao lanc on lanc.id = iplv.lancepregao_id " +
                "               inner join propostafornecedor prop on prop.id = lanc.propostafornecedor_id " +
                "               inner join pessoa pes on pes.id = prop.fornecedor_id " +
                "               left join pessoafisica pf on pf.id = pes.id " +
                "               left join pessoajuridica pj on pj.id = pes.id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and ipc.numero = :numeroItem " +
                "  union all " +
                "      select itemLote.id                         as id_item, " +
                "             p.id                                as id_mov, " +
                "             'PREGAO_POR_LOTE'                   as tipo_processo, " +
                "             ipc.quantidade                      as quantidade, " +
                "             itemLote.valor                      as valor_unitario, " +
                "             0                                   as valor_total, " +
                "             lic.numerolicitacao || '/' || ex.ano || ' - ' || coalesce(pf.nome, pj.razaosocial)  as descricao, " +
                "             ic.tipocontrole                     as tipo_controle, " +
                "             10                                   as movimento " +
                "      from pregao p " +
                "               inner join licitacao lic on lic.id = p.licitacao_id " +
                "               inner join exercicio ex on ex.id = lic.exercicio_id " +
                "               inner join itempregao ic on ic.pregao_id = p.id " +
                "               inner join itprelotpro iplp on iplp.itempregao_id = ic.id " +
                "               inner join ItemPregaoLoteItemProcesso itemLote on itemLote.itemPregaoLoteProcesso_id = iplp.id " +
                "               inner join itemprocessodecompra ipc on ipc.id = itemLote.itemprocessodecompra_id " +
                "               inner join itempregaolancevencedor iplv on iplv.id = ic.itempregaolancevencedor_id " +
                "               inner join lancepregao lanc on lanc.id = iplv.lancepregao_id " +
                "               inner join propostafornecedor prop on prop.id = lanc.propostafornecedor_id " +
                "               inner join pessoa pes on pes.id = prop.fornecedor_id " +
                "               left join pessoafisica pf on pf.id = pes.id " +
                "               left join pessoajuridica pj on pj.id = pes.id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and ipc.numero = :numeroItem " +
                "      union all " +
                "      select itemsfl.id                          as id_item, " +
                "             sfl.id                              as id_mov, " +
                "             'ADJUDICACAO'                       as tipo_processo, " +
                "             ipc.quantidade                      as quantidade, " +
                "             itemsfl.valorunitario               as valor_unitario, " +
                "             0                                   as valor_total, " +
                "             coalesce(pf.nome, pj.razaosocial)  as descricao, " +
                "             ic.tipocontrole                    as tipo_controle, " +
                "             11                                  as movimento " +
                "      from statusfornecedorlicitacao sfl " +
                "               inner join licitacaofornecedor lf on lf.id = sfl.licitacaofornecedor_id " +
                "               inner join pessoa p on p.id = lf.empresa_id " +
                "               left join pessoafisica pf on pf.id = p.id " +
                "               left join pessoajuridica pj on pj.id = p.id " +
                "               inner join itemstatusfornecedorlicit itemsfl on itemsfl.statusfornecedorlicitacao_id = sfl.id " +
                "               inner join itemprocessodecompra ipc on ipc.id = itemsfl.itemprocessocompra_id " +
                "               inner join licitacao lic on lic.id = lf.licitacao_id " +
                "               inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and itemSfl.situacao = 'ADJUDICADO'" +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and ipc.numero = :numeroItem" +
                "      union all " +
                "      select itemsfl.id                         as id_item, " +
                "             sfl.id                             as id_mov, " +
                "             'HOMOLOGACAO'                      as tipo_processo, " +
                "             ipc.quantidade                     as quantidade, " +
                "             itemsfl.valorunitario              as valor_unitario, " +
                "             0                                  as valor_total, " +
                "             coalesce(pf.nome, pj.razaosocial)  as descricao, " +
                "             ic.tipocontrole                    as tipo_controle, " +
                "             12                                 as movimento " +
                "      from statusfornecedorlicitacao sfl " +
                "              inner join licitacaofornecedor lf on lf.id = sfl.licitacaofornecedor_id " +
                "               inner join pessoa p on p.id = lf.empresa_id " +
                "               left join pessoafisica pf on pf.id = p.id " +
                "               left join pessoajuridica pj on pj.id = p.id " +
                "               inner join itemstatusfornecedorlicit itemsfl on itemsfl.statusfornecedorlicitacao_id = sfl.id " +
                "               inner join itemprocessodecompra ipc on ipc.id = itemsfl.itemprocessocompra_id " +
                "               inner join licitacao lic on lic.id = lf.licitacao_id " +
                "               inner join processodecompra pc on pc.id = lic.processodecompra_id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where lic.tipoavaliacao = :menorPreco " +
                "        and itemSfl.situacao = 'HOMOLOGADO' " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and ipc.numero = :numeroItem " +
                "     ) order by movimento  ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", selecionado.getTipoProcesso().isContrato() ? selecionado.getContrato().getId() : selecionado.getLicitacao().getId());
        q.setParameter("idObjetoCompra", idObjetoCompra);
        q.setParameter("numeroItem", numeroItem);
        if (selecionado.getTipoProcesso().isLicitacao()) {
            q.setParameter("menorPreco", TipoAvaliacaoLicitacao.MENOR_PRECO.name());
        }
        List<ItemControleProcessoMovimento> list = Lists.newArrayList();
        try {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                ItemControleProcessoMovimento item = new ItemControleProcessoMovimento();
                item.setIdItem(((BigDecimal) obj[0]).longValue());
                item.setIdMovimento(((BigDecimal) obj[1]).longValue());
                item.setTipoProcesso(TipoProcesso.valueOf((String) obj[2]));
                item.setQuantidade((BigDecimal) obj[3]);
                item.setValorUnitario((BigDecimal) obj[4]);
                item.setValorTotal((BigDecimal) obj[5]);
                item.setDescricao((String) obj[6]);
                item.setTipoControle(TipoControleLicitacao.valueOf((String) obj[7]));
                if (!item.hasValorTotal(item)) {
                    item.setValorTotal(item.getValorTotalCalculado(item.getQuantidade(), item.getValorUnitario()));
                }
                item.setQuantidadeOriginal(item.getQuantidade());
                item.setValorUnitarioOriginal(item.getValorUnitario());
                item.setValorTotalOriginal(item.getValorTotal());
                list.add(item);
            }
            return list;
        } catch (
            NoResultException nre) {
            return list;
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }
}
