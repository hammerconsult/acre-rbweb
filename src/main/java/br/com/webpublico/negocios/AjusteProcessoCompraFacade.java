package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AjusteProcessoCompraVO;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.entidadesauxiliares.ItemAjusteProcessoCompraVO;
import br.com.webpublico.entidadesauxiliares.MovimentoAjusteProcessoCompraVO;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoMascaraUnidadeMedida;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.negocios.administrativo.ParticipanteLicitacaoFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class AjusteProcessoCompraFacade extends AbstractFacade<AjusteProcessoCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private DispensaDeLicitacaoFacade dispensaDeLicitacaoFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private CotacaoFacade cotacaoFacade;
    @EJB
    private FormularioCotacaoFacade formularioCotacaoFacade;
    @EJB
    private IntencaoRegistroPrecoFacade intencaoRegistroPrecoFacade;
    @EJB
    private FornecedorDispensaDeLicitacaoFacade fornecedorDispensaDeLicitacaoFacade;
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private ParticipanteLicitacaoFacade participanteLicitacaoFacade;
    @EJB
    private ItemPregaoFacade itemPregaoFacade;
    @EJB
    private RegistroSolicitacaoMaterialExternoFacade registroSolicitacaoMaterialExternoFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;

    public AjusteProcessoCompraFacade() {
        super(AjusteProcessoCompra.class);
    }

    public void recuperarObjetoProcesso(TipoMovimentoProcessoLicitatorio tipoProcesso, Long idObjeto, AjusteProcessoCompraVO ajusteVO) {
        switch (tipoProcesso) {
            case IRP:
                IntencaoRegistroPreco irp = em.find(IntencaoRegistroPreco.class, idObjeto);
                ajusteVO.setIntencaoRegistroPreco(irp);
                ajusteVO.setDescricaoProcesso(irp.toString());
                break;
            case FORMULARIO_COTACAO:
                FormularioCotacao formulario = em.find(FormularioCotacao.class, idObjeto);
                ajusteVO.setFormularioCotacao(formulario);
                ajusteVO.setDescricaoProcesso(formulario.toString());
                break;
            case COTACAO:
                Cotacao cot = em.find(Cotacao.class, idObjeto);
                ajusteVO.setCotacao(cot);
                ajusteVO.setDescricaoProcesso(cot.toString());
                break;
            case SOLICITACAO_COMPRA:
                SolicitacaoMaterial solCompra = em.find(SolicitacaoMaterial.class, idObjeto);
                ajusteVO.setSolicitacaoCompra(solCompra);
                ajusteVO.setDescricaoProcesso(solCompra.toString());
                break;
            case LICITACAO:
            case CREDENCIAMENTO:
                Licitacao licitacao = em.find(Licitacao.class, idObjeto);
                ajusteVO.setLicitacao(licitacao);
                ajusteVO.setDescricaoProcesso(licitacao.toString());
                break;
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                DispensaDeLicitacao dispensa = em.find(DispensaDeLicitacao.class, idObjeto);
                ajusteVO.setDispensaLicitacao(dispensa);
                ajusteVO.setDescricaoProcesso(dispensa.toString());
                break;
            case CONTRATO:
                Contrato contrato = em.find(Contrato.class, idObjeto);
                ajusteVO.setDescricaoProcesso(contrato.toString());
                break;
            case ADESAO_EXTERNA:
                RegistroSolicitacaoMaterialExterno regExt = em.find(RegistroSolicitacaoMaterialExterno.class, idObjeto);
                ajusteVO.setRegistroSolicitacaoMaterialExterno(regExt);
                ajusteVO.setDescricaoProcesso(regExt.toString());
                break;
            case REQUISICAO_COMPRA:
                RequisicaoDeCompra req = em.find(RequisicaoDeCompra.class, idObjeto);
                ajusteVO.setRequisicaoDeCompra(req);
                break;
        }
    }

    public AjusteProcessoCompra salvarRetornando(AjusteProcessoCompraVO ajusteVO) {
        AjusteProcessoCompra entity = AjusteProcessoCompra.toAjusteProcessoCompraVO(ajusteVO);
        entity.setNumero(singletonGeradorCodigo.getProximoCodigo(AjusteProcessoCompra.class, "numero"));
        movimentarProcesso(ajusteVO, entity);
        return em.merge(entity);
    }

    private void movimentarProcesso(AjusteProcessoCompraVO ajusteVO, AjusteProcessoCompra entity) {
        switch (entity.getTipoAjuste()) {
            case SUBSTITUIR_CONTROLE_ITEM:
                salvarItensTipoControle(ajusteVO);
                criarItemAjusteProcessoSubstituicaoControle(ajusteVO, entity);
                entity.setHistorico(gerarHistorico(ajusteVO));
                break;
            case SUBSTITUIR_OBJETO_COMPRA:
                substituirObjetoCompra(ajusteVO.getObjetosCompraSubstituicao());
                criarItemAjusteProcessoSubstituicaoObjetoCompra(entity, ajusteVO.getObjetosCompraSubstituicao());
                entity.setHistorico(gerarHistorico(ajusteVO));
                break;
            case INCLUIR_PROPOSTA_FORNECEDOR:
            case EDITAR_PROPOSTA_FORNECEDOR:
                PropostaFornecedor propostaFornecedor = ajusteVO.getPropostaFornecedor();
                propostaFornecedor.setLotes(ajusteVO.getLotesProposta());

                propostaFornecedor = propostaFornecedorFacade.salvarRetornando(propostaFornecedor);
                entity.setPropostaFornecedor(propostaFornecedor);
                criarItemAjusteProcessoPropostaFornecedor(ajusteVO, entity, propostaFornecedor);
                entity.setHistorico(gerarHistorico(ajusteVO));
                break;
            case INCLUIR_FORNECEDOR:
            case EDITAR_FORNECEDOR:
                entity.setHistorico(gerarHistorico(ajusteVO));
                LicitacaoFornecedor licitacaoFornecedor = ajusteVO.getLicitacaoFornecedor();
                if (licitacaoFornecedor.getRepresentante() != null) {
                    RepresentanteLicitacao representante = licitacaoFornecedor.getRepresentante();
                    representante = em.merge(representante);
                    licitacaoFornecedor.setRepresentante(representante);
                }
                licitacaoFornecedor = em.merge(licitacaoFornecedor);
                entity.setLicitacaoFornecedor(licitacaoFornecedor);
                break;
            case SUBSTITUIR_FORNECEDOR:
                entity.setHistorico(gerarHistorico(ajusteVO));
                if (entity.getTipoProcesso().isLicitacao()) {
                    LicitacaoFornecedor licitacaoForn = ajusteVO.getLicitacaoFornecedor();

                    PropostaFornecedor propostaFornecAtual = propostaFornecedorFacade.recuperarPropostaDoFornecedorPorLicitacao(licitacaoForn.getEmpresa(), licitacaoForn.getLicitacao());
                    if (propostaFornecAtual == null) {
                        throw new ExcecaoNegocioGenerica("Proposta não encontrada para o fornecedor " + licitacaoForn.getEmpresa() + ".");
                    }
                    PropostaFornecedor propostaFornecNovo = propostaFornecedorFacade.recuperarPropostaDoFornecedorPorLicitacao(ajusteVO.getFornecedorSubstituicao(), licitacaoForn.getLicitacao());
                    if (propostaFornecNovo != null) {
                        throw new ExcecaoNegocioGenerica("O novo fornecedor <b>" + ajusteVO.getFornecedorSubstituicao() + "</b> já possui uma proposta. É permitido somente uma proposta por fornecedor no processo licitatório.");
                    }

                    ajusteVO.getContratos().stream()
                        .filter(cont -> cont.getContratado().equals(ajusteVO.getLicitacaoFornecedor().getEmpresa()))
                        .map(cont -> contratoFacade.recuperarContratoComDependenciasFornecedor(cont.getId()))
                        .forEach(cont -> {
                            contratoFacade.transferirFornecedorContrato(cont, ajusteVO.getFornecedorSubstituicao(), cont.getResponsavelEmpresa());
                            em.merge(cont);
                        });

                    propostaFornecAtual.setFornecedor(ajusteVO.getFornecedorSubstituicao());
                    licitacaoForn.setPropostaFornecedor(propostaFornecAtual);
                    licitacaoForn.setEmpresa(ajusteVO.getFornecedorSubstituicao());
                    licitacaoForn = em.merge(licitacaoForn);

                    entity.setLicitacaoFornecedor(licitacaoForn);
                    entity.setPropostaFornecedor(licitacaoForn.getPropostaFornecedor());
                    break;
                }
                ajusteVO.getContratos().stream()
                    .filter(cont -> cont.getContratado().equals(ajusteVO.getFornecedorDispensaLicitacao().getPessoa()))
                    .map(cont -> contratoFacade.recuperarContratoComDependenciasFornecedor(cont.getId()))
                    .forEach(cont -> {
                        contratoFacade.transferirFornecedorContrato(cont, ajusteVO.getFornecedorSubstituicao(), cont.getResponsavelEmpresa());
                        em.merge(cont);
                    });

                FornecedorDispensaDeLicitacao fornecedorDisp = ajusteVO.getFornecedorDispensaLicitacao();
                fornecedorDisp.setPessoa(ajusteVO.getFornecedorSubstituicao());
                fornecedorDisp = em.merge(fornecedorDisp);
                entity.setFornecedorDispensaLicitacao(fornecedorDisp);
                break;
        }
    }

    private void salvarItensTipoControle(AjusteProcessoCompraVO ajusteVO) {
        ajusteVO.getItensSubstituicaoTipoControle().stream().filter(ItemAjusteProcessoCompraVO::getSelecionado).forEach(item -> {
            item.getMovimentos().forEach(rel -> {
                switch (rel.getTipoProcesso()) {
                    case IRP:
                        ItemIntencaoRegistroPreco itemIrp = em.find(ItemIntencaoRegistroPreco.class, rel.getIdItem());
                        itemIrp.setTipoControle(rel.getTipoControle());
                        itemIrp.setQuantidade(rel.getQuantidade());
                        itemIrp.setValor(rel.getValorTotal());
                        em.merge(itemIrp);
                        break;

                    case PARTICIPANTE_IRP:
                        ItemParticipanteIRP itemPartIrp = em.find(ItemParticipanteIRP.class, rel.getIdItem());
                        itemPartIrp.setQuantidade(rel.getQuantidade());
                        itemPartIrp.setValor(rel.getValorTotal());
                        em.merge(itemPartIrp);
                        break;

                    case FORMULARIO_COTACAO:
                        ItemLoteFormularioCotacao itemFc = em.find(ItemLoteFormularioCotacao.class, rel.getIdItem());
                        itemFc.setTipoControle(rel.getTipoControle());
                        itemFc.setQuantidade(rel.getQuantidade());
                        em.merge(itemFc);
                        break;

                    case COTACAO:
                        ItemCotacao itemCot = em.find(ItemCotacao.class, rel.getIdItem());
                        itemCot.setTipoControle(rel.getTipoControle());
                        itemCot.setQuantidade(rel.getQuantidade());
                        em.merge(itemCot);
                        break;

                    case VALOR_COTACAO:
                        ValorCotacao vl = em.find(ValorCotacao.class, rel.getIdItem());
                        vl.setPreco(rel.getValorUnitario());
                        em.merge(vl);
                        break;

                    case SOLICITACAO_COMPRA:
                        ItemSolicitacao itemSol = em.find(ItemSolicitacao.class, rel.getIdItem());
                        itemSol.setQuantidade(rel.getQuantidade());
                        itemSol.setPreco(rel.getValorUnitario());
                        em.merge(itemSol);
                        break;

                    case PROCESSO_COMPRA:
                        ItemProcessoDeCompra itemPc = em.find(ItemProcessoDeCompra.class, rel.getIdItem());
                        itemPc.setQuantidade(rel.getQuantidade());
                        em.merge(itemPc);
                        break;

                    case PROPOSTA_FORNECEDOR:
                    case CREDENCIADO:
                        ItemPropostaFornecedor itemProp = em.find(ItemPropostaFornecedor.class, rel.getIdItem());
                        itemProp.setPreco(rel.getValorUnitario());
                        em.merge(itemProp);
                        break;

                    case PROPOSTA_FORNECEDOR_DISPENSA:
                        ItemPropostaFornecedorDispensa itemPropDisp = em.find(ItemPropostaFornecedorDispensa.class, rel.getIdItem());
                        itemPropDisp.setPreco(rel.getValorUnitario());
                        em.merge(itemPropDisp);
                        break;

                    case PREGAO_POR_ITEM:
                        ItemPregao itemPregao = em.find(ItemPregao.class, rel.getIdItem());
                        itemPregao.getItemPregaoLanceVencedor().setValor(rel.getValorUnitario());
                        em.merge(itemPregao.getItemPregaoLanceVencedor());

                        List<LancePregao> lances = pregaoFacade.buscarLancesPregaoPorItemPregao(itemPregao);
                        for (LancePregao lance : lances) {
                            lance.setValor(rel.getValorTotalCalculado(rel.getQuantidadeOriginal(), lance.getValor()));
                            if (lance.getStatusLancePregao().isVencedor()) {
                                lance.setValorFinal(lance.getValor());
                            }
                            em.merge(lance);
                        }
                        break;

                    case PREGAO_POR_LOTE:
                        ItemPregaoLoteItemProcesso itemPregaoLote = em.find(ItemPregaoLoteItemProcesso.class, rel.getIdItem());
                        itemPregaoLote.setValor(rel.getValorUnitario());
                        em.merge(itemPregaoLote);
                        break;

                    case ADJUDICACAO:
                        ItemStatusFornecedorLicitacao itemAdjudicado = em.find(ItemStatusFornecedorLicitacao.class, rel.getIdItem());
                        itemAdjudicado.setValorUnitario(rel.getValorUnitario());
                        em.merge(itemAdjudicado);
                        break;

                    case HOMOLOGACAO:
                        ItemStatusFornecedorLicitacao itemHomologado = em.find(ItemStatusFornecedorLicitacao.class, rel.getIdItem());
                        itemHomologado.setValorUnitario(rel.getValorUnitario());
                        em.merge(itemHomologado);
                        break;
                }
            });
        });
    }

    public void salvarLance(ItemPregao itemPregao) {
        itemPregao.getListaDeRodadaPregao().stream()
            .flatMap(rod -> rod.getListaDeLancePregao().stream())
            .filter(lance -> lance.getId() == null)
            .forEach(lance -> em.persist(lance));
    }

    public void atribuirGrupoContaDespesaObjetoCompra(ObjetoCompra objetoCompra) {
        objetoCompra.setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra()));
    }

    private void criarItemAjusteProcessoSubstituicaoObjetoCompra(AjusteProcessoCompra entity, List<ItemAjusteProcessoCompraVO> itens) {
        for (ItemAjusteProcessoCompraVO item : itens) {
            AjusteProcessoCompraItem itemSubstituicao = new AjusteProcessoCompraItem();
            itemSubstituicao.setNumeroItem(item.getNumeroItem());
            itemSubstituicao.setAjusteProcessoCompra(entity);
            itemSubstituicao.setObjetoCompra(item.getObjetoCompra());
            itemSubstituicao.setObjetoCompraPara(item.getObjetoCompraPara());
            itemSubstituicao.setEspecificacaoDe(item.getEspecificacao());
            itemSubstituicao.setEspecificacaoPara(item.getEspecificacaoPara());
            itemSubstituicao.setUnidadeMedidaDe(item.getUnidadeMedidaDe());
            itemSubstituicao.setUnidadeMedidaPara(item.getUnidadeMedidaPara());
            itemSubstituicao.setQuantidadeDe(item.getQuantidadeDe());
            itemSubstituicao.setQuantidadePara(item.getQuantidadePara());
            Util.adicionarObjetoEmLista(entity.getItens(), itemSubstituicao);
        }
    }

    private ItemPropostaFornecedor getItemPropostaFornecedorDaPropostaSalva(ItemPropostaFornecedor itemPropAjuste, PropostaFornecedor propostaFornecedor) {
        return propostaFornecedor.getLotes()
            .stream()
            .flatMap(loteProp -> loteProp.getItens().stream())
            .filter(itemProp -> itemProp.getItemProcessoDeCompra().equals(itemPropAjuste.getItemProcessoDeCompra()))
            .findFirst().orElse(itemPropAjuste);
    }

    private void criarItemAjusteProcessoPropostaFornecedor(AjusteProcessoCompraVO ajusteVO, AjusteProcessoCompra entity, PropostaFornecedor propostaFornecedor) {
        ajusteVO.getLotesProposta()
            .stream().flatMap(lote -> lote.getItens().stream())
            .filter(ItemPropostaFornecedor::getSelecionado)
            .forEach(itemPropVo -> {
                ItemPropostaFornecedor itemProp = getItemPropostaFornecedorDaPropostaSalva(itemPropVo, propostaFornecedor);
                AjusteProcessoCompraItem itemAjuste = new AjusteProcessoCompraItem();
                itemAjuste.setAjusteProcessoCompra(entity);
                itemAjuste.setItemPropostaFornecedor(itemProp);
                itemAjuste.setItemProcessoCompra(itemProp.getItemProcessoDeCompra());
                itemAjuste.setObjetoCompra(itemProp.getItemProcessoDeCompra().getObjetoCompra());
                itemAjuste.setNumeroItem(itemAjuste.getItemProcessoCompra().getNumero().longValue());
                entity.getItens().add(itemAjuste);
            });
    }

    private void criarItemAjusteProcessoSubstituicaoControle(AjusteProcessoCompraVO ajusteVO, AjusteProcessoCompra entity) {
        ajusteVO.getItensSubstituicaoTipoControle().forEach(itemVO -> {
            if (itemVO.getSelecionado()) {
                AjusteProcessoCompraItem itemAjuste = new AjusteProcessoCompraItem();
                itemAjuste.setAjusteProcessoCompra(entity);
                itemAjuste.setItemProcessoCompra(itemVO.getItemProcessoCompra());
                itemAjuste.setObjetoCompra(itemVO.getObjetoCompra());
                itemAjuste.setNumeroItem(itemAjuste.getItemProcessoCompra().getNumero().longValue());
                entity.getItens().add(itemAjuste);
            }
        });
    }

    private void substituirObjetoCompra(List<ItemAjusteProcessoCompraVO> itens) {
        MovimentoAjusteProcessoCompraVO movSolicitacao = null;
        for (ItemAjusteProcessoCompraVO itemVo : itens) {
            for (MovimentoAjusteProcessoCompraVO mov : itemVo.getMovimentos()) {
                switch (mov.getTipoProcesso()) {
                    case IRP:
                        ItemIntencaoRegistroPreco itemIrp = em.find(ItemIntencaoRegistroPreco.class, mov.getIdItem());
                        itemIrp.setObjetoCompra(getObjetoCompraSubstituicao(itemVo));
                        itemIrp.setDescricaoComplementar(getDescricaoComplementarSubstituicao(itemVo));
                        itemIrp.setUnidadeMedida(getUnidadeMedidaSubstituicao(itemVo));
                        itemIrp.setQuantidade(getQuantidadeSubstituicao(itemVo));
                        em.merge(itemIrp);
                        break;

                    case FORMULARIO_COTACAO:
                        ItemLoteFormularioCotacao itemFormulario = em.find(ItemLoteFormularioCotacao.class, mov.getIdItem());
                        itemFormulario.setObjetoCompra(getObjetoCompraSubstituicao(itemVo));
                        itemFormulario.setDescricaoComplementar(getDescricaoComplementarSubstituicao(itemVo));
                        itemFormulario.setUnidadeMedida(getUnidadeMedidaSubstituicao(itemVo));
                        itemFormulario.setQuantidade(getQuantidadeSubstituicao(itemVo));
                        em.merge(itemFormulario);
                        break;

                    case COTACAO:
                        ItemCotacao itemCotacao = em.find(ItemCotacao.class, mov.getIdItem());
                        itemCotacao.setObjetoCompra(getObjetoCompraSubstituicao(itemVo));
                        itemCotacao.setDescricaoComplementar(getDescricaoComplementarSubstituicao(itemVo));
                        itemCotacao.setUnidadeMedida(getUnidadeMedidaSubstituicao(itemVo));
                        itemCotacao.setQuantidade(getQuantidadeSubstituicao(itemVo));
                        em.merge(itemCotacao);
                        break;

                    case SOLICITACAO_COMPRA:
                        ItemSolicitacao itemSol = em.find(ItemSolicitacao.class, mov.getIdItem());
                        itemSol.setObjetoCompra(getObjetoCompraSubstituicao(itemVo));
                        itemSol.setDescricaoComplementar(getDescricaoComplementarSubstituicao(itemVo));
                        itemSol.setUnidadeMedida(getUnidadeMedidaSubstituicao(itemVo));
                        itemSol.setQuantidade(getQuantidadeSubstituicao(itemVo));
                        itemSol.setPrecoTotal(itemSol.getQuantidade().multiply(itemSol.getPreco()).setScale(2, RoundingMode.HALF_EVEN));
                        em.merge(itemSol);
                        movSolicitacao = mov;
                        break;

                    case SOLICITACAO_ADESAO_INTERNA:
                        ItemSolicitacaoExterno itemSolExt = em.find(ItemSolicitacaoExterno.class, mov.getIdItem());
                        itemSolExt.setObjetoCompra(getObjetoCompraSubstituicao(itemVo));
                        itemSolExt.setDescricaoComplementar(getDescricaoComplementarSubstituicao(itemVo));
                        itemSolExt.setDescricaoProduto(itemVo.hasObjetoCompraPara() ? itemVo.getObjetoCompraPara().getDescricao() : itemVo.getObjetoCompra().getDescricao());
                        itemSolExt.setUnidadeMedida(getUnidadeMedidaSubstituicao(itemVo));
                        itemSolExt.setQuantidade(getQuantidadeSubstituicao(itemVo));
                        itemSolExt.setValorTotal(itemSolExt.getQuantidade().multiply(itemSolExt.getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN));
                        em.merge(itemSolExt);
                        break;

                    case SOLICITACAO_ADESAO_EXTERNA:
                        ItemSolicitacaoExterno itemAtaInterna = em.find(ItemSolicitacaoExterno.class, mov.getIdItem());
                        itemAtaInterna.setObjetoCompra(getObjetoCompraSubstituicao(itemVo));
                        itemAtaInterna.setDescricaoComplementar(getDescricaoComplementarSubstituicao(itemVo));
                        itemAtaInterna.setDescricaoProduto(itemVo.hasObjetoCompraPara() ? itemVo.getObjetoCompraPara().getDescricao() : itemVo.getObjetoCompra().getDescricao());
                        itemAtaInterna.setUnidadeMedida(getUnidadeMedidaSubstituicao(itemVo));
                        itemAtaInterna.setQuantidade(getQuantidadeSubstituicao(itemVo));
                        itemAtaInterna.setValorTotal(itemAtaInterna.getQuantidade().multiply(itemAtaInterna.getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN));
                        em.merge(itemAtaInterna);
                        break;

                    case LICITACAO:
                    case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                        ItemProcessoDeCompra itemProcesso = em.find(ItemProcessoDeCompra.class, mov.getIdItem());
                        itemProcesso.setQuantidade(getQuantidadeSubstituicao(itemVo));
                        em.merge(itemProcesso);
                        break;

                    case REQUISICAO_COMPRA:
                        ItemRequisicaoDeCompra itemReq = em.find(ItemRequisicaoDeCompra.class, mov.getIdItem());
                        itemReq.setObjetoCompra(getObjetoCompraSubstituicao(itemVo));
                        itemReq.setDescricaoComplementar(getDescricaoComplementarSubstituicao(itemVo));
                        itemReq.setUnidadeMedida(getUnidadeMedidaSubstituicao(itemVo));
                        em.merge(itemReq);
                        break;
                }
            }
        }
        em.flush();
        if (movSolicitacao != null) {
            atualizarValorLote(movSolicitacao);
        }
    }

    private static BigDecimal getQuantidadeSubstituicao(ItemAjusteProcessoCompraVO itemVo) {
        return itemVo.hasQuantidadePara() ? itemVo.getQuantidadePara() : itemVo.getQuantidadeDe();
    }

    private static UnidadeMedida getUnidadeMedidaSubstituicao(ItemAjusteProcessoCompraVO itemVo) {
        return itemVo.hasUnidadeMedidaPara() ? itemVo.getUnidadeMedidaPara() : itemVo.getUnidadeMedidaDe();
    }

    private static String getDescricaoComplementarSubstituicao(ItemAjusteProcessoCompraVO itemVo) {
        return !Strings.isNullOrEmpty(itemVo.getEspecificacaoPara()) ? itemVo.getEspecificacaoPara() : itemVo.getEspecificacao();
    }

    private static ObjetoCompra getObjetoCompraSubstituicao(ItemAjusteProcessoCompraVO itemVo) {
        return itemVo.hasObjetoCompraPara() ? itemVo.getObjetoCompraPara() : itemVo.getObjetoCompra();
    }

    private void atualizarValorLote(MovimentoAjusteProcessoCompraVO movSol) {
        SolicitacaoMaterial solicitacao = atualizarValorLoteSolicitacaoCompra(movSol);

        ProcessoDeCompra processoCompra = processoDeCompraFacade.recuperarProcessoCompraPorSolicitacao(solicitacao.getId());
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

    private SolicitacaoMaterial atualizarValorLoteSolicitacaoCompra(MovimentoAjusteProcessoCompraVO movSol) {
        SolicitacaoMaterial solicitacao = em.find(SolicitacaoMaterial.class, movSol.getIdProcesso());
        List<LoteSolicitacaoMaterial> lotesSolicitacao = solicitacaoMaterialFacade.buscarLotesWithItens(solicitacao);
        for (LoteSolicitacaoMaterial loteSol : lotesSolicitacao) {
            loteSol.setValor(loteSol.getValorTotalItens());
        }
        solicitacao.setValor(solicitacao.getValorTotalLotes());
        em.merge(solicitacao);
        em.flush();
        return solicitacao;
    }

    private String gerarHistorico(AjusteProcessoCompraVO ajusteVO) {
        StringBuilder historico = new StringBuilder();
        switch (ajusteVO.getTipoAjuste()) {
            case SUBSTITUIR_OBJETO_COMPRA:
                ajusteVO.getObjetosCompraSubstituicao()
                    .stream()
                    .filter(ItemAjusteProcessoCompraVO::getSelecionado)
                    .forEach(item -> historico.append("<div>").append("Item nº ").append(item.getNumeroItem()).append(" - ").append(item.getObjetoCompra()).append(", ").append("de <b>para </b>").append(item.getObjetoCompraPara()).append("</div>"));
                break;
            case SUBSTITUIR_CONTROLE_ITEM:
                ajusteVO.getItensSubstituicaoTipoControle()
                    .stream()
                    .filter(ItemAjusteProcessoCompraVO::getSelecionado)
                    .forEach(item -> historico.append("<div>").append("Item nº ").append(item.getNumeroItem()).append(" - ").append(item.getObjetoCompra()).append(", ").append("de <b>Quantidade </b>").append(" para <b>Valor</b></div>"));
                break;
            case INCLUIR_FORNECEDOR:
                historico.append("<div>").append("Fornecedor Incluído: ").append(ajusteVO.getLicitacaoFornecedor().getEmpresa()).append("</div>");
                break;
            case EDITAR_FORNECEDOR:
                historico.append("<div>").append("Fornecedor Editado: ").append(ajusteVO.getLicitacaoFornecedor()).append("</div>");
                break;
            case INCLUIR_PROPOSTA_FORNECEDOR:
                historico.append("<div>").append("Proposta Incluída: ").append(ajusteVO.getPropostaFornecedor()).append("</div>");
                break;
            case EDITAR_PROPOSTA_FORNECEDOR:
                historico.append("<div>").append("Proposta Editada: ").append(ajusteVO.getPropostaFornecedor()).append("</div>");
                break;
            case SUBSTITUIR_FORNECEDOR:
                historico.append("<div>").append("Fornecedor de: <b>").append(ajusteVO.getFornecedorLiciacaoOrDispensa()).append("</b> para <b>").append(ajusteVO.getFornecedorSubstituicao()).append("</b></div>");
                break;
            default:
                historico.append("Ajsute processo de compra");
        }
        return historico.toString();
    }

    public GrupoContaDespesa getGrupoContaDespesa(ObjetoCompra objetoCompra) {
        return objetoCompraFacade.criarGrupoContaDespesa(objetoCompra.getTipoObjetoCompra(), objetoCompra.getGrupoObjetoCompra());
    }

    public List<ItemAjusteProcessoCompraVO> buscarObjetosCompraAndMovimentos(AjusteProcessoCompraVO ajusteVO) {
        List<ItemAjusteProcessoCompraVO> itensVo = Lists.newArrayList();

        for (AjusteProcessoCompraItem itemAjuste : ajusteVO.getItensAjuste()) {
            ItemAjusteProcessoCompraVO novoItemVO = new ItemAjusteProcessoCompraVO();
            novoItemVO.setNumeroItem(itemAjuste.getNumeroItem());
            novoItemVO.setObjetoCompra(itemAjuste.getObjetoCompra());
            novoItemVO.setObjetoCompraPara(itemAjuste.getObjetoCompraPara());
            novoItemVO.setEspecificacao(itemAjuste.getEspecificacaoDe());
            novoItemVO.setEspecificacaoPara(itemAjuste.getEspecificacaoPara());
            novoItemVO.setGrupoContaDespesaDe(getGrupoContaDespesa(itemAjuste.getObjetoCompra()));
            if (itemAjuste.getObjetoCompraPara() != null) {
                novoItemVO.setGrupoContaDespesaPara(getGrupoContaDespesa(itemAjuste.getObjetoCompraPara()));
            }
            novoItemVO.setUnidadeMedidaDe(itemAjuste.getUnidadeMedidaDe());
            novoItemVO.setUnidadeMedidaPara(itemAjuste.getUnidadeMedidaPara());
            novoItemVO.setQuantidadeDe(itemAjuste.getQuantidadeDe());
            novoItemVO.setQuantidadePara(itemAjuste.getQuantidadePara());

            ObjetoCompra objetoCompra = itemAjuste.getObjetoCompraPara() != null ? itemAjuste.getObjetoCompraPara() : itemAjuste.getObjetoCompra();
            novoItemVO.setMovimentos(buscarMovimentosObjetoCompra(ajusteVO, null, novoItemVO.getNumeroItem()));
            itensVo.add(novoItemVO);
        }
        return itensVo;
    }

    public List<ItemAjusteProcessoCompraVO> buscarObjetosCompraPorProcesso(AjusteProcessoCompraVO ajusteVO) {
        String sql;
        if (ajusteVO.getTipoProcesso().isContrato()) {
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
                "       inner join objetocompra oc on oc.id = coalesce(itemsol.objetocompra_id,ise.objetocompra_id) " + getCondicaoSqlObjetoCompra(ajusteVO) +
                " order by coalesce(ipc.numero, ise.numero) ";
        } else if (ajusteVO.getTipoProcesso().isLicitacao() || ajusteVO.getTipoProcesso().isCredenciamento()) {
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
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + getCondicaoSqlObjetoCompra(ajusteVO);
        } else if (ajusteVO.getTipoProcesso().isDispensa()) {
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
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + getCondicaoSqlObjetoCompra(ajusteVO);
        } else if (ajusteVO.getTipoProcesso().isSolicitacaoCompra()) {
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
                "      inner join objetocompra oc on oc.id = item.objetocompra_id " + getCondicaoSqlObjetoCompra(ajusteVO);
        } else if (ajusteVO.getTipoProcesso().isCotacao()) {
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
                "       inner join cotacao cot on cot.id = lote.cotacao_id " + getCondicaoSqlObjetoCompra(ajusteVO);
        } else if (ajusteVO.getTipoProcesso().isFormularioCotacao()) {
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
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + getCondicaoSqlObjetoCompra(ajusteVO);
        } else if (ajusteVO.getTipoProcesso().isIrp()) {
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
                "      inner join objetocompra oc on oc.id = item.objetocompra_id " + getCondicaoSqlObjetoCompra(ajusteVO);
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
                "     inner join objetocompra oc on oc.id = item.objetocompra_id " + getCondicaoSqlObjetoCompra(ajusteVO);
        }
        Query q = em.createNativeQuery(sql);
        List<ItemAjusteProcessoCompraVO> itensVo = Lists.newArrayList();
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] o : (List<Object[]>) resultList) {
                ItemAjusteProcessoCompraVO item = new ItemAjusteProcessoCompraVO();
                item.setIdItem(((BigDecimal) o[0]).longValue());
                item.setNumeroItem(((BigDecimal) o[1]).longValue());
                item.setNumeroLote(((BigDecimal) o[2]).longValue());
                item.setEspecificacao((String) o[3]);
                if (o[4] != null) {
                    UnidadeMedida unidadeMedida = em.find(UnidadeMedida.class, ((BigDecimal) o[4]).longValue());
                    item.setUnidadeMedidaDe(unidadeMedida);
                }
                item.setQuantidadeDe((BigDecimal) o[5]);
                ObjetoCompra objetoCompraDe = em.find(ObjetoCompra.class, ((BigDecimal) o[6]).longValue());
                item.setObjetoCompra(objetoCompraDe);
                item.setGrupoContaDespesaDe(getGrupoContaDespesa(objetoCompraDe));
                item.setIdProcesso(((BigDecimal) o[7]).longValue());
                itensVo.add(item);
            }
        }
        return itensVo;
    }

    public List<MovimentoAjusteProcessoCompraVO> buscarMovimentosObjetoCompra(AjusteProcessoCompraVO ajusteVO, ObjetoCompra objetoCompra, Long numeroItem) {
        String sql;
        String condicao = getCondicaoSqlMovimentosObjetoCompra(ajusteVO, objetoCompra, numeroItem);
        if (ajusteVO.getTipoProcesso().isRegistroPrecoExterno()) {
            sql = "" +
                " select distinct * from ( " +
                "  select 'REQUISICAO_COMPRA' as tipo_processo, " +
                "       req.id              as id_movimento, " +
                "       item.id             as id_item, " +
                "       to_char(req.numero) as numero, " +
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
                "         inner join exercicio ex on ex.id = cont.exerciciocontrato_id " + condicao +
                "               and item.quantidade > 0 " +
                "  union all " +
                " select 'CONTRATO'                     as tipo_processo, " +
                "       cont.id                         as id_movimento, " +
                "       ic.id                           as id_item, " +
                "       cont.numerocontrato || ' - ' || cont.numerotermo as numero, " +
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
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + condicao +
                "  union all " +
                " select 'EXECUCAO_CONTRATO'  as tipo_processo, " +
                "         ec.id               as id_movimento, " +
                "         ic.id               as id_item, " +
                "         to_char(ec.numero)  as numero, " +
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
                "       inner join objetocompra oc on oc.id = item.objetocompra_id " + condicao +
                " union all " +
                " select 'ADESAO_EXTERNA'           as tipo_processo, " +
                "       reg.id                      as id_movimento, " +
                "       item.id                     as id_item, " +
                "       to_char(reg.numeroregistro) as numero, " +
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
                "         inner join exercicio ex on ex.id = reg.exercicioregistro_id " + condicao +
                " union all " +
                " select 'SOLICITACAO_ADESAO_EXTERNA' as tipo_processo, " +
                "       sol.id                                as id_movimento, " +
                "       item.id                               as id_item, " +
                "       to_char(sol.numero)                   as numero, " +
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
                "         inner join exercicio ex on ex.id = sol.exercicio_id " + condicao +
                " ) " +
                " order by ordem_movimento ";
        } else {
            sql = "select distinct * " +
                "   from (" +
                " select 'REQUISICAO_COMPRA' as tipo_processo, " +
                "       req.id              as id_movimento, " +
                "       item.id             as id_item, " +
                "       to_char(req.numero)          as numero, " +
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
                "         inner join exercicio ex on ex.id = coalesce(lic.exercicio_id, disp.exerciciodadispensa_id)" + condicao +
                "               and item.quantidade > 0 " +
                "  union all " +
                "      select 'SOLICITACAO_ADESAO_INTERNA'    as tipo_processo,  " +
                "             solext.id                              as id_movimento,  " +
                "             item.id                                as id_item,  " +
                "             to_char(solext.numero)                 as numero,  " +
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
                "               left join dispensadelicitacao disp on disp.id = cd.dispensadelicitacao_id " + condicao +
                " and solext.tiposolicitacaoregistropreco = 'INTERNA' " +
                " union all " +
                " select 'CONTRATO'                     as tipo_processo, " +
                "       cont.id                         as id_movimento, " +
                "       ic.id                           as id_item, " +
                "       cont.numerocontrato || ' - ' || cont.numerotermo as numero, " +
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
                "       inner join objetocompra oc on oc.id = itemsol.objetocompra_id " + condicao +
                "  union all " +
                " select 'EXECUCAO_CONTRATO'  as tipo_processo, " +
                "         ec.id               as id_movimento, " +
                "         exitem.id           as id_item, " +
                "         to_char(ec.numero)  as numero, " +
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
                "               inner join objetocompra oc on oc.id = itemsol.objetocompra_id " + condicao +
                " union all " +
                " select case when sol.modalidadelicitacao = 'CREDENCIAMENTO' then 'CREDENCIAMENTO'" +
                "             when sol.modalidadelicitacao in ('DISPENSA_LICITACAO', 'INEXIGIBILIDADE') then 'DISPENSA_LICITACAO_INEXIGIBILIDADE' " +
                "             else 'LICITACAO' end as tipo_processo, " +
                "       coalesce(lic.id, disp.id)                                                   as id_movimento, " +
                "       ipc.id                                                                      as id_item, " +
                "       to_char(coalesce(lic.numerolicitacao, disp.numerodadispensa))               as numero, " +
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
                "         inner join exercicio ex on ex.id = coalesce(lic.exercicio_id, disp.exerciciodadispensa_id)" + condicao +
                "      union all " +
                "      select 'SOLICITACAO_COMPRA'                 as tipo_processo, " +
                "             sol.id                               as id_movimento, " +
                "             item.id                              as id_item, " +
                "             to_char(sol.numero)                  as numero, " +
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
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id)" + condicao +
                "      union all " +
                "      select 'COTACAO'                            as tipo_processo, " +
                "             cot.id                               as id_movimento, " +
                "             item.id                              as id_item, " +
                "             to_char(cot.numero)                  as numero, " +
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
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id)" + condicao +
                "  union all " +
                "      select 'FORMULARIO_COTACAO'                 as tipo_processo, " +
                "             fc.id                                as id_movimento, " +
                "             item.id                              as id_item, " +
                "             to_char(fc.numero)                   as numero, " +
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
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id)" + condicao +
                " union all " +
                " select 'IRP'                                                     as tipo_processo, " +
                "             irp.id                                               as id_movimento, " +
                "             item.id                                              as id_item, " +
                "             to_char(irp.numero)                                  as numero, " +
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
                "               left join contrato cont on cont.id = coalesce(cl.contrato_id, cd.contrato_id) " + condicao +
                "     ) " +
                " order by ordem_movimento ";
        }
        Query q = em.createNativeQuery(sql);
        List<MovimentoAjusteProcessoCompraVO> movimentos = Lists.newArrayList();
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            MovimentoAjusteProcessoCompraVO mov = new MovimentoAjusteProcessoCompraVO();
            mov.setTipoProcesso(TipoMovimentoProcessoLicitatorio.valueOf((String) o[0]));
            mov.setIdProcesso(((BigDecimal) o[1]).longValue());
            mov.setIdItem(((BigDecimal) o[2]).longValue());
            mov.setNumero(o[3] != null ? (String) o[3] + "/" + ((BigDecimal) o[5]).intValue() : null);
            mov.setData((Date) o[4]);
            mov.setDescricao(o[6] != null ? (String) o[6] : null);
            if (objetoCompra != null) {
                buscarDesdobramentoExecucaoContrato(objetoCompra, mov);
            }
            movimentos.add(mov);
        }
        return movimentos;
    }

    public String getCondicaoSqlMovimentosObjetoCompra(AjusteProcessoCompraVO ajusteVO, ObjetoCompra objetoCompra, Long numeroItem) {
        String condicao = " where item.numero = " + numeroItem;
        if (objetoCompra != null) {
            condicao += " and oc.id = " + objetoCompra.getId();
        }
        if (ajusteVO.getLicitacao() != null) {
            condicao += " and lic.id = " + ajusteVO.getLicitacao().getId();
        }
        if (ajusteVO.getDispensaLicitacao() != null) {
            condicao += " and disp.id = " + ajusteVO.getDispensaLicitacao().getId();
        }
        if (ajusteVO.getSolicitacaoCompra() != null) {
            condicao += " and sol.id = " + ajusteVO.getSolicitacaoCompra().getId();
        }
        if (ajusteVO.getCotacao() != null) {
            condicao += " and cot.id = " + ajusteVO.getCotacao().getId();
        }
        if (ajusteVO.getFormularioCotacao() != null) {
            condicao += " and fc.id = " + ajusteVO.getFormularioCotacao().getId();
        }
        if (ajusteVO.getIntencaoRegistroPreco() != null) {
            condicao += " and irp.id = " + ajusteVO.getIntencaoRegistroPreco().getId();
        }
        if (ajusteVO.getRegistroSolicitacaoMaterialExterno() != null) {
            condicao += " and reg.id = " + ajusteVO.getRegistroSolicitacaoMaterialExterno().getId();
        }
        return condicao;
    }

    private String getCondicaoSqlObjetoCompra(AjusteProcessoCompraVO ajusteVO) {
        String condicao = "";
        if (ajusteVO.getLicitacao() != null) {
            condicao += " where lic.id = " + ajusteVO.getLicitacao().getId();
        }
        if (ajusteVO.getDispensaLicitacao() != null) {
            condicao += " where disp.id = " + ajusteVO.getDispensaLicitacao().getId();
        }
        if (ajusteVO.getSolicitacaoCompra() != null) {
            condicao += " where sol.id = " + ajusteVO.getSolicitacaoCompra().getId();
        }
        if (ajusteVO.getFormularioCotacao() != null) {
            condicao += " where fc.id = " + ajusteVO.getFormularioCotacao().getId();
        }
        if (ajusteVO.getCotacao() != null) {
            condicao += " where cot.id = " + ajusteVO.getCotacao().getId();
        }
        if (ajusteVO.getIntencaoRegistroPreco() != null) {
            condicao += " where irp.id = " + ajusteVO.getIntencaoRegistroPreco().getId();
        }
        if (ajusteVO.getRegistroSolicitacaoMaterialExterno() != null) {
            condicao += " where reg.id = " + ajusteVO.getRegistroSolicitacaoMaterialExterno().getId();
        }
        return condicao;
    }

    private void buscarDesdobramentoExecucaoContrato(ObjetoCompra objetoCompra, MovimentoAjusteProcessoCompraVO mov) {
        if (mov.getTipoProcesso().isExecucaoContrato() && objetoCompra.getGrupoContaDespesa() != null) {
            DesdobramentoEmpenho desdobramento = getContaDesdobradaEmpenhoPorExecucaoContrato(mov.getIdProcesso(), objetoCompra.getGrupoContaDespesa());
            if (desdobramento != null) {
                mov.setContaDesdobrada(desdobramento.getConta());
                return;
            }
            SolicitacaoEmpenho solicitacao = getContaSolicitacaoEmpenhoPorExecucaoContrato(mov.getIdProcesso(), objetoCompra.getGrupoContaDespesa());
            if (solicitacao != null) {
                mov.setContaDesdobrada(solicitacao.getContaDespesaDesdobrada());
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

    public List<ItemAjusteProcessoCompraVO> buscarItensSubstituicaoTipoControle(AjusteProcessoCompraVO ajusteVO, List<Long> idsItemProcesso) {
        String sql = " select " +
            "  ipc.id                              as id_item, " +
            "  ipc.numero                          as numero_item, " +
            "  lote.numero                         as numero_Lote, " +
            "  ic.tipocontrole                     as tipo_controle, " +
            "  um.mascaraquantidade                as mascara_quantidade, " +
            "  um.mascaravalorunitario             as mascara_valor," +
            "  to_char(item.descricaocomplementar) as desc_complementar " +
            "  from itemprocessodecompra ipc " +
            "    inner join loteprocessodecompra lote on lote.id = ipc.loteprocessodecompra_id " +
            "    inner join processodecompra pc on pc.id = lote.processodecompra_id " +
            "    inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
            "    inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
            "    inner join objetocompra oc on oc.id = item.objetocompra_id " +
            "    left join unidademedida um on um.id = item.unidademedida_id ";
        if (ajusteVO.getTipoProcesso().isLicitacao() || ajusteVO.getTipoProcesso().isCredenciamento()) {
            sql += " inner join licitacao lic on pc.id = lic.processodecompra_id " +
                "    where lic.id = :idProcesso ";
        } else if (ajusteVO.getTipoProcesso().isDispensa()) {
            sql += " inner join dispensadelicitacao disp on pc.id = disp.processodecompra_id " +
                "    where disp.id = :idProcesso ";
        }
        if (!Util.isListNullOrEmpty(idsItemProcesso)) {
            sql += " and ipc.id in (:idsItem) ";
        }
        sql += "    order by ipc.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", ajusteVO.getIdProcesso());
        if (!Util.isListNullOrEmpty(idsItemProcesso)) {
            q.setParameter("idsItem", idsItemProcesso);
        }

        List<ItemAjusteProcessoCompraVO> list = Lists.newArrayList();
        try {
            List<Object[]> resultList = q.getResultList();
            for (Object[] obj : resultList) {
                ItemAjusteProcessoCompraVO item = new ItemAjusteProcessoCompraVO();
                long idItem = ((BigDecimal) obj[0]).longValue();
                item.setItemProcessoCompra(em.find(ItemProcessoDeCompra.class, idItem));
                item.setObjetoCompra(item.getItemProcessoCompra().getObjetoCompra());
                atribuirGrupoContaDespesaObjetoCompra(item.getObjetoCompra());

                item.setNumeroItem(((BigDecimal) obj[1]).longValue());
                item.setNumeroLote(((BigDecimal) obj[2]).longValue());
                item.setTipoControle(TipoControleLicitacao.valueOf((String) obj[3]));
                item.setMascaraQuantidade(obj[4] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[4]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
                item.setMascaraValor(obj[5] != null ? TipoMascaraUnidadeMedida.valueOf((String) obj[5]) : TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL);
                item.setEspecificacao((String) obj[6]);
                item.setMovimentos(buscarMovimentosSubstituicaoTipoControle(ajusteVO.getIdProcesso(), item.getObjetoCompra().getId(), item.getNumeroItem()));
                list.add(item);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public List<MovimentoAjusteProcessoCompraVO> buscarMovimentosSubstituicaoTipoControle(Long idProcesso, Long idObjetoCompra, Long numeroItem) {
        String sql =
            " select * from (" +
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
                "      where lic.id = :idProcesso " +
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
                "      where lic.id = :idProcesso " +
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
                "               left join licitacao lic on pc.id = lic.processodecompra_id " +
                "               left join dispensadelicitacao disp on pc.id = disp.processodecompra_id " +
                "               inner join objetocompra oc on oc.id = ifc.objetocompra_id " +
                "      where coalesce(lic.id, disp.id) = :idProcesso " +
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
                "               left join licitacao lic on pc.id = lic.processodecompra_id " +
                "               left join dispensadelicitacao disp on pc.id = disp.processodecompra_id " +
                "               inner join objetocompra oc on oc.id = ic.objetocompra_id " +
                "      where coalesce(lic.id, disp.id) = :idProcesso " +
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
                "               left join licitacao lic on pc.id = lic.processodecompra_id " +
                "               left join dispensadelicitacao disp on pc.id = disp.processodecompra_id " +
                "               inner join objetocompra oc on oc.id = ic.objetocompra_id " +
                "      where coalesce(lic.id, disp.id) = :idProcesso " +
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
                "               left join licitacao lic on pc.id = lic.processodecompra_id " +
                "               left join dispensadelicitacao disp on pc.id = disp.processodecompra_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where coalesce(lic.id, disp.id) = :idProcesso " +
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
                "               left join licitacao lic on pc.id = lic.processodecompra_id " +
                "               left join dispensadelicitacao disp on pc.id = disp.processodecompra_id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where coalesce(lic.id, disp.id) = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and ipc.numero = :numeroItem" +
                "      union all " +
                "      select ipf.id                              as id_item, " +
                "             pf.id                               as id_mov, " +
                "             case when lic.modalidadelicitacao = 'CREDENCIAMENTO' then 'CREDENCIADO' " +
                "               else 'PROPOSTA_FORNECEDOR' end    as tipo_processo, " +
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
                "      where lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and ipc.numero = :numeroItem " +
                "      union all " +
                "    select   ipf.id                            as id_item, " +
                "             disp.id                           as id_mov, " +
                "             'PROPOSTA_FORNECEDOR_DISPENSA'    as tipo_processo, " +
                "             ipc.quantidade                    as quantidade, " +
                "             ipf.preco                         as valor_unitario, " +
                "             0                                 as valor_total, " +
                "             coalesce(pf.nome, pj.razaosocial) as descricao, " +
                "             ic.tipocontrole                   as tipo_controle, " +
                "             8                                 as movimento " +
                "      from propostafornecedordispensa pfd " +
                "               inner join fornecedordisplic fdl on fdl.propostafornecedordispensa_id = pfd.id " +
                "               inner join lotepropostafornedisp lote on lote.propostafornecedordispensa_id = pfd.id " +
                "               inner join dispensadelicitacao disp on disp.id = fdl.dispensadelicitacao_id " +
                "               inner join pessoa p on p.id = fdl.pessoa_id " +
                "               left join pessoafisica pf on pf.id = p.id " +
                "               left join pessoajuridica pj on pj.id = p.id " +
                "               inner join itempropostafornedisp ipf on ipf.lotepropostafornedisp_id = lote.id " +
                "               inner join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id " +
                "               inner join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id " +
                "               inner join itemcotacao ic on ic.id = item.itemcotacao_id " +
                "               inner join objetocompra oc on oc.id = item.objetocompra_id " +
                "      where disp.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra" +
                "        and ipc.numero = :numeroItem" +
                "      union all  " +
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
                "      where lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and lic.tipoapuracao = :apuracaoItem " +
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
                "      where lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and ipc.numero = :numeroItem " +
                "        and lic.tipoapuracao = :apuracaoLote " +
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
                "      where itemSfl.situacao = 'ADJUDICADO'" +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and ipc.numero = :numeroItem " +
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
                "      where itemSfl.situacao = 'HOMOLOGADO' " +
                "        and lic.id = :idProcesso " +
                "        and oc.id = :idObjetoCompra " +
                "        and ipc.numero = :numeroItem " +
                "     ) order by movimento desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", idProcesso);
        q.setParameter("idObjetoCompra", idObjetoCompra);
        q.setParameter("numeroItem", numeroItem);
        q.setParameter("apuracaoLote", TipoApuracaoLicitacao.LOTE.name());
        q.setParameter("apuracaoItem", TipoApuracaoLicitacao.ITEM.name());
        List<MovimentoAjusteProcessoCompraVO> list = Lists.newArrayList();
        try {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                MovimentoAjusteProcessoCompraVO item = new MovimentoAjusteProcessoCompraVO();
                item.setIdItem(((BigDecimal) obj[0]).longValue());
                item.setIdProcesso(((BigDecimal) obj[1]).longValue());
                item.setTipoProcesso(TipoMovimentoProcessoLicitatorio.valueOf((String) obj[2]));
                item.setQuantidade((BigDecimal) obj[3]);
                item.setValorUnitario((BigDecimal) obj[4]);
                item.setValorTotal((BigDecimal) obj[5]);
                item.setDescricaoProcesso((String) obj[6]);
                item.setTipoControle(TipoControleLicitacao.valueOf((String) obj[7]));
                if (!item.hasValorTotal(item)) {
                    item.setValorTotal(item.getValorTotalCalculado(item.getQuantidade(), item.getValorUnitario()));
                }
                item.setQuantidadeOriginal(item.getQuantidade());
                item.setValorUnitarioOriginal(item.getValorUnitario());
                item.setTipoControleOriginal(item.getTipoControle());
                list.add(item);
            }
            return list;
        } catch (
            NoResultException nre) {
            return list;
        }
    }

    public List<AjusteProcessoCompraVO> buscarAjustesVO(Long idProcesso) {
        String sql = " select * from ajusteprocessocompra " +
            "           where idprocesso = :idProcesso " +
            "          order by numero ";
        Query q = em.createNativeQuery(sql, AjusteProcessoCompra.class);
        q.setParameter("idProcesso", idProcesso);
        List<AjusteProcessoCompraVO> list = Lists.newArrayList();
        try {
            List<AjusteProcessoCompra> resultList = q.getResultList();
            for (AjusteProcessoCompra obj : resultList) {
                AjusteProcessoCompraVO ajusteVo = criarAjusteProcessoCompraVO(obj);
                list.add(ajusteVo);
            }
            return list;
        } catch (NoResultException nre) {
            return list;
        }
    }

    public AjusteProcessoCompraVO buscarAjusteVO(Long idAjuste) {
        String sql = " select * from ajusteprocessocompra " +
            "           where id = :idAjuste " +
            "          order by numero ";
        Query q = em.createNativeQuery(sql, AjusteProcessoCompra.class);
        q.setParameter("idAjuste", idAjuste);
        try {
            AjusteProcessoCompra obj = (AjusteProcessoCompra) q.getSingleResult();
            return criarAjusteProcessoCompraVO(obj);
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<AjusteProcessoCompraItem> buscarItensAjustes(Long idAjuste) {
        String sql = " select * from ajusteprocessocompraitem " +
            "           where ajusteprocessocompra_id = :idAjuste ";
        Query q = em.createNativeQuery(sql, AjusteProcessoCompraItem.class);
        q.setParameter("idAjuste", idAjuste);
        return q.getResultList();
    }

    private AjusteProcessoCompraVO criarAjusteProcessoCompraVO(AjusteProcessoCompra entity) {
        AjusteProcessoCompraVO ajusteVo = AjusteProcessoCompraVO.toAjusteProcessoCompraObjeto(entity);
        ajusteVo.setItensAjuste(buscarItensAjustes(entity.getId()));
        recuperarObjetoProcesso(entity.getTipoProcesso(), entity.getIdProcesso(), ajusteVo);
        buscarItensAjusteVO(ajusteVo);
        buscarItensPregao(ajusteVo);
        return ajusteVo;
    }

    private void buscarItensPregao(AjusteProcessoCompraVO ajusteVo) {
        if (ajusteVo.getTipoAjuste().isIncluirProposta() || ajusteVo.getTipoAjuste().isEditarProposta() && !Util.isListNullOrEmpty(ajusteVo.getItensAjuste())) {
            List<Long> idsIpc = getIdsItemProcessoCompra(ajusteVo);
            if (!Util.isListNullOrEmpty(idsIpc)) {
                ajusteVo.setItensPregao(itemPregaoFacade.buscarItemPregaoPorItemProcessoCompra(ajusteVo.getLicitacao(), idsIpc, null));
            }
        }
    }

    private void buscarItensAjusteVO(AjusteProcessoCompraVO ajusteVo) {
        switch (ajusteVo.getTipoAjuste()) {
            case SUBSTITUIR_CONTROLE_ITEM:
                ajusteVo.setItensSubstituicaoTipoControle(buscarItensSubstituicaoTipoControle(ajusteVo, getIdsItemProcessoCompra(ajusteVo)));
                break;
            case SUBSTITUIR_OBJETO_COMPRA:
                ajusteVo.setObjetosCompraSubstituicao(buscarObjetosCompraAndMovimentos(ajusteVo));
                break;
            case INCLUIR_PROPOSTA_FORNECEDOR:
            case EDITAR_PROPOSTA_FORNECEDOR:
                ajusteVo.setPropostas(propostaFornecedorFacade.buscarPorLicitacao(ajusteVo.getIdProcesso(), ""));
                break;
            case INCLUIR_FORNECEDOR:
            case EDITAR_FORNECEDOR:
                ajusteVo.setFornecedoresLicitacao(participanteLicitacaoFacade.buscarPorLicitacao(ajusteVo.getIdProcesso(), ""));
                break;
            case SUBSTITUIR_FORNECEDOR:
                ajusteVo.setContratos(buscarContratos(ajusteVo));
                break;
        }
    }

    public List<Contrato> buscarContratos(AjusteProcessoCompraVO ajusteVo) {
        Pessoa fornecedor = ajusteVo.getId() == null ? ajusteVo.getFornecedorSubstituicao() : ajusteVo.getFornecedorLiciacaoOrDispensa();
        List<Contrato> contratos = ajusteVo.getTipoProcesso().isLicitacao()
            ? contratoFacade.buscarContratoLicitacao(ajusteVo.getLicitacao())
            : contratoFacade.buscarContratoDispensa(ajusteVo.getDispensaLicitacao());

        if (fornecedor != null) {
            contratos = contratos
                .stream()
                .filter(cont -> cont.getContratado().equals(fornecedor))
                .collect(Collectors.toList());
        }
        return contratos;
    }

    public List<Long> getIdsItemProcessoCompra(AjusteProcessoCompraVO ajusteVO) {
        List<Long> ids = Lists.newArrayList();
        for (AjusteProcessoCompraItem item : ajusteVO.getItensAjuste()) {
            long id = item.getItemPropostaFornecedor() !=null
                ? item.getItemPropostaFornecedor().getItemProcessoDeCompra().getId()
                : item.getItemProcessoCompra().getId();

            ids.add(id);
        }
        return ids;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public DispensaDeLicitacaoFacade getDispensaDeLicitacaoFacade() {
        return dispensaDeLicitacaoFacade;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public FormularioCotacaoFacade getFormularioCotacaoFacade() {
        return formularioCotacaoFacade;
    }

    public IntencaoRegistroPrecoFacade getIntencaoRegistroPrecoFacade() {
        return intencaoRegistroPrecoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public FornecedorDispensaDeLicitacaoFacade getFornecedorDispensaDeLicitacaoFacade() {
        return fornecedorDispensaDeLicitacaoFacade;
    }

    public PropostaFornecedorFacade getPropostaFornecedorFacade() {
        return propostaFornecedorFacade;
    }

    public ParticipanteLicitacaoFacade getParticipanteLicitacaoFacade() {
        return participanteLicitacaoFacade;
    }

    public PregaoFacade getPregaoFacade() {
        return pregaoFacade;
    }

    public ItemPregaoFacade getItemPregaoFacade() {
        return itemPregaoFacade;
    }

    public CotacaoFacade getCotacaoFacade() {
        return cotacaoFacade;
    }

    public RegistroSolicitacaoMaterialExternoFacade getRegistroSolicitacaoMaterialExternoFacade() {
        return registroSolicitacaoMaterialExternoFacade;
    }

    public DotacaoSolicitacaoMaterialFacade getDotacaoSolicitacaoMaterialFacade() {
        return dotacaoSolicitacaoMaterialFacade;
    }
}
