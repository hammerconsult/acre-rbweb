package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroSubstituicaoObjetoCompra;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.entidadesauxiliares.SubstituicaoObjetoCompraMovimento;
import br.com.webpublico.entidadesauxiliares.SubstituicaoObjetoCompraVo;
import br.com.webpublico.enums.OrigemSubstituicaoObjetoCompra;
import br.com.webpublico.enums.TipoSolicitacaoRegistroPreco;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Stateless
public class SubstituicaoObjetoCompraFacade extends AbstractFacade<SubstituicaoObjetoCompra> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private CotacaoFacade cotacaoFacade;
    @EJB
    private FormularioCotacaoFacade formularioCotacaoFacade;
    @EJB
    private IntencaoRegistroPrecoFacade intencaoRegistroPrecoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private RegistroSolicitacaoMaterialExternoFacade registroSolicitacaoMaterialExternoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private ItemPregaoFacade itemPregaoFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;

    public SubstituicaoObjetoCompraFacade() {
        super(SubstituicaoObjetoCompra.class);
    }

    @Override
    public SubstituicaoObjetoCompra recuperar(Object id) {
        SubstituicaoObjetoCompra entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        return entity;
    }

    public SubstituicaoObjetoCompra salvarRetornando(SubstituicaoObjetoCompra entity, List<SubstituicaoObjetoCompraVo> objetosCompra) {
        substituirObjetoCompra(objetosCompra);
        criarItemSubstituicaoObjetoCompra(entity, objetosCompra);
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(SubstituicaoObjetoCompra.class, "numero"));
        }
        entity.setHistorico(gerarHistorico(objetosCompra));
        return super.salvarRetornando(entity);
    }

    private void criarItemSubstituicaoObjetoCompra(SubstituicaoObjetoCompra entity, List<SubstituicaoObjetoCompraVo> itens) {
        for (SubstituicaoObjetoCompraVo item : itens) {
            SubstituicaoObjetoCompraItem itemSubstituicao = new SubstituicaoObjetoCompraItem();
            itemSubstituicao.setNumeroItem(item.getNumeroItem());
            itemSubstituicao.setSubstituicaoObjetoCompra(entity);
            itemSubstituicao.setObjetoCompraDe(item.getObjetoCompraDe());
            itemSubstituicao.setObjetoCompraPara(item.getObjetoCompraPara());
            itemSubstituicao.setEspecificacaoDe(item.getEspecificacaoDe());
            itemSubstituicao.setEspecificacaoPara(item.getEspecificacaoPara());
            itemSubstituicao.setUnidadeMedidaDe(item.getUnidadeMedidaDe());
            itemSubstituicao.setUnidadeMedidaPara(item.getUnidadeMedidaPara());
            itemSubstituicao.setQuantidadeDe(item.getQuantidadeDe());
            itemSubstituicao.setQuantidadePara(item.getQuantidadePara());
            Util.adicionarObjetoEmLista(entity.getItens(), itemSubstituicao);
        }
    }

    private void substituirObjetoCompra(List<SubstituicaoObjetoCompraVo> itens) {
        SubstituicaoObjetoCompraMovimento movSolicitacao = null;
        for (SubstituicaoObjetoCompraVo objetoCompraVo : itens) {
            for (SubstituicaoObjetoCompraMovimento mov : objetoCompraVo.getMovimentos()) {
                switch (mov.getTipoMovimento()) {
                    case IRP:
                        ItemIntencaoRegistroPreco itemIrp = em.find(ItemIntencaoRegistroPreco.class, mov.getIdItem());
                        itemIrp.setObjetoCompra(getObjetoCompraSubstituicao(objetoCompraVo));
                        itemIrp.setDescricaoComplementar(getDescricaoComplementarSubstituicao(objetoCompraVo));
                        itemIrp.setUnidadeMedida(getUnidadeMedidaSubstituicao(objetoCompraVo));
                        itemIrp.setQuantidade(getQuantidadeSubstituicao(objetoCompraVo));
                        em.merge(itemIrp);
                        break;

                    case FORMULARIO_COTACAO:
                        ItemLoteFormularioCotacao itemFormulario = em.find(ItemLoteFormularioCotacao.class, mov.getIdItem());
                        itemFormulario.setObjetoCompra(getObjetoCompraSubstituicao(objetoCompraVo));
                        itemFormulario.setDescricaoComplementar(getDescricaoComplementarSubstituicao(objetoCompraVo));
                        itemFormulario.setUnidadeMedida(getUnidadeMedidaSubstituicao(objetoCompraVo));
                        itemFormulario.setQuantidade(getQuantidadeSubstituicao(objetoCompraVo));
                        em.merge(itemFormulario);
                        break;

                    case COTACAO:
                        ItemCotacao itemCotacao = em.find(ItemCotacao.class, mov.getIdItem());
                        itemCotacao.setObjetoCompra(getObjetoCompraSubstituicao(objetoCompraVo));
                        itemCotacao.setDescricaoComplementar(getDescricaoComplementarSubstituicao(objetoCompraVo));
                        itemCotacao.setUnidadeMedida(getUnidadeMedidaSubstituicao(objetoCompraVo));
                        itemCotacao.setQuantidade(getQuantidadeSubstituicao(objetoCompraVo));
                        em.merge(itemCotacao);
                        break;

                    case SOLICITACAO_COMPRA:
                        ItemSolicitacao itemSol = em.find(ItemSolicitacao.class, mov.getIdItem());
                        itemSol.setObjetoCompra(getObjetoCompraSubstituicao(objetoCompraVo));
                        itemSol.setDescricaoComplementar(getDescricaoComplementarSubstituicao(objetoCompraVo));
                        itemSol.setUnidadeMedida(getUnidadeMedidaSubstituicao(objetoCompraVo));
                        itemSol.setQuantidade(getQuantidadeSubstituicao(objetoCompraVo));
                        itemSol.setPrecoTotal(itemSol.getQuantidade().multiply(itemSol.getPreco()).setScale(2, RoundingMode.HALF_EVEN));
                        em.merge(itemSol);
                        movSolicitacao = mov;
                        break;

                    case SOLICITACAO_REGISTRO_PRECO_EXTERNO:
                        ItemSolicitacaoExterno itemSolExt = em.find(ItemSolicitacaoExterno.class, mov.getIdItem());
                        itemSolExt.setObjetoCompra(getObjetoCompraSubstituicao(objetoCompraVo));
                        itemSolExt.setDescricaoComplementar(getDescricaoComplementarSubstituicao(objetoCompraVo));
                        itemSolExt.setDescricaoProduto(objetoCompraVo.hasObjetoCompraPara() ? objetoCompraVo.getObjetoCompraPara().getDescricao() : objetoCompraVo.getObjetoCompraDe().getDescricao());
                        itemSolExt.setUnidadeMedida(getUnidadeMedidaSubstituicao(objetoCompraVo));
                        itemSolExt.setQuantidade(getQuantidadeSubstituicao(objetoCompraVo));
                        itemSolExt.setValorTotal(itemSolExt.getQuantidade().multiply(itemSolExt.getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN));
                        em.merge(itemSolExt);
                        break;

                    case ADESAO_ATA_REGISTRO_PRECO_INTERNA:
                        ItemSolicitacaoExterno itemAtaInterna = em.find(ItemSolicitacaoExterno.class, mov.getIdItem());
                        itemAtaInterna.setObjetoCompra(getObjetoCompraSubstituicao(objetoCompraVo));
                        itemAtaInterna.setDescricaoComplementar(getDescricaoComplementarSubstituicao(objetoCompraVo));
                        itemAtaInterna.setDescricaoProduto(objetoCompraVo.hasObjetoCompraPara() ? objetoCompraVo.getObjetoCompraPara().getDescricao() : objetoCompraVo.getObjetoCompraDe().getDescricao());
                        itemAtaInterna.setUnidadeMedida(getUnidadeMedidaSubstituicao(objetoCompraVo));
                        itemAtaInterna.setQuantidade(getQuantidadeSubstituicao(objetoCompraVo));
                        itemAtaInterna.setValorTotal(itemAtaInterna.getQuantidade().multiply(itemAtaInterna.getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN));
                        em.merge(itemAtaInterna);
                        break;

                    case LICITACAO:
                    case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                        ItemProcessoDeCompra itemProcesso = em.find(ItemProcessoDeCompra.class, mov.getIdItem());
                        itemProcesso.setQuantidade(getQuantidadeSubstituicao(objetoCompraVo));
                        em.merge(itemProcesso);
                        break;

                    case REQUISICAO_COMPRA:
                        ItemRequisicaoDeCompra itemReq = em.find(ItemRequisicaoDeCompra.class, mov.getIdItem());
                        itemReq.setObjetoCompra(getObjetoCompraSubstituicao(objetoCompraVo));
                        itemReq.setDescricaoComplementar(getDescricaoComplementarSubstituicao(objetoCompraVo));
                        itemReq.setUnidadeMedida(getUnidadeMedidaSubstituicao(objetoCompraVo));
                        em.merge(itemReq);
                        break;
                }
            }
            salvarEspecificacaoObjetoCompra(objetoCompraVo.getObjetoCompraPara(), objetoCompraVo.getEspecificacaoPara());
        }
        em.flush();
        if (movSolicitacao != null) {
            atualizarValorLote(movSolicitacao);
        }
    }

    private static BigDecimal getQuantidadeSubstituicao(SubstituicaoObjetoCompraVo objetoCompraVo) {
        return objetoCompraVo.hasQuantidadePara() ? objetoCompraVo.getQuantidadePara() : objetoCompraVo.getQuantidadeDe();
    }

    private static UnidadeMedida getUnidadeMedidaSubstituicao(SubstituicaoObjetoCompraVo objetoCompraVo) {
        return objetoCompraVo.hasUnidadeMedidaPara() ? objetoCompraVo.getUnidadeMedidaPara() : objetoCompraVo.getUnidadeMedidaDe();
    }

    private static String getDescricaoComplementarSubstituicao(SubstituicaoObjetoCompraVo objetoCompraVo) {
        return !Strings.isNullOrEmpty(objetoCompraVo.getEspecificacaoPara()) ? objetoCompraVo.getEspecificacaoPara() : objetoCompraVo.getEspecificacaoDe();
    }

    private static ObjetoCompra getObjetoCompraSubstituicao(SubstituicaoObjetoCompraVo objetoCompraVo) {
        return objetoCompraVo.hasObjetoCompraPara() ? objetoCompraVo.getObjetoCompraPara() : objetoCompraVo.getObjetoCompraDe();
    }

    private void atualizarValorLote(SubstituicaoObjetoCompraMovimento movSol) {
        SolicitacaoMaterial solicitacao = atualizarValorLoteSolicitacaoCompra(movSol);

        ProcessoDeCompra processoCompra = processoDeCompraFacade.recuperarProcessoCompraPorSolicitacao(movSol.getId());
        if (processoCompra != null) {
            BigDecimal valorTotalLote = atualizarValorLoteProcessoCompra(processoCompra);

            if (solicitacao.getModalidadeLicitacao().isDispensaLicitacao() || solicitacao.getModalidadeLicitacao().isInexigibilidade()) {
                atualizarValorLoteDispensa(processoCompra);
            } else {
                atualizarValorLoteLicitacao(processoCompra, valorTotalLote);
            }
        }
    }

    private void atualizarValorLoteLicitacao(ProcessoDeCompra processoCompra, BigDecimal valorTotalLote) {
        Licitacao licitacao = licitacaoFacade.recuperarLicitacaoPorProcessoCompra(processoCompra);
        if (licitacao != null) {
            licitacao.setValorMaximo(valorTotalLote);
            em.merge(licitacao);

            List<PropostaFornecedor> propostas = propostaFornecedorFacade.buscarPropostaFornecedorPorLicitacao(licitacao);
            for (PropostaFornecedor pf : propostas) {
                for (LotePropostaFornecedor lotePf : pf.getLotes()) {
                    propostaFornecedorFacade.atualizaValorDoLote(lotePf);
                    em.merge(lotePf);
                }
            }
        }
    }

    private void atualizarValorLoteDispensa(ProcessoDeCompra processoCompra) {
        BigDecimal valorTotalLote;
        DispensaDeLicitacao dispensa = dispensaDeLicitacaoFacade.recuperarDispensaLicitacaoPorProcessoCompra(processoCompra);
        if (dispensa != null) {
            valorTotalLote = BigDecimal.ZERO;
            dispensa = dispensaDeLicitacaoFacade.recuperraComDependenciasFornecedores(dispensa.getId());
            for (FornecedorDispensaDeLicitacao forn : dispensa.getFornecedores()) {
                if (forn.getPropostaFornecedorDispensa() != null) {
                    for (LotePropostaFornecedorDispensa lote : forn.getPropostaFornecedorDispensa().getLotesDaProposta()) {

                        for (ItemPropostaFornecedorDispensa item : lote.getItensProposta()) {
                            item.setQuantidade(item.getItemProcessoDeCompra().getQuantidade());
                            em.merge(item);
                        }
                        lote.setValor(lote.getValorTotalItens());
                        valorTotalLote = valorTotalLote.add(lote.getValor());
                    }
                }
            }
            dispensa.setValorMaximo(dispensa.getValorMaximo().add(valorTotalLote));
        }
    }

    private BigDecimal atualizarValorLoteProcessoCompra(ProcessoDeCompra processoCompra) {
        BigDecimal valorTotalLote = BigDecimal.ZERO;
        List<LoteProcessoDeCompra> lotesProcesso = processoDeCompraFacade.recuperarLotesProcesso(processoCompra);
        for (LoteProcessoDeCompra lotePc : lotesProcesso) {
            lotePc.setValor(lotePc.getValorTotalItens());
            valorTotalLote = valorTotalLote.add(lotePc.getValor());
            em.merge(lotePc);
        }
        return valorTotalLote;
    }

    private SolicitacaoMaterial atualizarValorLoteSolicitacaoCompra(SubstituicaoObjetoCompraMovimento movSol) {
        SolicitacaoMaterial solicitacao = em.find(SolicitacaoMaterial.class, movSol.getId());
        List<LoteSolicitacaoMaterial> lotesSolicitacao = solicitacaoMaterialFacade.buscarLotesWithItens(solicitacao);
        for (LoteSolicitacaoMaterial loteSol : lotesSolicitacao) {
            loteSol.setValor(loteSol.getValorTotalItens());
        }
        solicitacao.setValor(solicitacao.getValorTotalLotes());
        em.merge(solicitacao);
        em.flush();
        return solicitacao;
    }

    public String gerarHistorico(List<SubstituicaoObjetoCompraVo> itens) {
        StringBuilder dePara = new StringBuilder();
        String movimentos = "";
        for (SubstituicaoObjetoCompraVo item : itens) {
            if (item.hasObjetoCompraPara()) {
                dePara.append("<div style='color: darkblue'>").append("<b>De: </b>").append(item.getObjetoCompraDe()).append("</div>");
                dePara.append("<div style='color: darkgreen'>").append("<b>Para: </b>").append(item.getObjetoCompraPara()).append("</div>");
            }
            if (Strings.isNullOrEmpty(movimentos)) {
                for (SubstituicaoObjetoCompraMovimento mov : item.getMovimentos()) {
                    movimentos += "<b>" + mov.getTipoMovimento().getDescricao().toUpperCase() + ": </b> " + " " + mov.getNumero() + " (ID " + mov.getId() + ")<br/>";
                }
            }
        }
        return dePara + movimentos;
    }

    private void salvarEspecificacaoObjetoCompra(ObjetoCompra objetoCompra, String especificacao) {
        List<ObjetoDeCompraEspecificacao> list = Lists.newArrayList();
        if (!Strings.isNullOrEmpty(especificacao)) {
            objetoCompraFacade.adicionarNovaEspecificacao(objetoCompra, especificacao, list);
        }
        objetoCompraFacade.salvarEspecificacoes(list);
    }

    public GrupoContaDespesa getGrupoContaDespesa(ObjetoCompra objetoCompra) {
        return objetoCompraFacade.criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra());
    }

    public List<SubstituicaoObjetoCompraVo> buscarObjetosCompraAndMovimentos(SubstituicaoObjetoCompra entity) {
        List<SubstituicaoObjetoCompraVo> itensVo = Lists.newArrayList();
        for (SubstituicaoObjetoCompraItem itemSub : entity.getItens()) {
            FiltroSubstituicaoObjetoCompra filtro = novoFiltroConsulta(entity, itemSub);
            SubstituicaoObjetoCompraVo item = new SubstituicaoObjetoCompraVo();
            item.setNumeroItem(itemSub.getNumeroItem());
            item.setObjetoCompraDe(itemSub.getObjetoCompraDe());
            item.setObjetoCompraPara(itemSub.getObjetoCompraPara());
            item.setEspecificacaoDe(itemSub.getEspecificacaoDe());
            item.setEspecificacaoPara(itemSub.getEspecificacaoPara());
            item.setGrupoContaDespesaDe(getGrupoContaDespesa(itemSub.getObjetoCompraDe()));
            if (itemSub.getObjetoCompraPara() != null) {
                item.setGrupoContaDespesaPara(getGrupoContaDespesa(itemSub.getObjetoCompraPara()));
            }
            item.setUnidadeMedidaDe(itemSub.getUnidadeMedidaDe());
            item.setUnidadeMedidaPara(itemSub.getUnidadeMedidaPara());
            item.setQuantidadeDe(itemSub.getQuantidadeDe());
            item.setQuantidadePara(itemSub.getQuantidadePara());

            filtro.setObjetoCompra(item.getObjetoCompraDe());
            List<SubstituicaoObjetoCompraMovimento> movimentos = buscarMovimentosObjetoCompra(filtro);
            item.setMovimentos(movimentos);
            itensVo.add(item);
        }
        return itensVo;
    }

    private FiltroSubstituicaoObjetoCompra novoFiltroConsulta(SubstituicaoObjetoCompra entity, SubstituicaoObjetoCompraItem item) {
        FiltroSubstituicaoObjetoCompra filtro = new FiltroSubstituicaoObjetoCompra();
        filtro.setOrigemSubstituicao(entity.getOrigemSubstituicao());
        Long idObjeto = item.getObjetoCompraPara() != null ? item.getObjetoCompraPara().getId() : item.getObjetoCompraDe().getId();
        String condicao = " where oc.id = " + idObjeto;
        if (item.getNumeroItem() != null) {
            condicao += " and item.numero = " + item.getNumeroItem();
        }

        if (entity.getOrigemSubstituicao().isContrato()) {
            Contrato contrato = contratoFacade.recuperarSemDependencias(entity.getIdMovimento());
            if (filtro.getOrigemSubstituicao().isContrato()) {
                condicao += " and cont.id = " + entity.getIdMovimento();
            }
            if (contrato.getTipoAquisicao().isDispensa()) {
                condicao += " and disp.id = " + contrato.getDispensaDeLicitacao().getId();
            }
            if (contrato.isProcessoLicitatorio()) {
                condicao += " and lic.id = " + contrato.getLicitacao().getId();
            }
            if (contrato.isDeRegistroDePrecoExterno()) {
                condicao += " and reg.id = " + contrato.getRegistroSolicitacaoMaterialExterno().getId();
                filtro.setContratoRegistroPrecoExterno(true);
            }
        }
        if (filtro.getOrigemSubstituicao().isLicitacao()) {
            condicao += " and lic.id = " + entity.getIdMovimento();
        }
        if (filtro.getOrigemSubstituicao().isDispensa()) {
            condicao += " and disp.id = " + entity.getIdMovimento();
        }
        if (filtro.getOrigemSubstituicao().isSolicitacaoCompra()) {
            condicao += " and sol.id = " + entity.getIdMovimento();
        }
        if (filtro.getOrigemSubstituicao().isCotacao()) {
            condicao += " and cot.id = " + entity.getIdMovimento();
        }
        if (filtro.getOrigemSubstituicao().isFormularioCotacao()) {
            condicao += " and fc.id = " + entity.getIdMovimento();
        }
        if (filtro.getOrigemSubstituicao().isIrp()) {
            condicao += " and irp.id = " + entity.getIdMovimento();
        }
        if (filtro.getOrigemSubstituicao().isRegistroPrecoExterno()) {
            condicao += " and reg.id = " + entity.getIdMovimento();
        }
        filtro.setCondicaoSql(condicao);
        return filtro;
    }

    @TransactionAttribute(value = TransactionAttributeType.NOT_SUPPORTED)
    public List<SubstituicaoObjetoCompraVo> buscarObjetosCompraPorProcesso(FiltroSubstituicaoObjetoCompra filtro) {
        String sql;
        if (filtro.getOrigemSubstituicao().isContrato()) {
            sql = "select " +
                "       ic.id                                                                       as id_item," +
                "       coalesce(ipc.numero, ise.numero)                                            as numero_item," +
                "       coalesce(lote.numero, 1)                                                    as numero_Lote," +
                "       to_char(coalesce(itemsol.descricaocomplementar, ise.descricaocomplementar)) as desc_complementar, " +
                "       coalesce(itemsol.unidademedida_id, ise.unidademedida_id)                    as unidade_medida, " +
                "       coalesce(itemsol.quantidade, ise.quantidade)                                as quantidade, " +
                "       oc.id                                                                       as id_objeto_compra," +
                "       cont.id                                                                     as id_mov" +
                "   from contrato cont " +
                "       inner join itemcontrato ic on ic.contrato_id = cont.id " +
                "       left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                "       left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                "       left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                "       left join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                "       left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                "       left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id " +
                "       left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
                "       left join itemprocessodecompra ipc on ipc.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                "       left join loteprocessodecompra lote on lote.id = ipc.loteprocessodecompra_id  " +
                "       left join itemsolicitacao itemsol on itemsol.id = ipc.itemsolicitacaomaterial_id" +
                "       left join itemsolicitacaoexterno ise on ise.id = icise.itemsolicitacaoexterno_id  " +
                "       inner join objetocompra oc on oc.id = coalesce(itemsol.objetocompra_id,ise.objetocompra_id) " + filtro.getCondicaoSql() +
                " order by coalesce(ipc.numero, ise.numero) ";
        } else if (filtro.getOrigemSubstituicao().isLicitacao()) {
            sql = "select " +
                "       ipc.id                                  as id_item," +
                "       ipc.numero                              as numero_item," +
                "       lote.numero                             as numero_Lote," +
                "       to_char(item.descricaocomplementar)     as desc_complementar, " +
                "       item.unidademedida_id                   as unidade_medida, " +
                "       ipc.quantidade                          as quantidade, " +
                "       oc.id                                   as id_objeto_compra," +
                "       lic.id                                  as id_mov" +
                "   from licitacao lic " +
                "       inner join processodecompra pc on lic.processodecompra_id = pc.id " +
                "       inner join loteprocessodecompra lote on lote.processodecompra_id = pc.id  " +
                "       inner join itemprocessodecompra ipc on ipc.loteprocessodecompra_id = lote.id " +
                "       inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id" +
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + filtro.getCondicaoSql();
        } else if (filtro.getOrigemSubstituicao().isDispensa()) {
            sql = "select " +
                "       ipc.id                                  as id_item," +
                "       ipc.numero                              as numero_item," +
                "       lote.numero                             as numero_Lote," +
                "       to_char(item.descricaocomplementar)     as desc_complementar, " +
                "       item.unidademedida_id                   as unidade_medida, " +
                "       ipc.quantidade                          as quantidade, " +
                "       oc.id                                   as id_objeto_compra," +
                "       disp.id                                 as id_mov" +
                "   from dispensadelicitacao disp " +
                "       inner join processodecompra pc on disp.processodecompra_id = pc.id " +
                "       inner join loteprocessodecompra lote on lote.processodecompra_id = pc.id  " +
                "       inner join itemprocessodecompra ipc on ipc.loteprocessodecompra_id = lote.id " +
                "       inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id" +
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + filtro.getCondicaoSql();
        } else if (filtro.getOrigemSubstituicao().isSolicitacaoCompra()) {
            sql = "select " +
                "       item.id                                 as id_item," +
                "       item.numero                             as numero_item," +
                "       lote.numero                             as numero_Lote," +
                "       to_char(item.descricaocomplementar)     as desc_complementar, " +
                "       item.unidademedida_id                   as unidade_medida, " +
                "       item.quantidade                         as quantidade, " +
                "       oc.id                                   as id_objeto_compra," +
                "       sol.id                                  as id_mov" +
                " from solicitacaomaterial sol " +
                "      inner join lotesolicitacaomaterial lote on lote.solicitacaomaterial_id = sol.id " +
                "      inner join itemsolicitacao item on item.lotesolicitacaomaterial_id = lote.id " +
                "      inner join objetocompra oc on oc.id = item.objetocompra_id " + filtro.getCondicaoSql();
        } else if (filtro.getOrigemSubstituicao().isCotacao()) {
            sql = "select " +
                "       item.id                                 as id_item," +
                "       item.numero                             as numero_item," +
                "       lote.numero                             as numero_Lote," +
                "       to_char(item.descricaocomplementar)     as desc_complementar, " +
                "       item.unidademedida_id                   as unidade_medida, " +
                "       item.quantidade                         as quantidade, " +
                "       oc.id                                   as id_objeto_compra," +
                "       cot.id                                  as id_mov" +
                "  from cotacao cot " +
                "       inner join lotecotacao lote on cot.id = lote.cotacao_id " +
                "       inner join itemcotacao item on lote.id = item.lotecotacao_id  " +
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "       inner join cotacao cot on cot.id = lote.cotacao_id " + filtro.getCondicaoSql();
        } else if (filtro.getOrigemSubstituicao().isFormularioCotacao()) {
            sql = "select " +
                "       item.id                                 as id_item," +
                "       item.numero                             as numero_item," +
                "       lote.numero                             as numero_Lote," +
                "       to_char(item.descricaocomplementar)     as desc_complementar, " +
                "       item.unidademedida_id                   as unidade_medida, " +
                "       item.quantidade                         as quantidade, " +
                "       oc.id                                   as id_objeto_compra," +
                "       fc.id                                   as id_mov" +
                "   from formulariocotacao fc " +
                "       inner join loteformulariocotacao lote on lote.formulariocotacao_id = fc.id " +
                "       inner join itemloteformulariocotacao item on item.loteformulariocotacao_id = lote.id " +
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + filtro.getCondicaoSql();
        } else if (filtro.getOrigemSubstituicao().isIrp()) {
            sql = "select " +
                "       item.id                                 as id_item," +
                "       item.numero                             as numero_item," +
                "       lote.numero                             as numero_Lote," +
                "       to_char(item.descricaocomplementar)     as desc_complementar, " +
                "       item.unidademedida_id                   as unidade_medida, " +
                "       item.quantidade                         as quantidade, " +
                "       oc.id                                   as id_objeto_compra," +
                "       irp.id                                  as id_mov" +
                "   from intencaoregistropreco irp " +
                "      inner join loteintencaoregistropreco lote on lote.intencaoregistropreco_id = irp.id " +
                "      inner join itemintencaoregistropreco item on item.loteintencaoregistropreco_id = lote.id " +
                "      inner join objetocompra oc on oc.id = item.objetocompra_id " + filtro.getCondicaoSql();
        } else {
            sql = "select " +
                "       item.id                                 as id_item," +
                "       item.numero                             as numero_item," +
                "       1                                       as numero_Lote," +
                "       to_char(item.descricaocomplementar)     as desc_complementar, " +
                "       item.unidademedida_id                   as unidade_medida, " +
                "       item.quantidade                         as quantidade, " +
                "       oc.id                                   as id_objeto_compra," +
                "       reg.id                                  as id_mov" +
                " from solicitacaomaterialext sol " +
                "     inner join registrosolmatext reg on reg.solicitacaomaterialexterno_id = sol.id " +
                "     inner join itemsolicitacaoexterno item on sol.id = item.solicitacaomaterialexterno_id " +
                "     inner join objetocompra oc on oc.id = item.objetocompra_id " + filtro.getCondicaoSql();
        }
        Query q = em.createNativeQuery(sql);
        List<SubstituicaoObjetoCompraVo> itensVo = Lists.newArrayList();
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] o : (List<Object[]>) resultList) {
                SubstituicaoObjetoCompraVo item = new SubstituicaoObjetoCompraVo();
                item.setIdItem(((BigDecimal) o[0]).longValue());
                item.setNumeroItem(((BigDecimal) o[1]).longValue());
                item.setNumeroLote(((BigDecimal) o[2]).longValue());
                item.setEspecificacaoDe((String) o[3]);
                if (o[4] != null) {
                    UnidadeMedida unidadeMedida = em.find(UnidadeMedida.class, ((BigDecimal) o[4]).longValue());
                    item.setUnidadeMedidaDe(unidadeMedida);
                }
                item.setQuantidadeDe((BigDecimal) o[5]);
                ObjetoCompra objetoCompraDe = em.find(ObjetoCompra.class, ((BigDecimal) o[6]).longValue());
                item.setObjetoCompraDe(objetoCompraDe);
                item.setGrupoContaDespesaDe(getGrupoContaDespesa(objetoCompraDe));
                item.setIdMovimento(((BigDecimal) o[7]).longValue());
                itensVo.add(item);
            }
        }
        return itensVo;
    }

    public List<SubstituicaoObjetoCompraMovimento> buscarMovimentosObjetoCompra(FiltroSubstituicaoObjetoCompra filtro) {
        String sql = "";
        if (filtro.getOrigemSubstituicao().isRegistroPrecoExterno() || filtro.getContratoRegistroPrecoExterno()) {
            sql = "" +
                "select distinct * from ( " +
                " select 'REQUISICAO_COMPRA' as tipo_processo, " +
                "       req.id              as id_movimento, " +
                "       item.id             as id_item, " +
                "       req.numero          as numero, " +
                "       req.datarequisicao  as data_mov, " +
                "       ex.ano              as exercicio, " +
                "       req.descricao       as descricao, " +
                "       1                   as ordem_movimento " +
                " from itemrequisicaodecompra item " +
                "         inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "         inner join requisicaodecompra req on req.id = item.requisicaodecompra_id " +
                "         inner join contrato cont on cont.id = req.contrato_id " +
                "         inner join conregprecoexterno cr on cr.contrato_id = cont.id " +
                "         inner join registrosolmatext reg on cr.regsolmatext_id = reg.id " +
                "         inner join exercicio ex on ex.id = cont.exerciciocontrato_id " + filtro.getCondicaoSql() +
                "               and item.quantidade > 0 " +
                "  union all " +
                " select 'CONTRATO'                     as tipo_processo, " +
                "       cont.id                         as id_movimento, " +
                "       ic.id                           as id_item, " +
                "       to_number(cont.numerocontrato)  as numero, " +
                "       cont.datalancamento             as data_mov, " +
                "       ex.ano                          as exercicio, " +
                "       cont.objeto                     as descricao, " +
                "       2                               as ordem_movimento " +
                " from contrato cont " +
                "       inner join exercicio ex on ex.id = cont.exerciciocontrato_id " +
                "       inner join conregprecoexterno cr on cr.contrato_id = cont.id " +
                "       inner join registrosolmatext reg on cr.regsolmatext_id = reg.id " +
                "       inner join itemcontrato ic on ic.contrato_id = cont.id " +
                "       inner join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                "       inner join itemsolicitacaoexterno item on item.id = icise.itemsolicitacaoexterno_id  " +
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + filtro.getCondicaoSql() +
                "  union all " +
                " select 'EXECUCAO_CONTRATO'  as tipo_processo, " +
                "         ec.id               as id_movimento, " +
                "         ic.id               as id_item, " +
                "         ec.numero           as numero, " +
                "         ec.datalancamento   as data_mov, " +
                "         ex.ano              as exercicio, " +
                "         cont.objeto         as descricao, " +
                "         2                   as ordem_movimento " +
                "      from execucaocontrato ec " +
                "       INNER JOIN EXECUCAOCONTRATOITEM eci ON eci.EXECUCAOCONTRATO_ID = ec.ID " +
                "       INNER JOIN itemcontrato ic ON ic.id = eci.ITEMCONTRATO_ID " +
                "       inner join contrato cont on cont.id = ec.contrato_id " +
                "       inner join conregprecoexterno cr on cr.contrato_id = cont.id " +
                "       inner join registrosolmatext reg on cr.regsolmatext_id = reg.id " +
                "       inner join exercicio ex on ex.id = cont.exerciciocontrato_id " +
                "       inner join itemcontrato ic on ic.contrato_id = cont.id " +
                "       inner join itemcontratoitemsolext icise on icise.itemcontrato_id = ic.id " +
                "       inner join itemsolicitacaoexterno item on item.id = icise.itemsolicitacaoexterno_id  " +
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + filtro.getCondicaoSql() +
                " union all " +
                " select 'REGISTRO_PRECO_EXTERNO'   as tipo_processo, " +
                "       reg.id                      as id_movimento, " +
                "       item.id                     as id_item, " +
                "       reg.numeroregistro          as numero, " +
                "       reg.dataregistrocarona      as data_mov, " +
                "       ex.ano                      as exercicio, " +
                "       reg.objeto                  as descricao, " +
                "       3                           as ordem_movimento " +
                " from itemsolicitacaoexterno item " +
                "         inner join solicitacaomaterialext sol on sol.id = item.solicitacaomaterialexterno_id " +
                "         inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "         inner join registrosolmatext reg on reg.solicitacaomaterialexterno_id = sol.id " +
                "         left join conregprecoexterno cr on cr.regsolmatext_id = reg.id " +
                "         left join contrato cont on cr.contrato_id = cont.id " +
                "         inner join exercicio ex on ex.id = reg.exercicioregistro_id " + filtro.getCondicaoSql() +
                " union all " +
                " select 'SOLICITACAO_REGISTRO_PRECO_EXTERNO' as tipo_processo, " +
                "       sol.id                                as id_movimento, " +
                "       item.id                               as id_item, " +
                "       sol.numero                            as numero, " +
                "       sol.datasolicitacao                   as data_mov, " +
                "       ex.ano                                as exercicio, " +
                "       sol.objeto                            as descricao, " +
                "       4                                    as ordem_movimento " +
                " from itemsolicitacaoexterno item " +
                "         inner join solicitacaomaterialext sol on sol.id = item.solicitacaomaterialexterno_id " +
                "         inner join registrosolmatext reg on reg.solicitacaomaterialexterno_id = sol.id " +
                "         inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "         left join conregprecoexterno cr on cr.regsolmatext_id = reg.id " +
                "         left join contrato cont on cr.contrato_id = cont.id " +
                "         inner join exercicio ex on ex.id = sol.exercicio_id " + filtro.getCondicaoSql() +
                " ) " +
                " order by ordem_movimento desc ";
        } else {
            sql = "" +
                    "  select distinct * " +
                    "   from (" +
                    " select 'REQUISICAO_COMPRA' as tipo_processo, " +
                    "       req.id              as id_movimento, " +
                    "       item.id             as id_item, " +
                    "       req.numero          as numero, " +
                    "       req.datarequisicao  as data_mov, " +
                    "       ex.ano              as exercicio, " +
                    "       req.descricao       as descricao, " +
                    "       1                   as ordem_movimento " +
                    " from itemrequisicaodecompra item " +
                    "         inner join objetocompra oc on oc.id = item.objetocompra_id " +
                    "         inner join requisicaodecompra req on req.id = item.requisicaodecompra_id " +
                    "         inner join contrato cont on cont.id = req.contrato_id " +
                    "         left join conlicitacao cl on cl.contrato_id = cont.id " +
                    "         left join licitacao lic on cl.licitacao_id = lic.id " +
                    "         left join condispensalicitacao cd on cd.contrato_id = cont .id " +
                    "         left join dispensadelicitacao disp on cd.dispensadelicitacao_id = disp.id " +
                    "         left join processodecompra pc on pc.id = coalesce(lic.processodecompra_id, disp.processodecompra_id) " +
                    "         left join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                    "         left join cotacao cot on cot.id = sol.cotacao_id " +
                    "         left join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                    "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                    "         inner join exercicio ex on ex.id = coalesce(lic.exercicio_id, disp.exerciciodadispensa_id)" + filtro.getCondicaoSql() +
                    "               and item.quantidade > 0 " +
                    "  union all " +
                    " " +
                    "      select 'ADESAO_ATA_REGISTRO_PRECO_INTERNA'    as tipo_processo,  " +
                    "             solext.id                              as id_movimento,  " +
                    "             item.id                                as id_item,  " +
                    "             solext.numero                          as numero,  " +
                    "             solext.datasolicitacao                 as data_mov,  " +
                    "             ex.ano                                 as exercicio,  " +
                    "             solext.objeto                          as descricao,  " +
                    "             2                                      as ordem_movimento  " +
                    "      from itemsolicitacaoexterno item  " +
                    "               inner join solicitacaomaterialext solext on solext.id = item.solicitacaomaterialexterno_id  " +
                    "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                    "               inner join ataregistropreco ata on ata.id = solext.ataregistropreco_id  " +
                    "               inner join licitacao lic on lic.id = ata.licitacao_id  " +
                    "               inner join processodecompra pc on pc.id = lic.processodecompra_id  " +
                    "               inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id  " +
                    "               inner join cotacao cot on cot.id = sol.cotacao_id  " +
                    "               inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id  " +
                    "               inner join exercicio ex on ex.id = solext.exercicio_id  " +
                    "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id  " +
                    "               left join conlicitacao cl on cl.licitacao_id = lic.id  " +
                    "               left join contrato cont on cont.id = cl.contrato_id  " +
                    "               left join condispensalicitacao cd on cd.contrato_id = cont.id  " +
                    "               left join dispensadelicitacao disp on disp.id = cd.dispensadelicitacao_id " + filtro.getCondicaoSql() +
                    " and solext.tiposolicitacaoregistropreco = 'INTERNA' " +
                    " " +
                    " union all " +
                    " select 'CONTRATO'                     as tipo_processo, " +
                    "       cont.id                         as id_movimento, " +
                    "       ic.id                           as id_item, " +
                    "       to_number(cont.numerocontrato)  as numero, " +
                    "       cont.datalancamento             as data_mov, " +
                    "       ex.ano                          as exercicio, " +
                    "       cont.objeto                     as descricao, " +
                    "       3                               as ordem_movimento " +
                    " from contrato cont " +
                    "       inner join exercicio ex on ex.id = cont.exerciciocontrato_id " +
                    "       left join conlicitacao cl on cl.contrato_id = cont.id " +
                    "       left join licitacao lic on cl.licitacao_id = lic.id " +
                    "       left join condispensalicitacao cd on cd.contrato_id = cont.id " +
                    "       left join dispensadelicitacao disp on cd.dispensadelicitacao_id = disp.id " +
                    "       left join processodecompra pc on pc.id = coalesce(lic.processodecompra_id, disp.processodecompra_id) " +
                    "       left join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                    "       left join cotacao cot on cot.id = sol.cotacao_id " +
                    "       left join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                    "       left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                    "       left join itemcontrato ic on ic.contrato_id = cont.id " +
                    "       left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                    "       left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                    "       left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                    "       left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                    "       left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id" +
                    "       left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
                    "       inner join itemprocessodecompra item on item.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                    "       inner join loteprocessodecompra lote on lote.id = item.loteprocessodecompra_id  " +
                    "       inner join itemsolicitacao itemsol on itemsol.id = item.itemsolicitacaomaterial_id" +
                    "       inner join objetocompra oc on oc.id = itemsol.objetocompra_id " + filtro.getCondicaoSql() +
                    "  union all " +
                    " select 'EXECUCAO_CONTRATO'  as tipo_processo, " +
                    "         ec.id               as id_movimento, " +
                    "         exitem.id           as id_item, " +
                    "         ec.numero           as numero, " +
                    "         ec.datalancamento   as data_mov, " +
                    "         ex.ano              as exercicio, " +
                    "         cont.objeto         as descricao, " +
                    "         4                   as ordem_movimento " +
                    "      from execucaocontrato ec " +
                    "               inner join contrato cont on cont.id = ec.contrato_id " +
                    "               inner join exercicio ex on ex.id = cont.exerciciocontrato_id " +
                    "               inner join execucaocontratoitem exitem on exitem.execucaocontrato_id = ec.id " +
                    "               inner join itemcontrato ic on ic.id = exitem.itemcontrato_id " +
                    "               left join conlicitacao cl on cl.contrato_id = cont.id " +
                    "               left join licitacao lic on cl.licitacao_id = lic.id " +
                    "               left join condispensalicitacao cd on cd.contrato_id = cont.id " +
                    "               left join dispensadelicitacao disp on cd.dispensadelicitacao_id = disp.id " +
                    "               left join processodecompra pc on pc.id = coalesce(lic.processodecompra_id, disp.processodecompra_id) " +
                    "               left join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
                    "               left join cotacao cot on cot.id = sol.cotacao_id " +
                    "               left join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                    "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                    "               left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
                    "               left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
                    "               left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
                    "               left join itemcontratoitempropdisp icipfd on icipfd.itemcontrato_id = ic.id " +
                    "               left join itempropostafornedisp ipfd on icipfd.itempropfornecdispensa_id = ipfd.id" +
                    "               left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id " +
                    "               inner join itemprocessodecompra item on item.id = coalesce(ipf.itemprocessodecompra_id, ipfd.itemprocessodecompra_id) " +
                    "               inner join loteprocessodecompra lote on lote.id = item.loteprocessodecompra_id " +
                    "               inner join itemsolicitacao itemsol on itemsol.id = item.itemsolicitacaomaterial_id " +
                    "               inner join objetocompra oc on oc.id = itemsol.objetocompra_id " + filtro.getCondicaoSql() +
                    " union all " +
                    " select case when lic.id is not null then 'LICITACAO' else 'DISPENSA_LICITACAO_INEXIGIBILIDADE' end as tipo_processo, " +
                    "       coalesce(lic.id, disp.id)                                                   as id_movimento, " +
                    "       ipc.id                                                                      as id_item, " +
                    "       coalesce(lic.numerolicitacao, disp.numerodadispensa)                        as numero, " +
                    "       coalesce(lic.emitidaem, disp.datadadispensa)                                as data_mov, " +
                    "       ex.ano                                                                      as exercicio, " +
                    "       coalesce(lic.resumodoobjeto, disp.resumodoobjeto)                           as descricao, " +
                    "       5                                                                           as ordem_movimento " +
                    " from itemsolicitacao item " +
                    "         inner join lotesolicitacaomaterial lote on lote.id = item.lotesolicitacaomaterial_id " +
                    "         inner join solicitacaomaterial sol on sol.id = lote.solicitacaomaterial_id " +
                    "         inner join itemprocessodecompra ipc on ipc.itemsolicitacaomaterial_id = item.id " +
                    "         inner join objetocompra oc on oc.id = item.objetocompra_id " +
                    "         inner join cotacao cot on cot.id = sol.cotacao_id " +
                    "         inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                    "         left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                    "         left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                    "         left join licitacao lic on lic.processodecompra_id = pc.id " +
                    "         left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                    "         left join conlicitacao cl on cl.licitacao_id = lic.id " +
                    "         left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                    "         left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id)" +
                    "         inner join exercicio ex on ex.id = coalesce(lic.exercicio_id, disp.exerciciodadispensa_id)" + filtro.getCondicaoSql() +
                    "      union all " +
                    "      select 'SOLICITACAO_COMPRA'                 as tipo_processo, " +
                    "             sol.id                               as id_movimento, " +
                    "             item.id                              as id_item, " +
                    "             sol.numero                           as numero, " +
                    "             sol.datasolicitacao                  as data_mov, " +
                    "             ex.ano                               as exercicio, " +
                    "             sol.descricao                        as descricao, " +
                    "             6                                    as ordem_movimento " +
                    "      from itemsolicitacao item " +
                    "               inner join lotesolicitacaomaterial lote on lote.id = item.lotesolicitacaomaterial_id " +
                    "               inner join solicitacaomaterial sol on sol.id = lote.solicitacaomaterial_id " +
                    "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                    "               inner join cotacao cot on cot.id = sol.cotacao_id " +
                    "               inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                    "               inner join exercicio ex on ex.id = sol.exercicio_id " +
                    "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                    "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                    "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                    "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                    "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                    "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                    "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id)" + filtro.getCondicaoSql() +
                    "      union all " +
                    "      select 'COTACAO'                            as tipo_processo, " +
                    "             cot.id                               as id_movimento, " +
                    "             item.id                              as id_item, " +
                    "             cot.numero                           as numero, " +
                    "             cot.datacotacao                      as data_mov, " +
                    "             ex.ano                               as exercicio, " +
                    "             cot.descricao                        as descricao, " +
                    "             7                                    as ordem_movimento " +
                    "      from itemcotacao item " +
                    "               inner join lotecotacao lote on lote.id = item.lotecotacao_id " +
                    "               inner join cotacao cot on cot.id = lote.cotacao_id " +
                    "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                    "               inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
                    "               inner join exercicio ex on ex.id = cot.exercicio_id " +
                    "               left join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                    "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                    "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                    "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                    "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id" +
                    "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                    "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                    "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id)" + filtro.getCondicaoSql() +
                    "  union all " +
                    "      select 'FORMULARIO_COTACAO'                 as tipo_processo, " +
                    "             fc.id                                as id_movimento, " +
                    "             item.id                              as id_item, " +
                    "             fc.numero                            as numero, " +
                    "             fc.dataformulario                    as data_mov, " +
                    "             ex.ano                               as exercicio, " +
                    "             fc.objeto                            as descricao, " +
                    "             8                                    as ordem_movimento " +
                    "      from itemloteformulariocotacao item " +
                    "               inner join loteformulariocotacao lote on lote.id = item.loteformulariocotacao_id " +
                    "               inner join formulariocotacao fc on fc.id = lote.formulariocotacao_id " +
                    "               inner join exercicio ex on ex.id = fc.exercicio_id " +
                    "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                    "               left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
                    "               left join cotacao cot on cot.formulariocotacao_id = fc.id " +
                    "               left join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                    "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                    "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                    "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id" +
                    "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                    "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                    "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id)" + filtro.getCondicaoSql() +
                    " union all " +
                    " select 'IRP'                                                     as tipo_processo, " +
                    "             irp.id                                               as id_movimento, " +
                    "             item.id                                              as id_item, " +
                    "             irp.numero                                           as numero, " +
                    "             irp.datalancamento                                   as data_mov, " +
                    "             cast(to_char(irp.datalancamento, 'yyyy') as integer) as exercicio, " +
                    "             irp.descricao                                        as descricao, " +
                    "             9                                                    as ordem_movimento " +
                    "      from itemintencaoregistropreco item " +
                    "               inner join loteintencaoregistropreco lote on lote.id = item.loteintencaoregistropreco_id " +
                    "               inner join intencaoregistropreco irp on irp.id = lote.intencaoregistropreco_id " +
                    "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                    "               left join formulariocotacao fc on fc.intencaoregistropreco_id = irp.id " +
                    "               left join cotacao cot on cot.formulariocotacao_id = fc.id " +
                    "               left join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
                    "               left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
                    "               left join licitacao lic on lic.processodecompra_id = pc.id " +
                    "               left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
                    "               left join conlicitacao cl on cl.licitacao_id = lic.id " +
                    "               left join condispensalicitacao cd on cd.dispensadelicitacao_id = disp.id " +
                    "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " + filtro.getCondicaoSql() +
                    "     ) " +
                    " order by ordem_movimento desc ";
        }
        Query q = em.createNativeQuery(sql);
        List<SubstituicaoObjetoCompraMovimento> movimentos = Lists.newArrayList();
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            SubstituicaoObjetoCompraMovimento mov = new SubstituicaoObjetoCompraMovimento();
            mov.setTipoMovimento(OrigemSubstituicaoObjetoCompra.valueOf((String) o[0]));
            mov.setId(((BigDecimal) o[1]).longValue());
            mov.setIdItem(((BigDecimal) o[2]).longValue());
            mov.setNumero(o[3] != null ? ((BigDecimal) o[3]).toString() + "/" + ((BigDecimal) o[5]).intValue() : null);
            mov.setData((Date) o[4]);
            mov.setDescricao(o[6] != null ? (String) o[6] : null);
            buscarDesdobramentoExecucaoContrato(filtro, mov);
            movimentos.add(mov);
        }
        return movimentos;
    }

    private void buscarDesdobramentoExecucaoContrato(FiltroSubstituicaoObjetoCompra filtro, SubstituicaoObjetoCompraMovimento mov) {
        if (mov.getTipoMovimento().isExecucaoContrato()) {
            if (filtro.getObjetoCompra().getGrupoContaDespesa() != null) {
                DesdobramentoEmpenho desdobramento = getContaDesdobradaEmpenhoPorExecucaoContrato(mov.getId(), filtro.getObjetoCompra().getGrupoContaDespesa());
                if (desdobramento != null) {
                    mov.setContaDesdobrada(desdobramento.getConta());
                    mov.setEmpenho(desdobramento.getEmpenho());
                }

                SolicitacaoEmpenho solicitacao = getContaSolicitacaoEmpenhoPorExecucaoContrato(mov.getId(), filtro.getObjetoCompra().getGrupoContaDespesa());
                if (solicitacao != null) {
                    mov.setContaDesdobrada(solicitacao.getContaDespesaDesdobrada());
                    mov.setSolicitacaoEmpenho(solicitacao);
                }
            }
        }
    }

    public DesdobramentoEmpenho getContaDesdobradaEmpenhoPorExecucaoContrato(Long idExecucao, GrupoContaDespesa grupoContaDespesa) {
        String sql = " select desd.* " +
            "from  desdobramentoempenho desd  " +
            " inner join conta c on c.id = desd.conta_id " +
            " inner join contadespesa cd on c.id = cd.id  " +
            " inner join empenho emp on emp.id = desd.empenho_id " +
            " inner join execucaocontratoempenho exemp on exemp.empenho_id = emp.id ";
        if (grupoContaDespesa != null && grupoContaDespesa.getIdGrupo() != null) {
            sql += "   left join CONFIGGRUPOMATERIAL config on config.contadespesa_id = c.id" +
                "      AND trunc(emp.dataempenho) between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), trunc(emp.dataempenho)) " +
                "      left join CONFIGDESPESABENS confbem on confbem.contadespesa_id = c.id" +
                "      AND trunc(emp.dataempenho) between trunc(confbem.INICIOVIGENCIA) AND coalesce(trunc(confbem.FIMVIGENCIA), trunc(emp.dataempenho))";
        }
        sql += " where exemp.execucaocontrato_id = :idExecucao ";
        sql += grupoContaDespesa != null && grupoContaDespesa.getIdGrupo() != null ? " and coalesce(CONFIG.GRUPOMATERIAL_ID, confbem.grupobem_id) = :idGrupo" : "";

        Query q = em.createNativeQuery(sql, DesdobramentoEmpenho.class);
        q.setParameter("idExecucao", idExecucao);
        if (grupoContaDespesa != null && grupoContaDespesa.getIdGrupo() != null) {
            q.setParameter("idGrupo", grupoContaDespesa.getIdGrupo());
        }
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (DesdobramentoEmpenho) resultList.get(0);
        }
        return null;
    }

    public SolicitacaoEmpenho getContaSolicitacaoEmpenhoPorExecucaoContrato(Long idExecucao, GrupoContaDespesa grupoContaDespesa) {
        String sql = " select sol.* " +
            " from execucaocontratoempenho exemp " +
            "   inner join solicitacaoempenho sol on sol.id = exemp.solicitacaoempenho_id " +
            "   inner join conta c on c.id = sol.contadespesadesdobrada_id " +
            "   inner join contadespesa cd on c.id = cd.id ";
        if (grupoContaDespesa != null && grupoContaDespesa.getIdGrupo() != null) {
            sql += "   left join CONFIGGRUPOMATERIAL config on config.contadespesa_id = c.id" +
                "      AND trunc(sol.datasolicitacao) between trunc(CONFIG.INICIOVIGENCIA) AND coalesce(trunc(CONFIG.FIMVIGENCIA), trunc(sol.datasolicitacao)) " +
                "      left join CONFIGDESPESABENS confbem on confbem.contadespesa_id = c.id" +
                "      AND trunc(sol.datasolicitacao) between trunc(confbem.INICIOVIGENCIA) AND coalesce(trunc(confbem.FIMVIGENCIA), trunc(sol.datasolicitacao)) ";
        }
        sql += " where exemp.execucaocontrato_id = :idExecucao ";
        sql += grupoContaDespesa != null && grupoContaDespesa.getIdGrupo() != null ? " and coalesce(CONFIG.GRUPOMATERIAL_ID, confbem.grupobem_id) = :idGrupo" : "";

        Query q = em.createNativeQuery(sql, SolicitacaoEmpenho.class);
        q.setParameter("idExecucao", idExecucao);
        if (grupoContaDespesa != null && grupoContaDespesa.getIdGrupo() != null) {
            q.setParameter("idGrupo", grupoContaDespesa.getIdGrupo());
        }
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (SolicitacaoEmpenho) resultList.get(0);
        }
        return null;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public CotacaoFacade getCotacaoFacade() {
        return cotacaoFacade;
    }

    public FormularioCotacaoFacade getFormularioCotacaoFacade() {
        return formularioCotacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public IntencaoRegistroPrecoFacade getIntencaoRegistroPrecoFacade() {
        return intencaoRegistroPrecoFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }

    public RegistroSolicitacaoMaterialExternoFacade getRegistroSolicitacaoMaterialExternoFacade() {
        return registroSolicitacaoMaterialExternoFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }
}
