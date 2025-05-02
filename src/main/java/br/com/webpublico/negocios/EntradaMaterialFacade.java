/*
 * Codigo gerado automaticamente em Fri Feb 24 10:25:37 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.GuiaDistribuicaoItemVO;
import br.com.webpublico.entidadesauxiliares.GuiaDistribuicaoVO;
import br.com.webpublico.entidadesauxiliares.ItemEntradaReservaEstoqueVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Stateless
public class EntradaMaterialFacade extends AbstractFacade<EntradaMaterial> {

    @EJB
    protected IntegradorMateriaisContabilFacade integradorMateriaisContabilFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;
    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private MovimentoEstoqueFacade movimentoEstoqueFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private TipoIngressoFacade tipoIngressoFacade;
    @EJB
    private ModeloFacade modeloFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    @EJB
    private ConfigContaDespesaTipoDocumentoFacade configContaDespesaTipoDocumentoFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associacaoGrupoObjetoCompraGrupoMaterialFacade;
    @EJB
    private CardapioFacade cardapioFacade;
    @EJB
    private CardapioRequisicaoCompraFacade cardapioRequisicaoCompraFacade;
    @EJB
    private TipoBaixaBensFacade tipoBaixaBensFacade;
    @EJB
    private DoctoFiscalLiquidacaoFacade doctoFiscalLiquidacaoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;

    public EntradaMaterialFacade() {
        super(EntradaMaterial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void remover(EntradaMaterial entity) {
        movimentarSituacaoRequisicaoCompra(entity, SituacaoRequisicaoCompra.EM_ELABORACAO);
        super.remover(entity);
    }

    @Override
    public EntradaMaterial recuperar(Object id) {
        EntradaMaterial entrada = super.recuperar(id);
        Hibernate.initialize(entrada.getItens());
        Util.ordenarListas(entrada.getItens());

        if (entrada.ehEntradaCompra()) {
            EntradaCompraMaterial entradaCompra = (EntradaCompraMaterial) entrada;
            carregarDocumentosEItens(entradaCompra);
        }
        return entrada;
    }

    public EntradaMaterial recuperarEntradaComDependenciasDocumentos(Object id) {
        EntradaMaterial entrada = super.recuperar(id);
        if (entrada.ehEntradaCompra()) {
            carregarDocumentosEItens((EntradaCompraMaterial) entrada);
        }
        return entrada;
    }

    public void salvarSolicitacaoEstorno(List<SolicitacaoEstornoEntradaMaterial> solicitacoes, List<ItemEntradaReservaEstoqueVO> itensReserva) {
        solicitacoes.forEach(sol -> em.merge(sol));
        itensReserva.forEach(item -> em.merge(item.getReservaEstoque()));
    }

    @Override
    public EntradaMaterial salvarRetornando(EntradaMaterial entity) {
        ordernarItemEntrada(entity);
        atribuirLocalEstoqueOrcamentarioDeCadaItemEntrada(entity);
        associarMaterialPorCompraAoLocalEstoque(entity);
        movimentarSituacaoRequisicaoCompra(entity, SituacaoRequisicaoCompra.AGUARDANDO_EFETIVACAO);
        return super.salvarRetornando(entity);
    }

    public EntradaMaterial concluirEntradaPorCompra(EntradaMaterial entity) throws OperacaoEstoqueException {
        entity.setSituacao(SituacaoEntradaMaterial.ATESTO_DEFINITIVO_LIQUIDADO);
        gerarNumeroEntrada(entity);
        entity = em.merge(entity);
        gerarMovimentoEstoqueDeCadaItemEntrada(entity);
        contabilizar(entity);
        movimentarSituacaoRequisicaoCompra(entity, SituacaoRequisicaoCompra.EFETIVADA);
        return entity;
    }

    public EntradaMaterial salvarGuiaDistribuicaoMaterial(EntradaMaterial entity) {
        try {
            if (entity.ehEntradaCompra()) {
                EntradaCompraMaterial entradaCompra = (EntradaCompraMaterial) entity;
                CardapioRequisicaoCompra cardapioRequisicaoCompra = cardapioRequisicaoCompraFacade.recuperarCardapioRequisicaoCompra(entradaCompra.getRequisicaoDeCompra());
                if (cardapioRequisicaoCompra != null) {
                    List<GuiaDistribuicaoVO> guias = cardapioRequisicaoCompraFacade.buscarGuiaDistribuicaoVO(cardapioRequisicaoCompra, SituacaoRequisicaoMaterial.NAO_AVALIADA);

                    for (GuiaDistribuicaoVO guia : guias) {
                        guia.setRequisicaoMaterial(requisicaoMaterialFacade.recuperar(guia.getRequisicaoMaterial().getId()));
                        criarAprovacaoMaterialTransferencia(guia.getRequisicaoMaterial());
                        SaidaRequisicaoMaterial saidaTrans = criarSaidaMaterialTransferencia(entity, guia);
                        criarEntradaMaterialTransferencia(guia, saidaTrans);
                        finalizarRequisicaoMaterial(guia.getRequisicaoMaterial());
                    }
                }
            }
            return entity;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível transferir as guias de distribuição.");
        }
    }

    public EntradaMaterial salvarEntradaMaterial(EntradaMaterial entrada) throws OperacaoEstoqueException, ExcecaoNegocioGenerica {
        ordernarItemEntrada(entrada);
        atribuirLocalEstoqueOrcamentarioDeCadaItemEntrada(entrada);
        movimentarItensDaRequisicaoPorTransferencia(entrada);
        gerarNumeroEntrada(entrada);
        entrada = em.merge(entrada);
        gerarMovimentoEstoqueDeCadaItemEntrada(entrada);
        contabilizar(entrada);
        return entrada;
    }

    private void movimentarSituacaoRequisicaoCompra(EntradaMaterial entrada, SituacaoRequisicaoCompra situacao) {
        if (entrada.ehEntradaCompra()) {
            EntradaCompraMaterial entradaCompra = (EntradaCompraMaterial) entrada;
            RequisicaoDeCompra requisicao = entradaCompra.getRequisicaoDeCompra();
            requisicaoDeCompraFacade.movimentarSituacaoRequisicaoCompra(requisicao, situacao);
        }
    }

    public EntradaMaterial salvarEntradaTransferencia(EntradaMaterial entrada) throws OperacaoEstoqueException, ExcecaoNegocioGenerica {
        ordernarItemEntrada(entrada);
        gerarNumeroEntrada(entrada);
        entrada = em.merge(entrada);
        for (ItemEntradaMaterial iem : entrada.getItens()) {
            iem.setMovimentoEstoque(movimentoEstoqueFacade.criarMovimentoEstoque(iem, entrada.getDataEntrada()));
        }
        return entrada;
    }

    public void gerarNumeroEntrada(EntradaMaterial entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(EntradaMaterial.class, "numero"));
        }
    }

    public void ordernarItemEntrada(EntradaMaterial selecionado) {
        Iterator<ItemEntradaMaterial> itens = selecionado.getItens().iterator();
        while (itens.hasNext()) {
            ItemEntradaMaterial proximo = itens.next();
            int i = selecionado.getItens().indexOf(proximo);
            proximo.setNumeroItem(i + 1);
            Util.adicionarObjetoEmLista(selecionado.getItens(), proximo);
        }
    }


    private void movimentarItensDaRequisicaoPorTransferencia(EntradaMaterial entrada) {
        if (entrada.ehEntradaTransferencia()) {
            EntradaTransferenciaMaterial entradaTransferencia = (EntradaTransferenciaMaterial) entrada;
            RequisicaoMaterial reqTransf = entradaTransferencia.getRequisicaoMaterial();

            reqTransf.setItensRequisitados(requisicaoMaterialFacade.buscarItensRequisicao(reqTransf));
            entradaTransferencia.getSaidaRequisicaoMaterial().setListaDeItemSaidaMaterial(saidaMaterialFacade.buscarItensSaidaMaterial(entradaTransferencia.getSaidaRequisicaoMaterial()));

            entradaTransferencia.getSaidaRequisicaoMaterial().atribuirQuantidadeAtendida();
            entradaTransferencia.getSaidaRequisicaoMaterial().diminuirQuantidadeEmTransito();
            reqTransf.atualizarSituacaoRequisicaoTransferencia();

            requisicaoMaterialFacade.salvar(reqTransf);
        }
    }

    private void contabilizar(EntradaMaterial entradaMaterial) throws ExcecaoNegocioGenerica {
        if (entradaMaterial.ehEntradaIncorporacao()) {
            EntradaIncorporacaoMaterial entradaIncorporacao = (EntradaIncorporacaoMaterial) entradaMaterial;
            Util.ordenarListas(entradaIncorporacao.getItens());
            integradorMateriaisContabilFacade.contabilizarEntradaPorIncorporacao(entradaIncorporacao);
        }
        if (entradaMaterial.ehEntradaTransferencia()) {
            EntradaTransferenciaMaterial entradaTransferenciaMaterial = (EntradaTransferenciaMaterial) entradaMaterial;
            Util.ordenarListas(entradaTransferenciaMaterial.getItens());
            integradorMateriaisContabilFacade.contabilzarEntradaPorTransferencia(entradaTransferenciaMaterial);
        }
    }

    private void atribuirLocalEstoqueOrcamentarioDeCadaItemEntrada(EntradaMaterial entrada) {
        Map<LocalEstoque, Map<UnidadeOrganizacional, LocalEstoqueOrcamentario>> mapa = new HashMap<>();
        for (ItemEntradaMaterial iem : entrada.getItens()) {
            iem.setLocalEstoqueOrcamentario(localEstoqueFacade.getLocalEstoqueOrcamentario(mapa, iem.getLocalEstoque(), iem.getUnidadeOrcamentaria(), iem.getDataMovimento()));
        }
    }

    public void gerarMovimentoEstoqueDeCadaItemEntrada(EntradaMaterial entrada) throws OperacaoEstoqueException {
        for (ItemEntradaMaterial iem : entrada.getItens()) {
            if (iem.getMovimentoEstoque() == null) {
                iem.setMovimentoEstoque(movimentoEstoqueFacade.criarMovimentoEstoque(iem));
            }
        }
    }

    private void associarMaterialPorCompraAoLocalEstoque(EntradaMaterial entity) {
        if (entity.ehEntradaCompra()) {
            EntradaCompraMaterial entradaCompra = (EntradaCompraMaterial) entity;

            Map<LocalEstoque, Set<Material>> mapLocalEstoque = Maps.newHashMap();
            for (ItemEntradaMaterial item : entradaCompra.getItens()) {
                LocalEstoque localEstoque = item.getLocalEstoque();
                if (!mapLocalEstoque.containsKey(localEstoque)) {
                    mapLocalEstoque.put(localEstoque, new HashSet<>());
                }
                mapLocalEstoque.get(localEstoque).add(item.getMaterial());
            }

            CardapioRequisicaoCompra cardapioRequisicaoCompra = cardapioRequisicaoCompraFacade.recuperarCardapioRequisicaoCompra(entradaCompra.getRequisicaoDeCompra());
            if (cardapioRequisicaoCompra != null) {
                List<GuiaDistribuicaoVO> guias = cardapioRequisicaoCompraFacade.buscarGuiaDistribuicaoVO(cardapioRequisicaoCompra, SituacaoRequisicaoMaterial.NAO_AVALIADA);

                for (GuiaDistribuicaoVO guia : guias) {
                    LocalEstoque localEstoque = guia.getRequisicaoMaterial().getLocalEstoqueDestino();
                    if (!mapLocalEstoque.containsKey(localEstoque)) {
                        mapLocalEstoque.put(localEstoque, new HashSet<>());
                    }
                    for (GuiaDistribuicaoItemVO item : guia.getItens()) {
                        mapLocalEstoque.get(localEstoque).add(item.getItemRequisicaoMaterial().getMaterialRequisitado());
                    }
                }
            }
            localEstoqueFacade.associarMaterialAoLocalEstoque(mapLocalEstoque);
        }
    }

    private EntradaTransferenciaMaterial criarEntradaMaterialTransferencia(GuiaDistribuicaoVO guia, SaidaRequisicaoMaterial saidaTrans) {
        EntradaTransferenciaMaterial entTransf = new EntradaTransferenciaMaterial();
        entTransf.setSaidaRequisicaoMaterial(saidaTrans);
        entTransf.setHistorico("Gerado automaticamente para distribuição das guia " + guia + " referente a integração com o módulo de alimentação escolar");
        entTransf.setDataEntrada(DataUtil.somaPeriodo(saidaTrans.getDataSaida(), 3, TipoPrazo.SEGUNDOS));
        entTransf.setResponsavel(sistemaFacade.getUsuarioCorrente().getPessoaFisica());
        entTransf.setUnidadeOrganizacional(guia.getRequisicaoMaterial().getLocalEstoqueDestino().getUnidadeOrganizacional());
        entTransf.setTipoIngresso(tipoIngressoFacade.recuperarTipoIngresso(TipoBem.ESTOQUE, TipoIngressoBaixa.COMPRA));
        entTransf.setItens(Lists.newArrayList());

        for (ItemSaidaMaterial itemSaida : saidaTrans.getListaDeItemSaidaMaterial()) {
            ItemEntradaMaterial itemEntMat = new ItemEntradaMaterial();
            itemEntMat.setEntradaMaterial(entTransf);
            itemEntMat.setMaterial(itemSaida.getMaterial());
            LocalEstoqueOrcamentario localEstoqueOrc = localEstoqueFacade.recuperarOuCriarNovoLocalEstoqueOrcamentario(guia.getRequisicaoMaterial().getLocalEstoqueDestino(), itemSaida.getLocalEstoqueOrcamentario().getUnidadeOrcamentaria(), sistemaFacade.getDataOperacao());
            itemEntMat.setLocalEstoqueOrcamentario(localEstoqueOrc);

            itemEntMat.setNumeroItem(entTransf.getItens().size() + 1);
            itemEntMat.setQuantidade(itemSaida.getQuantidade());
            itemEntMat.setValorUnitario(itemSaida.getValorUnitario());
            itemEntMat.setValorTotal(itemSaida.getValorTotal());
            itemEntMat.setItemTransferenciaMaterial(new ItemTransferenciaMaterial(itemSaida, itemEntMat));
            Util.adicionarObjetoEmLista(entTransf.getItens(), itemEntMat);
        }
        try {
            return (EntradaTransferenciaMaterial) salvarEntradaTransferencia(entTransf);
        } catch (OperacaoEstoqueException e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private SaidaRequisicaoMaterial criarSaidaMaterialTransferencia(EntradaMaterial entity, GuiaDistribuicaoVO guia) {
        SaidaRequisicaoMaterial saidaTrans = new SaidaRequisicaoMaterial();
        saidaTrans.setRequisicaoMaterial(guia.getRequisicaoMaterial());
        saidaTrans.setDataSaida(DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao()));
        saidaTrans.setDataConclusao(saidaTrans.getDataConclusao());
        saidaTrans.setHistorico("Gerado automaticamente para distribuição das guia " + guia + " referente a integração com o módulo de alimentação escolar");
        saidaTrans.setRetiradoPor(sistemaFacade.getUsuarioCorrente().getNome());
        saidaTrans.setUsuario(sistemaFacade.getUsuarioCorrente());
        saidaTrans.setSituacao(SituacaoMovimentoMaterial.CONCLUIDA);
        saidaTrans.setTipoBaixaBens(tipoBaixaBensFacade.recuperarTipoIngressoBaixa(TipoBem.ESTOQUE, TipoIngressoBaixa.CONSUMO));
        saidaTrans.setListaDeItemSaidaMaterial(Lists.newArrayList());

        for (ItemRequisicaoMaterial itemReq : guia.getRequisicaoMaterial().getItensRequisitados()) {
            ItemSaidaMaterial itemSaidaTransf = new ItemSaidaMaterial();
            itemSaidaTransf.setSaidaMaterial(saidaTrans);
            itemSaidaTransf.setNumeroItem(saidaTrans.getListaDeItemSaidaMaterial().size() + 1);
            ItemEntradaMaterial itemEntrada = recuperarItemEntradaMaterial(entity, itemReq.getMaterialAprovado());
            itemSaidaTransf.setLocalEstoqueOrcamentario(itemEntrada.getLocalEstoqueOrcamentario());
            itemSaidaTransf.setMaterial(itemReq.getMaterialAprovado());
            itemSaidaTransf.setQuantidade(itemReq.getQuantidade());
            itemSaidaTransf.setValorUnitario(itemEntrada.getValorUnitario());
            itemSaidaTransf.calcularValorTotal();
            itemSaidaTransf.setItemRequisicaoSaidaMaterial(new ItemRequisicaoSaidaMaterial(itemReq, itemSaidaTransf));
            Util.adicionarObjetoEmLista(saidaTrans.getListaDeItemSaidaMaterial(), itemSaidaTransf);
        }
        try {
            return (SaidaRequisicaoMaterial) saidaMaterialFacade.salvarSaidaTransferencia(saidaTrans);
        } catch (ParseException | OperacaoEstoqueException e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private AprovacaoMaterial criarAprovacaoMaterialTransferencia(RequisicaoMaterial reqMat) {
        AprovacaoMaterial aprovMat = new AprovacaoMaterial();
        aprovMat.setRequisicaoMaterial(reqMat);
        aprovMat.setDataDaAprovacao(sistemaFacade.getDataOperacao());
        aprovMat.setAprovador(sistemaFacade.getUsuarioCorrente());
        aprovMat.setItensAprovados(Lists.newArrayList());

        for (ItemRequisicaoMaterial itemReq : reqMat.getItensRequisitados()) {
            ItemAprovacaoMaterial itemAprovMat = new ItemAprovacaoMaterial();
            itemAprovMat.setAprovacaoMaterial(aprovMat);
            itemAprovMat.setItemRequisicaoMaterial(itemReq);
            itemAprovMat.setTipoAprovacao(TipoAprovacaoMaterial.APROVADO_TOTAL);
            itemAprovMat.setQuantidadeAprovada(itemReq.getQuantidade());
            Util.adicionarObjetoEmLista(aprovMat.getItensAprovados(), itemAprovMat);
        }
        if (aprovMat.getNumero() == null) {
            aprovMat.setNumero(singletonGeradorCodigo.getProximoCodigo(AprovacaoMaterial.class, "numero"));
        }
        return em.merge(aprovMat);
    }

    public void removerSolicitacaoEstornoEntrada(List<SolicitacaoEstornoEntradaMaterial> solicitacoes, List<ItemEntradaReservaEstoqueVO> itensReserva) {
        solicitacoes.forEach(solEst -> em.remove(em.find(SolicitacaoEstornoEntradaMaterial.class, solEst.getId())));
        itensReserva.forEach(item -> em.remove(em.find(ReservaEstoque.class, item.getReservaEstoque().getId())));
    }

    public void estornarEntradaPorCompra(EntradaCompraMaterial entrada) {
        try {
            entrada.setSituacao(SituacaoEntradaMaterial.ATESTO_DEFINITIVO_ESTORNADO);
            em.merge(entrada);

            RequisicaoDeCompra requisicao = entrada.getRequisicaoDeCompra();
            requisicao.setSituacaoRequisicaoCompra(SituacaoRequisicaoCompra.EM_ELABORACAO);
            em.merge(requisicao);

            List<ItemEntradaMaterial> itensEntrada = recuperarItensEntradaMaterial(entrada);
            for (ItemEntradaMaterial itemEnt : itensEntrada) {
                ReservaEstoque reservaEstoque = buscarReservaEstoqueSolicitacaoEstornoEntrada(itemEnt);
                if (reservaEstoque != null) {
                    reservaEstoque.setStatusReservaEstoque(ReservaEstoque.StatusReservaEstoque.FINALIZADA);
                    em.merge(reservaEstoque);
                }

                ItemEntradaMaterialEstorno itemEst = new ItemEntradaMaterialEstorno();
                itemEst.setItemEntradaMaterial(itemEnt);
                MovimentoEstoque movimentoEstoque = movimentoEstoqueFacade.criarMovimentoEstoque(itemEst, entrada.getDataEstorno());
                itemEst.setMovimentoEstoque(movimentoEstoque);
                em.merge(itemEst);
            }
        } catch (OperacaoEstoqueException e) {
            throw new RuntimeException(e);
        }
    }

    private RequisicaoMaterial finalizarRequisicaoMaterial(RequisicaoMaterial reqMat) {
        reqMat.setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.TRANSFERENCIA_TOTAL_CONCLUIDA);
        return em.merge(reqMat);
    }

    public List<SituacaoDocumentoFiscalEntradaMaterial> buscarSituacoesDocumentoEntrada(Long idEntrada) {
        String sql = " select distinct doc.situacao from doctofiscalentradacompra doc " +
            "          where doc.entradacompramaterial_id = :idEntrada ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEntrada", idEntrada);
        List<String> resultado = q.getResultList();
        if (Util.isListNullOrEmpty(q.getResultList())){
            return Lists.newArrayList();
        }
        Set<SituacaoDocumentoFiscalEntradaMaterial> toReturn = Sets.newHashSet();
        Arrays.stream(SituacaoDocumentoFiscalEntradaMaterial.values()).forEach(sit -> {
            resultado.stream()
                .filter(lista -> sit.name().equals(lista))
                .map(lista -> sit)
                .forEach(toReturn::add);
        });
        return Lists.newArrayList(toReturn);
    }

    public ItemEntradaMaterial recuperarItemEntradaMaterial(EntradaMaterial entrada, Material material) {
        String sql = " select item.* from itementradamaterial item " +
            "         where item.entradamaterial_id = :idEntrada " +
            "           and item.material_id = :idMaterial ";
        Query q = em.createNativeQuery(sql, ItemEntradaMaterial.class);
        q.setParameter("idEntrada", entrada.getId());
        q.setParameter("idMaterial", material.getId());
        try {
            return (ItemEntradaMaterial) q.getSingleResult();
        } catch (NoResultException ex) {
            throw new ExcecaoNegocioGenerica("Local de estoque orçamentário não encontrado para o material " + material.getCodigo() + ".");
        }
    }

    public Long recuperarIdRevisaoAuditoria(Long id) {
        String sql = " select rev.id from entradamaterial_aud aud " +
            " inner join revisaoauditoria rev on rev.id = aud.rev " +
            " where rev.id = (select rev from entradamaterial_aud eaud where id = :id and eaud.revtype = 0) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public EntradaCompraMaterial recuperarEntradaDaRequisicao(RequisicaoDeCompra rdc) {
        String sql = "select * " +
            "       from entradamaterial em " +
            " inner join entradacompramaterial ecm on ecm.id = em.id " +
            "      where ecm.requisicaodecompra_id = :requisicao_id";

        Query q = em.createNativeQuery(sql, EntradaCompraMaterial.class);
        q.setParameter("requisicao_id", rdc.getId());

        try {
            return (EntradaCompraMaterial) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<ItemEntradaMaterial> recuperarItensEntradaMaterial(EntradaMaterial entrada) {
        String hql = "from ItemEntradaMaterial iem"
            + " where iem.entradaMaterial = :entrada";
        Query q = em.createQuery(hql);
        q.setParameter("entrada", entrada);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ItemDoctoItemEntrada> buscarItemDoctoItemEntrada(DoctoFiscalLiquidacao doctoFiscal, SituacaoDocumentoFiscalEntradaMaterial situacao) {
        String sql = " Select associacao.* from itementradamaterial item " +
            "           Inner Join itemdoctoitementrada associacao On associacao.itementradamaterial_id = item.id " +
            "           Inner Join doctofiscalentradacompra itemDocFisc On itemDocFisc.id = associacao.doctofiscalentradacompra_id " +
            "         where itemDocFisc.doctofiscalliquidacao_id = :idDocumento ";
        sql += situacao != null ? " and associacao.situacao = :situacao " : "";
        Query q = em.createNativeQuery(sql, ItemDoctoItemEntrada.class);
        q.setParameter("idDocumento", doctoFiscal.getId());
        if (situacao != null) {
            q.setParameter("situacao", situacao.name());
        }
        return q.getResultList();
    }

    public List<DoctoFiscalEntradaCompra> buscarDoctoFiscalEntradaCompra(EntradaCompraMaterial entradaCompraMaterial) {
        String sql = " select det.* from doctofiscalentradacompra det where det.entradacompramaterial_id = :idEntrada ";
        Query q = em.createNativeQuery(sql, DoctoFiscalEntradaCompra.class);
        q.setParameter("idEntrada", entradaCompraMaterial.getId());
        return q.getResultList();
    }

    public DoctoFiscalEntradaCompra recuperarDoctoFiscalEntradaCompra(DoctoFiscalLiquidacao documentoFiscal) {
        String sql = " select det.* from doctofiscalentradacompra det where det.doctofiscalliquidacao_id = :idDocumento ";
        Query q = em.createNativeQuery(sql, DoctoFiscalEntradaCompra.class);
        q.setParameter("idDocumento", documentoFiscal.getId());
        try {
            return (DoctoFiscalEntradaCompra) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<SolicitacaoEstornoEntradaMaterial> buscarSolicitacaoEstornoEntrada(EntradaCompraMaterial entradaCompraMaterial) {
        String sql = " select sol.* from solicitacaoestornoentcomp   sol where sol.entradacompramaterial_id = :idEntrada ";
        Query q = em.createNativeQuery(sql, SolicitacaoEstornoEntradaMaterial.class);
        q.setParameter("idEntrada", entradaCompraMaterial.getId());
        return q.getResultList();
    }

    public SolicitacaoEstornoEntradaMaterial buscarSolicitacaoEstornoEntradaPorSolicitacaoLiquidacaoEstorno(SolicitacaoLiquidacaoEstorno solLiqEst) {
        String sql = " select sol.* from solicitacaoestornoentcomp sol where sol.solicitacaoliquidacaoest_id = :idSolLiqEst ";
        Query q = em.createNativeQuery(sql, SolicitacaoEstornoEntradaMaterial.class);
        q.setParameter("idSolLiqEst", solLiqEst.getId());
        try {
            return (SolicitacaoEstornoEntradaMaterial) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public ReservaEstoque buscarReservaEstoqueSolicitacaoEstornoEntrada(ItemEntradaMaterial itemEntradaMaterial) {
        String sql = " select res.* from reservaestoque res where res.itementradamaterial_id = :idItemEnt ";
        Query q = em.createNativeQuery(sql, ReservaEstoque.class);
        q.setParameter("idItemEnt", itemEntradaMaterial.getId());
        try {
            return (ReservaEstoque) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public void carregarDocumentosEItens(EntradaCompraMaterial ecm) {
        Hibernate.initialize(ecm.getDocumentosFiscais());
        for (DoctoFiscalEntradaCompra docto : ecm.getDocumentosFiscais()) {
            Hibernate.initialize(docto.getItens());
        }
    }

    public List<EntradaCompraMaterial> recuperarEntradaPorRequisicao(String parte) {
        return em.createQuery("from EntradaCompraMaterial " +
                "   where to_char(numero) like :parte " +
                " or to_char(dataEntrada) like :parte")
            .setParameter("parte", "%" + parte.toLowerCase().trim() + "%")
            .setMaxResults(10)
            .getResultList();
    }

    public List<EntradaTransferenciaMaterial> recuperaEntradasPorSaidas(List<SaidaRequisicaoMaterial> saidas) {
        String hql = " from EntradaTransferenciaMaterial transf where transf.saidaRequisicaoMaterial in :saidas ";
        Query q = em.createQuery(hql);
        q.setParameter("saidas", saidas);
        List<EntradaTransferenciaMaterial> lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<>();
        } else {
            for (EntradaTransferenciaMaterial entrada : lista) {
                entrada.getItens().size();
            }
        }
        return q.getResultList();
    }

    public List<EntradaMaterial> recuperarEntradasDaRequisicao(RequisicaoMaterial rm) {
        String sql = "select em.*, " +
            "            etm.saidarequisicaomaterial_id " +
            "       from entradamaterial em " +
            " inner join entradatransfmaterial etm on etm.id = em.id " +
            " inner join saidarequisicaomaterial srm on srm.id = etm.id " +
            "      where srm.requisicaomaterial_id = :requisicao_id";

        try {
            return em.createNativeQuery(sql, EntradaMaterial.class).setParameter("requisicao_id", rm.getId()).getResultList();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<EntradaCompraMaterial> buscarEntradaMaterialPorCompra(String parte) {
        String sql = "" +
            "    select ecm.requisicaodecompra_id, ecm.detentdoctofiscalliquidacao_id, ent.* " +
            "    from entradamaterial ent " +
            "     inner join entradacompramaterial ecm on ent.id = ecm.id " +
            "     inner join requisicaodecompra req on req.id = ecm.requisicaodecompra_id " +
            "   where (ent.numero like :parte or req.numero like :parte or lower(req.descricao) like :parte) " +
            "    and ent.numero is not null " +
            "  order by ent.numero desc ";
        Query q = em.createNativeQuery(sql, EntradaCompraMaterial.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    public EntradaCompraMaterial recuperarEntradaMaterialPorDocumentoFiscal(DoctoFiscalLiquidacao documento) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select distinct entmat.*, ent.* ")
            .append(" from entradacompramaterial entmat ")
            .append("  inner join entradamaterial ent on ent.id = entmat.id ")
            .append("  inner join doctofiscalentradacompra det on entmat.id = det.entradacompramaterial_id ")
            .append("  inner join doctofiscalliquidacao docto on docto.id = det.doctofiscalliquidacao_id ")
            .append(" where docto.id = :idDocumento ");
        Query q = em.createNativeQuery(sql.toString(), EntradaCompraMaterial.class);
        q.setParameter("idDocumento", documento.getId());
        try {

            return (EntradaCompraMaterial) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<EntradaCompraMaterial> buscarEntradaCompraMaterial(String parte, SituacaoDocumentoFiscalEntradaMaterial... situacoesDocumento) {
        String sql = "  select distinct entmat.requisicaodecompra_id, ent.* from entradacompramaterial entmat " +
            "           inner join entradamaterial ent on ent.id = entmat.id " +
            "           inner join doctofiscalentradacompra det on entmat.id = det.entradacompramaterial_id " +
            "           where ent.numero like :parte " +
            "           and det.situacao in (:situacoesDoc)" +
            "           order by ent.numero ";
        Query q = em.createNativeQuery(sql, EntradaCompraMaterial.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacoesDoc", Util.getListOfEnumName(situacoesDocumento));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<EntradaCompraMaterial> buscarEntradaCompraMaterialPorLocalEstoque(String parte, LocalEstoque localEstoque, boolean isGerarnumeroSituacao, SituacaoDocumentoFiscalEntradaMaterial... situacoesDocumento) {
        String sql = "  select distinct entmat.requisicaodecompra_id, ent.* " +
                " from entradacompramaterial entmat " +
                "          inner join entradamaterial ent on ent.id = entmat.id " +
                "          inner join doctofiscalentradacompra det on entmat.id = det.entradacompramaterial_id " +
                "          inner join itementradamaterial item on ent.ID = item.entradamaterial_id " +
                "          inner join localestoqueorcamentario leo on item.localestoqueorcamentario_id = leo.id " +
                "          inner join localestoque le on leo.localestoque_id = le.id " +
                "          inner join requisicaodecompra req on entmat.requisicaodecompra_id = req.id " +
                " where (ent.numero like :parte " +
                "       or lower(ent.historico) like :parte " +
                "       or to_char(ent.dataentrada, 'dd/MM/yyyy') like :parte" +
                "       or req.numero like :parte) " +
                "  and det.situacao in (:situacoesDoc) " +
                "  and req.tipocontadespesa <> :tipoRequisicaoVPD " ;
                if (localEstoque != null) {
                   sql += "  and le.id = :idLocalEstoque ";
                }
                sql += " order by ent.numero  ";

        Query q = em.createNativeQuery(sql, EntradaCompraMaterial.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("situacoesDoc", Util.getListOfEnumName(situacoesDocumento));
        q.setParameter("tipoRequisicaoVPD", TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA.name());
        if (localEstoque != null) {
            q.setParameter("idLocalEstoque", localEstoque.getId());
        }
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (isGerarnumeroSituacao) {
            List<EntradaCompraMaterial> resultados = q.getResultList();
            List<EntradaCompraMaterial> entradasComDocumentosLiquidados = new ArrayList<>();
            if (!Util.isListNullOrEmpty(resultados)) {
                for (EntradaCompraMaterial entrada : resultados) {
                    if (entrada.getDocumentosFiscais().stream().allMatch(doc -> doc.getSituacao().isLiquidado())) {
                        entradasComDocumentosLiquidados.add(entrada);
                    }
                }
            }
            return entradasComDocumentosLiquidados;
        }
        return q.getResultList();
    }

    public Date buscarUltimaDataLiquidacaoDocto(EntradaCompraMaterial entradaCompraMaterial) {
        String sql = " select max(liq.dataliquidacao) " +
                "from entradacompramaterial entmat " +
                "         inner join entradamaterial ent on ent.id = entmat.id " +
                "         inner join doctofiscalentradacompra det on entmat.id = det.entradacompramaterial_id " +
                "         inner join doctofiscalliquidacao doc on det.doctofiscalliquidacao_id = doc.id " +
                "         inner join liquidacaodoctofiscal ldf on doc.id = ldf.doctofiscalliquidacao_id " +
                "         inner join liquidacao liq on ldf.liquidacao_id = liq.id " +
                "where ent.id = :idEntrada ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEntrada", entradaCompraMaterial.getId());
        if (!Util.isListNullOrEmpty(q.getResultList())) {
            return (Date) q.getResultList().get(0);
        }
        return null;
    }

    public List<EntradaCompraMaterial> buscarEntradasComprasMateriaisPorRequisicao(RequisicaoDeCompra requisicaoDeCompra) {
        if (requisicaoDeCompra.getId() != null) {
            String sql = " select entmat.*, ent.* " +
                " from entradacompramaterial entmat " +
                "    inner join entradamaterial ent on ent.id = entmat.id " +
                " where entmat.requisicaodecompra_id = :idRequisicaoDeCompra " +
                " order by ent.numero ";
            Query q = em.createNativeQuery(sql, EntradaCompraMaterial.class);
            q.setParameter("idRequisicaoDeCompra", requisicaoDeCompra.getId());
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<EntradaCompraMaterial> buscarEntradaCompraPorMaterial(Material material, SituacaoEntradaMaterial situacao) {
        String sql = " select entmat.*, ent.* " +
            " from entradacompramaterial entmat " +
            "    inner join entradamaterial ent on ent.id = entmat.id " +
            "    inner join itementradamaterial item on item.entradamaterial_id = ent.id " +
            " where item.material_id = :idMaterial " +
            "   and ent.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, EntradaCompraMaterial.class);
        q.setParameter("idMaterial", material.getId());
        q.setParameter("situacao", situacao.name());
        return q.getResultList();
    }

    public BigDecimal getValorLiquidadoPorGrupo(Long idGrupoMaterial, DoctoFiscalLiquidacao doctoFiscal) {
        String sql = " " +
            " select " +
            "  coalesce(sum(idie.valorliquidado), 0) as vl_liquidado " +
            "  from itemdoctoitementrada idie " +
            " inner join itementradamaterial iem on iem.id = idie.itementradamaterial_id" +
            " inner join doctofiscalentradacompra docent on docent.id = idie.doctofiscalentradacompra_id " +
            " inner join material mat on mat.id = iem.material_id " +
            "where mat.grupo_id = :idGrupo " +
            "  and docent.doctofiscalliquidacao_id = :idDocumento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idGrupo", idGrupoMaterial);
        q.setParameter("idDocumento", doctoFiscal.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorLiquidadoPorGrupo(GrupoMaterial grupoMaterial, DoctoFiscalLiquidacao doctoFiscal) {
        return getValorLiquidadoPorGrupo(grupoMaterial.getId(), doctoFiscal);
    }


    public BigDecimal getValorTotalLiquidadoEntradaPorCompra(Long idEntrada) {
        String sql = " select coalesce(sum(docto.valor),0) from doctofiscalliquidacao docto " +
            "           inner join doctofiscalentradacompra docent on docent.doctofiscalliquidacao_id = docto.id " +
            "           where docent.entradacompramaterial_id = :idEntrada " +
            "           and docent.situacao = :situacaoLiquidado";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEntrada", idEntrada);
        q.setParameter("situacaoLiquidado", SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO.name());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotalLiquidadoDocumentoFiscal(DoctoFiscalLiquidacao doctoFiscal) {
        String sql = " select coalesce(sum(itemdoc.valorliquidado),0) from itemdoctoitementrada itemdoc " +
            "           inner join doctofiscalentradacompra docent on docent.id = itemdoc.doctofiscalentradacompra_id " +
            "           where docent.doctofiscalliquidacao_id = :idDocto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocto", doctoFiscal.getId());
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException e) {
            return BigDecimal.ZERO;
        }
    }

    public List<DoctoFiscalEntradaCompra> buscarDocumentosFiscaisDuplicados(String numero, String chave, TipoDocumentoFiscal tipoDocumentoFiscal, Pessoa fornecedor) {
        String sql = " select distinct docem.* " +
            " from doctofiscalliquidacao doc " +
            "         inner join doctofiscalentradacompra docem on doc.id = docem.doctofiscalliquidacao_id " +
            "         inner join entradacompramaterial ecm on docem.entradacompramaterial_id = ecm.id " +
            "         inner join tipodocumentofiscal tipo on doc.tipodocumentofiscal_id = tipo.id " +
            "         left join requisicaodecompra req on ecm.requisicaodecompra_id = req.id " +
            "         left join reconhecimentodivida rd on req.reconhecimentodivida_id = rd.id " +
            "         left join requisicaocompraexecucao rce on rce.requisicaocompra_id = req.id " +
            "         left join execucaoprocesso execproc on execproc.id = rce.execucaoprocesso_id " +
            "         left join contrato c on c.id = req.contrato_id " +
            " where coalesce(c.contratado_id, rd.fornecedor_id, execproc.fornecedor_id) = :idFornecedor " +
            "    and tipo.id = :idTipo ";
        if (!Util.isStringNulaOuVazia(chave)) {
            sql += "  and regexp_replace(replace(replace(doc.chavedeacesso, '-', ''), '.', ''), ' ', '') = " +
                "  regexp_replace(replace(replace(:chave, '-', ''), '.', ''), ' ', '') ";
        } else {
            sql += " and regexp_replace(trim(leading '0' from replace(replace(replace(doc.numero, '-', ''), '.', ''), ' ', '')), '^0', '') = " +
                " regexp_replace(trim(leading '0' from replace(replace(replace(:numero, '-', ''), '.', ''), ' ', '')), '^0', '') ";
        }

        Query q = em.createNativeQuery(sql, DoctoFiscalEntradaCompra.class);
        q.setParameter("idFornecedor", fornecedor.getId());
        q.setParameter("idTipo", tipoDocumentoFiscal.getId());

        if (!Util.isStringNulaOuVazia(chave)) {
            q.setParameter("chave", chave);
        }
        if (numero != null && Util.isStringNulaOuVazia(chave)) {
            q.setParameter("numero", numero);
        }

        try {
            return q.getResultList();
        } catch (NoResultException ex) {
            return Lists.newArrayList();
        }
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public LoteMaterialFacade getLoteMaterialFacade() {
        return loteMaterialFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public EstoqueFacade getEstoqueFacade() {
        return estoqueFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public TipoIngressoFacade getTipoIngressoFacade() {
        return tipoIngressoFacade;
    }

    public ModeloFacade getModeloFacade() {
        return modeloFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public TipoDocumentoFiscalFacade getTipoDocumentoFiscalFacade() {
        return tipoDocumentoFiscalFacade;
    }

    public ConfigContaDespesaTipoDocumentoFacade getConfigContaDespesaTipoDocumentoFacade() {
        return configContaDespesaTipoDocumentoFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public AssociacaoGrupoObjetoCompraGrupoMaterialFacade getAssociacaoGrupoObjetoCompraGrupoMaterialFacade() {
        return associacaoGrupoObjetoCompraGrupoMaterialFacade;
    }

    public CardapioFacade getCardapioFacade() {
        return cardapioFacade;
    }

    public CardapioRequisicaoCompraFacade getCardapioRequisicaoCompraFacade() {
        return cardapioRequisicaoCompraFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public SaidaMaterialFacade getSaidaMaterialFacade() {
        return saidaMaterialFacade;
    }

    public RequisicaoMaterialFacade getRequisicaoMaterialFacade() {
        return requisicaoMaterialFacade;
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }

}
