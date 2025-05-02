package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoRequisicaoMaterial;
import br.com.webpublico.enums.TipoRequisicaoMaterial;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Stateless
public class CardapioRequisicaoCompraFacade extends AbstractFacade<CardapioRequisicaoCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    public CardapioFacade cardapioFacade;
    @EJB
    public RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    public SistemaFacade sistemaFacade;
    @EJB
    public ContratoFacade contratoFacade;
    @EJB
    public SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    public RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    public LocalEstoqueFacade localEstoqueFacade;

    public CardapioRequisicaoCompraFacade() {
        super(CardapioRequisicaoCompra.class);
    }

    @Override
    public CardapioRequisicaoCompra recuperar(Object id) {
        CardapioRequisicaoCompra entity = super.recuperar(id);
        Hibernate.initialize(entity.getGuiasDistribuicao());
        for (GuiaDistribuicaoRequisicao guia : entity.getGuiasDistribuicao()) {
            Hibernate.initialize(guia.getItens());
        }
        return entity;
    }

    public RequisicaoDeCompra recuperarRequisicaoCompra(RequisicaoDeCompra requisicao) {
        RequisicaoDeCompra reqCompra = requisicaoDeCompraFacade.recuperarComDependenciasItens(requisicao.getId());
        Collections.sort(reqCompra.getItens());
        for (ItemRequisicaoDeCompra item : reqCompra.getItens()) {
            Collections.sort(item.getItensRequisicaoExecucao());
        }
        return reqCompra;
    }

    public void salvarCardapioRequisicao(CardapioRequisicaoCompraVO selecionadoVO) {
        for (RequisicaoCompraGuiaVO reqCompraGuiaVo : selecionadoVO.getRequisicoes()) {

            if (reqCompraGuiaVo.getValorTotal().compareTo(BigDecimal.ZERO) > 0) {
                CardapioRequisicaoCompra selecionado;
                RequisicaoDeCompra requisicaoCompra;
                if (Operacoes.NOVO.equals(selecionadoVO.getOperacao())) {
                    selecionado = new CardapioRequisicaoCompra();
                    requisicaoCompra = reqCompraGuiaVo.getRequisicaoCompra();
                    if (requisicaoCompra.getNumero() == null) {
                        requisicaoCompra.setNumero(singletonGeradorCodigo.getProximoCodigo(RequisicaoDeCompra.class, "numero"));
                    }
                } else {
                    selecionado = recuperarCardapioRequisicaoCompra(reqCompraGuiaVo.getRequisicaoCompra());
                    requisicaoCompra = requisicaoDeCompraFacade.recuperar(reqCompraGuiaVo.getRequisicaoCompra().getId());
                    requisicaoCompra.getItens().clear();
                    requisicaoCompra.getExecucoes().clear();
                    requisicaoCompra.setDescricao(reqCompraGuiaVo.getRequisicaoCompra().getDescricao());
                    requisicaoCompra.setLocalEntrega(reqCompraGuiaVo.getRequisicaoCompra().getLocalEntrega());
                }
                criarItemRequisicaoCompra(selecionadoVO, reqCompraGuiaVo, requisicaoCompra);
                criarRequisicaoCompraExecucao(requisicaoCompra);

                selecionado.setRequisicaoCompra(requisicaoCompra);
                selecionado.setCardapio(selecionadoVO.getCardapio());

                criarGuiaDistribuicao(selecionado, reqCompraGuiaVo, selecionadoVO);
                atribuirNumeroGuiasNaRequisicaoCompra(selecionado);
                em.merge(selecionado);
            }
        }
    }

    private void atribuirNumeroGuiasNaRequisicaoCompra(CardapioRequisicaoCompra selecionado) {
        StringBuilder numeroGuia = new StringBuilder("Conforme guias de distribuição em anexo: ");
        for (GuiaDistribuicaoRequisicao guia : selecionado.getGuiasDistribuicao()) {
            if (guia.getRequisicaoMaterial().getNumero() != null) {
                numeroGuia.append(guia.getRequisicaoMaterial().getNumero()).append(", ");
            }
        }
        selecionado.getRequisicaoCompra().setLocalEntrega(numeroGuia.substring(0, numeroGuia.length() - 2));
    }

    private void criarItemRequisicaoCompra(CardapioRequisicaoCompraVO selecionadoVO, RequisicaoCompraGuiaVO reqCompraGuiaVo, RequisicaoDeCompra requisicaoCompra) {
        for (RequisicaoCompraGuiaItemVO itemReqVo : reqCompraGuiaVo.getItensRequisicao()) {
            if (itemReqVo.hasQuantidadeRequisicao()) {
                ItemRequisicaoDeCompra novoItemReq = novoItemRequisicaoCompra(requisicaoCompra, itemReqVo);
                novoItemRequisicaoCompraExecucao(itemReqVo, novoItemReq, selecionadoVO);
                itemReqVo.setItemRequisicaoCompra(novoItemReq);
            }
        }
        atribuirQuantidadeValorItemRequisicaoExecucao(requisicaoCompra);
    }

    private ItemRequisicaoDeCompra novoItemRequisicaoCompra(RequisicaoDeCompra novaReq, RequisicaoCompraGuiaItemVO itemReqVo) {
        ItemRequisicaoDeCompra novoItemReq = new ItemRequisicaoDeCompra();
        novoItemReq.setRequisicaoDeCompra(novaReq);
        novoItemReq.setMaterial(itemReqVo.getMaterial());
        novoItemReq.setItemContrato(itemReqVo.getItemContrato());
        novoItemReq.setNumero(itemReqVo.getItemContrato().getNumero());
        novoItemReq.setDescricaoComplementar(itemReqVo.getItemContrato().getItemAdequado().getDescricaoComplementar());
        novoItemReq.setObjetoCompra(itemReqVo.getMaterial().getObjetoCompra());
        novoItemReq.setUnidadeMedida(itemReqVo.getMaterial().getUnidadeMedida());
        novoItemReq.setQuantidade(itemReqVo.getQuantidadeRequisicao());
        novoItemReq.setValorUnitario(itemReqVo.getValorUnitario());
        novoItemReq.setValorTotal(itemReqVo.getValorTotal());
        novaReq.getItens().add(novoItemReq);
        return novoItemReq;
    }

    private void novoItemRequisicaoCompraExecucao(RequisicaoCompraGuiaItemVO mat, ItemRequisicaoDeCompra novoItemReq, CardapioRequisicaoCompraVO selecionadoVO) {
        for (ExecucaoContratoItem itemEx : selecionadoVO.getMapItemContratoItensExecucao().get(mat.getItemContrato())) {
            if (itemEx != null) {
                ItemRequisicaoCompraExecucao novoItemReqEx = new ItemRequisicaoCompraExecucao();
                novoItemReqEx.setExecucaoContratoItem(itemEx);
                novoItemReqEx.setItemRequisicaoCompra(novoItemReq);
                novoItemReqEx.setQuantidade(novoItemReq.getQuantidade());
                novoItemReqEx.setValorUnitario(novoItemReq.getValorUnitario());
                novoItemReqEx.setValorTotal(novoItemReq.getValorTotal());
                novoItemReq.getItensRequisicaoExecucao().add(novoItemReqEx);
            }
        }
    }

    private void criarRequisicaoCompraExecucao(RequisicaoDeCompra novaReq) {
        Set<ExecucaoContrato> execucoes = new HashSet<>();
        for (ItemRequisicaoDeCompra item : novaReq.getItens()) {
            for (ItemRequisicaoCompraExecucao itemEx : item.getItensRequisicaoExecucao()) {
                execucoes.add(itemEx.getExecucaoContratoItem().getExecucaoContrato());
            }
        }
        for (ExecucaoContrato exec : execucoes) {
            RequisicaoCompraExecucao novaExecReq = new RequisicaoCompraExecucao();
            novaExecReq.setRequisicaoCompra(novaReq);
            novaExecReq.setExecucaoContrato(exec);
            novaReq.getExecucoes().add(novaExecReq);
        }
    }

    private void atribuirQuantidadeValorItemRequisicaoExecucao(RequisicaoDeCompra reqCompra) {
        for (ItemRequisicaoDeCompra itemReq : reqCompra.getItens()) {
            if (itemReq.hasMaisDeUmItemRequisicaoExecucao()) {
                int qtdeItemEx = itemReq.getItensRequisicaoExecucao().size();
                BigDecimal qtdeParcionada = itemReq.getQuantidade().divide(new BigDecimal(qtdeItemEx), 2, RoundingMode.HALF_EVEN);
                for (ItemRequisicaoCompraExecucao itemEx : itemReq.getItensRequisicaoExecucao()) {
                    itemEx.setQuantidade(qtdeParcionada);
                    itemEx.calcularValorTotal();
                }
                if (itemReq.getQuantidadeTotalItemExecucao().compareTo(itemReq.getQuantidade()) != 0) {
                    qtdeParcionada = itemReq.getQuantidade().subtract(qtdeParcionada);
                    ItemRequisicaoCompraExecucao itemEx = itemReq.getItensRequisicaoExecucao().get(itemReq.getItensRequisicaoExecucao().size() - 1);
                    itemEx.setQuantidade(itemEx.getQuantidade().add(qtdeParcionada));
                }
            }
        }
    }

    public void criarGuiaDistribuicao(CardapioRequisicaoCompra selecionado, RequisicaoCompraGuiaVO reqCompraVO, CardapioRequisicaoCompraVO selecionadoVO) {
        selecionado.getGuiasDistribuicao().clear();
        for (GuiaDistribuicaoVO guiaVo : reqCompraVO.getGuiasDistribuicao()) {
            if (guiaVo.getItens().stream().anyMatch(GuiaDistribuicaoItemVO::hasQuantidade)) {
                GuiaDistribuicaoRequisicao novaGuia = new GuiaDistribuicaoRequisicao();
                RequisicaoMaterial requisicaoMaterial = criarRequisicaoMaterial(guiaVo, selecionadoVO, reqCompraVO);

                novaGuia.setCardapioRequisicaoCompra(selecionado);
                novaGuia.setRequisicaoMaterial(requisicaoMaterial);
                novaGuia.setItens(Lists.newArrayList());

                for (GuiaDistribuicaoItemVO itemGuiaVo : guiaVo.getItens()) {
                    if (itemGuiaVo.hasQuantidade()) {
                        ItemRequisicaoMaterial novoItemReq = criarItemRequisicaoMaterial(requisicaoMaterial, itemGuiaVo);
                        Util.adicionarObjetoEmLista(requisicaoMaterial.getItensRequisitados(), novoItemReq);

                        criarItemGuiaDistribuicao(novaGuia, itemGuiaVo.getItemReqCompra().getItemRequisicaoCompra(), novoItemReq);
                    }
                }
                Util.adicionarObjetoEmLista(selecionado.getGuiasDistribuicao(), novaGuia);
            }
        }
    }

    private void criarItemGuiaDistribuicao(GuiaDistribuicaoRequisicao novaGuia, ItemRequisicaoDeCompra itemReqCompra, ItemRequisicaoMaterial itemReqMat) {
        GuiaDistribuicaoReqItem novoItemGuia = new GuiaDistribuicaoReqItem();
        novoItemGuia.setGuiaDistribuicaoRequisicao(novaGuia);
        novoItemGuia.setItemRequisicaoCompra(itemReqCompra);
        novoItemGuia.setItemRequisicaoMaterial(itemReqMat);
        Util.adicionarObjetoEmLista(novaGuia.getItens(), novoItemGuia);
    }

    private ItemRequisicaoMaterial criarItemRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial, GuiaDistribuicaoItemVO itemGuiaVo) {
        ItemRequisicaoMaterial novoItemReq = new ItemRequisicaoMaterial();
        novoItemReq.setRequisicaoMaterial(requisicaoMaterial);

        Material material = itemGuiaVo.getItemReqCompra().getMaterial();
        novoItemReq.setMaterialAprovado(material);
        novoItemReq.setMaterialRequisitado(material);

        BigDecimal quantidade = itemGuiaVo.getQuantidade();
        novoItemReq.setQuantidade(quantidade);
        novoItemReq.setQuantidadeAutorizada(quantidade);
        novoItemReq.setQuantidadeAtendida(quantidade);
        return novoItemReq;
    }

    private RequisicaoMaterial criarRequisicaoMaterial(GuiaDistribuicaoVO guiaVo, CardapioRequisicaoCompraVO selecionadoVO,
                                                       RequisicaoCompraGuiaVO reqCompraVo) {
        RequisicaoMaterial novaReqMat = new RequisicaoMaterial();
        novaReqMat.setDataRequisicao(sistemaFacade.getDataOperacao());
        novaReqMat.setIntegrada(true);
        novaReqMat.setTipoRequisicao(TipoRequisicaoMaterial.TRANSFERENCIA);
        novaReqMat.setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.NAO_AVALIADA);
        novaReqMat.setLocalEstoqueOrigem(selecionadoVO.getLocalEstoquePai());
        novaReqMat.setLocalEstoqueDestino(guiaVo.getLocalEstoqueDestino());
        novaReqMat.setUnidadeRequerente(selecionadoVO.getLocalEstoquePai().getUnidadeOrganizacional());
        novaReqMat.setRequerenteEAutorizador(sistemaFacade.getUsuarioCorrente());

        RequisicaoDeCompra reqCompra = reqCompraVo.getRequisicaoCompra();
        String textoDigitado = guiaVo.getDescricao();
        String textoPadrao = "";
        if (reqCompra.getId() == null) {
            textoPadrao = "Referente a Distribuição da Requisição de Compra No. " + reqCompra.getNumero() + " - " + DataUtil.getDataFormatada(reqCompra.getDataRequisicao())
                + ", Cardápio No. " + selecionadoVO.getCardapio().getNumero() + "/" + DataUtil.getAno(selecionadoVO.getCardapio().getDataCadastro())
                + " - (" + DataUtil.getDataFormatada(selecionadoVO.getCardapio().getDataInicial()) + " a " + DataUtil.getDataFormatada(selecionadoVO.getCardapio().getDataFinal()) + ")"
                + " - " + selecionadoVO.getCardapio().getProgramaAlimentacao().getNumero() + " - " + selecionadoVO.getCardapio().getProgramaAlimentacao().getNome();
        }
        novaReqMat.setDescricao(textoPadrao + Util.quebraLinha() + textoDigitado);
        novaReqMat.setNumero(singletonGeradorCodigo.getProximoCodigo(RequisicaoMaterial.class, "numero"));
        return novaReqMat;
    }

    public List<GuiaDistribuicaoVO> buscarGuiaDistribuicaoVO(CardapioRequisicaoCompra entity, SituacaoRequisicaoMaterial... situacoes) {
        List<GuiaDistribuicaoVO> guiasVo = Lists.newArrayList();
        List<GuiaDistribuicaoRequisicao> guiasDistribuicao = buscarGuiasDistribuicao(entity, situacoes);
        for (GuiaDistribuicaoRequisicao guia : guiasDistribuicao) {
            if (guia.getRequisicaoMaterial() != null) {

                GuiaDistribuicaoVO guiaVo = new GuiaDistribuicaoVO(guia.getRequisicaoMaterial().getLocalEstoqueDestino());
                guiaVo.setRequisicaoMaterial(em.find(RequisicaoMaterial.class, guia.getRequisicaoMaterial().getId()));
                EnderecoLocalEstoque enderecoLocalEst = getEnderecoLocalEstoque(guia.getRequisicaoMaterial().getLocalEstoqueDestino());
                guiaVo.setEnderecoCompleto(enderecoLocalEst !=null ? enderecoLocalEst.getEnderecoCompleto() : "Endereço não cadastrado");

                List<ItemRequisicaoMaterial> itensReq = requisicaoMaterialFacade.buscarItensRequisicao(guia.getRequisicaoMaterial());
                for (ItemRequisicaoMaterial itemReq : itensReq) {
                    GuiaDistribuicaoItemVO itemGuia = new GuiaDistribuicaoItemVO();
                    itemGuia.setItemRequisicaoMaterial(itemReq);
                    guiaVo.getItens().add(itemGuia);
                }
                guiasVo.add(guiaVo);
            }
        }

        Collections.sort(guiasVo);
        return guiasVo;
    }

    public EnderecoLocalEstoque getEnderecoLocalEstoque(LocalEstoque localEstoque) {
        if (localEstoque.getCadastroImobiliario() != null) {
            return localEstoqueFacade.recuperarEnderecoLocalEstoque(localEstoque.getCadastroImobiliario().getId());
        }
        return null;
    }

    public Material getMaterialGuiaDistribuicao(Cardapio cardapio, ItemRequisicaoDeCompra itemReq) {
        String sql = " select mat.* from guiadistribuicaoreqitem item " +
            "           inner join itemrequisicaomaterial irm on irm.id = item.itemrequisicaomaterial_id " +
            "           inner join material mat on mat.id = irm.materialrequisitado_id " +
            "           inner join guiadistribuicaorequisicao guia on guia.id = item.guiadistribuicaorequisicao_id " +
            "           inner join cardapiorequisicaocompra crc on crc.id = guia.cardapiorequisicaocompra_id " +
            "         where item.itemrequisicaocompra_id = :idItemReq " +
            "           and crc.cardapio_id = :idCardapio ";
        Query q = em.createNativeQuery(sql, Material.class);
        q.setParameter("idItemReq", itemReq.getId());
        q.setParameter("idCardapio", cardapio.getId());
        try {
            return (Material) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public CardapioRequisicaoCompra recuperarCardapioRequisicaoCompra(RequisicaoDeCompra requisicao) {
        String sql = " select crc.* from cardapiorequisicaocompra crc  " +
            "          where crc.requisicaocompra_id = :idRequisicao ";
        Query q = em.createNativeQuery(sql, CardapioRequisicaoCompra.class);
        q.setParameter("idRequisicao", requisicao.getId());
        try {
            return (CardapioRequisicaoCompra) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public BigDecimal getQuantidadeItemGuia(Material material, ItemContrato itemContrato, LocalEstoque localEstoque, CardapioRequisicaoCompra entity) {
        String sql = " select coalesce(sum(irm.quantidade), 0) as quantidade " +
            "          from guiadistribuicaoreqitem item " +
            "           inner join guiadistribuicaorequisicao guia on guia.id = item.guiadistribuicaorequisicao_id " +
            "           inner join requisicaomaterial req on req.id = guia.requisicaomaterial_id " +
            "           inner join itemrequisicaomaterial irm on irm.id = item.itemrequisicaomaterial_id " +
            "           inner join itemrequisicaodecompra irc on irc.id = item.itemrequisicaocompra_id " +
            "         where irm.materialrequisitado_id = :idMaterial " +
            "         and irc.itemcontrato_id = :idItemContrato " +
            "         and req.localestoquedestino_id = :idLocalEstoque" +
            "         and guia.cardapiorequisicaocompra_id = :idCardapioReq ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMaterial", material.getId());
        q.setParameter("idItemContrato", itemContrato.getId());
        q.setParameter("idLocalEstoque", localEstoque.getId());
        q.setParameter("idCardapioReq", entity.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getQuantidadeUtilizadaItemRequisicao(ItemContrato itemContrato, ItemRequisicaoDeCompra itemReq, CardapioRequisicaoCompra entity) {
        String sql = " select coalesce(sum(irm.quantidade), 0) as quantidade " +
            "          from guiadistribuicaoreqitem item " +
            "           inner join guiadistribuicaorequisicao guia on guia.id = item.guiadistribuicaorequisicao_id " +
            "           inner join requisicaomaterial req on req.id = guia.requisicaomaterial_id " +
            "           inner join itemrequisicaodecompra irc on irc.id = item.itemrequisicaocompra_id " +
            "           inner join itemrequisicaomaterial irm on irm.id = item.itemrequisicaomaterial_id " +
            "         where irc.itemcontrato_id = :idItemContrato " ;
        sql += itemReq != null && itemReq.getId() != null ?
            "        and irc.id <> :idItemReq " : "";
        sql += entity != null && entity.getId() != null ?
            "       and guia.cardapiorequisicaocompra_id <> :idCardapioReq " : "";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemContrato", itemContrato.getId());
        if (itemReq != null && itemReq.getId() !=null) {
            q.setParameter("idItemReq", itemReq.getId());
        }
        if (entity != null && entity.getId() !=null) {
            q.setParameter("idCardapioReq", entity.getId());
        }
        return (BigDecimal) q.getSingleResult();
    }

    public List<RequisicaoCompraGuiaItemVO> criarRequisicaoCompraGuiaItemVO(String condicao) {
        String sql;
        sql = " select distinct mat.id  as id_materail," +
            "                   ic.id   as id_itemcontrato, " +
            "                   coalesce(ipc.numero, ise.numero, itcot.numero) as numeroitem " +
            "   from contrato cont  " +
            "         inner join unidadecontrato unid on cont.id = unid.contrato_id  " +
            "         inner join vwhierarquiaadministrativa vwha on vwha.subordinada_id = unid.unidadeadministrativa_id  " +
            "         inner join itemcontrato ic on cont.ID = ic.CONTRATO_ID  " +
            "         left join itemrequisicaodecompra irc on irc.itemcontrato_id = ic.id  " +
            "         left join itemcontratoitempropfornec icpf on ic.id = icpf.itemcontrato_id  " +
            "         left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id  " +
            "         left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id  " +
            "         left join itemcontratoitempropdisp icpd on ic.id = icpd.itemcontrato_id  " +
            "         left join itempropostafornedisp ipfd on icpd.itempropfornecdispensa_id = ipfd.id  " +
            "         left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id  " +
            "         left join itemprocessodecompra ipc on coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) = ipc.id  " +
            "         left join itemsolicitacao its on ipc.itemsolicitacaomaterial_id = its.id  " +
            "         left join itemcontratoitemsolext icse on ic.id = icse.itemcontrato_id  " +
            "         left join itemsolicitacaoexterno ise on icse.itemsolicitacaoexterno_id = ise.id  " +
            "         left join itemcontratovigente icv on ic.id = icv.itemcontrato_id  " +
            "         left join itemcotacao itcot on itcot.id = icv.itemcotacao_id  " +
            "         inner join objetocompra oc on coalesce(its.OBJETOCOMPRA_ID, ise.OBJETOCOMPRA_ID, itcot.OBJETOCOMPRA_ID) = oc.id  " +
            "         inner join material mat on oc.ID = mat.OBJETOCOMPRA_ID  " +
            "         inner join CARDAPIOAGENDAREFEICAOMAT cmat on cmat.MATERIAL_ID = mat.ID  " +
            "         inner join CardapioAgendaRefeicao cref on cref.id = cmat.CARDAPIOAGENDAREFEICAO_ID  " +
            "         inner join CardapioAgenda cagenda on cref.CARDAPIOAGENDA_ID = cagenda.ID  " +
            "         inner join cardapio card on card.id = cagenda.cardapio_id " +
            "         left join cardapiorequisicaocompra crc on crc.cardapio_id = card.id " +
            "       where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vwha.iniciovigencia) and coalesce(trunc(vwha.FIMVIGENCIA), to_date(:dataOperacao, 'dd/MM/yyyy'))  " +
            "       and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(unid.iniciovigencia) and coalesce(trunc(unid.FIMVIGENCIA),  to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "       and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(cont.INICIOVIGENCIA) and coalesce(trunc(cont.TERMINOVIGENCIAATUAL), to_date(:dataOperacao, 'dd/MM/yyyy')) "
            + condicao +
            " order by coalesce(ipc.numero, ise.numero, itcot.numero) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
        List<Object[]> list = q.getResultList();
        List<RequisicaoCompraGuiaItemVO> materiais = new ArrayList<>();
        for (Object[] obj : list) {
            RequisicaoCompraGuiaItemVO mat = new RequisicaoCompraGuiaItemVO();
            long idMaterial = ((BigDecimal) obj[0]).longValue();
            mat.setMaterial(em.find(Material.class, idMaterial));

            long idItemContrato = ((BigDecimal) obj[1]).longValue();
            mat.setItemContrato(em.find(ItemContrato.class, idItemContrato));
            mat.setContrato(mat.getItemContrato().getContrato());
            materiais.add(mat);
        }
        return materiais;
    }

    public List<GuiaDistribuicaoRequisicao> buscarGuiasDistribuicao(CardapioRequisicaoCompra cardapioReqCompra, SituacaoRequisicaoMaterial... situacoes) {
        String sql = " select guia.* from guiadistribuicaorequisicao guia " +
            "           inner join requisicaomaterial req on req.id = guia.requisicaomaterial_id " +
            "         where guia.cardapiorequisicaocompra_id = :idCardReq " +
            "           and req.tiposituacaorequisicao in (:situacoes)";
        Query q = em.createNativeQuery(sql, GuiaDistribuicaoRequisicao.class);
        q.setParameter("idCardReq", cardapioReqCompra.getId());
        q.setParameter("situacoes", Util.getListOfEnumName(situacoes));
        return q.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CardapioFacade getCardapioFacade() {
        return cardapioFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public RequisicaoMaterialFacade getRequisicaoMaterialFacade() {
        return requisicaoMaterialFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }
}
